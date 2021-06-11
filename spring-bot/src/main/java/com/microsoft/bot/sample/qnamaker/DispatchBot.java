// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MT License.

package com.microsoft.bot.sample.qnamaker;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.microsoft.bot.builder.*;
import com.microsoft.bot.builder.RecognizerResult.NamedIntentScore;
import com.microsoft.bot.sample.qnamaker.translation.LanguageService;
import com.microsoft.bot.sample.qnamaker.weather.CurrentWeatherResponse;
import com.microsoft.bot.sample.qnamaker.weather.WeatherService;
import com.microsoft.bot.schema.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class DispatchBot extends ActivityHandler {
    private static final Logger log = getLogger(DispatchBot.class);

    private static final String WELCOME_TEXT =
            new StringBuilder("This bot will introduce you to translation middleware. ")
                    .append("Say 'hi' to get started.").toString();

    private final Logger logger;
    private final BotServices botServices;
    private final WeatherService weatherService;
    private final LanguageService languageService;
    private final UserState userState;
    private final StatePropertyAccessor<String> languagePreference;

    public DispatchBot(UserState withUserState, BotServices botServices, WeatherService weatherService, LanguageService languageService) {
        this.weatherService = weatherService;
        this.languageService = languageService;
        logger = getLogger(DispatchBot.class);
        this.botServices = botServices;

        if (withUserState == null) {
            throw new IllegalArgumentException("userState");
        }
        this.userState = withUserState;

        this.languagePreference = userState.createProperty("LanguagePreference");
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded,
                                                     TurnContext turnContext) {
        return sendWelcomeMessage(turnContext);
    }

    private static CompletableFuture<Void> sendWelcomeMessage(TurnContext turnContext) {
        // Greet anyone that was not the target (recipient) of this message.
        // To learn more about Adaptive Cards, see https://aka.ms/msbot-adaptivecards for more details.
        return turnContext.getActivity().getMembersAdded().stream()
                .filter(member -> !StringUtils.equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
                .map(channel -> {
                    Attachment welcomeCard = createAdaptiveCardAttachment();
                    Activity response = MessageFactory.attachment(welcomeCard);
                    return turnContext.sendActivity(response)
                            .thenCompose(task -> turnContext.sendActivity(MessageFactory.text(WELCOME_TEXT)));
                })
                .collect(CompletableFutures.toFutureList())
                .thenApply(resourceResponse -> null);
    }

    /**
     * Load attachment from file.
     *
     * @return the welcome adaptive card
     */
    private static Attachment createAdaptiveCardAttachment() {
        // combine path for cross platform support
        try (
                InputStream input = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("cards/welcomeCard.json")
        ) {
            String adaptiveCardJson = IOUtils.toString(input, StandardCharsets.UTF_8.toString());

            Attachment attachment = new Attachment();
            attachment.setContentType("application/vnd.microsoft.card.adaptive");
            attachment.setContent(Serialization.jsonToTree(adaptiveCardJson));
            return attachment;
        } catch (IOException e) {
            e.printStackTrace();
            return new Attachment();
        }
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {

        if (languageService.isLanguageChangeRequested(turnContext.getActivity().getText())) {
            String currentLang = turnContext.getActivity().getText().toLowerCase();
            String lang = currentLang;

            // If the user requested a language change through the suggested actions with values "es" or "en",
            // simply change the user's language preference in the user state.
            // The translation middleware will catch this setting and translate both ways to the user's
            // selected language.
            // If Spanish was selected by the user, the reply below will actually be shown in spanish to the user.
            return languagePreference.set(turnContext, lang)
                    .thenCompose(task -> {
                        Activity reply = MessageFactory.text(String.format("Your current language code is: %s", lang));
                        return turnContext.sendActivity(reply);
                    })
                    // Save the user profile updates into the user state.
                    .thenCompose(task -> userState.saveChanges(turnContext, false));
        }

        return languagePreference.get(turnContext)
                .thenCompose(lang -> {

                    if (!org.springframework.util.StringUtils.hasLength(lang)) {
                        Activity reply = MessageFactory.text("Choose your language:");

                        List<CardAction> actions =
                                languageService.getAvailableLanguages()
                                        .stream()
                                        .map(l -> {
                                            CardAction action = new CardAction();
                                            action.setTitle(l.getName());
                                            action.setType(ActionTypes.POST_BACK);
                                            action.setValue(l.getCode());
                                            return action;
                                        }).collect(Collectors.toList());

                        SuggestedActions suggestedActions = new SuggestedActions();
                        suggestedActions.setActions(actions);
                        reply.setSuggestedActions(suggestedActions);
                        return turnContext.sendActivity(reply).thenApply(resourceResponse -> null);
                    }
                    // First, we use the dispatch model to determine which cognitive service (LUIS or QnA) to use.
                    return botServices.getDispatch().recognize(turnContext).thenCompose(recognizerResult -> {
                        // Top intent tell us which cognitive service to use.
                        NamedIntentScore topIntent = recognizerResult.getTopScoringIntent();

                        // Next, we call the dispatcher with the top intent.
                        return dispatchToTopIntent(turnContext, topIntent.intent, recognizerResult).thenApply(task -> null);
                    });
                });

    }

    private CompletableFuture<Void> dispatchToTopIntent(
            TurnContext turnContext,
            String intent,
            RecognizerResult recognizerResult
    ) {

        switch (intent) {
            case "q_open-banking-qna":
            case "q_chit-chat": {
                if (recognizerResult.getTopScoringIntent().score < 0.7f) {
                    return processSampleQnA(turnContext);
                }
                return processChitChatQnA(turnContext);
            }

            case "l_weather":
                return processWeather(turnContext, recognizerResult);

            default:
                logger.info(String.format("Dispatch unrecognized intent: %s.", intent));
                return turnContext
                        .sendActivity(MessageFactory.text(String.format("Dispatch unrecognized intent: %s.", intent)))
                        .thenApply(result -> null);
        }
    }

    private BotWeatherInfo mapEntities(JsonNode entityNode) {
        BotWeatherInfo info = new BotWeatherInfo();
        List<String> entities = new ArrayList<String>();
        for (Iterator<Map.Entry<String, JsonNode>> child = entityNode.fields(); child.hasNext(); ) {
            Map.Entry<String, JsonNode> childIntent = child.next();
            String childName = childIntent.getKey();
            log.debug("mapEntities: {}", childName, childIntent.getValue());
            if ("datetime".equals(childName)) {
                info.setTime(childIntent.getValue().get(0).get("timex").get(0).asText());
            }
            if ("geographyV2".equals(childName)) {
                info.setLocation(childIntent.getValue().get(0).get("location").asText());
            }
            if (!childName.startsWith("$")) {
                entities.add(childIntent.getValue().get(0).toPrettyString());
            }
        }
        info.setEntities(entities);
        return info;
    }

    private PredictionResult mapPredictionResult(JsonNode luisResult) {
        JsonNode prediction = luisResult.get("prediction");
        JsonNode intentsObject = prediction.get("intents");
        if (intentsObject == null) {
            return null;
        }
        PredictionResult result = new PredictionResult();
        result.setTopIntent(prediction.get("topIntent").asText());
        List<Intent> intents = new ArrayList<Intent>();
        for (Iterator<Map.Entry<String, JsonNode>> it = intentsObject.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> intent = it.next();
            double score = intent.getValue().get("score").asDouble();
            String intentName = intent.getKey().replace(".", "_").replace(" ", "_");
            Intent newIntent = new Intent();
            newIntent.setName(intentName);
            newIntent.setScore(score);
            JsonNode childNode = intent.getValue().get("childApp");
            if (childNode != null) {
                newIntent.setTopIntent(childNode.get("topIntent").asText());
                List<Intent> childIntents = new ArrayList<Intent>();
                JsonNode childIntentNodes = childNode.get("intents");
                for (Iterator<Map.Entry<String, JsonNode>> child = childIntentNodes.fields(); child.hasNext(); ) {
                    Map.Entry<String, JsonNode> childIntent = child.next();
                    double childScore = childIntent.getValue().get("score").asDouble();
                    String childIntentName = childIntent.getKey();
                    Intent newChildIntent = new Intent();
                    newChildIntent.setName(childIntentName);
                    newChildIntent.setScore(childScore);
                    childIntents.add(newChildIntent);
                }
                newIntent.setChildIntents(childIntents);
            }

            intents.add(newIntent);
        }
        result.setIntents(intents);
        return result;
    }

    private CompletableFuture<Void> processWeather(TurnContext turnContext, RecognizerResult luisResult) {
        logger.info("ProcessWeather");

        // Retrieve LUIS result for Weather.
        PredictionResult predictionResult = mapPredictionResult(luisResult.getProperties().get("luisResult"));

        Intent topIntent = predictionResult.getIntents().get(0);
        log.info("weather: " + String.format("ProcessWeather top intent %s.", topIntent.getTopIntent()));
        List<String> intents = Arrays.asList(topIntent.getName());
        log.info("weather: " + String.format("ProcessWeather Intents detected:%s", String.join("\n\n", intents)));

        if (luisResult.getEntities() != null) {
            BotWeatherInfo info = mapEntities(luisResult.getEntities());
            if (isNotBlank(info.getLocation()) && isNotBlank(info.getTime())) {
                CurrentWeatherResponse weather = weatherService.getWeather(info.getLocation(), info.getTime());
                return turnContext
                        .sendActivity(MessageFactory.text(
                                String.format(
                                        "The temperature in %s at %s is: %s",
                                        info.getLocation(), info.getTime(),
                                        weather.getCurrent().getTemperature()
                                )
                        ))
                        .thenApply(finalResult -> null);
            }
        }
        return turnContext
                .sendActivity(
                        MessageFactory
                                .text(String.format("Maybe you forgot to mention both location and time?:\n\n%s", String.join("\n\n", intents)))
                ).thenApply(finalResult -> null);
    }

    private CompletableFuture<Void> processSampleQnA(TurnContext turnContext) {
        logger.info("ProcessSampleQnA");

        return botServices.getSampleQnA().getAnswers(turnContext, null).thenCompose(results -> {
            if (results.length > 0) {
                return turnContext.sendActivity(MessageFactory.text(results[0].getAnswer())).thenApply(result -> null);
            } else {
                return turnContext
                        .sendActivity(MessageFactory.text("Sorry, could not find an answer in the Q and A system."))
                        .thenApply(result -> null);
            }
        });
    }

    private CompletableFuture<Void> processChitChatQnA(TurnContext turnContext) {
        logger.info("processChitChatQnA");

        return botServices.getChitChatQnA().getAnswers(turnContext, null).thenCompose(results -> {
            if (results.length > 0) {
                return turnContext.sendActivity(MessageFactory.text(results[0].getAnswer())).thenApply(result -> null);
            } else {
                return turnContext
                        .sendActivity(MessageFactory.text("Sorry, could not find an answer in the Q and A system."))
                        .thenApply(result -> null);
            }
        });
    }
}

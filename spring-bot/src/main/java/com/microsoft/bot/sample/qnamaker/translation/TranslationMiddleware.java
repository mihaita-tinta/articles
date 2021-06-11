// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.sample.qnamaker.translation;

import com.google.common.base.Strings;
import com.microsoft.bot.builder.*;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ActivityTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Middleware for translating text between the user and bot.
 * Uses the Microsoft Translator Text API.
 */
public class TranslationMiddleware implements Middleware  {
    private final LanguageService languageService;
    private final MicrosoftTranslator translator;
    private final StatePropertyAccessor<String> languageStateProperty;

    /**
     * Initializes a new instance of the {@link TranslationMiddleware} class.
     * @param languageService
     * @param withTranslator Translator implementation to be used for text translation.
     * @param userState State property for current language.
     */
    public TranslationMiddleware(LanguageService languageService, MicrosoftTranslator withTranslator, UserState userState) {
        this.languageService = languageService;
        if (withTranslator == null) {
            throw new IllegalArgumentException("withTranslator");
        }
        this.translator = withTranslator;
        if (userState == null) {
            throw new IllegalArgumentException("userState");
        }

        this.languageStateProperty = userState.createProperty("LanguagePreference");
    }

    /**
     * Processes an incoming activity.
     * @param turnContext Context object containing information for a single turn of conversation with a user.
     * @param next The delegate to call to continue the bot middleware pipeline.
     * @return A Task representing the asynchronous operation.
     */
    public CompletableFuture<Void> onTurn(TurnContext turnContext, NextDelegate next) {
        if (turnContext == null) {
            throw new IllegalArgumentException("turnContext");
        }

        return this.shouldTranslate(turnContext).thenCompose(translate -> {
            if (translate) {
                if (turnContext.getActivity().isType(ActivityTypes.MESSAGE)) {

                    if (languageService.isLanguageChangeRequested(turnContext.getActivity().getText())) {
                        // skip translation for
                        return CompletableFuture.completedFuture(null);
                    }
                    return this.translator.translate(
                        turnContext.getActivity().getText(),
                        TranslationSettings.DEFAULT_LANGUAGE)
                    .thenApply(text -> {
                        turnContext.getActivity().setText(text);
                        return CompletableFuture.completedFuture(null);
                    });
                }
            }
            return CompletableFuture.completedFuture(null);
        }).thenCompose(task -> {
            turnContext.onSendActivities((newContext, activities, nextSend) -> {
                return this.languageStateProperty.get(turnContext, () -> TranslationSettings.DEFAULT_LANGUAGE).thenCompose(userLanguage -> {
                    Boolean shouldTranslate = !userLanguage.equals(TranslationSettings.DEFAULT_LANGUAGE);

                    // Translate messages sent to the user to user language
                    if (shouldTranslate) {
                        ArrayList<CompletableFuture<Void>> tasks = new ArrayList<CompletableFuture<Void>>();
                        for (Activity activity : activities.stream().filter(a -> a.getType().equals(ActivityTypes.MESSAGE)).collect(Collectors.toList())) {
                            tasks.add(this.translateMessageActivity(activity, userLanguage));
                        }

                        if (!Arrays.asList(tasks).isEmpty()) {
                            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                        }
                    }

                    return nextSend.get();
                });
            });

            turnContext.onUpdateActivity((newContext, activity, nextUpdate) -> {
                return this.languageStateProperty.get(turnContext, () -> TranslationSettings.DEFAULT_LANGUAGE).thenCompose(userLanguage -> {
                    Boolean shouldTranslate = !userLanguage.equals(TranslationSettings.DEFAULT_LANGUAGE);

                    // Translate messages sent to the user to user language
                    if (activity.getType().equals(ActivityTypes.MESSAGE)) {
                        if (shouldTranslate) {
                            this.translateMessageActivity(activity, userLanguage);
                        }
                    }

                    return nextUpdate.get();
                });
            });

            return next.next();
        });
    }

    private CompletableFuture<Void> translateMessageActivity(Activity activity, String targetLocale) {
        if (activity.getType().equals(ActivityTypes.MESSAGE)) {
            return this.translator.translate(activity.getText(), targetLocale).thenAccept(text -> {
                activity.setText(text);
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Boolean> shouldTranslate(TurnContext turnContext) {
        return this.languageStateProperty.get(turnContext, () -> TranslationSettings.DEFAULT_LANGUAGE).thenApply(userLanguage -> {
            if (Strings.isNullOrEmpty(userLanguage)) {
                userLanguage = TranslationSettings.DEFAULT_LANGUAGE;
            }
            return !userLanguage.equals(TranslationSettings.DEFAULT_LANGUAGE);
        });
    }
}

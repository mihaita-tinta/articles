package com.microsoft.bot.sample.qnamaker.translation;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    private final List<Lang> languages;

    public LanguageService() {
        languages = new RestTemplate()
                .getForEntity("https://api.cognitive.microsofttranslator.com/languages?api-version=3.0&scope=translation", LanguageResponse.class)
                .getBody()
                .translation
                .entrySet()
                .stream()
                .map(e -> new Lang(e.getKey(), e.getValue().get("name")))
                .collect(Collectors.toList());

    }


    public List<Lang> getAvailableLanguages() {
        return Collections.unmodifiableList(languages);
    }
    /**
     * Checks whether the utterance from the user is requesting a language change.
     * In a production bot, we would use the Microsoft Text Translation API language
     * detection feature, along with detecting language names.
     * For the purpose of the sample, we just assume that the user requests language
     * changes by responding with the language code through the suggested action presented
     * above or by typing it.
     *
     * @param utterance utterance the current turn utterance.
     * @return the utterance.
     */
    public Boolean isLanguageChangeRequested(String utterance) {
        if (Strings.isNullOrEmpty(utterance)) {
            return false;
        }

        return languages
                .stream()
                .anyMatch(l -> l.getCode().equals(utterance.toLowerCase().trim()));
    }
    public static class LanguageResponse {
        Map<String, Map<String, String>> translation;

        public Map<String, Map<String, String>> getTranslation() {
            return translation;
        }

        public void setTranslation(Map<String, Map<String, String>> translation) {
            this.translation = translation;
        }
    }
    public static class Lang {
        private final String code;
        private final String name;

        public Lang(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}

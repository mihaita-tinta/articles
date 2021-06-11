// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.sample.qnamaker.translation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Array of translated results from Translator API v3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatorResponse {
    @JsonProperty("translations")
    private List<TranslatorResult> translations;

    /**
     * Gets the translation results.
     * @return A list of {@link TranslatorResult}
     */
    public List<TranslatorResult> getTranslations() {
        return this.translations;
    }

    /**
     * Sets the translation results.
     * @param withTranslations A list of {@link TranslatorResult}
     */
    public void setTranslations(List<TranslatorResult> withTranslations) {
        this.translations = withTranslations;
    }
}

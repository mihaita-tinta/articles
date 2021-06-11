package com.microsoft.bot.sample.qnamaker.translation;

import org.junit.Assert;
import org.junit.Test;

public class LanguageServiceTest {

    @Test
    public void test() {

        Assert.assertNotNull(new LanguageService().getAvailableLanguages());
    }
}

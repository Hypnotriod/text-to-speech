package com.hypnotriod.texttospeech.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LoggerService {

    public void logGenerationStarted() {
        System.out.println("Generation started...");
    }

    public void logGenerationPhrase(String phrase, String group, String languageCode, String gender) {
        System.out.println(
                "Phrase: " + phrase
                + " | Group: " + group
                + " | LanguageCode: " + languageCode
                + " | Gender: " + gender);
    }

    public void logGenerationFinished() {
        System.out.println("Generation finished...");
    }

    public void logPlying(String fileName) {
        System.out.println("Playing: " + fileName);
    }
    
    public void logSettingsFileCorrupted() {
        System.out.println("Settings file is corrupted!");
    }
}

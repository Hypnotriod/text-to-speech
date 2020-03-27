package com.hypnotriod.texttospeech.service;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.entity.Settings;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
public class SettingsService {

    @Autowired
    private FilesManagementService filesManagementService;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private Settings settings;

    @Autowired
    private Gson gson;

    @PostConstruct
    public void init() {
        String settingsJSON = filesManagementService.readFileToString(Configurations.PATH_SETTINGS);
        if (settingsJSON != null) {
            try {
                settings = gson.fromJson(settingsJSON, Settings.class);
            } catch (JsonParseException ex) {
                loggerService.logSettingsFileCorrupted();
            }
        }
    }

    public void saveSettings() {
        String settingsJSON = gson.toJson(settings);
        filesManagementService.writeStringToFile(Configurations.PATH_SETTINGS, settingsJSON);
    }

    public ArrayList<String> getFilterPatterns() {
        return settings.filterPatterns;
    }

    public double getAppWidth() {
        return settings.appWith;
    }

    public void setAppWidth(double value) {
        settings.appWith = value;
    }

    public double getAppHeight() {
        return settings.appHeight;
    }

    public void setAppHeight(double value) {
        settings.appHeight = value;
    }

    public String getLanguageCode() {
        return settings.languageCode;
    }

    public void setLanguageCode(String value) {
        settings.languageCode = value;
    }

    public SsmlVoiceGender getGender() {
        return settings.gender;
    }

    public void setGender(SsmlVoiceGender value) {
        settings.gender = value;
    }

    public String getGroup() {
        return settings.group;
    }

    public void setGroup(String value) {
        settings.group = value;
    }

    public String getFilter() {
        return settings.filter;
    }

    public void setFilter(String value) {
        settings.filter = value;
    }
}

package com.hypnotriod.texttospeech.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Services;
import com.hypnotriod.texttospeech.entity.Settings;
import java.util.ArrayList;

/**
 *
 * @author Ilya Pikin
 */
public class SettingsService {

    private final FilesManagementService filesManagementService = Services.FILES_MANAGEMENT_SERVICE;
    private final LoggerService loggerService = Services.LOGGER_SERVICE;
    private final Gson gson = new Gson();
    private Settings settings = new Settings();

    public SettingsService() {
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
}

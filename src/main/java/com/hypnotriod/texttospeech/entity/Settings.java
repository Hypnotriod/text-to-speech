package com.hypnotriod.texttospeech.entity;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Languages;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
public class Settings {

    public ArrayList<String> filterPatterns = new ArrayList<>();
    public double appHeight = Configurations.APP_HEIGHT_MIN;
    public double appWith = Configurations.APP_WITH_MIN;
    public String languageCode = Languages.CODES[0];
    public SsmlVoiceGender gender = Configurations.VOICE_GENDERS[0];
    public String group = "";
    public String filter = "";
}

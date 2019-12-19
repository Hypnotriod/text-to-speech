package com.hypnotriod.texttospeech.entity;

import com.hypnotriod.texttospeech.constants.Configurations;
import java.util.ArrayList;

/**
 *
 * @author Ilya Pikin
 */
public class Settings {

    public ArrayList<String> filterPatterns = new ArrayList<>();
    public double appHeight = Configurations.APP_HEIGHT_MIN;
    public double appWith = Configurations.APP_WITH_MIN;
}

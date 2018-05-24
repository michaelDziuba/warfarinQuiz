package com.michael.warfarin;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by cdu on 2016-05-18.
 */
public class AppFunctions {


    public static void speakSpeech(TextToSpeech t1, String speech) throws InterruptedException {

        HashMap<String, String> myHash = new HashMap<String, String>();

        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "done");

        String[] splitspeech = speech.split("\\:");

        for (int i = 0; i < splitspeech.length; i++) {
            //textOutput.append("\n" + splitspeech[i].toString().trim());

            t1.speak(splitspeech[i].trim(), TextToSpeech.QUEUE_ADD, myHash);
            if (i == 0) {
                t1.playSilence(600, TextToSpeech.QUEUE_ADD, null);
            } else {
                t1.playSilence(600, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }









}

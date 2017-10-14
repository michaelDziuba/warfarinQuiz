package com.michael.warfarin;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    Button button1;
    ImageButton exitImageButton;
    ImageButton speechButton1;
    ScrollView scrollView;
    boolean isSpeak;
    TextToSpeech t1;
    String speakString;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(Button1Handler);


        exitImageButton = (ImageButton) findViewById(R.id.exitImageButton);
        exitImageButton.setOnClickListener(ExitImageButtonHandler);

        speechButton1 = (ImageButton) findViewById(R.id.speechButton1);
        speechButton1.setBackgroundResource(R.drawable.no_talk);
        speechButton1.setOnClickListener(SpeechButtonHandler);
        isSpeak = false;
        speakString = ((TextView) findViewById(R.id.disclaimerText)).getText().toString();

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                    t1.setSpeechRate((float) 0.75);
                    t1.setPitch((float) 1.2);

                }
            }
        });



        scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }





    View.OnClickListener Button1Handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            t1.stop();
            startActivity(new Intent(MainActivity.this, GeneralQuizActivity.class));
        }
    };

    View.OnClickListener ExitImageButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishAffinity();
        }
    };

    View.OnClickListener SpeechButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(isSpeak) {
                speechButton1.setBackgroundResource(R.drawable.no_talk);
                isSpeak = false;
                t1.stop();
            }else{
                speechButton1.setBackgroundResource(R.drawable.talk);
                isSpeak = true;
                try {
                    AppFunctions.speakSpeech(t1, speakString);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onPause(){
        super.onPause();
        if(t1 != null){
            t1.stop();
            t1.shutdown();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



}



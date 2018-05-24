package com.michael.warfarin;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;




public class GeneralQuizActivity extends AppCompatActivity {
    ArrayList<String> questions1;
    ArrayList<String> questions2;
    ArrayList<String> answers;
    ArrayList<String> answers_speech;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    String speakString = "";
    int countQuestion1;
    int countQuestion2;
    int countAnswer;
    int countSpeechAnswer;

    TextView textOutput;
    TextView answerText;
    TextToSpeech t1;
    Button answerButton;
    Button nextQuestionButton;
    ImageButton homeImageButton;
    ImageButton speechButton;

    ScrollView scrollView;

    ImageView imageView;
    AssetManager assets;
    Button showButton;

    int[] correctAnswer;
    boolean answerCheck;
    boolean answerButtonClicked;
    boolean isSpeak;

    private PopupWindow pw;
    boolean popupShowing;
    GIFView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_quiz);

        correctAnswer = new int[] {1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1};

        questions1 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.questions1)));
        questions2 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.questions2)));
        answers = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.answers)));
        answers_speech = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.answers_speech)));

        radioGroup = (RadioGroup) findViewById(R.id.answer_group);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);

        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);

        textOutput = (TextView) findViewById(R.id.textOutput);
        answerText = (TextView) findViewById(R.id.answerText);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        showButton = (Button) findViewById(R.id.showButton);
        showButton.setOnClickListener(ShowButtonHandler);

        assets = this.getAssets();

        answerButton=(Button)findViewById(R.id.answerButton);
        nextQuestionButton = (Button) findViewById(R.id.nextQuestionButton);

        homeImageButton = (ImageButton) findViewById(R.id.homeImageButton);
        homeImageButton.setOnClickListener(HomeImageButtonHandler);

        speechButton = (ImageButton) findViewById(R.id.speechButton);
        speechButton.setOnClickListener(SpeechButtonHandler);
        speechButton.setBackgroundResource(R.drawable.talk);

        imageView = (ImageView) findViewById(R.id.imageView);

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

        answerButton.setOnClickListener(AnswerButtonHandler);
        nextQuestionButton.setOnClickListener(NextQuestionButtonHandler);

        if(savedInstanceState == null) {
            textOutput.setText(questions1.get(0));
            radioButton1.setText(questions2.get(0));
            radioButton2.setText(questions2.get(1));
            countQuestion1 = 1;
            countQuestion2 = 2;
            countAnswer = 0;
            countSpeechAnswer = 0;
            answerButtonClicked = false;
            isSpeak = true;
            popupShowing = false;
        }else{
            countQuestion1 = savedInstanceState.getInt("countQuestion1");
            countQuestion2 = savedInstanceState.getInt("countQuestion2");
            countAnswer = savedInstanceState.getInt("countAnswer");
            countSpeechAnswer = savedInstanceState.getInt("countSpeechAnswer");
            answerButtonClicked = savedInstanceState.getBoolean("answerButtonClicked");
            isSpeak = savedInstanceState.getBoolean("isSpeak");
            speakString = savedInstanceState.getString("speakString");
            popupShowing = savedInstanceState.getBoolean("popupShowing");
            textOutput.setText(savedInstanceState.getString("textOutput"));
            radioButton1.setText(savedInstanceState.getString("radioButton1"));
            radioButton2.setText(savedInstanceState.getString("radioButton2"));
            answerText.setText(savedInstanceState.getString("answerText"));

            if(countQuestion1 - 1 == 1){
                showButton.setVisibility(View.VISIBLE);
            }else{
                showButton.setVisibility(View.GONE);
            }

            if(answerButtonClicked){
                showCorrectAnswer(countAnswer - 1);
            }

            if(countQuestion1 - 1 > 0){
                try {
                    Drawable image = Drawable.createFromStream(assets.open((countQuestion1 -1) + ".png"), "" + (countQuestion1 - 1));
                    imageView.setImageDrawable(image);

                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isSpeak) {
                        isSpeak = false;
                        speechButton.performClick();
                    }else{
                        isSpeak = true;
                        speechButton.performClick();
                    }

                    if(popupShowing){
                        showButton.performClick();
                    }
                }
            }, 500);
        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("countQuestion1", countQuestion1);
        savedInstanceState.putInt("countQuestion2", countQuestion2);
        savedInstanceState.putInt("countAnswer", countAnswer);
        savedInstanceState.putInt("countSpeechAnswer", countSpeechAnswer);
        savedInstanceState.putBoolean("answerButtonClicked", answerButtonClicked);
        savedInstanceState.putBoolean("isSpeak", isSpeak);
        savedInstanceState.putBoolean("popupShowing", popupShowing);
        savedInstanceState.putString("speakString", speakString);
        savedInstanceState.putString("textOutput", textOutput.getText().toString());
        savedInstanceState.putString("radioButton1", radioButton1.getText().toString());
        savedInstanceState.putString("radioButton2", radioButton2.getText().toString());
        savedInstanceState.putString("answerText", answerText.getText().toString());

    }


    View.OnClickListener SpeechButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(isSpeak) {
                speechButton.setBackgroundResource(R.drawable.no_talk);
                isSpeak = false;
                t1.stop();
            }else{
                speechButton.setBackgroundResource(R.drawable.talk);
                isSpeak = true;
                try {
                    AppFunctions.speakSpeech(t1, speakString);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }
    };




    View.OnClickListener AnswerButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        if(!answerButtonClicked) {
            if (!radioGroup.isActivated()) {
               showCorrectAnswer(countAnswer);
            } else {
                radioButton1.setSelected(true);
            }

            answerText.setText(answers.get(countAnswer));

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

            speakString = answers_speech.get(countSpeechAnswer);

            countAnswer++;
            countSpeechAnswer++;

            if(isSpeak) {
                try {
                    AppFunctions.speakSpeech(t1, speakString);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            answerButtonClicked = true;
        }
        }
    };


    View.OnClickListener NextQuestionButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            t1.stop();
            speakString = "";

            if (countAnswer < answers.size()) {
                answerButtonClicked = false;

                if (answerText.getText().equals("")) {
                    answerButton.performClick();
                    return;
                }

                try {
                    Drawable image = Drawable.createFromStream(assets.open(countQuestion1 + ".png"), "" + countQuestion1);
                    imageView.setImageDrawable(image);

                }catch(IOException e){
                    e.printStackTrace();
                }

                radioGroup.clearCheck();
                answerText.setText("");
                radioButton1.setBackgroundColor(Color.TRANSPARENT);
                radioButton2.setBackgroundColor(Color.TRANSPARENT);
                radioButton1.setPaintFlags(0);
                radioButton2.setPaintFlags(0);

                textOutput.setText(questions1.get(countQuestion1));
                radioButton1.setText(questions2.get(countQuestion2));
                countQuestion2++;
                radioButton2.setText(questions2.get(countQuestion2));

                if(countQuestion1 == 1){
                    showButton.setVisibility(View.VISIBLE);
                }else{
                    showButton.setVisibility(View.GONE);
                }

                countQuestion1++;
                countQuestion2++;

            }else{
                textOutput.setText(questions1.get(0));
                radioButton1.setText(questions2.get(0));
                radioButton2.setText(questions2.get(1));
                radioGroup.clearCheck();
                answerText.setText("");
                radioButton1.setBackgroundColor(Color.TRANSPARENT);
                radioButton2.setBackgroundColor(Color.TRANSPARENT);
                radioButton1.setPaintFlags(0);
                radioButton2.setPaintFlags(0);
                countQuestion1 = 1;
                countQuestion2 = 2;
                countAnswer = 0;
                countSpeechAnswer = 0;
                answerButtonClicked = false;
                try {
                    Drawable image = Drawable.createFromStream(assets.open("8.png"), "" + countQuestion1);
                    imageView.setImageDrawable(image);

                }catch(IOException e){
                    e.printStackTrace();
                }
                //Log.i("Swithchover", "done");
            }
        }
    };


    View.OnClickListener HomeImageButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            t1.stop();
            startActivity(new Intent(GeneralQuizActivity.this, MainActivity.class));
        }
    };

    View.OnClickListener ShowButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initiatePopupWindow();
            popupShowing = true;
            scrollView.setAlpha((float)0.2);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pw.dismiss();
                    popupShowing = false;
                    scrollView.setAlpha((float)1.0);
                }
            }, GIFView.movieDuration);
        }
    };


    public void onPause(){
        super.onPause();

        if(pw != null){
            pw.dismiss();
        }

        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    private void initiatePopupWindow() {
        try {
           // LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater inflater = LayoutInflater.from( this );
            View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popupElement));

            pw = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);

            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            pw.getContentView().setOnClickListener(CancelPopupElementHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener CancelPopupElementHandler = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            scrollView.setAlpha((float)1.0);
        }
    };


    private void showCorrectAnswer(int countAnswer){
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
            if (correctAnswer[countAnswer] == 1) {
                answerCheck = true;
                radioButton1.setBackgroundColor(Color.GREEN);
                radioButton2.setBackgroundColor(Color.RED);
                radioButton2.setPaintFlags(radioButton2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                answerCheck = false;
                radioButton1.setBackgroundColor(Color.RED);
                radioButton1.setPaintFlags(radioButton1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                radioButton2.setBackgroundColor(Color.GREEN);
            }
        } else {
            if (correctAnswer[countAnswer] == 2) {
                radioButton1.setBackgroundColor(Color.RED);
                radioButton1.setPaintFlags(radioButton1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                radioButton2.setBackgroundColor(Color.GREEN);
                answerCheck = true;
            } else {
                answerCheck = false;
                radioButton1.setBackgroundColor(Color.GREEN);
                radioButton2.setBackgroundColor(Color.RED);
                radioButton2.setPaintFlags(radioButton2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    
}
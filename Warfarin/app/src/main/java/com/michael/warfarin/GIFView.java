package com.michael.warfarin;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


public class GIFView extends View{
    int screenWidth;
    int screenHeight;
    float scaleFactor;

    public Movie mMovie;
    public static int movieDuration;
    public long movieStart;

    public GIFView(Context context) {
        super(context);
        initializeView();
    }

    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public GIFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView();
    }

    private void initializeView() {
        InputStream inputStream;
        try {
            inputStream = getContext().getResources().getAssets().open("clot.gif");
            mMovie = Movie.decodeStream(inputStream);
            movieDuration = mMovie.duration();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenHeight = displayMetrics.heightPixels > 2560 ? 2560 : displayMetrics.heightPixels;
            screenWidth = displayMetrics.widthPixels > 1440 ? 1440 : displayMetrics.widthPixels;
            scaleFactor = 0.95f * (mMovie.height() < mMovie.width() ? screenWidth / mMovie.width() : screenHeight / mMovie.height());

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(scaleFactor,scaleFactor);
        canvas.drawColor(Color.TRANSPARENT);

        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % movieDuration);
            mMovie.setTime(relTime);
           mMovie.draw(canvas, 0, 0);

            this.invalidate();
        }

    }



    @Override
    protected void onMeasure(int width, int height){
        super.onMeasure(width, height);
        setMeasuredDimension((int)(mMovie.width() * scaleFactor), (int)(mMovie.height() * scaleFactor));
    }




    private int gifId;

    public void setGIFResource(int resId) {
        this.gifId = resId;
        initializeView();
    }

    public int getGIFResource() {
        return this.gifId;
    }
}

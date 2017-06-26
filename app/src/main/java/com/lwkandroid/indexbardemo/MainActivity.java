package com.lwkandroid.indexbardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lwkandroid.indexbar.IndexBar;

public class MainActivity extends AppCompatActivity
{
    IndexBar indexBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        indexBar = (IndexBar) findViewById(R.id.indexBar);
        indexBar.setOnIndexLetterChangedListener(new IndexBar.OnIndexLetterChangedListener()
        {
            @Override
            public void onTouched(boolean touched)
            {

            }

            @Override
            public void onLetterChanged(CharSequence indexChar, int index, float y)
            {
                Log.e("s", "当前索引:" + indexChar + " " + index + " " + y);
            }
        });
    }
}

package com.example.carhub;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {

                    sleep(1000);
                    Intent intent = new  Intent(MainActivity.this ,Login.class);
                    startActivity(intent);
                    finish();

                }
                catch (InterruptedException e )
                {
                    e.printStackTrace();

                }

            }

        };
        myThread.start();

    }
}

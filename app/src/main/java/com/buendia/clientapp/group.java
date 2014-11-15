package com.buendia.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Group extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void refresh(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
}

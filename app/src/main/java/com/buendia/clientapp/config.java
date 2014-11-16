package com.buendia.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class Config extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
    }

    public void save(View view) {
        EditText ip = (EditText) findViewById(R.id.configIP);
        Intent intent = new Intent(this, Main.class);
        intent.putExtra("IP",ip.getText().toString());
        System.out.println(ip.getText().toString());
        startActivity(intent);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
}

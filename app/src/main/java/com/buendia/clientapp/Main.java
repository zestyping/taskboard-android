package com.buendia.clientapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public void refresh(View view) {
        try {
            new MainUpdateAsyncTask().execute(new URL("http://index.hu"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void config(View view) {
        Intent intent = new Intent(this, Config.class);
        startActivity(intent);
    }

    public class MainUpdateAsyncTask extends AsyncTask<URL,Void,Long> {

        @Override
        protected Long doInBackground(URL... params) {

            return null;
        }

        protected void onPostExecute(Long result) {

            Context context = getApplicationContext();
            CharSequence text = "Downloading finished!";
            int duration = Toast.LENGTH_SHORT;

            Toast refreshingToast = Toast.makeText(context,text,duration);
            refreshingToast.show();

            String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                    "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                    "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                    "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                    "Android", "iPhone", "WindowsMobile" };

            final ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < values.length; ++i) {
                list.add(values[i]);
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(getBaseContext(),
                    android.R.layout.simple_list_item_1, list);
            final ListView listview = (ListView) findViewById(R.id.listView);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    final String item = (String) parent.getItemAtPosition(position);

                }

            });
        }

        protected void onPreExecute() {

            Context context = getApplicationContext();
            CharSequence text = "Started downloading the new content!";
            int duration = Toast.LENGTH_SHORT;

            Toast refreshingToast = Toast.makeText(context,text,duration);
            refreshingToast.show();

        }
    }

    }

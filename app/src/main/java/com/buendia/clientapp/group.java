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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Group extends Activity {

    private String groupName;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        this.groupName = intent.getStringExtra("GROUP_NAME");
        this.ip = intent.getStringExtra("IP");
        final TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(groupName);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void refresh(View view) {
        new GroupUpdateAsyncTask().execute(ip);
    }

    public class GroupUpdateAsyncTask extends AsyncTask<String, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(String... params) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://");
            stringBuilder.append(params[0]);
            stringBuilder.append("/board");
            String url = stringBuilder.toString();
            System.out.println(url);

            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));
                response = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }


        protected void onPostExecute(HttpResponse result) {

            if (result == null) {

                Context context = getApplicationContext();
                CharSequence text = "Downloading failed!";
                int duration = Toast.LENGTH_SHORT;

                Toast refreshingToast = Toast.makeText(context, text, duration);
                refreshingToast.show();

            } else {

                Context context = getApplicationContext();
                CharSequence text = "Downloading finished!";
                int duration = Toast.LENGTH_SHORT;

                Toast refreshingToast = Toast.makeText(context, text, duration);
                refreshingToast.show();

                InputStream inputStream = null;
                try {
                    inputStream = result.getEntity().getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String content = inputStreamToString(inputStream);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject data = null;
                try {
                    data = new JSONObject(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject cells = null;
                if (data != null) {
                    try {
                        cells = data.getJSONObject("cells");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject labels = null;
                if (data != null) {
                    try {
                        labels = cells.getJSONObject("labels");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONArray layout = null;
                if (data != null) {
                    try {
                        layout = cells.getJSONArray("layout");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private String inputStreamToString(InputStream inputStream) {
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}

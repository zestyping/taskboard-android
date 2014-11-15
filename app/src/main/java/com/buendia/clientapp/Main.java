package com.buendia.clientapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;

public class Main extends Activity {

    String ip = "54.69.215.38:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MainUpdateAsyncTask().execute(ip);
        final TextView titleTextView = (TextView) findViewById(R.id.selectedIp);
        titleTextView.setText("The selected server address is: " + ip);
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
        new MainUpdateAsyncTask().execute(ip);
    }

    public void config(View view) {
        Intent intent = new Intent(this, Config.class);
        startActivity(intent);
    }

    public class MainUpdateAsyncTask extends AsyncTask<String, Void, HttpResponse> {

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

                JSONObject tabs = null;
                if (data != null) {
                    try {
                        tabs = data.getJSONObject("tabs");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject labels = null;
                if (data != null) {
                    try {
                        labels = tabs.getJSONObject("labels");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONArray order = null;
                if (data != null) {
                    try {
                        order = tabs.getJSONArray("order");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                int length = order.length();
                String[] labelsInCorrectOrder = new String[length];
                for (int i = 0; i < length; i++) {
                    try {
                        labelsInCorrectOrder[i] = labels.getString(Integer.toString(order.getInt(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < labelsInCorrectOrder.length; ++i) {
                    list.add(labelsInCorrectOrder[i]);
                }
                list.add("All");

                final StableArrayAdapter adapter = new StableArrayAdapter(getBaseContext(),
                        android.R.layout.simple_list_item_1, list);
                final ListView listview = (ListView) findViewById(R.id.listView);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final String groupName = (String) parent.getItemAtPosition(position);
                        Intent intent = new Intent(getBaseContext(), Group.class);
                        intent.putExtra("GROUP_NAME", groupName);
                        intent.putExtra("IP", ip);
                        startActivity(intent);
                    }

                });
            }
        }

        protected void onPreExecute() {

            Context context = getApplicationContext();
            CharSequence text = "Started downloading the new content!";
            int duration = Toast.LENGTH_SHORT;

            Toast refreshingToast = Toast.makeText(context, text, duration);
            refreshingToast.show();

        }

        private String inputStreamToString(InputStream inputStream) {
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

}

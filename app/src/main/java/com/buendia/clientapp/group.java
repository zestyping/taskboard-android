package com.buendia.clientapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

    public class GroupUpdateAsyncTask extends AsyncTask<String, Void, HttpResponse[]> {

        @Override
        protected HttpResponse[] doInBackground(String... params) {

            StringBuilder stringBuilderBoard = new StringBuilder();
            stringBuilderBoard.append("http://");
            stringBuilderBoard.append(params[0]);
            stringBuilderBoard.append("/board");
            String urlBoard = stringBuilderBoard.toString();

            StringBuilder stringBuilderState = new StringBuilder();
            stringBuilderState.append("http://");
            stringBuilderState.append(params[0]);
            stringBuilderState.append("/state");
            String urlState = stringBuilderState.toString();
            
            HttpResponse[] response = {null, null}; //0 board, 1 state
            
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(urlBoard));
                response[0] = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(urlState));
                response[1] = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }


        protected void onPostExecute(HttpResponse[] result) {

            if (result[0] == null || result[1] ==null) {

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
                    inputStream = result[0].getEntity().getContent();
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

                int cellsPerRow = 0;
                if (layout.length() == 0) {
                    CharSequence error = "Downloading completed but there is nothing to show!";
                    Toast errorToast = Toast.makeText(context, error, duration);
                    errorToast.show();
                } else {
                    //check the how many cells/row should it use
                    try {
                        cellsPerRow = layout.getJSONArray(0).length()+1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final GridView gridView = (GridView) findViewById(R.id.gridView);
                gridView.setNumColumns(cellsPerRow);

                gridView.setHorizontalSpacing(10);
                gridView.setVerticalSpacing(10);

                gridView.setAdapter(new BackgroundColorAdapter(getBaseContext()));

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        final TextView textView = (TextView) parent.getItemAtPosition(position);
                        System.out.println(textView.getText());
                        gridView.invalidateViews();
                    }
                });

            }
        }

        private String inputStreamToString(InputStream inputStream) {
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        public class BackgroundColorAdapter extends BaseAdapter {

            private final Context context;

            public BackgroundColorAdapter(Context context) {
                this.context = context;
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = new TextView(context);
                    textView.setHeight(110);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundColor(Color.parseColor("green"));
                    textView.setTextColor(Color.parseColor("black"));
                    textView.setText("Alma");

                } else{
                    if (position == 2) {
                        textView = (TextView) convertView;
                        textView.setText("test");
                    } else {
                        textView = (TextView) convertView;
                    }

                }
                return textView;            }
        }
    }
}

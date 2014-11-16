package com.buendia.clientapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Group extends Activity {

    private String groupName;
    private String ip;
    List<String> names;
    private int selectedGroup;
    private List<String> status = new ArrayList<String>();
    JSONObject labels = null;
    private String urlStatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        this.groupName = intent.getStringExtra("GROUP_NAME");
        this.ip = intent.getStringExtra("IP");
        this.selectedGroup = intent.getIntExtra("POSITION", 0);
        final TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(groupName);
        status = new ArrayList<String>();
        new GroupUpdateAsyncTask().execute(ip);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void refresh(View view) {
        status = new ArrayList<String>();
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
            urlStatePost = urlState;

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

            if (result[0] == null || result[1] == null) {

                Context context = getApplicationContext();
                CharSequence text = "Downloading failed!";
                int duration = Toast.LENGTH_SHORT;

                Toast refreshingToast = Toast.makeText(context, text, duration);
                refreshingToast.show();

            } else {

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

                final List<String> orderState = new ArrayList<String>();
                for (int i = 0; i < layout.length(); i++) {
                    try {
                        for (int j = 0; j < layout.getJSONArray(i).length(); j++) {
                            orderState.add(layout.getJSONArray(i).getString(j));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                names = new ArrayList<String>();
                for (int i = 0; i < orderState.size(); i++) {
                    try {
                        names.add(labels.getString(orderState.get(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                int cellsPerRow = 0;
                if (layout.length() == 0) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence error = "Downloading completed but there is nothing to show!";
                    Toast errorToast = Toast.makeText(context, error, duration);
                    errorToast.show();
                } else {
                    //check the how many cells/row should it use
                    try {
                        cellsPerRow = layout.getJSONArray(0).length() + 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                InputStream inputStreamState = null;
                try {
                    inputStreamState = result[1].getEntity().getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String contentState = inputStreamToString(inputStreamState);
                try {
                    inputStreamState.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject dataState = null;
                try {
                    dataState = new JSONObject(contentState);
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

                JSONArray order = null;
                if (data != null) {
                    try {
                        order = tabs.getJSONArray("order");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                int i = 0;
                try {
                    for (i = 0; i < order.length(); i++) {
                        JSONObject jsonObject = dataState.getJSONObject(order.getString(i));
                        try {

                            for (int j = 0; j < labels.length(); j++) {
                                status.add(jsonObject.getString(orderState.get(j)));
                            }

                        } catch (JSONException e) {
                            status.add("0");
                        }

                    }
                } catch (JSONException e) {
                    for (int j = 0; j < labels.length(); j++) {
                        status.add("0");
                    }
                }

                for (int j = 0; j < status.size(); j++) {
                }

                final GridView gridView = (GridView) findViewById(R.id.gridView);
                gridView.setNumColumns(cellsPerRow);

                gridView.setHorizontalSpacing(10);
                gridView.setVerticalSpacing(10);

                gridView.setAdapter(new BackgroundColorAdapter(getBaseContext()));

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        String message = null;
                        String currentStatus = null;
                        if (selectedGroup == 0) {
                            currentStatus = status.get(position);
                            if (currentStatus.equals("0")) {
                                message = "1";
                            } else {
                                message = "0";
                            }
                        }

                        if (selectedGroup != 0) {
                            currentStatus = status.get(selectedGroup * labels.length() + position);
                            if (currentStatus.equals("0")) {
                                message = "1";
                            } else {
                                message = "0";
                            }
                        }
                            System.out.println("selected group: " + Integer.toString(selectedGroup+1) + " order state: " + orderState.get(position)  + " message: " + message);

                            new PostAsyncTask().execute(Integer.toString(selectedGroup+1), orderState.get(position), message);

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
                return names.size();
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
                    textView.setText(names.get(position));
                    if (selectedGroup == 0) {
                        String currentStatus = status.get(position);
                        if (currentStatus.equals("0")) {
                            textView.setBackgroundColor(Color.parseColor("green"));
                        } else {
                            textView.setBackgroundColor(Color.parseColor("red"));
                        }
                    }

                    if (selectedGroup != 0) {
                        String currentStatus = status.get(selectedGroup * labels.length() + position);
                        if (currentStatus.equals("0")) {
                            textView.setBackgroundColor(Color.parseColor("green"));
                        } else {
                            textView.setBackgroundColor(Color.parseColor("red"));
                        }
                    }

                } else {
                    textView = (TextView) convertView;
                    if (selectedGroup == 0) {
                        String currentStatus = status.get(position);
                        if (currentStatus.equals("0")) {
                            textView.setBackgroundColor(Color.parseColor("green"));
                        } else {
                            textView.setBackgroundColor(Color.parseColor("red"));
                        }
                    }

                    if (selectedGroup != 0) {
                        String currentStatus = status.get(selectedGroup * labels.length() + position);
                        if (currentStatus.equals("0")) {
                            textView.setBackgroundColor(Color.parseColor("green"));
                        } else {
                            textView.setBackgroundColor(Color.parseColor("red"));
                        }
                    }
                }
                return textView;
            }
        }
    }

    public class PostAsyncTask extends AsyncTask<String, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(String... params) {
            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI(urlStatePost));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("tid", params[0]));
                nameValuePairs.add(new BasicNameValuePair(params[1], params[2]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
                CharSequence text = "Status update failed!";
                int duration = Toast.LENGTH_SHORT;

                Toast refreshingToast = Toast.makeText(context, text, duration);
                refreshingToast.show();
            } else {
                refresh(null);
            }
        }
    }
}

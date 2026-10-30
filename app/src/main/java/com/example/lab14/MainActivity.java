package com.example.lab14;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnSimple, btnComplex, btnArray;
    TextView txt;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSimple = findViewById(R.id.btnSimple);
        btnComplex = findViewById(R.id.btnComplex);
        btnArray = findViewById(R.id.btnArray);
        txt = findViewById(R.id.txt);
        listView = findViewById(R.id.listView);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParseJSONSimple().execute("http://192.168.1.25/method/jsonarray.php");
            }
        });

        btnArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParseJSONArray().execute("http://192.168.1.25/method/jsonarray1.php");
            }
        });
    }

    class ParseJSONSimple extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL link = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) link.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(1500);
                httpURLConnection.setDoInput(true);
                if (httpURLConnection.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    String name = jsonObject.has("name") ? jsonObject.getString("name") : "";
                    boolean gender = jsonObject.getBoolean("gender");
                    String hobbies = jsonObject.getString("hobbies");
                    String phone = jsonObject.getString("phone");
                    int age = jsonObject.getInt("age");
                    return "Name: " + name + "\nGender: " + gender + "\nHobbies: " + hobbies + "\nPhone: " + phone + "\nAge: " + age;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getString(R.string.error_msg);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            txt.setText(s);
        }
    }

    class ParseJSONArray extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                URL link = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) link.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(1500);
                httpURLConnection.setDoInput(true);
                if (httpURLConnection.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    ArrayList<String> tempArray = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONArray employees = jsonObject.getJSONArray("employees");
                    for (int i = 0; i < employees.length(); i++) {
                        JSONObject obj = employees.getJSONObject(i);
                        tempArray.add(obj.getString("name") + "\n" + obj.getString("hobbies") + "\n" + obj.getInt("age") + "\n" + obj.getBoolean("gender") + "\n" + obj.getString("phone"));
                    }
                    return tempArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if (s != null) {
                txt.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                arrayList.clear();
                arrayList.addAll(s);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
package com.shaquibquraishi.demoweatherapp;

import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    TextView textViewWeather;
    TextView textViewTemp;
    TextView textViewHumidity;
    EditText editTextCityName;
    ProgressBar progressBar;

    public class DownloadTask extends AsyncTask<String, Void ,String> {
        @Override
        protected String doInBackground(String... urls){
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject jsonObject=new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");
                String tempInfo=jsonObject.getString("main");
                JSONObject jsonObject2=new JSONObject(tempInfo);

                JSONArray array=new JSONArray(weatherInfo);
                for(int i=0;i<array.length();i++){
                    JSONObject jsonObject1=array.getJSONObject(i);
                    Log.i("main",jsonObject1.getString("main"));
                    Log.i("description",jsonObject1.getString("description"));
                    String weather=jsonObject1.getString("main")+" : "+jsonObject1.getString("description");
                    textViewWeather.setText(weather);

                }
                //Log.i("temp",jsonObject2.getString("temp_max")+ jsonObject2.getString("temp_min"));
                double t= Double.parseDouble(jsonObject2.getString("temp"))-273.15;
                String temp=new DecimalFormat("##.##").format(t)+" C";

                String humidity=jsonObject2.getString("humidity")+"%";
                textViewTemp.setText(temp);
                textViewHumidity.setText(humidity);
                progressBar.setVisibility(View.GONE);



            }catch (JSONException e){
                e.printStackTrace();
            }


        }
    }
  public void onWeatherButton(View view){
      DownloadTask task=new DownloadTask();

      if(editTextCityName.getText()!=null) {
          progressBar.setVisibility(View.VISIBLE);
          String cityName=editTextCityName.getText().toString().trim();
          task.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid=0bf257ad991546fdab6f62ce69f5eec7");
      }else{
          Toast.makeText(getApplicationContext(),"Please enter a city name",Toast.LENGTH_SHORT).show();
      }

  }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextCityName=findViewById(R.id.editTextCityName);
        textViewWeather=findViewById(R.id.textViewWeather);
        progressBar=findViewById(R.id.progressBar);
        textViewTemp=findViewById(R.id.textViewTemp);
        textViewHumidity=findViewById(R.id.textViewHumidity);
        textViewWeather.setText("");
        textViewTemp.setText("");
        textViewHumidity.setText("");


    }
}

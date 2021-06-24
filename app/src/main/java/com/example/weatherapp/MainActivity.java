 package com.example.weatherapp;

 import android.content.Context;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.view.inputmethod.InputMethodManager;
 import android.widget.EditText;
 import android.widget.TextView;

 import androidx.appcompat.app.AppCompatActivity;

 import org.json.JSONArray;
 import org.json.JSONObject;

 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView details;

    public void selectCity(View v){
    try{
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName.getText().toString() +"&appid=" + getString(R.string.api_key));
        }catch (Exception e){
            Log.e("error", "onCreate: "+ e.getMessage() );
        }

        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
    }


    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                String result="";
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }

                 return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

       protected void onPostExecute(String s){
           try {
               String message="";
               JSONObject obj = new JSONObject(s);
               String coords = obj.getString("coord");
               String latitude = new JSONObject(coords).getString("lat");
               String longitude = new JSONObject(coords).getString("lon");
               message += "LatLng = " + longitude + "  "+ latitude + "\n";

               String weatherInfo = obj.getString("weather");
               JSONArray arr = new JSONArray(weatherInfo);

               for(int i=0;i<arr.length();i++){

                   JSONObject jsonPart = arr.getJSONObject(i);
                   String main = jsonPart.getString("main");
                   String description = jsonPart.getString("description");

                   if(!main.equals("") && !description.equals("")){
                       message += main + ": " + description +"\n";
                   }
               }

               String temperature = obj.getString("main");
               String temp = new JSONObject(temperature).getString("temp");
               String pressure = new JSONObject(temperature).getString("pressure");
               String humidity = new JSONObject(temperature).getString("humidity");

               float tempInCelsius = Float.parseFloat(temp);
               tempInCelsius = tempInCelsius - (float)273.15;

                message += "Temperature : " + String.valueOf(tempInCelsius) + "°C" + "\n";
                message += "Pressure : " + pressure + " mb" + "\n";
                message += "Humidity = " + humidity  + "%";

               if(!message.equals("")){
                   details.setText(message);
               }

           }catch (Exception e){
               e.printStackTrace();
               details.setText("SomeThing is wrong ");
           }
       }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.cityName);
        details = (TextView)findViewById(R.id.details);

    }
}

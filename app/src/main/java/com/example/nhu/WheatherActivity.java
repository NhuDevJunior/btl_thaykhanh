package com.example.nhu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WheatherActivity extends AppCompatActivity {

    EditText city, country;
    TextView result;

    private final String URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String apikey = "ded89ed57affea7a6cda23019ba88e72";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheather);

        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        result = findViewById(R.id.tvResult);
    }

    public void getWheather(View view) {
        String tempURL = "";
        String cityName = city.getText().toString().trim();
        String countryName = country.getText().toString().trim();

        if (cityName.equals("")) {
            result.setText("City field can not be empty !");
        } else {
            if (!countryName.equals("")) {
                tempURL = URL + "?q=" + cityName + "," + countryName + "&appid=" + apikey;

            } else {
                tempURL = URL + "?q=" + cityName + "&appid=" + apikey;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output= "";
                    try {
                        JSONObject jsonObjectRespone =  new JSONObject(response);

                        JSONArray jsonArray = jsonObjectRespone.getJSONArray("weather");
                        JSONObject jsonObjectWheather = jsonArray.getJSONObject(0);
                        String des = jsonObjectWheather.getString("description");

                        JSONObject jsonObjectMain = jsonObjectRespone.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        int humidity = jsonObjectMain.getInt("humidity");

                        JSONObject jsonObjectWind = jsonObjectRespone.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");


                        JSONObject jsonObjectSys = jsonObjectRespone.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonObjectRespone.getString("name");

                        output += "Thời tiết hiện tại của: " + cityName + "(" + countryName + ")"+
                                "\n Nhiệt độ: " + df.format(temp) + "độ C" +
                                "\n Cảm giác như: " + df.format(feelLike) + "độ C" +
                                "\n Độ ẩm: " + humidity + "%" +
                                "\n Tốc độ gió: " + wind + "m/s" ;
                        result.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
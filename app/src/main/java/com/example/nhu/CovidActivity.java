package com.example.nhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;

public class CovidActivity extends AppCompatActivity {


    TextView tvCases, tvRecovered,
            tvcasesPerOneMillion, tvActive,
            tvTodayCases, tvTotalDeaths,
            tvTodayDeaths,
            tvPopulation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid);

        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvcasesPerOneMillion = findViewById(R.id.tvcasesPerOneMillion);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvPopulation = findViewById(R.id.tvPopulation);

        Calendar calendar = Calendar.getInstance();
        String curentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.date);
        textViewDate.setText(curentDate);


        // Creating a method fetchdata()
        fetchdata();
    }

    private void fetchdata()
    {

        // Create a String request
        // using Volley Library
        String url = "https://disease.sh/v3/covid-19/countries/vietnam";

        StringRequest request
                = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        // Handle the JSON object and
                        // handle it inside try and catch
                        try {

                            // Creating object of JSONObject
                            JSONObject jsonObject
                                    = new JSONObject(
                                    response.toString());

                            // Set the data in text view
                            // which are available in JSON format
                            // Note that the parameter inside
                            // the getString() must match
                            // with the name given in JSON format
                            tvCases.setText(
                                    jsonObject.getString(
                                            "cases"));
                            tvRecovered.setText(
                                    jsonObject.getString(
                                            "recovered"));
                            tvcasesPerOneMillion.setText(
                                    jsonObject.getString(
                                            "casesPerOneMillion"));
                            tvActive.setText(
                                    jsonObject.getString(
                                            "active"));
                            tvTodayCases.setText(
                                    jsonObject.getString(
                                            "todayCases"));
                            tvTotalDeaths.setText(
                                    jsonObject.getString(
                                            "deaths"));
                            tvTodayDeaths.setText(
                                    jsonObject.getString(
                                            "todayDeaths"));
                            tvPopulation.setText(
                                    jsonObject.getString(
                                            "population"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(
                                CovidActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        RequestQueue requestQueue
                = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
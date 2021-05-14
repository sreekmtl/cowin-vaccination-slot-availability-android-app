package com.sreehari.vacslot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private String url="";

    private String districtCode;

    private String districtName;

    private String temp;
    private String[] centerName=new String[300];
    private String[] centerDistrict= new String[300];
    private String[] sessions=new String[300];
    private String[] fare= new String[300];
    private String[] pincode= new String[300];
    private String[] disState= new String[300];

    private final List<ListItem> listItems= new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        progressBar=findViewById(R.id.loadProgress);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter= new MyAdapter(listItems,this);

        Intent intent= getIntent();
        districtCode=intent.getStringExtra("DISCODE");
        districtName=intent.getStringExtra("DISTRICTNAME");

        Calendar calendar= Calendar.getInstance();
        int d= calendar.get(Calendar.DAY_OF_MONTH);
        int m=calendar.get(Calendar.MONTH);
        int y=calendar.get(Calendar.YEAR);

        m=m+1;

        String finalDate=d+"-"+m+"-"+y;

        url="https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+districtCode+"&date="+finalDate.trim();
        searchSlots();



    }

    private void searchSlots(){

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ResultActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                requestQueue.stop();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void processResponse(String response){
        String hehe="";
        try {

            JSONObject jsonObject= new JSONObject(response);
            temp= jsonObject.getString("centers");
            JSONArray jsonArray= new JSONArray(temp);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                centerName[i] = jsonObject1.getString("name");
                centerDistrict[i]=jsonObject1.getString("district_name");
                fare[i]=jsonObject1.getString("fee_type");
                sessions[i]=jsonObject1.getString("sessions");
                pincode[i]=jsonObject1.getString("pincode");
                disState[i]=jsonObject1.getString("district_name")+", "+jsonObject1.getString("state_name");

            }

            hehe=centerName[1];

            for (int j=0;j<centerName.length;j++){
                if (centerName[j]!=null) {
                    ListItem listItem = new ListItem("" + centerName[j], ""+fare[j], "" + sessions[j],""+disState[j],""+pincode[j]);
                    listItems.add(listItem);
                }
            }


            recyclerView.setAdapter(myAdapter);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);


        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
package com.sreehari.vacslot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private Button button;
    String url="https://www.google.com";
    private Boolean isCheck=false;
    private static final String shared_prefs="sharedprefs";

    private AutoCompleteTextView statetxt;
    private AutoCompleteTextView districttxt;
    final String[] dis_name= new String[756];
    final String[] dis_id= new String[756];

    private Spinner ageSpinner;
    private String minAge;

    private String ageStr;

    private TextView notiDetailTv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notiDetailTv=findViewById(R.id.notiDetailTxt);
        openJson();
        button=findViewById(R.id.button);

        ageSpinner=findViewById(R.id.ageActvMain);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(MainActivity.this,R.array.ages, android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(spinnerAdapter);

        districttxt=findViewById(R.id.districtActvMain);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,dis_name);
        districttxt.setAdapter(adapter);



        SharedPreferences sharedPreferences= getSharedPreferences(shared_prefs,Context.MODE_PRIVATE);
        isCheck=sharedPreferences.getBoolean("ISCHECK",false);
        minAge=sharedPreferences.getString("MIN_AGE","45");

        if (isCheck==false){
            districttxt.setEnabled(true);
            ageSpinner.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (districttxt.length()==0 || ageSpinner.getSelectedItem().toString().equalsIgnoreCase("Select minimum age limit")){
                        Toast.makeText(MainActivity.this, "Enter district name and age limit", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        check();
                    }
                }
            });
        } else if (isCheck==true){
            districttxt.setText(sharedPreferences.getString("DISTRICT_NAME","Not Found"));
            ageStr=sharedPreferences.getString("MIN_AGE","");
            districttxt.setEnabled(false);
            ageSpinner.setEnabled(false);
            notiDetailTv.setText("You have selected "+districttxt.getText().toString()+" and "+"age limit of minimum age "+ageStr);
            button.setText("Turn off Notifications");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check();
                }
            });

        }






    }

    public void check(){
        if (isCheck==false){
            startAlarm();
        }else{
              stopAlarm();
        }
    }
    private void save_sp(Boolean a){
        SharedPreferences sharedPreferences= this.getSharedPreferences(shared_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ISCHECK",a);
        editor.apply();
        editor.commit();
    }
    private void startAlarm(){

        String id="";
        ageStr= ageSpinner.getSelectedItem().toString();

        for (int i=0;i<dis_name.length;i++){
            if (districttxt.getText().toString().equalsIgnoreCase(dis_name[i])){
                id=dis_id[i];
            }
        }

        AlarmManager alarmManager= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(this,AlertReceiver.class);

        SharedPreferences sp= this.getSharedPreferences(shared_prefs,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sp.edit();
        editor.putString("DISTRICT_UID",id);
        editor.putString("DISTRICT_NAME",districttxt.getText().toString());
        editor.putString("MIN_AGE",ageStr);
        editor.apply();
        editor.commit();

        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,1,intent,0);
       alarmManager.cancel(pendingIntent);
       alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),10000,pendingIntent);
        Toast.makeText(this, "Slot Checking Started", Toast.LENGTH_SHORT).show();
        button.setText("Turn off notifications");
        isCheck=true;
        save_sp(true);
        districttxt.setEnabled(false);
        ageSpinner.setEnabled(false);
        notiDetailTv.setText("You have selected "+districttxt.getText().toString()+" and "+"age limit of minimum age "+ageStr);
    }

    private void stopAlarm(){
        AlarmManager alarmManager= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(this,AlertReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,1,intent,0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Slot Checking Cancelled", Toast.LENGTH_SHORT).show();
        button.setText("Turn on notifications");
        isCheck=false;
        save_sp(false);
        districttxt.setEnabled(true);
        ageSpinner.setEnabled(true);
        notiDetailTv.setText("");
    }

    public void openJson(){
        String json;
        try {
            InputStream inputStream= getApplicationContext().getAssets().open("districts.json");
            int size=inputStream.available();
            byte buffer[]=new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json=new String(buffer,"UTF-8");
            JSONArray jsonArray= new JSONArray(json);

            for (int i =0;i<jsonArray.length();i++){
                JSONObject jsonObject= jsonArray.getJSONObject(i);
                dis_name[i]=jsonObject.getString("district_name");
                dis_id[i]= jsonObject.getString("district_id");

            }

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
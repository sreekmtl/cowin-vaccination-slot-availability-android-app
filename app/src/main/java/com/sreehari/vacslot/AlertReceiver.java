package com.sreehari.vacslot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {
    Context mcontext;
    String url="";

    private String temp;
    private String[] centerName=new String[300];
    private String[] centerDistrict= new String[300];
    private String[] sessions=new String[300];
    private String[] fare= new String[300];
    private String[][] dateavail= new String[300][50];
    private int[][] capacity= new int[300][50];
    private String[][] ageLimit = new String[300][50];
    private String[][] vaccineType= new String[300][50];

    private String id;
    private String age;
    private String districtName;

    private int count;

    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences sharedPreferences= context.getSharedPreferences("sharedprefs",Context.MODE_PRIVATE);
        id=sharedPreferences.getString("DISTRICT_UID","250");
        age=sharedPreferences.getString("MIN_AGE","45");
        districtName=sharedPreferences.getString("DISTRICT_NAME","");
        count=sharedPreferences.getInt("COUNT",1);
        count=count+1;
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putInt("COUNT",count);
        editor.apply();
        editor.commit();

        Calendar calendar= Calendar.getInstance();
        int d= calendar.get(Calendar.DAY_OF_MONTH);
        int m=calendar.get(Calendar.MONTH);
        int y=calendar.get(Calendar.YEAR);

        m=m+1;

        String finalDate=d+"-"+m+"-"+y;

        url="https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+id+"&date="+finalDate.trim();

        mcontext=context;

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
            }
        });
        requestQueue.add(stringRequest);




    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void notiChannel(String id,String name,int imp){
        NotificationChannel channel= new NotificationChannel(id,name,imp);
        channel.setShowBadge(true);

        NotificationManager notificationManager= (NotificationManager)mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager!=null;
        notificationManager.createNotificationChannel(channel);
    }

    private void Notify(String text){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
          notiChannel("CHANNEL_1","Slot Channel",NotificationManager.IMPORTANCE_HIGH);

          NotificationCompat.Builder notification = new NotificationCompat.Builder(mcontext,"CHANNEL_1");

          notification.setSmallIcon(R.drawable.ic_baseline_medical_services_24)
                  .setContentTitle("Vaccine Slot")
                  .setContentText(text+" Available in "+districtName)
                  .setNumber(1);
          NotificationManager notificationManager= (NotificationManager)mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
          assert notificationManager!=null;
          notificationManager.notify(1,notification.build());
      } else{
            NotificationCompat.Builder notification1= new NotificationCompat.Builder(mcontext)
                    .setSmallIcon(R.drawable.ic_baseline_medical_services_24)
                    .setContentTitle("Vaccine Slot")
                    .setContentText(text+" Available in "+districtName);
            notification1.setPriority(NotificationManager.IMPORTANCE_HIGH);
            Uri notisound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification1.setSound(notisound);
            long[] p={500,500,500,500};
            notification1.setVibrate(p);
            NotificationManager manager= (NotificationManager)mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(5,notification1.build());
        }
    }

    private void processResponse(String response){

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

                JSONArray jsonArray1= new JSONArray(sessions[i]);
                for (int k=0;k<jsonArray1.length();k++){
                    JSONObject jsonObject2= jsonArray1.getJSONObject(k);
                    dateavail[i][k]=jsonObject2.getString("date");
                    capacity[i][k]=jsonObject2.getInt("available_capacity");
                    ageLimit[i][k]=jsonObject2.getString("min_age_limit");
                    vaccineType[i][k]=jsonObject2.getString("vaccine");
                }

            }



        }catch (JSONException e){
            e.printStackTrace();
        }
        checkSlots();

    }

    private void checkSlots(){

        int total=0;

        for (int a=0;a<200;a++){
            for (int b=0;b<10;b++){
                if (ageLimit[a][b]!=null) {
                    if (age.contains(ageLimit[a][b])) {
                        total = total + capacity[a][b];
                    }
                }
            }
        }

        if (total==0){

        }else{
            Notify(""+total+" Slots");
        }

    }
}

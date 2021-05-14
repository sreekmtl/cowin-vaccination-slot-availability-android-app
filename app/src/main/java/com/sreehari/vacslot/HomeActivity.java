package com.sreehari.vacslot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class HomeActivity extends AppCompatActivity {

    private Button searchButton;
    private Button notiButton;

    private AutoCompleteTextView districtActv;

    final String[] dis_name= new String[756];
    final String[] dis_id= new String[756];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        openJson();

        check_connectivity();
        alert();


        districtActv=findViewById(R.id.districtActvHome);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this,android.R.layout.simple_list_item_1,dis_name);
        districtActv.setAdapter(adapter);

        notiButton=findViewById(R.id.notibtn);
        notiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        searchButton=findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id="";

                if (districtActv.length()==0){
                    Toast.makeText(HomeActivity.this, "Enter a district", Toast.LENGTH_SHORT).show();
                }else {

                    for (int i = 0; i < dis_name.length; i++) {
                        if (districtActv.getText().toString().equalsIgnoreCase(dis_name[i])) {
                            id = dis_id[i];
                        }
                    }
                    Intent intent = new Intent(HomeActivity.this, ResultActivity.class);
                    intent.putExtra("DISCODE", id);
                    intent.putExtra("DISTRICTNAME", districtActv.getText().toString());
                    startActivity(intent);
                }
            }
        });

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

    public void check_connectivity(){

        ConnectivityManager connectivityManager= (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){
            //ok
        }else{
            customToast("Connect to Internet");
        }


    }

    public void customToast(String toast){
        AlertDialog.Builder tbuilder = new AlertDialog.Builder(this);
        View tview = getLayoutInflater().inflate(R.layout.toastalert,null);
        final TextView toasttxt = tview.findViewById(R.id.alerttext);
        final TextView tstokbtn= tview.findViewById(R.id.okbtntst);
        toasttxt.setText(toast);
        tbuilder.setView(tview);
        final AlertDialog alertDialog = tbuilder.create();
        alertDialog.show();
        tstokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }

    public void alert(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.toastalert,null);
        final TextView alertText=view.findViewById(R.id.alerttext);
        final TextView okbtn=view.findViewById(R.id.okbtntst);
        alertText.setText(R.string.alert);
        builder.setView(view);
        final AlertDialog malertdialog= builder.create();
        malertdialog.show();
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                malertdialog.cancel();
            }
        });
    }

}
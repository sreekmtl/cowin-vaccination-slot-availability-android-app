package com.sreehari.vacslot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewHolder> {

    private String centerName,feeType,sessions;
    private final List<ListItem> listItems;
    private final Context context;

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        viewHolder holder= new viewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

         String[] date= new String[50];
         String[] age= new String[50];
         String[] capacity= new String[50];
         String[] vaccine = new String[50];

         String sessionStr="";


        ListItem listItem= listItems.get(position);
        holder.centerNameTv.setText(listItem.getCenterName());
        holder.fareTv.setText(listItem.getFare());
       try {
           JSONArray jsonArray = new JSONArray(listItem.getSessions());
           for (int n=0;n<jsonArray.length();n++){
               JSONObject jsonObject= jsonArray.getJSONObject(n);
               date[n]= jsonObject.getString("date");
               age[n]= jsonObject.getString("min_age_limit");
               capacity[n]= jsonObject.getString("available_capacity");
               vaccine[n]= jsonObject.getString("vaccine");

               sessionStr=sessionStr+ date[n]+" Age: "+age[n]+" Slots: "+capacity[n]+" - "+vaccine[n]+"\n";
           }
       }catch (JSONException e){
           e.printStackTrace();
       }

        holder.sessionsTv.setText(sessionStr);
       holder.pinTv.setText(listItem.getPincode());
       holder.disTv.setText(listItem.getDistrictName());


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        final LinearLayout linearLayout;
        private final TextView centerNameTv;
        private final TextView sessionsTv;
        private final TextView fareTv;
        private final TextView pinTv;
        private final TextView disTv;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.cardviewlayout);
            centerNameTv=itemView.findViewById(R.id.centernameTxt);
            sessionsTv=itemView.findViewById(R.id.sessionsTxt);
            fareTv=itemView.findViewById(R.id.fareTxt);
            pinTv=itemView.findViewById(R.id.pincode);
            disTv=itemView.findViewById(R.id.districttxt);
        }
    }
}

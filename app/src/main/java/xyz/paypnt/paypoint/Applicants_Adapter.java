package xyz.paypnt.paypoint;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.core.content.ContextCompat.startActivity;

public class Applicants_Adapter extends RecyclerView.Adapter<Applicants_Adapter.AdapterViewHolder> {

    HashMap<String, ArrayList<String>> list;

    public Applicants_Adapter(HashMap<String, ArrayList<String>> list, Context context) {
        this.list = list;
    }

    //Creates ViewHolder
    @Override
    public Applicants_Adapter.AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.applicants_view_item,parent,false);
        AdapterViewHolder viewHolder=new AdapterViewHolder(v);
        return viewHolder;
    }

    //Sets Data of Views
    @Override
    public void onBindViewHolder(Applicants_Adapter.AdapterViewHolder holder, int position) {
        Log.d("HELLO", holder.uid.getText().toString());
        holder.uid.setText(list
                .get(String.valueOf(position))
                .get(0));
        holder.username.setText(list
                .get(String.valueOf(position))
                .get(1));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Applicants()).startAct(holder.username.getText().toString());
            }
        });
    }

    //Initializes Views for each iteration of ViewItem
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder{

        protected TextView username;
        protected TextView uid;
        protected LinearLayout layout;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.app_username_01);
            uid = (TextView) itemView.findViewById(R.id.app_uid_01);
            layout = (LinearLayout) itemView.findViewById(R.id.app_layout);

        }
    }
}

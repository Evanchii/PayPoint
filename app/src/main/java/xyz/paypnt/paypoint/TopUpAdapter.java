package xyz.paypnt.paypoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class TopUpAdapter extends RecyclerView.Adapter<TopUpAdapter.AdapterViewHolder> {

    HashMap<String, ArrayList<String>> list;

    public TopUpAdapter(HashMap<String, ArrayList<String>> list, Context context) {
        this.list = list;
    }

    //Creates ViewHolder
    @Override
    public TopUpAdapter.AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.topup_view_item,parent,false);
        AdapterViewHolder viewHolder=new AdapterViewHolder(v);
        return viewHolder;
    }

    //Sets Data of Views
    @Override
    public void onBindViewHolder(TopUpAdapter.AdapterViewHolder holder, int position) {
        holder.type.setText(list.get(String.valueOf(position)).get(0));
        holder.amount_status.setText(list.get(String.valueOf(position)).get(1));
        holder.date.setText(list.get(String.valueOf(position)).get(2));
    }

    //Initializes Views for each iteration of ViewItem
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder{

        protected TextView type;
        protected TextView amount_status;
        protected TextView date;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            type= (TextView) itemView.findViewById(R.id.rec_type);
            amount_status= (TextView) itemView.findViewById(R.id.rec_amount_status);
            date= (TextView) itemView.findViewById(R.id.rec_date);
        }
    }
}

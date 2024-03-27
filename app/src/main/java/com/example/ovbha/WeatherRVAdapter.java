package com.example.ovbha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {


    private Context context;
    private ArrayList<WeatherTVModal> weatherTVModalsArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherTVModal> weatherTVModalsArrayList) {
        this.context = context;
        this.weatherTVModalsArrayList = weatherTVModalsArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherTVModal modal = weatherTVModalsArrayList.get(position);
        holder.temptxt.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditiontxt);
        holder.windtxt.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = input.parse(modal.getTime());
            holder.timetxt.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherTVModalsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windtxt,temptxt,timetxt;
        private ImageView conditiontxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windtxt=itemView.findViewById(R.id.txtWindSpeed);
            temptxt=itemView.findViewById(R.id.txtTemp);
            timetxt=itemView.findViewById(R.id.txtTime);
            conditiontxt=itemView.findViewById(R.id.IVCondition);
        }
    }
}

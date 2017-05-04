package com.utt.application.isradeleon.vendedorescorso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Isra on 4/12/2017.
 */

public class MyDatesAdapter extends ArrayAdapter {
    Context con;
    LayoutInflater inflate;
    List indexes;
    int datesType;

    public MyDatesAdapter(Context context, List objects, LayoutInflater in, int option) {
        super(context, R.layout.date_item, objects);
        con=context;
        indexes=objects;
        inflate=in;
        datesType=option;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;
        if(itemView==null){
            itemView=inflate.inflate(R.layout.date_item,parent,false);
        }
        try{
            JSONObject date=SingleJSON.getInstance().getJsonArray().getJSONObject(Integer.parseInt(String.valueOf(indexes.get(position))));

            String url="http://corso.esy.es/"+date.getJSONObject("casa").getJSONArray("fotos").getJSONObject(0).getString("string_foto");
            Picasso.with(con).load(url).into(((ImageView)itemView.findViewById(R.id.image_house)));

            JSONObject client=date.getJSONObject("cliente");
            ((TextView)itemView.findViewById(R.id.client_name)).setText(
                    client.getString("nombre")+" "+client.getString("ap_paterno")+" "+client.getString("ap_materno")+"."
            );

            switch(datesType){
                case 0:
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat targetFormat = new SimpleDateFormat("dd / MMMM / yyyy");
                        Date dateObject = originalFormat.parse(date.getString("fecha_hora"));
                        String formattedDate = targetFormat.format(dateObject);

                        originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        targetFormat = new SimpleDateFormat("h:mm a");
                        dateObject = originalFormat.parse(date.getString("fecha_hora"));
                        String formattedTime=targetFormat.format(dateObject);
                        ((TextView)itemView.findViewById(R.id.date_time)).setText(formattedDate+"\n"+formattedTime);
                        (itemView.findViewById(R.id.date_time)).setVisibility(View.VISIBLE);
                        (itemView.findViewById(R.id.status_q)).setVisibility(View.GONE);
                        (itemView.findViewById(R.id.status_fail)).setVisibility(View.GONE);
                        (itemView.findViewById(R.id.status_ok)).setVisibility(View.GONE);
                    break;
                case 1:
                        (itemView.findViewById(R.id.date_time)).setVisibility(View.GONE);
                        int status=date.getInt("status");
                        if (status==2){
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.VISIBLE);
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.GONE);
                            (itemView.findViewById(R.id.status_q)).setVisibility(View.GONE);
                        }
                        else if(status==3){
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.VISIBLE);
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.GONE);
                            (itemView.findViewById(R.id.status_q)).setVisibility(View.GONE);
                        }else{
                            (itemView.findViewById(R.id.status_q)).setVisibility(View.VISIBLE);
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.GONE);
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.GONE);
                        }
                    break;
            }


            int type=date.getInt("tipo_cita");
            if (type==1){
                (itemView.findViewById(R.id.date_visit)).setVisibility(View.VISIBLE);
                (itemView.findViewById(R.id.date_sale)).setVisibility(View.GONE);
            }
            else if(type==2){
                ((TextView)itemView.findViewById(R.id.date_sale)).setText("Documentos folio: "+String.valueOf(date.getInt("venta_docs_id")));
                (itemView.findViewById(R.id.date_sale)).setVisibility(View.VISIBLE);
                (itemView.findViewById(R.id.date_visit)).setVisibility(View.GONE);
            }




        }catch(Exception e){
            Toast.makeText(con, "Something wrong happened!", Toast.LENGTH_SHORT).show();
        }
        return itemView;
    }
}

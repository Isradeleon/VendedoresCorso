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
 * Created by Isra on 4/13/2017.
 */

public class MySalesAdapter extends ArrayAdapter {
    Context con;
    LayoutInflater inflate;
    List indexes;
    int salesType;

    public MySalesAdapter(Context context, List objects, LayoutInflater in, int option) {
        super(context, R.layout.sale_item, objects);
        con=context;
        indexes=objects;
        inflate=in;
        salesType=option;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;
        if(itemView==null){
            itemView=inflate.inflate(R.layout.sale_item,parent,false);
        }
        try{
            JSONObject sale=SingleJSON.getInstance().getJsonArray().getJSONObject(Integer.parseInt(String.valueOf(indexes.get(position))));

            String url="http://corso.esy.es/"+sale.getJSONObject("casa").getJSONArray("fotos").getJSONObject(0).getString("string_foto");
            Picasso.with(con).load(url).into(((ImageView)itemView.findViewById(R.id.image_house)));

            ((TextView)itemView.findViewById(R.id.folio_venta)).setText(
                    "Folio: "+String.valueOf(sale.getInt("id"))
            );

            JSONObject client=sale.getJSONObject("cliente");
            ((TextView)itemView.findViewById(R.id.client_name)).setText(
                    client.getString("nombre")+" "+client.getString("ap_paterno")+" "+client.getString("ap_materno")+"."
            );

            switch(salesType){
                case 0:
                        String monto=String.valueOf(sale.get("monto_cubierto"));
                        if (monto!="null"){
                            (itemView.findViewById(R.id.sale_indicator)).setVisibility(View.VISIBLE);
                            ((TextView)itemView.findViewById(R.id.monto_cubierto)).setText("Monto cubierto!");
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.GONE);
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.GONE);
                        }
                    break;
                case 1:
                        int status=sale.getInt("status");
                        if (status==2){
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.VISIBLE);
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.GONE);
                        }
                        else{
                            (itemView.findViewById(R.id.status_fail)).setVisibility(View.VISIBLE);
                            (itemView.findViewById(R.id.status_ok)).setVisibility(View.GONE);
                        }
                    break;
            }

        }catch(Exception e){
            Toast.makeText(con, "Something wrong happened!", Toast.LENGTH_SHORT).show();
        }
        return itemView;
    }
}

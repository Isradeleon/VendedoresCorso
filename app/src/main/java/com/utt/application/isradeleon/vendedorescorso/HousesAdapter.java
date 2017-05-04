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

import java.util.List;

/**
 * Created by Isra on 4/14/2017.
 */

public class HousesAdapter extends ArrayAdapter {
    Context con;
    LayoutInflater inflate;
    List indexes;

    public HousesAdapter(Context context, List objects, LayoutInflater in) {
        super(context, R.layout.house_item, objects);
        con=context;
        indexes=objects;
        inflate=in;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;
        if(itemView==null){
            itemView=inflate.inflate(R.layout.house_item,parent,false);
        }
        try{
            JSONObject house=SingleJSON.getInstance().getJsonArray().getJSONObject(Integer.parseInt(String.valueOf(indexes.get(position))));

            String url="http://corso.esy.es/"+house.getJSONArray("fotos").getJSONObject(0).getString("string_foto");
            Picasso.with(con).load(url).into(((ImageView)itemView.findViewById(R.id.image_house)));

            ((TextView)itemView.findViewById(R.id.house_city)).setText(house.getString("ciudad"));

            /*String address="#"+String.valueOf(house.get("numero_exterior"));
            if (String.valueOf(house.get("numero_interior")) != "null"){
                address+=" Int. "+String.valueOf(house.get("numero_interior"));
            }
            address+=", "+house.getString("calle_o_avenida")+", Col. "+house.getString("colonia")+".";
            ((TextView)itemView.findViewById(R.id.house_address)).setText(address);*/

        }catch(Exception e){
            Toast.makeText(con, "Something wrong happened!", Toast.LENGTH_SHORT).show();
        }
        return itemView;
    }
}

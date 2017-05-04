package com.utt.application.isradeleon.vendedorescorso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HouseActivity extends AppCompatActivity {

    private int position;
    private JSONObject house;
    private JSONArray fotos;
    private ViewFlipper flipper_photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flipper_photos=(ViewFlipper)findViewById(R.id.photos_flipper);

        position=getIntent().getExtras().getInt("position");
        try{
            house=SingleJSON.getInstance().getJsonArray().getJSONObject(position);

            ((Button)findViewById(R.id.maps_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent inin = new Intent(HouseActivity.this,MapsActivity.class);
                        inin.putExtra("lat",house.getString("eje_x_mapa"));
                        inin.putExtra("lng",house.getString("eje_y_mapa"));
                        startActivity(inin);
                    }catch(Exception e){}
                }
            });

            fotos=house.getJSONArray("fotos");
            if(fotos.length()>1){
                ImageButton next=((ImageButton) findViewById(R.id.next_button));
                ImageButton prev=((ImageButton) findViewById(R.id.prev_button));
                next.setVisibility(View.VISIBLE);
                prev.setVisibility(View.VISIBLE);
                for(int i=0; i<fotos.length(); i++){
                    ImageView img=new ImageView(this);
                    ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img.setTag(i);
                    img.setLayoutParams(params);
                    flipper_photos.addView(img);
                    String url="http://corso.esy.es/"+fotos.getJSONObject(i).getString("string_foto");
                    Picasso.with(this).load(url).into(img);
                }
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flipper_photos.showNext();
                    }
                });
                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flipper_photos.showPrevious();
                    }
                });

            }else{
                (findViewById(R.id.next_button)).setVisibility(View.INVISIBLE);
                (findViewById(R.id.prev_button)).setVisibility(View.INVISIBLE);
                ImageView img=new ImageView(this);
                ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setTag(0);
                img.setLayoutParams(params);
                flipper_photos.addView(img);
                String url="http://corso.esy.es/"+fotos.getJSONObject(0).getString("string_foto");
                Picasso.with(this).load(url).into(img);
            }

            String address="Dirección: #"+String.valueOf(house.get("numero_exterior"));
            if (String.valueOf(house.get("numero_interior")) != "null"){
                address+=" Int. "+String.valueOf(house.get("numero_interior"));
            }
            address+=", "+house.getString("calle_o_avenida")+", Col. "+house.getString("colonia")+".";
            ((TextView)findViewById(R.id.address)).setText(address);
            ((TextView)findViewById(R.id.house_city)).setText(house.getString("ciudad"));

            ((TextView)findViewById(R.id.superficie)).setText(
                    "Superficie: "+house.getString("superficie")
            );

            ((TextView)findViewById(R.id.habitaciones)).setText(
                    "Habitaciones: "+String.valueOf(house.getInt("num_habitaciones"))
            );

            ((TextView)findViewById(R.id.banos)).setText(
                    "Banos: "+String.valueOf(house.getInt("num_banos"))
            );

            ((TextView)findViewById(R.id.comentarios)).setText(
                    "Comentarios: "+String.valueOf(house.getString("detalles"))
            );

            ((TextView)findViewById(R.id.precio_estimado)).setText(
                    "Estimado: "+String.valueOf(house.get("precio_estimado"))
            );

            if (String.valueOf(house.get("precio_evaluado")) != "null"){
                (findViewById(R.id.precio_evaluado)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.precio_evaluado)).setText("Precio evaluado: "+String.valueOf(house.get("precio_evaluado")));
            }else{
                (findViewById(R.id.precio_evaluado)).setVisibility(View.GONE);
            }

        }catch(Exception e){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}

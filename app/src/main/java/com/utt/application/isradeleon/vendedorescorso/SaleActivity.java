package com.utt.application.isradeleon.vendedorescorso;

import android.content.Intent;
import android.support.v7.app.ActionBar;
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

public class SaleActivity extends AppCompatActivity {
    private int position;
    private JSONObject sale;
    private JSONObject house;
    private JSONArray fotos;
    private ViewFlipper flipper_photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flipper_photos=(ViewFlipper)findViewById(R.id.photos_flipper);

        position=getIntent().getExtras().getInt("position");
        try{
            sale=SingleJSON.getInstance().getJsonArray().getJSONObject(position);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd / MMMM / yyyy");
            Date dateObject = originalFormat.parse(sale.getString("fecha_inicio"));
            String formattedDate = targetFormat.format(dateObject);
            ((TextView)findViewById(R.id.sale_start)).setText("Inició: "+formattedDate);

            if(String.valueOf(sale.get("fecha_cierre"))!="null"){
                SimpleDateFormat originalFormat3 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat targetFormat3 = new SimpleDateFormat("dd / MMMM / yyyy");
                Date dateObject3 = originalFormat3.parse(sale.getString("fecha_cierre"));
                String formattedDate3 = targetFormat3.format(dateObject3);
                (findViewById(R.id.sale_end)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.sale_end)).setText("Se cerró: "+formattedDate3);
            }else
                (findViewById(R.id.sale_end)).setVisibility(View.GONE);

            house=sale.getJSONObject("casa");

            ((Button)findViewById(R.id.maps_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent inin = new Intent(SaleActivity.this,MapsActivity.class);
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

            ((TextView)findViewById(R.id.folio_venta)).setText(
                    "Folio: "+String.valueOf(sale.getInt("id"))
            );
            int status_sale=sale.getInt("status");
            switch (status_sale){
                case 1:
                    (findViewById(R.id.status_tramite)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_ok)).setVisibility(View.GONE);
                    (findViewById(R.id.status_fail)).setVisibility(View.GONE);
                    break;

                case 2:
                    (findViewById(R.id.status_ok)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_tramite)).setVisibility(View.GONE);
                    (findViewById(R.id.status_fail)).setVisibility(View.GONE);
                    break;

                case 3:
                    (findViewById(R.id.status_fail)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_tramite)).setVisibility(View.GONE);
                    (findViewById(R.id.status_ok)).setVisibility(View.GONE);
                    break;
            }
            JSONObject client=sale.getJSONObject("cliente");
            ((TextView)findViewById(R.id.client_name)).setText(
                    "Cliente: "+client.getString("nombre")+" "+client.getString("ap_paterno")+" "+client.getString("ap_materno")
            );

            JSONObject secre=sale.getJSONObject("secretaria");
            ((TextView)findViewById(R.id.secretaria_nombre)).setText(
                    "Registró: "+secre.getString("nombre")+" "+secre.getString("ap_paterno")+" "+secre.getString("ap_materno")
            );

            ((TextView)findViewById(R.id.monto)).setText(
                    "$ Monto: "+String.valueOf(sale.getDouble("monto"))
            );

            if (String.valueOf(sale.get("monto_cubierto")) != "null"){
                SimpleDateFormat originalFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat targetFormat2 = new SimpleDateFormat("dd / MMMM / yyyy");
                Date dateObject2 = originalFormat2.parse(sale.getString("monto_cubierto"));
                String formattedDate2 = targetFormat2.format(dateObject2);
                (findViewById(R.id.monto_cubierto)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.monto_cubierto)).setText("Monto cubierto el "+formattedDate2);

                (findViewById(R.id.seccion_escrituras)).setVisibility(View.VISIBLE);
            }else{
                (findViewById(R.id.monto_cubierto)).setVisibility(View.GONE);
                (findViewById(R.id.seccion_escrituras)).setVisibility(View.GONE);
            }

            JSONObject docs=sale.getJSONObject("documento");
            if(String.valueOf(docs.get("acta_nacimiento"))!="null"){
                ((ImageView)findViewById(R.id.first_document)).setImageResource(R.drawable.ic_check_box_black_24dp);
            }
            if(String.valueOf(docs.get("ine"))!="null"){
                ((ImageView)findViewById(R.id.second_document)).setImageResource(R.drawable.ic_check_box_black_24dp);
            }
            if(String.valueOf(docs.get("escrituras"))!="null"){
                ((ImageView)findViewById(R.id.third_document)).setImageResource(R.drawable.ic_check_box_black_24dp);
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

package com.utt.application.isradeleon.vendedorescorso;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatCodePointException;

public class DateActivity extends AppCompatActivity {
    private int position;
    private JSONObject date;
    private JSONObject house;
    private JSONArray fotos;
    private ViewFlipper flipper_photos;
    private RequestQueue requestQueue;
    private DBManager dbManager;
    private int id_date=-1;
    private int id_user=-1;
    private String token="";
    private int id_token=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flipper_photos=(ViewFlipper)findViewById(R.id.photos_flipper);

        requestQueue= Volley.newRequestQueue(this);
        dbManager=new DBManager(this);

        position=getIntent().getExtras().getInt("position");
        try{
            Cursor cursor=dbManager.getData();
            if (cursor.moveToFirst()){
                id_user=cursor.getInt(cursor.getColumnIndex(DataTable.C_ID));
                id_token=cursor.getInt(cursor.getColumnIndex(DataTable.C_ID_TOKEN));
                token=cursor.getString(cursor.getColumnIndex(DataTable.C_TOKEN));
            }

            date=SingleJSON.getInstance().getJsonArray().getJSONObject(position);
            id_date=date.getInt("id");

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd / MMMM / yyyy");
            Date dateObject = originalFormat.parse(date.getString("fecha_hora"));
            String formattedDate = targetFormat.format(dateObject);

            originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            targetFormat = new SimpleDateFormat("h:mm a");
            dateObject = originalFormat.parse(date.getString("fecha_hora"));
            String formattedTime=targetFormat.format(dateObject);
            ((TextView)findViewById(R.id.date_time)).setText(formattedDate+" a las "+formattedTime);

            house=date.getJSONObject("casa");

            ((Button)findViewById(R.id.maps_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent inin = new Intent(DateActivity.this,MapsActivity.class);
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

            int type=date.getInt("tipo_cita");
            if (type==1){
                ((TextView)findViewById(R.id.tipo_cita)).setText("Propósito: Visita a la propiedad");
            }
            else {
                ((TextView)findViewById(R.id.tipo_cita)).setText("Propósito: Documentos folio: "+String.valueOf(date.getInt("venta_docs_id")));
            }

            int status_date=date.getInt("status");
            switch (status_date){
                case 1:
                    (findViewById(R.id.status_pendiente)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_ok)).setVisibility(View.GONE);
                    (findViewById(R.id.status_fail)).setVisibility(View.GONE);
                    break;

                case 2:
                    (findViewById(R.id.status_ok)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_pendiente)).setVisibility(View.GONE);
                    (findViewById(R.id.status_fail)).setVisibility(View.GONE);
                    break;

                case 3:
                    (findViewById(R.id.status_fail)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.status_pendiente)).setVisibility(View.GONE);
                    (findViewById(R.id.status_ok)).setVisibility(View.GONE);
                    break;
            }
            JSONObject client=date.getJSONObject("cliente");
            ((TextView)findViewById(R.id.client_name)).setText(
                    "Cliente: "+client.getString("nombre")+" "+client.getString("ap_paterno")+" "+client.getString("ap_materno")
            );

            JSONObject secre=date.getJSONObject("secretaria");
            ((TextView)findViewById(R.id.secretaria_nombre)).setText(
                    "Registró: "+secre.getString("nombre")+" "+secre.getString("ap_paterno")+" "+secre.getString("ap_materno")
            );

            checkDate(date.getInt("id"));
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void successDate(){
        String url="http://corso.esy.es/s_d/"
                +String.valueOf(id_user)+"/"
                +String.valueOf(id_token)+"/"
                +token+"/"
                +String.valueOf(id_date);
        JsonObjectRequest requestS=new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean result=response.getBoolean("Result");
                            if(result){
                                dbManager.updateToken(response.getString("NewToken"));
                                Intent intent = new Intent();
                                intent.putExtra("change", true);
                                setResult(RESULT_OK, intent);
                                finish();
                                /*new AlertDialog.Builder(DateActivity.this)
                                        .setMessage("Cita indicada Exitosa!")
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();*/
                            }else{
                                Toast.makeText(DateActivity.this, "Error en los datos de usuario!", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(DateActivity.this, "Error en la respuesta del servidor!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DateActivity.this, "Error de conexión!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(requestS);
    }

    private void cancelDate(){
        String url="http://corso.esy.es/c_d/"
                +String.valueOf(id_user)+"/"
                +String.valueOf(id_token)+"/"
                +token+"/"
                +String.valueOf(id_date);
        JsonObjectRequest requestC=new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean result=response.getBoolean("Result");
                            if(result){
                                dbManager.updateToken(response.getString("NewToken"));
                                Intent intent = new Intent();
                                intent.putExtra("change", true);
                                setResult(RESULT_OK, intent);
                                finish();
                                /*new AlertDialog.Builder(DateActivity.this)
                                        .setMessage("Cita cancelada!")
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();*/
                            }else{
                                Toast.makeText(DateActivity.this, "Error en los datos de usuario!", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(DateActivity.this, "Error en la respuesta del servidor!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DateActivity.this, "Error de conexión!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(requestC);
    }

    private void checkDate(int id){
        String url="http://corso.esy.es/check_date/"+String.valueOf(id);
        JsonObjectRequest getRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean action=response.getBoolean("Action");
                            boolean success=response.getBoolean("Success");
                            if (action){
                                (findViewById(R.id.date_actions)).setVisibility(View.VISIBLE);
                                //Action to cancel
                                ((Button)findViewById(R.id.cancel_date)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(DateActivity.this)
                                                .setTitle("Cancelar la cita")
                                                .setMessage("¿Seguro de cancelar esta cita??")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        cancelDate();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no,null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                });
                                if(success){
                                    (findViewById(R.id.successful_date)).setVisibility(View.VISIBLE);
                                    //Action to successful date
                                    ((Button)findViewById(R.id.successful_date)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new AlertDialog.Builder(DateActivity.this)
                                                    .setTitle("Cita exitosa")
                                                    .setMessage("¿Indicar cita exitosa?")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            successDate();
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no,null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    });
                                }else{
                                    (findViewById(R.id.successful_date)).setVisibility(View.GONE);
                                }
                            }else{
                                (findViewById(R.id.date_actions)).setVisibility(View.GONE);
                                (findViewById(R.id.successful_date)).setVisibility(View.GONE);
                            }
                        }catch(Exception e){
                            Toast.makeText(DateActivity.this, "Error en los datos!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DateActivity.this, "Error de conexión!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("change", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("change", false);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}

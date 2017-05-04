package com.utt.application.isradeleon.vendedorescorso;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private DBManager dbManager;
    private DrawerLayout _drawerLayout;
    private ActionBarDrawerToggle _actionBar;
    private NavigationView nv;
    private RequestQueue requestQueue;
    private List damnObjects;
    private LinearLayout linearContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        _drawerLayout=(DrawerLayout)findViewById(R.id.activity_menu);
        _actionBar=new ActionBarDrawerToggle(this,_drawerLayout,R.string.open,R.string.close);

        _drawerLayout.addDrawerListener(_actionBar);
        _actionBar.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestQueue= Volley.newRequestQueue(this);
        nv=(NavigationView)findViewById(R.id.nav_principal);
        damnObjects=new ArrayList();
        linearContent=(LinearLayout)findViewById(R.id.linear_content);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!item.isChecked()){
                    switch (item.getItemId()){
                        case R.id.nav_dates:
                            uncheckAll();
                            item.setChecked(true);
                            _drawerLayout.closeDrawers();
                            setDatesView();
                            break;

                        case R.id.nav_sales:
                            uncheckAll();
                            item.setChecked(true);
                            _drawerLayout.closeDrawers();
                            setSalesView();
                            break;

                        case R.id.nav_houses:
                            uncheckAll();
                            item.setChecked(true);
                            _drawerLayout.closeDrawers();
                            setHousesView();
                            break;

                        case R.id.logout:
                            alertDialog();
                            break;
                    }
                    return true;
                }
                return false;
            }
        });
        dbManager = new DBManager(this);
        Cursor cursor=dbManager.getData();
        if (cursor.moveToFirst()){
            ((TextView)nv.getHeaderView(0).findViewById(R.id.menu_txt_name)).setText(cursor.getString(cursor.getColumnIndex(DataTable.C_NAME)));
            ((TextView)nv.getHeaderView(0).findViewById(R.id.menu_txt_email)).setText(cursor.getString(cursor.getColumnIndex(DataTable.C_EMAIL)));
        }

        setDatesView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getExtras().getBoolean("change")) {
            setDatesView();
        }
    }

    private void alertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que desea salir?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _drawerLayout.closeDrawers();
                        setLoadingView();
                    }
                })
                .setNegativeButton(android.R.string.no,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setLoadingView(){
        ProgressBar pb=new ProgressBar(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pb.setLayoutParams(lp);
        linearContent.removeAllViews();
        linearContent.addView(pb);
        logout();
    }

    private void logout(){
        Cursor cr=dbManager.getData();
        if (cr.moveToFirst()){
            int idtoken=cr.getInt(cr.getColumnIndex(DataTable.C_ID_TOKEN));
            String url="http://corso.esy.es/logout_m/"+String.valueOf(idtoken);
            JsonObjectRequest getRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean success=response.getBoolean("Result");
                                if(success){
                                    dbManager.truncateTable();
                                    Intent inin=new Intent(MenuActivity.this,MainActivity.class);
                                    startActivity(inin);
                                    finish();
                                }
                            }catch(Exception e){
                                Toast.makeText(MenuActivity.this, "Error en los datos recibidos!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MenuActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(getRequest);
        }
    }

    private void setHousesView(){
        linearContent.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_layout,linearContent);
        List disponibilidad=new ArrayList();
        disponibilidad.add("Casas en venta");
        disponibilidad.add("En trámite");
        disponibilidad.add("Casas vendidas");
        ArrayAdapter newadapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,disponibilidad);
        Spinner spin=((Spinner)findViewById(R.id.spinner_items));
        spin.setAdapter(newadapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getHousesFromServer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setSalesView(){
        linearContent.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_layout,linearContent);
        List tipo=new ArrayList();
        tipo.add("Ventas en trámite");
        tipo.add("Historial de Ventas");
        ArrayAdapter newadapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,tipo);
        Spinner spin=((Spinner)findViewById(R.id.spinner_items));
        spin.setAdapter(newadapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMySalesFromServer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void setDatesView(){
        linearContent.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_layout,linearContent);
        List tipo=new ArrayList();
        tipo.add("Citas pendientes");
        tipo.add("Historial de Citas");
        ArrayAdapter newadapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,tipo);
        Spinner spin=((Spinner)findViewById(R.id.spinner_items));
        spin.setAdapter(newadapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMyDatesFromServer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void getHousesFromServer(int disponibilidad){
        LinearLayout linearItems=(LinearLayout)findViewById(R.id.linear_for_items);
        linearItems.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_content,linearItems);
        Cursor cursor=dbManager.getData();
        if (cursor.moveToFirst()){
            String url="http://corso.esy.es/casas_m"+String.valueOf(disponibilidad+1)+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID))+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID_TOKEN))+"/"
                    +cursor.getString(cursor.getColumnIndex(DataTable.C_TOKEN));
            JsonObjectRequest getRequest=new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean success=response.getBoolean("Result");
                                if (success){
                                    dbManager.updateToken(response.getString("NewToken"));
                                    int count=response.getInt("Count");
                                    if (count>0){
                                        SingleJSON.getInstance().setJsonArray(response.getJSONArray("Casas"));
                                        SingleJSON.getInstance().getJsonArray().length();
                                        fillDamnObjects(SingleJSON.getInstance().getJsonArray().length());
                                        HousesAdapter myadapter=new HousesAdapter(getApplicationContext(),damnObjects,getLayoutInflater());
                                        ListView lv=((ListView)findViewById(R.id.list_view));
                                        lv.setAdapter(myadapter);

                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent inn=new Intent(MenuActivity.this,HouseActivity.class);
                                                inn.putExtra("position",position);
                                                startActivity(inn);
                                            }
                                        });

                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }else{
                                        List objError=new ArrayList();
                                        objError.add("No hay casas que mostrar!");
                                        ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                        ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }
                                }else{
                                    List objError=new ArrayList();
                                    objError.add("Vuelva a iniciar sesión!");
                                    ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                    ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                    ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    Toast.makeText(MenuActivity.this, "Los datos no coinciden en el servidor!", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e){
                                List objError=new ArrayList();
                                objError.add("Error en los datos!");
                                ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    List objError=new ArrayList();
                    objError.add("Error de conexión!");
                    ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                    ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                    ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                }
            });
            requestQueue.add(getRequest);
        }
    }

    private void getMySalesFromServer(final int saleType){
        LinearLayout linearItems=(LinearLayout)findViewById(R.id.linear_for_items);
        linearItems.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_content,linearItems);
        Cursor cursor=dbManager.getData();
        if (cursor.moveToFirst()){
            String url="http://corso.esy.es/ventas_m"+String.valueOf(saleType+1)+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID))+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID_TOKEN))+"/"
                    +cursor.getString(cursor.getColumnIndex(DataTable.C_TOKEN));
            JsonObjectRequest getRequest=new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean success=response.getBoolean("Result");
                                if (success){
                                    dbManager.updateToken(response.getString("NewToken"));
                                    int count=response.getInt("Count");
                                    if (count>0){
                                        SingleJSON.getInstance().setJsonArray(response.getJSONArray("Ventas"));
                                        SingleJSON.getInstance().getJsonArray().length();
                                        fillDamnObjects(SingleJSON.getInstance().getJsonArray().length());
                                        MySalesAdapter myadapter=new MySalesAdapter(getApplicationContext(),damnObjects,getLayoutInflater(),saleType);
                                        ListView lv=((ListView)findViewById(R.id.list_view));
                                        lv.setAdapter(myadapter);

                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent inn=new Intent(MenuActivity.this,SaleActivity.class);
                                                inn.putExtra("position",position);
                                                startActivity(inn);
                                            }
                                        });

                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }else{
                                        List objError=new ArrayList();
                                        objError.add("No hay ventas en trámite!");
                                        ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                        ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }
                                }else{
                                    List objError=new ArrayList();
                                    objError.add("Vuelva a iniciar sesión!");
                                    ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                    ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                    ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    Toast.makeText(MenuActivity.this, "Los datos no coinciden en el servidor!", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e){
                                List objError=new ArrayList();
                                objError.add("Error en los datos!");
                                ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    List objError=new ArrayList();
                    objError.add("Error de conexión!");
                    ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                    ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                    ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                }
            });
            requestQueue.add(getRequest);
        }
    }

    private void getMyDatesFromServer(final int datesType){
        LinearLayout linearItems=(LinearLayout)findViewById(R.id.linear_for_items);
        linearItems.removeAllViews();
        getLayoutInflater().inflate(R.layout.items_content,linearItems);
        Cursor cursor=dbManager.getData();
        if (cursor.moveToFirst()){
            String url="http://corso.esy.es/citas_m"+String.valueOf(datesType+1)+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID))+"/"
                    +cursor.getInt(cursor.getColumnIndex(DataTable.C_ID_TOKEN))+"/"
                    +cursor.getString(cursor.getColumnIndex(DataTable.C_TOKEN));
            JsonObjectRequest getRequest=new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean success=response.getBoolean("Result");
                                if (success){
                                    dbManager.updateToken(response.getString("NewToken"));
                                    int count=response.getInt("Count");
                                    if (count>0){
                                        SingleJSON.getInstance().setJsonArray(response.getJSONArray("Citas"));
                                        SingleJSON.getInstance().getJsonArray().length();
                                        fillDamnObjects(SingleJSON.getInstance().getJsonArray().length());
                                        MyDatesAdapter myadapter=new MyDatesAdapter(getApplicationContext(),damnObjects,getLayoutInflater(),datesType);
                                        ListView lv=((ListView)findViewById(R.id.list_view));
                                        lv.setAdapter(myadapter);

                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent inn = new Intent(MenuActivity.this, DateActivity.class);
                                                inn.putExtra("position",position);
                                                int requestCode = 1; // Or some number you choose
                                                startActivityForResult(inn, requestCode);
                                            }
                                        });

                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }else{
                                        List objError=new ArrayList();
                                        objError.add("No hay citas pendientes!");
                                        ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                        ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                        ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    }
                                }else{
                                    List objError=new ArrayList();
                                    objError.add("Vuelva a iniciar sesión!");
                                    ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                    ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                    ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                                    Toast.makeText(MenuActivity.this, "Los datos no coinciden en el servidor!", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e){
                                List objError=new ArrayList();
                                objError.add("Error en los datos!");
                                ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                                ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                                ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            List objError=new ArrayList();
                            objError.add("Error de conexión!");
                            ErrorAdapter errorAdapter=new ErrorAdapter(getApplicationContext(),objError,getLayoutInflater());
                            ((ListView)findViewById(R.id.list_view)).setAdapter(errorAdapter);
                            ((ViewFlipper)findViewById(R.id.view_flipper)).showNext();
                        }
            });
            requestQueue.add(getRequest);
        }
    }

    private void fillDamnObjects(int Size){
        damnObjects.clear();
        for(int i=0; i<Size; i++){
            damnObjects.add(i);
        }
    }

    private void uncheckAll(){
        for(int i=0; i<nv.getMenu().size(); i++){
            nv.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_actionBar.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}

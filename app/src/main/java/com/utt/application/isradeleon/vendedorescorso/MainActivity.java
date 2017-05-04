package com.utt.application.isradeleon.vendedorescorso;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private DBManager dbManager;
    private ScrollView scrollView;
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager=new DBManager(this);
        scrollView=(ScrollView)findViewById(R.id.scroll_view);

        if (dbManager.isEmpty()){
            scrollView.removeAllViews();

            LayoutInflater inflater=LayoutInflater.from(this);
            inflater.inflate(R.layout.login_layout,scrollView);
            viewFlipper=(ViewFlipper) findViewById(R.id.view_flipper);

            final RequestQueue requestQueue= Volley.newRequestQueue(this);
            ((Button) this.findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email=((EditText)findViewById(R.id.txt_email)).getText().toString();
                    String password=((EditText)findViewById(R.id.txt_password)).getText().toString();
                    if (isValidEmail(email)){
                        if (isValidString(password)){
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            viewFlipper.showNext();
                            String emailKE=stringKindaEncryptedButNotReallyEncryptedYouKnow(email);
                            String passKE=stringKindaEncryptedButNotReallyEncryptedYouKnow(password);

                            String url="http://corso.esy.es/l_m/"+emailKE+"/"+passKE;

                            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                                    new Response.Listener<JSONObject>()
                                    {
                                        @Override
                                        public void onResponse(JSONObject res) {
                                            try{
                                                boolean success=res.getBoolean("Result");
                                                if (success){
                                                    Toast.makeText(MainActivity.this, "Bienvenido!", Toast.LENGTH_SHORT).show();
                                                    JSONObject user=res.getJSONObject("User");
                                                    JSONObject vendedor=res.getJSONObject("Vendedor");
                                                    JSONObject llave=res.getJSONObject("Llave");
                                                    dbManager.insertData(
                                                            user.getInt("id"),
                                                            vendedor.getString("nombre")+" "+vendedor.getString("ap_paterno")+" "+vendedor.getString("ap_materno"),
                                                            user.getString("email"),
                                                            llave.getString("token"),
                                                            llave.getInt("id")
                                                    );
                                                    Intent in = new Intent(MainActivity.this,MenuActivity.class);
                                                    startActivity(in);
                                                    finish();
                                                }
                                                else{
                                                    viewFlipper.showPrevious();
                                                    Toast.makeText(MainActivity.this, "Error de autenticación!", Toast.LENGTH_SHORT).show();
                                                }
                                            }catch(Exception e){
                                                viewFlipper.showPrevious();
                                                Toast.makeText(MainActivity.this, "Ha ocurrido un error con la respuesta del servidor!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            viewFlipper.showPrevious();
                                            Toast.makeText(MainActivity.this, "Error de conexión!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                            requestQueue.add(getRequest);
                        }
                        else
                            Toast.makeText(MainActivity.this, "Indique su clave de acceso!", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(MainActivity.this, "Indique un email válido!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Intent in = new Intent(MainActivity.this,MenuActivity.class);
            startActivity(in);
            finish();
        }
    }

    private String stringKindaEncryptedButNotReallyEncryptedYouKnow(String s){
        String result="";
        for(char c : s.toCharArray()){
            int ascii=(int)c;
            result+=Integer.toBinaryString(ascii)+"2";
        }
        result=result.substring(0,result.length()-1);
        return result;
    }

    private boolean isValidEmail(String target) {
        target=target.trim();
        if (target.isEmpty())
            return false;
        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidString(String target){
        target=target.trim();
        if (target.isEmpty())
            return false;
        else
            return true;
    }
}

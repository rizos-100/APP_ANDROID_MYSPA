package com.utl.rizosdb.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utl.rizosdb.login.commons.MySPACommons;

import edu.utl.dsm403.myspa.modelo.Salas;
import edu.utl.dsm403.myspa.modelo.Sucursales;

public class ActivityMain extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtPass;
    Button btnLogin;

    TextView txtREST;

    Dialog dlgConsultarREST;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
        }else{
            inicializarComponentes();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){

        super.onRequestPermissionsResult(requestCode,permissions,grantResult);

        if (grantResult != null && grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
            Log.i("info","Permiso INTERNET concedido.");
            inicializarComponentes();
        }

    }

    private void inicializarComponentes(){
        try {
            txtPass = findViewById(R.id.txtPass);
            txtUsuario = findViewById(R.id.txtUsuario);
            btnLogin = findViewById(R.id.btnLogin);
            txtREST = findViewById(R.id.txtRest);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    consultarREST();


                }
            });

            Drawable d = new ColorDrawable(Color.BLACK);
            dlgConsultarREST = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            d.setAlpha(130);
            dlgConsultarREST.getWindow().setBackgroundDrawable(d);


        }catch(Exception ex){
            Log.i("info",ex.toString());
            Toast.makeText(this,"Error"+ex.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void consultarREST() {
        try {
            //Instanciamos una nueva petición HTTP a través de Volley:
            RequestQueue rq = Volley.newRequestQueue(this);

            //La URL del servicio de divisas tomando como base el dólar:
            String url = MySPACommons.URL_SERVER+ "api/login/logueoAndroid?usuario=" + txtUsuario.getText().toString() + "&pass=" + txtPass.getText().toString();
            Log.i("info",url);
            //Generamos un nuevo objeto Response.Listener<String> para indicar que haremos cuando
            //tengamos una respuesta correcta:
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                //Aquí indicamos que haremos con la respuesta de la petición HTTP.
                @Override
                public void onResponse(String response) {
                    //Generamos un objeto JSON Genérico:
                    JsonParser jp = new JsonParser();
                    JsonObject jso = (JsonObject) jp.parse(response);

                    //Leemos la propiedad "MXN" como un valor tipo float:



                    String token = jso.get("result").getAsString();
                    String id = jso.get("id").getAsString();


                    if (!token.equals(".") && !token.equals("-")) {
                       // Log.i("info","Mensaje: "+token);
                            Intent intent = new Intent(ActivityMain.this, ActivityToken.class);
                            intent.putExtra("token",token);
                            intent.putExtra("id",id);
                            Log.i("info","id -> "+ id);
                            startActivity(intent);

                    } else {
                        txtREST.setText("Usuario o Contraseña Invalidos");

                    }



                    //Establecemos el precio del dólar en la caja de texto:
                    dlgConsultarREST.hide();
                }
            };

            //Generamos un nuevo objeto Response.ErrorListener para indicar que haremos
            //cuando ocurra un error con nuestra petición:
            Response.ErrorListener errorListener = new Response.ErrorListener() {

                public void onErrorResponse(VolleyError error) {
                    dlgConsultarREST.hide();
                    Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("info","msj: "+error.getMessage());

                }
            };

            //Generamos una nueva petición Volley:
            StringRequest sr = new StringRequest(Request.Method.GET,
                    url,
                    responseListener, errorListener);

            dlgConsultarREST.show();
            //Agregamos la petición a la cola de peticiones de Volley
            //para que se ejecute:
            rq.add(sr);

        }catch(Exception ex){
            Log.i("info","error: "+ex.toString());
            Toast.makeText(this,"Error"+ex.toString(),Toast.LENGTH_LONG).show();
        }
    }


}

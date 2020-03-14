package com.utl.rizosdb.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.utl.rizosdb.login.ActivityToken;
import com.utl.rizosdb.login.R;
import com.utl.rizosdb.login.commons.MySPACommons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utl.dsm403.myspa.modelo.Horario;
import edu.utl.dsm403.myspa.modelo.Salas;
import edu.utl.dsm403.myspa.modelo.Sucursales;

public class FragmentReservacion extends Fragment {

    Sucursales sucursal;
    Salas sala;

    TextView txtSucursal;
    TextView txtSala;
    CalendarView clbCalendar;
    Spinner spnHorario;
    ImageView imgvFoto;
    Button btnGuardarReservacion;
    Button btnCancelarReservacion;

    List<Horario> horario;
    List<String> spinnerArray;
    ArrayAdapter<String> adapterSpinner;

    String token;
    String id;

    String f = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        token = getActivity().getIntent().getStringExtra("token");
        id = getActivity().getIntent().getStringExtra("id");

        View root = inflater.inflate(R.layout.fragment_reservacion, container, false);
        ActivityToken am = (ActivityToken) getActivity();

        inicializarComponentes(root);
        setDatosPrevios(am.getReservacionSucursal(), am.getReservacionSala());

        return root;
    }

    private void inicializarComponentes(View v) {
        try{

            txtSala = v.findViewById(R.id.txtSala);
            txtSucursal = v.findViewById(R.id.txtSucursal);
            imgvFoto = v.findViewById(R.id.imgvFotoReserv);
            clbCalendar = v.findViewById(R.id.clbCalendar);
            spnHorario = v.findViewById(R.id.spnHorario);
            btnCancelarReservacion = v.findViewById(R.id.btnCancelarReservacion);
            btnGuardarReservacion = v.findViewById(R.id.btnGuardarReservacion);

            long fechaActual = clbCalendar.getDate();
            clbCalendar.setMinDate(fechaActual);

            btnGuardarReservacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "ID" + id, Toast.LENGTH_LONG).show();
                   recolectarDatos();
                }
            });

            btnCancelarReservacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        ActivityToken am = (ActivityToken) getActivity();
                        am.irFragmentSala();
                    }catch (Exception ex){
                        Toast.makeText(getActivity(),"Error de cambio -> "+ex.toString(),Toast.LENGTH_LONG);
                    }
                }
            });


            clbCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                   f = year+"-"+corregirFecha(month+1)+"-"+corregirFecha(dayOfMonth);
                    consultarHorario(f);
                }
            });

        }catch (Exception ex){
            Toast.makeText(getActivity(),"Error -> "+ex.toString(),Toast.LENGTH_LONG);
        }


    }

    public void setDatosPrevios(Sucursales sucursal, Salas sala) {

        try {
            this.sucursal = sucursal;
            this.sala = sala;

            txtSucursal.setText(sucursal.getNombre());
            txtSala.setText(sala.getNombre());


            imgvFoto.setImageDrawable(MySPACommons.fromBase64(getContext(), sala.getFoto()));
        } catch (Exception e) {
            imgvFoto.setImageDrawable(null);
            Toast.makeText(getActivity(),"Error -> "+e.toString(),Toast.LENGTH_LONG);
        }

    }

    private String corregirFecha(int dato) {
        String fecha = "";
        if (dato < 10){
            fecha = "0"+dato;
        }else{
            fecha = ""+dato;
        }

        return fecha;
    }

    private void consultarHorario(String fecha) {

        //Instanciamos una nueva petición HTTP a través de Volley:
        RequestQueue rq = Volley.newRequestQueue(getActivity());

        //La URL del servicio que trae las sucursales:

        String url = MySPACommons.URL_SERVER + "api/reservacion/getAllHorarioAndroid?fecha=" + fecha +
                "&idS=" + sala.getIdSala() +
                "&token=" + token;


        //Generamos un nuevo objetivo Response.Listener<String> para indicar que haremos y tengamos una respuesta correcta
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //Convertimos la respuesta en una Lista de sucursales;
                    Type horarioListType = new TypeToken<ArrayList<Horario>>() {
                    }.getType();

                    Gson gson = new Gson();
                    horario = gson.fromJson(response, horarioListType);


                    //Con la lista de sucursales llenamos el spinner:
                    spinnerArray = new ArrayList<>();

                    for (Horario h : horario) {
                        spinnerArray.add(h.getHoraInicio() + "-" + h.getHoraFin());
                    }

                    //Creamos el adaptador del spinner:
                    adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

                    //Le establecemos al spinner su adaptador:
                    spnHorario.setAdapter(adapterSpinner);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getActivity(), "Error -> " + ex.toString(), Toast.LENGTH_LONG);

                }

            }


        };
        //Generamos un nuveo objeto Reponse.ErrorListener para indicar que haaremos cuando ocurra un error con nuestra ptición
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error", error.getMessage());
            }
        };

        //Generamos una nueva petición  Volley:
        StringRequest sr = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Agregamos la petición a la cola de peticiones de Volley para que se ejecute
        rq.add(sr);


    }

    private void recolectarDatos() {
        final int pos = spnHorario.getSelectedItemPosition();

        if (pos < 0) {
            return;
        }

        String url = MySPACommons.URL_SERVER + "api/reservacion/insert";

        StringRequest postRequestInsert = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            Log.i("info","RESPUESTA -> "+response);
                            JsonParser jp = new JsonParser();
                            JsonObject jso = (JsonObject) jp.parse(response);

                            String idReservacion = jso.get("result").getAsString();


                            Toast.makeText(getActivity(),"Su id de reservacion es: "+idReservacion,Toast.LENGTH_LONG).show();

                            ActivityToken am = (ActivityToken) getActivity();
                            am.irFragmentSala();


                        }catch (Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getActivity(),"Exception controlada"+ex.toString(),Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(),"Exception controlada ",Toast.LENGTH_LONG).show();
                    Log.i("info","ERROR CLIENTE -> "+error.toString());
            }


        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> params = new HashMap<String,String>();
                    params.put("idCli",""+id);
                    params.put("idSala",""+sala.getIdSala());
                    params.put("fechaHI",f + " " + horario.get(pos).getHoraInicio());
                    params.put("fechaHF",f + " " + horario.get(pos).getHoraFin());
                    params.put("token", token);

                    return params;
            }
        };

        //Instanciamos una nueva petición HTTP a través de Volley:
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(postRequestInsert);

    }



}
package com.utl.rizosdb.login.ui;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utl.rizosdb.login.ActivityToken;
import com.utl.rizosdb.login.R;
import com.utl.rizosdb.login.commons.MySPACommons;
import com.utl.rizosdb.login.components.AdapterSala;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.utl.dsm403.myspa.modelo.Salas;
import edu.utl.dsm403.myspa.modelo.Sucursales;

public class FragmentSala extends Fragment {


    Spinner spnSucursales;
    RecyclerView rclSalas;

    //Objetos para llevar el control de sucursales
    List<Sucursales> sucursales;
    List<String> spinnerArray;
    ArrayAdapter<String> adapterSpinner;

    List<Salas> salas;
    AdapterSala adapterSala;

    String token;
    int id;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sala, container, false);

        token = getActivity().getIntent().getStringExtra("token");
        id = getActivity().getIntent().getIntExtra("id",0);
        inicializarComponentes(root);

        return root;
    }

    private void inicializarComponentes(View root){

        spnSucursales = root.findViewById(R.id.spnSucursales);
        rclSalas = root.findViewById(R.id.rcvSalas);
        rclSalas.setLayoutManager(new LinearLayoutManager(getActivity()));
        consultarScursales();

        spnSucursales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consultarSala();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void consultarScursales(){

            //Instanciamos una nueva petición HTTP a través de Volley:
            RequestQueue rq = Volley.newRequestQueue(getActivity());

            //La URL del servicio que trae las sucursales:

            String url = MySPACommons.URL_SERVER + "api/sucursal/getAllAndroid?token=" + token;
            Log.i("info", url);

            //Generamos un nuevo objetivo Response.Listener<String> para indicar que haremos y tengamos una respuesta correcta
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //Convertimos la respuesta en una Lista de sucursales;
                        Type sucursalesListType = new TypeToken<ArrayList<Sucursales>>() {
                        }.getType();

                        Gson gson = new Gson();
                        sucursales = gson.fromJson(response, sucursalesListType);


                        //Con la lista de sucursales llenamos el spinner:
                        spinnerArray = new ArrayList<>();

                        for (Sucursales s : sucursales) {
                            spinnerArray.add(s.getNombre());
                        }

                        //Creamos el adaptador del spinner:
                        adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,spinnerArray);

                        //Le establecemos al spinner su adaptador:
                        spnSucursales.setAdapter(adapterSpinner);
                        consultarSala();

                    }catch (Exception ex){
                        ex.printStackTrace();

                       // Toast.makeText(getActivity(),"Error -> "+ex.toString(),Toast.LENGTH_LONG);

                    }

                }
            };



        //Generamos un nuveo objeto Reponse.ErrorListener para indicar que haaremos cuando ocurra un error con nuestra ptición
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error",error.getMessage());
            }
        };

        //Generamos una nueva petición  Volley:
        StringRequest sr = new StringRequest(Request.Method.GET, url, responseListener,errorListener);

        //Agregamos la petición a la cola de peticiones de Volley para que se ejecute
        rq.add(sr);

    }

    private void consultarSala(){
        final int pos = spnSucursales.getSelectedItemPosition();

        if (pos < 0){
            return;
        }

        int idSucursal = sucursales.get(pos).getIdSucursal();

        RequestQueue rq = Volley.newRequestQueue(getActivity());
        String url = MySPACommons.URL_SERVER+"api/sala/getAllSalas?num="+idSucursal;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type salasListType = new TypeToken<ArrayList<Salas>>(){}.getType();
                Gson gson = new Gson();
                salas = gson.fromJson(response, salasListType);
                if (adapterSala == null){
                    adapterSala = new AdapterSala((ActivityToken) getActivity(), sucursales.get(pos), salas);
                }else{
                    adapterSala.setItems(sucursales.get(pos), salas);
                }

                rclSalas.setAdapter(adapterSala);
            }
        };

        //Generamos un objeto Response.ErrorListener para indicar que haremos cuando ocurra un error con nuestras peticiones:
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Log.i("error",error.getMessage());
            }
        };

        //Generamos una petición Volley:
        StringRequest sr = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Agregamos la petición a la cola de peticiones de Volley para que se ejecute
        rq.add(sr);
    }


}
package com.utl.rizosdb.login.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.utl.rizosdb.login.R;
import com.utl.rizosdb.login.commons.MySPACommons;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import edu.utl.dsm403.myspa.modelo.Sucursales;

public class FragmentSucursal extends Fragment {

    Spinner spnSucursales;
    RecyclerView rclSalas;

    //Objetos para llevar el control de sucursales
    List<Sucursales> sucursales;
    List<String> spinnerArray;
    ArrayAdapter<String> adapterSpinner;

    String token;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sucursal, container, false);
        token = getActivity().getIntent().getStringExtra("token");

        return root;
    }

    private void consultarScursales(){
        //Instanciamos una nueva petición HTTP a través de Volley:
        RequestQueue rq = Volley.newRequestQueue(getActivity());

        //La URL del servicio que trae las sucursales:
        String url = MySPACommons.URL_SERVER + "api/sucursal/getAll?token=";

        //Generamos un nuevo objetivo Response.Listener<String> para indicar que haremos y tengamos una respuesta correcta
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convertimos la respuesta en una Lista de sucursales;
                Type sucursalesListType = new TypeToken<ArrayList<Sucursales>>(){}.getType();
                Gson gson = new Gson();
                sucursales = gson.fromJson(response,sucursalesListType);

                //Con la lista de sucursales llenamos el spinner:
                spinnerArray = new ArrayList<>();

                for (Sucursales s: sucursales){
                    spinnerArray.add(s.getNombre());
                }

                //Creamos el adaptador del spinner:
                adapterSpinner = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item);

                //Le establecemos al spinner su adaptador:
                spnSucursales.setAdapter(adapterSpinner);
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
}
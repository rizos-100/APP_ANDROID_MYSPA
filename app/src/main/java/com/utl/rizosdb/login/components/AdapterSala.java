package com.utl.rizosdb.login.components;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utl.rizosdb.login.ActivityMain;
import com.utl.rizosdb.login.ActivityToken;
import com.utl.rizosdb.login.R;
import com.utl.rizosdb.login.commons.MySPACommons;

import java.util.ArrayList;
import java.util.List;

import edu.utl.dsm403.myspa.modelo.Salas;
import edu.utl.dsm403.myspa.modelo.Sucursales;

public class AdapterSala extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Salas> salas;

    Sucursales sucursal;

    ActivityToken activityMain;

    public AdapterSala(List<Salas> salas){
        setItems(salas);
    }

    public AdapterSala(ActivityToken activityMain, Sucursales sucursal, List<Salas> salas){
        super();
        this.activityMain = activityMain;
        setItems(sucursal, salas);
    }

    public void setItems(Sucursales sucursal, List<Salas> salas){
        this.sucursal = sucursal;
        this.salas = salas != null ? salas : new ArrayList<Salas>();
    }

    public void setItems(List<Salas> salas){
        this.salas = salas != null ? salas : new ArrayList<Salas>();
    }

    /**
     * Se crea un adapter sala como intermedio entre el dataset y el RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sala_par,parent,false);
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sala_impar,parent,false);

        ViewHolderSala vhs = new ViewHolderSala(v);

        if (viewType == 0){
            vhs = new ViewHolderSala(v);
        }else{
             vhs = new ViewHolderSala(v2);
        }
        this.context = parent.getContext();


        this.context = parent.getContext();
        //Log.i("info","Texto: "+viewType);
        return vhs;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       final Salas s = salas.get(position);
        ViewHolderSala vhs = (ViewHolderSala) holder;

            vhs.txtvNombreSala.setText(s.getNombre());
            vhs.txtvDescripcionSala.setText(s.getDescripcion());
            final int id = s.getIdSala();

            vhs.btnReservarSala.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(v.getContext(), "ID Sala: "+id,Toast.LENGTH_SHORT).show();
                    activityMain.irFragmentReservacion(sucursal, s);
                }
            });

            try{
                vhs.imgvFotoSala.setImageDrawable(MySPACommons.fromBase64(context, s.getFoto()));
            }catch(Exception ex){
                ex.printStackTrace();
                vhs.imgvFotoSala.setImageDrawable(null);
            }


    }

    @Override
    public int getItemCount() {
        return salas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position %2;

    }
}

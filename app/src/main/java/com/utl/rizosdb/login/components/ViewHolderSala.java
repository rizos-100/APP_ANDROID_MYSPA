package com.utl.rizosdb.login.components;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utl.rizosdb.login.R;

public class ViewHolderSala extends RecyclerView.ViewHolder {

    protected ImageView imgvFotoSala, imgvFotoSalaImpar;
    protected TextView txtvNombreSala, txtvNombreSalaImpar;
    protected TextView txtvDescripcionSala, txtvDescripcionSalaImpar;
    protected Button btnReservarSala, btnReservarSalaImpar;

    public ViewHolderSala(@NonNull View itemView) {
        super(itemView);
        imgvFotoSala = itemView.findViewById(R.id.imgvFotoSala);
        txtvNombreSala = itemView.findViewById(R.id.txtvNombreSalaImpar);
        txtvDescripcionSala = itemView.findViewById(R.id.txtvDescripcionSalaImpar);
        btnReservarSala = itemView.findViewById(R.id.btnReservarSalaImpar);


    }



}

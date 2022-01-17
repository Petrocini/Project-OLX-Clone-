package com.lucaspetrocini.projetoolx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.lights.LightState;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.adapter.AdapterMeusAnuncios;
import com.lucaspetrocini.projetoolx.helper.ConfiguracaoFirebase;
import com.lucaspetrocini.projetoolx.helper.RecyclerItemOnClickListener;
import com.lucaspetrocini.projetoolx.model.Anuncio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private FloatingActionButton fabAdicionarAnuncios;
    private RecyclerView rvMeusAnuncios;
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private List<Anuncio> aMeusAnuncios = new ArrayList<>();
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog alertDialog;
    private AlertDialog.Builder dialogConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando meus anúncios")
                .setCancelable(false)
                .build();
        alertDialog.show();

        //Metodo
        configInic();

    }

    //Inicializando componentes e configurações iniciais
    public void configInic(){

        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("meus_anuncios")
        .child(ConfiguracaoFirebase.getIdUsuario());
        recuperarAnuncios();
        rvMeusAnuncios = findViewById(R.id.recyclerMeusAnuncios);
        rvMeusAnuncios.setLayoutManager(new LinearLayoutManager(this));
        rvMeusAnuncios.setHasFixedSize(true);
        adapterMeusAnuncios = new AdapterMeusAnuncios(aMeusAnuncios, MeusAnunciosActivity.this);
        rvMeusAnuncios.setAdapter(adapterMeusAnuncios);
        rvMeusAnuncios.addOnItemTouchListener(new RecyclerItemOnClickListener(this, rvMeusAnuncios, new RecyclerItemOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

                dialogConfirm = new AlertDialog.Builder(MeusAnunciosActivity.this);
                dialogConfirm.setTitle("Atenção !!");
                dialogConfirm.setMessage("Deseja mesmo excluir estes anúncios ? ");
                dialogConfirm.setCancelable(false);
                dialogConfirm.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Anuncio anuncioSelecionado = aMeusAnuncios.get(position);
                        anuncioSelecionado.remover();

                        adapterMeusAnuncios.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialogConfirm.create();
                dialogConfirm.show();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        fabAdicionarAnuncios = findViewById(R.id.fabAdicionarAnuncios);

        fabAdicionarAnuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeusAnunciosActivity.this, CadastrarAnuncioActivity.class));
            }
        });

    }

    private void recuperarAnuncios(){
        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aMeusAnuncios.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    aMeusAnuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(aMeusAnuncios);
                adapterMeusAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapterMeusAnuncios.notifyDataSetChanged();
    }
}
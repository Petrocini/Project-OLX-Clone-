package com.lucaspetrocini.projetoolx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.activity.acesso.CadastroActivity;
import com.lucaspetrocini.projetoolx.activity.acesso.LoginActivity;
import com.lucaspetrocini.projetoolx.adapter.AdapterMeusAnuncios;
import com.lucaspetrocini.projetoolx.helper.ConfiguracaoFirebase;
import com.lucaspetrocini.projetoolx.helper.RecyclerItemOnClickListener;
import com.lucaspetrocini.projetoolx.model.Anuncio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterMeusAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private Spinner spinnerFiltro;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios");

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterMeusAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

        //Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemOnClickListener(
                this,
                recyclerAnunciosPublicos,
                new RecyclerItemOnClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncioSelecionado = listaAnuncios.get(position);
                        Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                        i.putExtra("anuncioSelecionadio" , (Serializable) anuncioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

    }

    public void filtrarPorEstado(View view){
        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        final Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, estados
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                buttonRegiao.setText(filtroEstado);
                    recuperarAnunciosPorEstado();
                    filtrandoPorEstado = true;

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    public void recuperarAnunciosPorEstado(){

        //Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categorias: dataSnapshot.getChildren() ){
                    for(DataSnapshot anuncios: categorias.getChildren() ){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add( anuncio );

                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void filtrarPorCategoria(View view){

        if( filtrandoPorEstado == true ){

            AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
            dialogEstado.setTitle("Selecione a categoria desejada");

            //Configurar spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Configura spinner de categorias
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] estados = getResources().getStringArray(R.array.categoria);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item,
                    estados
            );
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinnerCategoria.setAdapter( adapter );

            dialogEstado.setView( viewSpinner );

            dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    buttonCategoria.setText(filtroCategoria);
                    recuperarAnunciosPorCategoria();
                }
            });

            dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEstado.create();
            dialog.show();

        }else {
            Toast.makeText(this, "Escolha primeiro uma região!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void recuperarAnunciosPorCategoria(){

        //Configura nó por categoria
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado)
                .child( filtroCategoria );

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for(DataSnapshot anuncios: dataSnapshot.getChildren() ){
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add( anuncio );
                }
                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage("Recuperando anúncios")
                .setCancelable( false )
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot estados: dataSnapshot.getChildren()){
                    for (DataSnapshot categorias: estados.getChildren() ){
                        for(DataSnapshot anuncios: categorias.getChildren() ){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add( anuncio );

                        }
                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entrar_cadastrar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if( autenticacao.getCurrentUser() == null ){//usuario deslogado
            menu.setGroupVisible(R.id.grupo_deslogado,true);
        }else {//Usuario logado
            menu.setGroupVisible(R.id.grupo_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.menu_autenticacao:
                startActivity( new Intent(getApplicationContext(), CadastroActivity.class));
                break;
            case R.id.menu_sair :
                autenticacao.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                invalidateOptionsMenu();
                break;
            case R.id.menu_meusAnuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){
        buttonRegiao = findViewById(R.id.btnEstado);
        buttonCategoria = findViewById(R.id.btnCategoria);
        recyclerAnunciosPublicos = findViewById(R.id.rvAnuncios);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterAnuncios.notifyDataSetChanged();
    }
}

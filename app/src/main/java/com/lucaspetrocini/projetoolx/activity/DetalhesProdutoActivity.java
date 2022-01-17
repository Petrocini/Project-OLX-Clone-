package com.lucaspetrocini.projetoolx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private TextView txtTitutlo, txtValor, txtDesc, txtEstado;
    private Button btnVerTelefone;
    private CarouselView carouselView;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icarrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhe produto");

        inicializarComponentes();

        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionadio");

        if (anuncioSelecionado != null){
            txtTitutlo.setText(anuncioSelecionado.getTitulo());
            txtDesc.setText(anuncioSelecionado.getDescricao());
            txtEstado.setText(anuncioSelecionado.getEstado());
            txtValor.setText("R$ " + anuncioSelecionado.getValor());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }

    private void inicializarComponentes(){
        carouselView = findViewById(R.id.carouselView);
        txtTitutlo = findViewById(R.id.txtTituloProd);
        txtValor = findViewById(R.id.txtValorProd);
        txtDesc = findViewById(R.id.txtDesc);
        txtEstado = findViewById(R.id.txtEstado);
        btnVerTelefone = findViewById(R.id.btnVerTelefone);
    }

}
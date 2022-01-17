package com.lucaspetrocini.projetoolx.adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.model.Anuncio;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.MyViewHolder> {

    private List<Anuncio> aMeusAnuncios;
    private Context context;

    public AdapterMeusAnuncios(List<Anuncio> aMeusAnuncios, Context c) {

        this.aMeusAnuncios = aMeusAnuncios;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meu_anuncio, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = aMeusAnuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());

        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return aMeusAnuncios.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView titulo;
        TextView valor;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            valor = itemView.findViewById(R.id.txtValor);
            foto = itemView.findViewById(R.id.imageMeuAnuncio);
        }
    }

}

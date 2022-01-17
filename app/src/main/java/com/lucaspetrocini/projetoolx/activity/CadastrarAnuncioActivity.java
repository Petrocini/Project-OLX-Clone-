package com.lucaspetrocini.projetoolx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.request.RequestCoordinator;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.helper.ConfiguracaoFirebase;
import com.lucaspetrocini.projetoolx.helper.CoversorString;
import com.lucaspetrocini.projetoolx.helper.MaskEditUtil;
import com.lucaspetrocini.projetoolx.helper.MoneyTextWatcher;
import com.lucaspetrocini.projetoolx.helper.Permissoes;
import com.lucaspetrocini.projetoolx.model.Anuncio;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
implements View.OnClickListener {

    private ImageView imagem1, imagem2,imagem3;
    private Spinner campoEstado, campoCategoria;
    private StorageReference storage;
    private Anuncio anuncio;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private EditText editValorAnuncio, editTelefoneAnunciante, editTituloAnuncio, editDescAnuncio;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private android.app.AlertDialog alertDialog;
    private Button buttonCadastrarAnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Metodos
        configInic();
        carregarDadosSpinner();

        //Mascara para moeda e telefone
        editValorAnuncio.addTextChangedListener(new MoneyTextWatcher(editValorAnuncio));
        editTelefoneAnunciante.addTextChangedListener(MaskEditUtil.mask(editTelefoneAnunciante, MaskEditUtil.FORMAT_FONE));

    }

    //Inicializando compnentes e configurações iniciais
    public void configInic(){
        editTituloAnuncio = findViewById(R.id.editTituloAnuncio);
        editDescAnuncio = findViewById(R.id.editDescricaoAnuncio);
        editValorAnuncio = findViewById(R.id.editValorAnuncio);
        editTelefoneAnunciante = findViewById(R.id.editTelefoneAnunciante);
        buttonCadastrarAnuncio =findViewById(R.id.buttonCadastrarAnuncio);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById(R.id.imageAnuncio1);
        imagem2 = findViewById(R.id.imageAnuncio2);
        imagem3 = findViewById(R.id.imageAnuncio3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);
    }

    public void carregarDadosSpinner(){
        String[] estados = getResources().getStringArray(R.array.estados);
        String[] categoria = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, estados
        );
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categoria
        );
        adapterCategoria.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapterEstado);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidarPermissao();
            }
        }



    }

    public Anuncio configurarAnuncio(){
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = editTituloAnuncio.getText().toString();
        String valor = editValorAnuncio.getText().toString();
        String descricao = editDescAnuncio.getText().toString();
        String telefone =editTelefoneAnunciante.getText().toString();

        telefone = CoversorString.converterTelefone(telefone);
        valor = CoversorString.converterMoeda(valor);

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setDescricao(descricao);
        anuncio.setTelefone(telefone);

        return anuncio;
    }

    public void salvarAnuncio(){

        alertDialog = new SpotsDialog.Builder()
        .setContext(this)
        .setMessage("Salvando anúncio")
        .setCancelable(false)
        .build();
        alertDialog.show();

        for (int i = 0; i < listaFotosRecuperadas.size() ; i ++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }

    }

    private void salvarFotoStorage(String url, int totalFotos, int contador){
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);

        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(url));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseUrl = task.getResult();
                        String urlConvertida = firebaseUrl.toString();

                        listaURLFotos.add(urlConvertida);

                        if (totalFotos == listaURLFotos.size()){
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();

                            alertDialog.dismiss();
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload" + e.getMessage());
            }
        });
    }

    public void validarDadosAnuncio(View view){

        anuncio = configurarAnuncio();

        if (listaFotosRecuperadas.size() != 0){
            if (!anuncio.getEstado().isEmpty() && !anuncio.getEstado().equals("Selecione um estado")){
                if (!anuncio.getCategoria().isEmpty() && !anuncio.getCategoria().equals("Selecione uma categoria")){
                    if (!anuncio.getTitulo().isEmpty()){
                        if (!anuncio.getValor().isEmpty() && !anuncio.getValor().equals("000")){
                            if (!anuncio.getDescricao().isEmpty()){
                                if (!anuncio.getTelefone().isEmpty()){

                                    salvarAnuncio();

                                }else {
                                    exibirMensagemErro("Preencha o campo telefone !");
                                }
                            }else{
                                exibirMensagemErro("Preencha o campo descrição !");
                            }
                        }else{
                            exibirMensagemErro("Insira um valor valido !");
                        }
                    }else{
                        exibirMensagemErro("Preencha o campo titulo !");
                    }
                }else{
                    exibirMensagemErro("Selecione uma categoria");
                }
            }else {
                exibirMensagemErro("Selecione um estado !");
            }
        }else {
            exibirMensagemErro("Insira pelo menos uma foto !");
        }
    }

    public void exibirMensagemErro(String mensagem){
        Toast.makeText(CadastrarAnuncioActivity.this,
                mensagem,
                Toast.LENGTH_SHORT).show();
    }

    private void alertaValidarPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confimrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageAnuncio1:
                escolherImagem(1);
                break;
            case R.id.imageAnuncio2:
                escolherImagem(2);
                break;
            case R.id.imageAnuncio3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem (int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            if (requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
            }else if (requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }else if (requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);
        }
    }
}
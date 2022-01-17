package com.lucaspetrocini.projetoolx.activity.acesso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.activity.AnunciosActivity;
import com.lucaspetrocini.projetoolx.activity.MainActivity;
import com.lucaspetrocini.projetoolx.helper.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    //Objetos de interface
    private TextInputEditText editEmail, editSenha;
    private Button buttonLogin, buttonVerAnuncios;
    private TextView textCadastrar;
    private FirebaseAuth autenticacao;
    private String campoEmail, campoSenha;
    private AlertDialog alert;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Metodos
        verificarUsuarioLogado();
        configInic();
        chamarTelaCadastro();
        realizarLogin();
        verAnuncios();


    }

    //Entrar para ver anuncios
    public void verAnuncios(){
        buttonVerAnuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, AnunciosActivity.class));
            }
        });
    }

    //Abrindo alertDialog
    public void abrirAlertDialog(String titulo){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(titulo);
        dialog.setCancelable(false);
        dialog.setView(R.layout.progress_alert);

        alert = dialog.create();
        alert.show();

    }

    public void verificarUsuarioLogado(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
        }
    }

    //validar se os campos foram digitados e fazer login
    public void realizarLogin(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirAlertDialog("Carregando informaões ");

                campoEmail = editEmail.getText().toString();
                campoSenha = editSenha.getText().toString();

                if (!campoEmail.isEmpty()){
                    if (!campoSenha.isEmpty()){
                        autenticacao.signInWithEmailAndPassword(
                                campoEmail,campoSenha
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    startActivity(new Intent(LoginActivity.this, AnunciosActivity.class));
                                    alert.cancel();
                                }else {
                                    Toast.makeText(LoginActivity.this,
                                            "Erro ao fazer login, verifique o e-mail e senha !",
                                            Toast.LENGTH_SHORT).show();
                                    alert.cancel();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Por favor, preencha todos os campos !",
                                Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Por favor, preencha todos os campos !",
                            Toast.LENGTH_SHORT).show();
                    alert.cancel();
                }
            }
        });


    }

    //Inicializando componentes e configurações iniciais
    public void configInic(){
        editEmail = findViewById(R.id.editEmailLogin);
        editSenha = findViewById(R.id.editSenhaLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonVerAnuncios = findViewById(R.id.buttonVerAnuncios);
        textCadastrar = findViewById(R.id.textCadastrar);


    }

    public void chamarTelaCadastro(){
        textCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
            }
        });
    }

}
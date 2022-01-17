package com.lucaspetrocini.projetoolx.activity.acesso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.lucaspetrocini.projetoolx.R;
import com.lucaspetrocini.projetoolx.activity.MainActivity;
import com.lucaspetrocini.projetoolx.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha, editNome, editConfirmSenha;
    private Button buttonCadastrar;
    private String campoEmail, campoSenha, campoNome, campoConfirmSenha;
    private AlertDialog alert;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Metodos
        configInic();
        cadastrarUsuario();
    }

    public void abrirDialogCarregamento(String titulo){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(titulo);
        dialog.setCancelable(false);
        dialog.setView(R.layout.progress_alert);

        alert = dialog.create();
        alert.show();
    }

    //Validando se os campos foram digitados e cadastrar usuario
    public void cadastrarUsuario(){
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            abrirDialogCarregamento("Criando usuário");
                campoEmail = editEmail.getText().toString();
                campoSenha = editSenha.getText().toString();
                campoNome = editNome.getText().toString();
                campoConfirmSenha = editConfirmSenha.getText().toString();

                if (!campoNome.isEmpty()){
                    if (!campoEmail.isEmpty()){
                        if (!campoSenha.isEmpty()){
                            if (!campoConfirmSenha.isEmpty()){
                                autenticacao.createUserWithEmailAndPassword(
                                        campoEmail,campoSenha
                                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            startActivity(new Intent(CadastroActivity.this, MainActivity.class));

                                            Toast.makeText(CadastroActivity.this,
                                                    "Cadastro realizado com sucesso !",
                                                    Toast.LENGTH_SHORT).show();
                                            alert.cancel();

                                        }else{
                                            String erroExcecao = "";
                                            try {
                                                throw task.getException();
                                            }catch (FirebaseAuthWeakPasswordException e){
                                                erroExcecao = "Digite uma senha mais forte !";
                                            }catch (FirebaseAuthInvalidCredentialsException e){
                                                erroExcecao = "Por favor, digite um e-mail válido !";
                                            }catch (FirebaseAuthUserCollisionException e){
                                                erroExcecao = "Usuário já cadastrado !";
                                            }catch (Exception e){
                                                erroExcecao = "Ao cadastrar usuário: " + e.getMessage();
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(CadastroActivity.this,
                                                    erroExcecao,
                                                    Toast.LENGTH_SHORT).show();
                                            alert.cancel();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(CadastroActivity.this,
                                        "Por favor, preencha todos os campos !",
                                        Toast.LENGTH_SHORT).show();
                                alert.cancel();
                            }
                        }else{
                            Toast.makeText(CadastroActivity.this,
                                    "Por favor, preencha todos os campos !",
                                    Toast.LENGTH_SHORT).show();
                            alert.cancel();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Por favor, preencha todos os campos !",
                                Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Por favor, preencha todos os campos !",
                            Toast.LENGTH_SHORT).show();
                    alert.cancel();
                }

            }
        });
    }

    //Inicializando componentes e configurações iniciais
    public void configInic(){
        editEmail = findViewById(R.id.editEmailCad);
        editSenha = findViewById(R.id.editSenhaCad);
        editNome = findViewById(R.id.editNomeCad);
        editConfirmSenha = findViewById(R.id.editConfirmSenhaCad);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
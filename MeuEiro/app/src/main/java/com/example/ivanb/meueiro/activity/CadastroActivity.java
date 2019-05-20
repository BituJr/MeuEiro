package com.example.ivanb.meueiro.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivanb.meueiro.R;
import com.example.ivanb.meueiro.codificacao.Base64Custom;
import com.example.ivanb.meueiro.config.ConfiguracaoFirebase;
import com.example.ivanb.meueiro.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha, campoSenha2;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getSupportActionBar().hide();

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        campoSenha2 = findViewById(R.id.editSenha2);

        botaoCadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();
                String textoSenha2 = campoSenha2.getText().toString();

                if (!textoNome.isEmpty()) {

                    if (!textoEmail.isEmpty()) {

                        if (!textoSenha.isEmpty()) {

                            if (!textoSenha2.isEmpty()) {

                                usuario = new Usuario();
                                usuario.setNome(textoNome);
                                usuario.setEmail(textoEmail);
                                usuario.setSenha(textoSenha);
                                usuario.setSenha2(textoSenha2);

                                verificarSenha();

                            } else {
                                Toast.makeText(CadastroActivity.this,
                                        "Digite sua senha novamente!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(CadastroActivity.this,
                                    "Digite sua senha!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CadastroActivity.this,
                                "Digite seu email!",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Digite seu nome!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){

                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setIdUsuario( idUsuario );
                    usuario.salvar();
                    finish();

                }else {

                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esse e-mail já foi cadastrado!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário:" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void verificarSenha() {
        if (usuario.getSenha().equals(usuario.getSenha2())) {
            cadastrarUsuario();

        } else {
            Toast.makeText(CadastroActivity.this,
                    "Senhas não correspondem!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

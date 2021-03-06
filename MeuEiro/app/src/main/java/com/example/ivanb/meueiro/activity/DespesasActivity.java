package com.example.ivanb.meueiro.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivanb.meueiro.R;
import com.example.ivanb.meueiro.codificacao.Base64Custom;
import com.example.ivanb.meueiro.codificacao.DateCustom;
import com.example.ivanb.meueiro.config.ConfiguracaoFirebase;
import com.example.ivanb.meueiro.modelo.Movimentacao;
import com.example.ivanb.meueiro.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Button salvarDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        salvarDespesa = findViewById(R.id.buttonSalvar);

        campoData.setText( DateCustom.dataAtual() );
        recuperarDespesaTotal();

        salvarDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( validarCamposDespesa() ){

                    movimentacao = new Movimentacao();
                    String data = campoData.getText().toString();
                    Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

                    movimentacao.setValor( valorRecuperado );
                    movimentacao.setCategoria( campoCategoria.getText().toString() );
                    movimentacao.setDescricao( campoDescricao.getText().toString() );
                    movimentacao.setData( data );
                    movimentacao.setTipo( "D" );

                    Double despesaAtualizada = despesaTotal + valorRecuperado;
                    atualizarDespesa( despesaAtualizada );

                    movimentacao.salvar( data );
                    Toast.makeText(DespesasActivity.this,
                            "Despesa salva com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    finish();

                }
            }
        });

    }

    public Boolean validarCamposDespesa(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty() ){
                    if ( !textoDescricao.isEmpty() ){
                        return true;
                    }else {
                        Toast.makeText(DespesasActivity.this,
                                "Por favor, digite uma descrição!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Toast.makeText(DespesasActivity.this,
                            "Por favor, digite uma categoria!!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(DespesasActivity.this,
                        "Por favor, digite uma data!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(DespesasActivity.this,
                    "Por favor, digite um valor!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizarDespesa(Double despesa){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.child("despesaTotal").setValue(despesa);

    }

}

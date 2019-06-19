package com.carona;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Cadastro1Activity extends AppCompatActivity{

    private EditText edtEmail, edtSenha, edtSenhaC;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro1);

        edtEmail = findViewById(R.id.txtEmail);
        edtSenha = findViewById(R.id.txtPassword);
        edtSenhaC = findViewById(R.id.txtPasswordC);

        mAuth = FirebaseAuth.getInstance();
    }

    public void salvar(View view) {
        final String email = edtEmail.getText().toString();
        final String senha = edtSenha.getText().toString();
        final String senhac = edtSenhaC.getText().toString();

        if (senha.length() >= 8) {
            if (senha.equals(senhac)) {
                mAuth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Intent intent = new Intent(getApplicationContext(), Cadastro2Activity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Cadastro1Activity.this, "Não foi possível cadastrar", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "A senha precisa ter no mínimo 8 caracteres", Toast.LENGTH_LONG).show();
        }
    }

    public void cancelar(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}























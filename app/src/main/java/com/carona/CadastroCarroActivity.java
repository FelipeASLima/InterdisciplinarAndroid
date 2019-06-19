package com.carona;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CadastroCarroActivity extends AppCompatActivity {

    private EditText edtMarca, edtModelo, edtCor, edtPlaca;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_carro);

        edtMarca = findViewById(R.id.txtMarca);
        edtModelo = findViewById(R.id.txtModelo);
        edtCor = findViewById(R.id.txtCor);
        edtPlaca = findViewById(R.id.txtPlaca);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void salvar(View view) {

        Map<String,Object> carro = new HashMap<>();

        carro.put("Marca",edtMarca.getText().toString());
        carro.put("Modelo",edtModelo.getText().toString());
        carro.put("Cor",edtCor.getText().toString());
        carro.put("Placa",edtPlaca.getText().toString());
        carro.put("User", currentUser.getUid());


        db.collection("Cars")
                .add(carro)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CadastroCarroActivity.this, "Carro cadastrado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MyCarsActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadastroCarroActivity.this, "Não foi possível cadastrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancelar(View view) {
        Intent intent = new Intent(getApplicationContext(), MyCarsActivity.class);
        startActivity(intent);
    }

}

package com.carona;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailCarroActivity extends AppCompatActivity{

    private EditText editMarca, editModelo, editPlaca, editCor;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_carro);

        editMarca = findViewById(R.id.txtMarca);
        editModelo = findViewById(R.id.txtModelo);
        editPlaca = findViewById(R.id.txtPlaca);
        editCor = findViewById(R.id.txtCor);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        id = (String) getIntent().getExtras().get("id");

    }

    @Override
    protected void onStart(){
        super.onStart();
        recuperaCarro(id);
    }

    public void editarCarro(View view) {

        Map<String,Object> carro = new HashMap<>();

        carro.put("Marca",editMarca.getText().toString());
        carro.put("Modelo",editModelo.getText().toString());
        carro.put("Placa",editPlaca.getText().toString());
        carro.put("Cor",editCor.getText().toString());
        carro.put("User", currentUser.getUid());

        db.collection("Cars")
                .document(id)
                .set(carro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailCarroActivity.this, "Carro editado.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailCarroActivity.this,MyCarsActivity.class);
                        startActivity(intent);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailCarroActivity.this, "Não foi possível editar.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void recuperaCarro(String id){
        db.collection("Cars")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        editMarca.setText(document.get("Marca").toString());
                        editModelo.setText(document.get("Modelo").toString());
                        editPlaca.setText(document.get("Placa").toString());
                        editCor.setText(document.get("Cor").toString());
                    }
                }
            }
        });


    }

    public void deletaCarro(View view){
        db.collection("Cars")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DetailCarroActivity.this, "Carro excluido!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailCarroActivity.this,MyCarsActivity.class);
                        startActivity(intent);
                    }
                });
    }
}

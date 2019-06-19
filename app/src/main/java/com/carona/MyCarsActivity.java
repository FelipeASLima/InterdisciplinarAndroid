package com.carona;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.carona.adapter.AdapterCarro;
import com.carona.model.Carro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyCarsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private List<Carro> carros;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        carros = new ArrayList<>();

       listView = findViewById(R.id.listview);
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MyCarsActivity.this, DetailCarroActivity.class);
                intent.putExtra("id", carros.get(i).getIdBanco());
                intent.putExtra("index", i);

                startActivity(intent);
            }

        });
    }
        @Override
        protected void onStart(){
            super.onStart();
            recuperarDados();
        }

        public void recuperarDados() {

            db.collection("Cars")
                    .whereEqualTo("User", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                carros.clear();
                                listView.setAdapter(null);

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> objeto = document.getData();
                                    Carro carro = new Carro();
                                    carro.setMarca(objeto.get("Marca").toString());
                                    carro.setModelo(objeto.get("Modelo").toString());
                                    carro.setPlaca(objeto.get("Placa").toString());
                                    carro.setCor(objeto.get("Cor").toString());
                                    carro.setIdBanco(document.getId());
                                    carros.add(carro);
                                }

                                AdapterCarro adapter =
                                        new AdapterCarro(carros, MyCarsActivity.this);
                                listView.setAdapter(adapter);
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MyCarsActivity.this, "Não foi possivel carregar os carros cadastrados, tente novamente!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    public void addCar(View view) {
        Intent intent = new Intent(this,  CadastroCarroActivity.class);
        startActivity(intent);
    }

    public void voltar(View view) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

}

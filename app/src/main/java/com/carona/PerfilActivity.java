package com.carona;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.carona.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PerfilActivity extends AppCompatActivity {

    private EditText edtNome, edtMatricula, edtTelefone;
    private ImageView imageView;
    private Uri resultUri;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        edtNome = findViewById(R.id.txtNome);
        edtMatricula = findViewById(R.id.txtMatricula);
        edtTelefone = findViewById(R.id.nTelefone);
        imageView = findViewById(R.id.imageView);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        id = "";

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        recuperarDados();
    }

    public void recuperarDados() {

        db.collection("Users")
                .whereEqualTo("User", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                edtNome.setText(document.get("Nome").toString());
                                edtMatricula.setText(document.get("Matricula").toString());
                                edtTelefone.setText(document.get("Telefone").toString());
                                id = document.getId();
                                if (document.get("ImgUrl") != null) {
                                    Picasso.get()
                                            .load(document.get("ImgUrl").toString())
                                            .into(imageView);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                imageView.setImageDrawable(new BitmapDrawable(bitmap));
            }catch(IOException e){
                e.printStackTrace();
            }

            Picasso.get()
                    .load(resultUri)
                    .into(imageView);
        }
    }

    public void atualizar(View view) {
        if(resultUri != null) {
            String filename = UUID.randomUUID().toString();
            final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
            ref.putFile(resultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String urlProfile = uri.toString();

                                    Map<String, Object> user = new HashMap<>();

                                    user.put("Nome", edtNome.getText().toString());
                                    user.put("Matricula", edtMatricula.getText().toString());
                                    user.put("Telefone", edtTelefone.getText().toString());
                                    user.put("User", currentUser.getUid());
                                    user.put("ImgUrl", urlProfile);

                                    db.collection("Users")
                                            .document(id)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(PerfilActivity.this, "Cadastro atuaizado", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(PerfilActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PerfilActivity.this, "Verifique seu cadastro.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    });
        }else {
            Map<String, Object> user = new HashMap<>();

            user.put("Nome", edtNome.getText().toString());
            user.put("Matricula", edtMatricula.getText().toString());
            user.put("Telefone", edtTelefone.getText().toString());
            user.put("User", currentUser.getUid());

            db.collection("Users")
                    .document(id)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PerfilActivity.this, "Cadastro atuaizado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PerfilActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PerfilActivity.this, "Verifique seu cadastro.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void excluir(View view) {

        FirebaseUser user = mAuth.getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PerfilActivity.this, "Cadastro excluido!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


}

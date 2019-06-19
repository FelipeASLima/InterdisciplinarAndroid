package com.carona;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cadastro2Activity extends AppCompatActivity{

    private EditText edtNome, edtMatricula, edtTelefone;
    private ImageView imageView;
    private Uri resultUri;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);

        edtNome = findViewById(R.id.txtNome);
        edtMatricula = findViewById(R.id.txtMatricula);
        edtTelefone = findViewById(R.id.nTelefone);

        imageView = findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

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
        }
    }

    public void salvar(View view) {
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
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(Cadastro2Activity.this, "Cadastro realizado", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Cadastro2Activity.this, "Verifique seu cadastro.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    });

        } else{
            Map<String, Object> user = new HashMap<>();

            user.put("Nome", edtNome.getText().toString());
            user.put("Matricula", edtMatricula.getText().toString());
            user.put("Telefone", edtTelefone.getText().toString());
            user.put("User", currentUser.getUid());

            db.collection("Users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Cadastro2Activity.this, "Cadastro realizado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Cadastro2Activity.this, "Verifique seu cadastro.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    public void pular(View view) {
        Toast.makeText(Cadastro2Activity.this, "Complete seu cadastro depois.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}

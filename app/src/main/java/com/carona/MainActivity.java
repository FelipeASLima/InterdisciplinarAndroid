package com.carona;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.emailinput);
        edtPassword = findViewById(R.id.passwordinput);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            Toast.makeText(this, "Logado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    public void singnIn(View view){
        final String login = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        emptyValidation();

        if (!emptyValidation()) {

            firebaseAuth.signInWithEmailAndPassword(login, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MainActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("login do usuario", login);
                    startActivity(intent);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Não foi possivel, tente novamente!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(MainActivity.this, "Campos vazios", Toast.LENGTH_SHORT).show();
        }
    }

    public void singUP(View view){
        Intent intent = new Intent(getApplicationContext(), Cadastro1Activity.class);
        startActivity(intent);
    }

    private boolean emptyValidation() {
        return TextUtils.isEmpty(edtEmail.getText().toString()) || TextUtils.isEmpty(edtPassword.getText().toString());
    }
}

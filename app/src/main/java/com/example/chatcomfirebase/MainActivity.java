package com.example.chatcomfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText senhaEditText;
    private FirebaseAuth mAuth;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);

        loginEditText =
                findViewById(R.id.loginEditText );
        senhaEditText =
                findViewById(R.id.senhaEditText );
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener((v) ->{
            String login = loginEditText.getText().toString();
            String senha = senhaEditText.getText().toString();
            mAuth.signInWithEmailAndPassword(
                    login,
                    senha
            ).addOnSuccessListener((success) -> {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }).addOnFailureListener((exception) ->{
                exception.printStackTrace();
            });

        });
    }

    public void irParaCadastro(View view) {
        Intent intent = new Intent(this, NovoUsuario.class);
        startActivity(intent);

    }
}
package com.example.chatcomfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoUsuario extends AppCompatActivity {

    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private FirebaseAuth mAuth;
    private ImageView pictureImageView;

    private static final int REQ_CODE_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);

        loginNovoUsuarioEditText =
                findViewById(R.id. loginNovoUsuarioEditText );
        senhaNovoUsuarioEditText =
                findViewById(R.id. senhaNovoUsuarioEditText );
        mAuth = FirebaseAuth.getInstance();

        pictureImageView = findViewById(R.id.pictureImageView);
    }

    public void criarNovoUsuario(View view) {
        String login = loginNovoUsuarioEditText.getText().toString();
        String senha = senhaNovoUsuarioEditText.getText().toString();
        Task<AuthResult> task =
                mAuth.createUserWithEmailAndPassword(login, senha);
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(NovoUsuario.this, getString(android.R.string.ok),
                        Toast. LENGTH_SHORT ).show();
                finish();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NovoUsuario.this, e.getMessage(),
                        Toast. LENGTH_SHORT ).show();

                e.printStackTrace();
            }
        });
    }

    public void tirarFoto(View view) {
        if(loginNovoUsuarioEditText.getText() != null && !loginNovoUsuarioEditText.getText().toString().isEmpty()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQ_CODE_CAMERA);
            }
            else{
                Toast.makeText(this, getString(R.string.cant_take_pic), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, getString(R.string.no_email_no_pic), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_CAMERA:
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, getString(R.string.no_pic), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap picture = (Bitmap)data.getExtras().get("data");

                pictureImageView.setImageBitmap(picture);

                uploadPicture(picture);
                break;
        }
    }

    private void uploadPicture(Bitmap picture){
        StorageReference pictureStorageReference = FirebaseStorage.getInstance()
                                                                    .getReference(
                                                                                    String.format(
                                                                                            Locale.getDefault(),
                                                                                            "images/%s/profile.jpg",
                                                                                            loginNovoUsuarioEditText.getText().toString().replace("@","")));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        pictureStorageReference.putBytes(bytes);
    }

}

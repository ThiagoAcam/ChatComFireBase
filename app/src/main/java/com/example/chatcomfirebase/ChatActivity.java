package com.example.chatcomfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecycleView;
    private ChatAdapter adapter;
    private List<Mensagem> mensagemList;
    private FirebaseUser firebaseUser;
    private CollectionReference collMensagensReference;
    private Location CurrentLocation;

    private EditText mensagemEditText;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_PERMISSION_GPS = 1001;
    private ImageButton sendLocationImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendLocationImageButton = findViewById(R.id.sendLocationImageButton);
        sendLocationImageButton.setEnabled(false);

        mensagemEditText = findViewById(R.id.mensagemEditText);

        mensagensRecycleView = findViewById(R.id.mensagensRecyclerView);

        mensagemList = new ArrayList<>();
        adapter = new ChatAdapter(this, mensagemList);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        mensagensRecycleView.setAdapter(adapter);
        mensagensRecycleView.setLayoutManager(llm);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener =
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if(location == null)
                            return;

                        sendLocationImageButton.setEnabled(true);

                        CurrentLocation = location;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
    }

    private void setupFireBase(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        collMensagensReference = FirebaseFirestore.getInstance().collection("mensagens");

        collMensagensReference.addSnapshotListener((result, e) -> {
            mensagemList.clear();

            for(DocumentSnapshot doc: result.getDocuments())
                mensagemList.add(doc.toObject(Mensagem.class));

            Collections.sort(mensagemList);

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFireBase();

        if (ActivityCompat.checkSelfPermission(
                ChatActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_PERMISSION_GPS
            );

        }
        else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500,
                    0,
                    locationListener
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_GPS) {
            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            500,
                            0,
                            locationListener
                    );

                }
            } else {
                Toast.makeText(this,
                        getString(
                                R.string.no_gps_no_app
                        ),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.
                removeUpdates(locationListener);
    }

    public void enviarMensagem(View view) {
        String mensagem = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem (mensagem, new Date(), firebaseUser.getEmail(), false);
        esconderTeclado(view);

        mensagemEditText.setText("");

        collMensagensReference.add(m);
    }

    public void enviarLocalizacao(View view) {
        String mensagem = String.format(
                "geo:%f,%f",
                CurrentLocation.getLatitude(),
                CurrentLocation.getLongitude()
        );
        Mensagem m = new Mensagem (mensagem, new Date(), firebaseUser.getEmail(), true);

        collMensagensReference.add(m);
    }

    private void esconderTeclado (View v){
        InputMethodManager ims = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}

class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView dataNomeTextView;
    public TextView mensagemTextView;
    public ImageButton vaiProMapsImageButton;
    public ImageView profilePicImageView;

    ChatViewHolder(View raiz) {
        super(raiz);
        this.dataNomeTextView = raiz.findViewById(R.id.dataNomeTextView);
        this.mensagemTextView = raiz.findViewById(R.id.mensagemTextView);
        this.vaiProMapsImageButton = raiz.findViewById(R.id.vaiProMapsImageButton);
        this.profilePicImageView = raiz.findViewById(R.id.profilePicImageView);
    }
}

class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private Context context;
    private List<Mensagem> mensagemList;
    private Map<String, Bitmap> fotos;

    public ChatAdapter(Context context, List<Mensagem> mensagens){
        this.context = context;
        this.mensagemList = mensagens;
        this.fotos = new HashMap<>();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View raiz = inflater.inflate(R.layout.list_item, parent, false);

        return new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem mensagem = mensagemList.get(position);

        holder.dataNomeTextView.setText(context.getString(R.string.data_nome, DateHelper.format(mensagem.getData()), mensagem.getEmail()));

        if(mensagem.getDadosGPS())
        {
            holder.vaiProMapsImageButton.setTag(mensagem.getTexto());
            holder.vaiProMapsImageButton.setOnClickListener((v) ->{
                Uri uri =
                        Uri.parse(v.getTag().toString());
                Intent intent =
                        new Intent (
                                Intent.ACTION_VIEW,
                                uri
                        );
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);

            });


            holder.vaiProMapsImageButton.setVisibility(View.VISIBLE);
            holder.mensagemTextView.setVisibility(View.INVISIBLE);
        }
        else{
            holder.mensagemTextView.setText(mensagem.getTexto());
            holder.vaiProMapsImageButton.setVisibility(View.INVISIBLE);
            holder.mensagemTextView.setVisibility(View.VISIBLE);
        }

        if(fotos.containsKey(mensagem.getEmail()))
            holder.profilePicImageView.setImageBitmap(fotos.get(mensagem.getEmail()));
        else
        {
            StorageReference pictureStorageReference = FirebaseStorage.getInstance()
                    .getReference(
                            String.format(
                                    Locale.getDefault(),
                                    "images/%s/profile.jpg",
                                    mensagem.getEmail().replace("@", "")));

            pictureStorageReference.getDownloadUrl()
                    .addOnSuccessListener((result) -> {

                        Glide.with(context).
                                asBitmap().addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                fotos.put(mensagem.getEmail(), resource);
                                holder.profilePicImageView.setImageBitmap(resource);
                                return true;
                            }
                        }).
                                load(pictureStorageReference).
                                into(holder.profilePicImageView);

                    })
                    .addOnFailureListener((exception) -> {

                        holder.profilePicImageView.setImageResource(R.drawable.ic_person_black_50dp);

                    });
        }

    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }
}

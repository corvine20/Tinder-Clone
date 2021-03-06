package com.example.tinder.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tinder.Adapters.MessagesAdapter;
import com.example.tinder.Connection.TinderManager;
import com.example.tinder.Interfaces.GenericCallback;
import com.example.tinder.Model.Recipient;
import com.example.tinder.Model.SendMensaje;
import com.example.tinder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity implements GenericCallback {
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private RecyclerView recycle;
    private ImageView perfil;
    private TextView name;
    private ImageView altra;
    private Button button;
    private EditText tersto;
    private ArrayList<com.example.tinder.Model.Message> messages;
    private long id;
    private String picture;
    private String nameFriend;
    private MessagesAdapter messagesAdapterView;
    private MessagesThread t;
    private Button image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_personal_chat);
        id = getIntent().getExtras().getLong("USER_ID");
        nameFriend = getIntent().getExtras().getString("NAME");
        picture = getIntent().getExtras().getString("PICTURE");
        messages = new ArrayList<>();

        recycle = findViewById(R.id.recyclerView);
        recycle.setLayoutManager(new LinearLayoutManager(this));

        image = findViewById(R.id.image);

        messagesAdapterView = new MessagesAdapter(messages, this, nameFriend);
        recycle.setAdapter(messagesAdapterView);

        perfil = findViewById(R.id.personalImage);
        if (picture != null) {
            byte[] decodedString = Base64.decode(picture, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            perfil.setImageBitmap(decodedByte);
        } else {
            perfil.setImageResource(R.drawable.iscle);
        }

        name = findViewById(R.id.nameChat);
        name.setText(nameFriend);

        tersto = findViewById(R.id.introduccion);
        button = findViewById(R.id.button);
        altra = findViewById(R.id.atra);

        if (tersto.getText() != null)
            button.setOnClickListener(v -> enviarMensaje(tersto.getText().toString()));
        image.setOnClickListener(v -> openGallery());

        altra.setOnClickListener(v -> atras());

        TinderManager.getInstance().getMessages(id, 20, this);

        recycle.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recycle.smoothScrollToPosition(recycle.getAdapter().getItemCount() - 1);
            }
        });
        t = new MessagesThread(this);
        t.start();
    }

    public synchronized void actualizaMensajes() {
        TinderManager.getInstance().getMessages(id, 20, this);
    }

    private void atras() {
        t.running = false;
        finish();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                enviarImagen(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void enviarImagen(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String mensaje = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        SendMensaje men = new SendMensaje();
        men.setCreatedDate("");
        men.setPicture(mensaje);
        men.setRecipient(new Recipient(id));
        men.setPictureContentType("");
        men.setUrl("");
        men.setMessage("");
        TinderManager.getInstance().postMessages(this, men);
        tersto.setText("");
    }

    private void enviarMensaje(String mensaje) {
        SendMensaje men = new SendMensaje();
        men.setCreatedDate("");
        men.setPicture("");
        men.setRecipient(new Recipient(id));
        men.setPictureContentType("");
        men.setUrl("");
        men.setMessage(mensaje);
        TinderManager.getInstance().postMessages(this, men);
        tersto.setText("");

    }


    @Override
    public void onSuccess(Object data) {
        if (data != null) {
            com.example.tinder.Model.Message[] men = (com.example.tinder.Model.Message[]) data;
            messages = new ArrayList<>(Arrays.asList(men));
            if (messages.size() != 0) {
                messagesAdapterView.setDataset(messages);
                recycle.smoothScrollToPosition(messagesAdapterView.getItemCount() - 1);
            }
        } else {
            actualizaMensajes();
        }
    }

    @Override
    public void onFailure(Object data) {
        // Me caguen
    }
}

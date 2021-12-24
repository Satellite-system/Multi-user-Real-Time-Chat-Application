package com.assistant.android.bolchal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ConstraintLayout sendLayout;
    private ImageView addImage;
    private EditText msgEditView;
    private ImageView sendImg;

    private String mUserName;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.recycleView);
        sendLayout = findViewById(R.id.sendContainerView);
        addImage = findViewById(R.id.addImageExtra);
        msgEditView = (EditText)findViewById(R.id.typingMsgTextView);
        sendImg = findViewById(R.id.sendImageView);




        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages");


        /**
         * Adapter of List Setup
         */
        ArrayList<Message> arrayList = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(this,R.layout.chat_layout_page,arrayList);
        mListView.setAdapter(adapter);

        mUserName = "temporary";

        /**
         * When Text changes in message Box
         */
        msgEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendImg.setImageResource(R.drawable.ic_active_send);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /**
         * Message Send To the Server
         */
        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(msgEditView.getText().toString(),mUserName,null);
                mMessageDatabaseReference.push().setValue(message);

                msgEditView.setText("");
                sendImg.setImageResource(R.drawable.ic_baseline_send_24);
            }
        });

        /**
         * Child Event Listener tiggered when chat/Message new is added,updated or deleted
         * refreshing view
         */
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messageServer = snapshot.getValue(Message.class);
                adapter.add(messageServer);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messageServer = snapshot.getValue(Message.class);
                adapter.add(messageServer);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message messageServer = snapshot.getValue(Message.class);
                adapter.add(messageServer);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messageServer = snapshot.getValue(Message.class);
                adapter.add(messageServer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Error While Syncind data From Web",Toast.LENGTH_SHORT).show();
            }
        };
        mMessageDatabaseReference.addChildEventListener(mChildEventListener);

    }

    /**
     * TODO : Edit rules of real Time Firebase
     */
}
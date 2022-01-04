package com.assistant.android.bolchal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ConstraintLayout sendLayout;
    private ImageView addImage;
    private EditText msgEditView;
    private ImageView sendImg;
    private ChatAdapter mChatAdapter;
    private TextView userNameTxtView;
    private CircularImageView userProfilePic;
    private ImageView settingImg;

    private String mUserName;
    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "Unknown";
    public static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";

    private static final int RC_PHOTO_PICKER = 3;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 80;
    private static final int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mMessageDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_action_bar);
        //getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();

        mUserName = ANONYMOUS;

        mListView = findViewById(R.id.recycleView);
        sendLayout = findViewById(R.id.sendContainerView);
        addImage = findViewById(R.id.addImageExtra);
        msgEditView = (EditText)findViewById(R.id.typingMsgTextView);
        sendImg = findViewById(R.id.sendImageView);
        userNameTxtView = findViewById(R.id.userName);
        userProfilePic = findViewById(R.id.userProfileImg);
        settingImg = findViewById(R.id.action_menu_presenter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        mFirebaseAuth = FirebaseAuth.getInstance();

        /**
         * Check Whether user is loggedin Or not
         * if logged then continue otherwise send to login Page
         */
        //Send To loginPage
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user!=null) {
                    //user is signIn
                    onSignedInIntialize(user.getDisplayName());
                }
                else {
                    //User is not signned in
                    onSignedOutCleanUp();
                    Intent Send_to_logInPage = new Intent(MainActivity.this,LogIn_page.class);
                    startActivityForResult(Send_to_logInPage,RC_SIGN_IN);
                }
            }
        };


        /**
         * Adapter of List Setup
         */
        final List<Message> arrayList = new ArrayList<>();
        mChatAdapter = new ChatAdapter(this,R.layout.chat_layout_page,arrayList);
        mListView.setAdapter(mChatAdapter);


        /**
         * When Text changes in message Box
         */
        msgEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()>0) {
                    sendImg.setImageResource(R.drawable.ic_active_send);
                }else{
                    sendImg.setImageResource(R.drawable.ic_baseline_send_24);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        msgEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        /**
         * Set UserName And Profile pic at the headinning of page
         */
        userNameTxtView.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
        Glide.with(userProfilePic.getContext()).load(mFirebaseAuth.getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.profile_placeholder).into(userProfilePic);

        /**
         * Photo/Resource pickup Intent to Select a image for a message
         */
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "choose one"), RC_PHOTO_PICKER);
            }
        });

        /**
         * Message Send To the Server
         */
        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgEditView.getText().toString();
                if(!msg.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
                    Date date = new Date();

                    Message message = new Message(msg, mUserName, null, dateFormat.format(date.getTime()));
                    mMessageDatabaseReference.push().setValue(message);

                    msgEditView.setText("");
                    sendImg.setImageResource(R.drawable.ic_baseline_send_24);
                }
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
                mChatAdapter.add(messageServer);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messageServer = snapshot.getValue(Message.class);
                mChatAdapter.add(messageServer);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message messageServer = snapshot.getValue(Message.class);
                mChatAdapter.add(messageServer);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messageServer = snapshot.getValue(Message.class);
                mChatAdapter.add(messageServer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Error While Syncind data From Web",Toast.LENGTH_SHORT).show();
            }
        };
        mMessageDatabaseReference.addChildEventListener(mChildEventListener);

        //logout when settingIcon is clicked
        settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, settingImg);
                MainActivity.this.getMenuInflater().inflate(R.menu.main_page_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sign_out:
                                //signOut
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainActivity.this, LogIn_page.class));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
        
    }


    private void onSignedInIntialize(String displayName) {
        mUserName = displayName;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp(){
        mUserName = ANONYMOUS;
        mChatAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message friendlyMessage1 = dataSnapshot.getValue(Message.class);
                    mChatAdapter.add(friendlyMessage1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                /**
                 * Tiggered on Error in sending msg
                 *
                 * @param databaseError
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMessageDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener!=null){
            mMessageDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RC_SIGN_IN){
            if (resultCode== RESULT_OK){
                Toast.makeText(MainActivity.this,"Signed it!",Toast.LENGTH_SHORT).show();
            } else if(resultCode==RESULT_CANCELED){
                Toast.makeText(MainActivity.this,"Sign in Cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if(requestCode == RC_PHOTO_PICKER && resultCode==RESULT_OK){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
            Date date = new Date();

            Uri selectedImageUri = Objects.requireNonNull(data).getData();
            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

//            Toast.makeText(MainActivity.this,selectedImageUri.toString(),Toast.LENGTH_SHORT).show();

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Get a Url to the uploaded content
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                            downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    Message message = new Message(uri.toString(),mUserName,null,dateFormat.format(date.getTime()));
//                                    mMessageDatabaseReference.push().setValue(message);

                                    // Set the download URL to the message box, so that the user can send it to the database
                                    Message friendlyMessage = new Message(null, mUserName, uri.toString(),dateFormat.format(date));
                                    mMessageDatabaseReference.push().setValue(friendlyMessage);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"unsucessful Upload",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener!=null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mChatAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        attachDatabaseReadListener();
    }
}
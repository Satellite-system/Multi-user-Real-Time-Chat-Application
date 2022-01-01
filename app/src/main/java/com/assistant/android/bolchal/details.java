package com.assistant.android.bolchal;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class details extends Activity {

    private EditText userName;
    private CircularImageView profile_img;
    private FirebaseAuth firebaseAuth;
    private String user_name;

    private static final int RC_PROFILE_PICKER = 20;
    private Uri profile_pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details);

        userName = findViewById(R.id.userName_inputArea);
        Button uploadImg = findViewById(R.id.upload_button);
        Button updateProfile = findViewById(R.id.Updatebutton);
        profile_img  = findViewById(R.id.profile_image);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user_name = "anonymas";

        //Choose a profile picture from the storage
        uploadImg.setOnClickListener(view -> {
            Intent profile_picker_intent = new Intent();
            profile_picker_intent.setType("image/jpeg");
            profile_picker_intent.setAction(Intent.ACTION_GET_CONTENT);
            profile_picker_intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
            startActivityForResult(profile_picker_intent,RC_PROFILE_PICKER);
        });

        //Finish And Save Final Profile
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userName.getText().toString().isEmpty()) {
                    user_name = userName.getText().toString();
                }

                    UserProfileChangeRequest profilUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user_name)
                            .setPhotoUri(Uri.parse(String.valueOf(profile_pic_url)))
                            .build();

                    if (user != null) {
                        user.updateProfile(profilUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(details.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RC_PROFILE_PICKER && resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"Profile pic Selected",Toast.LENGTH_LONG).show();
            profile_pic_url = Objects.requireNonNull(data).getData();

            //update ImageView Profile_holder
            Glide.with(profile_img.getContext())
                    .load(profile_pic_url)
                    .centerCrop().placeholder(R.drawable.profile_placeholder).into(profile_img);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
package com.assistant.android.bolchal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class ImageViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image_viewer);

        /**
         * Toolbar/ActionBar transparent
         */
        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView image = (ImageView) findViewById(R.id.image_view_holder);
        TextView name = findViewById(R.id.img_action_name);
        TextView timeDate = findViewById(R.id.img_action_date);
        ImageView back = findViewById(R.id.img_action_back);
        ImageView menu = findViewById(R.id.img_action_menu);

        Message message = getIntent().getParcelableExtra("ImageRes");
        Glide.with(image.getContext()).load(message.getPhotoUrl()).into(image);

        name.setText(message.getName());
        timeDate.setText(message.getTime());

        back.setOnClickListener(view -> {
            finish();
        });

    }



}
package com.assistant.android.bolchal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class ChatAdapter extends ArrayAdapter<Message> {

    public ChatAdapter(Context context, int resource, List<Message> objects){
        super(context,resource,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.chat_layout_page,parent,false);
        }

        Message message = getItem(position);

        String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();


        boolean isPhotoAvailable = message.getPhotoUrl()!=null;
        boolean isTimeAvailable = message.getTime()!=null;


            TextView AuthorTxtView = convertView.findViewById(R.id.nameTextView);
            ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
            TextView messageTxtView = convertView.findViewById(R.id.messageTextView);
            TextView timeTxtView = convertView.findViewById(R.id.timeTextView);

            if (isPhotoAvailable) {
                messageTxtView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(message.getPhotoUrl())
                        .centerCrop().placeholder(R.drawable.placeholder).into(photoImageView);
            } else {
                messageTxtView.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                messageTxtView.setText(message.getText());
            }

            if (isTimeAvailable) {
                timeTxtView.setVisibility(View.VISIBLE);
                timeTxtView.setText(message.getTime());
            } else timeTxtView.setVisibility(View.GONE);


            AuthorTxtView.setText(message.getName());

        return convertView;
    }
}

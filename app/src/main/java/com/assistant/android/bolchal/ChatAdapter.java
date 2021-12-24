package com.assistant.android.bolchal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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

        TextView AuthorTxtView = (TextView)convertView.findViewById(R.id.nameTextView);
        ImageView photoImageView = (ImageView)convertView.findViewById(R.id.photoImageView);
        TextView messageTxtView = (TextView) convertView.findViewById(R.id.messageTextView);

        Message message = getItem(position);

        boolean isPhotoAvailable = message.getPhotoUrl()!=null;
        if(isPhotoAvailable){
            messageTxtView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext()).load(message.getPhotoUrl()).into(photoImageView);
        }else{
            messageTxtView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTxtView.setText(message.getText());
        }
        AuthorTxtView.setText(message.getName());

        return convertView;
    }
}

package com.assistant.android.bolchal;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

    private String text;
    private String name;
    private String photoUrl;
    private String time;

    public Message(){

    }

    public Message(String text,String name,String photoUrl){
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Message(String text,String name,String photoUrl,String time){
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.time = time;
    }

    protected Message(Parcel in) {
        text = in.readString();
        name = in.readString();
        photoUrl = in.readString();
        time = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getText(){
        return text;
    }

    public String getName(){
        return name;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(name);
        parcel.writeString(photoUrl);
        parcel.writeString(time);
    }
}

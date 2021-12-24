package com.assistant.android.bolchal;

public class Message {

    private String text;
    private String name;
    private String photoUrl;

    public Message(){

    }

    public Message(String text,String name,String photoUrl){
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getText(){
        return text;
    }

    public String getName(){
        return name;
    }

    public String getPhotoUrl(){
        return photoUrl;
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
}

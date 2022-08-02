package com.example.messenger.model;

import java.util.Date;

public class ChatDetails {
    //For text
    private String text;
    //For Image
    private String imagePath;
    //For record
    private String audioUri;
    private String audioName;
    //Public for all
    private String senderId;
    private String senderName;
    private String recipientName;
    private String date;
    private String recipient;
    public String type_text;

    public ChatDetails() {
    }

    public ChatDetails(String text, String imagePath, String audioUri, String audioName, String senderId, String senderName, String recipientName, String date, String recipient, String type_text) {
        this.text = text;
        this.imagePath = imagePath;
        this.audioUri = audioUri;
        this.audioName = audioName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.date = date;
        this.recipient = recipient;
        this.type_text = type_text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getType_text() {
        return type_text;
    }

    public void setType_text(String type_text) {
        this.type_text = type_text;
    }
}

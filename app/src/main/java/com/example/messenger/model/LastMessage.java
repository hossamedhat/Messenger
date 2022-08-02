package com.example.messenger.model;

import java.util.Calendar;
import java.util.Date;

public class LastMessage {
    String chatId;
    String text;
    String recipient;
    String name;
    String date;
    String type;
    String image_path;

    public LastMessage() {
    }

    public LastMessage(String chatId, String text, String recipient, String name, String date, String type, String image_path) {
        this.chatId = chatId;
        this.text = text;
        this.recipient = recipient;
        this.name=name;
        this.date = date;
        this.type = type;
        this.image_path = image_path;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}

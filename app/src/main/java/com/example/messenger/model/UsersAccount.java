package com.example.messenger.model;

public class UsersAccount {
    public String name;
    public String profileImage;


    public UsersAccount(){
    }

    public UsersAccount(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

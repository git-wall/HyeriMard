package com.example.client_chat.Model;

import java.io.Serializable;

public class Message_user implements Serializable {
    private String ImgProfile;
    private String Username;
    private String Message;
    private String UniqueId;
    private int Gender;
    private int Type;
    private String idMessage;

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public Message_user() {
    }

    public Message_user(String imgProfile, String uniqueId, String username, String message, int Gender, int Type, String idMessage) {
        this.ImgProfile = imgProfile;
        Username = username;
        Message = message;
        UniqueId = uniqueId;
        this.Type = Type;
        this.Gender = Gender;
        this.idMessage = idMessage;
    }

    public String getImgProfile() {
        return ImgProfile;
    }

    public void setImgProfile(String imgProfile) {
        ImgProfile = imgProfile;
    }

    public int getType() {
        return Type;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(String uniqueId) {
        UniqueId = uniqueId;
    }
}
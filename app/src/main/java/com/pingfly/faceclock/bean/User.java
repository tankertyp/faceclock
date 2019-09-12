package com.pingfly.faceclock.bean;

import android.text.TextUtils;

import org.litepal.annotation.Encrypt;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @描述 用户User实例(本地)
 */
public class User extends DataSupport implements Serializable {

    private String userId;
    private String mail;

    @Encrypt(algorithm = MD5)
    private String userPwd;

    private String name;
    private String portraitUri;
    private String displayName;
    //private String region;
    private String phone;
    private String status;
    private Long timestamp;
    private String letters;
    private String nameSpelling;
    private String displayNameSpelling;

    // 先使用一个泛型为AlarmClock的List集合来表示User中包含多个AlarmClock
    private List<AlarmClock> alarmClockList = new ArrayList<AlarmClock>();

    public User(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = name;
    }


    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayname) {
        this.displayName = displayname;
    }


    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getDisplayNameSpelling() {
        return displayNameSpelling;
    }

    public void setDisplayNameSpelling(String displayNameSpelling) {
        this.displayNameSpelling = displayNameSpelling;
    }

    public boolean isExitsDisplayName() {
        return !TextUtils.isEmpty(displayName);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            User user = (User) o;
            return (getUserId() != null && getUserId().equals(user.getUserId()));
//            return (getUserId() != null && getUserId().equals(userInfo.getUserId()))
//                    && (getName() != null && getName().equals(userInfo.getName()))
//                    && (getPortraitUri() != null && getPortraitUri().equals(userInfo.getPortraitUri()))
//                    && (phoneNumber != null && phoneNumber.equals(userInfo.getPhoneNumber()))
//                    && (displayName != null && displayName.equals(userInfo.getDisplayName()));
        } else {
            return false;
        }
    }

}

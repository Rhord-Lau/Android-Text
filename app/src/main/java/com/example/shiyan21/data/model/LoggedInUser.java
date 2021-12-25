package com.example.shiyan21.data.model;

/**
 * 数据类，用于捕获从LoginRepository检索的已登录用户的用户信息
 */
public class LoggedInUser {

    private String userId;
    private String displayName;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
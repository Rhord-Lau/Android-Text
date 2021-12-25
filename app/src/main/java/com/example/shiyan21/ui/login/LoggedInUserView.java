package com.example.shiyan21.ui.login;

/**
 * 类将经过身份验证的用户详细信息公开给UI。
 */
class LoggedInUserView {
    private String displayName;
    //... UI可以访问的其他数据字段

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}
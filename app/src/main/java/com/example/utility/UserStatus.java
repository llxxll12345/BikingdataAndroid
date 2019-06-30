package com.example.utility;

public class UserStatus {
    static boolean login;
    static String sessionId;
    static String userName = "游客";

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserStatus.userName = userName;
    }

    public static boolean getlogin() {
        return login;
    }

    public static void setLogin(boolean lg) {
        login = lg;
    }

    public static void setSessionId(String sessionId) {
        UserStatus.sessionId = sessionId;
    }

    public static String getSessionId() {
        return sessionId;
    }
}

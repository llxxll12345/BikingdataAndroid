package com.example.utility;

public class UserStatus {
    static boolean login;
    static String sessionId;

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

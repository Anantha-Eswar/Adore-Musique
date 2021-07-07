package com.anantheswar.adoremusique.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

public class UserLoginInfo {
    private static final String SESSION = "session";

    @SerializedName(SESSION)
    public LastfmUserSession mSession;


}

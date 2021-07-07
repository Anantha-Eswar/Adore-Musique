package com.anantheswar.adoremusique.lastfmapi;

import com.anantheswar.adoremusique.lastfmapi.models.ScrobbleInfo;
import com.anantheswar.adoremusique.lastfmapi.models.UserLoginInfo;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface LastFmUserRestService {

    String BASE = "/";

    @POST(BASE)
    @FormUrlEncoded
    void getUserLoginInfo(@Field("method") String method, @Field("format") String format, @Field("api_key") String apikey, @Field("api_sig") String apisig, @Field("username") String username, @Field("password") String password, Callback<UserLoginInfo> callback);

    @POST(BASE)
    @FormUrlEncoded
    void getScrobbleInfo(@Field("api_sig") String apisig, @Field("format") String format, @FieldMap Map<String, String> fields, Callback<ScrobbleInfo> callback);

}

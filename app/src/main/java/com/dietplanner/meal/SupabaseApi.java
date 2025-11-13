package com.dietplanner.meal;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;

public interface SupabaseApi {
    // GET userdetails by username:
    // e.g. GET /userdetails?select=name,salt,hash&username=eq.mikey833
    @GET("userdetails")
    Call<List<UserDetail>> getUserByUsername(@Query("select") String select, @Query("username") String usernameEq);

    // POST to RPC function
    @POST("search")
    Call<List<Plan>> searchPlansVulnerable(@Body Map<String, String> body);

    // Insert plan into 'plans' table (POST to rest endpoint)
    @POST("plans")
    Call<Void> insertPlan(@Body Plan plan);
}

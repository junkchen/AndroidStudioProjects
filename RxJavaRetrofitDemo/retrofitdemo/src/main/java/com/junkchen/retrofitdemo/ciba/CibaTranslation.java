package com.junkchen.retrofitdemo.ciba;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CibaTranslation {
    public static void main(String[] args) {
        // 4. Create Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 5. Create request instance
        GetRequest_Interface api = retrofit.create(GetRequest_Interface.class);

        // 6. 发送网络请求
        Call<Translation> call = api.getCall();
        call.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                response.body().show();
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                System.out.println("Connect failure.");
            }
        });
    }
}

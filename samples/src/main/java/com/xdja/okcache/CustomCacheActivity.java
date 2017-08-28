package com.xdja.okcache;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xdja.okcache.common.OkCache;
import com.xdja.okcache.common.strategy.custom.OnlyCacheStrategy;
import com.xdja.okcache.common.strategy.custom.OnlyNetworkStrategy;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CustomCacheActivity extends AppCompatActivity {


    private TextView tvMessage;

    private String bodyContent = "{'name':'ldy'}";
    private String url = "http://www.test.com/";
    RequestBody requestBody = RequestBody.create(MediaType.parse("text"), "{'method':'testPost'}");
    private Request request = new Request.Builder().url(url).method("POST", requestBody).build();
    private Interceptor.Chain chain = new Interceptor.Chain() {
        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response proceed(Request request) throws IOException {

            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .body(ResponseBody.create(MediaType.parse("text"), bodyContent))
                    .message("ok")
//                        .sentRequestAtMillis(-1L)
//                        .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }

        @Nullable
        @Override
        public Connection connection() {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_cache);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        OkCache.init(this, null);

        ((EditText) findViewById(R.id.edit_input)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bodyContent = s.toString();
            }
        });
    }

    public void netRequest(View view) throws IOException {
        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        Response netResponse = onlyNetworkStrategy.request(chain);

        tvMessage.setText("netRequest:" + netResponse.body().string());
    }

    public void cacheRequest(View view) throws IOException {
        Response response = new OnlyCacheStrategy().request(chain);
        tvMessage.setText("cacheRequest:" + response.body().string());
    }
}

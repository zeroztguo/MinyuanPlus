package com.csmy.minyuanplus.education;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.kymjs.rxvolley.client.RequestConfig;
import com.kymjs.rxvolley.http.HttpHeaderParser;
import com.kymjs.rxvolley.http.NetworkResponse;
import com.kymjs.rxvolley.http.Request;
import com.kymjs.rxvolley.http.Response;
import com.kymjs.rxvolley.toolbox.HttpParamsEntry;
import com.kymjs.rxvolley.toolbox.Loger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zero on 16/8/3.
 */
public class MyRequest extends Request<byte[]> {
    private final HttpParams mParams;

    public MyRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, callback);
        if (params == null) {
            params = new HttpParams();
        }
        this.mParams = params;
    }

    @Override
    public String getCacheKey() {
        if (getMethod() == RxVolley.Method.POST) {
            return getUrl() + mParams.getUrlParams();
        } else {
            return getUrl();
        }
    }

    @Override
    public String getBodyContentType() {
        if (mParams.getContentType() != null) {
            return mParams.getContentType();
        } else {
            return super.getBodyContentType();
        }
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mParams.writeTo(bos);
        } catch (IOException e) {
            Loger.debug("FormRequest#getBody()--->IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers,
                HttpHeaderParser.parseCacheHeaders(getUseServerControl(), getCacheTime(),
                        response));
    }

    @Override
    protected void deliverResponse(ArrayList<HttpParamsEntry> headers, final byte[] response) {
        if (mCallback != null) {
            HashMap<String, String> map = new HashMap<>(headers.size());
            for (HttpParamsEntry entry : headers) {
                map.put(entry.k, entry.v);
            }
            mCallback.onSuccess(map, response);
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }
}

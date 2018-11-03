package no.aegisdynamics.habitat.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class InputStreamVolleyRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private final Map<String, String> mParams;

    protected InputStreamVolleyRequest(int method, String mUrl, Response.Listener<byte[]> listener,
                                       Response.ErrorListener errorListener, Map<String, String> params) {

        super(method, mUrl, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
        mParams=params;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }


    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        //Pass the response data here
        return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}

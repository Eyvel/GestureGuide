package com.example.guestureguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private static final String BOUNDARY = "----WebKitFormBoundary" + System.currentTimeMillis();
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";

    private final File file;
    private final String studentId;
    private final Response.Listener<String> listener;
    private final Response.ErrorListener errorListener;

    public MultipartRequest(String url, File file, String studentId,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.file = file;
        this.studentId = studentId;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Add student_id parameter
            bos.write((TWO_HYPHENS + BOUNDARY + LINE_END).getBytes());
            bos.write(("Content-Disposition: form-data; name=\"student_id\"" + LINE_END).getBytes());
            bos.write((LINE_END + studentId + LINE_END).getBytes());

            // Add file data
            bos.write((TWO_HYPHENS + BOUNDARY + LINE_END).getBytes());
            bos.write(("Content-Disposition: form-data; name=\"profile_pic\"; filename=\"" + file.getName() + "\"" + LINE_END).getBytes());
            bos.write(("Content-Type: " + "image/jpeg" + LINE_END).getBytes()); // Change MIME type if necessary
            bos.write(LINE_END.getBytes());

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            bos.write(LINE_END.getBytes());

            // Add closing boundary
            bos.write((TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END).getBytes());

        } catch (IOException e) {
            throw new AuthFailureError("Error creating request body: " + e.getMessage());
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }
}


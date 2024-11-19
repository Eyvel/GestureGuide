package com.example.guestureguide;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final File file;
    private final Response.Listener<String> listener;
    private final Response.ErrorListener errorListener;
    private SharedPreferences sharedPreferences;

    // Constructor to initialize the request
    public MultipartRequest(Context context, String url, File file, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.file = file;
        this.listener = listener;
        this.errorListener = errorListener;
        this.sharedPreferences = context.getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=*****");
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        // Add additional parameters if needed (e.g., student_id)
        Map<String, String> params = new HashMap<>();
        String userId = sharedPreferences.getString("user_id", ""); // Retrieve the user_id
        params.put("student_id", userId); // Example of adding a student ID parameter
        return params;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        // Prepare the body content for file upload in multipart/form-data format
        String boundary = "*****";
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        StringBuilder body = new StringBuilder();

        // Add file content to the request body
        body.append(twoHyphens).append(boundary).append(lineEnd);
        body.append("Content-Disposition: form-data; name=\"profile_pic\"; filename=\"")
                .append(file.getName()).append("\"").append(lineEnd);
        body.append("Content-Type: ").append("image/jpeg").append(lineEnd);  // Modify based on the file type
        body.append(lineEnd);

        // Add the file data (content)
        byte[] fileBytes = getFileBytes(file);
        body.append(new String(fileBytes));

        // Closing boundary
        body.append(lineEnd).append(twoHyphens).append(boundary).append(twoHyphens).append(lineEnd);

        // Combine body and file content
        byte[] finalBody = new byte[body.length() + fileBytes.length];

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(body.toString().getBytes());
            byteArrayOutputStream.write(fileBytes);
            finalBody = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return finalBody;
    }

    private byte[] getFileBytes(File file) throws AuthFailureError {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
        } catch (IOException e) {
            throw new AuthFailureError("File reading error: " + e.getMessage());
        }
        return byteArrayOutputStream.toByteArray();
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


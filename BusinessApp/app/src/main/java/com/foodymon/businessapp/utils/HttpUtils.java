package com.foodymon.businessapp.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foodymon.businessapp.datastructure.ServerException;
import com.foodymon.businessapp.main.BusinessApplication;
import com.google.gson.Gson;

/**
 * Created by alexdai
 */
public class HttpUtils {
    public final static String BASE_URL = "http://52.73.213.106";


    public static <T> T get(String entryPoint, Map<String, String> params, Map<String, String> properties, Class<T> clazz, BusinessApplication app) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            String paramString = buildParams(params);
            URL url = new URL(getAbsoluteUrl(entryPoint, "?" + paramString));
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            if (!Utils.isEmpty(properties)) {
                for (String key : properties.keySet()) {
                    urlConnection.setRequestProperty(key, properties.get(key));
                }
            }

            if (Utils.isEmpty(properties) || !properties.containsKey("Authorization")) {
                urlConnection.setRequestProperty("Authorization", app.getToken());
            }

            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            /* 200 represents HTTP OK */
            if (statusCode == 200) {
                in = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new InputStreamReader(in);
                if (clazz != null) {
                    Gson gson = new Gson();
                    T output = gson.fromJson(reader, clazz);
                    return output;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static <T> T post(String entryPoint, Map<String, String> params, Map<String, String> properties,
                             Map<String, List<String>> responseHeader,
                             byte[] body, Class<T> clazz,
                             BusinessApplication app) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            String paramString = "";
            if (!Utils.isEmpty(params)) {
                paramString = "?" + buildParams(params);
            }
            URL url = new URL(getAbsoluteUrl(entryPoint, paramString));
            urlConnection = (HttpURLConnection) url.openConnection();
            /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setRequestMethod("POST");

            if (!Utils.isEmpty(properties)) {
                for (String key : properties.keySet()) {
                    urlConnection.setRequestProperty(key, properties.get(key));
                }
            }

            if (Utils.isEmpty(properties) || !properties.containsKey("Authorization")) {
                urlConnection.setRequestProperty("Authorization", app.getToken());
            }

            urlConnection.setRequestProperty("Content-Length", Integer.toString(body.length));

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.write(body);

            out.flush();
            out.close();

            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            if (!Utils.isEmpty(headers) && responseHeader != null) {
                responseHeader.putAll(headers);
            }


            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new InputStreamReader(inputStream);
                if (clazz == Boolean.class) {
                    return (T) Boolean.TRUE;
                } else if (clazz != null) {
                    Gson gson = new Gson();
                    T output = gson.fromJson(reader, clazz);
                    return output;
                }
            } else{
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new InputStreamReader(inputStream);
                Gson gson = new Gson();
                ServerException error = gson.fromJson(reader, ServerException.class);
                System.out.println("Error");
            }

            if (clazz == Boolean.class) {
                return (T) Boolean.FALSE;
            }
            return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    public static void getByUrl() {

    }

    public static void postByUrl() {

    }

    private static String getAbsoluteUrl(String relativeUrl, String params) {
        return BASE_URL + relativeUrl + params;
    }

    private static String buildParams(Map<String, String> params) {
        if (Utils.isEmpty(params)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (String key : params.keySet()) {
            if(!TextUtils.isEmpty(params.get(key))) {
                builder.append(prefix + key + "=" + params.get(key));
                prefix = "&";
            }
        }
        return builder.toString();
    }
}

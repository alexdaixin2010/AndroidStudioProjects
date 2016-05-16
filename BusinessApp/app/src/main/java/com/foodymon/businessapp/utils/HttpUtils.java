package com.foodymon.businessapp.utils;

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

import com.google.gson.Gson;

/**
 * Created by alexdai
 */
public class HttpUtils {
    private final static String BASE_URL = "http://52.73.213.106";


    public static <T> T get(String entryPoint, String[] params, String[] properties, Class<T> clazz) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            String paramString = buildParams(params);
            URL url = new URL(getAbsoluteUrl(entryPoint, "?"+paramString));
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            /* 200 represents HTTP OK */
            if (statusCode == 200) {
                in = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new InputStreamReader(in);
                if(clazz != null) {
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

    public static <T> T post(String entryPoint, String[] params, String[] properties,  Class<T> clazz) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            String paramString = "";
            if(!Utils.isNullOrEmpty(params)) {
                paramString = "?" + buildParams(params);
            }
            URL url = new URL(getAbsoluteUrl(entryPoint, paramString));
            urlConnection = (HttpURLConnection) url.openConnection();
            /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setRequestMethod("POST");

            if(!Utils.isNullOrEmpty(properties)) {
                for(int i = 0 ; i < properties.length; i +=2) {
                    urlConnection.setRequestProperty(properties[i], properties[i+1]);
                }
            }

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());

            out.flush();
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new InputStreamReader(inputStream);
                if(clazz == Boolean.class) {
                    return (T)Boolean.TRUE;
                } else if(clazz != null) {
                    Gson gson = new Gson();
                    T output = gson.fromJson(reader, clazz);
                    return output;
                }
            }
            return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
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

    private static String buildParams(String[] params) {
        if(Utils.isNullOrEmpty(params)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for(int i = 0 ; i < params.length; i +=2) {
            builder.append(prefix + params[i] + "=" + params[i+1]);
            prefix = "&";
        }
        return builder.toString();
    }
}

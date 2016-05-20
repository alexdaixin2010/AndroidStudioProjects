package com.foodymon.businessapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by alexdai on 5/19/16.
 */
public class ImageLoader {

    public static void loadImage(final ImageView view, final String imageId) {
        final String url = HttpUtils.BASE_URL+"/userattachment?type=image&id="+imageId;
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String[] params) {
                Bitmap image = null;
                try {
                    InputStream in = new java.net.URL(params[0]).openStream();
                    image = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return image;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                view.setImageBitmap(result);
            }
        }.execute(url);
    }
}

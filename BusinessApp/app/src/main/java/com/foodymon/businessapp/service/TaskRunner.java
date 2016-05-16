package com.foodymon.businessapp.service;

import android.os.AsyncTask;

import java.util.concurrent.Callable;

/**
 * Created by alexdai on 4/8/16.
 */
public class TaskRunner<T, V> {
    private Task<T, V> mTask;

    public TaskRunner(Task task) {
        mTask = task;
    }

    public void execute(T[] params) {
        AsyncTask<T, Object, V> asyncTask = new AsyncTask<T, Object, V>() {
            @Override
            protected void onPreExecute() {
                mTask.onPreExecute();
            }

            @Override
            protected V doInBackground(T[] params) {
                return mTask.doInBackground(params);
            }

            @Override
            protected void onPostExecute(V v) {
                mTask.onPostExecute(v);
            }
        };
        asyncTask.execute(params);
    }

}

package com.tachyon.bindaas.SimpleClasses;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.ProxyCacheException;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.net.Socket;

public class Bindaas extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
        try {
            Fresco.initialize(this);
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        Bindaas app = (Bindaas) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
    }

}

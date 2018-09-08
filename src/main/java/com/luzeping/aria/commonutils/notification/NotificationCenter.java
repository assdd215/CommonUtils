package com.luzeping.aria.commonutils.notification;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

public class NotificationCenter {
    private SparseArray<WeakHashMap<NotificationCenterDelegate,Object>> observers = new SparseArray<>();
    private Object obj = new Object();
    private static volatile Handler handler;
    private static NotificationCenter INSTANCE;

    private NotificationCenter() {

    }

    public static void init(Context context) {
        handler = new Handler(context.getApplicationContext().getMainLooper());
    }

    public static NotificationCenter getInstance() {
        if (INSTANCE == null) {
            synchronized (NotificationCenter.class) {
                if (INSTANCE == null)
                    INSTANCE = new NotificationCenter();
            }
        }
        return INSTANCE;
    }

    public void addObserver(Object observer,int id) {
        synchronized (this) {
            WeakHashMap<NotificationCenterDelegate,Object> map = observers.get(id);
            if (map == null) {
                observers.put(id,map = new WeakHashMap<>());
            }
            if (!map.containsKey(observer)) {
                map.put((NotificationCenterDelegate) observer,obj);
            }
        }
    }

    public void removeObserver(Object observer,int id) {
        synchronized (this) {
            WeakHashMap<NotificationCenterDelegate,Object> objects = observers.get(id);
            if (objects != null) {
                objects.remove(observer);
                if (objects.size() == 0) {
                    this.observers.remove(id);
                }
            }
        }
    }

    public void postNotificationName(final int id,final Object ... args) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                WeakHashMap<NotificationCenterDelegate,Object> objects = observers.get(id);
                if (objects != null && objects.size() > 0) {
                    HashSet<NotificationCenterDelegate> listeners = new HashSet<>(objects.keySet());
                    Iterator iterator = listeners.iterator();
                    while (iterator.hasNext()) {
                        NotificationCenterDelegate listener = (NotificationCenterDelegate) iterator.next();
                        if (listener != null)
                            listener.didReceivedNotification(id,args);
                    }
                }
            }
        });
    }
}

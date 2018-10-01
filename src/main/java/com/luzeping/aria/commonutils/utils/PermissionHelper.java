package com.luzeping.aria.commonutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 用于封装权限申请的操作
 */
public class PermissionHelper {

    private static ArrayList<PermissionRequest> requests = new ArrayList<>();

    public static void askForPermission(Activity activity,String[] permissions, @NonNull PermissionCallback permissionCallback) {
        if (hasPermission(activity,permissions)) {
            permissionCallback.permissionGranted();
            return;
        }
        PermissionRequest request = new PermissionRequest(new ArrayList(Arrays.asList(permissions)),permissionCallback);
        requests.add(request);
        ActivityCompat.requestPermissions(activity,permissions,request.getRequestCode());
    }

    public static boolean hasPermission(Context context, String[] permissions) {
        int size = permissions.length;
        for (int  i = 0;i< permissions.length; i++) {
            String permission = permissions[i];
            if (ContextCompat.checkSelfPermission(context,permission) != 0)
                return false;
        }
        return true;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        for (int i = 0;i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest result = new PermissionRequest(requestCode);
        if (requests.contains(result)) {
            PermissionRequest request = requests.get(requests.indexOf(result));
            if (verifyPermissions(grantResults)) {
                request.getPermissionCallback().permissionGranted();
            }else
                request.getPermissionCallback().permissionRefused();
            requests.remove(request);
        }
    }
}

class PermissionRequest {
    private static Random random;
    private ArrayList<String> permissions;
    private int requestCode;
    private PermissionCallback permissionCallback;

    public PermissionRequest(int requestCode) {
        this.requestCode = requestCode;
    }

    public PermissionRequest(ArrayList<String> permissions, PermissionCallback permissionCallback) {
        this.permissions = permissions;
        this.permissionCallback = permissionCallback;
        if (random == null) {
            random = new Random();
        }

        this.requestCode = random.nextInt(255);
    }

    public ArrayList<String> getPermissions() {
        return this.permissions;
    }

    public int getRequestCode() {
        return this.requestCode;
    }

    public PermissionCallback getPermissionCallback() {
        return this.permissionCallback;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof PermissionRequest) {
            return ((PermissionRequest)object).requestCode == this.requestCode;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.requestCode;
    }
}


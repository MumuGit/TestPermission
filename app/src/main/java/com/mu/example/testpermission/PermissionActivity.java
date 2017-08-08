package com.mu.example.testpermission;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by mu on 2017/8/3.
 */

public class PermissionActivity extends AppCompatActivity {
    private static PermissionListener mPermissionListener;
    static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";

    public static void setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String[] permissions = intent.getStringArrayExtra(KEY_INPUT_PERMISSIONS);
        if (mPermissionListener == null || permissions == null)
            finish();
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(permissions, 1);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionListener != null)
            mPermissionListener.onRequestPermissionsResult(permissions, grantResults);
        mPermissionListener = null;
        finish();
    }

//    interface PermissionListener {
//        void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults);
//    }
}

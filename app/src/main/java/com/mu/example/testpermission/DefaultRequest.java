
package com.mu.example.testpermission;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;


import com.mu.example.testpermission.target.Target;
import com.yanzhenjie.permission.PermissionNo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class DefaultRequest implements
        RationaleRequest, Rationale, PermissionListener {
    private String[] mPermissions;
    private int mRequestCode;
    private Object mCallback;
    private RationaleListener mRationaleListener;
    private String[] mDeniedPermissions;
    private Target target;

    DefaultRequest(Target target) {
        if (target == null)
            throw new IllegalArgumentException("The target can not be null.");
        this.target = target;
    }

    @NonNull
    @Override
    public RationaleRequest permission(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    @NonNull
    @Override
    public RationaleRequest requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    @Override
    public RationaleRequest callback(Object callback) {
        this.mCallback = callback;
        return this;
    }

    @Override
    public void start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callbackSucceed();
        } else {
            mDeniedPermissions = getDeniedPermissions(target.getContext(), mPermissions);
            if (mDeniedPermissions.length > 0) {
                // Remind users of the purpose of mPermissions.
                boolean showRationale = target.shouldShowRationalePermissions(mDeniedPermissions);
                if (showRationale && mRationaleListener != null)
                    mRationaleListener.showRequestPermissionRationale(mRequestCode, this);
                else
                    resume();
            } else { // All permission granted.
                callbackSucceed();
            }
        }
    }

    private static String[] getDeniedPermissions(Context context, @NonNull String... permissions) {
        List<String> deniedList = new ArrayList<>(1);
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                deniedList.add(permission);
        return deniedList.toArray(new String[deniedList.size()]);
    }

    private void callbackSucceed() {
        if (mCallback != null) {
            if (mCallback instanceof PermissionListener)
                ((PermissionListener) mCallback).onSucceed(mRequestCode, Arrays.asList(mPermissions));

        }
    }
    private void callbackFailed(List<String> deniedList) {
        if (mCallback != null) {
            if (mCallback instanceof PermissionListener)
                ((PermissionListener) mCallback).onFailed(mRequestCode, deniedList);

        }
    }
    @NonNull
    @Override
    public RationaleRequest rationale(RationaleListener listener) {
        this.mRationaleListener = listener;
        return this;
    }

    @Override
    public void resume() {
        PermissionActivity.setPermissionListener(this);
        Intent intent = new Intent(target.getContext(),PermissionActivity.class);
        intent.putExtra(PermissionActivity.KEY_INPUT_PERMISSIONS, mDeniedPermissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.startActivity(intent);
    }

    @Override
    public void cancel() {
        int[] results = new int[mPermissions.length];
        for (int i = 0; i < mPermissions.length; i++)
            results[i] = ContextCompat.checkSelfPermission(target.getContext(), mPermissions[i]);
        onRequestPermissionsResult(mPermissions, results);
    }

    @Override
    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

    }

    @Override
    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

    }

    @Override
    public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++)
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                deniedList.add(permissions[i]);

        if (deniedList.isEmpty())
            callbackSucceed();
        else
            callbackFailed(deniedList);
    }
}
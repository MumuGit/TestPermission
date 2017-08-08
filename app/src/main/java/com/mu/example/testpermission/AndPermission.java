package com.mu.example.testpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.mu.example.testpermission.target.AppActivityTarget;
import com.mu.example.testpermission.target.ContextTarget;
import com.mu.example.testpermission.target.Target;



import java.util.Arrays;
import java.util.List;

/**
 * Created by mu on 2017/8/2.
 */

public class AndPermission {
    public static
    @NonNull
    RationaleRequest with(@NonNull AppCompatActivity activity) {
        return new DefaultRequest(new AppActivityTarget(activity));
    }

    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }
    public static boolean hasPermission(@NonNull Context context, @NonNull List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) continue;
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_IGNORED) return false;
            result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static
    @NonNull
    RationaleDialog rationaleDialog(@NonNull Context context, Rationale rationale) {
        return new RationaleDialog(context, rationale);
    }
    public static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, @NonNull List<String> deniedPermissions) {
        Target target = new AppActivityTarget(activity);
        return !target.shouldShowRationalePermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]));
    }
    public static
    @NonNull
    SettingDialog defaultSettingDialog(@NonNull Activity activity, int requestCode) {
        return new SettingDialog(activity, new SettingExecutor(new com.yanzhenjie.permission.target.AppActivityTarget(activity), requestCode));
    }
    public static
    @NonNull
    RationaleRequest with(@NonNull Activity activity) {
        return new DefaultRequest(new AppActivityTarget(activity));
    }
    /**
     * Anywhere..
     *
     * @param context {@link Context}.
     * @return {@link com.yanzhenjie.permission.Request}.
     */
    public static
    @NonNull
    Request with(@NonNull Context context) {
        return new DefaultRequest(new ContextTarget(context));
    }

    private AndPermission() {
    }
}

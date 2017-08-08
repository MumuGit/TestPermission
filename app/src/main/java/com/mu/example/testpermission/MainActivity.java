package com.mu.example.testpermission;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;



import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_PERMISSION_SD = 100;
    private static final int REQUEST_CODE_SETTING = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_request_single).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_single:
                AndPermission.hasPermission(this,Manifest.permission.CAMERA);
                AndPermission.with(this).permission(Manifest.permission.CAMERA).
                        requestCode(REQUEST_CODE_PERMISSION_SD).
                        callback(permissionListener).
                        rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                AndPermission.rationaleDialog(MainActivity.this, rationale).
                                        show();
                            }
                        }).start();
                break;
        }
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SD: {
                    Toast.makeText(MainActivity.this, "获取日历权限成功", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
            case REQUEST_CODE_PERMISSION_SD: {
                Toast.makeText(MainActivity.this, "获取日历权限失败", Toast.LENGTH_SHORT).show();
                break;
            }
        }

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。

            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, 100).show();

            }

        }

        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {

        }
    };
}

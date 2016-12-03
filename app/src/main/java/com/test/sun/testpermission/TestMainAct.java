package com.test.sun.testpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.test.sun.testpermission.utils.PermissionUtil;

import java.util.HashSet;

/**
 * Created by ZS27 on 2016/12/3.
 */

public class TestMainAct extends AppCompatActivity {


    public static final int REQUESTCODE = 0x100;
    private PermissionUtil.PermissionObj.RequestObject requestObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*需要申请的权限集合*/
        HashSet<String> set = new HashSet<>();
        set.add(Manifest.permission.CAMERA);
        set.add(Manifest.permission.READ_CONTACTS);
        set.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        /**
         * 创建辅助类
         *  设置dialog内容，设置回调接口
         * */
        requestObject = PermissionUtil.with(this)
                .checkPermission(set)
                .helpContent("提示标题", "提示内容")
                .addCallBack(new PermissionUtil.PermissionCallback() {
                    @Override
                    public void PermissionResult(boolean allGranted) {
                        /*是否已经获取到所有权限*/
                        if (!allGranted) {
                            /*dialog点击取消，结束act*/
                            TestMainAct.this.finish();
                        } else {
                            /*权限成功获取，开始操作*/
                        }
                    }
                });
    }

    /**
     * 用于从 setting返回时，重新检查权限
     */
    @Override
    protected void onStart() {
        super.onStart();
        requestObject.requsetPermission(REQUESTCODE);
    }

    /**
     * 用于从act开启的权限检查，不能写在onResume与Dialog冲突
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        requestObject.requsetPermission(REQUESTCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("info", ":" + permissions.length + " " + grantResults.length);
        /*处理结果*/
        requestObject.onResultOperation(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

package com.test.sun.testpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.test.sun.testpermission.utils.PermissionUtil;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ZS27 on 2016/12/3.
 */

public class TestMainAct extends AppCompatActivity {

    /*定义请求常量 工具类*/
    public static final int REQUESTCODE = 0x100;
    private PermissionUtil.PermissionObj.RequestObject requestObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*需要申请的权限集合*/

        HashMap<String, String> permissionMap = new HashMap<>();
        permissionMap.put(Manifest.permission.CAMERA, "相机");
        permissionMap.put(Manifest.permission.READ_CONTACTS, "通讯录");
        permissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "存储空间");


        /**
         * 创建辅助类
         *  设置dialog内容，设置回调接口
         * */
        try {
            requestObject = PermissionUtil.with(this)
                    .checkPermission(permissionMap)
                    .setHelpContent("帮助", "为了正常显示内容，请点击“去开启”在应用设置中打开“权限”，开启以下权限：\n")
                    .addCallBack(new PermissionUtil.PermissionCallback() {
                        @Override
                        public void failToGetPermission() {
                            /*需要的权限未全部获取，进行退出操作*/
                            TestMainAct.this.finish();
                        }

                        @Override
                        public void allNeededPermissionGranted() {
                            /*需要的权限全部获取，进行相关，初始操作需要从resume开始*/
                        }
                    });
        } catch (Exception e) {

        }
    }

    /**
     * 用于从 setting返回时，重新检查权限
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        requestObject.requsetPermission(REQUESTCODE);
    }

    /**
     * 用于从act开启的权限检查，不能写在onResume与Dialog冲突
     */
    @Override
    protected void onStart() {
        super.onStart();
        requestObject.requsetPermission(REQUESTCODE);
    }

    /**
     * 将权限处理结果交给权限工具类进行处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        requestObject.onResultOperation(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

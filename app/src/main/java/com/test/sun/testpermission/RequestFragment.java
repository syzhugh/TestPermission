package com.test.sun.testpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.sun.testpermission.R;
import com.test.sun.testpermission.utils.PermissionUtil;

import java.util.HashMap;

/*
 * Created by ZS27 on 2016/12/14.
 *
 * fragment的用法
 */

public class RequestFragment extends Fragment {

    /*辅助类*/
    private PermissionUtil.PermissionObj.RequestObject requestObject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("info", "MFragment:onCreateView----------------------");
        View inflate = inflater.inflate(R.layout.fragment_temp, container, false);


        /*需要申请的(权限，别名)集合*/
        HashMap<String, String> permissionMap = new HashMap<>();
        permissionMap.put(Manifest.permission.CAMERA, "相机");
        permissionMap.put(Manifest.permission.READ_CONTACTS, "通讯录");
        permissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "存储空间");

        /**
         *  注册辅助类   field
         *  设置dialog内容，设置回调接口
         *  处理不能正常绑定的异常，直接推迟，结束当前页面
         * */
        try {
            /*需要的权限未全部获取，进行退出操作*//*需要的权限全部获取，进行相关，初始操作需要从resume开始*/
            requestObject = PermissionUtil.with(this)
                    .checkPermission(permissionMap)
                    .addCallBack(new PermissionUtil.PermissionCallback() {
                        @Override
                        public void failToGetPermission() {
                                /*需要的权限未全部获取，进行退出操作*/
                            getActivity().finish();
                        }

                        @Override
                        public void allNeededPermissionGranted() {
                                /*需要的权限全部获取，进行相关，初始操作需要从resume开始*/
                        }
                    }).setHelpContent("帮助", "为了正常显示内容，请点击“去开启”在应用设置中打开“权限”，开启以下权限：\n");
        } catch (Exception e) {
            Log.i("info", "Exception:Exception----------------------");
            e.printStackTrace();
        }

        return inflate;
    }

    /*
    * 意外问题：
    *   dialog 会引起onpause，窗口弹出（申请操作）不能写在onresume
    *
    *   cancel(back)引起窗口的无处理，无反馈，setOnCancelListener
    *
    *   home 引起重启时，dialog依旧存在。必须写在stop中，如果写在pause中，其他窗口弹出也会触发
    *
    * */


    @Override
    public void onStart() {
        super.onStart();
        /*因为要弹出窗口，在start调用*/
        requestObject.requsetPermission(0);
    }


    @Override
    public void onStop() {
        super.onStop();
        /*处理用户的home操作(极端情况)*/
        requestObject.cancelDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestObject.onResultOperation(requestCode, permissions, grantResults);
    }
}

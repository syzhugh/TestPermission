package com.test.sun.testpermission.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ZS27 on 2016/12/3.
 */

public class PermissionUtil {

    public static PermissionObj with(AppCompatActivity activity) {
        return new PermissionObj(activity);
    }

    public static PermissionObj with(Fragment fragmentV4) {
        return new PermissionObj(fragmentV4);
    }

    public static PermissionObj with(android.app.Fragment fragment) {
        return new PermissionObj(fragment);
    }

    public interface PermissionCallback {
        void PermissionResult(boolean allGranted);
    }

    public static class PermissionObj {

        private AppCompatActivity activity;
        private Fragment fragmentV4;
        private android.app.Fragment fragment;

        public PermissionObj(AppCompatActivity activity) {
            this.activity = activity;
        }

        public PermissionObj(Fragment fragmentV4) {
            this.fragmentV4 = fragmentV4;
        }

        public PermissionObj(android.app.Fragment fragment) {
            this.fragment = fragment;
        }

        /**
         * 用于单个权限的申请
         *
         * @param permission 单个权限的名称 {@link android.Manifest.permission}
         */
        public RequestObject checkPermission(String permission) {
            Activity tempAct = getActivity();
            HashSet<String> set = new HashSet<>();
            if (ContextCompat.checkSelfPermission(tempAct.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                set.add(permission);
            }
            return new RequestObject(tempAct, set);
        }

        /**
         * 用于单个权限的申请
         *
         * @param set 多个权限的的String Set集合 {@link android.Manifest.permission}
         */
        public RequestObject checkPermission(HashSet<String> set) {
            Activity tempAct = getActivity();
            Iterator<String> iterator = set.iterator();
            if (iterator.hasNext()) {
                String temp = iterator.next();
                if (ContextCompat.checkSelfPermission(tempAct.getApplicationContext(), temp) == PackageManager.PERMISSION_GRANTED)
                    set.remove(temp);
            }
            return new RequestObject(tempAct, set);
        }

        private Activity getActivity() {
            if (activity != null) {
                return activity;
            }
            if (fragmentV4 != null) {
                return fragmentV4.getActivity();
            }
            if (fragment != null) {
                return fragment.getActivity();
            }
            return null;
        }

        public static class RequestObject {
            private Activity activity;
            private HashSet<String> set;
            private PermissionCallback callback;

            private String title;
            private String content;

            public RequestObject(Activity activity, HashSet<String> set) {
                this.activity = activity;
                this.set = set;
            }

            /**
             * 用于添加权限检查结果的回调接口
             *
             * @return 用于级联调用
             */
            public RequestObject addCallBack(PermissionCallback callback) {
                this.callback = callback;
                return this;
            }

            /**
             * 用于添加提示信息
             *
             * @return 用于级联调用
             */
            public RequestObject helpContent(String title, String content) {
                this.title = title;
                this.content = content;
                return this;
            }

            /**
             * 在需要处进行调用，传入自定义的请求参数(标示)
             *
             * @param requestCode
             */
            public void requsetPermission(int requestCode) {
                String[] stringArr = new String[set.size()];
                Log.i("info", "RequestObject:requsetPermission----------------------");
                Log.i("info", ":" + stringArr.length);
                set.toArray(stringArr);
                ActivityCompat.requestPermissions(activity, stringArr, requestCode);
            }

            /**
             * 在需要处进行调用，传入自定义的请求参数(标示)
             *
             * @param requestCode
             * @param set         内部使用，用于处理检查过后依然没有给予权限的权限
             */
            private void requsetPermission(int requestCode, HashSet<String> set) {
                String[] stringArr = new String[set.size()];
                set.toArray(stringArr);
                ActivityCompat.requestPermissions(activity, stringArr, requestCode);
            }

            /**
             * 在activity中的onRequestPermissionsResult中进行调用，拿到方法参数进行调用
             *
             * @param requestCode  自定义请求常量，标示作用
             * @param permissions  申请的权限，String数组
             * @param grantResults 权限的设置结果，int数组
             */
            public void onResultOperation(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                HashSet<String> setTemp = new HashSet<>();
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        setTemp.add(permissions[i]);
                    }
                }
                if (setTemp.size() > 0) {
                    showDialog();
//                    boolean needToSet = showRequestPermissionRationale();
//                    if (needToSet) {
//                        showDialog();
//                    }
                } else {
                    callback.PermissionResult(true);
                }
            }

            public boolean showRequestPermissionRationale() {
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, iterator.next())) {
                        return true;
                    }
                }
                return false;
            }

            /*
            * 弹出自己的提示框
            * */
            private void showDialog() {
                new AlertDialog.Builder(activity)
                        .setTitle(title != null ? title : "帮助")
                        .setMessage(content != null ? content : "帮助信息")
                        .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                Log.i("info", ":" + intent.getDataString());
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.PermissionResult(false);
                            }
                        }).show();
            }

        }

    }


}

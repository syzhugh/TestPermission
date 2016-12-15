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
import android.view.KeyEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        void failToGetPermission();

        void allNeededPermissionGranted();
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
         * @param name       权限的别名，方便提示{如：Manifest.permission.CAMERA-->提示相机}
         * @return RequestObject 操作工具类
         */
        public RequestObject checkPermission(String permission, String name) {
            Activity tempAct = getActivity();
            HashMap<String, String> map = new HashMap<>();
            if (ContextCompat.checkSelfPermission(tempAct.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                map.put(permission, name);
            }
            if (activity != null) {
                return new RequestObjectAct(tempAct, map);
            }
            if (fragmentV4 != null) {
                return new RequestObjectFragmentv4(fragmentV4, map);
            }
            return null;
        }

        /**
         * 用于多个权限的申请
         *
         * @param map 多个权限的的String Map集合
         *            strint1{@link android.Manifest.permission}
         *            strint2{权限的别名，方便提示，如：Manifest.permission.CAMERA-->提示相机}
         * @return RequestObject 操作工具类
         * @throws Exception 获取对应的activity(context)失败,捕获异常进行退出
         */
        public RequestObject checkPermission(HashMap<String, String> map) throws Exception {
            Activity tempAct = getActivity();
            if (tempAct == null)
                throw new Exception("not found context");

            Set<String> set = map.keySet();
            Iterator<String> iterator = set.iterator();
            if (iterator.hasNext()) {
                String temp = iterator.next();
                if (ContextCompat.checkSelfPermission(tempAct.getApplicationContext(), temp) == PackageManager.PERMISSION_GRANTED)
                    map.remove(temp);
            }
            if (activity != null) {
                return new RequestObjectAct(tempAct, map);
            }
            if (fragmentV4 != null) {
                return new RequestObjectFragmentv4(fragmentV4, map);
            }
            return null;
        }

        private Activity getActivity() {
            if (activity != null) {
                return activity;
            }
            if (fragmentV4 != null) {
                return fragmentV4.getActivity();
            }
            return null;
        }

        public interface RequestObject {

            RequestObject addCallBack(PermissionCallback callback);

            /**
             * 用于添加提示信息
             *
             * @return 用于级联调用
             */
            RequestObject setHelpContent(String title, String content);

            /**
             * 在需要处进行调用，传入自定义的请求参数(标示)
             *
             * @param requestCode
             */
            void requsetPermission(int requestCode);

            /**
             * 在需要处进行调用，传入自定义的请求参数(标示)
             *
             * @param requestCode
             * @param set         内部使用，用于处理检查过后依然没有给予权限的权限
             */
            void requsetPermission(int requestCode, HashSet<String> set);

            /**
             * 在activity中的onRequestPermissionsResult中进行调用，拿到方法参数进行调用
             *
             * @param requestCode  自定义请求常量，标示作用
             * @param permissions  申请的权限，String数组
             * @param grantResults 权限的设置结果，int数组
             */

            void onResultOperation(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

            /**
             * 授权检查方法，配合实际使用，实时检查授权情况
             *
             * @return false未获取全部权限，true获取全部权限
             */

            boolean isAllGranted();

            /*
            * 弹出自己的提示框
            * */
            void showDialog(HashMap<String, String> map);

            /*
            * 关闭，避免重复弹出
            * */
            void cancelDialog();
        }


        public static class RequestObjectFragmentv4 implements RequestObject {
            private Fragment fragment;
            private HashMap<String, String> map;
            private PermissionCallback callback;
            private AlertDialog dialog;

            private String title;
            private String content;

            public RequestObjectFragmentv4(Fragment fragment, HashMap<String, String> map) {
                this.fragment = fragment;
                this.map = map;
            }

            public RequestObject addCallBack(PermissionCallback callback) {
                this.callback = callback;
                return this;
            }

            public RequestObject setHelpContent(String title, String content) {
                this.title = title;
                this.content = content;
                return this;
            }

            public void requsetPermission(int requestCode) {
                Set<String> set = map.keySet();
                String[] stringArr = new String[set.size()];
                set.toArray(stringArr);
                this.fragment.requestPermissions(stringArr, requestCode);
            }

            public void requsetPermission(int requestCode, HashSet<String> set) {
                String[] stringArr = new String[set.size()];
                set.toArray(stringArr);
                this.fragment.requestPermissions(stringArr, requestCode);
            }

            public void onResultOperation(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                HashMap<String, String> map = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        map.put(permissions[i], this.map.get(permissions[i]));
                    }
                }
                if (map.size() > 0) {
                    showDialog(map);
                } else {
                    callback.allNeededPermissionGranted();
                }
            }

            public boolean isAllGranted() {
                Set<String> set = map.keySet();
                Iterator<String> iterator = set.iterator();
                if (iterator.hasNext()) {
                    String temp = iterator.next();
                    if (ContextCompat.checkSelfPermission(fragment.getActivity(), temp) == PackageManager.PERMISSION_DENIED)
                        return false;
                }
                return true;
            }

            public void showDialog(HashMap<String, String> map) {
                StringBuilder builder = new StringBuilder();
                Iterator<String> iterator = map.values().iterator();
                while (iterator.hasNext()) {
                    builder.append("\n·" + iterator.next());
                }
                if (dialog == null) {
                    dialog = new AlertDialog.Builder(fragment.getActivity())
                            .setTitle(title != null ? title : "帮助")
                            .setMessage((content != null ? content : "帮助信息") + builder.toString())
                            .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + fragment.getActivity().getPackageName()));
                                    fragment.getActivity().startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callback.failToGetPermission();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    callback.failToGetPermission();
                                }
                            }).create();
                }
                dialog.show();
            }

            public void cancelDialog() {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        Log.i("info", "RequestObjectFragmentv4:onCancel----------------------");
                    }
                }
            }

        }

        public static class RequestObjectAct implements RequestObject {
            private Activity activity;
            private HashMap<String, String> map;
            private PermissionCallback callback;

            private String title;
            private String content;
            private AlertDialog dialog;

            public RequestObjectAct(Activity activity, HashMap<String, String> map) {
                this.activity = activity;
                this.map = map;
            }

            public RequestObjectAct addCallBack(PermissionCallback callback) {
                this.callback = callback;
                return this;
            }

            public RequestObjectAct setHelpContent(String title, String content) {
                this.title = title;
                this.content = content;
                return this;
            }

            public void requsetPermission(int requestCode) {
                Set<String> set = map.keySet();
                String[] stringArr = new String[set.size()];
                set.toArray(stringArr);
                ActivityCompat.requestPermissions(activity, stringArr, requestCode);
            }

            public void requsetPermission(int requestCode, HashSet<String> set) {
                String[] stringArr = new String[set.size()];
                set.toArray(stringArr);
                ActivityCompat.requestPermissions(activity, stringArr, requestCode);
            }

            public void onResultOperation(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                HashMap<String, String> map = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        map.put(permissions[i], this.map.get(permissions[i]));
                    }
                }
                if (map.size() > 0) {
                    showDialog(map);
                } else {
                    callback.allNeededPermissionGranted();
                }
            }

            public boolean showRequestPermissionRationale() {
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, iterator.next())) {
                        return true;
                    }
                }
                return false;
            }

            public boolean isAllGranted() {
                Set<String> set = map.keySet();
                Iterator<String> iterator = set.iterator();
                if (iterator.hasNext()) {
                    String temp = iterator.next();
                    if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), temp) == PackageManager.PERMISSION_DENIED)
                        return false;
                }
                return true;
            }

            public void showDialog(HashMap<String, String> map) {
                StringBuilder builder = new StringBuilder();
                Iterator<String> iterator = map.values().iterator();
                while (iterator.hasNext()) {
                    builder.append("\n·" + iterator.next());
                }
                if (dialog == null) {
                    dialog = new AlertDialog.Builder(activity)
                            .setTitle(title != null ? title : "帮助")
                            .setMessage((content != null ? content : "帮助信息") + builder.toString())
                            .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                    activity.startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callback.failToGetPermission();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    callback.failToGetPermission();
                                }
                            }).create();
                }
                dialog.show();
            }

            public void cancelDialog() {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        Log.i("info", "RequestObjectFragmentv4:onCancel----------------------");
                    }
                }
            }

        }
    }
}

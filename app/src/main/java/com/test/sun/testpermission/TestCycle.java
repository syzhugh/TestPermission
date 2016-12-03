package com.test.sun.testpermission;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by ZS27 on 2016/12/3.
 */

public class TestCycle extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        Log.i("info", "TestCycle:onStart----------------------");
        super.onStart();

        new AlertDialog.Builder(this)
                .setTitle("帮助")
                .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.setData(Uri.parse("package:" + TestCycle.this.getPackageName()));
//                        Log.i("info", ":" + intent.getDataString());
//                        TestCycle.this.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        callback.PermissionResult(false);
                    }
                })
                .setMessage("帮助信息")
                .show();
    }

    @Override
    protected void onResume() {
        Log.i("info", "TestCycle:onResume----------------------");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("info", "TestCycle:onPause----------------------");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("info", "TestCycle:onStop----------------------");
        super.onStop();
    }
}

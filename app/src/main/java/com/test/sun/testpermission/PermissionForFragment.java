package com.test.sun.testpermission;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PermissionForFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        RequestFragment mFragment = new RequestFragment();
        fragmentTransaction.add(R.id.main_container, mFragment, "123");
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        Log.i("info", "TestFragmentAct:onBackPressed----------------------");
        super.onBackPressed();
        finish();
    }
}

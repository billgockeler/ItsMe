package com.itsme;

import android.app.Activity;
import android.os.Bundle;




public class MainActivity extends Activity {

    MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = (MainFragment)getFragmentManager().findFragmentByTag("fragment_tag");

        if (fragment == null) {
            fragment = new MainFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, "fragment_tag")
                    .commit();
        }
    }

}

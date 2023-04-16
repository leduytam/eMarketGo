package com.group05.emarketgo.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;
import com.group05.emarketgo.views.fragments.HistoryFragment;
import com.group05.emarketgo.views.fragments.HomeFragment;
import com.group05.emarketgo.views.fragments.ProfileFragment;

@ExperimentalBadgeUtils public class LayoutActivity extends AppCompatActivity {
    private HomeFragment homeFragment;

    private HistoryFragment historyFragment;
    private ProfileFragment profileFragment;

    private static FirebaseAuth mAuth;

    private NavigationBarView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        mAuth = FirebaseAuth.getInstance();

        homeFragment = HomeFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();

        var ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_fragment_container, homeFragment);
        ft.commit();

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            var id = item.getItemId();
            var ft1 = getSupportFragmentManager().beginTransaction();

            if (id == R.id.action_home) {
                ft1.replace(R.id.fl_fragment_container, homeFragment);
            } else if (id == R.id.action_history) {
                ft1.replace(R.id.fl_fragment_container, historyFragment);
            } else if (id == R.id.action_profile) {
                ft1.replace(R.id.fl_fragment_container, profileFragment);
            }

            ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft1.commit();

            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        var firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            var isVerified = firebaseUser.isEmailVerified();
            if (!isVerified) {
                Intent intent = new Intent(LayoutActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(LayoutActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        }
    }
}

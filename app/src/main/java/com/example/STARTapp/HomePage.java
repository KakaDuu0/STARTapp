package com.example.STARTapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Fragment principal, contine bara de navigare
 */
public class HomePage extends AppCompatActivity {

    public static User user = new User();
    public static Leaderboard leaderboard = new Leaderboard();
    private long pressedTime;
    private int currentFragment = 1;
    private final int TOTAL_FRAGMENTS = 4;

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

        String username = getIntent().getStringExtra("username");
        user.setUsername(username);
        String email = getIntent().getStringExtra("email");
        user.setEmail(email);
        user.getData();

        leaderboard.init();

        FrameLayout frameLayout = findViewById(R.id.container);
        frameLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                if (currentFragment != TOTAL_FRAGMENTS) {
                    getFragment(++currentFragment);
                }
            }
            public void onSwipeRight() {
                if (currentFragment != 1) {
                    getFragment(--currentFragment);
                }
            }
        });

        BottomNavigationView eNav = findViewById(R.id.home_nav);
        eNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        eNav.setItemIconTintList(null);
        openFragment(ProfileFragment.newInstance(user));

    }

    private void getFragment(int poz) {
        switch (poz) {
            case 1:
                openFragment(ProfileFragment.newInstance(user));
                return;
            case 2:
                openFragment(CoursesFragment.newInstance());
                return;
            case 3:
                openFragment(LeaderboardFragment.newInstance());
                return;
            case 4:
                openFragment(SettingsFragment.newInstance(user));
        }
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            resetPref();
            super.onBackPressed();
            finish();
        } else {
            String close = getResources().getString(R.string.close_app);
            Toast.makeText(getBaseContext(), close, Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        final int profile_id = R.id.profile;
        final int settings_id = R.id.settings;
        final int courses_id = R.id.courses;
        final int leaderbd_id = R.id.leaderboard;

        switch (item.getItemId()) {
            case courses_id:
                openFragment(CoursesFragment.newInstance());
                currentFragment = 2;
                return true;
            case leaderbd_id:
                openFragment(LeaderboardFragment.newInstance());
                currentFragment = 3;
                return true;
            case profile_id:
                openFragment(ProfileFragment.newInstance(user));
                currentFragment = 1;
                return true;
            case settings_id:
                openFragment(SettingsFragment.newInstance(user));
                currentFragment = 4;
                return true;
        }
        return false;
    };

    private void resetPref() {
        getSharedPreferences(GetStartedPage.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(GetStartedPage.PREF_USERNAME, null)
                .apply();
    }

    /**
     * Functie pentru actualizarea culoare barii de status
     *
     * @param color culoare
     */
    public void updateStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}

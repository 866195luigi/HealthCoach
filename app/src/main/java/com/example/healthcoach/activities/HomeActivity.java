package com.example.healthcoach.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.fragments.FragmentHome;
import com.example.healthcoach.fragments.FragmentProfile;
import com.example.healthcoach.fragments.FragmentHistory;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.example.healthcoach.fragments.FragmentSetting;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.Nullable;



public class HomeActivity extends AppCompatActivity {

    private Fragment fragment1, fragment2, fragment3, fragment4;
    private Fragment activeFragment;
    private HomeActivityViewModel homeViewModel;
    private HomeActivityViewModel homeActivityViewModel;
    private StepViewModel stepViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        homeActivityViewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);

        // Initialize fragments
        fragment1 = new FragmentHome();
        fragment2 = new FragmentProfile();
        fragment3 = new FragmentHistory();
        fragment4 = new FragmentSetting();

        // Set initial active fragment
        activeFragment = fragment1;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment1, "1").commit();

        fragment1.setRetainInstance(true);
        fragment2.setRetainInstance(true);
        fragment3.setRetainInstance(true);
        fragment4.setRetainInstance(true);
    }

    // Removed the bindViews method as it's not used anymore

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = fragment1;
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = fragment2;
                        break;
                    case R.id.navigation_diary:
                        selectedFragment = fragment3;
                        break;
                    case R.id.navigation_setting:
                        selectedFragment = fragment4;
                        break;
                    default:
                        return false;
                }

                if (selectedFragment != activeFragment) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                    activeFragment = selectedFragment;
                }

                return true;
            };



}

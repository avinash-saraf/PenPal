package com.example.penpal.interests;

import android.content.Intent;
import android.os.Bundle;

import com.example.penpal.MainActivity;
import com.example.penpal.R;
import com.example.penpal.interests.model.Interest;
import com.example.penpal.interests.ui.main.PageViewModel;
import com.example.penpal.interests.ui.main.PlaceholderFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.penpal.interests.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class InterestsActivity extends AppCompatActivity {


    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setUserInputEnabled(false);
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(TAB_TITLES[position]);
                    }
                }
        ).attach();
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToMainActivity();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(InterestsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}


package com.taimoor.TodoNotifier.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.taimoor.TodoNotifier.Adapter.ViewPagerAdapter;
import com.taimoor.TodoNotifier.Fragments.BottomSheetFragment;
import com.taimoor.TodoNotifier.Fragments.CompletedFragment;
import com.taimoor.TodoNotifier.Fragments.PendingTaskFragment;
import com.taimoor.TodoNotifier.Model.SharedViewModel;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taimoor.TodoNotifier.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter vpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.setupWithViewPager(viewPager);

        //Using viewPager Adapter to display Fragments
        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new PendingTaskFragment(),"Pending Task");
        vpAdapter.addFragment(new CompletedFragment(),"Completed Task");

        viewPager.setAdapter(vpAdapter);

        //Setting up Bottom sheet fragment
        bottomSheetFragment = new BottomSheetFragment();
        ScrollView scrollView = findViewById(R.id.newbottomSheet);
        BottomSheetBehavior<ScrollView> bottomSheetBehavior = BottomSheetBehavior.from(scrollView);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        //Display Bottom Sheet fragment with click on Floating button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            sharedViewModel.setIsEdit(false);
           showBottomSheetDialogue();
        });
    }

    public static Context GetContextOfActivity(){
        return context;
    }

    private void showBottomSheetDialogue() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
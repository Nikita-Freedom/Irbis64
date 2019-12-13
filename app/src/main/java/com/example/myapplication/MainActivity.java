package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.ui.Account.AccountFragment;
import com.example.myapplication.ui.Basket.BasketFragment;
import com.example.myapplication.ui.News.NewsFragment;
import com.example.myapplication.ui.QR.QRFragment;
import com.example.myapplication.ui.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FragmentPagerAdapter _fragmentPagerAdapter;
    private int currentFragment = 0;
    private final List<Fragment> _fragments = new ArrayList<Fragment>();
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    public static final int FRAGMENT_FOUR = 3;
    public static final int FRAGMENT_FIVE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _fragments.add(FRAGMENT_ONE, new QRFragment());
        _fragments.add(FRAGMENT_TWO, new NewsFragment());
        _fragments.add(FRAGMENT_THREE, new SearchFragment());
        _fragments.add(FRAGMENT_FOUR, new BasketFragment());
        _fragments.add(FRAGMENT_FIVE, new AccountFragment());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFragment();
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, fragment)
//                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }
    private void initFragment(){
        for(Fragment fragment :_fragments){
            getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, fragment)
                    .hide(fragment)
                    .commit();
        }
        getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_ONE))
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        getSupportFragmentManager().beginTransaction().hide(_fragments.get(currentFragment))
                .commit();
        switch (item.getItemId()) {
            case R.id.navigation_QR:
                getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_ONE));
                currentFragment = FRAGMENT_ONE;
                break;
            case R.id.navigation_NEWS:
                getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_TWO))
                        .commit();
                currentFragment = FRAGMENT_TWO;
                break;
            case R.id.navigation_SEARCH:
                getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_THREE))
                        .commit();
                currentFragment = FRAGMENT_THREE;
                break;
            case R.id.navigation_BASKET:
                getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_FOUR))
                        .commit();
                currentFragment = FRAGMENT_FOUR;
                break;
            case R.id.navigation_ACCOUNT:
                getSupportFragmentManager().beginTransaction().show(_fragments.get(FRAGMENT_FIVE))
                        .commit();
                currentFragment = FRAGMENT_FIVE;
                break;
        }
        return loadFragment(fragment);
    }
}
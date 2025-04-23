package com.example.projefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.projefinal.R;
import com.example.projefinal.fragments.ProductListFragment;
import com.example.projefinal.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddProduct;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup Navigation Header
        View headerView = navigationView.getHeaderView(0);
        TextView headerPhone = headerView.findViewById(R.id.headerPhone);
        TextView headerUserType = headerView.findViewById(R.id.headerUserType);
        headerPhone.setText(sessionManager.getUserPhone());
        headerUserType.setText(sessionManager.isAdmin() ? "Admin" : "Customer");

        // Show/Hide Admin Menu Items
        navigationView.getMenu().findItem(R.id.nav_manage_products).setVisible(sessionManager.isAdmin());
        navigationView.getMenu().findItem(R.id.nav_reservations_admin).setVisible(sessionManager.isAdmin());
        fabAddProduct.setVisibility(sessionManager.isAdmin() ? View.VISIBLE : View.GONE);

        // Setup Navigation Listeners
        setupNavigationListeners();
        
        // Load default fragment
        loadFragment(new ProductListFragment(), "Products");
    }

    private void setupNavigationListeners() {
        // Drawer Navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_logout) {
                sessionManager.logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }

            // Handle other navigation items
            if (id == R.id.nav_products) {
                loadFragment(new ProductListFragment(), "Products");
            } else if (id == R.id.nav_categories) {
                // TODO: Implement categories fragment
            } else if (id == R.id.nav_my_reservations) {
                // TODO: Implement reservations fragment
            } else if (id == R.id.nav_manage_products && sessionManager.isAdmin()) {
                // TODO: Implement admin product management
            } else if (id == R.id.nav_reservations_admin && sessionManager.isAdmin()) {
                // TODO: Implement admin reservations management
            } else if (id == R.id.nav_profile_settings) {
                // TODO: Implement profile settings
            }
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                loadFragment(new ProductListFragment(), "Products");
                return true;
            } else if (id == R.id.nav_search) {
                // TODO: Implement search fragment
                return true;
            } else if (id == R.id.nav_reservations) {
                // TODO: Implement reservations fragment
                return true;
            } else if (id == R.id.nav_profile) {
                // TODO: Implement profile fragment
                return true;
            }
            
            return false;
        });

        // FAB click listener for admin to add new products
        fabAddProduct.setOnClickListener(v -> {
            if (sessionManager.isAdmin()) {
                startActivityForResult(new Intent(this, AddEditProductActivity.class), 
                        REQUEST_ADD_PRODUCT);
            }
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static final int REQUEST_ADD_PRODUCT = 1;
    private static final int REQUEST_EDIT_PRODUCT = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && 
           (requestCode == REQUEST_ADD_PRODUCT || requestCode == REQUEST_EDIT_PRODUCT)) {
            // Refresh current fragment if it's ProductListFragment
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
            if (currentFragment instanceof ProductListFragment) {
                ((ProductListFragment) currentFragment).loa5dProducts();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is still logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}

package com.example.projefinal.fragments;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.example.projefinal.R;
import com.example.projefinal.adapters.ProductAdapter;
import com.example.projefinal.database.DatabaseHelper;
import com.example.projefinal.models.Product;
import com.example.projefinal.models.Reservation;
import com.example.projefinal.utils.NotificationHelper;
import com.example.projefinal.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup categoryChipGroup;
    private TextInputEditText searchInput;
    private TextInputEditText minPriceInput;
    private TextInputEditText maxPriceInput;
    private ProductAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());
        notificationHelper = new NotificationHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        searchInput = view.findViewById(R.id.searchInput);
        minPriceInput = view.findViewById(R.id.minPriceInput);
        maxPriceInput = view.findViewById(R.id.maxPriceInput);

        setupRecyclerView();
        setupCategoryChips();
        setupSearchInput();
        setupPriceFilters();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(this, sessionManager.isAdmin());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Configure SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProducts();
        });
        
        loadProducts();
    }

    private void setupCategoryChips() {
        List<String> types = databaseHelper.getAllProductTypes();
        for (String type : types) {
            Chip chip = new Chip(requireContext());
            chip.setText(type);
            chip.setCheckable(true);
            categoryChipGroup.addView(chip);
        }

        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                applyFilters(null, getMinPrice(), getMaxPrice());
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    applyFilters(chip.getText().toString(), getMinPrice(), getMaxPrice());
                }
            }
        });
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3 || s.length() == 0) {
                    performSearch(s.toString());
                }
            }
        });
    }

    private void setupPriceFilters() {
        TextWatcher priceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String selectedType = null;
                if (categoryChipGroup.getCheckedChipId() != View.NO_ID) {
                    Chip chip = categoryChipGroup.findViewById(categoryChipGroup.getCheckedChipId());
                    if (chip != null) {
                        selectedType = chip.getText().toString();
                    }
                }
                applyFilters(selectedType, getMinPrice(), getMaxPrice());
            }
        };

        minPriceInput.addTextChangedListener(priceWatcher);
        maxPriceInput.addTextChangedListener(priceWatcher);
    }

    private double getMinPrice() {
        try {
            return Double.parseDouble(minPriceInput.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    private double getMaxPrice() {
        try {
            return Double.parseDouble(maxPriceInput.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    public void loadProducts() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        clearFilters();
        List<Product> products = databaseHelper.getAllProducts();
        updateProductList(products);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void clearFilters() {
        categoryChipGroup.clearCheck();
        searchInput.setText("");
        minPriceInput.setText("");
        maxPriceInput.setText("");
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Show product details
    }

    @Override
    public void onReserveClick(Product product) {
        if (sessionManager.isLoggedIn()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Reserve Product")
                    .setMessage("Would you like to reserve " + product.getName() + "?")
                    .setPositiveButton("Reserve", (dialog, which) -> {
                        Reservation reservation = new Reservation(
                                sessionManager.getUserId(),
                                product.getBarcode()
                        );
                        long reservationId = databaseHelper.createReservation(reservation);
                        if (reservationId != -1) {
                            notificationHelper.showReservationConfirmation(reservation, product.getName());
                            Toast.makeText(requireContext(), "Reservation successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onEditClick(Product product) {
        if (sessionManager.isAdmin()) {
            // TODO: Launch edit product activity
        }
    }

    @Override
    public void onDeleteClick(Product product) {
        if (sessionManager.isAdmin()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // TODO: Implement product deletion
                        adapter.removeProduct(product);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_product_list, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_product);
        if (addItem != null) {
            addItem.setVisible(sessionManager.isAdmin());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_product && sessionManager.isAdmin()) {
            // TODO: Launch add product activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyFilters(String type, double minPrice, double maxPrice) {
        List<Product> filteredProducts;
        String searchQuery = searchInput.getText().toString().trim();
        
        if (!searchQuery.isEmpty()) {
            filteredProducts = databaseHelper.searchProductsByName(searchQuery);
            if (type != null || minPrice >= 0 || maxPrice > 0) {
                filteredProducts.removeIf(product -> 
                    (type != null && !product.getType().equals(type)) ||
                    (minPrice >= 0 && product.getPrice() < minPrice) ||
                    (maxPrice > 0 && product.getPrice() > maxPrice)
                );
            }
        } else {
            filteredProducts = databaseHelper.searchProducts(type, minPrice, maxPrice);
        }
        
        updateProductList(filteredProducts);
    }

    private void performSearch(String query) {
        String selectedType;
        if (categoryChipGroup.getCheckedChipId() != View.NO_ID) {
            Chip chip = categoryChipGroup.findViewById(categoryChipGroup.getCheckedChipId());
            if (chip != null) {
                selectedType = chip.getText().toString();
            } else {
                selectedType = null;
            }
        } else {
            selectedType = null;
        }

        if (query.isEmpty()) {
            applyFilters(selectedType, getMinPrice(), getMaxPrice());
        } else {
            List<Product> searchResults = databaseHelper.searchProductsByName(query);
            if (selectedType != null || getMinPrice() >= 0 || getMaxPrice() > 0) {
                searchResults.removeIf(product ->
                    (selectedType != null && !product.getType().equals(selectedType)) ||
                    (getMinPrice() >= 0 && product.getPrice() < getMinPrice()) ||
                    (getMaxPrice() > 0 && product.getPrice() > getMaxPrice())
                );
            }
            updateProductList(searchResults);
        }
    }

    private void updateProductList(List<Product> products) {
        adapter.setProducts(products);
        View emptyView = getView().findViewById(R.id.emptyView);
        if (emptyView != null) {
            emptyView.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
        }
        recyclerView.setAdapter(null);
    }

    public void loa5dProducts() {
    }
}

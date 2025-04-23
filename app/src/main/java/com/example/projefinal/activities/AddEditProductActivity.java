package com.example.projefinal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.projefinal.R;
import com.example.projefinal.database.DatabaseHelper;
import com.example.projefinal.models.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditProductActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private static final int REQUEST_BARCODE_SCAN = 4;

    private ImageView productImageView;
    private TextInputEditText barcodeInput;
    private TextInputEditText nameInput;
    private AutoCompleteTextView typeInput;
    private TextInputEditText brandInput;
    private TextInputEditText priceInput;
    private ImageButton scanBarcodeButton;
    private ImageButton addImageButton;

    private DatabaseHelper databaseHelper;
    private String currentPhotoPath;
    private Product existingProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        // Initialize Database
        databaseHelper = new DatabaseHelper(this);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Views
        initializeViews();
        setupListeners();
        setupTypeDropdown();

        // Check if editing existing product
        String barcode = getIntent().getStringExtra("product_barcode");
        if (barcode != null) {
            loadProduct(barcode);
        }
    }

    private void initializeViews() {
        productImageView = findViewById(R.id.productImageView);
        barcodeInput = findViewById(R.id.barcodeInput);
        nameInput = findViewById(R.id.nameInput);
        typeInput = findViewById(R.id.typeInput);
        brandInput = findViewById(R.id.brandInput);
        priceInput = findViewById(R.id.priceInput);
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        addImageButton = findViewById(R.id.addImageButton);

        findViewById(R.id.saveButton).setOnClickListener(v -> saveProduct());
    }

    private void setupListeners() {
        addImageButton.setOnClickListener(v -> showImageSourceDialog());
        scanBarcodeButton.setOnClickListener(v -> startBarcodeScanner());
    }

    private void setupTypeDropdown() {
        List<String> types = databaseHelper.getAllProductTypes();
        if (types.isEmpty()) {
            // Add default categories if none exist
            types.add(getString(R.string.category_skincare));
            types.add(getString(R.string.category_makeup));
            types.add(getString(R.string.category_haircare));
            types.add(getString(R.string.category_fragrance));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, types);
        typeInput.setAdapter(adapter);
    }

    private void loadProduct(String barcode) {
        existingProduct = databaseHelper.getProduct(barcode);
        if (existingProduct != null) {
            barcodeInput.setText(existingProduct.getBarcode());
            barcodeInput.setEnabled(false);
            nameInput.setText(existingProduct.getName());
            typeInput.setText(existingProduct.getType());
            brandInput.setText(existingProduct.getBrand());
            priceInput.setText(String.valueOf(existingProduct.getPrice()));
            
            if (existingProduct.getImageUrl() != null) {
                // TODO: Load image using image loading library
            }

            setTitle(R.string.title_edit_product);
        }
    }

    private void showImageSourceDialog() {
        String[] options = {
            getString(R.string.action_take_photo),
            getString(R.string.action_choose_image)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.action_add)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (checkCameraPermission()) {
                            dispatchTakePictureIntent();
                        }
                    } else {
                        pickImageFromGallery();
                    }
                })
                .show();
    }

    private boolean checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void startBarcodeScanner() {
        // TODO: Implement barcode scanning
        Toast.makeText(this, "Barcode scanning coming soon", Toast.LENGTH_SHORT).show();
    }

    private void saveProduct() {
        String barcode = barcodeInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        String brand = brandInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(barcode)) {
            barcodeInput.setError(getString(R.string.msg_field_required));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.msg_field_required));
            return;
        }
        if (TextUtils.isEmpty(type)) {
            typeInput.setError(getString(R.string.msg_field_required));
            return;
        }
        if (TextUtils.isEmpty(brand)) {
            brandInput.setError(getString(R.string.msg_field_required));
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            priceInput.setError(getString(R.string.msg_field_required));
            return;
        }

        double price = Double.parseDouble(priceStr);
        Product product = new Product(barcode, name, type, brand, price, currentPhotoPath);

        long result = existingProduct == null ?
                databaseHelper.createProduct(product) :
                databaseHelper.updateProduct(product);

        if (result != -1) {
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (existingProduct != null) {
            getMenuInflater().inflate(R.menu.menu_edit_product, menu);
            menu.findItem(R.id.action_archive).setTitle(
                existingProduct.isArchived() ? R.string.action_unarchive : R.string.action_archive);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_archive) {
            handleArchiveProduct();
            return true;
        } else if (id == R.id.action_delete) {
            handleDeleteProduct();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleArchiveProduct() {
        boolean newArchiveState = !existingProduct.isArchived();
        new AlertDialog.Builder(this)
                .setTitle(newArchiveState ? R.string.action_archive : R.string.action_unarchive)
                .setMessage(getString(newArchiveState ? 
                        R.string.msg_confirm_archive : R.string.msg_confirm_unarchive))
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    if (databaseHelper.archiveProduct(existingProduct.getBarcode(), newArchiveState)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void handleDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage(R.string.msg_confirm_delete)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    if (databaseHelper.deleteProduct(existingProduct.getBarcode())) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Update ImageView with captured photo
                if (currentPhotoPath != null) {
                    productImageView.setImageURI(Uri.parse(currentPhotoPath));
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Handle picked image
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    // Copy the image to app's private storage
                    try {
                        File destination = createImageFile();
                        copyImageToFile(imageUri, destination);
                        currentPhotoPath = destination.getAbsolutePath();
                        productImageView.setImageURI(Uri.parse(currentPhotoPath));
                    } catch (IOException e) {
                        Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_BARCODE_SCAN && data != null) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                if (barcode != null) {
                    barcodeInput.setText(barcode);
                }
            }
        }
    }

    private void copyImageToFile(Uri sourceUri, File destination) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(sourceUri);
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}

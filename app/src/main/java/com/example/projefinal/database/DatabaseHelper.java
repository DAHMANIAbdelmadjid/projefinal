package com.example.projefinal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projefinal.models.Product;
import com.example.projefinal.models.Reservation;
import com.example.projefinal.models.User;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CosmeticsStore.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_RESERVATIONS = "reservations";

    // Common Columns
    private static final String KEY_ID = "id";

    // Users Table Columns
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_ADMIN = "is_admin";

    // Products Table Columns
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_IS_ARCHIVED = "is_archived";

    // Reservations Table Columns
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PRODUCT_BARCODE = "product_barcode";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIMESTAMP = "timestamp";

    // Create Table Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PHONE + " TEXT UNIQUE NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + KEY_IS_ADMIN + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + KEY_BARCODE + " TEXT PRIMARY KEY,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_TYPE + " TEXT NOT NULL,"
            + KEY_BRAND + " TEXT NOT NULL,"
            + KEY_PRICE + " REAL NOT NULL,"
            + KEY_IMAGE_URL + " TEXT,"
            + KEY_IS_ARCHIVED + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_RESERVATIONS = "CREATE TABLE " + TABLE_RESERVATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER NOT NULL,"
            + KEY_PRODUCT_BARCODE + " TEXT NOT NULL,"
            + KEY_STATUS + " TEXT NOT NULL,"
            + KEY_TIMESTAMP + " INTEGER NOT NULL,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
            + "FOREIGN KEY(" + KEY_PRODUCT_BARCODE + ") REFERENCES " + TABLE_PRODUCTS + "(" + KEY_BARCODE + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_RESERVATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User CRUD Operations
    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_IS_ADMIN, user.isAdmin() ? 1 : 0);
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUser(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_PHONE + "=?",
                new String[]{phone}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
            int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD);
            int isAdminIndex = cursor.getColumnIndex(KEY_IS_ADMIN);

            if (idIndex >= 0 && phoneIndex >= 0 && passwordIndex >= 0 && isAdminIndex >= 0) {
                user = new User(
                        cursor.getInt(idIndex),
                        cursor.getString(phoneIndex),
                        cursor.getString(passwordIndex),
                        cursor.getInt(isAdminIndex) == 1
                );
            }
            cursor.close();
        }
        return user;
    }

    // Product CRUD Operations
    public long createProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, product.getBarcode());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_TYPE, product.getType());
        values.put(KEY_BRAND, product.getBrand());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_IMAGE_URL, product.getImageUrl());
        values.put(KEY_IS_ARCHIVED, product.isArchived() ? 1 : 0);
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public Product getProduct(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, KEY_BARCODE + "=?",
                new String[]{barcode}, null, null, null);

        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            int barcodeIndex = cursor.getColumnIndex(KEY_BARCODE);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int typeIndex = cursor.getColumnIndex(KEY_TYPE);
            int brandIndex = cursor.getColumnIndex(KEY_BRAND);
            int priceIndex = cursor.getColumnIndex(KEY_PRICE);
            int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
            int isArchivedIndex = cursor.getColumnIndex(KEY_IS_ARCHIVED);

            if (barcodeIndex >= 0 && nameIndex >= 0 && typeIndex >= 0 &&
                    brandIndex >= 0 && priceIndex >= 0 && imageUrlIndex >= 0 && isArchivedIndex >= 0) {
                product = new Product(
                        cursor.getString(barcodeIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(typeIndex),
                        cursor.getString(brandIndex),
                        cursor.getDouble(priceIndex),
                        cursor.getString(imageUrlIndex));
                product.setArchived(cursor.getInt(isArchivedIndex) == 1);
            }
            cursor.close();
        }
        return product;
    }

    public long updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_TYPE, product.getType());
        values.put(KEY_BRAND, product.getBrand());
        values.put(KEY_PRICE, product.getPrice());
        if (product.getImageUrl() != null) {
            values.put(KEY_IMAGE_URL, product.getImageUrl());
        }

        return db.update(TABLE_PRODUCTS, values, KEY_BARCODE + "=?",
                new String[]{product.getBarcode()});
    }

    // This method seems to be incorrectly defined in the original code
    // Assuming it should be public List<Product> searchProducts(String type, double minPrice, double maxPrice)
    public List<Product> searchProducts(String type, double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "";
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (type != null && !type.isEmpty()) {
            selection += KEY_TYPE + "=? AND ";
            selectionArgs.add(type);
        }

        if (minPrice >= 0) {
            selection += KEY_PRICE + ">=? AND ";
            selectionArgs.add(String.valueOf(minPrice));
        }

        if (maxPrice > 0) {
            selection += KEY_PRICE + "<=? AND ";
            selectionArgs.add(String.valueOf(maxPrice));
        }

        selection += KEY_IS_ARCHIVED + "=0";

        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection,
                selectionArgs.toArray(new String[0]), null, null, KEY_PRICE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int barcodeIndex = cursor.getColumnIndex(KEY_BARCODE);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int typeIndex = cursor.getColumnIndex(KEY_TYPE);
            int brandIndex = cursor.getColumnIndex(KEY_BRAND);
            int priceIndex = cursor.getColumnIndex(KEY_PRICE);
            int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
            int isArchivedIndex = cursor.getColumnIndex(KEY_IS_ARCHIVED);

            if (barcodeIndex >= 0 && nameIndex >= 0 && typeIndex >= 0 &&
                    brandIndex >= 0 && priceIndex >= 0 && imageUrlIndex >= 0 && isArchivedIndex >= 0) {
                do {
                    Product product = new Product(
                            cursor.getString(barcodeIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(typeIndex),
                            cursor.getString(brandIndex),
                            cursor.getDouble(priceIndex),
                            cursor.getString(imageUrlIndex)
                    );
                    product.setArchived(cursor.getInt(isArchivedIndex) == 1);
                    products.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return products;
    }

    public List<Product> getAllProducts() {
        return searchProducts(null, -1, -1);
    }

    public List<String> getAllProductTypes() {
        List<String> types = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_PRODUCTS,
                new String[]{KEY_TYPE}, null, null, KEY_TYPE, null, KEY_TYPE + " ASC", null);

        if (cursor != null && cursor.moveToFirst()) {
            int typeIndex = cursor.getColumnIndex(KEY_TYPE);
            if (typeIndex >= 0) {
                do {
                    types.add(cursor.getString(typeIndex));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return types;
    }

    public boolean deleteProduct(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, KEY_BARCODE + "=?", new String[]{barcode}) > 0;
    }

    public boolean archiveProduct(String barcode, boolean archive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_ARCHIVED, archive ? 1 : 0);
        return db.update(TABLE_PRODUCTS, values, KEY_BARCODE + "=?",
                new String[]{barcode}) > 0;
    }

    public List<Product> searchProductsByName(String query) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_NAME + " LIKE ? AND " + KEY_IS_ARCHIVED + "=0";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs,
                null, null, KEY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int barcodeIndex = cursor.getColumnIndex(KEY_BARCODE);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int typeIndex = cursor.getColumnIndex(KEY_TYPE);
            int brandIndex = cursor.getColumnIndex(KEY_BRAND);
            int priceIndex = cursor.getColumnIndex(KEY_PRICE);
            int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
            int isArchivedIndex = cursor.getColumnIndex(KEY_IS_ARCHIVED);

            if (barcodeIndex >= 0 && nameIndex >= 0 && typeIndex >= 0 &&
                    brandIndex >= 0 && priceIndex >= 0 && imageUrlIndex >= 0 && isArchivedIndex >= 0) {
                do {
                    Product product = new Product(
                            cursor.getString(barcodeIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(typeIndex),
                            cursor.getString(brandIndex),
                            cursor.getDouble(priceIndex),
                            cursor.getString(imageUrlIndex)
                    );
                    product.setArchived(cursor.getInt(isArchivedIndex) == 1);
                    products.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return products;
    }

    // Reservation CRUD Operations
    public long createReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, reservation.getUserId());
        values.put(KEY_PRODUCT_BARCODE, reservation.getProductBarcode());
        values.put(KEY_STATUS, reservation.getStatus());
        values.put(KEY_TIMESTAMP, reservation.getTimestamp());
        return db.insert(TABLE_RESERVATIONS, null, values);
    }

    public boolean updateReservationStatus(int reservationId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, newStatus);
        return db.update(TABLE_RESERVATIONS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reservationId)}) > 0;
    }

    public List<Reservation> getUserReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.*, p.name as product_name, u.phone as user_phone " +
                "FROM " + TABLE_RESERVATIONS + " r " +
                "JOIN " + TABLE_PRODUCTS + " p ON r." + KEY_PRODUCT_BARCODE + " = p." + KEY_BARCODE + " " +
                "JOIN " + TABLE_USERS + " u ON r." + KEY_USER_ID + " = u." + KEY_ID + " " +
                "WHERE r." + KEY_USER_ID + "=? " +
                "ORDER BY r." + KEY_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int userIdIndex = cursor.getColumnIndex(KEY_USER_ID);
            int productBarcodeIndex = cursor.getColumnIndex(KEY_PRODUCT_BARCODE);
            int statusIndex = cursor.getColumnIndex(KEY_STATUS);
            int timestampIndex = cursor.getColumnIndex(KEY_TIMESTAMP);
            int productNameIndex = cursor.getColumnIndex("product_name");
            int userPhoneIndex = cursor.getColumnIndex("user_phone");

            if (idIndex >= 0 && userIdIndex >= 0 && productBarcodeIndex >= 0 &&
                    statusIndex >= 0 && timestampIndex >= 0 &&
                    productNameIndex >= 0 && userPhoneIndex >= 0) {
                do {
                    Reservation reservation = new Reservation(
                            cursor.getInt(idIndex),
                            cursor.getInt(userIdIndex),
                            cursor.getString(productBarcodeIndex),
                            cursor.getString(statusIndex),
                            cursor.getLong(timestampIndex)
                    );
                    reservation.setProductName(cursor.getString(productNameIndex));
                    reservation.setUserPhone(cursor.getString(userPhoneIndex));
                    reservations.add(reservation);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return reservations;
    }
}
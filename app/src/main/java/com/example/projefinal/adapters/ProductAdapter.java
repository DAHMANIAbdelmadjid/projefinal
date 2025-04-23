package com.example.projefinal.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projefinal.R;
import com.example.projefinal.models.Product;
import com.example.projefinal.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onReserveClick(Product product);
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    private List<Product> products;
    private final OnProductClickListener listener;
    private final boolean isAdmin;

    public ProductAdapter(OnProductClickListener listener, boolean isAdmin) {
        this.products = new ArrayList<>();
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener, isAdmin);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    public void updateProduct(Product product) {
        int index = -1;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getBarcode().equals(product.getBarcode())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            products.set(index, product);
            notifyItemChanged(index);
        }
    }

    public void removeProduct(Product product) {
        int index = products.indexOf(product);
        if (index != -1) {
            products.remove(index);
            notifyItemRemoved(index);
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout mediaContainer;
        private final ImageView productImage;
        private final VideoView productVideo;
        private final ImageButton playButton;
        private final TextView productName;
        private final TextView productBrand;
        private final TextView productType;
        private final TextView productPrice;
        private final MaterialButton reserveButton;
        private SimpleExoPlayer exoPlayer;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaContainer = itemView.findViewById(R.id.mediaContainer);
            productImage = itemView.findViewById(R.id.productImage);
            productVideo = itemView.findViewById(R.id.productVideo);
            playButton = itemView.findViewById(R.id.playButton);
            productName = itemView.findViewById(R.id.productName);
            productBrand = itemView.findViewById(R.id.productBrand);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            reserveButton = itemView.findViewById(R.id.reserveButton);
        }

        public void bind(final Product product, final OnProductClickListener listener, boolean isAdmin) {
            productName.setText(product.getName());
            productBrand.setText(product.getBrand());
            productType.setText(product.getType());
            productPrice.setText(String.format(Locale.getDefault(), "%.2f MAD", product.getPrice()));

            // Handle media content
            String mediaUrl = product.getImageUrl();
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                if (mediaUrl.endsWith(".mp4")) {
                    // Setup video
                    productImage.setVisibility(View.GONE);
                    productVideo.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.VISIBLE);
                    
                    playButton.setOnClickListener(v -> {
                        if (productVideo.isPlaying()) {
                            productVideo.pause();
                            playButton.setImageResource(android.R.drawable.ic_media_play);
                        } else {
                            productVideo.setVideoURI(Uri.parse(mediaUrl));
                            productVideo.start();
                            playButton.setImageResource(android.R.drawable.ic_media_pause);
                        }
                    });
                } else {
                    // Load image
                    productImage.setVisibility(View.VISIBLE);
                    productVideo.setVisibility(View.GONE);
                    playButton.setVisibility(View.GONE);
                    
                    Glide.with(itemView.getContext())
                            .load(mediaUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(productImage);
                }
            } else {
                productImage.setVisibility(View.VISIBLE);
                productVideo.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                productImage.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // Configure reserve/edit button
            reserveButton.setVisibility(product.isArchived() ? View.GONE : View.VISIBLE);
            if (isAdmin) {
                reserveButton.setText("Edit");
                reserveButton.setIconResource(android.R.drawable.ic_menu_edit);
            } else {
                reserveButton.setText("Reserve");
                reserveButton.setIconResource(android.R.drawable.ic_menu_send);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> listener.onProductClick(product));
            
            reserveButton.setOnClickListener(v -> {
                if (isAdmin) {
                    listener.onEditClick(product);
                } else {
                    listener.onReserveClick(product);
                }
            });

            // Long press for admin to delete
            if (isAdmin) {
                itemView.setOnLongClickListener(v -> {
                    listener.onDeleteClick(product);
                    return true;
                });
            }
        }

        public void release() {
            if (productVideo.isPlaying()) {
                productVideo.stopPlayback();
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ProductViewHolder holder) {
        super.onViewRecycled(holder);
        holder.release();
    }
}

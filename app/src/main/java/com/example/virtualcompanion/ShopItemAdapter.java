package com.example.virtualcompanion;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {

    private final int[] shopImages;
    private final int[] equipImages;
    private final String[] prices;
    private final OnItemClickListener listener;

    private int selectedPosition = -1;
    private ItemViewHolder lastSelectedHolder = null;

    public interface OnItemClickListener {
        void onItemClick(int equipResId, int position);
    }

    public ShopItemAdapter(int[] shopImages, int[] equipImages, String[] prices, OnItemClickListener listener) {
        this.shopImages = shopImages;
        this.equipImages = equipImages;
        this.prices = prices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemImage.setImageResource(shopImages[position]);

        // Show price or hide container
        if (prices[position].isEmpty()) {
            holder.priceContainer.setVisibility(View.GONE);
        } else {
            holder.priceContainer.setVisibility(View.VISIBLE);
            holder.itemPrice.setText(prices[position]);
        }

        // Click listener with animation
        holder.itemView.setOnClickListener(v -> {
            // Reset previous selection animation
            if (lastSelectedHolder != null && lastSelectedHolder != holder) {
                resetItemScale(lastSelectedHolder);
            }

            // Animate current selection (like emoji in MoodActivity)
            animateItemPopUp(holder);

            // Remember last selected
            lastSelectedHolder = holder;
            selectedPosition = position;

            // Callback
            if (listener != null) {
                listener.onItemClick(equipImages[position], position);
            }
        });

        // Restore scale if this is the selected item
        if (position == selectedPosition) {
            holder.itemRoot.setScaleX(1.1f);
            holder.itemRoot.setScaleY(1.1f);
            holder.itemRoot.setTranslationZ(8f);
            lastSelectedHolder = holder;
        } else {
            holder.itemRoot.setScaleX(1f);
            holder.itemRoot.setScaleY(1f);
            holder.itemRoot.setTranslationZ(0f);
        }
    }

    /**
     * Animate item with pop-up effect when clicked (1.1x scale)
     */
    private void animateItemPopUp(ItemViewHolder holder) {
        // Scale up animation with bounce effect
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.itemRoot, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.itemRoot, "scaleY", 1f, 1.1f);

        // Slight elevation effect
        ObjectAnimator elevate = ObjectAnimator.ofFloat(holder.itemRoot, "translationZ", 0f, 8f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevate);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(2f)); // Bounce effect
        animatorSet.start();
    }

    /**
     * Reset item scale to normal (1.1x back to 1.0x)
     */
    private void resetItemScale(ItemViewHolder holder) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.itemRoot, "scaleX", 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.itemRoot, "scaleY", 1.1f, 1f);
        ObjectAnimator lower = ObjectAnimator.ofFloat(holder.itemRoot, "translationZ", 8f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, lower);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    @Override
    public int getItemCount() {
        return shopImages.length;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemRoot;
        ImageView itemImage;
        LinearLayout priceContainer;
        TextView itemPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.itemRoot);
            itemImage = itemView.findViewById(R.id.itemImage);
            priceContainer = itemView.findViewById(R.id.priceContainer);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
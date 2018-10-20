package com.grrigore.tripback_up.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.R;
import com.grrigore.tripback_up.model.Trip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private List<Trip> trips;
    private List<StorageReference> storageReferences;
    private Context context;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public TripAdapter(List<Trip> trips, List<StorageReference> storageReferences, Context context) {
        this.trips = trips;
        this.storageReferences = storageReferences;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_media_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tvTitle.setText(trip.getTitle());
        // Load the image using Glide
        //todo update version of FirebaseImageLoader
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageReferences.get(position))
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        if (trips == null) {
            return 0;
        } else {
            return trips.size();
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, Trip trip);
    }

    public interface ItemLongClickListener {
        void onLongItemClick(View view, Trip trip);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, trips.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onLongItemClick(v, trips.get(getAdapterPosition()));
            return true;
        }
    }

}

package no.aegisdynamics.habitat.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.itemListeners.LocationItemListener;
import no.aegisdynamics.habitat.util.GlideApp;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Adapter for displaying list of locations with associated images where available.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder> {

    private List<Location> mLocations;
    private final LocationItemListener mItemListener;

    public LocationsAdapter(List<Location> locations, LocationItemListener itemListener) {
        setList(locations);
        mItemListener = itemListener;
    }


    @Override
    public LocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View locationView = inflater.inflate(R.layout.item_location, parent, false);

        return new LocationsViewHolder(locationView, mItemListener);
    }


    @Override
    public void onBindViewHolder(LocationsViewHolder holder, int position) {
        final Location location = mLocations.get(position);

        holder.title.setText(location.getTitle());
        holder.deviceCount.setText(String.format("%d %s", location.getDeviceCount(), holder.deviceCount.getContext().getString(R.string.locations_devices)));
        if (location.hasImage()) {
            String imageUrl = ZWayNetworkHelper.getZwayLocationImageUrl(holder.image.getContext().getApplicationContext(),location.getImageName());
            holder.image.setVisibility(View.VISIBLE);
            // Use Glide for image loading
            GlideApp.with(holder.image.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.image);

        } else {
            // Due to the nature of the RecyclerView, we need to ensure we clear
            // out images to odd behavior.
            holder.image.setVisibility(View.GONE);
        }

    }

    public void replaceData(List<Location> locations) {
        setList(locations);
        notifyDataSetChanged();
    }

    private void setList(List<Location> locations) {
        mLocations = locations;
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public class LocationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        final TextView title;

        final TextView deviceCount;

        final ImageView image;

        private final LocationItemListener mItemListener;

        LocationsViewHolder(View itemView, LocationItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            title = itemView.findViewById(R.id.item_location_title);
            image = itemView.findViewById(R.id.item_location_image);
            deviceCount = itemView.findViewById(R.id.item_location_devices);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Location location = getItem(position);
            mItemListener.onLocationClick(location);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(view.getContext().getString(R.string.location_action_menu_title));
            Activity mActivity = (Activity) view.getContext();
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.menu_location_actions, menu);

            /*
             * Workaround listener in order to pass context menu item selections to
             * our onCustomMenuItem click method so that we can handle
             * menu item selection in the ViewHolder with the ItemListener interface.
             */
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onCustomMenuItemClick(item);
                    return true;
                }
            };

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(listener);
        }

        private Location getItem(int position) {
            return mLocations.get(position);
        }

        boolean onCustomMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            Location location = getItem(position);

            switch (menuItem.getItemId()) {
                case R.id.menu_location_delete:
                    // Delete location
                    mItemListener.onLocationDeleteClick(location);
                    return true;
                default:
                    return false;
            }
        }
    }
}


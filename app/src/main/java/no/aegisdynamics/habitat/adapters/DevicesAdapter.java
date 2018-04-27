package no.aegisdynamics.habitat.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.angrybyte.numberpicker.listener.OnValueChangeListener;
import me.angrybyte.numberpicker.view.ActualNumberPicker;
import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.DeviceItemListener;
import no.aegisdynamics.habitat.provider.DeviceDataContract;
import no.aegisdynamics.habitat.util.DeviceStatusHelper;
import no.aegisdynamics.habitat.util.FavoritesHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Adapter for displaying list of devices with controls where applicable.
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder>
        implements DeviceDataContract, Filterable {

    private List<Device> mDevices;
    private List<Device> mAllDevices;
    private boolean temperatureValueSynced = false;
    private final DeviceItemListener mItemListener;
    private final DeviceCommandListener mCommandListener;
    private ValueFilter valueFilter;

    public DevicesAdapter(List<Device> devices, DeviceItemListener itemListener, DeviceCommandListener commandListener) {
        setList(devices);
        mItemListener = itemListener;
        mCommandListener = commandListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View deviceView = inflater.inflate(R.layout.item_device, parent, false);

        return new ViewHolder(deviceView, mItemListener);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Device device = mDevices.get(position);

        holder.room.setText(String.valueOf(device.getLocation()));
        holder.title.setText(device.getTitle());
        if (device.getStatusNotation() != null) {
            holder.status.setText(String.format("%s %s", device.getStatus(), device.getStatusNotation()));
        } else {
            holder.status.setText(device.getStatus());
        }
        holder.picker.setListener(null);
        holder.statusSwitch.setOnCheckedChangeListener(null);
        if (device.isSwitchable()) {
            holder.statusSwitch.setVisibility(View.VISIBLE);

            // TOGGLE_BUTTON devices should never be checked.
            if (device.getType().equals(DeviceDataContract.DEVICE_TYPE_TOGGLE_BUTTON)) {
                holder.statusSwitch.setChecked(false);

            } else {
                holder.statusSwitch.setChecked(DeviceStatusHelper.parseStatusMessageToBoolean(device.getStatus()));
                holder.statusSwitch.setText(device.getStatus());

            }
            holder.status.setVisibility(View.GONE);
            holder.picker.setVisibility(View.GONE);
            holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    mCommandListener.onCommand(device, DeviceStatusHelper.parseBooleanStatusFromDeviceToCommand(device, checked));
                }
            });

        } else if (device.getType().equals(DEVICE_SENSOR_THERMOSTAT)) {

            holder.picker.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
            holder.statusSwitch.setVisibility(View.GONE);
            holder.picker.setMaxValue(device.getDeviceMaxValue());
            holder.picker.setMinValue(device.getDeviceMinValue());
            try {
                holder.picker.setValue(Double.valueOf(device.getStatus()).intValue());
            } catch (NumberFormatException ex) {
                holder.picker.setValue(0);
            }
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Issue the temperature command
                    mCommandListener.onCommand(device, DeviceStatusHelper.getThermostatCommand(holder.picker.getValue()));
                }
            };

            final OnValueChangeListener thermostatValueListener = new OnValueChangeListener() {
                @Override
                public void onValueChanged(int oldValue, int newValue) {
                    try {
                        if (oldValue == 1000) {
                            temperatureValueSynced = false;
                        }
                        if (temperatureValueSynced && newValue != Double.valueOf(device.getStatus()).intValue()) {
                            handler.removeCallbacks(runnable);
                            handler.postDelayed(runnable, 1000);
                        }

                        if (newValue == Double.valueOf(device.getStatus()).intValue()) {
                            temperatureValueSynced = true;
                        }
                    } catch (NumberFormatException ex) {
                        // Thermostat returned unexpected non-numeric value
                        ex.printStackTrace();
                    }
                }
            };

            holder.picker.setListener(thermostatValueListener);

        } else {
            holder.statusSwitch.setVisibility(View.GONE);
            holder.picker.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
        }


        Set<String> favorites = FavoritesHelper.getFavoriteDevicesFromPrefs(holder.title.getContext());
        if (favorites.contains(device.getId())) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.favoriteIcon.setVisibility(View.GONE);
        }

        // Use placeholder icon by default
        String imageUrl = String.format("%s.png", ZWayNetworkHelper.getZwayDevicePlaceholderImageUrl(holder.icon.getContext()));
        if (device.hasIcon()) {
            imageUrl = String.format("%s.png", ZWayNetworkHelper.getZwayDeviceImageUrl(holder.icon.getContext(),
                    device.getIconName(), device.getType(), device.getStatus()));
        }

        holder.icon.setVisibility(View.VISIBLE);
        // Use Glide for image loading
        Glide.with(holder.icon.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(holder.icon) {


                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        Glide.clear(holder.icon);
                        holder.icon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                    }
                });
    }

    public void replaceData(List<Device> devices) {
        setList(devices);
        notifyDataSetChanged();
    }

    private void setList(List<Device> devices) {
        mDevices = devices;
        mAllDevices = devices;
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    private Device getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView room;

        final TextView title;

        final TextView status;

        final ImageView icon;

        final ImageView favoriteIcon;

        final Switch statusSwitch;

        final ActualNumberPicker picker;

        private final DeviceItemListener mItemListener;

        ViewHolder(View itemView, DeviceItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            room = itemView.findViewById(R.id.item_device_room);
            title = itemView.findViewById(R.id.item_device_title);
            status = itemView.findViewById(R.id.item_device_status_text);
            icon = itemView.findViewById(R.id.item_device_icon);
            statusSwitch = itemView.findViewById(R.id.item_device_status_switch);
            favoriteIcon = itemView.findViewById(R.id.item_device_favorite_icon);
            picker = itemView.findViewById(R.id.item_device_picker);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Device device = getItem(position);
            mItemListener.onDeviceClick(device);
        }
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Device> filterList = new ArrayList<>();
                for (int i = 0; i < mAllDevices.size(); i++) {
                    if ((mAllDevices.get(i).getTitle().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        filterList.add(mAllDevices.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mAllDevices.size();
                results.values = mAllDevices;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mDevices = (ArrayList<Device>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}

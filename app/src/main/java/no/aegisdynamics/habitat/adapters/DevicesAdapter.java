package no.aegisdynamics.habitat.adapters;

import android.content.Context;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
import no.aegisdynamics.habitat.util.GlideApp;
import no.aegisdynamics.habitat.util.LogHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Adapter for displaying list of devices with controls where applicable.
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>
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
    public DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View deviceView = inflater.inflate(R.layout.item_device, parent, false);

        return new DevicesViewHolder(deviceView, mItemListener);
    }


    @Override
    public void onBindViewHolder(final DevicesViewHolder holder, int position) {
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
            bindSwitchableDevice(holder, device);

        } else if (device.getType().equals(DEVICE_SENSOR_THERMOSTAT)) {
            bindThermostatDevice(holder, device);

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
        GlideApp.with(holder.icon.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.device_placeholder)
                .into(holder.icon);
    }

    private void bindSwitchableDevice(final DevicesViewHolder holder, final Device device) {
        holder.statusSwitch.setVisibility(View.VISIBLE);

        // TOGGLE_BUTTON devices should never be checked.
        if (device.getType().equals(DeviceDataContract.DEVICE_TYPE_TOGGLE_BUTTON)) {
            holder.statusSwitch.setChecked(false);

        } else {
            boolean deviceBooleanState = DeviceStatusHelper
                    .parseStatusMessageToBoolean(device.getStatus());
            holder.statusSwitch.setChecked(deviceBooleanState);
            // Override state check for lock devices.
            if (device.getType().equals(DeviceDataContract.DEVICE_TYPE_DOOR_LOCK)) {
                if (deviceBooleanState) {
                    holder.statusSwitch.setText(holder.statusSwitch.getContext()
                            .getString(R.string.device_lock_open));
                } else {
                    holder.statusSwitch.setText(holder.statusSwitch.getContext()
                            .getString(R.string.device_lock_closed));
                }
            } else {
                holder.statusSwitch.setText(device.getStatus());
            }

        }
        holder.status.setVisibility(View.GONE);
        holder.picker.setVisibility(View.GONE);
        holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mCommandListener.onCommand(device, DeviceStatusHelper.parseBooleanStatusFromDeviceToCommand(device, checked));
            }
        });
    }

    private void bindThermostatDevice(final DevicesViewHolder holder, final Device device) {
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
                    LogHelper.logError(holder.picker.getContext(), this.getClass().getSimpleName(),
                            ex.getMessage());
                }
            }
        };

        holder.picker.setListener(thermostatValueListener);
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

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    public class DevicesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView room;

        final TextView title;

        final TextView status;

        final ImageView icon;

        final ImageView favoriteIcon;

        final Switch statusSwitch;

        final ActualNumberPicker picker;

        private final DeviceItemListener mItemListener;

        DevicesViewHolder(View itemView, DeviceItemListener itemListener) {
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

        private Device getItem(int position) {
            return mDevices.get(position);
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

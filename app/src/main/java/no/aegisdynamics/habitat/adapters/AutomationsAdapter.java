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
import android.widget.TextView;


import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.itemListeners.AutomationItemListener;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

public class AutomationsAdapter extends RecyclerView.Adapter<AutomationsAdapter.ViewHolder> implements DeviceDataContract{

    private List<Automation> mAutomations;
    private AutomationItemListener mItemListener;

    public AutomationsAdapter(List<Automation> automations, AutomationItemListener itemListener) {
        setList(automations);
        mItemListener = itemListener;
    }


    @Override
    public AutomationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View automationView = inflater.inflate(R.layout.item_automation, parent, false);
        return new AutomationsAdapter.ViewHolder(automationView, mItemListener);
    }


    @Override
    public void onBindViewHolder(AutomationsAdapter.ViewHolder holder, int position) {
        final Automation automation = mAutomations.get(position);

        holder.title.setText(automation.getName());
        holder.description.setText(automation.getDescription());

        if (automation.getType().equals(AUTOMATION_TYPE_RECURRING)) {
            String timeTriggerString = String.format(holder.title.getContext().getString(R.string.automation_type_recurring),
                    automation.getTrigger());
            holder.triggerText.setText(timeTriggerString);
        } else {
            holder.triggerText.setText(automation.getTrigger());
        }
    }

    public void replaceData(List<Automation> automations) {
        setList(automations);
        notifyDataSetChanged();
    }

    private void setList(List<Automation> automations) {
        mAutomations = automations;
    }

    @Override
    public int getItemCount() {
        return mAutomations.size();
    }

    private Automation getItem(int position) {
        return mAutomations.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private TextView title;
        private TextView description;
        private TextView triggerText;

        private AutomationItemListener mItemListener;

        public ViewHolder(View itemView, AutomationItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            title = itemView.findViewById(R.id.item_automation_title);
            description = itemView.findViewById(R.id.item_automation_description);
            triggerText = itemView.findViewById(R.id.item_automation_trigger_info);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Automation automation = getItem(position);
            mItemListener.onAutomationClick(automation);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(view.getContext().getString(R.string.automation_action_menu_title));
            Activity mActivity = (Activity) view.getContext();
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.menu_automation_actions, menu);

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

        public boolean onCustomMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            Automation automation = getItem(position);

            switch (menuItem.getItemId()) {
                case R.id.menu_automation_delete:
                    // Delete automation
                    mItemListener.onAutomationDeleteClick(automation);
                    notifyItemRemoved(position);
                    return true;
                default:
                    return false;
            }
        }
    }
}

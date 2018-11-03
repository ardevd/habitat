package no.aegisdynamics.habitat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.module.Module;
import no.aegisdynamics.habitat.itemListeners.ModuleItemListener;
import no.aegisdynamics.habitat.util.GlideApp;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ModulesAdapterViewHolder>
        implements Filterable {

    private List<Module> mModules;
    private List<Module> mAllModules;
    private final ModuleItemListener mItemListener;
    private ModulesValueFilter valueFilter;

    public ModulesAdapter(List<Module> modules, ModuleItemListener itemListener) {
        setList(modules);
        mItemListener = itemListener;
    }


    @NonNull
    @Override
    public ModulesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View modulesView = inflater.inflate(R.layout.item_module, parent, false);
        return new ModulesAdapterViewHolder(modulesView, mItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesAdapter.ModulesAdapterViewHolder holder, int position) {
        final Module module = mModules.get(position);
        holder.moduleTitle.setText(module.getModuleName());
        holder.moduleAuthor.setText(module.getAuthor());

        if (!module.getIcon().isEmpty()) {
            String iconUrl = ZWayNetworkHelper.getZwayModuleIconUrl(holder.moduleIcon.getContext(),
                    module.getId(), module.getIcon());
            GlideApp.with(holder.moduleIcon.getContext())
                    .load(iconUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.moduleIcon);

        }
    }

    public void replaceData(List<Module> modules) {
        setList(modules);
        notifyDataSetChanged();
    }

    private void setList(List<Module> modules) {
        mModules = modules;
        mAllModules = modules;
    }

    public Module getItem(int position) {
        return mModules.get(position);
    }

    @Override
    public int getItemCount() {
        return mModules.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ModulesValueFilter();
        }

        return valueFilter;
    }

    public class ModulesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        final TextView moduleTitle;

        final TextView moduleAuthor;

        final ImageView moduleIcon;

        private final ModuleItemListener mItemListener;

        ModulesAdapterViewHolder(View itemView, ModuleItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            moduleTitle = itemView.findViewById(R.id.module_title);
            moduleAuthor = itemView.findViewById(R.id.module_author);
            moduleIcon = itemView.findViewById(R.id.module_icon);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Module moduleClicked = getItem(position);
            mItemListener.onModuleClicked(moduleClicked);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }

    private class ModulesValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Module> filterList = new ArrayList<>();
                for (int i = 0; i < mModules.size(); i++) {
                    if (mModules.get(i).getModuleName()
                            .toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(mModules.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mAllModules.size();
                results.values = mAllModules;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mModules = (ArrayList<Module>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}

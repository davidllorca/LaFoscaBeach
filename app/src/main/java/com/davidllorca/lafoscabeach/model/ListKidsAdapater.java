package com.davidllorca.lafoscabeach.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.davidllorca.lafoscabeach.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter of ListView of lost kids.
 */
public class ListKidsAdapater extends BaseAdapter implements Filterable {

    private List<Kid> objects;
    private LayoutInflater layoutInflater;
    private ItemFilter mFilter = new ItemFilter();
    private List<Kid> filteredObjects;

    // Constructor
    public ListKidsAdapater(Context context, List<Kid> objects) {
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
        // Sort List by age in descending order
        Collections.sort(this.objects, new Comparator<Kid>() {
            @Override
            public int compare(Kid k1, Kid k2) {
                if (k1.getAge() < k2.getAge()) {
                    return 1;
                } else if (k1.getAge() > k2.getAge()) {
                    return -1;
                }
                return 0;
            }
        });
        this.filteredObjects = this.objects;
    }

    @Override
    public int getCount() {
        return filteredObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView name;
        TextView age;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list, null);

            // View's references
            holder.name = (TextView) convertView.findViewById(R.id.name_tv);
            holder.age = (TextView) convertView.findViewById(R.id.age_tv);

            convertView.setTag(holder);
        } else {
            Kid kid = (Kid) getItem(position);
            holder = (ViewHolder) convertView.getTag();
        }

        // Set data in components
        holder.name.setText(filteredObjects.get(position).getName());
        holder.age.setText("" + filteredObjects.get(position).getAge());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Search filter
     */
    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Kid> list = objects;

            int count = list.size();
            final ArrayList<Kid> nlist = new ArrayList<Kid>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredObjects = (ArrayList<Kid>) results.values;
            notifyDataSetChanged();

        }
    }
}

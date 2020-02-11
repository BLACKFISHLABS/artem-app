package io.github.blackfishlabs.artem.ui.common;

import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public abstract class BaseFilter<T> extends Filter {

    private final RecyclerView.Adapter mAdapter;

    private final List<T> mList;

    private List<T> mOriginalList;

    public BaseFilter(
            final RecyclerView.Adapter adapter, final List<T> list, final List<T> originalList) {
        mAdapter = adapter;
        mList = list;
        mOriginalList = originalList;
    }

    @Override
    protected FilterResults performFiltering(final CharSequence prefix) {
        FilterResults results = new FilterResults();

        if (mOriginalList == null) {
            mOriginalList = new ArrayList<>(mList);
        }

        if (isEmpty(prefix)) {
            List<T> list = new ArrayList<>(mOriginalList);
            results.values = list;
            results.count = list.size();
        } else {
            final String prefixString = prefix.toString().toLowerCase();

            List<T> values = new ArrayList<>(mOriginalList);
            final List<T> newValues = new ArrayList<>();

            for (T object : values) {
                boolean contains = containsAnyProperty(prefixString, filterValues(object));

                if (contains) {
                    newValues.add(object);
                }
            }

            results.values = newValues;
            results.count = newValues.size();
        }

        return results;
    }

    @Override
    protected void publishResults(
            final CharSequence prefix, final FilterResults results) {
        mList.clear();
        //noinspection unchecked
        mList.addAll((List<T>) results.values);
        mAdapter.notifyDataSetChanged();
    }

    protected abstract String[] filterValues(T object);

    private boolean containsAnyProperty(String prefix, String... properties) {
        for (String property : properties) {
            if (!isEmpty(property) && property.trim().toLowerCase().contains(prefix)) {
                return true;
            }
        }
        return false;
    }
}


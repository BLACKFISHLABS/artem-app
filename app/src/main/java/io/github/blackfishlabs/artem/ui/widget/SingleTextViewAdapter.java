package io.github.blackfishlabs.artem.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class SingleTextViewAdapter<T> extends ArrayAdapter<T> {

    private static final int RES_SIMPLE_SPINNER_ITEM_LAYOUT = android.R.layout.simple_spinner_item;
    private static final int RES_SIMPLE_SPINNER_DROPDOWN_ITEM_LAYOUT = android.R.layout.simple_spinner_dropdown_item;

    private LayoutInflater mInflater;

    private final List<T> mItems;

    public SingleTextViewAdapter(@NonNull Context context, @NonNull List<T> list) {
        super(context, RES_SIMPLE_SPINNER_ITEM_LAYOUT, list);
        setDropDownViewResource(RES_SIMPLE_SPINNER_DROPDOWN_ITEM_LAYOUT);
        mInflater = LayoutInflater.from(context);
        mItems = list;
    }

    @NonNull
    @Override
    public View getView(
            int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent, RES_SIMPLE_SPINNER_ITEM_LAYOUT);
    }

    @Override
    public View getDropDownView(
            int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent, RES_SIMPLE_SPINNER_DROPDOWN_ITEM_LAYOUT);
    }

    private View getView(int position, View convertView, ViewGroup parent, int layoutRes) {
        SingleTextViewViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(layoutRes, parent, false);
            holder = new SingleTextViewViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SingleTextViewViewHolder) convertView.getTag();
        }

        holder.textViewNome.setText(getText(position));
        return convertView;
    }

    protected abstract String getText(final int position);

    public T getItem(@IntRange(from = 0) int position) {
        if (position >= 0 && position < mItems.size()) {
            return mItems.get(position);
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    static class SingleTextViewViewHolder {

        @BindView(android.R.id.text1)
        TextView textViewNome;

        SingleTextViewViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
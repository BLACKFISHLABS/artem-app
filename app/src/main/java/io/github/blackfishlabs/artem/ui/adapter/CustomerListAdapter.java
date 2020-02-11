package io.github.blackfishlabs.artem.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.helper.StringUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFilter;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder> implements Filterable {

    private final List<CustomerJson> mCustomers;
    private List<CustomerJson> mCustomersOriginalCopy;
    private CustomerListFilter mFilter;

    class CustomerListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_social_name)
        TextView textViewSocialName;
        @BindView(R.id.text_view_phone)
        TextView textViewPhone;
        @BindView(R.id.text_view_email)
        TextView textViewEmail;

        CustomerListViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public CustomerListAdapter(final List<CustomerJson> customers) {
        mCustomers = customers;
    }

    @Override
    public CustomerListViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customer, parent, false);
        return new CustomerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerListViewHolder holder, final int position) {
        final CustomerJson customer = mCustomers.get(position);
        final Context context = holder.itemView.getContext();

        holder.textViewSocialName.setText(customer.getName());

        if (!StringUtils.isNullOrEmpty(customer.getMainPhone())) {
            holder.textViewPhone.setText(FormattingUtils.formatPhoneNumber(customer.getMainPhone()));
        } else {
            holder.textViewPhone.setText(context.getString(R.string.customer_list_text_no_phone));
        }

        if (!StringUtils.isNullOrEmpty(customer.getEmail())) {
            holder.textViewEmail.setText(customer.getEmail());
        } else {
            holder.textViewEmail.setText(context.getString(R.string.customer_list_text_no_email));
        }
    }

    @Override
    public int getItemCount() {
        return mCustomers.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomerListFilter();
        }
        return mFilter;
    }

    public boolean isEmptyList() {
        return getItemCount() == 0;
    }

    public CustomerJson getCustomer(@IntRange(from = 0) int position) {
        if (position < 0 || position >= mCustomers.size()) {
            return null;
        }
        return mCustomers.get(position);
    }

    public int updateCustomer(final CustomerJson customer) {
        if (customer != null) {
            final int currentPosition = mCustomers.indexOf(customer);
            if (currentPosition == RecyclerView.NO_POSITION) {
                int lastPosition = mCustomers.size();
                mCustomers.add(lastPosition, customer);
                notifyItemInserted(lastPosition);
                return lastPosition;
            } else {
                mCustomers.set(currentPosition, customer);
                notifyItemChanged(currentPosition);
                return currentPosition;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public int getItemPosition(@NonNull CustomerJson customer) {
        return mCustomers.indexOf(customer);
    }

    private class CustomerListFilter extends BaseFilter<CustomerJson> {

        CustomerListFilter() {
            super(CustomerListAdapter.this, mCustomers, mCustomersOriginalCopy);
        }

        @Override
        protected String[] filterValues(final CustomerJson customer) {
            return new String[]{customer.getName()};
        }
    }
}


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

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.ui.common.BaseFilter;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> implements Filterable {

    private final List<ProductJson> mProducts;
    private List<ProductJson> mProductsOriginalCopy;
    private ProductListFilter mFilter;

    class ProductListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_product)
        TextView textViewProductName;
        @BindView(R.id.text_view_price)
        TextView textViewPrice;

        ProductListViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ProductListAdapter(final List<ProductJson> Products) {
        mProducts = Products;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ProductListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductListViewHolder holder, final int position) {
        final ProductJson product = mProducts.get(position);
        final Context context = holder.itemView.getContext();

        holder.textViewProductName.setText(product.getDescription());
        holder.textViewPrice.setText(NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(product.getValue()));
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ProductListFilter();
        }
        return mFilter;
    }

    public boolean isEmptyList() {
        return getItemCount() == 0;
    }

    public ProductJson getProduct(@IntRange(from = 0) int position) {
        if (position < 0 || position >= mProducts.size()) {
            return null;
        }
        return mProducts.get(position);
    }

    public int updateProduct(final ProductJson Product) {
        if (Product != null) {
            final int currentPosition = mProducts.indexOf(Product);
            if (currentPosition == RecyclerView.NO_POSITION) {
                int lastPosition = mProducts.size();
                mProducts.add(lastPosition, Product);
                notifyItemInserted(lastPosition);
                return lastPosition;
            } else {
                mProducts.set(currentPosition, Product);
                notifyItemChanged(currentPosition);
                return currentPosition;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public int getItemPosition(@NonNull ProductJson Product) {
        return mProducts.indexOf(Product);
    }

    private class ProductListFilter extends BaseFilter<ProductJson> {

        ProductListFilter() {
            super(ProductListAdapter.this, mProducts, mProductsOriginalCopy);
        }

        @Override
        protected String[] filterValues(final ProductJson Product) {
            return new String[]{Product.getDescription()};
        }
    }
}


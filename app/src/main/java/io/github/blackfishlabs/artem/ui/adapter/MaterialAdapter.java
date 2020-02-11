package io.github.blackfishlabs.artem.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.MaterialJson;
import io.github.blackfishlabs.artem.helper.Constants;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MyViewHolder> {

    private Context context;
    private List<MaterialJson> materials;

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.value)
        TextView value;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MaterialAdapter(Context context, List<MaterialJson> materials) {
        this.context = context;
        this.materials = materials;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MaterialJson json = materials.get(position);

        holder.description.setText(json.getDescription());
        holder.value.setText(NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(json.getValue()));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

}

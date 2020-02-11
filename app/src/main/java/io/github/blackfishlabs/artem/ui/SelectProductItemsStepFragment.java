package io.github.blackfishlabs.artem.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.MaterialJson;
import io.github.blackfishlabs.artem.helper.MoneyTextWatcher;
import io.github.blackfishlabs.artem.helper.MyDividerItemDecoration;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.RecyclerTouchListener;
import io.github.blackfishlabs.artem.ui.adapter.MaterialAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.AddedMaterialItemsEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedProductEvent;

import static java.util.Objects.requireNonNull;

public class SelectProductItemsStepFragment extends BaseFragment implements Step {

    @BindView(R.id.recycler_view_material)
    RecyclerView recyclerView;

    private List<MaterialJson> materials = Lists.newArrayList();
    private MaterialAdapter mAdapter;

    public static SelectProductItemsStepFragment newInstance() {
        return new SelectProductItemsStepFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SelectedProductEvent event = eventBus().getStickyEvent(SelectedProductEvent.class);
        if (event != null) {
            materials.clear();
            materials.addAll(event.getProduct().getItems());
        }

        mAdapter = new MaterialAdapter(getContext(), materials);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(requireNonNull(getContext()), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Remover Item"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Opções");
        builder.setItems(colors, (dialog, which) -> delete(materials.get(position)));
        builder.show();
    }

    @Override
    public VerificationError verifyStep() {
        if (materials.isEmpty()) {
            return new VerificationError(getString(R.string.all_correct_required_lists_error));
        }

        eventBus().post(AddedMaterialItemsEvent.newEvent(materials));
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(requireNonNull(getView()), error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_select_product_items;
    }

    @OnClick(R.id.add_material)
    void onClickAddMaterial() {
        showDialog();
    }

    private void showDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.material_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setView(view);

        final TextInputLayout inputDescription = view.findViewById(R.id.input_layout_dialog_description);
        final TextInputLayout inputValue = view.findViewById(R.id.input_layout_dialog_value);

        requireNonNull(inputValue.getEditText()).addTextChangedListener(new MoneyTextWatcher(inputValue.getEditText()));

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.lbl_new_material_title));

        inputDescription.requestFocus();
        requireNonNull(inputValue.getEditText()).setText(String.valueOf(0.0));

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("salvar", (dialogBox, id) -> {
                })
                .setNegativeButton("cancelar", (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (TextUtils.isEmpty(requireNonNull(inputDescription.getEditText()).getText().toString())) {
                Toast.makeText(getContext(), "Digite uma descrição!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (NumberUtils.toCurrencyBigDecimal(inputValue.getEditText().getText().toString()).signum() <= 0) {
                Toast.makeText(getContext(), "Digite uma valor!", Toast.LENGTH_SHORT).show();
                return;
            }

            MaterialJson material = new MaterialJson();
            material.withId(UUID.randomUUID().toString());
            material.withDescription(inputDescription.getEditText().getText().toString());
            material.withValue(NumberUtils.toCurrencyBigDecimal(inputValue.getEditText().getText().toString()));

            saveData(material);

            loadData();
            alertDialog.dismiss();
        });
    }

    private void saveData(MaterialJson obj) {
        materials.add(obj);
    }

    private void delete(MaterialJson obj) {
        materials.remove(obj);
        loadData();
    }

    private void loadData() {
        mAdapter.notifyDataSetChanged();
    }
}

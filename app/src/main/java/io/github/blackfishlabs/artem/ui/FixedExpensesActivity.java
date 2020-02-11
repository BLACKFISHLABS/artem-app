package io.github.blackfishlabs.artem.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.AtelierFixedExpenseEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierFixedExpenseJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.helper.MoneyTextWatcher;
import io.github.blackfishlabs.artem.helper.MyDividerItemDecoration;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.helper.RecyclerTouchListener;
import io.github.blackfishlabs.artem.ui.adapter.FixedExpensesAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class FixedExpensesActivity extends BaseActivity {

    public static final String TAG = FixedExpensesActivity.class.getName();

    @BindView(R.id.recycler_view_fixed_expenses)
    RecyclerView recyclerView;
    @BindView(R.id.total_value)
    TextView calculatedTotal;

    private FixedExpensesAdapter mAdapter;
    private List<AtelierFixedExpenseJson> expenses = Lists.newArrayList();

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_fixed_expenses;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsSubActivity();

        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de adicionar suas despesas fixas!", Toast.LENGTH_LONG).show();
            finish();
        }

        mAdapter = new FixedExpensesAdapter(this, expenses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        loadData();
    }

    private void loadData() {
        expenses.clear();
        double total = 0.0;

        RealmResults<AtelierFixedExpenseEntity> all = LocalDataInjector.getAtelierFixedExpenseRealmDB(this).getAll();

        for (AtelierFixedExpenseEntity obj : all) {
            expenses.add(LocalDataInjector.getAtelierFixedExpenseMapper().toViewObject(obj));
            total += obj.getValue();
        }

        calculatedTotal.setText(NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(total));

        mAdapter.notifyDataSetChanged();
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Editar", "Remover"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                showDialog(true, expenses.get(position));
            } else {
                delete(expenses.get(position).getId());
            }
        });
        builder.show();
    }

    @OnClick(R.id.add_fixed_expenses)
    void onClickAddInvestment(View v) {
        showDialog(false, null);
    }

    private void showDialog(final boolean shouldUpdate, final AtelierFixedExpenseJson loadedObj) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.fixed_expense_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(view);

        final TextInputLayout inputDescription = view.findViewById(R.id.input_layout_dialog_description);
        final TextInputLayout inputValue = view.findViewById(R.id.input_layout_dialog_value);

        requireNonNull(inputValue.getEditText()).addTextChangedListener(new MoneyTextWatcher(inputValue.getEditText()));

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_fixed_expense_title) : getString(R.string.lbl_edit_fixed_expense_title));

        if (shouldUpdate && loadedObj != null) {
            requireNonNull(inputDescription.getEditText()).setText(loadedObj.getDescription());
            requireNonNull(inputValue.getEditText()).setText(NumberUtils.currencyToString(loadedObj.getValue()));
        } else {
            inputDescription.requestFocus();
            requireNonNull(inputValue.getEditText()).setText(NumberUtils.currencyToString(BigDecimal.ZERO));
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "atualizar" : "salvar", (dialogBox, id) -> {
                })
                .setNegativeButton("cancelar", (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (TextUtils.isEmpty(requireNonNull(inputDescription.getEditText()).getText().toString())) {
                Toast.makeText(this, "Digite uma descrição!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (NumberUtils.toCurrencyBigDecimal(inputValue.getEditText().getText().toString()).signum() <= 0) {
                Toast.makeText(this, "Digite uma valor!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (shouldUpdate && loadedObj != null) {
                loadedObj.withDescription(inputDescription.getEditText().getText().toString());
                loadedObj.withValue(NumberUtils.toCurrencyBigDecimal(inputValue.getEditText().getText().toString()));

                saveData(loadedObj);
            } else {
                AtelierFixedExpenseJson investment = new AtelierFixedExpenseJson()
                        .withId(UUID.randomUUID().toString())
                        .withCompanyId(PrefUtils.getAtelierKey(this))
                        .withDescription(inputDescription.getEditText().getText().toString())
                        .withValue(NumberUtils.toCurrencyBigDecimal(inputValue.getEditText().getText().toString()));

                saveData(investment);
            }

            loadData();
            alertDialog.dismiss();
        });
    }

    private void saveData(AtelierFixedExpenseJson obj) {
        LocalDataInjector.getAtelierFixedExpenseRealmDB(this).store(
                Collections.singletonList(LocalDataInjector.getAtelierFixedExpenseMapper().toEntity(obj)));
    }

    private void delete(String id) {
        LocalDataInjector.getAtelierFixedExpenseRealmDB(this).delete(id);
        loadData();
    }
}

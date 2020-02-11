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
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.AtelierInvestmentEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierInvestmentJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.helper.MoneyTextWatcher;
import io.github.blackfishlabs.artem.helper.MyDividerItemDecoration;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.helper.RecyclerTouchListener;
import io.github.blackfishlabs.artem.ui.adapter.InvestmentAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class InvestmentActivity extends BaseActivity {

    public static final String TAG = InvestmentActivity.class.getName();

    @BindView(R.id.recycler_view_investment)
    RecyclerView recyclerView;
    @BindView(R.id.total_value)
    TextView calculatedTotal;
    @BindView(R.id.recover_value)
    TextView recoverValue;
    @BindView(R.id.resume)
    TextView resume;

    private InvestmentAdapter mAdapter;
    private List<AtelierInvestmentJson> investments = Lists.newArrayList();

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_investment;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsSubActivity();

        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de adicionar seus investimentos!", Toast.LENGTH_LONG).show();
            finish();
        }

        mAdapter = new InvestmentAdapter(this, investments);
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

        recoverValue.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Opções");
            CharSequence[] choices = new CharSequence[]{"3 meses", "6 meses", "9 meses", "12 meses", "24 meses", "36 meses", "48 meses"};

            alert.setSingleChoiceItems(choices, 0, (dialogInterface, i) -> {
                CharSequence charSequence = Lists.newArrayList(choices).get(i);
                int months = NumberUtils.toInt(charSequence.toString().replace("meses", "").trim());
                PrefUtils.storeMonthsRecoverValueInvestment(this, months);
            });

            alert.setCancelable(true);
            alert.setPositiveButton("ESCOLHER PLANO DE RETORNO", (dialogInterface, i) -> {
                loadData();
            });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        });

        loadData();
    }

    private void loadData() {
        investments.clear();
        BigDecimal total = BigDecimal.ZERO;
        int monthsRecoverValueInvestment = PrefUtils.getMonthsRecoverValueInvestment(this);

        RealmResults<AtelierInvestmentEntity> all = LocalDataInjector.getAtelierInvestmentRealmDB(this).getAll();

        for (AtelierInvestmentEntity obj : all) {
            investments.add(LocalDataInjector.getAtelierInvestmentMapper().toViewObject(obj));
            total = total.add(BigDecimal.valueOf(obj.getValue()));
        }

        calculatedTotal.setText(NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(total));

        if (monthsRecoverValueInvestment == 0) {
            recoverValue.setText(getString(R.string.lbl_recover_value));
            resume.setText(getString(R.string.lbl_resume));
        } else {
            recoverValue.setText("Planejo recuperar este valor em: ".concat(String.valueOf(monthsRecoverValueInvestment)).concat(" meses"));

            BigDecimal round = total.divide(BigDecimal.valueOf(monthsRecoverValueInvestment), RoundingMode.UP).setScale(0, BigDecimal.ROUND_UP);
            resume.setText("Aproximadamente ".concat(NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(round)).concat(" ao mês"));
        }

        mAdapter.notifyDataSetChanged();
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Editar", "Remover"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                showDialog(true, investments.get(position));
            } else {
                delete(investments.get(position).getId());
            }
        });
        builder.show();
    }

    @OnClick(R.id.add_investment)
    void onClickAddInvestment() {
        showDialog(false, null);
    }

    private void showDialog(final boolean shouldUpdate, final AtelierInvestmentJson loadedObj) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.investment_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(view);

        final TextInputLayout inputDescription = view.findViewById(R.id.input_layout_dialog_description);
        final TextInputLayout inputValue = view.findViewById(R.id.input_layout_dialog_value);

        requireNonNull(inputValue.getEditText()).addTextChangedListener(new MoneyTextWatcher(inputValue.getEditText()));

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_investment_title) : getString(R.string.lbl_edit_investment_title));

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
                AtelierInvestmentJson investment = new AtelierInvestmentJson()
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

    private void saveData(AtelierInvestmentJson obj) {
        LocalDataInjector.getAtelierInvestmentRealmDB(this).store(
                Collections.singletonList(LocalDataInjector.getAtelierInvestmentMapper().toEntity(obj)));
    }

    private void delete(String id) {
        LocalDataInjector.getAtelierInvestmentRealmDB(this).delete(id);
        loadData();
    }
}

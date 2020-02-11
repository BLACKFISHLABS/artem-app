package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.json.AtelierFixedExpenseJson;
import io.github.blackfishlabs.artem.domain.json.AtelierInvestmentJson;
import io.github.blackfishlabs.artem.domain.json.AtelierJson;
import io.github.blackfishlabs.artem.domain.json.AtelierValuePerHourJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;

import static android.text.TextUtils.isEmpty;

public class AtelierFragment extends BaseFragment {

    @BindView(R.id.info_atelier)
    LinearLayout btnInfoAtelier;
    @BindView(R.id.info_investment)
    LinearLayout btnInfoInvestment;
    @BindView(R.id.info_value_per_hour)
    LinearLayout btnInfoValuePerHour;
    @BindView(R.id.info_fixed_spending)
    LinearLayout btnInfoFixedSpending;
    @BindView(R.id.resume)
    TextView resume;

    public static final String TAG = AtelierFragment.class.getName();

    public static AtelierFragment newInstance() {
        return new AtelierFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_atelier;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        btnInfoAtelier.setOnClickListener(v -> navigate().toEditInfoAtelier());
        btnInfoInvestment.setOnClickListener(v -> navigate().toEditInfoInvestment());
        btnInfoValuePerHour.setOnClickListener(v -> navigate().toEditInfoValuePerHour());
        btnInfoFixedSpending.setOnClickListener(v -> navigate().toEditInfoFixedSpending());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadData();
    }

    private void loadData() {
        String atelierName = "";
        String valuePerHour = "";
        String valueInvestment = "";
        String valueExpense = "";

        if (PrefUtils.getAtelierKey(getHostActivity()) != null) {
            AtelierJson atelierJson = loadAtelierInfo();
            atelierName = atelierJson.getName();

            if (hasAtelierValuePerHourInfo()) {
                AtelierValuePerHourJson atelierValuePerHourJson = loadAtelierValuePerHourInfo();

                valuePerHour = NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(atelierValuePerHourJson.getValue());
            }

            if (hasInvestmentInfo()) {
                List<AtelierInvestmentJson> investments = loadInvestmentInfo();
                BigDecimal total = BigDecimal.ZERO;
                for (AtelierInvestmentJson investment : investments) {
                    total = total.add(investment.getValue());
                }
                valueInvestment = NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(total);
            }

            if (hasFixedExpenseInfo()) {
                List<AtelierFixedExpenseJson> expenses = loadFixedExpenseInfo();
                BigDecimal total = BigDecimal.ZERO;
                for (AtelierFixedExpenseJson expense : expenses) {
                    total = total.add(expense.getValue());
                }
                valueExpense = NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(total);
            }
        }

        buildInformation(atelierName, valueInvestment, valuePerHour, valueExpense);
    }

    private AtelierJson loadAtelierInfo() {
        return LocalDataInjector
                .getAtelierMapper()
                .toViewObject(LocalDataInjector
                        .getAtelierRealmDB(getHostActivity())
                        .findById(PrefUtils.getAtelierKey(getHostActivity())).first());
    }

    private AtelierValuePerHourJson loadAtelierValuePerHourInfo() {
        return LocalDataInjector
                .getAtelierValuePerHourMapper()
                .toViewObject(LocalDataInjector
                        .getAtelierValuePerHourDB(getHostActivity())
                        .findByOwner(PrefUtils.getAtelierKey(getHostActivity())).first());
    }

    private List<AtelierInvestmentJson> loadInvestmentInfo() {
        return LocalDataInjector
                .getAtelierInvestmentMapper()
                .toViewObjects(LocalDataInjector
                        .getAtelierInvestmentRealmDB(getHostActivity())
                        .findByOwner(PrefUtils.getAtelierKey(getHostActivity())));
    }

    private List<AtelierFixedExpenseJson> loadFixedExpenseInfo() {
        return LocalDataInjector
                .getAtelierFixedExpenseMapper()
                .toViewObjects(LocalDataInjector
                        .getAtelierFixedExpenseRealmDB(getHostActivity())
                        .findByOwner(PrefUtils.getAtelierKey(getHostActivity())));
    }

    private boolean hasAtelierValuePerHourInfo() {
        return !LocalDataInjector.getAtelierValuePerHourDB(getHostActivity()).getAll().isEmpty();
    }

    private boolean hasInvestmentInfo() {
        return !LocalDataInjector.getAtelierInvestmentRealmDB(getHostActivity()).getAll().isEmpty();
    }

    private boolean hasFixedExpenseInfo() {
        return !LocalDataInjector.getAtelierFixedExpenseRealmDB(getHostActivity()).getAll().isEmpty();
    }

    private void buildInformation(String... strings) {
        resume.setText((isEmpty(strings[0]) ? "● Preencha os dados do Ateliê" : "● ".concat(strings[0]))
                .concat("\n")
                .concat(isEmpty(strings[1]) ? "● Informe seus Investimentos" : "● Total dos Investimentos: ".concat(strings[1]))
                .concat("\n")
                .concat(isEmpty(strings[2]) ? "● Calcule o Valor da sua Hora" : "● Valor da Minha Hora: ".concat(strings[2]))
                .concat("\n")
                .concat(isEmpty(strings[3]) ? "● Lance suas Despesas Fixas Mensais" : "● Despesas Fixas Mensais: ".concat(strings[3])));
    }
}

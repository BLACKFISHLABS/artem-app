package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;

public class AdsFragment extends BaseFragment {

    public static final String TAG = AdsFragment.class.getName();

    @BindView(R.id.goToStorePagSeguro)
    ImageView imvPagSeguro;

    public static AdsFragment newInstance() {
        return new AdsFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_ads;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        imvPagSeguro.setOnClickListener(v -> navigate().toWebSite("https://loja.pagseguro.uol.com.br/?cm=V0KNw9qO"));

        return view;
    }
}

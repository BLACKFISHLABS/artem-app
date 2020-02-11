package io.github.blackfishlabs.artem.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;

public class AboutFragment extends BaseFragment {

    public static final String TAG = AboutFragment.class.getName();

    @BindView(R.id.bfLabs)
    TextView mGoToWebSite;
    @BindView(R.id.btnSendMail)
    Button btnEmailMe;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_about;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        mGoToWebSite.setOnClickListener(v -> navigate().toWebSite("http://blackfishlabs.github.io"));
        btnEmailMe.setOnClickListener(v -> sendMail());

        return view;
    }

    private void sendMail() {
        String subject = "[ARTEM - CONTATO] - Olá Equipe BLACKFISH LABS";
        String message = "Digite aqui sua dúvida ou sugestão de forma detalhada...";

        String mailto = "mailto:" + "dev.blackfishlabs@gmail.com" +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(message);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }

    }
}


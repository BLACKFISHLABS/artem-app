package io.github.blackfishlabs.artem.data;

import io.github.blackfishlabs.artem.data.remote.PostalCodeApi;
import io.github.blackfishlabs.artem.ui.PresentationInjector;

public class RemoteDataInjector {

    private RemoteDataInjector() {/* No instances */}

    public static PostalCodeApi providePostalCodeApi() {
        return PresentationInjector.provideRetrofit().create(PostalCodeApi.class);
    }

}

package io.github.blackfishlabs.artem.data.remote;

import io.github.blackfishlabs.artem.domain.json.PostalCode;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PostalCodeApi {

    @GET("http://api.postmon.com.br/v1/cep/{postalCode}")
    Single<PostalCode> get(@Path("postalCode") String postalCode);
}

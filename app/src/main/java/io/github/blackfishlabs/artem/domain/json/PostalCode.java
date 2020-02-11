package io.github.blackfishlabs.artem.domain.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostalCode {

    @Expose
    @SerializedName("bairro")
    public String district;

    @Expose
    @SerializedName("cidade")
    public String cityName;

    @Expose
    @SerializedName(value = "logradouro", alternate = "endereco")
    public String address;

    @Expose
    @SerializedName("estado_info")
    public StateInfo stateInfo;

    @Expose
    @SerializedName("cep")
    public String postalCode;

    @Expose
    @SerializedName("cidade_info")
    public CityInfo cityInfo;

    @Expose
    @SerializedName("estado")
    public String federativeUnit;

    public class StateInfo {
        @Expose
        @SerializedName("area_km2")
        public String area;

        @Expose
        @SerializedName("codigo_ibge")
        public String codeIbge;

        @Expose
        @SerializedName("nome")
        public String name;
    }

    public class CityInfo {
        @Expose
        @SerializedName("area_km2")
        public String area;

        @Expose
        @SerializedName("codigo_ibge")
        public String codeIbge;
    }
}

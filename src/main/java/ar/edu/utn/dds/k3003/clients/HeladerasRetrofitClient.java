package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HeladerasRetrofitClient {
  @GET("viandas/{qr}")
  Call<HeladeraDTO> get(@Path("qr") String qr);
}

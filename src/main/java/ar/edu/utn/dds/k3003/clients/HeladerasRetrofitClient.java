package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface HeladerasRetrofitClient {
  @GET("heladeras/{id}/temperaturas")
  Call<List<TemperaturaDTO>> get(@Path("id") Integer id);
}

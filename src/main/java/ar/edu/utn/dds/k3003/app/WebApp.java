package ar.edu.utn.dds.k3003.app;


import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controller.ViandaController;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.metric.DDMetricsUtils;
import java.util.concurrent.atomic.AtomicInteger;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
  public static void main(String[] args) {
    var fachada = new Fachada();
    var objectMapper = createObjectMapper();
    log.info("starting up the server");

    final var metricsUtils = new DDMetricsUtils("transferencias");
    final var registry = metricsUtils.getRegistry();

    // Metricas
    final var myGauge = registry.gauge("dds.unGauge", new AtomicInteger(0));
    Integer port = Integer.parseInt(System.getProperty("port","8080"));

    final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

    final var app = Javalin.create(config -> {
      config.registerPlugin(micrometerPlugin);
    });

    myGauge.set(1);
    //Javalin app = Javalin.create().start(port);
    var viandaController = new ViandaController(fachada);
    fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));


    app.post("/viandas",viandaController::agregar);
    app.get("/viandas",viandaController::listar);
    app.get("/viandas/search/findByColaboradorIdAndAnioAndMes",viandaController::buscarPorColaboradorIdMesYAnio);
    app.get("/viandas/{qr}",viandaController::buscarPorQr);
    app.get("/viandas/{qr}/vencida",viandaController::verificarVencimiento);
    app.patch("/viandas/{qr}",viandaController::modificarHeladera);
    app.patch("/viandas/{qr}/estado",viandaController::modificarEstado);
  }

  public static ObjectMapper createObjectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    objectMapper.setDateFormat(sdf);
    return objectMapper;
  }
}

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
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
  private static final String TOKEN = "token";
  public static void main(String[] args) {
    var fachada = new Fachada();
    var objectMapper = createObjectMapper();
    log.info("starting up the server");

    final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    Integer port = Integer.parseInt(System.getProperty("port","8080"));
    // agregar aquí cualquier tag que aplique a todas las métrivas de la app
    // (e.g. EC2 region, stack, instance id, server group)
    registry.config().commonTags("app", "metrics-sample");

    // agregamos a nuestro reigstro de métricas todo lo relacionado a infra/tech
    // de la instancia y JVM
    try (var jvmGcMetrics = new JvmGcMetrics();
         var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
      jvmGcMetrics.bindTo(registry);
      jvmHeapPressureMetrics.bindTo(registry);
    }
    new JvmMemoryMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new FileDescriptorMetrics().bindTo(registry);

    // agregamos métricas custom de nuestro dominio
    Gauge.builder("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", () -> (int)(Math.random() * 1000))
        .description("Random number from My-Application.")
        .strongReference(true)
        .register(registry);

    // seteamos el registro dentro de la config de Micrometer
    final var micrometerPlugin =
        new MicrometerPlugin(config -> config.registry = registry);
/*
    final var metricsUtils = new DDMetricsUtils("transferencias");
    final var registry = metricsUtils.getRegistry();

    // Metricas
    final var myGauge = registry.gauge("dds.unGauge", new AtomicInteger(0));


    final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

    final var app = Javalin.create(config -> {
      config.registerPlugin(micrometerPlugin);
    }).start(port);

    myGauge.set(1);
*/

    //Javalin app = Javalin.create().start(port);
    Javalin app = Javalin.create(config -> { config.registerPlugin(micrometerPlugin); }).start(port);

    var viandaController = new ViandaController(fachada);
    fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));


    app.post("/viandas",viandaController::agregar);
    app.get("/viandas",viandaController::listar);
    app.get("/viandas/search/findByColaboradorIdAndAnioAndMes",viandaController::buscarPorColaboradorIdMesYAnio);
    app.get("/viandas/{qr}",viandaController::buscarPorQr);
    app.get("/viandas/{qr}/vencida",viandaController::verificarVencimiento);
    app.patch("/viandas/{qr}",viandaController::modificarHeladera);
    app.patch("/viandas/{qr}/estado",viandaController::modificarEstado);
    app.get("/metrics",
        ctx -> {
          // chequear el header de authorization y chequear el token bearer
          // configurado
          var auth = ctx.header("Authorization");

          if (auth != null && auth.intern() == "Bearer " + TOKEN) {
            ctx.contentType("text/plain; version=0.0.4")
                .result(registry.scrape());
          } else {
            // si el token no es el apropiado, devolver error,
            // desautorizado
            // este paso es necesario para que Grafana online
            // permita el acceso
            ctx.status(401).json("unauthorized access");
          }
        });
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

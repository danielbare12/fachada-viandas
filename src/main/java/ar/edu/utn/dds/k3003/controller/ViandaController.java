package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.metric.DDMetricsUtils;
import ar.edu.utn.dds.k3003.model.HeladeraDestino;
import ar.edu.utn.dds.k3003.model.Respuesta;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
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

public class ViandaController {

  private final Fachada fachada;
  private final Counter viandasCounter;
  private final Gauge viandaGauge;
  private final DistributionSummary summary;
  // Metricas

  // Instancia de StatsDClient
  private static final StatsDClient statsd = new NonBlockingStatsDClient(
      "my.prefix",                  // Prefijo para las métricas
      "localhost",                  // Dirección del agente Datadog
      8125           // Puerto donde escucha el agente
  );

  public ViandaController(Fachada fachada, Counter viandasCounter,Gauge viandaGauge,DistributionSummary summary){

    this.fachada = fachada;
    this.viandasCounter = viandasCounter;
    this.viandaGauge = viandaGauge;
    this.summary = summary;

  }

  public void agregar(Context context){

    final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    ViandaDTO viandaDto = context.bodyAsClass(ViandaDTO.class);

    var viandaDtoRta = this.fachada.agregar(viandaDto);

    registry.config().commonTags("app", "metrics-sample");

    viandasCounter.increment();
    /*
    Gauge.builder("viandaAgregada", () -> (int)(Math.random() * 1000))
        .description("Random number from My-Application.")
        .strongReference(true)
        .register(registry);
      */
    summary.record(1.0);


    context.json(viandaDtoRta);
    context.status(HttpStatus.CREATED);
  }

  public void buscarPorColaboradorIdMesYAnio(Context context){
    var colabId = context.queryParamAsClass("colaboradorId",Long.class).get();
    var anio = context.queryParamAsClass("anio",Integer.class).get();
    var mes = context.queryParamAsClass("mes",Integer.class).get();
    var ViandaDtoRta = this.fachada.viandasDeColaborador(colabId,mes,anio);
    context.json(ViandaDtoRta);
  }

  public void buscarPorQr(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    var ViandaDtoRta = this.fachada.buscarXQR(qr);
    context.json(ViandaDtoRta);
  }

  public void verificarVencimiento(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    var respuesta = new Respuesta(this.fachada.evaluarVencimiento(qr));
    context.json(respuesta);
  }

  public void modificarHeladera(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    HeladeraDestino heladera = context.bodyAsClass(HeladeraDestino.class);
    var ViandaDtoRta = this.fachada.modificarHeladera(qr,heladera.getHeladeraDestino());
    context.json(ViandaDtoRta);
  }

  public void modificarEstado(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    EstadoViandaEnum estado = context.bodyAsClass(EstadoViandaEnum.class);
    var respuesta = this.fachada.modificarEstado(qr,estado);
    context.json(respuesta);
  }

  public void listar(Context context){
    var ViandaDtoRta = this.fachada.viandasLista();
    context.json(ViandaDtoRta);
  }
}

package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.HeladeraDestino;
import ar.edu.utn.dds.k3003.model.Respuesta;
import io.javalin.http.HttpStatus;
import io.javalin.http.Context;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class ViandaController {
  private final Fachada fachada;

  // Instancia de StatsDClient
  private static final StatsDClient statsd = new NonBlockingStatsDClient(
      "my.prefix",                  // Prefijo para las métricas
      "localhost",                  // Dirección del agente Datadog
      8080           // Puerto donde escucha el agente
  );

  public ViandaController(Fachada fachada){
    this.fachada = fachada;
  }

  public void agregar(Context context){
    ViandaDTO viandaDto = context.bodyAsClass(ViandaDTO.class);
    var viandaDtoRta = this.fachada.agregar(viandaDto);
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    statsd.incrementCounter("viandas_agregadas");
    System.out.println("!!!!!???????????????????????????!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    statsd.gauge("viandas_agregadas", 1);
    statsd.stop();
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

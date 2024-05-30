package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;

import java.time.LocalDateTime;

import static ar.edu.utn.dds.k3003.app.WebApp.configureObjectMapper;

public class ViandaTestServer {

    public static void main(String[] args) throws Exception {

        var env = System.getenv();

        var port = Integer.parseInt(env.getOrDefault("PORT", "8081"));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);

        app.get("/viandas/{qr}", ViandaTestServer::obtenerVianda);
        app.post("/pepe", ViandaTestServer::trasladoTest);
    }

    private static void trasladoTest(Context context) {

        var trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
        trasladoDTO.setId(14L);
        context.json(trasladoDTO);
    }

    private static void obtenerVianda(Context context) {

        var qr = context.pathParam("qr");
        if (qr.equals("unQRQueExiste")) {
            var viandaDTO1 = new ViandaDTO(qr, LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);
            viandaDTO1.setId(14L);
            context.json(viandaDTO1);
        } else {
            context.result("Vianda no encontrada: " + qr);
            context.status(HttpStatus.NOT_FOUND);
        }
    }
}

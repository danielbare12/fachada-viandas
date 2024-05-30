package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;

import java.time.LocalDateTime;

public class TrasladoMapper {

    public TrasladoDTO map(Traslado traslado) {
        TrasladoDTO trasladoDTO = new TrasladoDTO(traslado.getQrVianda(), traslado.getEstado(),
                traslado.getFechaTraslado(), traslado.getRuta().getHeladeraIdOrigen(),
                traslado.getRuta().getHeladeraIdDestino());

        trasladoDTO.setId(traslado.getId());
        trasladoDTO.setColaboradorId(traslado.getRuta().getColaboradorId());


        return trasladoDTO;
    }
}

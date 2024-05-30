package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.model.Ruta;

public class RutaMapper {

    public RutaDTO map(Ruta ruta){
        RutaDTO rutaDTO = new RutaDTO(ruta.getColaboradorId(),
                ruta.getHeladeraIdOrigen(),
                ruta.getHeladeraIdDestino());
        rutaDTO.setId(ruta.getId());
        return rutaDTO;
    }
}

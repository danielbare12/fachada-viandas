package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Ruta;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class RutaRepository {

    private static AtomicLong seqId = new AtomicLong();
    private Collection<Ruta> rutas;

    public RutaRepository() {
        this.rutas = new ArrayList<>();
    }

    public Ruta save(Ruta ruta) {
        if (Objects.isNull(ruta.getId())) {
            ruta.setId(seqId.getAndIncrement());
            this.rutas.add(ruta);
        }
        return ruta;
    }

    public Ruta findById(Long id) {
        Optional<Ruta> first = this.rutas.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay una ruta de id: %s", id)
        ));
    }

    public List<Ruta> findByHeladeras(Integer heladeraOrigen, Integer heladeraDestino) {
        return this.rutas.stream().filter(x -> x.getHeladeraIdOrigen().equals(heladeraOrigen) &&
                x.getHeladeraIdDestino().equals(heladeraDestino)
        ).toList();
    }
}

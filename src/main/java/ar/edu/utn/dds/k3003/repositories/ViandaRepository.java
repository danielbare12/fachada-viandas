package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ViandaRepository {
  private static AtomicLong seqId = new AtomicLong();
  private Collection<Vianda> viandas;

  public ViandaRepository() {
    this.viandas = new ArrayList<>();
  }

  public Vianda save(Vianda vianda){
    if(vianda.getId() < 0){
      vianda.setId(seqId.getAndIncrement());
      this.viandas.add(vianda);
    }

    return vianda;
  }

  public Vianda findById(Long id){
    Optional<Vianda> first = this.viandas.stream().filter(v -> v.getId() == id).findFirst();
    return first.orElseThrow(() -> new NoSuchElementException(
        String.format("No hay una ruta de id: %s", id)
    ));
  }

  public Vianda findByQr(String qr){
    Optional<Vianda> first = this.viandas.stream().filter(v -> v.getCodigoQR().equals(qr)).findFirst();
    return first.orElseThrow(() -> new NoSuchElementException(
        String.format("No hay una ruta de qr: %s", qr)
    ));
  }

  public List<Vianda> findByColaborador(Long idColab, Integer mes, Integer anio){
    return this.viandas.stream().filter(v -> v.getColaboradorId() == idColab
    && mes == v.getFechaElaboracion().getMonthValue()
    && anio == v.getFechaElaboracion().getYear()).toList();
  }
}

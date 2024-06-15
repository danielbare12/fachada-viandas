package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import lombok.Getter;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaViandas{
  private final ViandaMapper viandaMapper = new ViandaMapper();;
  private final ViandaRepository viandaRepository = new ViandaRepository();
  private FachadaViandas fachadaViandas;
  private FachadaHeladeras fachadaHeladeras;
  public Fachada(){

  }

  @Override
  public ViandaDTO agregar(ViandaDTO viandaDTO) throws NoSuchElementException {

    Vianda vianda = new Vianda(viandaDTO.getCodigoQR(),viandaDTO.getColaboradorId(),
        viandaDTO.getHeladeraId(),viandaDTO.getEstado());
    vianda = this.viandaRepository.save(vianda);
    return viandaMapper.map(vianda);
  }

  @Override
  public ViandaDTO modificarEstado(String s, EstadoViandaEnum estadoViandaEnum) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    vianda.setEstado(estadoViandaEnum);
    return viandaMapper.map(vianda);
  }

  @Override
  public List<ViandaDTO> viandasDeColaborador(Long aLong, Integer integer, Integer integer1) throws NoSuchElementException {
    List<Vianda> viandas = this.viandaRepository.findByColaborador(aLong,integer,integer1);
    return this.viandaMapper.mapAll(viandas);
  }

  @Override
  public ViandaDTO buscarXQR(String s) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    return viandaMapper.map(vianda);
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
    this.fachadaHeladeras = fachadaHeladeras;
  }

  @Override
  public boolean evaluarVencimiento(String s) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    List<TemperaturaDTO> temperaturaDTOList = this.fachadaHeladeras.obtenerTemperaturas(vianda.getHeladeraId());
    boolean band = temperaturaDTOList.stream().anyMatch(t -> t.getTemperatura() >= 5);
    return band;
  }

  @Override
  public ViandaDTO modificarHeladera(String s, int i) {
    Vianda vianda = this.viandaRepository.findByQr(s);
    vianda.setHeladeraId(i);
    this.viandaRepository.update(vianda);
    return viandaMapper.map(vianda);
  }

  public List<ViandaDTO> viandasLista() throws NoSuchElementException {
    List<Vianda> viandas = this.viandaRepository.list();
    return this.viandaMapper.mapAll(viandas);
  }

}

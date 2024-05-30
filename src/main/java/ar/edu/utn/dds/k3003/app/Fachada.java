package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.repositories.RutaMapper;
import ar.edu.utn.dds.k3003.repositories.RutaRepository;
import ar.edu.utn.dds.k3003.repositories.TrasladoMapper;
import ar.edu.utn.dds.k3003.repositories.TrasladoRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaLogistica {

  private final RutaRepository rutaRepository;
  private final RutaMapper rutaMapper;
  private final TrasladoRepository trasladoRepository;
  private final TrasladoMapper trasladoMapper;
  private FachadaViandas fachadaViandas;

  public Fachada() {
    this.rutaRepository = new RutaRepository();
    this.rutaMapper = new RutaMapper();
    this.trasladoMapper = new TrasladoMapper();
    this.trasladoRepository = new TrasladoRepository();
  }

  @Override
  public RutaDTO agregar(RutaDTO rutaDTO) {
    var ruta =
        new Ruta(
            rutaDTO.getColaboradorId(),
            rutaDTO.getHeladeraIdOrigen(),
            rutaDTO.getHeladeraIdDestino());
    ruta = this.rutaRepository.save(ruta);
    return rutaMapper.map(ruta);
  }

  @Override
  public TrasladoDTO buscarXId(Long aLong) throws NoSuchElementException {
    return trasladoMapper.map(trasladoRepository.findById(aLong));
  }

  @Override
  public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException {

    var viandaDTO = fachadaViandas.buscarXQR(trasladoDTO.getQrVianda());

    var rutasPosibles =
        this.rutaRepository.findByHeladeras(
            trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());

    if (rutasPosibles.isEmpty()) {
      throw new TrasladoNoAsignableException(
          String.format(
              "No hay rutas para ir de %s a %s ",
              trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino()));
    }

    Collections.shuffle(rutasPosibles);
    var ruta = rutasPosibles.get(0);

    var traslado =
        trasladoRepository.save(
            new Traslado(
                viandaDTO.getCodigoQR(),
                ruta,
                EstadoTrasladoEnum.ASIGNADO,
                trasladoDTO.getFechaTraslado()));

    return this.trasladoMapper.map(traslado);
  }

  @Override
  public List<TrasladoDTO> trasladosDeColaborador(Long aLong, Integer integer, Integer integer1) {
    return null;
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {}

  @Override
  public void setViandasProxy(FachadaViandas fachadaViandas) {
    this.fachadaViandas = fachadaViandas;
  }

  @Override
  public void trasladoRetirado(Long aLong) {}

  @Override
  public void trasladoDepositado(Long aLong) {}
}

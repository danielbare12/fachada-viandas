package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
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


    private Fachada() {
        this.rutaRepository = new RutaRepository();
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository();
    }

    /*
    *  ViandaDTO t =
        new ViandaDTO(
            QR_VIANDA,
            LocalDateTime.now(),
            EstadoViandaEnum.PREPARADA,
            15L,
            LogisticaTest.HELADERA_ORIGEN);
    when(fachadaViandas.buscarXQR(QR_VIANDA)).thenReturn(t);
    var agregar = instancia.agregar(new RutaDTO(14L, LogisticaTest.HELADERA_ORIGEN, 2));
    instancia.agregar(new RutaDTO(15L, LogisticaTest.HELADERA_ORIGEN, 3));
    assertNotNull(agregar.getId(), "la ruta una vez agregada deberia tener un identificador");

    var traslado = new TrasladoDTO(QR_VIANDA, LogisticaTest.HELADERA_ORIGEN, 2);
    var trasladoDTO = instancia.asignarTraslado(traslado);

    assertEquals(
        EstadoTrasladoEnum.ASIGNADO,
        trasladoDTO.getStatus(),
        "el estado de un traslado debe figurar como asignado luego de una asignación");
    assertEquals(14L, trasladoDTO.getColaboradorId(), "No se asigno el colaborador correcto");*/

    @Override
    public RutaDTO agregar(RutaDTO rutaDTO) {
        Ruta ruta = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
        ruta = this.rutaRepository.save(ruta);
        return rutaMapper.map(ruta);
    }

    @Override
    public TrasladoDTO buscarXId(Long aLong) throws NoSuchElementException {
        return null;
    }

    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException {

        ViandaDTO viandaDTO = fachadaViandas.buscarXQR(trasladoDTO.getQrVianda());

        List<Ruta> rutasPosibles = this.rutaRepository.findByHeladeras(trasladoDTO.getHeladeraOrigen(),
                trasladoDTO.getHeladeraDestino());

        Collections.shuffle(rutasPosibles);
        Ruta ruta = rutasPosibles.get(0);

        Traslado traslado = trasladoRepository.save(new Traslado(viandaDTO.getCodigoQR(), ruta,
                EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getFechaTraslado()));


        return this.trasladoMapper.map(traslado);
    }

    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long aLong, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {

    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    @Override
    public void trasladoRetirado(Long aLong) {

    }

    @Override
    public void trasladoDepositado(Long aLong) {

    }
}

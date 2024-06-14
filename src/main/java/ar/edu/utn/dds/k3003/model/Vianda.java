package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@Entity
public class Vianda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(unique = true)
  private String codigoQR;
  private long colaboradorId;
  private Integer heladeraId;
  private LocalDateTime fechaElaboracion;
  @Enumerated(EnumType.STRING)
  private EstadoViandaEnum estado;
  public Vianda() {
  }
  public Vianda(String qr, long colaboradorId, Integer heladeraId, EstadoViandaEnum estado) {
    this.codigoQR = qr;
    this.colaboradorId = colaboradorId;
    this.heladeraId = heladeraId;
    this.estado = estado;
    this.fechaElaboracion = LocalDateTime.now();
  }
}

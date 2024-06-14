package ar.edu.utn.dds.k3003.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor

@NoArgsConstructor
public class Respuesta {
  private boolean resultado;

  /*public Respuesta(boolean resultado) {
    this.resultado = resultado;
  }*/
}

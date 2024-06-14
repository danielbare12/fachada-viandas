package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ViandaRepository {
  private static AtomicLong seqId = new AtomicLong();
  //private Collection<Vianda> viandas;

  static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("copiamedb");;;
  EntityManager entityManager = entityManagerFactory.createEntityManager(); ;

  /*public ViandaRepository() {
    this.viandas = new ArrayList<>();
  }*/

  public Vianda save(Vianda vianda){
    //if(vianda.getId() <= 0){
    //  vianda.setId(seqId.getAndIncrement());
      //this.viandas.add(vianda);
      entityManager.getTransaction().begin();
      entityManager.persist(vianda);
      entityManager.getTransaction().commit();
    //  entityManager.close();
    //}

    return vianda;
  }

  public List<Vianda> list(){

    List<Vianda> viandas = entityManager.createQuery("from Vianda", Vianda.class).getResultList();

    return viandas;
  }

  public Vianda findById(Long id){
    Collection<Vianda> viandas = entityManager.createQuery("from Vianda",Vianda.class).getResultList();
    Optional<Vianda> first = viandas.stream().filter(v -> v.getId() == id).findFirst();
    return first.orElseThrow(() -> new NoSuchElementException(
        String.format("No hay una ruta de id: %s", id)
    ));
  }

  public Vianda findByQr(String qr){
    Collection<Vianda> viandas = entityManager.createQuery("from Vianda",Vianda.class).getResultList();
    Optional<Vianda> first = viandas.stream().filter(v -> v.getCodigoQR().equals(qr)).findFirst();
    return first.orElseThrow(() -> new NoSuchElementException(
        String.format("No hay una ruta de qr: %s", qr)
    ));
  }

  public List<Vianda> findByColaborador(Long idColab, Integer mes, Integer anio){
    Collection<Vianda> viandas = entityManager.createQuery("from Vianda",Vianda.class).getResultList();
    return viandas.stream().filter(v -> v.getColaboradorId() == idColab
    && mes == v.getFechaElaboracion().getMonthValue()
    && anio == v.getFechaElaboracion().getYear()).toList();
  }

  public void update(Vianda vianda){
    entityManager.getTransaction().begin();
    entityManager.merge(vianda);
    entityManager.getTransaction().commit();

  }
}

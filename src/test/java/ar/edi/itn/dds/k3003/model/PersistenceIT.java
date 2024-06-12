package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.model.Vianda;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceIT {
  static EntityManagerFactory entityManagerFactory ;
  EntityManager entityManager ;

  @BeforeAll
  public static void setUpClass() throws Exception {
    entityManagerFactory = Persistence.createEntityManagerFactory("copiamedb");
  }
  @BeforeEach
  public void setup() throws Exception {
    entityManager = entityManagerFactory.createEntityManager();
  }
  @Test
  public void testConectar() {
// vacío, para ver que levante el ORM
  }

  @Test
  public void testGuardarYRecuperarDoc() throws Exception {
    Vianda v1 = new Vianda("string", 1,1, EstadoViandaEnum.PREPARADA);
    entityManager.getTransaction().begin();
    entityManager.persist(v1);
    entityManager.getTransaction().commit();
    entityManager.close();

    entityManager = entityManagerFactory.createEntityManager();
    Vianda v2 = entityManager.find(Vianda.class,v1.getId());
    Assertions.assertEquals(v1.getCodigoQR(), v2.getCodigoQR()); // también puede redefinir el equals
  }


}

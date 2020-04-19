package co.com.lotopunto.mslotopunto.repositories;

import co.com.lotopunto.mslotopunto.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Component to access to db.
 * @author jhovannycanas
 * @see Person
 */
public interface PersonRepository extends JpaRepository<Person, Integer> {

    @Query("SELECT T FROM Person T WHERE T.identificacion=:identificacion and T.nombre=:nombre AND T.fechaNacimiento=:fechaNacimiento")
    Person findExistPersona(@Param("identificacion") BigInteger identificacion, @Param("nombre") String nombre,
                            @Param("fechaNacimiento") Date fechaNacimiento);

    Optional<Person> findByIdentificacion(BigInteger identificacion);

    @Query("SELECT T FROM Person T WHERE T.estado='A'")
    List<Person> findAllActive();
}

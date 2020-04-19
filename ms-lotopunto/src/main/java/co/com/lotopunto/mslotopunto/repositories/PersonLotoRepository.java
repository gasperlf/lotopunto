package co.com.lotopunto.mslotopunto.repositories;

import co.com.lotopunto.mslotopunto.entities.PersonLoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PersonLotoRepository extends JpaRepository<PersonLoto, Integer> {

    @Query("SELECT T FROM PersonLoto T WHERE T.identificacion=:identificacion and T.nombre=:nombre AND T.fechaNacimiento=:fechaNacimiento")
    PersonLoto findExistPersona(@Param("identificacion") BigInteger identificacion,@Param("nombre") String nombre,
                                @Param("fechaNacimiento") Date fechaNacimiento);

    Optional<PersonLoto> findByIdentificacion(BigInteger identificacion);

    @Query("SELECT T FROM PersonLoto T WHERE T.estado='A'")
    List<PersonLoto> findAllActive();
}

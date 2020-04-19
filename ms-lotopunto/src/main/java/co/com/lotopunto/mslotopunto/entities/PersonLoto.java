package co.com.lotopunto.mslotopunto.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Entity to respresent a person in db.
 * @author jhovannycanas
 */
@Table(schema = "loto",name = "loto_tbl_persona_loto")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonLoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "identificacion", nullable = false)
    private BigInteger identificacion;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private Date fechaNacimiento;

    @Column(name = "suma_variable")
    private Integer sumaVariable;

    @Column(name = "concatenado")
    private String concatenado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "estado")
    private String estado;
}

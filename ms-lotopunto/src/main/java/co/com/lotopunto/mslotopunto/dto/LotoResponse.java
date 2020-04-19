package co.com.lotopunto.mslotopunto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@Builder
public class LotoResponse {

    @JsonProperty("identificacion")
    private BigInteger identificacion;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("fecha_nacimiento")
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date fechaNacimiento;

    @JsonProperty("fecha_creacion")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Date fechaCrecion;

    @JsonProperty("concatenado")
    private String concatenado;

    @JsonProperty("suma_variable")
    private Integer sumaVariable;
}

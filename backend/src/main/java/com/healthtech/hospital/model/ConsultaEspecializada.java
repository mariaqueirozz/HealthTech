import com.healthtech.hospital.model.Consulta;

import jakarta.persistence.Entity;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.validation.constraints.NotBlank;



@Entity
public class ConsultaEspecializada extends Consulta {

@NotBlank
private String especialidade;

@Override
public void validar() {
    validarComum();

    if (especialidade == null || especialidade.isBlank()) {
        throw new RegraDeNegocioException("A especialidade é obrigatória.");
    }

    if (!(getProfissional() instanceof MedicoEspecialista esp) || !esp.atende(especialidade)) {
        throw new RegraDeNegocioException("O profissional responsável deve ser especialista em " + especialidade + ".");
    }
}
    
}

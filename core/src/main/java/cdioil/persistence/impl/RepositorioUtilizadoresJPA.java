package cdioil.persistence.impl;

import cdioil.persistence.RepositorioBaseJPA;
import cdioil.domain.authz.UserRegistado;
import cdioil.persistence.PersistenceUnitNameCore;
import cdioil.persistence.UserRegistadoRepository;

public class RepositorioUtilizadoresJPA extends RepositorioBaseJPA<UserRegistado, Long> implements UserRegistadoRepository {

    @Override
    protected String nomeUnidadePersistencia() {
        return PersistenceUnitNameCore.PERSISTENCE_UNIT_NAME;
    }

    @Override
    public UserRegistado addUserRegistado(UserRegistado userRegistado) {
        return super.add(userRegistado);
    }
}

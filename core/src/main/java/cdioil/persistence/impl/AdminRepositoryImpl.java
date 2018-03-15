package cdioil.persistence.impl;

import cdioil.domain.authz.Admin;
import cdioil.domain.authz.SystemUser;
import cdioil.persistence.PersistenceUnitNameCore;
import cdioil.persistence.RepositorioBaseJPA;

/**
 * Classe que implementa e permite a persistencia do repositório de administradores 
 * na base de dados
 * @author <a href="1160907@isep.ipp.pt">João Freitas</a>
 */
public class AdminRepositoryImpl extends RepositorioBaseJPA<Admin,Long>{
    /**
     * Método que devolve o nome da unidade de persistência usada no modulo em 
     * que está a ser feita a implementação
     * @return String com o nome da unidade de persistencia
     */
    @Override
    protected String nomeUnidadePersistencia() {
        return PersistenceUnitNameCore.PERSISTENCE_UNIT_NAME;
    }
    @Override
    public Admin add(Admin admin){
        if(exists(admin))return admin;
        return super.add(admin);
    }
    public boolean exists(Admin admin){return find(admin.getID())!=null;}
    
}

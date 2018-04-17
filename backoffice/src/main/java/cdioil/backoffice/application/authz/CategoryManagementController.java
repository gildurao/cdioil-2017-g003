package cdioil.backoffice.application.authz;

import cdioil.domain.Category;
import cdioil.domain.authz.Email;
import cdioil.domain.authz.Manager;
import cdioil.domain.authz.SystemUser;
import cdioil.persistence.impl.ManagerRepositoryImpl;
import cdioil.persistence.impl.MarketStructureRepositoryImpl;
import cdioil.persistence.impl.UserRepositoryImpl;

/**
 * Controller class used for US150 - Associar Categorias a um Gestor and US152 - Remover Categorias de um Gestor.
 *
 * @author <a href="1160936@isep.ipp.pt">Gil Durão</a>
 * @author <a href="1160912@isep.ipp.pt">Rita Gonçalves</a>
 */
public class CategoryManagementController {

    /**
     * Manager chosen by an admin to have categories removed or added.
     */
    private Manager manager;

    /**
     * Sufix of the regular expression used to search categories by its identifier.
     */
    private static final String REGEX_PREFIX = ".*";

    /**
     * Prefix of the regular expression used to search categories by its identifier.
     */
    private static final String REGEX_SUFIX = ".*";

    /**
     * Regular expression to validate the path of the Category in the Market Structure.
     */
    private final static String PATH_REGEX = "[0-9]+" + Category.Sufixes.SUFIX_DC + "((-[0-9]+" + Category.Sufixes.SUFIX_UN + "(-[0-9]+"
            + Category.Sufixes.SUFIX_CAT + "(-[0-9]+" + Category.Sufixes.SUFIX_SCAT + "(-[0-9]+" + Category.Sufixes.SUFIX_UB + ")?)?)?)?)";

    /**
     * Finds all managers saved in the database.
     *
     * @return iterable of managers
     */
    public Iterable<Manager> listAllManagers() {
        return new ManagerRepositoryImpl().findAll();
    }

    /**
     * Sets a manager chosen by an admin based on the email he inserted in the UI
     *
     * @param email email written by the admin on the UI
     * @return true if the manager exists and was set, false if otherwise
     */
    public boolean setManager(String email) {
        UserRepositoryImpl userRepo = new UserRepositoryImpl();
        Email managerEmail = new Email(email);
        SystemUser sysUser = userRepo.findByEmail(managerEmail);
        manager = new ManagerRepositoryImpl().findBySystemUser(sysUser);
        return manager != null;
    }

    /**
     * Removes categories from a manager.
     *
     * @param identifier identifier of the categories
     * @return true if they were removed with success, false if otherwise
     */
    public boolean removeCategories(String identifier) {
        if (manager.removeCategories(new MarketStructureRepositoryImpl().
                findCategoriesByIdentifierPattern(REGEX_PREFIX + identifier.toUpperCase() + REGEX_SUFIX)) != false) {
            Manager managerY = new ManagerRepositoryImpl().merge(manager);
            if (managerY != null) {
                manager = managerY;
                return true;
            }
        }
        return false;
    }

    /**
     * Adds categories to a manager.
     *
     * @param identifier identifier of the categories
     * @return true, if the categories are successfully added. Otherwise, returns false
     */
    public boolean addCategories(String identifier) {
        if (manager.addCategories(new MarketStructureRepositoryImpl().
                findCategoriesByIdentifierPattern(REGEX_PREFIX + identifier.toUpperCase() + REGEX_SUFIX)) != false) {
            Manager managerY = new ManagerRepositoryImpl().merge(manager);
            if (managerY != null) {
                manager = managerY;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inserted path is valid.
     *
     * @param identifier identifier of the categories
     * @return true, if the categories are valid. Otherwise, returns false
     */
    public boolean checkPath(String identifier) {
        return identifier.matches(PATH_REGEX);
    }

    /**
     * Returns the size of the manager list.
     *
     * @param managerList List with all managers
     * @return the size of the list
     */
    public int size(Iterable<Manager> managerList) {
        int size = 0;
        for (Manager m : managerList) {
            size++;
        }
        return size;
    }
}

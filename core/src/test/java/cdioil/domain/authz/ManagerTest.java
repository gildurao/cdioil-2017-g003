package cdioil.domain.authz;

import cdioil.domain.Category;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing for class Manager.
 *
 * @author
 */
public class ManagerTest {

    private SystemUser sysUser;

    @Before
    public void setUp() {
        Email email = new Email("lilpump@guccigang.com");
        Password pwd = new Password("Bananas19");
        Name nome = new Name("Lil", "Pump");
        sysUser = new SystemUser(email, nome, pwd);
    }

    /**
     * Builds invalid manager instance.
     */
    @Test(expected = IllegalArgumentException.class)
    public void criaInstanciaInvalidaDeGestorTest() {
        new Manager(null);
    }

    /**
     * Test of hashCode method of class Manager.
     */
    @Test
    public void hashCodeTest() {
        System.out.println("hashCode");
        Manager g = new Manager(sysUser);
        int expResult = sysUser.hashCode();
        int result = g.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method of class Manager.
     */
    @Test
    public void equalsTest() {
        System.out.println("equals");

        Manager instance = new Manager(sysUser);
        assertNotEquals("The condition should succeed because we are comparing"
                + "a manager instance to a null value", instance, null);
        assertNotEquals("The condition should succeed because we are comparing "
                + "instances of different classes", instance, "bananas");
        assertEquals("The condition should succeed because we are comparing "
                + "the same instance", instance, instance);

        Email email = new Email("lilxan@guccigang.com");
        Password pwd = new Password("Bananas19");
        Name nome = new Name("Lil", "Xan");
        SystemUser sysUser2 = new SystemUser(email, nome, pwd);

        Manager other = new Manager(sysUser2);
        Manager that = new Manager(sysUser);

        assertNotEquals("The condition should succeed because we are comparing "
                + "managers that have different system users", instance, other);
        assertEquals("The condition should succeed because we are comparing "
                + "managers that have the same system user", instance, that);
    }

    /**
     * Test addCategories method, of class Manager.
     */
    @Test
    public void testeAdicionarCategorias() {
        System.out.println("adicionarCategorias");

        Manager instance = new Manager(new SystemUser(new Email("myPrecious@gmail.com"),
                new Name("Gollum", "Smeagol"), new Password("Precious3")), new LinkedList<>());
        //teste adiconar lista a null
        List<Category> lc = null;
        assertFalse(instance.addCategories(lc));
        //teste adicionar lista vazia
        lc = new LinkedList<>();
        assertFalse(instance.addCategories(lc));
        //teste adiconar lista com elementos
        lc.add(new Category("Fruit", "124CAT", "10DC-10UN-124CAT"));
        lc.add(new Category("Beverage", "6040SCAT", "10DC-10UN-100CAT-6040SCAT"));
        assertTrue(instance.addCategories(lc));
        //teste adicionar lista com elementos repetidos
        assertTrue(instance.addCategories(lc));
    }

    /**
     * Test removeCategories method, of class Manager.
     */
    @Test
    public void testRemoveCategories() {
        System.out.println("removerCategorias");

        Manager instance = new Manager(new SystemUser(new Email("myPrecious@gmail.com"),
                new Name("Gollum", "Smeagol"), new Password("Precious3")), new LinkedList<>());
        //teste remover lista a null
        List<Category> lc = null;
        assertFalse(instance.removeCategories(lc));
        //teste remover lista vazia
        lc = new LinkedList<>();
        assertFalse(instance.removeCategories(lc));
        //teste remover lista com elementos inexistentes
        //teste adiconar lista com elementos
        lc.add(new Category("Fruit", "124CAT", "10DC-10UN-124CAT"));
        lc.add(new Category("Beverage", "6040SCAT", "10DC-10UN-100CAT-6040SCAT"));
        assertFalse(instance.removeCategories(lc));
        //teste adicionar lista com elementos existentes
        instance.addCategories(lc);
        assertTrue(instance.removeCategories(lc));
    }

    /**
     * Test of getID method, of class Manager.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        Manager instance = new Manager(sysUser);
        Email expResult = new Email("lilpump@guccigang.com");
        Email result = instance.getID();
        assertEquals(expResult, result);
    }
}

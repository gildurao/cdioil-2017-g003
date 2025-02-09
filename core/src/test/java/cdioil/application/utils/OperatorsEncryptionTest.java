package cdioil.application.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testes unitários relativamente à encriptação de operadores
 * @author <a href="1160907@isep.ipp.pt">João Freitas</a>
 */
public class OperatorsEncryptionTest {
    /**
     * Constante que representa o numero de Strings aleartorias a serem criadas 
     * de modo a testar a encriptação dos operadores
     */
    private static final int NUMERO_STRINGS_ALEARTORIAS=750;
    public OperatorsEncryptionTest() {
    }
    /**
     * Testa a encriptação e decriptação por operadores
     */
    @Test
    public void testOperatorsEncryption(){
        System.out.println("Teste Unitário relativamente à encriptação e decriptação "
                + "usando a encriptação por operadores");
        List<String> listRandomStrings=createListRandomStrings(NUMERO_STRINGS_ALEARTORIAS);
        List<String> listRandomEncryptedStrings=new ArrayList<>(NUMERO_STRINGS_ALEARTORIAS);
        List<String> listRandomDecryptedStrings=new ArrayList<>(NUMERO_STRINGS_ALEARTORIAS);
        for(int i=0;i<NUMERO_STRINGS_ALEARTORIAS;i++){
            listRandomEncryptedStrings.add(OperatorsEncryption.encrypt(listRandomStrings.get(i)));
            listRandomDecryptedStrings.add(OperatorsEncryption.decrypt(listRandomEncryptedStrings.get(i)));
        }
        assertEquals("A condição deve acertar pois as listas são iguais",listRandomStrings,listRandomDecryptedStrings);
    }
    
    /**
     * Testa a encriptação e decriptação usando determinados valores de encriptação 
     * e de operadores
     */
    @Test
    public void testOperatorsValues(){
        System.out.println("Teste Unitário relativamente à encriptação e decriptação "
                + "usando a encriptação por operadores com determinados valores de "
                + "encriptação e de operador");
        String palavra="Hello it's Lil Pump on this bitch!";
        int operador=OperatorsEncryption.BITWISE_ENCRYPTION_CODE;
        List<String> listaPalavrasEncriptadas=new ArrayList<>();
        for(int i=0;i<operador;i++){
            listaPalavrasEncriptadas.add(OperatorsEncryption.encrypt(palavra,operador,i));
        }
        operador=OperatorsEncryption.ADDITION_ENCRYPTION_CODE;
        for(int i=0;i<100;i++){
            int valor=new Random().nextInt(operador);
            listaPalavrasEncriptadas.add(OperatorsEncryption.encrypt(palavra,operador,valor));
        }
        operador=OperatorsEncryption.MULTIPLICATION_ENCRYPTION_CODE;
        for(int i=0;i<100;i++){
            int valor=new Random().nextInt(operador);
            listaPalavrasEncriptadas.add(OperatorsEncryption.encrypt(palavra,operador,valor));
        }
        listaPalavrasEncriptadas.forEach((t)->{areWordsEqual(palavra,OperatorsEncryption.decrypt(t));});
        assertEquals(OperatorsEncryption.encrypt(null),null);
        assertEquals(OperatorsEncryption.decrypt(null),null);
        assertEquals(OperatorsEncryption.encrypt(""),"");
        assertEquals(OperatorsEncryption.decrypt(""),"");
    }
    
    @Test
    public void testRemoveHeaderValue(){
        String encryptedStringNoHeader="Lil Pump#Biggest Flexer#of the century";
        String encryptedString="Hmmmm#maybe not#"+encryptedStringNoHeader;
        System.out.println("Test of Remove Header Value of an encrypted String");
        assertEquals("The condition must be successful since the content of the Strings "
                + "is the same",OperatorsEncryption.removeEncryptionHeader(null),null);
        assertEquals("The condition must be successful since the content of the Strings "
                + "is the same",OperatorsEncryption.removeEncryptionHeader(""),"");
        System.out.println(OperatorsEncryption.removeEncryptionHeader(encryptedString));
        assertEquals("The condition must be successful since the content of the Strings "
                + "is the same",OperatorsEncryption.removeEncryptionHeader(encryptedString),encryptedStringNoHeader);
    }
    
    /**
     * Cria uma lista de Strings aleartorias com o comprimento inferior a 15
     * @param numberOfStrings Integer com o numero de Strings aleartorias a serem criadas
     * @return List com as Strings aleartorias criadas
     */
    private List<String> createListRandomStrings(int numberOfStrings){
        List<String> listRandomStrings=new ArrayList<>(numberOfStrings);
        for(int i=0;i<numberOfStrings;i++){
            StringBuilder builder=new StringBuilder();
            int randomValue=new Random().nextInt(15);
            for(int j=0;j<randomValue;j++)builder.append((char)(new Random().nextInt('z')));
            listRandomStrings.add(builder.toString());
        }
        return listRandomStrings;
    }
    
    /**
     * Método que verifica se duas Strings iguais (Case-Sensitive)
     * @param wordX String com a palavra X
     * @param wordY String com a palavra Y
     */
    private void areWordsEqual(String wordX,String wordY){
        if(!wordX.equals(wordY))throw new AssertionError("Strings não são iguais!");
    }
}

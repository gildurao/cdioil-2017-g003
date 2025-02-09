package cdioil.domain.authz;

import org.h2.util.StringUtils;

import cdioil.application.utils.OperatorsEncryption;
import cdioil.framework.domain.ddd.ValueObject;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that represents a PhoneNumber of a SystemUser
 * @author Joana Pinheiro
 */
@Embeddable
public class PhoneNumber implements Serializable, ValueObject {

    /**
     * Serial Number
     */
    private static final long serialVersionUID = 6l;

    private static final String NUMBER_INVALID = "Número invalido";
    private static final String CONTAINS_NON_DIGIT_CHARACTERS = "Este contém caracteres";

    /**
     * Regular Expression for Portuguese service providers
     */
    private static final String VALID_PORTUGUESE_NUMBERS = "(91|92|93|96)([\\d]{7})";
    
    /**
     * Constant that represents the encryption value being used
     */
    public static final int ENCRYPTION_VALUE=0xBC67D;
    /**
     * Constant that represents the encryption codes being used
     */
    public static final int ENCRYPTION_CODE=OperatorsEncryption.ADDITION_ENCRYPTION_CODE;

    /**
     * Variable that represents a phone number
     */
    private String number;

    /**
     * Empty constructor used for JPA.
     */
    protected PhoneNumber() {
    }

    /**
     * PhoneNumber construtor
     * @param number phone number
     */
    public PhoneNumber(String number) {
        if (!validateNumber(number)) {
            throw new IllegalArgumentException(NUMBER_INVALID
                    ,new Throwable(this.getClass().getSimpleName()));
        }
        this.number = encryptNumber(number);
    }

    /**
     * Method that encrypts a phone number
     * @param number non encrypted phone number
     * @return encrypted phone number
     */
    private String encryptNumber(String number) {
        return OperatorsEncryption.encrypt(number,ENCRYPTION_CODE,ENCRYPTION_VALUE);
    }

    /**
     * Method that decrypts a phone number so that it can be used
     * @param encriptedNumber encrypted phone number
     * @return non encrypted phone number
     */
    private String decryptNumber(String encriptedNumber) {
        return OperatorsEncryption.decrypt(encriptedNumber);
    }

    /**
     * Method that verifies is the phone number is valid according to the Portuguese service providers
     * @param number non encrypted phone number
     * @return true if the phone number is valid and false if it's not
     */
    private boolean validateNumber(String number) {
        Pattern pattern = Pattern.compile(VALID_PORTUGUESE_NUMBERS);
        Matcher matcher = pattern.matcher(number);

        if (!StringUtils.isNumber(number)) {
            throw new IllegalArgumentException(CONTAINS_NON_DIGIT_CHARACTERS
                    ,new Throwable(this.getClass().getSimpleName()));
        }

        return matcher.matches();
    }

    
    public boolean validatesNumber(String phoneNumber) {
        return phoneNumber.equalsIgnoreCase(decryptNumber(number));
    }

    @Override
    public String toString() {
        return "Phone Number: " + decryptNumber(number);
    }
}

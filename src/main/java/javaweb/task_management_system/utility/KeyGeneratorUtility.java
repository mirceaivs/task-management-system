package javaweb.task_management_system.utility;

import javaweb.task_management_system.exceptions.InvalidAction;
import javaweb.task_management_system.exceptions.InvalidValueException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtility {

    public static KeyPair generateRsaKey(){
        KeyPair keyPair;

        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

        }catch (Exception e){
            throw new InvalidAction("Something went wrong while trying to generate the RSA key!");
        }
        return keyPair;
    }
}

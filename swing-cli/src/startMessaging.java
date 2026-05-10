import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class startMessaging{

    public volatile commsHandler comm;
    public static encHandler enc = new encHandler();
    public static decHandler dec = new decHandler();
    
    public static void main(String[] args){
        for(int l = 1; l <= 10; l++){
            for(int o = 5; o <= 10000; o++){
                String encThis = "what's up";
                //System.out.println("Generating key...");
                enc.genKey(l, o);
                //System.out.println("Key: " + enc.takeLineFromFile("currKey.dat"));
                //System.out.println("Encrypting: " + encThis);
                String encMess = enc.encrypt(encThis);
                //System.out.println("Encrypted message: " + mess);
                //System.out.println("Decrypting: " + mess);
                String decMess = dec.decrypt(encMess);
                //System.out.println("Decrypted message: " + mess);
                System.out.println("Level: " + l + " Ops: " + o + " Status: " + encThis.equals(decMess));
                if(!encThis.equals(decMess)){
                    System.out.print("Level: " + l + " Ops: " + o + " Status: " + encThis.equals(decMess) + " ");
                    System.out.println("Original: " + encThis + " Decrypted: " + decMess);
                    System.out.println("Encrypted: " + encMess);
                    return;
                }
            }
        }
    }
}


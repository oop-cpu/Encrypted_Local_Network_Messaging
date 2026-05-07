import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class startMessaging{

    public static String ipDest = "";
    
    public static void main(String[] args){
        if(args.length == 0)
            initCommandLine();
        else if(args[0].equals("gui"))
            initGui();
        else if(args[0].equals("cmd"))
            initCommandLine();
        else
            initCommandLine();
    }

    public static void initCommandLine(){
        Scanner user = new Scanner(System.in);
        System.out.print("Enter the IP that you want to message: ");
        ipDest = user.nextLine();
        commsHandler comm = new commsHandler(ipDest);

        while(!comm.ipReady()){
            System.out.print("Enter the IP that you want to message: ");
            ipDest = user.nextLine();
            comm = new commsHandler(ipDest);
        }
        
        String opt = "";
        while(!opt.equals("exit")){
            System.out.print("> ");
            opt=user.nextLine();
        }
    }

    public static void initGui(){
        System.out.println("Starting UI...");
    }
}


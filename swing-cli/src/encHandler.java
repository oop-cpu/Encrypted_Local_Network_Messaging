import java.util.*;
import java.io.*;

public class encHandler{
    private static String keyFile = "currKey.dat";
    private static Random rand = new Random();
    static final int MOD = 97;

    public static void genKey(int L, int O){
    
        // parameter corrections
        if(L > 10){
            System.out.println("Level " + L + " is too high. Lowering to 10.");
            L = 10;
        }
        if(L < 1){
            System.out.println("Level " + L + " is too low. Raising to 1.");
            L = 1;
        }
        if(O < 5){
            System.out.println("Operations count " + O + " is too low. Raising to 5.");
            O = 5;
        }
        if(O > 10000){
            System.out.println("Operations count " + O + " is too high. Lower to 10000.");
            O = 10000;
        }
    
        char[] ops = {'a', 's', 'm'};
    
        StringBuilder key = new StringBuilder();
        key.append("L").append(L).append("O").append(O);
    
        for(int i = 0; i < O; i++){
    
            char op = ops[rand.nextInt(3)];
            int by;
    
            if(op == 'm'){
                // 1 to MOD-1 (must have inverse)
                by = rand.nextInt(MOD - 1) + 1;
            } else {
                // 0 to MOD-1
                by = rand.nextInt(MOD);
            }
    
            key.append(op).append(by);
        }
    
        saveInFile(key.toString(), keyFile);
    }

    public static String encrypt(String mess){
        //init vars
        String re = "";
        String key = takeLineFromFile(keyFile);
        char[] opsArr = {'a', 's', 'm'};
        String[] ops;
        int[] by;
        int level;
        int numOps;

        //parse key into instructions
        int indexOpStart = 0;
        if(key.length() == 0) return "Missing key";
        //System.out.println("Key length: " + key.length());
        while(!checkCharOps(key.substring(indexOpStart, indexOpStart+1)) && indexOpStart<key.length()-1){
            //System.out.print(indexOpStart + " -> ");
            indexOpStart++;
            //System.out.println(indexOpStart);
        }
        if(indexOpStart >= key.length()) return "Key error.";

        //config key
        int lStart = 0;
        int oStart = 0;
        while(!key.substring(lStart, lStart+1).equals("L") && lStart < key.length()-1)
            lStart++;
        if(!key.substring(lStart,lStart+1).equals("L")) return "Key error";
        while(!key.substring(oStart, oStart+1).equals("O") && oStart < key.length()-1)
            oStart++;
        if(!key.substring(oStart,oStart+1).equals("O")) return "Key error";

        level = Integer.parseInt(key.substring(lStart + 1, oStart));
        numOps = Integer.parseInt(key.substring(oStart + 1, indexOpStart));

        //parse ops into arrays
        ops = new String[numOps];
        by = new int[numOps];
        int counter = 0;
        for(int i = indexOpStart; i < key.length(); i = returnNextOpIndex(i, key)){
            if(counter >= numOps) break; // Prevent array overflow
            
            if(checkCharOps(key.substring(i, i+1))){
                int nextOp = returnNextOpIndex(i, key);
                
                // Ensure we don't grab an empty string if an op is at the very end
                String valStr = key.substring(i + 1, (nextOp == i + 1 && nextOp < key.length()) ? nextOp + 1 : nextOp);
                
                if(!valStr.isEmpty()) {
                    ops[counter] = key.substring(i, i+1);
                    by[counter] = Integer.parseInt(valStr);
                    counter++;
                }
            }
            // If we've reached the end, stop
            if(returnNextOpIndex(i, key) >= key.length() - 1) break;
        }

        //FINALLY time to encrypt the message
        for(int i = 0; i < mess.length(); i++){
            long currentNum = getNumEq(mess.substring(i, i+1));
            for(int l = 0; l < level; l++)
                for(int o = 0; o < ops.length; o++)
                    currentNum = operate(currentNum, ops[o], by[o]);
            re += String.valueOf(currentNum) + " ";
        }
        
        return re;
    }

    //change long num based on op
    public static long operate(long num, String op, int by){
        switch(op){
            case "a":
                return (num + by) % MOD;
    
            case "s":
                return (num - by + MOD) % MOD;
    
            case "m":
                return (num * by) % MOD;
    
            default:
                return num;
        }
    }

    //convert char in string to num
    public static long getNumEq(String x){
        String[] alpha = 
        {"1","2","3","4","5","6",
         "7","8","9","0","a","b",
         "c","d","e","f","g","h",
         "i","j","k","l","m","n",
         "o","p","q","r","s","t",
         "u","v","w","x","y","z",
         "A","B","C","D","E","F",
         "G","H","I","J","K","L",
         "M","N","O","P","Q","R",
         "S","T","U","V","W","X",
         "Y","Z","!","@","#","$",
         "%","^","&","*","(",")",
         "_","-","+","=",",","<",
         ">",".","/","?",":",";",
         "'","{","}","[","]","|",
         "\\"," ","\""};
        for (int i = 0; i < alpha.length; i++) {
            if (x.equals(alpha[i])) return i;
        }
        return -1;
    }

    //helper to return next op index
    public static int returnNextOpIndex(int index, String cont){
        if(index > cont.length()-1) return index;
        index++;
        while(!checkCharOps(cont.substring(index, index+1)) && index < cont.length()-1)
            index++;
        return index;
    }

    //helper to check for chars
    public static boolean checkCharOps(String check){
        String[] list = {"a", "s", "m"};
        for(int i = 0; i < list.length; i++)
            if(check.equals(list[i]))
                return true;
        return false;
    }

    //simple method to help with file out
    public static void saveInFile(String cont, String file){
		try{
			FileWriter out = new FileWriter(file);
			out.write(cont + "\n");
			out.close();
		}catch(IOException e){System.out.println("Error in save file: " + e);}
	}

    //simple method to help with file in
    public static String takeLineFromFile(String file){
        try{
            Scanner in = new Scanner(new File(file));
            if(in.hasNextLine())
                return in.nextLine();
            else return "";
        }catch(FileNotFoundException e){System.out.println("Error in grab file: " + e);return "";}
    }

}
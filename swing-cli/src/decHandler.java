import java.util.*;
import java.io.*;

public class decHandler{
    private static String keyFile = "currKey.dat";
    static final long MOD = 97;

    public static String decrypt(String mess){
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
        Scanner in = new Scanner(mess);
        while(in.hasNext()){
            Long current = Long.parseLong(in.next());
            for(int l = 0; l < level; l++)
                for(int o = counter-1; o >= 0; o--)
                    current = operate(current, ops[o], by[o]);
            re += getStringEq(current);
        }
        
        return re;
    }

    //change long num based on op
    public static long operate(long num, String op, int by){
        switch(op){
            case "a":
                return (num - by + MOD) % MOD;
    
            case "s":
                return (num + by) % MOD;
    
            case "m":
                return (num * modInverse(by, MOD)) % MOD;
    
            default:
                return num;
        }
    }

    public static long modInverse(long a, long mod){
        for(long x = 1; x < mod; x++){
            if((a * x) % mod == 1) return x;
        }
        throw new RuntimeException("No inverse exists for " + a);
    }

    //convert char in string to num
    public static String getStringEq(long x){
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
        if(x < alpha.length)
            return alpha[(int)x];
        return "(?)";
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
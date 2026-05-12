import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class commsHandler{

    private volatile String thisIp = "";
    private volatile String destIp = "";
    private static int port = 7284;
    private volatile ServerSocket serverSocket;
    private static encHandler enc = new encHandler();
    private static decHandler dec = new decHandler();

    
    public commsHandler(int l, int ops){
        enc.genKey(l, ops);
        thisIp = getIp();
    }

    public  boolean tryDestination(String ipDest){
        try{
            if(isSameNetwork(ipDest)){
                destIp = ipDest;
                return true;
            }
            return false;
        }catch(Exception e){System.out.println("Error isSameNetwork: " + e); return false;}
    }

    public boolean isSameNetwork(String ip) throws Exception{
        try{
            return InetAddress.getByName(ip).isReachable(2000);
        }catch(Exception e){
            System.out.println("Error isSameNetwork: " + e);
            return false;
        }
    }

    public  String getIp(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address) return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {e.printStackTrace();}
        return "127.0.0.1";
    }

    public boolean requestConnection(){
        stopWaiting();
        try(Socket socket = new Socket()){
            int connectTimeoutMs = 5000;
            int readTimeoutMs = 5000;

            System.out.println("Asking " + destIp + " to connect...");

            socket.connect(new InetSocketAddress(destIp, port), connectTimeoutMs);
            socket.setSoTimeout(readTimeoutMs);

            try(PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)){
                out.println("457 " + thisIp + " " + enc.takeLineFromFile("currKey.dat"));
                if(out.checkError()){
                    System.err.println("Writer encountered an error while sending.");
                    return false;
                }
            }

            System.out.println("Connection request sent to: " + destIp);
			
        }catch(IOException e){
            System.err.println("Request failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean acceptConnection(){
        stopWaiting();
        try(Socket socket = new Socket()){
            int connectTimeoutMs = 5000;
            int readTimeoutMs = 5000;

            System.out.println("Asking " + destIp + " to connect...");

            socket.connect(new InetSocketAddress(destIp, port), connectTimeoutMs);
            socket.setSoTimeout(readTimeoutMs);

            try(PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)){
                out.println("458 " + thisIp + " " + enc.takeLineFromFile("currKey.dat"));
                if(out.checkError()){
                    System.err.println("Writer encountered an error while sending.");
                    return false;
                }
            }

            System.out.println("Connection request sent to: " + destIp);
			
        }catch(IOException e){
            System.err.println("Request failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    public String waitForMessage(){
        String result = "error";
        System.out.println("Waiting on port - " + port);
    
        try{
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(thisIp, port));
    
            try(Socket clientSocket = serverSocket.accept()){
    
                System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress());
                try(BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))){
    
                    String line = in.readLine();
                    if(line != null){
                        System.out.println("Received: " + line);
                        result = line;
                    }
                    else{
                        System.out.println("Received EOF before any data.");
                        result = "";
                    }
                }
            }
        }catch(IOException e){
            System.err.println("waitForMessage stopped: " + e.getMessage());
        }finally{
            try{
                if(serverSocket != null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
            }catch(IOException ignored){}
        }
    
        return result;
    }

    public void stopWaiting(){
        try{
            if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        }catch(IOException ignored){}
    }

    public boolean send(String mess){
        stopWaiting();
        mess = enc.encrypt(mess);
        return sendPort(mess);
    }

    public boolean sendPort(String mess){
        String ipAddress = destIp;
        try(Socket socket = new Socket(ipAddress, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            out.println(mess);
            System.out.println("Message sent: " + mess);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String acceptConnect(String ip){
        String re = "";
        re += "Attempting accept...\n";
        if(tryDestination(ip))
            re += "Machine is reachable.\n";
        else{
            re += "Machine is not reachable.";
                return re;
        }
        if(acceptConnection())
            re += "Accepted request.";
        else
            re += "Something went wrong while accepting.";
        return re;
    }
    public String sendRequest(String ip){
        String re = "";
        re += "Attempting request...\n";
        if(tryDestination(ip))
            re += "Machine is reachable.\n";
        else{
            re += "Machine is not reachable.";
                return re;
        }
        if(requestConnection())
            re += "Sent request.";
        else
            re += "Something went wrong while requesting.";
        return re;
    }

    public void storeDecKey(String key){
        dec.saveInFile(key);
    }
    public String decrypt(String mess){
        return dec.decrypt(mess);
    }
    public String getDestination(){
        return destIp;
    }
}
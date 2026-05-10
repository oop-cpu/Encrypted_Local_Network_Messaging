import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class commsHandler{

    private static String thisIp = "";
    private static String destIp = "";
    private static int port = 6925;

    public commsHandler(String ipDest){
        thisIp = getIp();
        try{
            if(isSameNetwork(ipDest))
                destIp = ipDest;
        }catch(Exception e){System.out.println("Error isSameNetwork: " + e);}
    }

    public boolean isSameNetwork(String ip) throws Exception{
        try{
            return InetAddress.getByName(ip).isReachable(2000);
        }catch(Exception e){
            System.out.println("Error isSameNetwork: " + e);
            return false;
        }
    }

    public static String getIp(){
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

    public static boolean requestConnection(){
        try(Socket socket = new Socket()){
            int connectTimeoutMs = 5000;
            int readTimeoutMs = 5000;

            System.out.println("Asking " + destIp + " to connect...");

            socket.connect(new InetSocketAddress(destIp, port), connectTimeoutMs);
            socket.setSoTimeout(readTimeoutMs);

            try(PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)){
                out.println(thisIp + " 427");
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
    
}
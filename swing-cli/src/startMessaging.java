import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class startMessaging{

    public volatile commsHandler comm = new commsHandler(10, 10000);
    private final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
	private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public volatile JFrame chat = new JFrame();
    public volatile JTextField requests = new JTextField();
    public volatile JButton accept = new JButton("Accept");
    public volatile JButton sendRequestButton = new JButton("Send");
    public volatile JTextField sendRequests = new JTextField();
    JTextArea outputText = new JTextArea();
    JScrollPane output = new JScrollPane(outputText);

    //theming
    Color backgroundColor = new Color(31, 31, 31);
    Color textBackgroundColor = new Color(20, 20, 20);
    Color textColor = new Color(224, 224, 224);
    Color buttonGreen = new Color(37, 184, 0);
    
    Font displayFont = new Font("SansSerif", Font.BOLD, 18);

    JPanel panel = new JPanel();

    Border line = BorderFactory.createLineBorder(Color.WHITE);
    Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    Border border = BorderFactory.createCompoundBorder(line, padding);

    int width = 0;
    int height = 0;

    public volatile String outputBuffer = "";
    public volatile int outputCount = 0;
    public volatile String potKey = "";

    public static startMessaging listener = new startMessaging();
    
    public static void main(String[] args){
        listener.startListening();
        listener.receiveMessage("initFrame");
		listener.receiveMessage("wait");
        listener.receiveMessage("sender");
    }

    //threads
    public void startListening(){
        Thread listenerThread = new Thread(() -> {
            while(true){
                try{
                    String line = messageQueue.take();
					executor.submit(() -> {
						if(line.equals("initFrame")){
                            initFrame();
                        }
                        if(line.equals("wait")){
                            waiter();
                        }
                        if(line.equals("sender")){
                            sender();
                        }
					});
                }catch(InterruptedException e){Thread.currentThread().interrupt();}
            }
        });
        listenerThread.start();
    }
	public void receiveMessage(String line){messageQueue.offer(line);}
    //end threads

    public void initFrame(){
        chat.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chat.setLayout(null);
        chat.setUndecorated(false);
        chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        Rectangle bounds = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getMaximumWindowBounds();
        
        int x = bounds.x;
        int y = bounds.y;
        width = bounds.width;
        height = bounds.height;

        requests.setEditable(false);
        requests.setFont(displayFont);
        requests.setForeground(textColor);
        requests.setBackground(textBackgroundColor);
        requests.setBounds(75, 75, 500, 50);
        requests.setVisible(true);
        requests.setText("Waiting for requests...");

        sendRequests.setEditable(true);
        sendRequests.setFont(displayFont);
        sendRequests.setForeground(textColor);
        sendRequests.setBackground(textBackgroundColor);
        sendRequests.setBounds(75, 150, 500, 50);
        sendRequests.setVisible(true);
        sendRequests.setText("Enter ip to send request...");

        accept.setFont(displayFont);
        accept.setForeground(textColor);
        accept.setBackground(buttonGreen);
        accept.setBounds(600, 75, 100, 50);
        accept.setVisible(true);

        sendRequestButton.setFont(displayFont);
        sendRequestButton.setForeground(textColor);
        sendRequestButton.setBackground(buttonGreen);
        sendRequestButton.setBounds(600, 150, 100, 50);
        sendRequestButton.setVisible(true);

        outputText.setEditable(false);
        output.setOpaque(true);
        output.setBounds(75, 250, 625, 400);
        output.setBorder(border);
        outputText.setFont(displayFont);
        outputText.setForeground(Color.GREEN);
        outputText.setBackground(textBackgroundColor);
        output.setBackground(textBackgroundColor);

        chat.getContentPane().setBackground(backgroundColor);

        accept.addActionListener(e->{
            acceptRequest();
        }                         
        );
        sendRequestButton.addActionListener(e->{
            sendRequest();
        }                         
        );

        chat.add(requests);
        chat.add(accept);
        chat.add(sendRequests);
        chat.add(output);
        chat.add(sendRequestButton);
        
        chat.setVisible(true);
    }
    public void waiter(){
        String mess = comm.waitForMessage();
        Scanner in = new Scanner(mess);
        String first = in.next();
        if(first.equals("457")){
            String ip = in.next();
            requests.setText(ip);
            print("Received request from: " + ip);
            potKey = in.nextLine();
        }
        if(first.equals("458")){
            String ip = in.next();
            sendRequests.setText("Accepted...");
            print("Request accept from: " + ip);
            potKey = in.nextLine();
            if(potKey.length() > 0){
                comm.storeDecKey(potKey);
                print("Remote decryption key saved.");
            }
            else print("ERROR: Remote decryption key is blank.");
                
        }
    }
    public void sender(){
        
    }
    public void acceptRequest(){
        print("Accepting connection request from: " + requests.getText());
        print(comm.acceptConnect(requests.getText()));
        listener.receiveMessage("wait");
        if(potKey.length() > 0){
            comm.storeDecKey(potKey);
            print("Remote decryption key saved.");
        }
        else print("ERROR: Remote decryption key is blank.");
    }
    public void sendRequest(){
        print("Sending request to: " + sendRequests.getText());
        print(comm.sendRequest(sendRequests.getText()));
        listener.receiveMessage("wait");
    }

    public void print(String x){
        System.out.println(x);
        outputBuffer = x + "\n";
        outputText.append(outputBuffer);
        outputText.setCaretPosition(outputText.getDocument().getLength());
    }
}


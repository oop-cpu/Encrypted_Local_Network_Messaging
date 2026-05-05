import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class startMessaging{
    public static JFrame menu = new JFrame();

    public static int sizeX = 1200;
    public static int sizeY = 900;
    public static int buttonSizeX = 100;
    public static int buttonSizeY = 50;

    public static void main(String[] args){
        initMenu();
    }

    //init/close frames
    public static void closeAll(){
        System.out.println("Closing all frames...");
        menu.setVisible(false);
    }

    public static void initMenu(){
        System.out.println("Opening menu...");
        closeAll();

        //buttons
        JButton start = new JButton("Start");
        start.setBounds(sizeX/2 - buttonSizeX, sizeY/2  - 80, buttonSizeX, buttonSizeY);
        menu.add(start);

        JButton settings = new JButton("Settings");
        settings.setBounds(sizeX/2 - buttonSizeX, sizeY/2, buttonSizeX, buttonSizeY);
        menu.add(settings);

        JButton exit = new JButton("Exit");
        exit.setBounds(sizeX/2 - buttonSizeX, sizeY/2 + 80, buttonSizeX, buttonSizeY);
        menu.add(exit);

        //title
        JLabel title = new JLabel("Messaging");
        title.setBounds(75, 50, sizeX - 200, 250);
        title.setFont(new Font("Serif", Font.PLAIN, 150));
        title.setForeground(Color.WHITE);
        FontMetrics metrics = title.getFontMetrics(title.getFont());
        int width = metrics.stringWidth(title.getText()) + 10; // Adding padding
        title.setPreferredSize(new Dimension(width, metrics.getHeight()));
        title.revalidate();
        title.repaint();

        menu.add(title);

        //button events
        start.addActionListener(e->
            System.out.println("start")
        );
        settings.addActionListener(e->
            System.out.println("settings")
        );
        exit.addActionListener(e->
            System.out.println("exit")
        );

        menu.getContentPane().setBackground(Color.BLACK);
        menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        //frame config
        menu.setSize(sizeX, sizeY);
        menu.setLayout(null);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setVisible(true);
    }

}


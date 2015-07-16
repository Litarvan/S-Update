package fr.theshark34.supdate;

import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.supdate.exception.BadServerResponseException;
import fr.theshark34.supdate.exception.BadServerVersionException;
import fr.theshark34.supdate.exception.ServerDisabledException;
import fr.theshark34.supdate.exception.ServerMissingSomethingException;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(350, 30);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);

        final JProgressBar bar = new JProgressBar();
        bar.setBorder(null);
        bar.setBackground(Color.LIGHT_GRAY);
        bar.setForeground(Color.GRAY);
        bar.setBounds(5, 5, 340, 20);
        frame.add(bar);

        frame.setVisible(true);

        SUpdate su = new SUpdate("http://localhost/3/S-Update-Server/", new File("C:/Users/Adrien/Documents/Su3Test"));
        su.addApplication(new FileDeleter());

        Thread t = new Thread() {
            @Override
            public void run() {
                while(!this.isInterrupted()) {
                    bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000D));
                    bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000D));

                    System.out.println(BarAPI.getNumberOfTotalDownloadedBytes() + " / " + BarAPI.getNumberOfTotalBytesToDownload());
                }
            }
        };
        t.start();

        try {
            su.start();
        } catch (BadServerResponseException | ServerDisabledException | BadServerVersionException | IOException | ServerMissingSomethingException e) {
            e.printStackTrace();
        }

        t.interrupt();
    }

}



package gara;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class GuidaClient extends Frame implements KeyListener {
    private PrintWriter out;

    public GuidaClient() {
        try {
            String EV3_IP = "10.0.1.1"; // Sostituisci con l'IP del tuo EV3
            int PORT = 1234;
            Socket socket = new Socket(EV3_IP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            setSize(300, 200); // Finestra per la cattura dei tasti
            setTitle("Controllo EV3 con WASD");
            addKeyListener(this);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = Character.toUpperCase(e.getKeyChar());

        if (key == 'X') {
            out.println("STOP");
            System.exit(0);
        } else if ("WASD".indexOf(key) != -1) {
            out.println(String.valueOf(key));
        } else if (key == 'T') { // TURBO
            out.println("TURBO");
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new GuidaClient();
    }
}


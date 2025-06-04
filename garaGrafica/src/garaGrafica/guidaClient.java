package garaGrafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class guidaClient extends JFrame {
    private JTextField ipTextField;
    private JTextField portTextField;
    private JLabel speedLabel;
    private Socket socket;
    private PrintWriter outStream;
    private BufferedReader inStream;

    private JButton btnUp, btnDown, btnLeft, btnRight, btnStop;

    public guidaClient() {
        setTitle("Telecomando EV3");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 450);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Pannello superiore con IP, porta e bottone
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel ipPortPanel = new JPanel();
        ipPortPanel.setLayout(new BoxLayout(ipPortPanel, BoxLayout.X_AXIS));
        ipPortPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        ipPortPanel.add(new JLabel("IP Server:"));
        ipPortPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        ipTextField = new JTextField("10.0.1.1", 15);
        ipPortPanel.add(ipTextField);
        ipPortPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        ipPortPanel.add(new JLabel("Porta:"));
        ipPortPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        portTextField = new JTextField("1234", 6);
        ipPortPanel.add(portTextField);
        ipPortPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        JButton connectButton = new JButton("Connetti");
        ipPortPanel.add(connectButton);

        topPanel.add(ipPortPanel);

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        speedPanel.add(new JLabel("Velocità: "));
        speedLabel = new JLabel("0");
        speedPanel.add(speedLabel);
        topPanel.add(speedPanel);

        add(topPanel, BorderLayout.NORTH);

        // Pannello controllo movimento
        JPanel controlPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        btnUp = new JButton("▲ Avanti");
        btnDown = new JButton("▼ Indietro");
        btnLeft = new JButton("◄ Sinistra");
        btnRight = new JButton("► Destra");
        btnStop = new JButton("■ Stop");

        controlPanel.add(new JLabel());
        controlPanel.add(btnUp);
        controlPanel.add(new JLabel());
        controlPanel.add(btnLeft);
        controlPanel.add(btnStop);
        controlPanel.add(btnRight);
        controlPanel.add(new JLabel());
        controlPanel.add(btnDown);
        controlPanel.add(new JLabel());

        add(controlPanel, BorderLayout.CENTER);

        btnUp.addActionListener(e -> sendCommand("W"));
        btnDown.addActionListener(e -> sendCommand("S"));
        btnLeft.addActionListener(e -> sendCommand("A"));
        btnRight.addActionListener(e -> sendCommand("D"));
        btnStop.addActionListener(e -> sendCommand("X"));

        setupKeyListener();
        connectButton.addActionListener(e -> connectToServer());
    }

    private void connectToServer() {
        String ip = ipTextField.getText().trim();
        int port = Integer.parseInt(portTextField.getText().trim());

        try {
            socket = new Socket(ip, port);
            outStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(this, "Connesso a " + ip + ":" + port);
            System.out.println("Connesso al server!");

            // Thread per ricevere la velocità dal server e salvarla su file
            new Thread(() -> {
                try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("C:\\Users\\Esame\\Documents\\GitHub\\OneDirection\\velocita_log.txt", true))) {
                    String line;
                    while ((line = inStream.readLine()) != null) {
                        if (line.startsWith("VEL:")) {
                            String speed = line.split(":")[1].trim();
                            speedLabel.setText(speed);

                            // Aggiungi timestamp
                            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            String logEntry = timestamp + " - Velocità: " + speed;

                            // Scrive su file
                            logWriter.write(logEntry);
                            logWriter.newLine();
                            logWriter.flush();

                            System.out.println("LOG: " + logEntry);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nella connessione al server.");
        }
    }

    private void sendCommand(String command) {
        if (outStream != null) {
            outStream.println(command);
        }
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) sendCommand("W");
                if (e.getKeyCode() == KeyEvent.VK_S) sendCommand("S");
                if (e.getKeyCode() == KeyEvent.VK_A) sendCommand("A");
                if (e.getKeyCode() == KeyEvent.VK_D) sendCommand("D");
                if (e.getKeyCode() == KeyEvent.VK_X) sendCommand("X");
                if (e.getKeyCode() == KeyEvent.VK_1) sendCommand("1");
                if (e.getKeyCode() == KeyEvent.VK_2) sendCommand("2");
                if (e.getKeyCode() == KeyEvent.VK_3) sendCommand("3");
            }
        });
        setFocusable(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            guidaClient client = new guidaClient();
            client.setVisible(true);
        });
    }
}

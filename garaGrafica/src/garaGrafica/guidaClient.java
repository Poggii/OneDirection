package garaGrafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class guidaClient extends JFrame {
    private JTextField ipTextField;
    private JTextField portTextField;
    private JLabel speedLabel;
    private Socket socket;
    private PrintWriter outStream;

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
            JOptionPane.showMessageDialog(this, "Connesso a " + ip + ":" + port);
            System.out.println("Connesso al server!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore connessione: " + e.getMessage());
        }
    }

    private void sendCommand(String cmd) {
        if (outStream != null) {
            outStream.println(cmd);
        } else {
            JOptionPane.showMessageDialog(this, "Non connesso al server.");
        }
    }

    private void setupKeyListener() {
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String cmd;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> cmd = "W";
                    case KeyEvent.VK_S -> cmd = "S";
                    case KeyEvent.VK_A -> cmd = "A";
                    case KeyEvent.VK_D -> cmd = "D";
                    case KeyEvent.VK_X -> cmd = "X";
                    case KeyEvent.VK_1 -> cmd = "1";
                    case KeyEvent.VK_2-> cmd = "2";
                    case KeyEvent.VK_3 -> cmd = "3";
                    case KeyEvent.VK_Q -> cmd = "Q";
                    case KeyEvent.VK_R -> {
                        System.out.println("Restart comando inviato");
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                sendCommand(cmd);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            guidaClient remote = new guidaClient();
            remote.setVisible(true);
        });
    }
}

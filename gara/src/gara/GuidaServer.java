package gara;

import java.io.*;
import java.net.*;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class GuidaServer {
    public static void main(String[] args) {
        int PORT = 1234;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("EV3 in ascolto sulla porta " + PORT + "...");

            // Inizializzazione motori
            EV3LargeRegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
            EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);

            int velocitaMassima = 0;
            motorA.setSpeed(velocitaMassima);
            motorB.setSpeed(velocitaMassima);
            motorA.setAcceleration(5000);
            motorB.setAcceleration(5000);

            while (true) {
                try (
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    System.out.println("Client connesso: " + socket.getInetAddress());

                    // Copie finali per l'uso nel thread
                    final EV3LargeRegulatedMotor mA = motorA;
                    final EV3LargeRegulatedMotor mB = motorB;

                    // Thread per inviare velocità al client ogni secondo
                    Thread speedSender = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (!socket.isClosed()) {
                                    int speedA = mA.getSpeed();
                                    int speedB = mB.getSpeed();
                                    int media = (speedA + speedB) / 2;
                                    out.println("VEL:" + media);
                                    Thread.sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                System.out.println("Errore invio velocità: " + e.getMessage());
                            }
                        }
                    });
                    speedSender.start();

                    // Gestione comandi ricevuti
                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println("Ricevuto comando: " + command);

                        switch (command) {
                            case "W":
                                resetMotorsSpeed(motorA, motorB, velocitaMassima);
                                motorA.forward();
                                motorB.forward();
                                break;
                            case "S":
                                motorA.backward();
                                motorB.backward();
                                break;
                            case "A":
                                motorA.setSpeed(velocitaMassima / 2);
                                motorB.setSpeed(velocitaMassima);
                                motorA.forward();
                                motorB.forward();
                                break;
                            case "D":
                                motorA.setSpeed(velocitaMassima);
                                motorB.setSpeed(velocitaMassima / 2);
                                motorA.forward();
                                motorB.forward();
                                break;
                            case "1":
                                velocitaMassima = 400;
                                System.out.println("Marcia 1 impostata: velocità massima = " + velocitaMassima);
                                break;
                            case "2":
                                velocitaMassima = 800;
                                System.out.println("Marcia 2 impostata: velocità massima = " + velocitaMassima);
                                break;
                            case "3":
                                velocitaMassima = 1000;
                                System.out.println("Marcia 3 impostata: velocità massima = " + velocitaMassima);
                                break;
                            case "TURBO":
                                velocitaMassima = (int) motorA.getMaxSpeed();
                                System.out.println("Modalità TURBO attivata! Velocità massima = " + velocitaMassima);
                                break;
                            case "X":
                                motorA.stop();
                                motorB.stop();
                                System.out.println("Motori arrestati.");
                                break;
                            default:
                                System.out.println("Comando sconosciuto: " + command);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetMotorsSpeed(EV3LargeRegulatedMotor motorA, EV3LargeRegulatedMotor motorB, int velocitaMassima) {
        motorA.setSpeed(velocitaMassima);
        motorB.setSpeed(velocitaMassima);
        System.out.println("Velocità ripristinata a " + velocitaMassima);
    }
}

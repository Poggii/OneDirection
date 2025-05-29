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

            EV3LargeRegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
            EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);

            // Configurazione iniziale dei motori
            motorA.setSpeed(500);
            motorB.setSpeed(500);
            motorA.setAcceleration(5000);
            motorB.setAcceleration(5000);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    System.out.println("Client connesso: " + socket.getInetAddress());

                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println("Ricevuto comando: " + command);
                        executeCommand(command, motorA, motorB);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String command, EV3LargeRegulatedMotor motorA, EV3LargeRegulatedMotor motorB) {
        switch (command) {
            case "W":
                motorA.setSpeed(800);
                motorB.setSpeed(800);
                motorA.forward();
                motorB.forward();
                break;
            case "S":
                motorA.setSpeed(800);
                motorB.setSpeed(800);
                motorA.backward();
                motorB.backward();
                break;
            case "A":
                motorB.setSpeed(800);
                motorB.forward();
                motorA.setSpeed(500);
                motorA.forward();
                break;
            case "D":
                motorA.setSpeed(800);
                motorA.forward();
                motorB.setSpeed(500);
                motorB.forward();
                break;
            case "TURBO":
                motorA.setSpeed((int) motorA.getMaxSpeed());
                motorB.setSpeed((int) motorB.getMaxSpeed());
                break;
            case "X":
                motorA.stop();
                motorB.stop();
                break;
            default:
                System.out.println("Comando sconosciuto: " + command);
        }
    }
}

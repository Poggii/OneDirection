package gara;

import java.io.*;
import java.lang.Thread;
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
            
            
            motorA.setSpeed(500); // Velocità normale
            motorB.setSpeed(500);
            motorA.setAcceleration(5000); // Accelerazione veloce
            motorB.setAcceleration(5000);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println("Ricevuto comando: " + command);

                        switch (command) {
                            case "W": motorA.setSpeed(800); motorA.forward(); motorB.setSpeed(800); motorB.forward(); break;
                            case "S": motorA.setSpeed(800);  motorA.backward(); motorB.setSpeed(800); motorB.backward(); break;
                            case "A": motorB.setSpeed(800); motorB.forward(); motorA.setSpeed(500); motorA.forward();break;
                            case "D": motorA.setSpeed(800); motorA.forward(); motorB.setSpeed(500); motorB.forward(); /*try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} */motorA.setSpeed(800); motorA.forward(); motorB.setSpeed(800); motorB.forward();break;
                            case "TURBO":
                                motorA.setSpeed((int) motorA.getMaxSpeed()); // Turbo Mode!
                                motorB.setSpeed((int) motorB.getMaxSpeed());
                                break;
                            case "STOP": motorA.stop(); motorB.stop(); break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




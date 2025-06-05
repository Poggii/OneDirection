package gara;

import java.io.*;
import java.net.*;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class GuidaServer {
	private static EV3UltrasonicSensor sensore = new EV3UltrasonicSensor(SensorPort.S4); 
    private static SampleProvider distanza = sensore.getDistanceMode();
    final static float[] sample = new float[distanza.sampleSize()];
    private static float distanzaMin = 0.2f;

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
                    
                    Thread bipThread = new Thread(new Runnable() {
                		@Override
                		
                		public void run() 
                		{
                			while(true)
                			{
                				distanza.fetchSample(sample, 0);
                				float misura = sample[0];
                				if(misura < distanzaMin)
                				{
                					Sound.beep();
                				}
                				try
                				{
                					Thread.sleep(1000);
                				}
                				catch(InterruptedException e)
                				{
                					break;
                				}
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
                                motorA.setSpeed((int) (velocitaMassima / 2));
                                motorB.setSpeed(velocitaMassima);
                                motorA.forward();
                                motorB.forward();
								/*try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								motorA.setSpeed(velocitaMassima);
                                motorB.setSpeed(velocitaMassima);*/
                                break;
                            case "D":
                                motorA.setSpeed(velocitaMassima);
                                motorB.setSpeed((int) (velocitaMassima / 2));
                                motorA.forward();
                                motorB.forward();
                                /*try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								motorA.setSpeed(velocitaMassima);
                                motorB.setSpeed(velocitaMassima);*/
           
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
                                velocitaMassima = 1100;
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

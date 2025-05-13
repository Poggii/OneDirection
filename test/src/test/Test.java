package test;
import lejos.hardware.lcd.LCD;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Test {
    public static void main(String[] args) {
        LCD.drawString("Forza OneDirection!", 0, 2); // Disegna il testo sullo schermo
        Sound.beep();
        
        // Inizializzazione del sensore di distanza sulla porta S4
        EV3UltrasonicSensor distanceSensor = new EV3UltrasonicSensor(SensorPort.S4);
        SampleProvider sampleProvider = distanceSensor.getDistanceMode();
        float[] samples = new float[sampleProvider.sampleSize()];

        // Acquisizione del valore della distanza
        sampleProvider.fetchSample(samples, 0);
        /*try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ // Aspetta mezzo secondo per stabilizzare i dati
        sampleProvider.fetchSample(samples, 0);
        LCD.drawString("Distanza: " + samples[0] + " m", 2, 5);

        Button.waitForAnyPress(); // Attende la pressione di un pulsante prima di terminare
        distanceSensor.close(); // Chiude la connessione con il sensore
    }
}

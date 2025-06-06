package test;

/*public class Test {
    public static void main(String[] args) {
        LCD.drawString("Forza OneDirection!", 0, 2); // Disegna il testo sullo schermo
        Sound.beep();
        
        // Inizializzazione del sensore di distanza sulla porta S4
        EV3UltrasonicSensor distanceSensor = new EV3UltrasonicSensor(SensorPort.S4);
        SampleProvider sampleProvider = distanceSensor.getDistanceMode();
        float[] samples = new float[sampleProvider.sampleSize()];

        // Acquisizione del valore della distanza
        sampleProvider.fetchSample(samples, 0);
        sampleProvider.fetchSample(samples, 0);
        LCD.drawString("Distanza: " + samples[0] + " m", 2, 5);

        Button.waitForAnyPress(); // Attende la pressione di un pulsante prima di terminare
        distanceSensor.close(); // Chiude la connessione con il sensore
    }
}*/


//package *add package name here*;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class Test {

 /**
  * Diameter of a standard EV3 wheel in meters.
  */
 static final double WHEEL_DIAMETER = 0.056;
 /**
  * Distance between center of wheels in meters.
  */
 static final double WHEEL_SPACING = 0.12;

 // These variables are initialized by initRobot()
 static Brick brick;
 static EV3MediumRegulatedMotor clawMotor;
 static EV3LargeRegulatedMotor leftMotor;
 static EV3LargeRegulatedMotor rightMotor;
 static EV3UltrasonicSensorWrapper distanceSensor;

 // These variables are initalized by initPilot()
 static MovePilot pilot;
 static PoseProvider poseProvider;

 /**
  * Main function of program.
  */
 public static void main(String[] args) {
     initRobot();
     initPilot();

     System.out.println("Press any button to start!");
     Button.waitForAnyPress();

     // Add your code below here
 }

 /**
  * Instantiates a brick and all the motors and sensors of the robot.
  */
 public static void initRobot() {
     if (brick != null) {
         // Already initialized
         return;
     }
     Button.LEDPattern(5); // Flashing red
     System.out.println("Initializing...");

     brick = BrickFinder.getDefault();

     clawMotor = new EV3MediumRegulatedMotor(brick.getPort("C"));
     leftMotor = new EV3LargeRegulatedMotor(brick.getPort("A"));
     rightMotor = new EV3LargeRegulatedMotor(brick.getPort("B"));

     // Initialize sensors in separate threads because each take a lot of time.
    
   
     Thread distanceThread = new Thread(new Runnable() {
         @Override
         public void run() {
             while (distanceSensor == null) {
                 try {
                     distanceSensor = new EV3UltrasonicSensorWrapper(brick.getPort("S4"));
                 } catch (IllegalArgumentException e) {
                     System.err.println("Ultrasonic sensor: " + e.getMessage() + ". Retrying...");
                 }
             }
         }
     });

     distanceThread.start();

     // Wait for sensors to be initialized.
     try {
         distanceThread.join();
     } catch (InterruptedException e) {
         System.err.println(e.getMessage());
     }

     Button.LEDPattern(1); // Steady green
     Sound.beepSequenceUp();
     System.out.println("Ready!");
 }

 /**
  * Instantiates a MovePilot and PoseProvider with default parameters.<br>
  * Don't call this function if you plan to use leftMotor and rightMotor
  * directly to control the robot.
  */
 public static void initPilot() {
     initPilot(WHEEL_DIAMETER, WHEEL_SPACING);
 }

 /**
  * Instantiates a MovePilot and PoseProvider.<br>
  * Don't call this function if you plan to use leftMotor and rightMotor
  * directly to control the robot.
  *
  * @param wheelDiameter Diameter of wheels in meters
  * @param wheelSpacing Distance between center of wheels in meters.
  */
 public static void initPilot(double wheelDiameter, double wheelSpacing) {
     if (pilot != null) {
         // Already initialized
         return;
     }
     Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_SPACING / 2);
     Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_SPACING / 2);

     Chassis chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

     pilot = new MovePilot(chassis);
     poseProvider = new OdometryPoseProvider(pilot);

     // Set default speed to 0.3 m/s and acceleration to 0.9 m/s^2
     pilot.setLinearSpeed(0.3);
     pilot.setLinearAcceleration(0.9);

     // Set default turn speed to 180 deg/s and acceleration to 540 deg/s^2
     pilot.setAngularSpeed(180);
     pilot.setAngularAcceleration(540);
 }

 /**
  * Wrapper class to allow easier use of EV3TouchSensor.
  */
 


 /**
  * Wrapper class to allow easier use of EV3ColorSensor.
  */
 
 public static class EV3UltrasonicSensorWrapper extends EV3UltrasonicSensor {

     private final SampleProvider sampleProvider;
     private final float[] samples;

     public EV3UltrasonicSensorWrapper(Port port) {
         super(port);

         try {
             sampleProvider = super.getDistanceMode();
         } catch (IllegalArgumentException e) {
             super.close();
             throw e;
         }
         samples = new float[1];
     }

     /**
      * Measures distance to an object in front of the sensor
      *
      * @return The distance (in meters) to an object in front of the sensor.
      */
     public double getDistance() {
         sampleProvider.fetchSample(samples, 0);

         return samples[0];
     }
 }
}

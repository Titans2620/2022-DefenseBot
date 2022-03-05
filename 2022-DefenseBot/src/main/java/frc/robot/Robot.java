// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import javax.swing.JSpinner.DateEditor;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.cameraserver.*;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  WPI_TalonSRX topLeftDrive = new WPI_TalonSRX(1);
  WPI_TalonSRX bottomLeftDrive = new WPI_TalonSRX(2);
  WPI_TalonSRX bottomRightDrive = new WPI_TalonSRX(3);
  WPI_TalonSRX topRightDrive = new WPI_TalonSRX(4);
  WPI_TalonSRX testMotor = new WPI_TalonSRX(5);

  private AddressableLED led;
  private Timer ledTimer;
  private AddressableLEDBuffer ledBuffer;
  private int rainbowFirstPixelHue;
  private int prevValue = 0;
  private int currentValue = 0;
  private char state = 'I';
  private String ledState = "";

  Joystick controller = new Joystick(0);

  MotorControllerGroup leftDrive = new MotorControllerGroup(topLeftDrive, bottomLeftDrive);
  MotorControllerGroup rightDrive = new MotorControllerGroup(topRightDrive, bottomRightDrive);

  DifferentialDrive drive = new DifferentialDrive(leftDrive, rightDrive);

  Servo leftServo = new Servo(0);
  Servo rightServo = new Servo(1);

  static int LEFTSERVOHIGHGEAR = 50;
  static int LEFTSERVOLOWGEAR = 150;
  static int RIGHTSERVOHIGHGEAR = 150;
  static int RIGHTSERVOLOWGEAR = 30;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    led = new AddressableLED(9);
    ledBuffer = new AddressableLEDBuffer(10);
    led.setLength(ledBuffer.getLength());
    led.setData(ledBuffer);
    led.start();
    
    rainbow();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
      drive.arcadeDrive(-controller.getRawAxis(0), controller.getRawAxis(1));

      if(controller.getRawButton(1)){
          leftServo.setAngle(LEFTSERVOLOWGEAR);
          rightServo.setAngle(RIGHTSERVOLOWGEAR);
      
      }
      else{
        leftServo.setAngle(LEFTSERVOHIGHGEAR);
        rightServo.setAngle(RIGHTSERVOHIGHGEAR);
      }

      if(controller.getRawButton(2)) {
        testMotor.set(1);
      }
      else if(controller.getRawButton(3)){
        testMotor.set(-1);
      }
      else{
        testMotor.set(0);  
      }  
      CameraServer.startAutomaticCapture();
}

  


  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

public void rainbow(){
        
  for (var i = 0; i < ledBuffer.getLength(); i++) {
      
      final var hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
      // Set the value
      ledBuffer.setHSV(i, hue, 255, 128);
    }
    // Increase by to make the rainbow "move"
    rainbowFirstPixelHue += 3;
    // Check bounds
    rainbowFirstPixelHue %= 180;
    led.setData(ledBuffer);
  }
  void changeLEDState(String state){
      ledState = state;
  }
  public void solid(int color){
    for(int i = 0; i < ledBuffer.getLength(); i++){
        ledBuffer.setHSV(i, color, 255, 128);
    }
    led.setData(ledBuffer);

}
public void blink(int color, double duration){
  if(ledTimer.get() == 0){
      ledTimer.start();
  }
  else if(ledTimer.get() < duration){
      for(int i = 0; i < ledBuffer.getLength(); i++){
          ledBuffer.setHSV(i, color, 255, 128);
      }
  }
  else if(ledTimer.get() < duration * 2){
      for(int i = 0; i < ledBuffer.getLength(); i++){
          ledBuffer.setHSV(i, color, 255, 0);
      }
  }
  else{
      ledTimer.stop();
      ledTimer.reset();
  }
  
}
public void pulse(int color){
  if(state == 'I'){
  
      currentValue = (prevValue + 1);
      for (var i = 0; i < ledBuffer.getLength(); i++) {
          ledBuffer.setHSV(i, color, 255, currentValue);
    }
    if(currentValue == 128){
        prevValue = currentValue;
        state = 'D';
    }
  }
  if(state == 'D'){
      currentValue = (prevValue - 1);
      for (var i = 0; i < ledBuffer.getLength(); i++) {
          ledBuffer.setHSV(i, color, 255, currentValue);
    }
    if(currentValue == 0){
        state = 'I';
    }
  }
    // Increase by to make the rainbow "move"
    rainbowFirstPixelHue += 3;
    // Check bounds
    rainbowFirstPixelHue %= 180;
    led.setData(ledBuffer);
    prevValue = currentValue;
}
public void setWhite(){
  for(int i = 0; i < ledBuffer.getLength(); i++){
      ledBuffer.setHSV(i, 0, 0, 100);
  }
  led.setData(ledBuffer);
  
}
public void setOff(){
  for(int i = 0; i < ledBuffer.getLength(); i++){
      ledBuffer.setHSV(i, 0, 0, 0);
  }
  led.setData(ledBuffer);
}
  void setLED(){
      //Set LED
  switch(ledState){
      case "SolidRed":
          solid(0);
          break;
      case "SolidYellow":
          solid(20);
          break;   
      case "SolidBlue":
          solid(120);
          break;
      case "SolidGreen":
          solid(40);
          break;
      case "SolidPurple":
          solid(200);
          break;
      case "BlinkRed":
          blink(0, 0.5);
          break;
      case "BlinkYellow":
          blink(40, 0.5);
          break;   
      case "BlinkBlue":
          blink(120, 0.5);
          break;
      case "BlinkGreen":
          blink(40, 0.5);
          break;
      case "BlinkPurple":
          blink(200, 0.5);
          break;
      default:
          setWhite();
          break;
  }
  }
 

}

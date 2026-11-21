package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="Manual Drive 2")
@Config
public class ManualDriving2OpMode extends LinearOpMode {
    public static class Params {
        public double flywheelStartRpm = 3000;//speed when you press play
        public double flywheelStepRpm =50;//change per d-pad press
        public double sorterVelocity = 1.0;
    }

    public static Params PARAMS = new Params();

    MecanumDrive driveTrain;
    DcMotorEx flyWheel;
    Servo sorterServo;

    DcMotorEx intakeMotor;
    Servo leftIntakeServo;
    Servo rightIntakeServo;
    double flywheelTargetRpm;
    boolean flywheelOn;
    boolean previousDpadUp;
    boolean previousDpadDown;
    boolean previousB;
    boolean intakeOn;
    boolean previousRightBumper;


    static final double FLYWHEEL_TICKS_PER_REV = 28;
    static final double FLYWHEEL_MAX_RPM = 6000;


    @Override
    public void runOpMode() throws InterruptedException {
        driveTrain = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));

        flyWheel = hardwareMap.get(DcMotorEx.class, "flyWheel");
        sorterServo = hardwareMap.get(Servo.class, "sorter");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");

        leftIntakeServo = hardwareMap.get(Servo.class, "leftServo");
        rightIntakeServo = hardwareMap.get(Servo.class, "rightServo");

        this.waitForStart();
        flywheelTargetRpm=PARAMS.flywheelStartRpm;
        flywheelOn=true;
        intakeOn=true;


        while (opModeIsActive()) {
            boolean dpadUp = gamepad1.dpad_up;
            if (dpadUp && !previousDpadUp) {
                flywheelTargetRpm +=PARAMS.flywheelStepRpm;
            }
            previousDpadUp = dpadUp;

            boolean dpadDown = gamepad1.dpad_down;
            if (dpadDown && !previousDpadDown) {
                flywheelTargetRpm -=PARAMS.flywheelStepRpm;
            }
            previousDpadDown = dpadDown;

            flywheelTargetRpm = Math.max(0,Math.min(FLYWHEEL_MAX_RPM,flywheelTargetRpm));

            boolean buttonB = gamepad1.b;
            if (buttonB && !previousB) {
                flywheelOn =!flywheelOn;
            }
            previousB= buttonB;

            if(flywheelOn) {
                flyWheel.setVelocity(flywheelTargetRpm * FLYWHEEL_TICKS_PER_REV / 60);
            }else {
                flyWheel.setPower(0);
            }
            if (gamepad1.a) {
                sorterServo.setPosition(PARAMS.sorterVelocity);
            } else {
                sorterServo.setPosition(0.5);
            }
            boolean rightBumper=gamepad1.right_bumper;
            if(rightBumper && !previousRightBumper){
                intakeOn = !intakeOn;
            }
            previousRightBumper = rightBumper;

            String intakeState;
            if(gamepad1.left_bumper){
                setIntake(-1);
                intakeState = "REVERSE";
            } else if (intakeOn) {
                setIntake(1);
                intakeState = "ON";
            }else{
                setIntake(0);
                intakeState = "OFF";
            }


            driveTrain.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(-gamepad1.left_stick_y, -gamepad1.left_stick_x),
                    -gamepad1.right_stick_x

            ));
            double flywheelActualRpm = flyWheel.getVelocity()*60/FLYWHEEL_TICKS_PER_REV;
            telemetry.addData("Flywheel target rpm:", flywheelTargetRpm);
            telemetry.addData("Flywheel actual rpm:", flywheelActualRpm);
            telemetry.addData("flywheel state:", flywheelOn? "ON":"OFF");
            telemetry.addData("intake state:", intakeState);
            telemetry.update();
        }
    }
    //direction: 1=intake, 0= stop, -1= reverse
    void setIntake(double direction) {
        intakeMotor.setVelocity(direction*360*100, AngleUnit.DEGREES);
        leftIntakeServo.setPosition(0.5-0.5*direction);
        rightIntakeServo.setPosition(0.5+0.5*direction);
    }
}

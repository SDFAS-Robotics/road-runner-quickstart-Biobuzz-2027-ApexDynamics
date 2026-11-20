package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
@Config
public class ManualDrivingOpMode extends LinearOpMode {
    public static class Params {
        public double flywheelVelocity = 29;
        public double sorterVelocity = 1.0;
    }

    public static Params PARAMS = new Params();

    MecanumDrive driveTrain;
    DcMotorEx flyWheel;
    Servo sorterServo;

    DcMotorEx intakeServo;
    Servo leftIntakeServo;
    Servo rightIntakeServo;

    double RPM = 360.0 / 60;

    @Override
    public void runOpMode() throws InterruptedException {
        driveTrain = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));

        flyWheel = hardwareMap.get(DcMotorEx.class, "flyWheel");
        sorterServo = hardwareMap.get(Servo.class, "sorter");
        intakeServo = hardwareMap.get(DcMotorEx.class, "intake");

        leftIntakeServo = hardwareMap.get(Servo.class, "leftServo");
        rightIntakeServo = hardwareMap.get(Servo.class, "rightServo");

        this.waitForStart();

        while (opModeIsActive()) {
            flyWheel.setVelocity(PARAMS.flywheelVelocity*RPM, AngleUnit.DEGREES);
            if (gamepad1.a) {
                sorterServo.setPosition(PARAMS.sorterVelocity);
            } else {
                sorterServo.setPosition(0.5);
            }

            intakeServo.setVelocity(360 * 100, AngleUnit.DEGREES);

            leftIntakeServo.setPosition(-1.0);
            rightIntakeServo.setPosition(1.0);

            driveTrain.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(-gamepad1.left_stick_y, -gamepad1.left_stick_x),
                    -gamepad1.right_stick_x
            ));
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Simple time-based parking autonomous: wait, drive forward, strafe, stop.
 * It does not use the localizer, so it works before Road Runner is tuned.
 *
 * This class is abstract: it is not an OpMode by itself. AutoParkRed and
 * AutoParkBlue extend it and only choose the strafe direction.
 */
@Config
public abstract class AutoParkBase extends LinearOpMode {
    public static class Params {
        public double startDelaySec = 0;      // starting value of the delay; adjust with the d-pad in INIT
        public double delayStepSec = 1;       // change per d-pad press in INIT
        public double maxDelaySec = 20;

        public double forwardPower = 0.3;     // 0 to 1
        public double forwardTimeSec = 0.3;   // tune on the field until the robot moves about 5 in

        public double strafePower = 0.4;      // strafing needs a bit more power than driving forward
        public double strafeTimeSec = 0;      // 0 = no strafe; tune once the loading zone distance is known
    }

    public static Params PARAMS = new Params();

    MecanumDrive driveTrain;

    /** +1 strafes left, -1 strafes right. Each alliance's OpMode decides. */
    protected abstract double strafeDirection();

    @Override
    public void runOpMode() {
        driveTrain = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        // ---- INIT: let the driver choose the start delay with the d-pad ----
        double delaySec = PARAMS.startDelaySec;
        boolean previousDpadUp = false;
        boolean previousDpadDown = false;

        while (opModeInInit()) {
            boolean dpadUp = gamepad1.dpad_up;
            if (dpadUp && !previousDpadUp) {
                delaySec += PARAMS.delayStepSec;
            }
            previousDpadUp = dpadUp;

            boolean dpadDown = gamepad1.dpad_down;
            if (dpadDown && !previousDpadDown) {
                delaySec -= PARAMS.delayStepSec;
            }
            previousDpadDown = dpadDown;

            delaySec = Math.max(0, Math.min(PARAMS.maxDelaySec, delaySec));

            telemetry.addData("Start delay (s)", delaySec);
            telemetry.addLine("D-pad up/down to change, then press Play");
            telemetry.update();
        }

        // Stop pressed during INIT: do nothing.
        if (isStopRequested()) {
            return;
        }

        // ---- RUN ----
        waitFor("Waiting", delaySec);
        driveFor("Forward", PARAMS.forwardPower, 0, PARAMS.forwardTimeSec);
        driveFor("Strafe", 0, strafeDirection() * PARAMS.strafePower, PARAMS.strafeTimeSec);

        telemetry.addData("Step", "Parked");
        telemetry.update();
    }

    /**
     * Drives with the given powers for the given time, then stops.
     * forward: + is forward. left: + is left, - is right.
     * Stops early if the match is stopped.
     */
    void driveFor(String stepName, double forward, double left, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < seconds) {
            driveTrain.setDrivePowers(new PoseVelocity2d(new Vector2d(forward, left), 0));
            telemetry.addData("Step", stepName);
            telemetry.addData("Time (s)", "%.1f / %.1f", timer.seconds(), seconds);
            telemetry.update();
        }
        driveTrain.setDrivePowers(new PoseVelocity2d(new Vector2d(0, 0), 0));
    }

    /** Waits for the given time while showing a countdown. Stops early if the match is stopped. */
    void waitFor(String stepName, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < seconds) {
            telemetry.addData("Step", stepName);
            telemetry.addData("Time left (s)", "%.1f", seconds - timer.seconds());
            telemetry.update();
        }
    }
}

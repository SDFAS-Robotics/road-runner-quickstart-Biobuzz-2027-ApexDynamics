package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Auto Park Red", preselectTeleOp = "Manual Drive 2")
public class AutoParkRed extends AutoParkBase {
    @Override
    protected double strafeDirection() {
        return -1; // right. Swap with Blue if the loading zone is on the other side.
    }
}

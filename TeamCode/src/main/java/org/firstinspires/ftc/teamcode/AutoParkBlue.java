package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Auto Park Blue", preselectTeleOp = "Manual Drive 2")
public class AutoParkBlue extends AutoParkBase {
    @Override
    protected double strafeDirection() {
        return 1; // left. Swap with Red if the loading zone is on the other side.
    }
}

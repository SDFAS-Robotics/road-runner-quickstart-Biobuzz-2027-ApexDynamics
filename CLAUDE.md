# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

FTC (FIRST Tech Challenge) robot code for team ApexDynamics, forked from the Road Runner 1.0 quickstart. The app runs on an Android Robot Controller (Control Hub). Road Runner tuning docs: https://rr.brott.dev/docs/v1-0/tuning/

- `FtcRobotController/`: the official FTC SDK (currently v11.0). Treat it as vendored. Upstream SDK/quickstart updates arrive as merges, so don't edit it.
- `TeamCode/`: all team code, in package `org.firstinspires.ftc.teamcode`.

## Build

- Build the APK with `./gradlew :TeamCode:assembleDebug` (`gradlew.bat` on Windows). Deploy with `./gradlew :TeamCode:installDebug` while connected to the Control Hub over ADB, or use Android Studio's Run button.
- There are no unit tests and no linter. Real verification means running OpModes on the robot, selected from the Driver Station.
- The Gradle JDK is pinned to Android Studio's bundled JBR 21 through `org.gradle.java.home` in `gradle.properties`. That path is machine-specific. Source and target compatibility are Java 8 (`build.common.gradle`), so newer Java language features are unavailable.
- Road Runner and FTC Dashboard versions are set in `TeamCode/build.gradle` (repo `https://maven.brott.dev/`). SDK dependencies are in `build.dependencies.gradle`.

## Architecture

- **OpModes** are classes annotated `@TeleOp`/`@Autonomous`, extending `OpMode` or `LinearOpMode`. The SDK discovers them automatically.
- **Tuning OpModes** are registered programmatically, not through annotations: see `TuningOpModes.register` (`@OpModeRegistrar`) in `tuning/TuningOpModes.java`. `DRIVE_CLASS` selects `MecanumDrive` or `TankDrive`, and the registrar builds a `DriveView` from the drive's localizer type. So when you change localizers, `TuningOpModes` must still understand the new localizer.
- **Drive classes** (`MecanumDrive`, `TankDrive`) own the motors, IMU (`LazyImu`), and `localizer`, and expose:
  - `actionBuilder(Pose2d)`, which returns a `TrajectoryActionBuilder` and is used for autonomous paths. It yields `FollowTrajectoryAction`/`TurnAction`, which run with `Actions.runBlocking(...)`.
  - `updatePoseEstimate()`, which must be called each loop when driving manually (for example with `setDrivePowers`) so the pose stays current.
- **Localizers** implement `Localizer`. The default is the drive-encoder `DriveLocalizer` inner class. Alternatives are `TwoDeadWheelLocalizer`, `ThreeDeadWheelLocalizer`, `PinpointLocalizer`, and `OTOSLocalizer`. To switch, change the `localizer = new ...` line in the drive constructor.
- **Tunable parameters** live in `public static Params PARAMS` on `@Config` classes, so FTC Dashboard (`http://192.168.43.1:8080/dash` on the robot's network) can edit them live. Dashboard edits are not saved: copy the final values back into the source.
- **Logging**: `FlightRecorder.write(...)` with the types in `messages/` records data for Road Runner's log viewer. `Drawing.drawRobot` draws on the Dashboard field overlay.
- Hardware device names such as `leftFront`, `leftBack`, `rightBack`, `rightFront`, and `imu` must match the Robot Controller hardware configuration.

## Current state

The drivetrain is not tuned yet: `MecanumDrive.PARAMS` still holds placeholder values (`inPerTick = 1`, and so on), and the drive-encoder localizer is in use. Team TeleOp/Autonomous OpModes and mechanism code have not been written yet.
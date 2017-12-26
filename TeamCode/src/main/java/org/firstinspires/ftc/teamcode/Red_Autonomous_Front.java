package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Autonomous(name="Red Autonomous Front", group="K9bot")
//@Disabled

public class Red_Autonomous_Front extends LinearOpMode
{
    public enum VuPos {LEFT, RIGHT, CENTER}

    static private final boolean blnBlueAlliance = false;
    K9bot robot = new K9bot();
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;
    static final double     DRIVE_GEAR_REDUCTION    = .5625 ;
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode()
    {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Path 0",  "Starting at %7d :%7d",
                robot.leftMotor.getCurrentPosition(),
                robot.rightMotor.getCurrentPosition());
        telemetry.update();

        waitForStart();

        initPhase();//Sets default positions of all parts
        readJewel();//Hits correct jewel off platform
        robotMovement(getColumnPos());//Goes to proper column via VuMark
        endPhase();//Puts block down and pushes in
    }

    public void initPhase()
    {
        //Set servos to default positions
        robot.JSX.setPosition(.5);
        robot.JSY.setPosition(.7);
        robot.leftGripper.setPosition(0);
        robot.rightGripper.setPosition(1);
        robot.liftMotor.setPower(1);
        sleep(500);
        robot.liftMotor.setPower(0);
    }

    public boolean readingJewel = true;
    public void readJewel()
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable(){
            public void run()
            {
                boolean blnSensorRed = false;
                boolean blnSensorBlue = false;
                robot.colorSensor.enableLed(true);

                //set initial positions of JS2 and swing JS1 in between the balls
                robot.JSY.setPosition(.04);
                robot.JSX.setPosition(.5);
                sleep(1500);

                if (robot.colorSensor.blue() > robot.colorSensor.red()) //Test if blue
                {
                    blnSensorBlue = true;
                }
                else if (robot.colorSensor.red() > robot.colorSensor.blue()) //Test if red
                {
                    blnSensorRed = true;
                }
                if (blnSensorRed ^ blnSensorBlue) //Makes sure one color is true
                {
                    if (blnSensorBlue ^ blnBlueAlliance)
                    {
                        robot.JSX.setPosition(0);
                    }
                    else
                    {
                        robot.JSX.setPosition(1);
                    }
                }

                sleep(1000);
                robot.JSY.setPosition(.7);
                robot.JSX.setPosition(.5);
                readingJewel = false;
            }
        });
    }

    VuforiaLocalizer vuforia;
    public VuPos getColumnPos() //Vuforia code (reads the VuMark)
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "ARMl4sr/////AAAAGW7XCTx7E0rTsT4i0g6I9E8IY/EGEWdA5QHmgcnvsPFeuf+2cafgFWlJht6/m4ps4hdqUeDgqSaHurLTDfSET8oOvZUEOiMYDq2xVxNDQzW4Puz+Tl8pOFb1EfCrP28aBkcBkDfXDADiws03Ap/mD///h0HK5rVbe3KYhnefc0odh1F7ZZ1oxJy+A1w2Zb8JCXM/SWzAVvB1KEAnz87XRNeaJAon4c0gi9nLAdZlG0jnC6bx+m0140C76l14CTthmzSIdZMBkIb8/03aQIouFzLzz+K1fvXauT72TlDAbumhEak/s5pkN6L555F28Jf8KauwCnGyLnePxTm9/NKBQ4xW/bzWNpEdfY4CrBxFoSkq";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT; // Use FRONT Camera (Change to BACK if you want to use that one)
        parameters.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES; // Display Axes

        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);

        relicTrackables.activate(); // Activate Vuforia
        runtime.reset();
        while (opModeIsActive())
        {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) // Test to see if image is visable
            {
                if (vuMark == RelicRecoveryVuMark.LEFT)
                { // Test to see if Image is the "LEFT" image and display value.
                    telemetry.addData("VuMark is", "Left");
                    relicTrackables.deactivate(); // Deactivate Vuforia
                    return VuPos.LEFT;
                }
                else if (vuMark == RelicRecoveryVuMark.RIGHT)
                { // Test to see if Image is the "RIGHT" image and display values.
                    telemetry.addData("VuMark is", "Right");
                    relicTrackables.deactivate(); // Deactivate Vuforia
                    return VuPos.RIGHT;
                }
                else if (vuMark == RelicRecoveryVuMark.CENTER)
                { // Test to see if Image is the "CENTER" image and display values.
                    telemetry.addData("VuMark is", "Center");
                    relicTrackables.deactivate(); // Deactivate Vuforia
                    return VuPos.CENTER;
                }
            }
            else
            {
                telemetry.addData("VuMark", "not visible");
                if(runtime.seconds() >= 5)
                {
                    return VuPos.CENTER;
                }
            }
            telemetry.update();
        }
        return VuPos.CENTER;
    }

    public void encoderDrive(double speed, double leftInches, double rightInches)
    {
        int newLeftTarget;
        int newRightTarget;

        if (opModeIsActive())
        {

            newLeftTarget = robot.leftMotor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightMotor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            robot.leftMotor.setTargetPosition(newLeftTarget);
            robot.rightMotor.setTargetPosition(newRightTarget);

            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            robot.leftMotor.setPower(Math.abs(speed));
            robot.rightMotor.setPower(Math.abs(speed));

            while (opModeIsActive() && (robot.leftMotor.isBusy() && robot.rightMotor.isBusy()))
            {

                telemetry.addData("Path 1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path 2",  "Running at %7d :%7d",
                        robot.leftMotor.getCurrentPosition(),
                        robot.rightMotor.getCurrentPosition());
                telemetry.update();
            }

            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void robotMovement(VuPos enumColumn)
    {
        boolean robotMovementFinished = false;
        while (opModeIsActive() && !robotMovementFinished)
        {
            if (!readingJewel)
            {
                encoderDrive(robot.autonomousSpeed, 13, -13);//Turn Right

                switch (enumColumn)
                {
                    case LEFT:
                        encoderDrive(robot.autonomousSpeed, 43, 43); //Left
                        break;
                    case RIGHT:
                        encoderDrive(robot.autonomousSpeed, 28, 28); //Right
                        break;
                    case CENTER:
                        encoderDrive(robot.autonomousSpeed, 35.5, 35.5); //Center
                        break;
                }
                encoderDrive(robot.autonomousSpeed, 13, -13);//Turn Right
                encoderDrive(robot.autonomousSpeed, 9, 9);//Forward
                robotMovementFinished = true;
            }
        }
    }

    public void endPhase()
    {
        robot.liftMotor.setPower(-1);
        sleep(500);
        robot.liftMotor.setPower(0);

        robot.leftGripper.setPosition(.4);
        robot.rightGripper.setPosition(.6);
        sleep(1000);

        encoderDrive(.1, 4, 4);//Forward
        encoderDrive(robot.autonomousSpeed, -4, -4);//Backward

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
}
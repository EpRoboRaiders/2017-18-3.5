package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name="Red Autonomous Front", group="K9bot")
//@Disabled
public class Red_Autonomous_Front extends LinearOpMode
{

    static private final boolean BLUE_DESIRED = false;
    K9bot robot = new K9bot();
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;
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

        //Set servos to default positions
        robot.JSX.setPosition(.5);
        robot.JSY.setPosition(.7);
        robot.leftGripper.setPosition(0);
        robot.rightGripper.setPosition(1);
        robot.liftMotor.setPower(-1);
        sleep(1000);
        robot.liftMotor.setPower(0);

        ReadJewel(BLUE_DESIRED);

        encoderMovement(getColumnPos());

        robot.liftMotor.setPower(1);
        sleep(1000);
        robot.liftMotor.setPower(0);

        robot.leftGripper.setPosition(.4);
        robot.rightGripper.setPosition(.6);
        sleep(1000);

        encoderDrive(.1, 2, 2);//Forward
        encoderDrive(.5, -4, -4);//Backward

        telemetry.addData("Path", "Complete");
        telemetry.update();
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

            //  sleep(250);   // optional pause after each move
        }
    }
    public void ReadJewel(boolean JewelBlueDesired)
    {
        boolean SensorBlue;

        robot.colorSensor.enableLed(true);

        //set initial positions of JS2 and swing JS1 in between the balls
        robot.JSY.setPosition(.06);
        robot.JSX.setPosition(.5);
        sleep(1500);

        if(robot.colorSensor.blue()  > robot.colorSensor.red())
        {
            sleep(500);
            SensorBlue = true;
        }
        else
        {
            sleep(500);
            SensorBlue = false;
        }

        telemetry.addData("Jewel is ", (SensorBlue) ? "BLUE" : "RED");
        telemetry.update();

        if(SensorBlue ^ JewelBlueDesired)
        {
            robot.JSX.setPosition(0);
        }
        else
        {
            robot.JSX.setPosition(1);
        }
        sleep(1000);
        robot.JSY.setPosition(.7);
        robot.JSX.setPosition(.5);
    }

    public void encoderMovement(String intColumn)
    {
        encoderDrive(.5, 13, -13);//Turn Right

        switch(intColumn)
        {
            case "Left":
                encoderDrive(.5, 43, 43); //Left
                break;
            case "Right":
                encoderDrive(.5, 28, 28); //Right
                break;
            case "Center":
                encoderDrive(.5, 35.5, 35.5); //Center
                break;
        }
        encoderDrive(.5, 13, -13);//Turn Right
        encoderDrive(.5, 9, 9);//Forward
    }

    VuforiaLocalizer vuforia;

    public String getColumnPos()
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
                    return "Left";
                }
                else if (vuMark == RelicRecoveryVuMark.RIGHT)
                { // Test to see if Image is the "RIGHT" image and display values.
                    telemetry.addData("VuMark is", "Right");
                    relicTrackables.deactivate(); // Deactivate Vuforia
                    return "Right";
                }
                else if (vuMark == RelicRecoveryVuMark.CENTER)
                { // Test to see if Image is the "CENTER" image and display values.
                    telemetry.addData("VuMark is", "Center");
                    relicTrackables.deactivate(); // Deactivate Vuforia
                    return "Center";
                }
            }
            else
            {
                telemetry.addData("VuMark", "not visible");
                if(runtime.seconds() >= 5)
                {
                    return "Center";
                }
            }
            telemetry.update();
        }
        return "Center";
    }
}
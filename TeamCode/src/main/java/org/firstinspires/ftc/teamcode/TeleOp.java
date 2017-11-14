package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="K9bot")
//@Disabled
public class TeleOp extends LinearOpMode
{

    K9bot robot = new K9bot();

    ElapsedTime runtime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);


    @Override
    public void runOpMode()
    {

        //Initial declaration of variables used in TeleOp
        boolean blnChangeSpeed = true;
        boolean blnStillPressed = false;

        robot.init(hardwareMap);

        //Declare motors without encoders(don't put in K9)
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Start", "TeleOp Ready");
        telemetry.update();

        waitForStart();

        // Jewel
        robot.JSX.setPosition(.5);
        robot.JSY.setPosition(.7);

        while(opModeIsActive())
        {

            // Tank drive
            float fltLeft = gamepad1.left_stick_y;
            float fltRight = gamepad1.right_stick_y;

            if(gamepad1.x)
            {
                if(!blnStillPressed)
                {
                    blnChangeSpeed = !blnChangeSpeed;
                    blnStillPressed = true;
                }
            }
            else
            {
                blnStillPressed = false;
            }

            double blnSpeed = blnChangeSpeed ? 1 : .50;
            if(gamepad1.right_bumper)
            {
                robot.leftMotor.setPower(blnSpeed);
                robot.rightMotor.setPower(blnSpeed);
            }
            else if (gamepad1.left_bumper)
            {
                robot.leftMotor.setPower(-blnSpeed);
                robot.rightMotor.setPower(-blnSpeed);
            }
            else
            {
                robot.leftMotor.setPower(-fltLeft * blnSpeed);
                robot.rightMotor.setPower(-fltRight * blnSpeed);
            }

            // Claw
            if (gamepad2.right_bumper)
            {
                robot.claw.setPosition(.5);
            }
            else if (gamepad2.left_bumper)
            {
                robot.claw.setPosition(0);
            }

            // Claw Y Axis
            if (gamepad2.dpad_up)
            {
                robot.clawY.setPosition(1);
            }
            else if (gamepad2.dpad_down)
            {
                robot.clawY.setPosition(0);
            }

            // Lift
            float fltLift = gamepad2.left_stick_y;
            robot.liftMotor.setPower(fltLift);

            // Gripper
            if (gamepad2.b)
            {
                //Closed
                robot.leftGripper.setPosition(0);
                robot.rightGripper.setPosition(1);
            }
            else if (gamepad2.x)
            {
                //Open More for collecting
                robot.leftGripper.setPosition(.4);
                robot.rightGripper.setPosition(.6);
            }
            else if (gamepad2.y)
            {
                //Open Less for moving away from the cryptobox
                robot.leftGripper.setPosition(.2);
                robot.rightGripper.setPosition(.8);
            }

            // Feedback
            telemetry.addData("Drive speed is ", (blnChangeSpeed) ? "100%" : "50%");
            telemetry.addData("Lift Motor(Glyph)",  "%.2f", fltLift);
            telemetry.update();
            // Pause for 40 mS each cycle = update 25 times a second.
            sleep(40);
        }
    }
}

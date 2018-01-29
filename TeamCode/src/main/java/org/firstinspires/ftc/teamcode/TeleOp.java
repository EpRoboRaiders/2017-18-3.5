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
        double slowSpeed = .5;

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

            //setting default speed

            if(gamepad1.dpad_down)
            {
                blnChangeSpeed = true;
            }
            else if(gamepad1.dpad_right)
            {
                slowSpeed = .75;
                blnChangeSpeed = false;
            }
            else if(gamepad1.dpad_up)
            {
                slowSpeed = .50;
                blnChangeSpeed = false;
            }
            else if(gamepad1.dpad_left)
            {
                slowSpeed = .25;
                blnChangeSpeed = false;
            }
            else if(gamepad1.x)
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
            slowSpeed = blnChangeSpeed ? .5 : slowSpeed;
            double dblSpeed = blnChangeSpeed ? 1 : slowSpeed;

            if(gamepad1.right_bumper)
            {
                robot.leftMotor.setPower(dblSpeed);
                robot.rightMotor.setPower(dblSpeed);
            }
            else if (gamepad1.left_bumper)
            {
                robot.leftMotor.setPower(-dblSpeed);
                robot.rightMotor.setPower(-dblSpeed);
            }
            else
            {
                robot.leftMotor.setPower(-fltLeft * dblSpeed);
                robot.rightMotor.setPower(-fltRight * dblSpeed);
            }

            // Lift
            float fltLift = gamepad2.left_stick_y;
            robot.liftMotor.setPower(-fltLift);

            // Gripper
            if (gamepad2.b)
            {
                //Closed
                /*
                robot.leftGripper.setPosition(0);
                robot.rightGripper.setPosition(1);
                robot.TopLeftGripper.setPosition(1);
                robot.TopRightGripper.setPosition(0);
                */
                robot.TopLeftBottomRightGripper.setPosition(1);
                robot.TopRightBottomLeftGripper.setPosition(0);

            }
            else if (gamepad2.x)
            {
                //Open More for collecting
                /*
                robot.leftGripper.setPosition(.4);
                robot.rightGripper.setPosition(.6);
                robot.TopLeftGripper.setPosition(.6);
                robot.TopRightGripper.setPosition(.4);
                */
                robot.TopLeftBottomRightGripper.setPosition(.5);
                robot.TopRightBottomLeftGripper.setPosition(.5);

            }
            else if (gamepad2.y)
            {
                //Open Less for moving away from the cryptobox
                /*
                robot.leftGripper.setPosition(.2);
                robot.rightGripper.setPosition(.8);
                robot.TopLeftGripper.setPosition(.8);
                robot.TopRightGripper.setPosition(.2);
                */
                robot.TopLeftBottomRightGripper.setPosition(.7);
                robot.TopRightBottomLeftGripper.setPosition(.3);

            }

            // Feedback
            telemetry.addLine("Driver Feedback");
            telemetry.addData("Drive speed is ", (blnChangeSpeed) ? "100%" : slowSpeed + "%");
            telemetry.addData("Lift Motor(Glyph)",  "%.2f", fltLift);
            telemetry.update();
            // Pause for 40 mS each cycle = update 25 times a second.
            sleep(40);
        }
    }
}

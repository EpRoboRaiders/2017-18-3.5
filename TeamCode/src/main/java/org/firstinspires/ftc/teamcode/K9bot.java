/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DistanceSensor;

/*************************************************************************
            Hungarian Notation Key
            ----------------------
arr - Array
bln - Boolean
byt - Byte
cur - Currency
flt - Float
dbl - Double
int - Integer
sht - Short
lng - Long
obj - Object
str - String
vnt - Variant

***************************************************************************/

public class K9bot
{
    /* Public OpMode members. */
    public DcMotor liftMotor = null;
    public DcMotor leftMotor = null;
    public DcMotor rightMotor = null;
    public Servo leftGripper = null;
    public Servo rightGripper = null;
    public DcMotor relicMotor = null;
    public Servo claw = null;
    public Servo clawY = null;
    //JSX = X movement of jewel, JSY = Y movement of jewel.
    public Servo JSY = null;
    public Servo JSX = null;
    public ColorSensor colorSensor = null;

    /* Local OpMode members. */
    HardwareMap hwMap  = null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public K9bot()
    {
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap)
    {
        // save reference to HW Map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftMotor = hwMap.get(DcMotor.class, "LeftMotor");
        rightMotor = hwMap.get(DcMotor.class, "RightMotor");
        liftMotor = hwMap.get(DcMotor.class, "LiftMotor");
        relicMotor = hwMap.get(DcMotor.class, "RelicMotor");

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        liftMotor.setPower(0);
        relicMotor.setPower(0);

        // Set motors direction
        leftMotor.setDirection(DcMotor.Direction.REVERSE);

        // Define and initialize ALL installed servos.
        leftGripper = hwMap.get(Servo.class, "Left Gripper");
        rightGripper = hwMap.get(Servo.class, "Right Gripper");
        claw = hwMap.get(Servo.class, "Claw");
        clawY = hwMap.get(Servo.class, "Claw Y");
        JSY = hwMap.get(Servo.class, "Jewel Servo One");
        JSX = hwMap.get(Servo.class, "Jewel Servo Two");

        // Define color/distance sensor
        colorSensor = hwMap.get(ColorSensor.class, "Color Sensor");
    }
}

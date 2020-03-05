package eora3D;

import java.util.List;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

/*
 * EORA3D - TURRET
 * * SLASER SERVICE
 * LASER STATUS - READ,WRITE_NO_RESPONSE,8 bits (00 laser off, 01 laser on)
 * * SMOTOR SERVICE

 * MOTOR CPOS - READ, 32 bits, default 0
 * MOTOR IPOS - WRITE_NOT_RESPONSE (Move to position. 00 scan right, then back to left endstop. Invert bytes! End is at 0x2300 ish)
 * MOTOR MMODE - READ, NOTIFY, 8bits - default 1
 * MOTOR SMODE - READ, WRITE_NO_RESPONSE, 8bits - default 1
 * MOTOR SPEED - READ, WRITE, 8 bits - default 0 (bigger is slower)

 * * SLED SERVICE
 * LED TYPE - READ, NOTIFY, WRITE_NO_RESPONSE, 8 bits, default 0xf6484700 (RGBI)
 * 
 * EORA3D - TTABLE
 * * TIO SERVICE
 * IO INPUT - WRITE_NO_RESPONSE
 * IO OUTPUT - READ, NOTIFY

 * * TMOTOR SERVICE
 * MOTOR CPOS - READ, NOTIFY
 * MOTOR IPOS - READ, WRITE_NO_RESPONSE
 * MOTOR MMODE - READ, NOTIFY
 * MOTOR SMODE - READ, WRITE_NO_RESPONSE
 * MOTOR SPEED - READ, WRITE_NO_RESPONSE
 * MOTOR ACCEL - READ, WRITE_NO_RESPONSE

 */

public class eora3D_bluetooth {
    String UUID_laser_service = "534c4153-4552-2053-4552-564943452020";
    String UUID_laser_status = "4c415345-5220-5354-4154-555320202020";
    String UUID_laser_motor_service = "534d4f54-4f52-2053-4552-564943452020";
    String UUID_laser_motor_cpos = "4d4f544f-5220-4350-4f53-202020202020";
    String UUID_laser_motor_ipos = "4d4f544f-5220-4950-4f53-202020202020";
    String UUID_laser_motor_mmode = "4d4f544f-5220-4d4d-4f44-452020202020";
    String UUID_laser_motor_smode = "4d4f544f-5220-534d-4f44-452020202020";
    String UUID_laser_motor_speed = "4d4f544f-5220-5350-4545-442020202020";
    String UUID_laser_sled_service = "534c4544-2053-4552-5649-434520202020";
    String UUID_laser_led_type = "4c454420-5459-5045-2020-202020202020";

    String UUID_turntable_tio_service = "54494f20-5345-5256-4943-452020202020";
    String UUID_turntable_io_input = "494f2049-4e50-5554-2020-202020202020";
    String UUID_turntable_io_output = "494f204f-5554-5055-5420-202020202020";
    String UUID_turntable_tmotor_service = "544d4f54-4f52-2053-4552-564943452020";
    String UUID_turntable_motor_cpos = "4d4f544f-5220-4350-4f53-202020202020";
    String UUID_turntable_motor_ipos = "4d4f544f-5220-4950-4f53-202020202020";
    String UUID_turntable_motor_mmode = "4d4f544f-5220-4d4d-4f44-452020202020";
    String UUID_turntable_motor_smode = "4d4f544f-5220-534d-4f44-452020202020";
    String UUID_turntable_motor_speed = "4d4f544f-5220-5350-4545-442020202020";
    String UUID_turntable_motor_accel = "4d4f544f-5220-4143-4345-4c2020202020";
    
    public eora3D_bluetooth()
    {
    }

    void getDevices() throws InterruptedException
    {

    }

 
    public void discover()
    {

    }
    
 
    public boolean setLaserStatus(boolean on)
    {
    	return false;
    }

	public boolean setLEDColour(int red, int green, int blue)
	{
		return false;
	}

	public boolean setMotorPos(int a_pos)
	{
 
        return false;
	}

	public boolean setMotorSpeed(int a_speed)
	{
		return false;
	}

	public boolean setTurntableMotorSpeed(int a_speed)
	{
		return false;
	}

	public boolean setTurntableMotorPos(int a_pos)
	{

        return false;
	}

	public boolean setTurntableMotorAccel(int a_accel)
	{
		return false;
	}

	public void cleanup() 
	{
 
	}

	public boolean laserOk() {
		return false;
	}

	public boolean turntableOk() {
		return false;
	}

	public String getLaserName() {
		return "Not found";
	}

	public String getTurntableName() {
		return "Not found";
	}
}

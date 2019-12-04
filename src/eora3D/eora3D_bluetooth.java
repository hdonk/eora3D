package eora3D;

import tinyb.*;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;
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

    private BluetoothManager manager;
    public List<BluetoothDevice> devices = null;
    
    BluetoothDevice laser = null;
    BluetoothDevice turntable = null;
    
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
    
	static void printDevice(BluetoothDevice device) {
        System.out.print("Address = " + device.getAddress());
        System.out.print(" Name = " + device.getName());
        System.out.print(" Connected = " + device.getConnected());
        System.out.println();
    }

    void getDevices() throws InterruptedException {
        BluetoothManager manager = BluetoothManager.getBluetoothManager();
        
        devices = manager.getDevices();

        for (BluetoothDevice device : devices) {
            printDevice(device);
        }
    }

    static BluetoothGattService getService(BluetoothDevice device, String UUID) throws InterruptedException {
        System.out.println("Services exposed by device:");
        BluetoothGattService tempService = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = device.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService service : bluetoothServices) {
                System.out.println("UUID: " + service.getUUID());
                if (service.getUUID().equals(UUID))
                    tempService = service;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty());
        return tempService;
    }

    static BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String UUID) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        if (characteristics == null)
            return null;

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUUID().equals(UUID))
                return characteristic;
        }
        return null;
    }

    public void discover() {
    	if(manager==null)
    		manager = BluetoothManager.getBluetoothManager();

        /*
         * The manager will try to initialize a BluetoothAdapter if any adapter is present in the system. To initialize
         * discovery we can call startDiscovery, which will put the default adapter in discovery mode.
         */
        boolean discoveryStarted = manager.startDiscovery();

        System.out.println("The discovery started: " + (discoveryStarted ? "true" : "false"));
        try {
			getDevices();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        /*
         * After we find the device we can stop looking for other devices.
         */
        try {
            manager.stopDiscovery();
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped.");
        }
    }
    
    void setLaser(BluetoothDevice a_laser)
    {
    	if(laser != null)
    		laser.disconnect();
    	laser = a_laser;
    	if(laser != null)
    		laser.connect();
    }
    
    void setTurntable(BluetoothDevice a_turntable)
    {
    	if(turntable != null)
    		turntable.disconnect();
    	turntable = a_turntable;
    	if(turntable != null)
    		turntable.connect();
    }
    
    void setLaserStatus(boolean on)
    {
    	if(laser == null) return;
        
        BluetoothGattService laserService = null;
		try {
			laserService = getService(laser, UUID_laser_service);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (laserService == null) {
            System.err.println("This device does not have the laser service we are looking for.");
            return;
        }
        System.out.println("Found laser service " + laserService.getUUID());

        BluetoothGattCharacteristic laserStatus = getCharacteristic(laserService, UUID_laser_status);

        if (laserStatus==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return;
        }

        System.out.println("Found the laser status characteristic");

        /*
         * Turn on the Temperature Service by writing 1 in the configuration characteristic, as mentioned in the PDF
         * mentioned above. We could also modify the update interval, by writing in the period characteristic, but the
         * default 1s is good enough for our purposes.
         */
        if(on) {
        	byte[] config = { 0x01 };
        	laserStatus.writeValue(config);
        }
        else
        {
        	byte[] config = { 0x00 };
        	laserStatus.writeValue(config);
        }
    }
}

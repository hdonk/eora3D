package eora3D;

import tinyb.*;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;

class ValueNotification implements BluetoothNotification<byte[]> {
	Object m_lock;
	boolean complete = true;
	
	ValueNotification()
	{
		super();
		m_lock = new String();
	}
	
	void unsetcomplete()
	{
		synchronized(m_lock)
		{
			complete = false;
		}
	}

	boolean waitforcomplete(int timeout)
	{
		while(!complete)
		{
			synchronized(m_lock)
			{
				try {
					m_lock.wait(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				if(!complete) return false;
			}
		}
		return true;
	}
	
    public void run(byte[] tempRaw)
    {
/*        System.out.print("ValueNotification raw = {");
        for (byte b : tempRaw) {
            System.out.print(String.format("%02x,", b));
        }
        System.out.print("}");*/
        if(tempRaw[0] == 1)
        {
        	synchronized(m_lock)
        	{
        		complete = true;
        		m_lock.notifyAll();
        	}
    	}
    }

}


public class eora3D_bluetooth_tinyb extends eora3D_bluetooth {

    private BluetoothManager manager;
    public List<BluetoothDevice> devices = null;
    
    BluetoothDevice laser = null;
    List<BluetoothGattService> laserServices = null;    
    private BluetoothGattCharacteristic m_motorMMODE = null;
	private ValueNotification m_motorNotification;
    
    BluetoothDevice turntable = null;
    List<BluetoothGattService> turntableServices = null;
    private BluetoothGattCharacteristic m_tmotorMMODE = null;
	private ValueNotification m_tmotorNotification;

	static void printDevice(BluetoothDevice device) {
        System.out.print("Address = " + device.getAddress());
        System.out.print(" Name = " + device.getName());
        System.out.print(" Connected = " + device.getConnected());
        System.out.println();
    }

    void getDevices() throws InterruptedException {
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
//                System.out.println("UUID: " + service.getUUID());
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
         * After we find the devices we can stop looking for other devices.
         */
        try {
            manager.stopDiscovery();
            System.out.println("The discovery stopped");
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped.");
        }
    }
    
    BluetoothGattCharacteristic getLaserCharacteristic(String serviceUUID, String characteristicUUID)
    {
    	if(laserServices == null)
    	{
    		System.out.println("No laser services found");
    		return null;
    	}
        for (BluetoothGattService service : laserServices) {
            //System.out.println("serviceUUID: " + service.getUUID());
            if (service.getUUID().equals(serviceUUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    //System.out.println("characteristicUUID: " + characteristic.getUUID());
                    if (characteristic.getUUID().equals(characteristicUUID))
                        return characteristic;
                }
            }
        } 
        return null;
    }
    
    BluetoothGattCharacteristic getTurntableCharacteristic(String serviceUUID, String characteristicUUID)
    {
    	if(turntableServices == null)
    	{
    		System.out.println("No turntable services found");
    		return null;
    	}
        for (BluetoothGattService service : turntableServices) {
            //System.out.println("serviceUUID: " + service.getUUID());
            if (service.getUUID().equals(serviceUUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    //System.out.println("characteristicUUID: " + characteristic.getUUID());
                    if (characteristic.getUUID().equals(characteristicUUID))
                        return characteristic;
                }
            }
        }
        System.out.println("No tt services");
        return null;
    }
    
    boolean setLaser(BluetoothDevice a_laser)
    {
    	if(laser != null)
    		laser.disconnect();
    	laser = a_laser;

    	if(laser != null)
    		laser.connect();
    	// Wait for connect
    	laserServices = laser.getServices();
    	if(laserServices==null)
		{
    		System.out.println("No laser services?");
            laser.disconnect();
            laser = null;
    		return false;
		}
        m_motorMMODE = getLaserCharacteristic(UUID_laser_motor_service, UUID_laser_motor_mmode);

        if (m_motorMMODE==null)
        {
            System.err.println("Could not find the correct characteristic for MMODE.");
            laserServices = null;
            laser.disconnect();
            laser = null;
            return false;
        }

        m_motorNotification = new ValueNotification();
        m_motorMMODE.enableValueNotifications(m_motorNotification);

        return true;
    }
    
    boolean setTurntable(BluetoothDevice a_turntable)
    {
    	if(turntable != null)
    		turntable.disconnect();
    	turntable = a_turntable;
    	if(turntable != null)
    	{
    		try
    		{
    			turntable.connect();
    		} catch(Exception e)
    		{
    			e.printStackTrace();
                turntable = null;
        		return false;
    		}
    	}
    	turntableServices = turntable.getServices();
    	if(turntableServices==null)
		{
    		System.out.println("No turntable services?");
            turntable.disconnect();
            turntable = null;
    		return false;
		}
        m_tmotorMMODE = getTurntableCharacteristic(UUID_turntable_tmotor_service, UUID_turntable_motor_mmode);

        if (m_tmotorMMODE==null)
        {
            System.err.println("Could not find the correct characteristic for turntable MMODE.");
            turntableServices = null;
            turntable.disconnect();
            turntable = null;
            return false;
        }

        m_tmotorNotification = new ValueNotification();
        m_tmotorMMODE.enableValueNotifications(m_tmotorNotification);

        return true;
    }
    
    public boolean setLaserStatus(boolean on)
    {
    	if(laser == null) return false;
        
/*        BluetoothGattService laserService = null;
        
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
*/
        BluetoothGattCharacteristic laserStatus = getLaserCharacteristic(UUID_laser_service, UUID_laser_status);

        if (laserStatus==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
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
        return true;
    }

	public boolean setLEDColour(int red, int green, int blue) {
    	byte[] colour = { (byte) red, (byte) green, (byte) blue, 0 };
    	if(laser == null) return false;
        BluetoothGattCharacteristic ledStatus = getLaserCharacteristic(UUID_laser_sled_service, UUID_laser_led_type);

        if (ledStatus==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

        System.out.println("Found the led status characteristic");
        ledStatus.writeValue(colour);
        return true;
	}

	public boolean setMotorPos(int a_pos) {
    	byte[] l_pos = { (byte) (a_pos&0xff), (byte) ((a_pos&0xff00)>>8)};
//    	byte[] l_pos = { (byte) ((a_pos&0xff00)>>8), (byte) (a_pos&0xff) };

    	if(laser == null) return false;
    	
    	BluetoothGattCharacteristic motorIPOS = getLaserCharacteristic(UUID_laser_motor_service, UUID_laser_motor_ipos);

        if (motorIPOS==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

//        System.out.println("Found the motor ipos characteristic");
        m_motorNotification.unsetcomplete();
        motorIPOS.writeValue(l_pos);
        if(!m_motorNotification.waitforcomplete(20000))
        {
        	System.out.println("Motor move not completed in 20 seconds.");
        	return false;
        }
        return true;
	}

	public boolean setMotorSpeed(int a_speed) {
    	byte[] l_speed = { (byte) a_speed};
    	if(laser == null) return false;
        BluetoothGattCharacteristic motorSpeed = getLaserCharacteristic(UUID_laser_motor_service, UUID_laser_motor_speed);

        if (motorSpeed==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

        System.out.println("Found the motor speed characteristic");
        motorSpeed.writeValue(l_speed);
        return true;
	}

	public boolean setTurntableMotorSpeed(int a_speed) {
    	byte[] l_speed = { (byte) a_speed};
    	if(turntable == null) return false;
        BluetoothGattCharacteristic motorSpeed = getTurntableCharacteristic(UUID_turntable_tmotor_service,
        			UUID_turntable_motor_speed);

        if (motorSpeed==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

        System.out.println("Found the motor speed characteristic");
        motorSpeed.writeValue(l_speed);
        return true;
	}

	public boolean setTurntableMotorPos(int a_pos) {
    	byte[] l_pos = { (byte) (a_pos&0xff), (byte) ((a_pos&0xff00)>>8)};

    	if(turntable == null) return false;
        BluetoothGattCharacteristic motorIPOS = getTurntableCharacteristic(UUID_turntable_tmotor_service,
        			UUID_turntable_motor_ipos);

        if (motorIPOS==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

        System.out.println("Found the motor ipos characteristic");
	
        m_tmotorNotification.unsetcomplete();
        motorIPOS.writeValue(l_pos);
        if(!m_tmotorNotification.waitforcomplete(20000))
        {
        	System.out.println("Motor move not completed in 20 seconds.");
        	return false;
        }
        return true;
}

	public boolean setTurntableMotorAccel(int a_accel) {
    	byte[] l_accel = { (byte) a_accel};
    	if(turntable == null) return false;
        BluetoothGattCharacteristic motorAccel = getTurntableCharacteristic(UUID_turntable_tmotor_service,
        			UUID_turntable_motor_accel);

        if (motorAccel==null)
        {
            System.err.println("Could not find the correct characteristic.");
            return false;
        }

        System.out.println("Found the motor accel characteristic");
        motorAccel.writeValue(l_accel);
        return true;
	}

	public void cleanup() {
        if(laser!=null) laser.disconnect();
        if(turntable!=null) turntable.disconnect();

    	devices = manager.getDevices();

    	System.out.println("Cleanup");
        for (BluetoothDevice device : devices) {
            printDevice(device);
        	device.remove();
        }
        
        manager = null;
	}
}

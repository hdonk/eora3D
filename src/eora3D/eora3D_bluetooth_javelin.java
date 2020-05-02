package eora3D;
import javelin.javelin;
import tinyb.BluetoothGattCharacteristic;

public class eora3D_bluetooth_javelin extends eora3D_bluetooth {
	javelin m_javelin;
 
	String m_laser_id = null;
	String m_laser = null;
	String m_laser_services[] = null;
	String m_turntable_id = null;
	String m_turntable = null;
	String m_turntable_services[] = null;
	
	public eora3D_bluetooth_javelin()
	{
		System.out.println("Loading dlls");
		if(false)
		{
			System.loadLibrary("msvcp140d_app");
			System.loadLibrary("vcruntime140_1d_app");
			System.loadLibrary("VCRUNTIME140D_APP");
			System.loadLibrary("CONCRT140D_APP");
			System.loadLibrary("ucrtbased");
		}
		else
		{
			System.loadLibrary("msvcp140_app");
			System.loadLibrary("vcruntime140_1_app");
			System.loadLibrary("VCRUNTIME140_APP");
			System.loadLibrary("CONCRT140_APP");
			System.loadLibrary("ucrtbase");			
		}
		System.loadLibrary("api-ms-win-core-synch-l1-2-0");
		System.loadLibrary("api-ms-win-core-synch-l1-1-0");
		System.loadLibrary("api-ms-win-core-processthreads-l1-1-0");
		System.loadLibrary("api-ms-win-core-debug-l1-1-0");
		System.loadLibrary("api-ms-win-core-errorhandling-l1-1-0");
		System.loadLibrary("api-ms-win-core-string-l1-1-0");
		System.loadLibrary("api-ms-win-core-profile-l1-1-0");
		System.loadLibrary("api-ms-win-core-sysinfo-l1-1-0");
		System.loadLibrary("api-ms-win-core-interlocked-l1-1-0");
		System.loadLibrary("api-ms-win-core-winrt-l1-1-0");
		System.loadLibrary("api-ms-win-core-heap-l1-1-0");
		System.loadLibrary("api-ms-win-core-memory-l1-1-0");
		System.loadLibrary("api-ms-win-core-libraryloader-l1-2-0");
		System.loadLibrary("OLEAUT32");
		System.loadLibrary("javelin");

		m_javelin = new javelin();
	}

	public void discover()
	{
		String l_devices[] = javelin.listBLEDevices();
		
		if(l_devices != null)
		{
			for(String l_device: l_devices)
			{
				//System.out.println(" "+l_device);
				String l_name = javelin.getBLEDeviceName(l_device);
				//System.out.println("  Name: "+l_name);
				if(l_name.startsWith("E3DT"))
				{
					System.out.println("Found turntable");
					m_turntable_services = javelin.listBLEDeviceServices(l_device);
					if(m_turntable_services!=null)
					{
						m_turntable = l_name;
						m_turntable_id = l_device;
						
						String l_chars[];
						for(String l_service: m_turntable_services)
						{
							l_chars = javelin.listBLEServiceCharacteristics(l_device, l_service);
							/*for(String l_char : l_chars)
							{
								System.out.println("Char: "+l_char);
							}*/
						}
					}
				}
				if(l_name.startsWith("E3DS"))
				{
					System.out.println("Found laser");
					m_laser_services = javelin.listBLEDeviceServices(l_device);
					if(m_laser_services!=null)
					{
						m_laser = l_name;
						m_laser_id = l_device;

						String l_chars[];
						for(String l_service: m_laser_services)
						{
							l_chars = javelin.listBLEServiceCharacteristics(l_device, l_service);
							/*for(String l_char : l_chars)
							{
								System.out.println("Char: "+l_char);
							}*/
						}
}
				}
			}
		}
	}

    public boolean setLaserStatus(boolean on)
    {
		byte[] l_status = { 0 };
		if(!laserOk()) return false;
		if(on) l_status[0] = 1;
    	if(!javelin.setBLECharacteristicValue(m_laser_id,
    			UUID_laser_service.toUpperCase(),
    			UUID_laser_status.toUpperCase(),
    			l_status))
    	{
    		System.err.println("Failed to set laser status");
    		return false;
    	}
    	return true;
    }

	public boolean setLEDColour(int red, int green, int blue)
	{
		byte[] l_colour = { (byte) red, (byte) green, (byte) blue, 0 };
		if(!laserOk()) return false;
    	if(!javelin.setBLECharacteristicValue(m_laser_id,
    			UUID_laser_motor_service.toUpperCase(),
    			UUID_laser_motor_ipos.toUpperCase(),
    			l_colour))
    	{
    		System.err.println("Failed to set laser colour");
    		return false;
    	}
    	return true;
	}

	public boolean setMotorPos(int a_pos)
	{
		byte[] l_pos = { (byte) (a_pos&0xff), (byte) ((a_pos&0xff00)>>8)};
    	if(!laserOk())
		{
    		System.err.println("!laserOk");
    		return false;
		}
    	if(!javelin.watchBLECharacteristicChanges(m_laser_id,
    			UUID_laser_motor_service.toUpperCase(),
    			UUID_laser_motor_mmode.toUpperCase()) )
		{
    		System.err.println("Failed to add laser motor watch");
    		return false;
		}
    	
    	if(!javelin.setBLECharacteristicValue(m_laser_id,
    			UUID_laser_motor_service.toUpperCase(),
    			UUID_laser_motor_ipos.toUpperCase(),
				l_pos))
    	{
    		System.err.println("Failed to set laser motor position");
        	if(!javelin.unWatchBLECharacteristicChanges(m_laser_id,
        			UUID_laser_motor_service.toUpperCase(),
        			UUID_laser_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove laser motor watch");
    		}
    		return false;
    	}
    	
    	byte[] l_bytes = 
    			javelin.waitForBLECharacteristicChanges(m_laser_id,
    					UUID_laser_motor_service.toUpperCase(),
    					UUID_laser_motor_mmode.toUpperCase(),
    	    			20000);
    	if(l_bytes == null)
    	{
    		System.err.println("Timed out waiting for laser motor start");
        	if(!javelin.unWatchBLECharacteristicChanges(m_laser_id,
        			UUID_laser_motor_service.toUpperCase(),
        			UUID_laser_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove laser motor watch");
    		}
    		return false;
    	}
    	if(l_bytes.length == 0)
    	{
    		System.err.println("Failed to get laser motor running indicator");
        	if(!javelin.unWatchBLECharacteristicChanges(m_laser_id,
        			UUID_laser_motor_service.toUpperCase(),
        			UUID_laser_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove laser motor watch");
    		}
    		return false;
    	}
    	if(l_bytes[0] != 2)
    	{
    		System.err.println("Got wrong laser motor running indicator "+l_bytes[0]);
        	if(!javelin.unWatchBLECharacteristicChanges(m_laser_id,
        			UUID_laser_motor_service.toUpperCase(),
        			UUID_laser_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove laser motor watch");
    		}
    		return false;
    	}
    	l_bytes = 
    			javelin.waitForBLECharacteristicChanges(m_laser_id,
    					UUID_laser_motor_service.toUpperCase(),
    					UUID_laser_motor_mmode.toUpperCase(),
    	    			40000);
    	if(!javelin.unWatchBLECharacteristicChanges(m_laser_id,
    			UUID_laser_motor_service.toUpperCase(),
    			UUID_laser_motor_mmode.toUpperCase()) )
		{
    		System.err.println("Failed to remove laser motor watch");
		}
    	if(l_bytes == null)
    	{
    		System.err.println("Timed out waiting for laser motor stop");
    		return false;
    	}
    	if(l_bytes.length == 0)
    	{
    		System.err.println("Failed to get laser motor stopped indicator");
    		return false;
    	}
    	if(l_bytes[0] != 1)
    	{
    		System.err.println("Got wrong laser motor stopped indicator "+l_bytes[0]);
        	return false;
    	}
		
    	return true;
	}

	public boolean setMotorSpeed(int a_speed)
	{
    	byte[] l_speed = { (byte) a_speed};
    	if(!laserOk()) return false;
    	boolean l_ret = javelin.setBLECharacteristicValue(m_laser_id,
    				UUID_laser_motor_service.toUpperCase(),
    				UUID_laser_motor_speed.toUpperCase(),
        			l_speed);
    	if(!l_ret) System.err.println("Failed to set laser motor speed");
    	return l_ret;
	}

	public boolean setTurntableMotorSpeed(int a_speed)
	{
    	byte[] l_speed = { (byte) a_speed};
    	if(!turntableOk()) return false;
    	boolean l_ret = javelin.setBLECharacteristicValue(m_turntable_id,
    				UUID_turntable_tmotor_service.toUpperCase(),
        			UUID_turntable_motor_speed.toUpperCase(),
        			l_speed);
    	if(!l_ret) System.err.println("Failed to set turntable motor speed");
    	return l_ret;
    }

	public boolean setTurntableMotorPos(int a_pos)
	{
		byte[] l_pos = { (byte) (a_pos&0xff), (byte) ((a_pos&0xff00)>>8)};
    	if(!turntableOk()) return false;
    	if(!javelin.watchBLECharacteristicChanges(m_turntable_id,
    			UUID_turntable_tmotor_service.toUpperCase(),
    			UUID_turntable_motor_mmode.toUpperCase()) )
		{
    		System.err.println("Failed to add turntable motor watch");
    		return false;
		}
    	
    	if(!javelin.setBLECharacteristicValue(m_turntable_id,
				UUID_turntable_tmotor_service.toUpperCase(),
				UUID_turntable_motor_ipos.toUpperCase(),
				l_pos))
    	{
    		System.err.println("Failed to set turntable motor position");
        	if(!javelin.unWatchBLECharacteristicChanges(m_turntable_id,
        			UUID_turntable_tmotor_service.toUpperCase(),
        			UUID_turntable_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove turntable motor watch");
    		}
    		return false;
    	}
    	
    	byte[] l_bytes = 
    			javelin.waitForBLECharacteristicChanges(m_turntable_id,
    					UUID_turntable_tmotor_service.toUpperCase(),
    	    			UUID_turntable_motor_mmode.toUpperCase(),
    	    			20000);
    	if(l_bytes == null)
    	{
    		System.err.println("Timed out waiting for motor start");
        	if(!javelin.unWatchBLECharacteristicChanges(m_turntable_id,
        			UUID_turntable_tmotor_service.toUpperCase(),
        			UUID_turntable_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove turntable motor watch");
    		}
    		return false;
    	}
    	if(l_bytes.length == 0)
    	{
    		System.err.println("Failed to get motor running indicator");
        	if(!javelin.unWatchBLECharacteristicChanges(m_turntable_id,
        			UUID_turntable_tmotor_service.toUpperCase(),
        			UUID_turntable_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove turntable motor watch");
    		}
    		return false;
    	}
    	if(l_bytes[0] != 2)
    	{
    		System.err.println("Got wrong motor running indicator "+l_bytes[0]);
        	if(!javelin.unWatchBLECharacteristicChanges(m_turntable_id,
        			UUID_turntable_tmotor_service.toUpperCase(),
        			UUID_turntable_motor_mmode.toUpperCase()) )
    		{
        		System.err.println("Failed to remove turntable motor watch");
    		}
    		return false;
    	}
    	l_bytes = 
    			javelin.waitForBLECharacteristicChanges(m_turntable_id,
    					UUID_turntable_tmotor_service.toUpperCase(),
    	    			UUID_turntable_motor_mmode.toUpperCase(),
    	    			40000);
    	if(!javelin.unWatchBLECharacteristicChanges(m_turntable_id,
    			UUID_turntable_tmotor_service.toUpperCase(),
    			UUID_turntable_motor_mmode.toUpperCase()) )
		{
    		System.err.println("Failed to remove turntable motor watch");
		}
    	if(l_bytes == null)
    	{
    		System.err.println("Timed out waiting for motor stop");
    		return false;
    	}
    	if(l_bytes.length == 0)
    	{
    		System.err.println("Failed to get motor stopped indicator");
    		return false;
    	}
    	if(l_bytes[0] != 1)
    	{
    		System.err.println("Got wrong motor stopped indicator "+l_bytes[0]);
    		return false;
    	}
		
    	return true;
	}

	public boolean setTurntableMotorAccel(int a_accel)
	{
    	byte[] l_accel = { (byte) a_accel};
    	if(!turntableOk()) return false;
    	boolean l_ret = javelin.setBLECharacteristicValue(m_turntable_id,
    				UUID_turntable_tmotor_service.toUpperCase(),
    				UUID_turntable_motor_accel.toUpperCase(),
    				l_accel);
    	if(!l_ret) System.err.println("Failed to set turntable motor acceleration");
    	return l_ret;
	}


	public boolean laserOk() {
		if(m_laser_id!=null) return true;
		return false;
	}

	public boolean turntableOk() {
		if(m_turntable_id!=null) return true;
		return false;
	}

	public String getLaserName() {
		return m_laser;
	}

	public String getTurntableName() {
		return m_turntable;
	}

}

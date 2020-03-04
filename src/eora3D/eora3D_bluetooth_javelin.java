package eora3D;
import javelin.javelin;

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
		System.loadLibrary("msvcp140d_app");
		System.loadLibrary("vcruntime140_1d_app");
		System.loadLibrary("VCRUNTIME140D_APP");
		System.loadLibrary("CONCRT140D_APP");
		System.loadLibrary("ucrtbased");
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
				System.out.println(" "+l_device);
				String l_name = javelin.getBLEDeviceName(l_device);
				System.out.println("  Name: "+l_name);
				if(l_name.startsWith("E3DT"))
				{
					m_turntable_services = javelin.listBLEDeviceServices(l_device);
					if(m_turntable_services!=null)
					{
						m_turntable = l_name;
						m_turntable_id = l_device;
					}
				}
				if(l_name.startsWith("E3DS"))
				{
					m_laser_services = javelin.listBLEDeviceServices(l_device);
					if(m_laser_services!=null)
					{
						m_laser = l_name;
						m_laser_id = l_device;
					}
				}
			}
		}
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

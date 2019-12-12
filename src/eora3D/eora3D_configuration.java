package eora3D;

import java.io.File;
import java.io.Serializable;

class eora3D_configuration_version implements Serializable
{
	public int sm_config_version = 1;
}

class eora3D_configuration_data_v1 implements Serializable
{
	public int sm_laser_0_offset = 1800;
	public int sm_laser_steps_per_deg = 80;
	
	public File sm_config_file = new File(System.getProperty("user.home")+File.separatorChar+"ScannerConfig.e3d");
	public File sm_image_dir = new File(System.getProperty("user.home")+File.separatorChar+"e3d_caps");
}


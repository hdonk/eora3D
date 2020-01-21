package eora3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;

import javax.swing.JDialog;

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
	
	public int sm_circle_min_rad = 3;
	public int sm_circle_max_rad = 10;
	
	//public int sm_circle_detection_threshold = 280;
	public int sm_laser_detection_threshold_r = 140;
	public int sm_laser_detection_threshold_g = 140;
	public int sm_laser_detection_threshold_b = 140;
	public float sm_laser_detection_threshold_percent = 30.0f;
	public String sm_laser_detection_threshold_logic = "Or";
	
	public int sm_calibration_vertical_offset = 0;
	
	public int sm_calibration_tl_motorpos_1 = 3413;
	public int sm_calibration_tr_motorpos_1 = 3915;
	public int sm_calibration_tl_motorpos_2 = 2100;
	public int sm_calibration_tr_motorpos_2 = 2244;
	
	public int sm_calibration_v_offset = 0;

	public int sm_camera_rotation = 90;
	
	public int sm_scan_start_angle = 1800;
	public int sm_scan_end_angle = 9000;
	public int sm_scan_step_size = 80;
	
	public int sm_threads = 8;
	
	public int sm_test_frame = 4216;
	
	public int sm_turntable_step_size = 1;
}


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
	public int sm_max_points_per_line = 3;
	public int sm_min_points_per_laser = 3;
	
	public int sm_calibration_vertical_offset = 0;
	
	public int sm_calibration_tl_motorpos_1 = 3043;
	public int sm_calibration_tr_motorpos_1 = 5078;
	
	public int sm_calibration_v_offset = 0;

	public int sm_camera_res_w = 1280;
	public int sm_camera_res_h = 720;
	public int sm_camera_rotation = 90;
	
	public int sm_scan_start_angle = 1800;
	public int sm_scan_end_angle = 9000;
	public int sm_scan_step_size = 80;
	
	public int sm_threads = 8;
	
	public int sm_test_frame = 4216;
	
	public int sm_turntable_step_size = 324;
	
	public int sm_turntable_steps_per_rotation = 6480;
	
	public int sm_leftfilter = -5000;
	public int sm_rightfilter = 5000;
	public int sm_topfilter = 5000;
	public int sm_bottomfilter = -5000;
	public int sm_frontfilter = -5000;
	public int sm_backfilter = 5000;
	
	int checkIntRange( int a_value, int a_min, int a_max)
	{
		if(a_value<a_min) return a_min;
		if(a_value>a_max) return a_max;
		return a_value;
	}
	
	float checkFloatRange( float a_value, float a_min, float a_max)
	{
		if(a_value<a_min) return a_min;
		if(a_value>a_max) return a_max;
		return a_value;
	}
	
	void verify()
	{
		 sm_laser_0_offset = checkIntRange( sm_laser_0_offset, 0, 9000);
		 sm_laser_steps_per_deg = checkIntRange( sm_laser_steps_per_deg, 1, 180);
		 sm_circle_min_rad = checkIntRange( sm_circle_min_rad, 1, 10);
		 sm_circle_max_rad = checkIntRange( sm_circle_max_rad, 2, 11);
		 sm_calibration_v_offset = checkIntRange( sm_calibration_v_offset, -200, +200);
		 sm_calibration_tl_motorpos_1 = checkIntRange( sm_calibration_tl_motorpos_1, 0, 9000);
		 sm_calibration_tr_motorpos_1 = checkIntRange( sm_calibration_tr_motorpos_1, 0, 9000);
		 sm_laser_detection_threshold_r = checkIntRange( sm_laser_detection_threshold_r, 1, 255);
		 sm_laser_detection_threshold_g = checkIntRange( sm_laser_detection_threshold_g, 1, 255);
		 sm_laser_detection_threshold_b = checkIntRange( sm_laser_detection_threshold_b, 1, 255);
		 sm_laser_detection_threshold_percent = checkFloatRange( sm_laser_detection_threshold_percent, 0.1f, 200.0f);
		 sm_max_points_per_line = checkIntRange( sm_max_points_per_line, 1, 10);
		 sm_scan_start_angle = checkIntRange( sm_scan_start_angle, 0, 9000);
		 sm_scan_end_angle = checkIntRange( sm_scan_end_angle, 1, 9000);
		 sm_scan_step_size = checkIntRange( sm_scan_step_size, 1, 180);
		 sm_threads = checkIntRange( sm_threads, 1, 64);
		 sm_test_frame = checkIntRange( sm_test_frame, 0, 9000);
		 sm_test_frame = checkIntRange( sm_test_frame, 1, 6480);
		 sm_leftfilter = checkIntRange( sm_leftfilter, -10000, +10000);
		 sm_rightfilter = checkIntRange( sm_rightfilter, -10000, +10000);
		 sm_topfilter = checkIntRange( sm_topfilter, -10000, +10000);
		 sm_bottomfilter = checkIntRange( sm_bottomfilter, -10000, +10000);
		 sm_frontfilter = checkIntRange( sm_frontfilter, -10000, +10000);
		 sm_backfilter = checkIntRange( sm_backfilter, -10000, +10000);

	}
}


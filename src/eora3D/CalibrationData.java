package eora3D;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class CalibrationData
{
	// mm
	public double board_w = 160.0f;
	public double board_h = 220.0f;
	public double spot_r = 2.0f;
	public double spot_sep_w = 130.0f;
	public double spot_sep_h = 191.0f;
	
	// Calculation inputs
	// pixels
	public int capture_w = 1280;
	public int capture_h = 720;
	public int v_offset = 0;
	public int v_offset_2 = 0;
	
	private double cal_pos_1_per = 0.90f;
	private double cal_pos_2_per = 0.70f;
	
	// Calculation outputs
	public Rectangle pos_1_tl;
	public Rectangle pos_1_tr;
	public Rectangle pos_1_bl;
	public Rectangle pos_1_br;

/*	public Rectangle pos_2_tl;
	public Rectangle pos_2_tr;
	public Rectangle pos_2_bl;
	public Rectangle pos_2_br;*/
	
	public Rectangle pos_1_board;
//	public Rectangle pos_2_board;
	
	public int v_offset_minmax = 0;

	public int detection_box = 20;
	public int circle_center_threshold = 5;
	public int circle_radius_threshold = 3;
	
	public Point cal_square_bl;
	public Point cal_square_br;
/*	public Point cal_square_tl;
	public Point cal_square_tr;*/
	
	public int m_minz = 0;
	public int m_maxz = 0;
	
	public double focal_length_pix = 0.0f;
	
	public int m_laser_to_camera_sep_pix = 0;
	
	void calculate()
	{
		double target_h;
		double target_w;
		if(capture_w > capture_h) // landscape
		{
			// use the height as the limit
			target_h = ((double)capture_h * cal_pos_1_per);
			target_w = (target_h/board_h)*board_w;
		}
		else // portrait
		{
			// Use the width as the limit
			target_w = ((double)capture_w * cal_pos_1_per);
			target_h = (target_w/board_w)*board_h;
		}
		pos_1_board = new Rectangle();
		pos_1_board.height = (int)target_h;
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation==90)
		{
			v_offset_minmax = (capture_w - (int)target_w)/2;
		}
		else
		{
			v_offset_minmax = (capture_h - (int)target_h)/2;
		}
		pos_1_board.width = (int)target_w;
		pos_1_board.x = (capture_w-pos_1_board.width)/2;
		pos_1_board.y = (capture_h-pos_1_board.height)/2 + v_offset;
		
		pos_1_tl = new Rectangle();
		pos_1_tl.width = (int)(((target_w/board_w)*spot_r))*2;
		pos_1_tl.height = (int)(((target_h/board_h)*spot_r))*2;
		pos_1_tl.x = (int)(capture_w -((target_w/board_w)*spot_sep_w))/2 - pos_1_tl.width;
		pos_1_tl.y = (int)(capture_h -((target_h/board_h)*spot_sep_h))/2 - pos_1_tl.height + v_offset;

		pos_1_tr = new Rectangle();
		pos_1_tr.width = pos_1_tl.width;
		pos_1_tr.height = pos_1_tl.height;
		pos_1_tr.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
		pos_1_tr.y = pos_1_tl.y;

		pos_1_bl = new Rectangle();
		pos_1_bl.width = pos_1_tl.width;
		pos_1_bl.height = pos_1_tl.height;
		pos_1_bl.x = pos_1_tl.x;
		pos_1_bl.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2 + v_offset*2;

		pos_1_br = new Rectangle();
		pos_1_br.width = pos_1_tl.width;
		pos_1_br.height = pos_1_tl.height;
		pos_1_br.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
		pos_1_br.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2 + v_offset*2;

	
/*		target_h = ((double)capture_h * cal_pos_2_per);
		target_w = (target_h/board_h)*board_w;
		v_offset_2 = (int)(((double)v_offset) * (cal_pos_2_per/cal_pos_1_per));
		pos_2_board = new Rectangle();
		pos_2_board.height = (int)target_h;
		pos_2_board.width = (int)target_w;
		pos_2_board.x = (capture_w-pos_2_board.width)/2;
		pos_2_board.y = (capture_h-pos_2_board.height)/2 + v_offset_2;
		
		pos_2_tl = new Rectangle();
		pos_2_tl.width = (int)(((target_w/board_w)*spot_r))*2;
		pos_2_tl.height = (int)(((target_h/board_h)*spot_r))*2;
		pos_2_tl.x = (int)(capture_w -((target_w/board_w)*spot_sep_w))/2 - pos_2_tl.width;
		pos_2_tl.y = (int)(capture_h -((target_h/board_h)*spot_sep_h))/2 - pos_2_tl.height + v_offset_2;

		pos_2_tr = new Rectangle();
		pos_2_tr.width = pos_2_tl.width;
		pos_2_tr.height = pos_2_tl.height;
		pos_2_tr.x = (int)(capture_w - pos_2_tl.x) - pos_2_tl.width*2;
		pos_2_tr.y = pos_2_tl.y;

		pos_2_bl = new Rectangle();
		pos_2_bl.width = pos_2_tl.width;
		pos_2_bl.height = pos_2_tl.height;
		pos_2_bl.x = pos_2_tl.x;
		pos_2_bl.y = (int)(capture_h - pos_2_tl.y) - pos_2_tl.height*2 + v_offset_2*2;

		pos_2_br = new Rectangle();
		pos_2_br.width = pos_2_tl.width;
		pos_2_br.height = pos_2_tl.height;
		pos_2_br.x = (int)(capture_w - pos_2_tl.x) - pos_2_tl.width*2;
		pos_2_br.y = (int)(capture_h - pos_2_tl.y) - pos_2_tl.height*2 + v_offset_2*2;*/
	}
	
	void calculateBaseCoords()
	{//cal_square_bl
		double l_to_camera_steps = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
		double alpha = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double beta = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double alpha_prime = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double beta_prime = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		
		double x, y;
		
		cal_square_bl = new Point();
		cal_square_br = new Point();
/*		cal_square_tl = new Point();
		cal_square_tr = new Point();*/
		
		x = (((double)pos_1_board.width)*Math.tan(Math.toRadians(alpha)))/(Math.tan(Math.toRadians(beta))-Math.tan(Math.toRadians(alpha)));
		cal_square_bl.x = (int)x;
		y = x*Math.tan(Math.toRadians(beta));
		cal_square_bl.y = (int)y;
		cal_square_br.y = (int)y;
		cal_square_br.x = cal_square_bl.x + pos_1_board.width;
		
/*		x = (((double)pos_2_board.width)*Math.tan(Math.toRadians(alpha_prime)))/(Math.tan(Math.toRadians(beta_prime))-Math.tan(Math.toRadians(alpha_prime)));
		cal_square_tl.x = (int)x;
		y = x*Math.tan(Math.toRadians(beta_prime));
		cal_square_tl.y = (int)y;
		cal_square_tr.y = (int)y;
		cal_square_tr.x = cal_square_bl.x + pos_2_board.width;*/
		
		// Calculate focal length
		double d_mm = spot_sep_w;
		double h_pix = pos_1_board.width;
		double H_mm = spot_sep_w;

		focal_length_pix = (d_mm*h_pix)/H_mm;
		//focal_length_pix *= 1.1f;
		System.out.println("Focal length in pixels: "+focal_length_pix);
		
		// Camera center to laser center separation
		m_laser_to_camera_sep_pix = cal_square_bl.x + pos_1_board.width/2;
		//m_laser_to_camera_sep_pix *= 1.2f;
		System.out.println("Camera laser sep pixels: "+m_laser_to_camera_sep_pix);
	}
	
	RGB3DPoint getPointOffset(int a_angle_steps, int screen_x, int screen_y)
	{
		double l_to_camera_steps = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
//		double alpha = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double C = (l_to_camera_steps - a_angle_steps)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		
		double x_pos = (double)screen_x - ((double)capture_w/2.0f) ;
		
		double alpha = Math.toDegrees(Math.atan(x_pos/focal_length_pix));
		double A = 90.0f + alpha;
		double B = 180.0f - A - C;
		double b = m_laser_to_camera_sep_pix;
		
		double c = (b*Math.sin(Math.toRadians(C))) / (Math.sin(Math.toRadians(B)));
		
		double z = c * Math.sin(Math.toRadians(A));
		double x = c * Math.cos(Math.toRadians(A));
		
		double y = z * (screen_y-capture_h/2.0f)/focal_length_pix;
		
		//System.out.println("In: angle: "+a_angle_steps+" x: "+screen_x+" y: "+screen_y+" Out: x: "+x+"y: "+y+" z: "+z);
		
		RGB3DPoint l_point = new RGB3DPoint(capture_w-(int)x, (int)y, (int)z);
		return l_point;
	}
	
	int findLaserPoint(BufferedImage a_base, BufferedImage a_in, int a_y, int a_x_min, int a_x_max)
	{
		for(int x = a_x_max-1; x>=a_x_min; --x)
		{
			int argb = a_in.getRGB(x, a_y);
			int r = (argb & 0xff0000) >> 16;
        	int g = (argb & 0x00ff00) >> 8;
    		int b = (argb & 0x0000ff);
    		
			int argb_base = a_base.getRGB(x, a_y);
			int r_base = (argb_base & 0xff0000) >> 16;
        	int g_base = (argb_base & 0x00ff00) >> 8;
    		int b_base = (argb_base & 0x0000ff);
    		
    		int rd = Math.abs(r_base-r);
    		int gd = Math.abs(g_base-g);
    		int bd = Math.abs(b_base-b);

    		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
    		{
        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) return x;
    		} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("And"))
    		{
        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) return x;
    		} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("%"))
    		{
    			float pc = ((float)rd/255.0f + (float)gd/255.0f + (float)bd/255.0f)*100.0f;
        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) return x;
    		} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Weighted %"))
    		{
    			float pc = (((float)rd/255.0f)*40.0f + ((float)gd/255.0f)*20.0f + ((float)bd/255.0f)*40.0f);
        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) return x;
    		}
		}
		return -1;
	}
	
}
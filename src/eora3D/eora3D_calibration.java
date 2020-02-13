package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
/*import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;*/
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.JScrollBar;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class eora3D_calibration extends JDialog implements ActionListener, AdjustmentListener, WindowListener {
	Eora3D_MainWindow m_e3d;
	private Webcam m_camera;
	private PaintImage image;
	private JTextField txtThreshold;
	private JTextField txtMinHits;
	private JTextField txtMinRad;
	private JTextField txtMaxRad;
	private BufferedImage capturedImage = null;
	private CalibrationData m_cal_data;
	private JScrollBar sbHeightOffset;
	private JTextField txtRedthreshold;
	private JTextField txtGreenthreshold;
	private JTextField txtBluethreshold;
	private JComboBox<String> cbThresholdLogic;
	private Thread m_calibration_thread = null;
	private boolean m_stop_calibration_thread;
	private JTextField txtTopleftmotorpos;
	private JTextField txtToprightmotorpos;
	private JLabel lblTopRightMotor;
	private JLabel lblTopLeftMotor;

	private Thread m_thread = null;
	boolean m_stop_for_cal = false;
	
	long lastTime = 0;
	private Thread m_detect_thread = null;
	private JTextField tfPercentageThreshold;

	eora3D_calibration(Eora3D_MainWindow a_e3d)
	{
		super();
		m_e3d = a_e3d;
		this.addWindowListener(this);
		getContentPane().setLayout(null);
		
		image = new PaintImage(true);
		image.setBounds(382, 12, 698, 846);
		getContentPane().add(image);
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(1126, 363, 281, 25);
		getContentPane().add(btnCalibrate);
		btnCalibrate.addActionListener(this);
		
		ButtonGroup calibrationPositionGroup = new ButtonGroup();
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(24, 930, 117, 25);
		getContentPane().add(btnConfig);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Circle detection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(24, 12, 327, 286);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblResRatio = new JLabel("Threshold");
		lblResRatio.setBounds(20, 19, 62, 15);
		panel.add(lblResRatio);
		
		txtThreshold = new JTextField();
		txtThreshold.setBounds(20, 33, 114, 19);
		panel.add(txtThreshold);
		txtThreshold.setText("280");
		txtThreshold.setColumns(10);
		
		JLabel lblMinHits = new JLabel("Min Hits");
		lblMinHits.setBounds(20, 64, 70, 15);
		panel.add(lblMinHits);
		
		txtMinHits = new JTextField();
		txtMinHits.setBounds(20, 80, 114, 19);
		panel.add(txtMinHits);
		txtMinHits.setText("200");
		
		JLabel lblHeightOffset = new JLabel("Vertical offset");
		lblHeightOffset.setBounds(30, 205, 102, 15);
		panel.add(lblHeightOffset);
		
		sbHeightOffset = new JScrollBar();
		sbHeightOffset.setBounds(28, 232, 221, 17);
		panel.add(sbHeightOffset);
		sbHeightOffset.setMaximum(8);
		sbHeightOffset.setOrientation(JScrollBar.HORIZONTAL);
		
		JLabel lblMinRad = new JLabel("Min rad");
		lblMinRad.setBounds(20, 111, 70, 15);
		panel.add(lblMinRad);
		
		txtMinRad = new JTextField();
		txtMinRad.setBounds(20, 125, 114, 19);
		panel.add(txtMinRad);
		txtMinRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad);
		txtMinRad.setColumns(10);
		
		JLabel lblMaxRad = new JLabel("Max rad");
		lblMaxRad.setBounds(20, 156, 70, 15);
		panel.add(lblMaxRad);
		
		txtMaxRad = new JTextField();
		txtMaxRad.setBounds(20, 174, 114, 19);
		panel.add(txtMaxRad);
		txtMaxRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad);
		txtMaxRad.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Laser detection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(1092, 12, 339, 339);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblThresholdLogic = new JLabel("Threshold logic");
		lblThresholdLogic.setBounds(32, 27, 110, 15);
		panel_1.add(lblThresholdLogic);
		
		cbThresholdLogic = new JComboBox<String>();
		cbThresholdLogic.setBounds(32, 54, 64, 24);
		cbThresholdLogic.addItem("Or");
		cbThresholdLogic.addItem("And");
		cbThresholdLogic.addItem("%");
		cbThresholdLogic.addItem("Weighted %");
		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("Or"))
		{
			cbThresholdLogic.setSelectedIndex(0);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("And"))
		{
			cbThresholdLogic.setSelectedIndex(1);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("%"))
		{
			cbThresholdLogic.setSelectedIndex(2);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("Weighted %"))
		{
			cbThresholdLogic.setSelectedIndex(3);
		}
		panel_1.add(cbThresholdLogic);
		cbThresholdLogic.addActionListener(this);
	
		JLabel lblRedThreshold = new JLabel("Red threshold");
		lblRedThreshold.setBounds(32, 95, 102, 15);
		panel_1.add(lblRedThreshold);
		
		txtRedthreshold = new JTextField();
		txtRedthreshold.setBounds(28, 110, 114, 19);
		panel_1.add(txtRedthreshold);
		txtRedthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r);
		txtRedthreshold.setColumns(10);
		
		JLabel lblGreenThreshold = new JLabel("Green threshold");
		lblGreenThreshold.setBounds(32, 153, 117, 15);
		panel_1.add(lblGreenThreshold);
		
		txtGreenthreshold = new JTextField();
		txtGreenthreshold.setBounds(32, 170, 114, 19);
		panel_1.add(txtGreenthreshold);
		txtGreenthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g);
		txtGreenthreshold.setColumns(10);
		
		JLabel lblBlueThreshold = new JLabel("Blue threshold");
		lblBlueThreshold.setBounds(32, 213, 107, 15);
		panel_1.add(lblBlueThreshold);
		
		txtBluethreshold = new JTextField();
		txtBluethreshold.setBounds(32, 228, 114, 19);
		panel_1.add(txtBluethreshold);
		txtBluethreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b);
		txtBluethreshold.setColumns(10);
		
		JLabel lblPercentageThreshold = new JLabel("Percentage threshold");
		lblPercentageThreshold.setBounds(32, 259, 182, 15);
		panel_1.add(lblPercentageThreshold);
		
		tfPercentageThreshold = new JTextField();
		tfPercentageThreshold.setBounds(32, 278, 122, 27);
		panel_1.add(tfPercentageThreshold);
		tfPercentageThreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent);
		tfPercentageThreshold.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Detected positions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(1092, 400, 339, 190);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		lblTopLeftMotor = new JLabel("Top left motor pos 1");
		lblTopLeftMotor.setBounds(22, 28, 143, 15);
		panel_2.add(lblTopLeftMotor);
		
		txtTopleftmotorpos = new JTextField();
		txtTopleftmotorpos.setBounds(32, 55, 114, 19);
		panel_2.add(txtTopleftmotorpos);
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		txtTopleftmotorpos.setColumns(10);
		
		lblTopRightMotor = new JLabel("Top right motor pos 1 ");
		lblTopRightMotor.setBounds(13, 86, 152, 15);
		panel_2.add(lblTopRightMotor);
		
		txtToprightmotorpos = new JTextField();
		txtToprightmotorpos.setBounds(32, 113, 114, 19);
		panel_2.add(txtToprightmotorpos);
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		txtToprightmotorpos.setColumns(10);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(1458, 930, 100, 27);
		getContentPane().add(btnFinish);
		txtBluethreshold.addActionListener(this);
		txtGreenthreshold.addActionListener(this);
		txtRedthreshold.addActionListener(this);
		sbHeightOffset.addAdjustmentListener(this);

		m_camera = m_e3d.m_camera;
		
		setSize(1584,1034);
		
		setModal(true);
		
		m_cal_data = m_e3d.m_cal_data;
		calculateCalibrationPositions();

		Runnable l_runnable = () -> {
			CircleDetection();
		};
		m_thread = new Thread(l_runnable);
		m_thread.start();
}
	
	private void calculateCalibrationPositions() {
/*		if(m_cal_data == null)
		{
			m_cal_data = new CalibrationData();
			m_cal_data.v_offset = Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset;
		}*/
		
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation==90)
		{
			m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		else
		{
			m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		
		m_cal_data.calculate();
		
		image.m_cal_data = m_cal_data;
		
		sbHeightOffset.removeAdjustmentListener(this);
		sbHeightOffset.setMinimum(-m_cal_data.v_offset_minmax);
		sbHeightOffset.setMaximum(m_cal_data.v_offset_minmax);
		System.out.println("Set minmax "+m_cal_data.v_offset_minmax);
		//sbHeightOffset.setValue(1);
		sbHeightOffset.addAdjustmentListener(this);

	}
	
	static BufferedImage rotate(BufferedImage a_image, int a_angle)
	{
		BufferedImage l_rot_image;
		int r_x = 0, r_y = 0;
		double angle = Math.toRadians(a_angle);
		if(a_angle==90 || a_angle==270)
		{
			l_rot_image = new BufferedImage(a_image.getHeight(), a_image.getWidth(), BufferedImage.TYPE_INT_ARGB);
			r_x = a_image.getHeight()/2;
			r_y = a_image.getHeight()/2;
		}
		else
		{
			l_rot_image = new BufferedImage(a_image.getWidth(), a_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			r_x = a_image.getWidth()/2;
			r_y = a_image.getWidth()/2;
		}
		Graphics2D l_g2d = (Graphics2D) l_rot_image.getGraphics();
		AffineTransform l_backup = l_g2d.getTransform();
		AffineTransform l_at = AffineTransform.getRotateInstance(angle, r_x, r_y);
		l_g2d.setTransform(l_at);
		l_g2d.drawImage(a_image, 0, 0, null);
		l_g2d.setTransform(l_backup);
		return l_rot_image;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Calibrate")
		{
			if(m_calibration_thread == null)
			{
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent = Float.parseFloat(this.tfPercentageThreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
				m_stop_for_cal = true;
				m_stop_calibration_thread = false;
				Runnable l_runnable = () -> {
					Calibrate();
				};
				m_calibration_thread = new Thread(l_runnable);
				m_calibration_thread.start();
			}
			else
			{
				m_stop_calibration_thread = true;
			}
		}
		else
		if(e.getActionCommand()=="Finish")
		{
			m_stop_calibration_thread = true;
			m_stop_for_cal = true;
			setVisible(false);
		}
		else
		if(e.getActionCommand()=="Config")
		{
			eora3D_configuration_editor l_editor = new eora3D_configuration_editor(m_e3d);
			l_editor.setVisible(true);
		}
	}

	void CircleDetection()
	{
		Rectangle l_found[] = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
		int l_corner_pos[] = {0, 0, 0, 0};
		System.out.println("Circle detecting");
		// Phase one, make sure board is in right place for pos1
		image.pos = 1;
		int l_goodenough;
		do
		{
			l_goodenough = 0;
			capturedImage = m_camera.getImage();
			capturedImage.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				capturedImage = rotate(capturedImage, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}
			image.m_image = capturedImage;
			image.m_overlay = new BufferedImage(capturedImage.getWidth(), capturedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.m_overlay.getGraphics();
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
//				System.out.println("Detecting circles");
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tl_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tl_det_spot.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_1_tl_det_spot.x+m_cal_data.pos_1_tl_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_1_tl_det_spot.y+m_cal_data.pos_1_tl_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_1_tl_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				else if(l_found[0] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tr_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tr_det_spot.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_1_tr_det_spot.x+m_cal_data.pos_1_tr_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_1_tr_det_spot.y+m_cal_data.pos_1_tr_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_1_tr_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				else if(l_found[1] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_bl_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_bl_det_spot.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2]!=null) System.out.println("Found 2");
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_1_bl_det_spot.x+m_cal_data.pos_1_bl_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_1_bl_det_spot.y+m_cal_data.pos_1_bl_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_1_bl_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				else if(l_found[2] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_br_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_br_det_spot.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3]!=null) System.out.println("Found 3");
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_1_br_det_spot.x+m_cal_data.pos_1_br_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_1_br_det_spot.y+m_cal_data.pos_1_br_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_1_br_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
				else if(l_found[3] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			image.m_overlay.flush();
			image.repaint();
		} while (!m_stop_for_cal);
		m_stop_for_cal = false;
		System.out.println("Stopped circle detecting");
	}
	
	void Calibrate()
	{
		Rectangle l_found[] = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
		int l_corner_pos[] = {0, 0, 0, 0};
		// Phase one, make sure board is in right place for pos1
		image.pos = 1;
		int l_goodenough;
		do
		{
			l_goodenough = 0;
			capturedImage = m_camera.getImage();
			capturedImage.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				capturedImage = rotate(capturedImage, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}
			image.m_image = capturedImage;
			image.m_overlay = new BufferedImage(capturedImage.getWidth(), capturedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.m_overlay.getGraphics();
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
//				System.out.println("Detecting circles");
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tl_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tl_det_spot.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_1_tl_det_spot.x+m_cal_data.pos_1_tl_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_1_tl_det_spot.y+m_cal_data.pos_1_tl_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_1_tl_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				else if(l_found[0] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tr_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tr_det_spot.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_1_tr_det_spot.x+m_cal_data.pos_1_tr_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_1_tr_det_spot.y+m_cal_data.pos_1_tr_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_1_tr_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				else if(l_found[1] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_bl_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_bl_det_spot.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_1_bl_det_spot.x+m_cal_data.pos_1_bl_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_1_bl_det_spot.y+m_cal_data.pos_1_bl_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_1_bl_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				else if(l_found[2] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_br_det_spot.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br_det_spot.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br_det_spot.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_br_det_spot.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_1_br_det_spot.x+m_cal_data.pos_1_br_det_spot.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_1_br_det_spot.y+m_cal_data.pos_1_br_det_spot.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_1_br_det_spot.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
				else if(l_found[3] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			image.repaint();
			if(m_stop_calibration_thread)
			{
				m_calibration_thread = null;
//				m_thread.start();
				return;
			}
			System.out.println("found "+l_goodenough+" corners");
		} while (l_goodenough < 4);
		// Hunt for laser in detection circles
		{
			int l_pos, l_pos_slow;
//			System.out.println("Move to base pos");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-2))
			{
				m_calibration_thread = null;
				//m_thread.start();
				return;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			BufferedImage l_base_image = m_camera.getImage();
			l_base_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				l_base_image = rotate(l_base_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}

			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
			for(int corner=0; corner<2; ++corner)
			{
				Rectangle l_search_box = null;
				System.out.println("Scan corner "+corner);
				switch(corner)
				{
					case 0: l_search_box = m_cal_data.pos_1_tl_det_spot; break;
					case 1: l_search_box = m_cal_data.pos_1_tr_det_spot; break;
					case 2: l_search_box = m_cal_data.pos_1_bl_det_spot; break;
					case 3: l_search_box = m_cal_data.pos_1_br_det_spot; break;
				}
				if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-1))
				{
					m_calibration_thread = null;
					//m_thread.start();
					return;
				}
				// Fast search
				for(
						l_pos = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset;
						l_pos < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						l_pos += Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg/4)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						m_calibration_thread = null;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
					{
						l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
					}
					image.m_image = l_image;
					image.repaint();

					int l_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Fast find found corner "+corner);
						break;
					}
				}
				if(l_pos >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					//m_thread.start();
					return;
				}
				// Slow search. We've seen the laser in line with the black dot. Now we search for a break in the laser line.
				int l_notch_count = 0;
				int l_top_found_offset = 0;
				int l_mid_found_offset = 0;
				int l_bot_found_offset = 0;
				for(
						l_pos_slow = l_pos - Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
						l_pos_slow < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						++l_pos_slow)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos_slow))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						m_calibration_thread = null;
						//m_thread.start();
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
					{
						l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
					}
					image.m_image = l_image;
					image.repaint();
	
					l_top_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[corner].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					l_mid_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[corner].y-m_cal_data.detection_box/2, l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					l_bot_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[corner].y+m_cal_data.detection_box/2, l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					System.out.println("top "+l_top_found_offset+"mid "+l_mid_found_offset+"bot "+l_bot_found_offset);
					if(l_top_found_offset == -1 && l_bot_found_offset == -1)
					{
						System.out.println("Lost laser at top and bot");
						l_notch_count = 0;
					}
					if(l_top_found_offset != -1 && l_bot_found_offset != -1 && l_mid_found_offset == -1)
					{
						System.out.println("Notch alignment at top: "+l_top_found_offset+" mid: "+l_mid_found_offset+" bot: "+l_bot_found_offset);
						++l_notch_count;
						if(l_notch_count>=3)
						{
							System.out.println("Notch found. Stopping");
							break;
						}
					}
					if(l_top_found_offset != -1 && l_bot_found_offset == -1)
					{
						System.out.println("Lost laser at bot");
						l_notch_count = 0;
					}
					if(l_top_found_offset != -1 && l_bot_found_offset == -1)
					{
						System.out.println("Lost laser at top");
						l_notch_count = 0;
					}
					if(l_top_found_offset != -1 && l_bot_found_offset != -1 && Math.abs(l_top_found_offset-l_bot_found_offset)>=3)
					{
						System.out.println("Laser vertical alignment error - >=3 at: "+Math.abs(l_top_found_offset-l_bot_found_offset));
					}
				}
				if(l_pos_slow >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					//m_thread.start();
					return;
				}
				l_corner_pos[corner] = l_pos_slow-l_notch_count+1;
				image.m_overlay = new BufferedImage(capturedImage.getWidth(), capturedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = image.m_overlay.getGraphics();
				g.setColor(Color.green);
			    g.fillOval(
			    		l_mid_found_offset-1,
			    		l_found[0].y-1,
			    		2,
			    		2
			    		);
			}
			image.m_overlay.flush();
			image.repaint();
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		}
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = l_corner_pos[0];
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		System.out.println("Motorpos TL 1: "+l_corner_pos[0]);
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = l_corner_pos[1];
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		System.out.println("Motorpos TR 1: "+l_corner_pos[1]);
		
		image.pos = 1;
		m_calibration_thread = null;
		//m_thread.start();

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(e.getSource().equals(sbHeightOffset))
		{
//			System.out.println("Scrollbar at "+sbHeightOffset.getValue());
			m_cal_data.v_offset_pix = sbHeightOffset.getValue();
			calculateCalibrationPositions();
			image.repaint();
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		m_stop_calibration_thread = true;
		m_stop_for_cal = true;
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent = Float.parseFloat(this.tfPercentageThreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
		Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset = m_cal_data.v_offset_pix;
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = Integer.parseInt(txtTopleftmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = Integer.parseInt(txtToprightmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad = Integer.parseInt(this.txtMinRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad = Integer.parseInt(this.txtMaxRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_v_offset = sbHeightOffset.getValue();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}

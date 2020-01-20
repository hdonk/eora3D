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
	private JTextField txtCalibImg;
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
	private JLabel lblTopLeftMotor_1;
	private JLabel lblTopRightMotor_1;
	private JTextField txtTopleftmotorpos_2;
	private JTextField txtToprightmotorpos_2;
	private JLabel lblCalibrationPosition;
	private JRadioButton radioButton;
	private JRadioButton radioButton_1;

	private Thread m_thread = null;
	
	long lastTime = 0;
	private Thread m_detect_thread = null;
	private JLabel lblPointSize;
	private JLabel lblScale;
	private JScrollBar sbPointSize;
	private JScrollBar sbScale;
	private JTextField tfScanStartAngle;
	private JTextField tfScanEndAngle;
	private JTextField tfScanStepSize;

	eora3D_calibration(Eora3D_MainWindow a_e3d)
	{
		super();
		m_e3d = a_e3d;
		this.addWindowListener(this);
		getContentPane().setLayout(null);
		
		JButton btnCapture = new JButton("Capture");
		btnCapture.setBounds(12, 779, 91, 25);
		getContentPane().add(btnCapture);
		btnCapture.addActionListener(this);
		
		image = new PaintImage();
		image.setBounds(12, 12, 1300, 732);
		getContentPane().add(image);
		
		JLabel lblResRatio = new JLabel("Threshold");
		lblResRatio.setBounds(121, 756, 203, 15);
		getContentPane().add(lblResRatio);
		
		txtThreshold = new JTextField();
		txtThreshold.setText("280");
		txtThreshold.setBounds(320, 754, 114, 19);
		getContentPane().add(txtThreshold);
		txtThreshold.setColumns(10);
		
		JLabel lblMinHits = new JLabel("Min Hits");
		lblMinHits.setBounds(121, 811, 70, 15);
		getContentPane().add(lblMinHits);
		
		JLabel lblMinRad = new JLabel("Min rad");
		lblMinRad.setBounds(473, 784, 70, 15);
		getContentPane().add(lblMinRad);
		
		JLabel lblMaxRad = new JLabel("Max rad");
		lblMaxRad.setBounds(473, 811, 70, 15);
		getContentPane().add(lblMaxRad);
		
		txtMinHits = new JTextField();
		txtMinHits.setText("200");
		txtMinHits.setBounds(320, 813, 114, 19);
		getContentPane().add(txtMinHits);
		
		txtMinRad = new JTextField();
		txtMinRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad);
		txtMinRad.setBounds(571, 782, 114, 19);
		getContentPane().add(txtMinRad);
		txtMinRad.setColumns(10);
		
		txtMaxRad = new JTextField();
		txtMaxRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad);
		txtMaxRad.setBounds(571, 809, 114, 19);
		getContentPane().add(txtMaxRad);
		txtMaxRad.setColumns(10);
		
		JButton btnCircles = new JButton("Circles");
		btnCircles.setBounds(12, 810, 91, 25);
		getContentPane().add(btnCircles);
		btnCircles.addActionListener(this);
		
		JButton btnScan = new JButton("Scan");
		btnScan.setBounds(12, 849, 91, 25);
		getContentPane().add(btnScan);
		btnScan.addActionListener(this);
		
		JButton btnDetect = new JButton("Detect");
		btnDetect.setBounds(12, 886, 117, 25);
		getContentPane().add(btnDetect);
		btnDetect.addActionListener(this);
		
		txtCalibImg = new JTextField();
		txtCalibImg.setText("3600");
		txtCalibImg.setBounds(320, 889, 114, 19);
		getContentPane().add(txtCalibImg);
		txtCalibImg.setColumns(10);
		
		JLabel lblCompareimg = new JLabel("Compare Image Number");
		lblCompareimg.setBounds(133, 891, 179, 15);
		getContentPane().add(lblCompareimg);
		
		sbHeightOffset = new JScrollBar();
		sbHeightOffset.setMaximum(8);
		sbHeightOffset.setOrientation(JScrollBar.HORIZONTAL);
		sbHeightOffset.setBounds(571, 754, 221, 17);
		getContentPane().add(sbHeightOffset);
		sbHeightOffset.addAdjustmentListener(this);
		
		JLabel lblHeightOffset = new JLabel("Vertical offset");
		lblHeightOffset.setBounds(462, 756, 102, 15);
		getContentPane().add(lblHeightOffset);
		
		JLabel lblRedThreshold = new JLabel("Red threshold");
		lblRedThreshold.setBounds(462, 838, 102, 15);
		getContentPane().add(lblRedThreshold);
		
		JLabel lblGreenThreshold = new JLabel("Green threshold");
		lblGreenThreshold.setBounds(447, 865, 117, 15);
		getContentPane().add(lblGreenThreshold);
		
		JLabel lblBlueThreshold = new JLabel("Blue threshold");
		lblBlueThreshold.setBounds(457, 891, 107, 15);
		getContentPane().add(lblBlueThreshold);
		
		txtRedthreshold = new JTextField();
		txtRedthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r);
		txtRedthreshold.setBounds(571, 836, 114, 19);
		getContentPane().add(txtRedthreshold);
		txtRedthreshold.setColumns(10);
		txtRedthreshold.addActionListener(this);
		
		txtGreenthreshold = new JTextField();
		txtGreenthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g);
		txtGreenthreshold.setBounds(571, 863, 114, 19);
		getContentPane().add(txtGreenthreshold);
		txtGreenthreshold.setColumns(10);
		txtGreenthreshold.addActionListener(this);
		
		txtBluethreshold = new JTextField();
		txtBluethreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b);
		txtBluethreshold.setBounds(571, 889, 114, 19);
		getContentPane().add(txtBluethreshold);
		txtBluethreshold.setColumns(10);
		txtBluethreshold.addActionListener(this);
		
		JLabel lblThresholdLogic = new JLabel("Threshold logic");
		lblThresholdLogic.setBounds(202, 864, 110, 15);
		getContentPane().add(lblThresholdLogic);
		
		cbThresholdLogic = new JComboBox<String>();
		cbThresholdLogic.setBounds(320, 849, 64, 24);
		getContentPane().add(cbThresholdLogic);
		cbThresholdLogic.addItem("Or");
		cbThresholdLogic.addItem("And");
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
		cbThresholdLogic.addActionListener(this);
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(214, 920, 281, 25);
		getContentPane().add(btnCalibrate);
		btnCalibrate.addActionListener(this);
		
		lblTopLeftMotor = new JLabel("Top left motor pos 1");
		lblTopLeftMotor.setBounds(722, 784, 143, 15);
		getContentPane().add(lblTopLeftMotor);
		
		lblTopRightMotor = new JLabel("Top right motor pos 1 ");
		lblTopRightMotor.setBounds(713, 811, 152, 15);
		getContentPane().add(lblTopRightMotor);
		
		txtTopleftmotorpos = new JTextField();
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		txtTopleftmotorpos.setBounds(883, 782, 114, 19);
		getContentPane().add(txtTopleftmotorpos);
		txtTopleftmotorpos.setColumns(10);
		
		txtToprightmotorpos = new JTextField();
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		txtToprightmotorpos.setBounds(883, 809, 114, 19);
		getContentPane().add(txtToprightmotorpos);
		txtToprightmotorpos.setColumns(10);
		
		lblTopLeftMotor_1 = new JLabel("Top left motor pos 2");
		lblTopLeftMotor_1.setBounds(722, 838, 143, 15);
		getContentPane().add(lblTopLeftMotor_1);
		
		lblTopRightMotor_1 = new JLabel("Top right motor pos 2");
		lblTopRightMotor_1.setBounds(713, 865, 152, 15);
		getContentPane().add(lblTopRightMotor_1);
		
		txtTopleftmotorpos_2 = new JTextField();
		txtTopleftmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2);
		txtTopleftmotorpos_2.setBounds(883, 836, 114, 19);
		getContentPane().add(txtTopleftmotorpos_2);
		txtTopleftmotorpos_2.setColumns(10);
		
		txtToprightmotorpos_2 = new JTextField();
		txtToprightmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2);
		txtToprightmotorpos_2.setBounds(883, 863, 114, 19);
		getContentPane().add(txtToprightmotorpos_2);
		txtToprightmotorpos_2.setColumns(10);
		
		lblCalibrationPosition = new JLabel("Calibration position");
		lblCalibrationPosition.setBounds(121, 784, 143, 15);
		getContentPane().add(lblCalibrationPosition);
		
		ButtonGroup calibrationPositionGroup = new ButtonGroup();
		radioButton = new JRadioButton("1");
		radioButton.setBounds(278, 780, 33, 23);
		getContentPane().add(radioButton);
		radioButton.addActionListener(this);
		calibrationPositionGroup.add(radioButton);
		
		radioButton_1 = new JRadioButton("2");
		radioButton_1.setBounds(330, 780, 47, 23);
		getContentPane().add(radioButton_1);
		radioButton_1.addActionListener(this);
		calibrationPositionGroup.add(radioButton_1);
		
		radioButton.setSelected(true);
		
		lblPointSize = new JLabel("Point size");
		lblPointSize.setBounds(706, 891, 70, 15);
		getContentPane().add(lblPointSize);
		
		lblScale = new JLabel("Scale");
		lblScale.setBounds(927, 891, 70, 15);
		getContentPane().add(lblScale);
		
		sbPointSize = new JScrollBar();
		sbPointSize.setBlockIncrement(10);
		sbPointSize.setUnitIncrement(10);
		sbPointSize.setMinimum(10);
		sbPointSize.setValue(10);
		sbPointSize.setMaximum(80);
		sbPointSize.setOrientation(JScrollBar.HORIZONTAL);
		sbPointSize.setBounds(782, 886, 129, 17);
		getContentPane().add(sbPointSize);
		
		sbScale = new JScrollBar();
		sbScale.setValue(10);
		sbScale.setOrientation(JScrollBar.HORIZONTAL);
		sbScale.setMinimum(1);
		sbScale.setMaximum(40);
		sbScale.setBounds(976, 886, 129, 17);
		getContentPane().add(sbScale);

		JLabel lblScanStartAngle = new JLabel("Scan Start Angle");
		lblScanStartAngle.setBounds(12, 960, 143, 15);
		getContentPane().add(lblScanStartAngle);
		
		tfScanStartAngle = new JTextField();
		tfScanStartAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle);
		tfScanStartAngle.setBounds(143, 958, 47, 19);
		getContentPane().add(tfScanStartAngle);
		tfScanStartAngle.setColumns(10);
		
		JLabel lblScanEndAngle = new JLabel("Scan End Angle");
		lblScanEndAngle.setBounds(207, 960, 117, 15);
		getContentPane().add(lblScanEndAngle);
		
		tfScanEndAngle = new JTextField();
		tfScanEndAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle);
		tfScanEndAngle.setBounds(342, 958, 47, 19);
		getContentPane().add(tfScanEndAngle);
		tfScanEndAngle.setColumns(10);
		
		JLabel lblScanStepSize = new JLabel("Scan Step Size");
		lblScanStepSize.setBounds(407, 960, 117, 15);
		getContentPane().add(lblScanStepSize);
		
		tfScanStepSize = new JTextField();
		tfScanStepSize.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
		tfScanStepSize.setBounds(542, 958, 47, 19);
		getContentPane().add(tfScanStepSize);
		tfScanStepSize.setColumns(10);
		
		JButton btnExportPoints = new JButton("Export points");
		btnExportPoints.setBounds(1114, 886, 129, 25);
		getContentPane().add(btnExportPoints);
		btnExportPoints.addActionListener(this);

		m_camera = m_e3d.m_camera;
		
		setSize(1300,1020);
		
		calculateCalibrationPositions();
		
		setModal(true);
		
		m_cal_data = m_e3d.m_cal_data;
}
	
	private void calculateCalibrationPositions() {
		if(m_cal_data == null)
		{
			m_cal_data = new CalibrationData();
			m_cal_data.v_offset = Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset;
		}
		
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation==90)
		{
			m_cal_data.capture_h = (int)m_camera.getViewSize().getWidth();
			m_cal_data.capture_w = (int)m_camera.getViewSize().getHeight();
		}
		else
		{
			m_cal_data.capture_w = (int)m_camera.getViewSize().getWidth();
			m_cal_data.capture_h = (int)m_camera.getViewSize().getHeight();
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
		if(e.getActionCommand()=="Capture")
		{
			BufferedImage l_image = m_camera.getImage();
			l_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}
			
//			image.circles=null;
			
			image.m_image = l_image;
			image.m_overlay = null;
			capturedImage = l_image;
			image.repaint();
		} else
		if(e.getActionCommand()=="Circles")
		{
			if(capturedImage == null) return;
			
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
				l_cd.initTotalCircles(capturedImage);
//				System.out.println("Detecting circles");
				if(image.pos==1)
				{
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_tl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_tl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_tr.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tr.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tr.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_tr.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_bl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_bl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_bl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_bl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_br.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_br.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_br.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_br.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
				}
				else
				{
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_tl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_tl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_tr.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tr.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tr.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_tr.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_bl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_bl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_bl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_bl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_br.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_br.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_br.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_br.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
				}
//				System.out.println("Detection complete");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			image.m_overlay = l_cd.displayimage;
			image.repaint();
/*
			Mat input = new Mat(l_image.getHeight(), l_image.getWidth(), CvType.CV_8UC3);
			byte data[] = ((DataBufferByte)l_image.getRaster().getDataBuffer()).getData();
			input.put(0,  0,  data);
			Mat grey = new Mat();
			Imgproc.cvtColor(input,  grey,  Imgproc.COLOR_BGRA2GRAY);
			image.circles = new Mat(10, 10, CvType.CV_64FC1);
			System.out.println("Detecting circles");
			Imgproc.HoughCircles(grey, image.circles, Imgproc.CV_HOUGH_GRADIENT,
					Integer.parseInt(txtResratio.getText()),
					Integer.parseInt(txtMinDist.getText()),
					Integer.parseInt(txtParam.getText()),
					Integer.parseInt(txtParam_1.getText()),
					Integer.parseInt(txtMinRad.getText()),
					Integer.parseInt(txtMaxRad.getText()));
			System.out.println("Detection complete");
*/
		} else
/*		if(e.getActionCommand()=="Detect")
		{
			if(m_detect_thread == null)
			{
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = Integer.parseInt(txtTopleftmotorpos.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = Integer.parseInt(txtToprightmotorpos.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = Integer.parseInt(txtTopleftmotorpos_2.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = Integer.parseInt(txtToprightmotorpos_2.getText());
				if(m_thread == null)
				{
					m_thread = new Thread(this);
					m_thread.start();
				}
				Runnable l_runnable = () -> {
					Detect();
				};
				m_detect_thread = new Thread(l_runnable);
				m_detect_thread.start();
			}
		} else*/
		if(e.getActionCommand()=="Calibrate")
		{
			if(m_calibration_thread == null)
			{
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
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
		} else
		if(e.getSource().equals(radioButton))
		{
			if(radioButton.isSelected()) image.pos = 1;
		} else
		if(e.getSource().equals(radioButton_1))
		{
			if(radioButton_1.isSelected()) image.pos = 2;
		}/* else
		if(e.getActionCommand()=="Export points")
		{
			m_pco.save("test.ply");
		}*/
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
			image.m_overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
					    		m_cal_data.pos_1_tl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tl.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_1_tl.x+m_cal_data.pos_1_tl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_1_tl.y+m_cal_data.pos_1_tl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_1_tl.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_1_tr.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tr.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_1_tr.x+m_cal_data.pos_1_tr.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_1_tr.y+m_cal_data.pos_1_tr.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_1_tr.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_1_bl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_bl.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_1_bl.x+m_cal_data.pos_1_bl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_1_bl.y+m_cal_data.pos_1_bl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_1_bl.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_1_br.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_br.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_1_br.x+m_cal_data.pos_1_br.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_1_br.y+m_cal_data.pos_1_br.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_1_br.width/2))<m_cal_data.circle_radius_threshold)
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
				return;
			}
		} while (l_goodenough < 4);
		// Hunt for laser in detection circles
		{
			int l_pos, l_pos_slow;
//			System.out.println("Move to base pos");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-2))
			{
				m_calibration_thread = null;
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
					case 0: l_search_box = m_cal_data.pos_1_tl; break;
					case 1: l_search_box = m_cal_data.pos_1_tr; break;
					case 2: l_search_box = m_cal_data.pos_1_bl; break;
					case 3: l_search_box = m_cal_data.pos_1_br; break;
				}
				if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-1))
				{
					m_calibration_thread = null;
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

					int l_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					return;
				}
				// Slow search
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
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
					{
						l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
					}
	
					int l_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos_slow >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					return;
				}
				l_corner_pos[corner] = l_pos_slow;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		}
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = l_corner_pos[0];
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		System.out.println("Motorpos TL 1: "+l_corner_pos[0]);
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = l_corner_pos[1];
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		System.out.println("Motorpos TR 1: "+l_corner_pos[1]);
		
		
		
		
		image.pos = 2;
		// Repeat for pos2
		int l_corner_pos_2[] = {0, 0, 0, 0};
		// Phase one, make sure board is in right place for pos1
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
			image.m_overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
					    		m_cal_data.pos_2_tl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_tl.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_2_tl.x+m_cal_data.pos_2_tl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_2_tl.y+m_cal_data.pos_2_tl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_2_tl.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_2_tr.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tr.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tr.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_tr.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_2_tr.x+m_cal_data.pos_2_tr.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_2_tr.y+m_cal_data.pos_2_tr.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_2_tr.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_2_bl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_bl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_bl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_bl.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_2_bl.x+m_cal_data.pos_2_bl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_2_bl.y+m_cal_data.pos_2_bl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_2_bl.width/2))<m_cal_data.circle_radius_threshold)
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
					    		m_cal_data.pos_2_br.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_br.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_br.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_br.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_2_br.x+m_cal_data.pos_2_br.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_2_br.y+m_cal_data.pos_2_br.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_2_br.width/2))<m_cal_data.circle_radius_threshold)
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
				image.pos = 1;
				return;
			}
		} while (l_goodenough < 4);
		// Hunt for laser in detection circles
		{
			int l_pos, l_pos_slow;
//			System.out.println("Move to base pos");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-2))
			{
				image.pos = 1;
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
					case 0: l_search_box = m_cal_data.pos_2_tl; break;
					case 1: l_search_box = m_cal_data.pos_2_tr; break;
					case 2: l_search_box = m_cal_data.pos_2_bl; break;
					case 3: l_search_box = m_cal_data.pos_2_br; break;
				}
				if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-1))
				{
					image.pos = 1;
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
						image.pos = 1;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
					{
						l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
					}

					int l_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						image.pos = 1;
						break;
					}
				}
				if(l_pos >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					image.pos = 1;
					return;
				}
				// Slow search
				for(
						l_pos_slow = l_pos - Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
						l_pos_slow < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						++l_pos_slow)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos_slow))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						image.pos = 1;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
					{
						l_image = rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
					}
	
					int l_found_offset = m_e3d.m_cal_data.findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos_slow >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					image.pos = 1;
					return;
				}
				l_corner_pos_2[corner] = l_pos_slow;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		}
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = l_corner_pos_2[0];
		txtTopleftmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2);
		System.out.println("Motorpos TL 2: "+l_corner_pos_2[0]);
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = l_corner_pos_2[1];
		txtToprightmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2);
		System.out.println("Motorpos TR 2: "+l_corner_pos_2[1]);
		invalidate();
		
		image.pos = 1;
		m_calibration_thread = null;

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(e.getSource().equals(sbHeightOffset))
		{
//			System.out.println("Scrollbar at "+sbHeightOffset.getValue());
			m_cal_data.v_offset = sbHeightOffset.getValue();
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
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
		Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset = m_cal_data.v_offset;
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = Integer.parseInt(txtTopleftmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = Integer.parseInt(txtToprightmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = Integer.parseInt(txtTopleftmotorpos_2.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = Integer.parseInt(txtToprightmotorpos_2.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad = Integer.parseInt(this.txtMinRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad = Integer.parseInt(this.txtMaxRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_v_offset = sbHeightOffset.getValue();
		Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
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

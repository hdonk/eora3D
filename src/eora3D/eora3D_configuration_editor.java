package eora3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class eora3D_configuration_editor extends JDialog implements ActionListener {
	Eora3D_MainWindow m_e3d = null;
	
	private JTextField tfLaser0pointoffset;
	private JTextField tfLaserstepsperdegree;
	private JTextField tfCircleminradius;
	private JTextField tfCirclemaxradius;
	private JTextField tfBoardverticaloffset;
	private JTextField tfDetectionangleleft;
	private JTextField tfDetectionangleright;
	private JTextField tfRedthreshold;
	private JTextField tfGreenthreshold;
	private JTextField tfBluethreshold;
	private JTextField tfPercentagethreshold;
	private JTextField tfStartposition;
	private JTextField tfEndposition;
	private JTextField tfStepsize;
	private JTextField tfThreads;
	private JTextField tfTestframe;
	private JComboBox<String> cbAlgorithm;
	private JComboBox<String> cbCamerarotation;
	private JComboBox<String> cbTurntableStops;
	private JTextField tfCameraWidth;
	private JTextField tfCameraHeight;
	private JTextField tfMaxPointsPerLine;
	private JTextField tfMinpointsperlaser;
	private JTextField tfLatencyframes;

	eora3D_configuration_editor(Eora3D_MainWindow a_e3d)
	{
		super();
		m_e3d = a_e3d;
		setResizable(false);
		setModal(true);
		setSize(495, 847);
		setTitle("Configuration");
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Laser", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 0, 163, 146);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblLaserPoint = new JLabel("Laser 0 point offset");
		lblLaserPoint.setBounds(6, 21, 127, 15);
		panel.add(lblLaserPoint);
		
		tfLaser0pointoffset = new JTextField();
		tfLaser0pointoffset.setBounds(6, 37, 122, 27);
		panel.add(tfLaser0pointoffset);
		tfLaser0pointoffset.setColumns(10);
		tfLaser0pointoffset.addActionListener(this);
		
		JLabel lblLaserStepsPer = new JLabel("Laser steps per degree");
		lblLaserStepsPer.setBounds(6, 65, 147, 15);
		panel.add(lblLaserStepsPer);
		
		tfLaserstepsperdegree = new JTextField();
		tfLaserstepsperdegree.setBounds(6, 86, 122, 27);
		panel.add(tfLaserstepsperdegree);
		tfLaserstepsperdegree.setColumns(10);
		tfLaserstepsperdegree.addActionListener(this);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Calibration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(6, 148, 163, 285);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblCircleMinRadius = new JLabel("Circle Min Radius");
		lblCircleMinRadius.setBounds(6, 21, 138, 15);
		panel_1.add(lblCircleMinRadius);
		
		tfCircleminradius = new JTextField();
		tfCircleminradius.setBounds(6, 37, 122, 27);
		panel_1.add(tfCircleminradius);
		tfCircleminradius.setColumns(10);
		tfCircleminradius.addActionListener(this);
		
		JLabel lblCircleMaxRadius = new JLabel("Circle Max Radius");
		lblCircleMaxRadius.setBounds(6, 63, 113, 15);
		panel_1.add(lblCircleMaxRadius);
		
		tfCirclemaxradius = new JTextField();
		tfCirclemaxradius.setBounds(6, 80, 122, 27);
		panel_1.add(tfCirclemaxradius);
		tfCirclemaxradius.setColumns(10);
		tfCirclemaxradius.addActionListener(this);
		
		JLabel lblBoardVerticalOffset = new JLabel("Board vertical offset");
		lblBoardVerticalOffset.setBounds(6, 109, 138, 15);
		panel_1.add(lblBoardVerticalOffset);
		
		tfBoardverticaloffset = new JTextField();
		tfBoardverticaloffset.setBounds(6, 129, 122, 27);
		panel_1.add(tfBoardverticaloffset);
		tfBoardverticaloffset.setColumns(10);
		tfBoardverticaloffset.addActionListener(this);
		
		JLabel lblDetectionAngleLeft = new JLabel("Detection angle left");
		lblDetectionAngleLeft.setBounds(6, 160, 138, 15);
		panel_1.add(lblDetectionAngleLeft);
		
		tfDetectionangleleft = new JTextField();
		tfDetectionangleleft.setBounds(6, 178, 122, 27);
		panel_1.add(tfDetectionangleleft);
		tfDetectionangleleft.setColumns(10);
		tfDetectionangleleft.addActionListener(this);
		
		JLabel lblDetectionAngleRight = new JLabel("Detection angle right");
		lblDetectionAngleRight.setBounds(6, 217, 155, 15);
		panel_1.add(lblDetectionAngleRight);
		
		tfDetectionangleright = new JTextField();
		tfDetectionangleright.setBounds(6, 238, 122, 27);
		panel_1.add(tfDetectionangleright);
		tfDetectionangleright.setColumns(10);
		tfDetectionangleright.addActionListener(this);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Laser detection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(6, 465, 335, 241);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblRedThreshold = new JLabel("Red threshold");
		lblRedThreshold.setBounds(6, 73, 97, 15);
		panel_2.add(lblRedThreshold);
		
		tfRedthreshold = new JTextField();
		tfRedthreshold.setBounds(6, 92, 122, 27);
		panel_2.add(tfRedthreshold);
		tfRedthreshold.setColumns(10);
		tfRedthreshold.addActionListener(this);
		
		JLabel lblGreenThreshold = new JLabel("Green threshold");
		lblGreenThreshold.setBounds(6, 126, 122, 15);
		panel_2.add(lblGreenThreshold);
		
		tfGreenthreshold = new JTextField();
		tfGreenthreshold.setBounds(6, 143, 122, 27);
		panel_2.add(tfGreenthreshold);
		tfGreenthreshold.setColumns(10);
		tfGreenthreshold.addActionListener(this);
		
		JLabel lblAlgorithm = new JLabel("Algorithm");
		lblAlgorithm.setBounds(6, 20, 60, 15);
		panel_2.add(lblAlgorithm);
		
		cbAlgorithm = new JComboBox<String>();
		cbAlgorithm.setBounds(6, 36, 122, 25);
		cbAlgorithm.addItem("Or");
		cbAlgorithm.addItem("And");
		cbAlgorithm.addItem("%");
		cbAlgorithm.addItem("Weighted %");
		panel_2.add(cbAlgorithm);
		
		JLabel lblBlueThreshold = new JLabel("Blue threshold");
		lblBlueThreshold.setBounds(6, 174, 122, 15);
		panel_2.add(lblBlueThreshold);
		
		tfBluethreshold = new JTextField();
		tfBluethreshold.setBounds(6, 195, 122, 27);
		panel_2.add(tfBluethreshold);
		tfBluethreshold.setColumns(10);
		tfBluethreshold.addActionListener(this);
		
		JLabel lblPercentageThreshold = new JLabel("Percentage threshold");
		lblPercentageThreshold.setBounds(140, 41, 149, 20);
		panel_2.add(lblPercentageThreshold);
		
		tfPercentagethreshold = new JTextField();
		tfPercentagethreshold.setBounds(140, 67, 122, 27);
		panel_2.add(tfPercentagethreshold);
		tfPercentagethreshold.setColumns(10);
		tfPercentagethreshold.addActionListener(this);
		
		JLabel lblMaxPointsPer = new JLabel("Max points per line");
		lblMaxPointsPer.setBounds(140, 104, 174, 15);
		panel_2.add(lblMaxPointsPer);
		
		tfMaxPointsPerLine = new JTextField();
		tfMaxPointsPerLine.setText("3");
		tfMaxPointsPerLine.setBounds(140, 124, 122, 27);
		panel_2.add(tfMaxPointsPerLine);
		tfMaxPointsPerLine.setColumns(10);
		tfMaxPointsPerLine.addActionListener(this);
		
		JLabel lblMinLaserBand = new JLabel("Min laser band points");
		lblMinLaserBand.setBounds(146, 163, 158, 15);
		panel_2.add(lblMinLaserBand);
		
		tfMinpointsperlaser = new JTextField();
		tfMinpointsperlaser.setText("3");
		tfMinpointsperlaser.setBounds(140, 195, 122, 27);
		panel_2.add(tfMinpointsperlaser);
		tfMinpointsperlaser.setColumns(10);
		tfMinpointsperlaser.addActionListener(this);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Camera", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(181, 0, 123, 204);
		getContentPane().add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblCameraRotation = new JLabel("Camera rotation");
		lblCameraRotation.setBounds(6, 18, 107, 15);
		panel_3.add(lblCameraRotation);
		
		cbCamerarotation = new JComboBox<String>();
		cbCamerarotation.setBounds(6, 32, 72, 25);
		cbCamerarotation.addItem("0");
		cbCamerarotation.addItem("90");
		cbCamerarotation.addItem("180");
		cbCamerarotation.addItem("270");
		panel_3.add(cbCamerarotation);
		
		JLabel lblWidth = new JLabel("Width");
		lblWidth.setBounds(6, 58, 60, 15);
		panel_3.add(lblWidth);
		
		tfCameraWidth = new JTextField();
		tfCameraWidth.setBounds(6, 76, 107, 27);
		panel_3.add(tfCameraWidth);
		tfCameraWidth.setColumns(10);
		tfCameraWidth.addActionListener(this);
		
		JLabel lblHeight = new JLabel("Height");
		lblHeight.setBounds(6, 101, 60, 15);
		panel_3.add(lblHeight);
		
		tfCameraHeight = new JTextField();
		tfCameraHeight.setBounds(6, 115, 107, 27);
		panel_3.add(tfCameraHeight);
		tfCameraHeight.setColumns(10);
		tfCameraHeight.addActionListener(this);
		
		JLabel lblLatencyFrames = new JLabel("Latency frames");
		lblLatencyFrames.setBounds(6, 143, 107, 15);
		panel_3.add(lblLatencyFrames);
		
		tfLatencyframes = new JTextField();
		tfLatencyframes.setBounds(6, 158, 111, 27);
		panel_3.add(tfLatencyframes);
		tfLatencyframes.setText("0");
		tfLatencyframes.setColumns(10);
		tfLatencyframes.addActionListener(this);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new TitledBorder(null, "Scan", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		panel_4.setBounds(182, 204, 136, 171);
		getContentPane().add(panel_4);
		
		JLabel lblStartPosition = new JLabel("Start position");
		lblStartPosition.setBounds(6, 24, 96, 15);
		panel_4.add(lblStartPosition);
		
		tfStartposition = new JTextField();
		tfStartposition.setBounds(6, 38, 122, 27);
		panel_4.add(tfStartposition);
		tfStartposition.setColumns(10);
		tfStartposition.addActionListener(this);
		
		JLabel lblEndPosition = new JLabel("End position");
		lblEndPosition.setBounds(6, 67, 96, 15);
		panel_4.add(lblEndPosition);
		
		tfEndposition = new JTextField();
		tfEndposition.setBounds(6, 85, 122, 27);
		panel_4.add(tfEndposition);
		tfEndposition.setColumns(10);
		tfEndposition.addActionListener(this);
		
		JLabel lblStepSize = new JLabel("Step size");
		lblStepSize.setBounds(6, 120, 84, 15);
		panel_4.add(lblStepSize);
		
		tfStepsize = new JTextField();
		tfStepsize.setBounds(6, 134, 122, 27);
		panel_4.add(tfStepsize);
		tfStepsize.setColumns(10);
		tfStepsize.addActionListener(this);
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new TitledBorder(null, "Model generation", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		panel_5.setBounds(316, 0, 163, 116);
		getContentPane().add(panel_5);
		
		JLabel lblThreads = new JLabel("Threads");
		lblThreads.setBounds(6, 23, 60, 15);
		panel_5.add(lblThreads);
		
		tfThreads = new JTextField();
		tfThreads.setBounds(6, 40, 122, 27);
		panel_5.add(tfThreads);
		tfThreads.setColumns(10);
		tfThreads.addActionListener(this);
		
		JLabel lblTestFrame = new JLabel("Test frame");
		lblTestFrame.setBounds(6, 67, 89, 15);
		panel_5.add(lblTestFrame);
		
		tfTestframe = new JTextField();
		tfTestframe.setBounds(6, 79, 122, 27);
		panel_5.add(tfTestframe);
		tfTestframe.setColumns(10);
		tfTestframe.addActionListener(this);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(6, 737, 100, 27);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(this);
		
		JButton btnApply = new JButton("Apply");
		btnApply.setBounds(118, 737, 100, 27);
		getContentPane().add(btnApply);
		btnApply.addActionListener(this);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.setBounds(230, 737, 100, 27);
		getContentPane().add(btnLoad);
		btnLoad.addActionListener(this);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(342, 737, 100, 27);
		getContentPane().add(btnSave);
		btnSave.addActionListener(this);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "Turntable", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(181, 376, 150, 88);
		getContentPane().add(panel_6);
		panel_6.setLayout(null);
		
		JLabel lblStepSize_1 = new JLabel("Stops");
		lblStepSize_1.setBounds(6, 24, 70, 15);
		panel_6.add(lblStepSize_1);
		
		cbTurntableStops = new JComboBox<String>();
		cbTurntableStops.setBounds(6, 47, 114, 27);
		cbTurntableStops.addItem("18");
		cbTurntableStops.addItem("12");
		cbTurntableStops.addItem("6");
		cbTurntableStops.addItem("4");
		cbTurntableStops.addItem("2");
		panel_6.add(cbTurntableStops);
		
		setFromConfig(Eora3D_MainWindow.m_e3d_config);
	}
	
	void setFromConfig(eora3D_configuration_data_v1 a_cfg)
	{
		tfLaser0pointoffset.setText(""+a_cfg.sm_laser_0_offset);
		tfLaserstepsperdegree.setText(""+a_cfg.sm_laser_steps_per_deg);
		tfCircleminradius.setText(""+a_cfg.sm_circle_min_rad);
		tfCirclemaxradius.setText(""+a_cfg.sm_circle_max_rad);
		tfBoardverticaloffset.setText(""+a_cfg.sm_calibration_vertical_offset);
		tfDetectionangleleft.setText(""+a_cfg.sm_calibration_tl_motorpos_1);
		tfDetectionangleright.setText(""+a_cfg.sm_calibration_tr_motorpos_1);
		tfRedthreshold.setText(""+a_cfg.sm_laser_detection_threshold_r);
		tfGreenthreshold.setText(""+a_cfg.sm_laser_detection_threshold_g);
		tfBluethreshold.setText(""+a_cfg.sm_laser_detection_threshold_b);
		tfPercentagethreshold.setText(""+a_cfg.sm_laser_detection_threshold_percent);
		tfMaxPointsPerLine.setText(""+a_cfg.sm_max_points_per_line);
		tfMinpointsperlaser.setText(""+a_cfg.sm_min_points_per_laser);
		tfStartposition.setText(""+a_cfg.sm_scan_start_angle);
		tfEndposition.setText(""+a_cfg.sm_scan_end_angle);
		tfStepsize.setText(""+a_cfg.sm_scan_step_size);
		tfThreads.setText(""+a_cfg.sm_threads);
		tfTestframe.setText(""+a_cfg.sm_test_frame);
		tfCameraWidth.setText(""+a_cfg.sm_camera_res_w);
		tfCameraHeight.setText(""+a_cfg.sm_camera_res_h);
		cbTurntableStops.setSelectedItem(""+(6516/a_cfg.sm_turntable_step_size));
		
		if(a_cfg.sm_laser_detection_threshold_logic.equals("Or"))
		{
			cbAlgorithm.setSelectedIndex(0);
		}
		else if(a_cfg.sm_laser_detection_threshold_logic.equals("And"))
		{
			cbAlgorithm.setSelectedIndex(1);
		}
		else if(a_cfg.sm_laser_detection_threshold_logic.equals("%"))
		{
			cbAlgorithm.setSelectedIndex(2);
		}
		else if(a_cfg.sm_laser_detection_threshold_logic.equals("Weighted %"))
		{
			cbAlgorithm.setSelectedIndex(3);
		}
		switch(a_cfg.sm_camera_rotation)
		{
			case 0:
				cbCamerarotation.setSelectedIndex(0);
				break;
			case 90:
				cbCamerarotation.setSelectedIndex(1);
				break;
			case 180:
				cbCamerarotation.setSelectedIndex(2);
				break;
			case 270:
				cbCamerarotation.setSelectedIndex(3);
				break;
		}
	}
	
	void setConfig(eora3D_configuration_data_v1 a_cfg)
	{
		a_cfg.sm_laser_0_offset = Integer.parseInt(tfLaser0pointoffset.getText());
		a_cfg.sm_laser_steps_per_deg = Integer.parseInt(tfLaserstepsperdegree.getText());
		a_cfg.sm_circle_min_rad = Integer.parseInt(tfCircleminradius.getText());
		a_cfg.sm_circle_max_rad = Integer.parseInt(tfCirclemaxradius.getText());
		a_cfg.sm_calibration_vertical_offset = Integer.parseInt(tfBoardverticaloffset.getText());
		a_cfg.sm_calibration_tl_motorpos_1 = Integer.parseInt(tfDetectionangleleft.getText());
		a_cfg.sm_calibration_tr_motorpos_1 = Integer.parseInt(tfDetectionangleright.getText());
		a_cfg.sm_laser_detection_threshold_r = Integer.parseInt(tfRedthreshold.getText());
		a_cfg.sm_laser_detection_threshold_g = Integer.parseInt(tfGreenthreshold.getText());
		a_cfg.sm_laser_detection_threshold_b = Integer.parseInt(tfBluethreshold.getText());
		a_cfg.sm_laser_detection_threshold_percent = Float.parseFloat(tfPercentagethreshold.getText());
		a_cfg.sm_max_points_per_line = Integer.parseInt(tfMaxPointsPerLine.getText());
		a_cfg.sm_min_points_per_laser = Integer.parseInt(tfMinpointsperlaser.getText());
		a_cfg.sm_scan_start_angle = Integer.parseInt(tfStartposition.getText());
		a_cfg.sm_scan_end_angle = Integer.parseInt(tfEndposition.getText());
		a_cfg.sm_scan_step_size = Integer.parseInt(tfStepsize.getText());
		a_cfg.sm_threads = Integer.parseInt(tfThreads.getText());
		a_cfg.sm_test_frame = Integer.parseInt(tfTestframe.getText());

		a_cfg.sm_camera_res_w = Integer.parseInt(tfCameraWidth.getText());
		a_cfg.sm_camera_res_h = Integer.parseInt(tfCameraHeight.getText());

		a_cfg.sm_turntable_step_size = (6516/Integer.parseInt(cbTurntableStops.getSelectedItem().toString())); 
		
		switch(cbAlgorithm.getSelectedIndex())
		{
			case 0:
				a_cfg.sm_laser_detection_threshold_logic = "Or"; break;
			case 1:
				a_cfg.sm_laser_detection_threshold_logic = "And"; break;
			case 2:
				a_cfg.sm_laser_detection_threshold_logic = "%"; break;
			case 3:
				a_cfg.sm_laser_detection_threshold_logic = "Weighted %"; break;
		}
		a_cfg.sm_camera_rotation = cbCamerarotation.getSelectedIndex()*90;
	}
	
	public void checkTextFieldIntRange(JTextField a_tf, int a_min, int a_max, int a_default)
	{
		int a_value;
		try
		{
			a_value = Integer.parseInt(a_tf.getText());
		}
		catch(Exception e)
		{
			a_tf.setText(""+a_default);
			return;
		}
		if(!(""+a_value).equals(a_tf.getText()))
		{
			a_tf.setText(""+a_default);
			return;
		}
		if(a_value < a_min)
		{
			a_tf.setText(""+a_min);
			return;
		}
		if(a_value > a_max)
		{
			a_tf.setText(""+a_max);
			return;
		}
	}
	
	public void checkTextFieldFloatRange(JTextField a_tf, float a_min, float a_max, float a_default)
	{
		float a_value;
		if(a_tf.getText().indexOf(".")==-1)
			a_tf.setText(a_tf.getText()+".0");
		try
		{
			a_value = Float.parseFloat(a_tf.getText());
		}
		catch(Exception e)
		{
			a_tf.setText(""+a_default);
			return;
		}
		if(!(""+a_value).equals(a_tf.getText()))
		{
			a_tf.setText(""+a_default);
			return;
		}
		if(a_value < a_min)
		{
			a_tf.setText(""+a_min);
			return;
		}
		if(a_value > a_max)
		{
			a_tf.setText(""+a_max);
			return;
		}		
	}
	
	public void verifyValueRanges()
	{
		eora3D_configuration_data_v1 l_default = new eora3D_configuration_data_v1();  
		checkTextFieldIntRange(tfLaser0pointoffset, 0, 9000, l_default.sm_laser_0_offset);
		checkTextFieldIntRange(tfLaserstepsperdegree, 1, 180, l_default.sm_laser_steps_per_deg);
		checkTextFieldIntRange(tfCircleminradius, 1, 10, l_default.sm_circle_min_rad);
		checkTextFieldIntRange(tfCirclemaxradius, 2, 11, l_default.sm_circle_max_rad);
		checkTextFieldIntRange(tfBoardverticaloffset, -200, +200, l_default.sm_calibration_v_offset);
		checkTextFieldIntRange(tfDetectionangleleft, 0, 9000, l_default.sm_calibration_tl_motorpos_1);
		checkTextFieldIntRange(tfDetectionangleright, 0, 9000, l_default.sm_calibration_tr_motorpos_1);
		checkTextFieldIntRange(tfRedthreshold, 1, 255, l_default.sm_laser_detection_threshold_r);
		checkTextFieldIntRange(tfGreenthreshold, 1, 255, l_default.sm_laser_detection_threshold_g);
		checkTextFieldIntRange(tfBluethreshold, 1, 255, l_default.sm_laser_detection_threshold_b);
		checkTextFieldFloatRange(tfPercentagethreshold, 0.1f, 200.0f, l_default.sm_laser_detection_threshold_percent);
		checkTextFieldIntRange(tfMaxPointsPerLine, 1, 10, l_default.sm_max_points_per_line);
		checkTextFieldIntRange(tfStartposition, 0, 9000, l_default.sm_scan_start_angle);
		checkTextFieldIntRange(tfEndposition, 1, 9000, l_default.sm_scan_end_angle);
		checkTextFieldIntRange(tfStepsize, 1, 180, l_default.sm_scan_step_size);
		checkTextFieldIntRange(tfThreads, 1, 64, l_default.sm_threads);
		checkTextFieldIntRange(tfTestframe, 0, 9000, l_default.sm_test_frame);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		verifyValueRanges();
		if(e.getActionCommand()=="Cancel")
		{
			setVisible(false);
			return;
		}
		else
		if(e.getActionCommand()=="Apply")
		{
			setConfig(m_e3d.m_e3d_config);
			setVisible(false);
			return;
		}
		else
		if(e.getActionCommand()=="Load")
		{
			JFileChooser l_fc;
			l_fc = new JFileChooser(System.getProperty("user.home"));
			l_fc.setFileFilter(new extensionFileFilter("e3d"));
			l_fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			l_fc.setSelectedFile(m_e3d.m_e3d_config.sm_config_file);
			int l_ret = l_fc.showOpenDialog(null);

			if (l_ret == JFileChooser.APPROVE_OPTION) {
				File l_file = l_fc.getSelectedFile();
				try {
					FileInputStream l_fis = new FileInputStream(l_file);
					ObjectInputStream l_ois = new ObjectInputStream(l_fis);
					eora3D_configuration_version l_e3d_config_ver = (eora3D_configuration_version) l_ois.readObject();
					if(l_e3d_config_ver.sm_config_version!=1)
					{
						System.err.println("Unrecognised configuration file");
						l_ois.close();
						return;
					}
					eora3D_configuration_data_v1 l_config;
					l_config = (eora3D_configuration_data_v1) l_ois.readObject();
					l_ois.close();
					l_fis.close();
					l_config.sm_config_file = l_file;
					setFromConfig(l_config);
//					JOptionPane.showMessageDialog(getContentPane(), "Ok", "Load", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Serialized data loaded from " + l_file);
				} catch (Exception ioe) {
					ioe.printStackTrace();
					System.err.println("Failed to open " + l_file);
					JOptionPane.showMessageDialog(getContentPane(), "Failed", "Load", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else
		if(e.getActionCommand()=="Save")
		{
			eora3D_configuration_data_v1 l_config = new eora3D_configuration_data_v1();
			setConfig(l_config);
			JFileChooser l_fc;
			l_fc = new JFileChooser(m_e3d.m_e3d_config.sm_config_file);
			l_fc.setFileFilter(new extensionFileFilter("e3d"));
			l_fc.setSelectedFile(m_e3d.m_e3d_config.sm_config_file);
			int l_ret = l_fc.showSaveDialog(this);

			if (l_ret == JFileChooser.APPROVE_OPTION)
			{
				File l_file = l_fc.getSelectedFile();
				if(!l_file.toString().contains("."))
				{
					l_file = new File(l_file.toString()+".e3d");
				}
				l_config.sm_config_file = l_file;
				try {
					FileOutputStream l_fos = new FileOutputStream(l_file);
					ObjectOutputStream l_oos = new ObjectOutputStream(l_fos);
					l_oos.writeObject(new eora3D_configuration_version());
					l_oos.writeObject(l_config);
					l_oos.close();
					l_fos.close();
					JOptionPane.showMessageDialog(getContentPane(), "Ok", "Save", JOptionPane.INFORMATION_MESSAGE);
					setFromConfig(l_config);
					System.out.println("Serialized data is saved in " + l_file);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					JOptionPane.showMessageDialog(getContentPane(), "Failed", "Save", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}

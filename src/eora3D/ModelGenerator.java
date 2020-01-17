package eora3D;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.BevelBorder;
import javax.imageio.ImageIO;
import javax.swing.JButton;

public class ModelGenerator extends JDialog implements ActionListener, WindowListener, AdjustmentListener {
	Eora3D_MainWindow m_e3d;
	private JTextField tfRedthreshold;
	private JTextField tfGreenthreshold;
	private JTextField tfBluethreshold;
	private JTextField tfPercentagechange;
	private PaintImage imagePanel;
	private JScrollBar sbScaling;
	private JScrollBar sbPointsize;
	private JComboBox cbDetectionmethod;
	
	PointCloudObject m_pco;

	Thread m_detect_thread = null;
	boolean m_stop_detection = false;
	
	public ModelGenerator(Eora3D_MainWindow a_e3d) {
		setResizable(false);
		setModal(true);
		m_e3d = a_e3d;
		setTitle("Generate Model");
		getContentPane().setLayout(null);
		setSize(792,675);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(6, 0, 141, 414);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel("Laser detection");
		label.setBounds(6, 6, 106, 15);
		panel.add(label);
		
		JLabel label_1 = new JLabel("Detection method");
		label_1.setBounds(6, 33, 122, 15);
		panel.add(label_1);
		
		cbDetectionmethod = new JComboBox();
		cbDetectionmethod.setBounds(6, 60, 106, 25);
		panel.add(cbDetectionmethod);
		cbDetectionmethod.addItem("Or");
		cbDetectionmethod.addItem("And");
		cbDetectionmethod.addItem("%");
		cbDetectionmethod.addItem("Weighted %");
		cbDetectionmethod.setSelectedIndex(0);
		
		JLabel label_2 = new JLabel("Red threshold");
		label_2.setBounds(6, 97, 106, 15);
		panel.add(label_2);
		
		tfRedthreshold = new JTextField();
		tfRedthreshold.setEnabled(false);
		tfRedthreshold.setColumns(10);
		tfRedthreshold.setBounds(6, 124, 122, 27);
		panel.add(tfRedthreshold);
		
		JLabel label_3 = new JLabel("Green threshold");
		label_3.setBounds(6, 163, 106, 15);
		panel.add(label_3);
		
		tfGreenthreshold = new JTextField();
		tfGreenthreshold.setEnabled(false);
		tfGreenthreshold.setColumns(10);
		tfGreenthreshold.setBounds(6, 190, 122, 27);
		panel.add(tfGreenthreshold);
		
		JLabel label_4 = new JLabel("Blue threshold");
		label_4.setBounds(6, 229, 106, 15);
		panel.add(label_4);
		
		tfBluethreshold = new JTextField();
		tfBluethreshold.setEnabled(false);
		tfBluethreshold.setColumns(10);
		tfBluethreshold.setBounds(6, 256, 122, 27);
		panel.add(tfBluethreshold);
		
		tfRedthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r);
		tfGreenthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g);
		tfBluethreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b);
		
		JLabel label_5 = new JLabel("% change");
		label_5.setBounds(6, 295, 106, 15);
		panel.add(label_5);
		
		tfPercentagechange = new JTextField();
		tfPercentagechange.setEnabled(false);
		tfPercentagechange.setColumns(10);
		tfPercentagechange.setBounds(6, 322, 122, 27);
		panel.add(tfPercentagechange);
		
		JButton btnTest = new JButton("Test");
		btnTest.setBounds(19, 381, 100, 27);
		panel.add(btnTest);
		btnTest.addActionListener(this);
		
		imagePanel = new PaintImage();
		imagePanel.setBounds(148, 0, 468, 497);
		getContentPane().add(imagePanel);
		imagePanel.m_cal_data = m_e3d.m_cal_data;
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(642, 6, 100, 27);
		getContentPane().add(btnGenerate);
		btnGenerate.addActionListener(this);
		
		JButton btnExport = new JButton("Export");
		btnExport.setBounds(642, 45, 100, 27);
		getContentPane().add(btnExport);
		btnExport.addActionListener(this);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(642, 84, 100, 27);
		getContentPane().add(btnFinish);
		btnFinish.addActionListener(this);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setBounds(628, 443, 150, 190);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lbldDisplay = new JLabel("3D Display");
		lbldDisplay.setBounds(6, 6, 79, 15);
		panel_1.add(lbldDisplay);
		
		JLabel lblScaling = new JLabel("Scaling");
		lblScaling.setBounds(6, 33, 60, 15);
		panel_1.add(lblScaling);
		
		sbScaling = new JScrollBar();
		sbScaling.setOrientation(JScrollBar.HORIZONTAL);
		sbScaling.setBounds(6, 60, 122, 27);
		sbScaling.setValue(10);
		sbScaling.setMinimum(1);
		sbScaling.setMaximum(40);
		panel_1.add(sbScaling);
		sbScaling.addAdjustmentListener(this);
		
		JLabel lblPointSize = new JLabel("Point size");
		lblPointSize.setBounds(6, 99, 60, 15);
		panel_1.add(lblPointSize);
		
		sbPointsize = new JScrollBar();
		sbPointsize.setOrientation(JScrollBar.HORIZONTAL);
		sbPointsize.setBounds(6, 126, 122, 27);
		sbPointsize.setBlockIncrement(10);
		sbPointsize.setUnitIncrement(10);
		sbPointsize.setMinimum(10);
		sbPointsize.setValue(10);
		sbPointsize.setMaximum(80);
		sbPointsize.setVisibleAmount(4);
		panel_1.add(sbPointsize);
		sbPointsize.addAdjustmentListener(this);
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(6, 606, 100, 27);
		getContentPane().add(btnConfig);
		btnConfig.addActionListener(this);
		
		m_pco = new PointCloudObject();
		m_pco.m_Pointsize = sbPointsize.getValue();
		m_pco.m_Scale = sbScaling.getValue();
		new Thread(m_pco).start();
		
		addWindowListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		if(ae.getActionCommand() == "Generate")
		{
			if(m_detect_thread==null)
			{
				Runnable l_runnable = () -> {
					Detect();
				};
				m_detect_thread = new Thread(l_runnable);
				m_detect_thread.start();
			}
		} else
		if(ae.getActionCommand() == "Export")
		{
			FileDialog l_fd;
			l_fd = new FileDialog(this);
			l_fd.setVisible(true);
			
/*
			JFileChooser l_fc;
			l_fc = new JFileChooser();
			l_fc.setSelectedFile(new File("export.ply"));
			l_fc.setFileFilter(new extensionFileFilter("ply"));
			int l_ret = l_fc.showSaveDialog(this);

			if (l_ret == JFileChooser.APPROVE_OPTION) {
				File l_file = l_fc.getSelectedFile();
				if(!l_file.toString().contains("."))
				{
					l_file = new File(l_file.toString()+".ply");
				}
				
				m_pco.save(l_file);
			}*/
			m_pco.save(new File("export.ply"));
		} else
		if(ae.getActionCommand() == "Finish")
		{
			m_pco.Close();
			this.setVisible(false);
		}
	}

	void Detect()
	{
		m_pco.clear();
		m_e3d.m_cal_data.calculate();
		m_e3d.m_cal_data.calculateBaseCoords();
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(tfRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(tfGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(tfBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)cbDetectionmethod.getSelectedItem();
		File l_basefile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_base.png");
		for (int l_pos = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset;
				l_pos < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*45;
				++l_pos)
		{
			File l_infile;
			try {
				l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_"+l_pos+".png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_detect_thread=null;
				return;
			}
			if(!l_infile.exists()) continue;
//			System.out.println("Analysing "+l_infile.toString());
			BufferedImage l_inimage, l_baseimage;

			try {
				l_baseimage = ImageIO.read(l_basefile);
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				e1.printStackTrace();
				m_detect_thread=null;
				return;
			}
			
			imagePanel.m_image = l_baseimage;
			imagePanel.m_overlay = analyzeImage(l_baseimage, l_inimage);
			
			Thread l_threads[] = new Thread[Eora3D_MainWindow.m_e3d_config.sm_threads];
			for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
			{
				final int l_lambda_thread = l_thread;
				final int l_lambda_pos = l_pos;
				Runnable l_task = () -> { 
					int l_x_points[];
					// TBC correctify this!
					int l_range_start = (l_baseimage.getHeight()/8)*l_lambda_thread;
					int l_range_end = (l_baseimage.getHeight()/8)*(l_lambda_thread+1);
					l_x_points = analyzeImagetoArray(l_baseimage, l_inimage, l_range_start, l_range_end);
					for( int i=l_range_start; i<l_range_end; ++i)
					{
						if(l_x_points[i]>=0)
						{
							RGB3DPoint l_point = m_e3d.m_cal_data.getPointOffset(l_lambda_pos, l_x_points[i], (l_baseimage.getHeight()-i)-1);
							l_point.m_r = (l_baseimage.getRGB(l_x_points[i], i) & 0xff0000)>>16;
							l_point.m_g = (l_baseimage.getRGB(l_x_points[i], i) & 0xff00)>>8;
							l_point.m_b = l_baseimage.getRGB(l_x_points[i], i) & 0x00;
							System.out.println(l_point.m_x+","+l_point.m_y+","+l_point.m_z);
//							System.out.println("Z calculated as "+l_x_points[i]+" -> "+m_cal_data.getZoffset(l_pos, l_x_points[i]));
							m_pco.addPoint(l_point);
						}
					}
				};
				l_threads[l_thread] = new Thread(l_task);
				l_threads[l_thread].start();
			}
			for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
			{
				try {
					l_threads[l_thread].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			imagePanel.repaint();
			if(m_stop_detection) return;
//			System.out.println("Complete "+l_infile.toString());
		}
//		System.out.println("Found "+l_points+" points");
		System.out.println("Min Z: "+m_e3d.m_cal_data.m_minz);
		System.out.println("Max Z: "+m_e3d.m_cal_data.m_maxz);
		m_detect_thread=null;

	}
	
	BufferedImage analyzeImage(BufferedImage a_base, BufferedImage a_in)
	{
		BufferedImage a_out = new BufferedImage(a_in.getWidth(), a_in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int x, y;

		for(y = 0; y< a_in.getHeight(); ++y)
		{
			for(x = a_in.getWidth()-1; x>=0; --x)
			{
				int argb = a_in.getRGB(x, y);
				int r = (argb & 0xff0000) >> 16;
            	int g = (argb & 0x00ff00) >> 8;
        		int b = (argb & 0x0000ff);
        		
				int argb_base = a_base.getRGB(x, y);
				int r_base = (argb_base & 0xff0000) >> 16;
            	int g_base = (argb_base & 0x00ff00) >> 8;
        		int b_base = (argb_base & 0x0000ff);
        		
        		//r = ((255-r)-(255-r_base));
        		//g = ((255-g)-(255-g_base));
        		//b = ((255-b)-(255-b_base));
        		r = Math.abs(r_base-r);
        		g = Math.abs(g_base-g);
        		b = Math.abs(b_base-b);
//        		g = b = 0;
/*        		r = Math.abs(r-r_base);
        		g = Math.abs(g-g_base);
        		b = Math.abs(b-b_base);*/
        		
/*        		r = Math.max(0, r);
        		g = Math.max(0, g);
        		b = Math.max(0, b);*/

//        		r = 255-r;
//        		g = 255-g;
//        		b = 255-b;

        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) argb = 0xff00ff00;
	        		else argb = 0xff000000;
        		} else
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) argb = 0xff00ff00;
	        		else argb = 0xff000000;
        		}
//        		argb = (r << 16) | (g << 8) | (b);

//				argb = 0x00ffffff - argb;				
//				argb |= 0xff000000;
				a_out.setRGB(x, y, argb);
				if(argb == 0xff00ff00) break;
			}
		}
		return a_out;
	}
	
	int[] analyzeImagetoArray(BufferedImage a_base, BufferedImage a_in, int a_start, int a_end)
	{
		int[] a_out = new int[a_in.getHeight()];
		int x, y;

		for(y = a_start; y < a_end; ++y)
		{
			for(x = a_in.getWidth()-1; x>=0; --x)
			{
				int argb = a_in.getRGB(x, y);
				int r = (argb & 0xff0000) >> 16;
            	int g = (argb & 0x00ff00) >> 8;
        		int b = (argb & 0x0000ff);
        		
				int argb_base = a_base.getRGB(x, y);
				int r_base = (argb_base & 0xff0000) >> 16;
            	int g_base = (argb_base & 0x00ff00) >> 8;
        		int b_base = (argb_base & 0x0000ff);
        		
        		r = Math.abs(r_base-r);
        		g = Math.abs(g_base-g);
        		b = Math.abs(b_base-b);

        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) break;
        		} else
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) break;
        		}
			}
			a_out[y] = x;
		}
		return a_out;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Closing");
		m_stop_detection = true;
		m_pco.Close();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		m_pco.Close();
		
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

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(sbPointsize))
		{
			m_pco.m_Pointsize = sbPointsize.getValue();
		} else
		if(e.getSource().equals(sbScaling))
		{
			m_pco.m_Scale = sbScaling.getValue();
		}
	}
	
	

}

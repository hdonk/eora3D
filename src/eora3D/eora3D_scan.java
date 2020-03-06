package eora3D;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.BevelBorder;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

class ExtensionFilter implements FilenameFilter
{
	  private String extension;
	  public ExtensionFilter( String extension ) {
	    this.extension = extension;             
	  }
	  
	  public boolean accept(File dir, String name) {
	    return (name.endsWith(extension));
	  }
}

public class eora3D_scan extends JDialog implements ActionListener, Runnable {
	Eora3D_MainWindow m_e3d = null;
	private JTextField tfScanStartAngle;
	private JTextField tfScanEndAngle;
	private JTextField tfScanStepSize;
	private JComboBox<String> cbTurntableStops;
	private PaintImage imagePanel;
	private JButton btnLaserTestStart;
	private JButton btnLaserTestEnd;
	
	private Thread m_scanning_thread = null;
	private JCheckBox chckbxPauseToTurn;
	
	boolean m_config_verify = false;
	
	boolean m_stop_camera = false;
	Thread m_camera_thread = null;
	
	public eora3D_scan(Eora3D_MainWindow a_e3d) {
		super();
		setSize(new Dimension(804, 690));
		setModal(true);
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblLaserStartPosition = new JLabel("Laser start position");
		lblLaserStartPosition.setBounds(6, 6, 133, 15);
		getContentPane().add(lblLaserStartPosition);
		
		tfScanStartAngle = new JTextField();
		tfScanStartAngle.setBounds(6, 33, 122, 27);
		getContentPane().add(tfScanStartAngle);
		tfScanStartAngle.setColumns(10);
		
		JLabel lblLaserEndPosition = new JLabel("Laser end position");
		lblLaserEndPosition.setBounds(6, 72, 144, 15);
		getContentPane().add(lblLaserEndPosition);
		
		tfScanEndAngle = new JTextField();
		tfScanEndAngle.setBounds(6, 99, 122, 27);
		getContentPane().add(tfScanEndAngle);
		tfScanEndAngle.setColumns(10);
		
		JLabel lblScanStepSize = new JLabel("Scan steps size");
		lblScanStepSize.setBounds(6, 138, 150, 15);
		getContentPane().add(lblScanStepSize);
		
		tfScanStepSize = new JTextField();
		tfScanStepSize.setBounds(6, 165, 122, 27);
		getContentPane().add(tfScanStepSize);
		tfScanStepSize.setColumns(10);
		
		btnLaserTestStart = new JButton("Laser test");
		btnLaserTestStart.setBounds(140, 33, 100, 27);
		getContentPane().add(btnLaserTestStart);
		btnLaserTestStart.addActionListener(this);
		
		btnLaserTestEnd = new JButton("Laser test");
		btnLaserTestEnd.setBounds(140, 99, 100, 27);
		getContentPane().add(btnLaserTestEnd);
		btnLaserTestEnd.addActionListener(this);
		
		imagePanel = new PaintImage(true);
		imagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		imagePanel.setBounds(250, 6, 358, 644);
		imagePanel.pos = 0;
		getContentPane().add(imagePanel);
		
		JButton btnStartScan = new JButton("Start scan");
		btnStartScan.setBounds(620, 198, 100, 27);
		getContentPane().add(btnStartScan);
		btnStartScan.addActionListener(this);
		
		JButton btnStartTurntableScan = new JButton("Start turntable scan");
		btnStartTurntableScan.setBounds(620, 372, 168, 27);
		getContentPane().add(btnStartTurntableScan);
		btnStartTurntableScan.addActionListener(this);
		
		JLabel lblTurntableStops = new JLabel("Turntable stops");
		lblTurntableStops.setBounds(16, 345, 194, 15);
		getContentPane().add(lblTurntableStops);
		
		cbTurntableStops = new JComboBox<String>();
		cbTurntableStops.setBounds(16, 372, 122, 27);
		cbTurntableStops.addItem("18");
		cbTurntableStops.addItem("12");
		cbTurntableStops.addItem("6");
		cbTurntableStops.addItem("4");
		cbTurntableStops.addItem("2");
		getContentPane().add(cbTurntableStops);
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(10, 420, 100, 27);
		getContentPane().add(btnConfig);
		btnConfig.addActionListener(this);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(620, 623, 100, 27);
		getContentPane().add(btnFinish);
		btnFinish.addActionListener(this);
		
/*		JButton btnSnapshot = new JButton("Snapshot");
		btnSnapshot.setBounds(261, 1003, 100, 27);
		getContentPane().add(btnSnapshot);
		btnSnapshot.addActionListener(this);
	*/	
		JLabel lblPauseToTurn = new JLabel("");
		lblPauseToTurn.setBounds(6, 203, 168, 15);
		getContentPane().add(lblPauseToTurn);
		
		chckbxPauseToTurn = new JCheckBox("Pause to turn lights off");
		chckbxPauseToTurn.setBounds(6, 200, 204, 23);
		getContentPane().add(chckbxPauseToTurn);
		chckbxPauseToTurn.addActionListener(this);

		imagePanel.m_centerline = true;
		
		setFromConfig();
		
		m_e3d = a_e3d;
		
		m_camera_thread = new Thread(this);
		m_camera_thread.start();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("AE: "+e+" from "+e.getActionCommand());
		putToConfig();
		if(e.getActionCommand()=="Start scan")
		{
			if(m_scanning_thread != null) return;
			m_stop_camera = true;
			try {
				m_camera_thread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			m_camera_thread = null;
			Runnable l_runnable = () -> {
				deleteFiles(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString(), ".png");
				captureScanChainColourMap();
				captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_step_size, "");
				m_camera_thread = new Thread(this);
				m_camera_thread.start();
			};
			m_scanning_thread = new Thread(l_runnable);
			m_scanning_thread.start();
		} 
		else
		if(e.getActionCommand()=="Start turntable scan")
		{
			if(m_scanning_thread != null) return;
			m_stop_camera = true;
			try {
				m_camera_thread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			m_camera_thread = null;
			deleteFiles(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString(), ".png"); 

				Runnable l_runnable = () -> {
					if(!captureScanChainColourMapTurntable())
					{
						JOptionPane.showMessageDialog(getContentPane(), "OK", "Failed to set capture turntable colour map", JOptionPane.ERROR_MESSAGE);
						return;
					}
					for(int i=0; i<Eora3D_MainWindow.m_e3d_config.sm_turntable_steps_per_rotation/Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size; ++i)
					{
						captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
								Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
								Eora3D_MainWindow.m_e3d_config.sm_scan_step_size, "_tt"+i);
						if(!m_e3d.m_e3D_bluetooth.setTurntableMotorPos(Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size))
						{
							JOptionPane.showMessageDialog(getContentPane(), "OK", "Failed to set turntable position", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					m_camera_thread = new Thread(this);
					m_camera_thread.start();

				};
				m_scanning_thread = new Thread(l_runnable);
				m_scanning_thread.start();
		} 
		else
		if(e.getActionCommand()=="Config")
		{
			eora3D_configuration_editor l_editor = new eora3D_configuration_editor(m_e3d);
			l_editor.setVisible(true);
			setFromConfig();
		}
		else
		if(e.getActionCommand()=="Snapshot")
		{
			BufferedImage l_image = m_e3d.getImage();
			imagePanel.m_image = l_image;
			imagePanel.m_overlay = null;
			imagePanel.repaint();
		}
		else
		if(e.getActionCommand()=="Finish")
		{
			m_stop_camera = true;
			try {
				m_camera_thread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			m_camera_thread = null;
			setVisible(false);
		}
		else
		if(e.getSource().equals(btnLaserTestStart))
		{
			System.out.println("TestStart");
			Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
			
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle))
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			}
			else
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
				BufferedImage l_image = m_e3d.getImage();

				imagePanel.m_image = l_image;
				imagePanel.repaint();

			}
		}
		else
		if(e.getSource().equals(btnLaserTestEnd))
		{
			System.out.println("TestEnd");
			
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle))
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			}
			else
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
				BufferedImage l_image = m_e3d.m_camera.getImage();

				imagePanel.m_image = l_image;
				imagePanel.repaint();

			}
		}
	}
	
  public static void deleteFiles( String directory, String extension )
  {
	    ExtensionFilter filter = new ExtensionFilter(extension);
	    File dir = new File(directory);

	    String[] list = dir.list(filter);
	    File file;
	    if (list==null || list.length == 0) return;

	    for (int i = 0; i < list.length; i++) {
	      //file = new File(directory + list[i]);
	      file = new File(directory, list[i]);
	      System.out.println(file + "  deleted : " + file.delete());
	    }
	   }

	void captureScanChainColourMap()
	{
		if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.isDirectory())
			if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.mkdir())
		{
			JOptionPane.showMessageDialog(this, "Failed", "Creating scanned image store", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		if(chckbxPauseToTurn.isSelected())
		{
			JOptionPane.showMessageDialog(getContentPane(), "Done", "Turn the lights ON", JOptionPane.INFORMATION_MESSAGE);
		}
		File l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_colourmap.png");
		BufferedImage l_image = m_e3d.getImage();

		try {
			ImageIO.write(l_image, "png", l_outfile);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		imagePanel.m_image = l_image;
		imagePanel.repaint();
		
		if(chckbxPauseToTurn.isSelected())
		{
			JOptionPane.showMessageDialog(getContentPane(), "Done", "Turn the lights OFF", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}

	boolean captureScanChainColourMapTurntable()
	{
		if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.isDirectory())
			if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.mkdir())
		{
			JOptionPane.showMessageDialog(this, "Failed", "Creating scanned image store", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		if(chckbxPauseToTurn.isSelected())
		{
			JOptionPane.showMessageDialog(getContentPane(), "Done", "Turn the lights ON", JOptionPane.INFORMATION_MESSAGE);
		}

		for(int i=0; i<Eora3D_MainWindow.m_e3d_config.sm_turntable_steps_per_rotation/Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size; ++i)
		{
			File l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_tt"+i+"_colourmap.png");
			BufferedImage l_image = m_e3d.getImage();

			try {
				ImageIO.write(l_image, "png", l_outfile);
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			imagePanel.m_image = l_image;
			imagePanel.repaint();
			if(!m_e3d.m_e3D_bluetooth.setTurntableMotorPos(Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size))
			{
				JOptionPane.showMessageDialog(getContentPane(), "OK", "Failed to set turntable position", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		if(chckbxPauseToTurn.isSelected())
		{
			JOptionPane.showMessageDialog(getContentPane(), "Done", "Turn the lights OFF", JOptionPane.INFORMATION_MESSAGE);
		}
		return true;
	}

	void captureScanChain(int a_start, int a_end, int a_stepsize, String a_file_insert)
	{
		if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.isDirectory())
			if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.mkdir())
		{
			JOptionPane.showMessageDialog(this, "Failed", "Creating scanned image store", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(a_start-1))
		{
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		File l_outfile;
		BufferedImage l_image;
		l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan"+a_file_insert+"_base.png");
		l_image = m_e3d.getImage();

		try {
			ImageIO.write(l_image, "png", l_outfile);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
		for(int l_pos = a_start; l_pos < a_end; l_pos += a_stepsize)
		{
			l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan"+a_file_insert+"_"+l_pos+".png");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos))
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
				return;
			}
			l_image = m_e3d.getImage();

			try {
				ImageIO.write(l_image, "png", l_outfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
				return;
			}
			imagePanel.m_image = l_image;
			imagePanel.repaint();
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		m_scanning_thread = null;
	}

	void setFromConfig()
	{
		tfScanStartAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle);
		tfScanEndAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle);
		tfScanStepSize.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
		cbTurntableStops.setSelectedItem(""+(6516/Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size));
	}
	
	void putToConfig()
	{
		if(m_config_verify) return;
		Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
		Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size = (6516/Integer.parseInt(cbTurntableStops.getSelectedItem().toString()));
		
		m_config_verify = true;
		Eora3D_MainWindow.m_e3d_config.verify();
		setFromConfig();
		m_config_verify = false;
	}
	@Override
	public void run() {
		while(!m_stop_camera)
		{
			BufferedImage l_image = m_e3d.getImage();
			if(l_image != null)
			{
				imagePanel.m_image = l_image;
				imagePanel.m_overlay = null;
				imagePanel.repaint();
			}
			else
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		m_stop_camera = false;
	}
	
}

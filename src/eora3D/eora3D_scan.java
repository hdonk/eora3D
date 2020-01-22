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

public class eora3D_scan extends JDialog implements ActionListener {
	Eora3D_MainWindow m_e3d = null;
	private JTextField tfScanStartAngle;
	private JTextField tfScanEndAngle;
	private JTextField tfScanStepSize;
	private JTextField tfTurntableStepSize;
	private PaintImage imagePanel;
	private JButton btnLaserTestStart;
	private JButton btnLaserTestEnd;
	
	private Thread m_scanning_thread = null;
	
	public eora3D_scan(Eora3D_MainWindow a_e3d) {
		super();
		setSize(new Dimension(1371, 1127));
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
		tfScanStartAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle);
		
		JLabel lblLaserEndPosition = new JLabel("Laser end position");
		lblLaserEndPosition.setBounds(6, 72, 144, 15);
		getContentPane().add(lblLaserEndPosition);
		
		tfScanEndAngle = new JTextField();
		tfScanEndAngle.setBounds(6, 99, 122, 27);
		getContentPane().add(tfScanEndAngle);
		tfScanEndAngle.setColumns(10);
		tfScanEndAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle);
		
		JLabel lblScanStepSize = new JLabel("Scan steps size");
		lblScanStepSize.setBounds(16, 138, 150, 15);
		getContentPane().add(lblScanStepSize);
		
		tfScanStepSize = new JTextField();
		tfScanStepSize.setBounds(6, 165, 122, 27);
		getContentPane().add(tfScanStepSize);
		tfScanStepSize.setColumns(10);
		tfScanStepSize.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
		
		
		btnLaserTestStart = new JButton("Laser test");
		btnLaserTestStart.setBounds(140, 33, 100, 27);
		getContentPane().add(btnLaserTestStart);
		btnLaserTestStart.addActionListener(this);
		
		btnLaserTestEnd = new JButton("Laser test");
		btnLaserTestEnd.setBounds(140, 99, 100, 27);
		getContentPane().add(btnLaserTestEnd);
		btnLaserTestEnd.addActionListener(this);
		
		imagePanel = new PaintImage();
		imagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		imagePanel.setBounds(252, 33, 730, 940);
		imagePanel.pos = 0;
		getContentPane().add(imagePanel);
		
		JButton btnStartScan = new JButton("Start scan");
		btnStartScan.setBounds(1009, 45, 100, 27);
		getContentPane().add(btnStartScan);
		btnStartScan.addActionListener(this);
		
		JButton btnStartTurntableScan = new JButton("Start turntable scan");
		btnStartTurntableScan.setBounds(1009, 339, 168, 27);
		getContentPane().add(btnStartTurntableScan);
		btnStartTurntableScan.addActionListener(this);
		
		JLabel lblTurntableStepSize = new JLabel("Turntable step size (deg)");
		lblTurntableStepSize.setBounds(16, 345, 194, 15);
		getContentPane().add(lblTurntableStepSize);
		
		tfTurntableStepSize = new JTextField();
		tfTurntableStepSize.setBounds(16, 372, 122, 27);
		getContentPane().add(tfTurntableStepSize);
		tfTurntableStepSize.setColumns(10);
		tfTurntableStepSize.setText(""+Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size);
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(16, 1003, 100, 27);
		getContentPane().add(btnConfig);
		btnConfig.addActionListener(this);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(1233, 1003, 100, 27);
		getContentPane().add(btnFinish);
		btnFinish.addActionListener(this);
		
		JButton btnSnapshot = new JButton("Snapshot");
		btnSnapshot.setBounds(261, 1003, 100, 27);
		getContentPane().add(btnSnapshot);
		btnSnapshot.addActionListener(this);

		m_e3d = a_e3d;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("AE: "+e+" from "+e.getActionCommand());
		if(e.getActionCommand()=="Start scan")
		{
			if(m_scanning_thread != null) return;
			Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());

			Runnable l_runnable = () -> {
				captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
			};
			m_scanning_thread = new Thread(l_runnable);
			m_scanning_thread.start();
		} 
		else
		if(e.getActionCommand()=="Start turntable scan")
		{
			if(m_scanning_thread != null) return;
			Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
			Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size = Integer.parseInt(tfTurntableStepSize.getText());
			
			Runnable l_runnable = () -> {
				captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
						Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
			};
			m_scanning_thread = new Thread(l_runnable);
			m_scanning_thread.start();
		} 
		else
		if(e.getActionCommand()=="Config")
		{
			eora3D_configuration_editor l_editor = new eora3D_configuration_editor(m_e3d);
			l_editor.setVisible(true);
		}
		else
		if(e.getActionCommand()=="Snapshot")
		{
			BufferedImage l_image = m_e3d.m_camera.getImage();
			l_image.flush();
			
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}
			
			imagePanel.m_image = l_image;
			imagePanel.repaint();
		}
		else
		if(e.getActionCommand()=="Finish")
		{
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
				BufferedImage l_image = m_e3d.m_camera.getImage();
				l_image.flush();

				if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
				{
					l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
				}

				imagePanel.m_image = l_image;
				imagePanel.repaint();

			}
		}
		else
		if(e.getSource().equals(btnLaserTestEnd))
		{
			System.out.println("TestEnd");
			Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
			
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle))
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			}
			else
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
				BufferedImage l_image = m_e3d.m_camera.getImage();
				l_image.flush();

				if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
				{
					l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
				}

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
	    if (list.length == 0) return;

	    for (int i = 0; i < list.length; i++) {
	      //file = new File(directory + list[i]);
	      file = new File(directory, list[i]);
	      System.out.println(file + "  deleted : " + file.delete());
	    }
	   }

	void captureScanChain(int a_start, int a_end, int a_stepsize)
	{
		if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.isDirectory())
			if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.mkdir())
		{
			JOptionPane.showMessageDialog(this, "Failed", "Creating scanned imag store", JOptionPane.ERROR_MESSAGE);
			return;
		}
		deleteFiles(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString(), ".png"); 
		if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(a_start-1))
		{
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		File l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_base.png");
		BufferedImage l_image = m_e3d.m_camera.getImage();
		l_image.flush();
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
		{
			l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
		}

		try {
			ImageIO.write(l_image, "png", l_outfile);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
		for(int l_pos = a_start; l_pos < a_end; l_pos += a_stepsize)
		{
			l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_"+l_pos+".png");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos))
			{
				Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
				return;
			}
			l_image = m_e3d.m_camera.getImage();
			l_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}

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
}

package eora3D;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JComboBox;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import eora3D.eora3D_bluetooth;
import tinyb.BluetoothDevice;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JMenu;
import javax.swing.JTextField;

class extensionFileFilter extends javax.swing.filechooser.FileFilter {

	String m_extension;
	
	extensionFileFilter(String a_extension)
	{
		super();
		m_extension = a_extension;
	}
	
	@Override
	public boolean accept(File a_pathname) {
		if (a_pathname.isDirectory()) {
			return true;
		}
		if(a_pathname.toString().endsWith("."+m_extension))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "Just "+m_extension+" files";
	}
}
public class Eora3D_MainWindow extends JDialog implements ActionListener, WindowListener {
	static public eora3D_bluetooth m_e3D_bluetooth;
	private JLabel laser_selector;
	private BluetoothDevice m_laser = null;
	private JLabel turntable_selector;
	private BluetoothDevice turntable = null;
	private JComboBox<String> camera_selector;
	
	List<Webcam> m_webcams = null;
	public Webcam m_camera = null;
	static eora3D_configuration_data_v1 m_e3d_config;
	public CalibrationData m_cal_data = null;
	private JTextField tfIPcameraurl;
	private boolean m_use_IPCamera;
	
	public Eora3D_MainWindow()
	{
		m_e3d_config = new eora3D_configuration_data_v1();
		m_cal_data = new CalibrationData();
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblEoradLaserTurret = new JLabel("Laser");
		lblEoradLaserTurret.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEoradLaserTurret.setBounds(10, 11, 81, 30);
		getContentPane().add(lblEoradLaserTurret);
		
		laser_selector = new JLabel();
		laser_selector.setText("Not found");
		laser_selector.setBounds(94, 12, 181, 30);
		getContentPane().add(laser_selector);
		
		JLabel lblEoradTurntable = new JLabel("Turntable");
		lblEoradTurntable.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEoradTurntable.setBounds(10, 53, 81, 30);
		getContentPane().add(lblEoradTurntable);
		
		turntable_selector = new JLabel();
		turntable_selector.setText("Not found");
		turntable_selector.setBounds(94, 54, 181, 30);
		getContentPane().add(turntable_selector);
		
		JLabel lblCamera = new JLabel("Camera");
		lblCamera.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCamera.setBounds(10, 95, 81, 30);
		getContentPane().add(lblCamera);
		
		camera_selector = new JComboBox<String>();
		camera_selector.setBounds(94, 96, 181, 30);
		getContentPane().add(camera_selector);
		camera_selector.addActionListener(this);
		
		JButton btnBluetoothRescan = new JButton("Detect");
		btnBluetoothRescan.setBounds(202, 39, 88, 23);
		getContentPane().add(btnBluetoothRescan);
		btnBluetoothRescan.addActionListener(this);
		
		JButton btnCameraRescan = new JButton("Relist");
		btnCameraRescan.setBounds(302, 100, 99, 23);
		getContentPane().add(btnCameraRescan);
		btnCameraRescan.addActionListener(this);
		
		JButton btndScanning = new JButton("Scan");
		btndScanning.setBounds(10, 222, 116, 23);
		getContentPane().add(btndScanning);
		btndScanning.addActionListener(this);
		
		JButton btnCalibration = new JButton("Calibrate");
		btnCalibration.setBounds(302, 38, 99, 25);
		getContentPane().add(btnCalibration);
		btnCalibration.addActionListener(this);
		
		JButton btnGenerateModel = new JButton("Generate model");
		btnGenerateModel.setBounds(10, 254, 181, 27);
		getContentPane().add(btnGenerateModel);
		btnGenerateModel.addActionListener(this);
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(10, 293, 116, 27);
		getContentPane().add(btnConfig);
		btnConfig.addActionListener(this);
		
		JButton btnTtt = new JButton("TTT");
		btnTtt.setBounds(301, 293, 100, 27);
		getContentPane().add(btnTtt);
		btnTtt.addActionListener(this);
		
		tfIPcameraurl = new JTextField();
		tfIPcameraurl.setBounds(4, 152, 394, 27);
		getContentPane().add(tfIPcameraurl);
		tfIPcameraurl.setText(m_e3d_config.sm_IP_webcam);
		tfIPcameraurl.setColumns(10);
		tfIPcameraurl.addActionListener(this);
		
		JLabel lblIpCameraUrl = new JLabel("IP Camera URL");
		lblIpCameraUrl.setBounds(10, 135, 99, 15);
		getContentPane().add(lblIpCameraUrl);
		
		
		setSize(412, 365);
		
		addWindowListener(this);
		
		setTitle("Eora3D Scanner");
		UIManager.put("OptionPane.minimumSize",new Dimension(400,100)); 

		setVisible(true);
		
		Bluetooth_Rescan();
		Camera_Rescan();
		
	}
	
	void Bluetooth_Rescan()
	{
		if(m_e3D_bluetooth == null) m_e3D_bluetooth = new eora3D_bluetooth();
		
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{
			m_e3D_bluetooth.discover();
		} catch(Exception e)
		{
			getContentPane().setCursor(Cursor.getDefaultCursor());
			laser_selector.setText("Not found");
			turntable_selector.setText("Not found");
			return;
		}
		getContentPane().setCursor(Cursor.getDefaultCursor());
		laser_selector.setText("Not found");
		turntable_selector.setText("Not found");
		if(m_e3D_bluetooth.devices!=null)
		{
			for (BluetoothDevice device : m_e3D_bluetooth.devices) {
				if(device.getName().substring(0, 4).equals("E3DS"))
				{
					laser_selector.setText(device.getName());
					m_laser = device;
					if(!m_e3D_bluetooth.setLaser(m_laser))
					{
						System.out.println("Not connected to laser successfully");
						laser_selector.setText("Not found");
						m_laser = null;
					}
					else
					{
						System.out.print("Found laser: ");
						eora3D_bluetooth.printDevice(m_laser);
					}
					break;
				}
			}
			for (BluetoothDevice device : m_e3D_bluetooth.devices) {
				System.out.println("Looking for turntable at "+device.getName());
				if(device.getName().substring(0, 4).equals("E3DT"))
				{
					turntable_selector.setText(device.getName());
					turntable = device;
					if(!m_e3D_bluetooth.setTurntable(turntable))
					{
						System.out.println("Not connected to turntable successfully");
						turntable_selector.setText("Not found");
						m_laser = null;
					}
					else
					{
						System.out.print("Found turntable: ");
						eora3D_bluetooth.printDevice(turntable);
					}
					break;
				}
			}
		}
	}
	
	void Camera_Rescan()
	{
		synchronized(this)
		{
			if(m_camera!=null)
			{
				m_camera.close();
				m_camera=null;
				System.gc();
			}
			camera_selector.removeActionListener(this);
			camera_selector.removeAllItems();
			m_webcams = Webcam.getWebcams();
			for(Webcam webcam : m_webcams)
			{
				camera_selector.addItem(webcam.getName());
			}
			camera_selector.addItem(new String("IPCamera"));
			camera_selector.addActionListener(this);
			camera_selector.setSelectedItem(0);
			if(m_webcams.size()==0)
			{
				m_camera = null;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("action :"+e);
		if(e.getActionCommand()=="Detect")
		{
			Bluetooth_Rescan();
			return;
		} else
		if(e.getActionCommand()=="Relist")
		{
			Camera_Rescan();
			return;
		} else
		if(e.getSource().equals(this.camera_selector))
		{
			synchronized(this)
			{
				if(m_camera!=null)
					m_camera.close();
				System.out.println("Selected camera "+camera_selector.getSelectedItem().toString());
				if(camera_selector.getSelectedItem().toString().equals("IPCamera"))
				{
					setupIPCamera();
				}
				else
				{
					m_use_IPCamera = false;
					Webcam.setDriver(new WebcamDefaultDriver());
					m_camera  = Webcam.getWebcamByName(camera_selector.getSelectedItem().toString());
					Dimension l_res[] = new Dimension[] {new Dimension(m_e3d_config.sm_camera_res_w, m_e3d_config.sm_camera_res_h)};
					m_camera.setCustomViewSizes(l_res);
					m_camera.setViewSize(new Dimension(m_e3d_config.sm_camera_res_w, m_e3d_config.sm_camera_res_h));
					try
					{
						m_camera.open();
						BufferedImage l_image = m_camera.getImage();
						m_e3d_config.sm_camera_res_w = l_image.getWidth();
						m_e3d_config.sm_camera_res_h = l_image.getHeight();
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
						m_camera = null;
					}
				}
			}
		} else
		if(e.getSource().equals(this.tfIPcameraurl))
		{
			setupIPCamera();
		} else
		if(e.getActionCommand()=="Calibrate")
		{
			System.out.println("Camera is "+m_camera);
			if(m_camera==null && !m_use_IPCamera)
			{
				JOptionPane.showMessageDialog(getContentPane(), "No camera", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(m_laser==null)
			{
				JOptionPane.showMessageDialog(getContentPane(), "No laser", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			new eora3D_calibration(this).setVisible(true);
		}
		else
		if(e.getActionCommand()=="Scan")
		{
			System.out.println("Camera is "+m_camera);
			if(m_camera==null && !m_use_IPCamera)
			{
				JOptionPane.showMessageDialog(getContentPane(), "No camera", "Camera needed", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(m_laser==null)
			{
				JOptionPane.showMessageDialog(getContentPane(), "No laser", "Laser needed", JOptionPane.ERROR_MESSAGE);
				return;
			}
			new eora3D_scan(this).setVisible(true);
		}
		else
		if(e.getActionCommand()=="Generate model")
		{
			new ModelGenerator(this).setVisible(true);
		}
		else
		if(e.getActionCommand()=="Config")
		{
			eora3D_configuration_editor l_editor = new eora3D_configuration_editor(this);
			l_editor.setVisible(true);
			tfIPcameraurl.setText(m_e3d_config.sm_IP_webcam);
		} else
/*		else
		if(e.getActionCommand()=="Load config")
		{
			JFileChooser l_fc;
			l_fc = new JFileChooser(System.getProperty("user.home"));
			l_fc.setFileFilter(new extensionFileFilter("e3d"));
			l_fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			l_fc.setSelectedFile(m_e3d_config.sm_config_file);
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
					m_e3d_config = (eora3D_configuration_data_v1) l_ois.readObject();
					l_ois.close();
					l_fis.close();
					m_e3d_config.sm_config_file = l_file;
					System.out.println("Serialized data loaded from " + l_file);
				} catch (Exception ioe) {
					ioe.printStackTrace();
					System.err.println("Failed to open " + l_file);
				}
				
			} else {
			}

		}
		else
		if(e.getActionCommand()=="Save config")
		{
			JFileChooser l_fc;
			l_fc = new JFileChooser(m_e3d_config.sm_config_file);
			l_fc.setFileFilter(new extensionFileFilter("e3d"));
			l_fc.setSelectedFile(m_e3d_config.sm_config_file);
			int l_ret = l_fc.showSaveDialog(this);

			if (l_ret == JFileChooser.APPROVE_OPTION) {
				File l_file = l_fc.getSelectedFile();
				if(!l_file.toString().contains("."))
				{
					l_file = new File(l_file.toString()+".e3d");
				}
				m_e3d_config.sm_config_file = l_file;
				try {
					FileOutputStream l_fos = new FileOutputStream(l_file);
					ObjectOutputStream l_oos = new ObjectOutputStream(l_fos);
					l_oos.writeObject(new eora3D_configuration_version());
					l_oos.writeObject(m_e3d_config);
					l_oos.close();
					l_fos.close();
					JOptionPane.showMessageDialog(getContentPane(), "Ok", "Save", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Serialized data is saved in " + l_file);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					JOptionPane.showMessageDialog(getContentPane(), "Failed", "Save", JOptionPane.ERROR_MESSAGE);
				}
			}
		}*/
		if(e.getActionCommand()=="TTT")
		{
			new eora3D_turntable_controller(m_e3D_bluetooth).setVisible(true);
		}
	}

	void setupIPCamera()
	{
		m_use_IPCamera = true;
		m_e3d_config.sm_IP_webcam = tfIPcameraurl.getText();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Window closing");
		if(m_e3D_bluetooth!=null) m_e3D_bluetooth.cleanup();
		System.out.println("Window closing done");
	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.out.println("Window closed");
		System.exit(0);
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
	
	public BufferedImage getImage()
	{
		BufferedImage l_image = null;
		if(m_use_IPCamera)
		{
			try
			{
				URL url = new URL(m_e3d_config.sm_IP_webcam);
				l_image = ImageIO.read(url);
				if(l_image != null && Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
				{
					l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
				}
				l_image.flush();
			} catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Failed to read from "+m_e3d_config.sm_IP_webcam);
				return null;
			}
			return l_image;
		}
		else
		{
			if(m_camera == null) return null;
			l_image = m_camera.getImage();
			if(l_image != null && Eora3D_MainWindow.m_e3d_config.sm_camera_rotation!=0)
			{
				l_image = eora3D_calibration.rotate(l_image, Eora3D_MainWindow.m_e3d_config.sm_camera_rotation);
			}
			l_image.flush();
			return l_image;
		}
	}
}

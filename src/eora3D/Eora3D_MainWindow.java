package eora3D;

import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JButton;
import eora3D.eora3D_bluetooth;
import tinyb.BluetoothDevice;

public class Eora3D_MainWindow extends JDialog implements ActionListener {
	static eora3D_bluetooth m_e3D_bluetooth;
	private JComboBox<String> laser_selector;
	private JComboBox<String> turntable_selector;
	private JComboBox<String> camera_selector;
	
	public Eora3D_MainWindow()
	{
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblEoradLaserTurret = new JLabel("Eora3D Laser Turret");
		lblEoradLaserTurret.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEoradLaserTurret.setBounds(10, 11, 170, 30);
		getContentPane().add(lblEoradLaserTurret);
		
		laser_selector = new JComboBox();
		laser_selector.setBounds(10, 52, 423, 30);
		getContentPane().add(laser_selector);
		
		JLabel lblEoradTurntable = new JLabel("Eora3D Turntable");
		lblEoradTurntable.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEoradTurntable.setBounds(10, 93, 249, 30);
		getContentPane().add(lblEoradTurntable);
		
		turntable_selector = new JComboBox();
		turntable_selector.setBounds(10, 134, 423, 30);
		getContentPane().add(turntable_selector);
		
		JLabel lblCamera = new JLabel("Camera");
		lblCamera.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCamera.setBounds(10, 175, 249, 30);
		getContentPane().add(lblCamera);
		
		camera_selector = new JComboBox();
		camera_selector.setBounds(10, 216, 423, 30);
		getContentPane().add(camera_selector);
		
		JButton btnBluetoothRescan = new JButton("Bluetooth Rescan");
		btnBluetoothRescan.setBounds(10, 257, 127, 23);
		getContentPane().add(btnBluetoothRescan);
		btnBluetoothRescan.addActionListener(this);
		
		JButton btnCameraRescan = new JButton("Camera Rescan");
		btnCameraRescan.setBounds(147, 257, 116, 23);
		getContentPane().add(btnCameraRescan);
		btnCameraRescan.addActionListener(this);
		
		JButton btndScanning = new JButton("3D Scanning");
		btndScanning.setBounds(273, 257, 116, 23);
		getContentPane().add(btndScanning);
		btndScanning.addActionListener(this);
		
		setSize(449+16, 320+64);
		
		setVisible(true);
		
		Bluetooth_Rescan();
	}
	
	void Bluetooth_Rescan()
	{
		if(m_e3D_bluetooth == null) m_e3D_bluetooth = new eora3D_bluetooth();
		
		laser_selector.removeAllItems();
		turntable_selector.removeAllItems();
		
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		m_e3D_bluetooth.discover();
		getContentPane().setCursor(Cursor.getDefaultCursor());
		if(m_e3D_bluetooth.devices!=null)
		{
			boolean found = false;
			for (BluetoothDevice device : m_e3D_bluetooth.devices) {
				System.out.println(device.getName().substring(0, 4));
				if(device.getName().substring(0, 4).equals("E3DS"))
				{
					laser_selector.addItem(device.getName());
					found = true;
				}
			}
			if(found) laser_selector.setSelectedIndex(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Bluetooth Rescan")
		{
			Bluetooth_Rescan();
			return;
		}
		
	}
}

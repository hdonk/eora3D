package eora3D;

import javax.swing.JDialog;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class eora3D_laser_controller extends JDialog implements ActionListener {
	eora3D_bluetooth m_eora3D_bluetooth = null;
	private JTextField motorPos;
	private JTextField ledRed;
	private JTextField ledGreen;
	private JTextField ledBlue;
	private JTextField motorSpeed;
	
	eora3D_laser_controller(eora3D_bluetooth a_eora3D_bluetooth)
	{
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 10, 10);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnLaserOn = new JButton("Laser On");
		btnLaserOn.setBounds(0, 0, 117, 25);
		getContentPane().add(btnLaserOn);
		btnLaserOn.addActionListener(this);
		
		JButton btnLaserOff = new JButton("Laser Off");
		btnLaserOff.setBounds(140, 0, 117, 25);
		getContentPane().add(btnLaserOff);
		btnLaserOff.addActionListener(this);
		
		JLabel lblMotorPosition = new JLabel("Motor position:");
		lblMotorPosition.setBounds(0, 37, 117, 15);
		getContentPane().add(lblMotorPosition);
		
		motorPos = new JTextField();
		motorPos.setName("MotorPos");
		motorPos.setText("0");
		motorPos.setBounds(150, 35, 114, 19);
		getContentPane().add(motorPos);
		motorPos.setColumns(10);
		motorPos.addActionListener(this);
		
		JLabel lblLedColour = new JLabel("LED colour");
		lblLedColour.setBounds(0, 64, 107, 15);
		getContentPane().add(lblLedColour);
		
		ledRed = new JTextField();
		ledRed.setText(Integer.toString(0xf6));
		ledRed.setBounds(160, 62, 32, 19);
		getContentPane().add(ledRed);
		ledRed.setColumns(10);
		ledRed.addActionListener(this);
		
		JLabel lblR = new JLabel("R");
		lblR.setBounds(140, 66, 16, 15);
		getContentPane().add(lblR);
		
		JLabel lblG = new JLabel("G");
		lblG.setBounds(168, 66, 22, 15);
		getContentPane().add(lblG);
		
		JLabel lblG_1 = new JLabel("G");
		lblG_1.setBounds(202, 66, 24, 15);
		getContentPane().add(lblG_1);
		
		ledGreen = new JTextField();
		ledGreen.setText(Integer.toString(0x48));
		ledGreen.setBounds(219, 62, 38, 19);
		getContentPane().add(ledGreen);
		ledGreen.setColumns(10);
		ledGreen.addActionListener(this);
		
		JLabel lblB = new JLabel("B");
		lblB.setBounds(268, 64, 16, 15);
		getContentPane().add(lblB);
		
		ledBlue = new JTextField();
		ledBlue.setText(Integer.toString(0x47));
		ledBlue.setBounds(287, 62, 38, 19);
		getContentPane().add(ledBlue);
		ledBlue.setColumns(10);
		
		JLabel lblSpeed = new JLabel("Speed:");
		lblSpeed.setBounds(270, 37, 55, 15);
		getContentPane().add(lblSpeed);
		
		motorSpeed = new JTextField();
		motorSpeed.setText("0");
		motorSpeed.setBounds(346, 35, 55, 19);
		getContentPane().add(motorSpeed);
		motorSpeed.setColumns(10);
		motorSpeed.addActionListener(this);
		
		m_eora3D_bluetooth = a_eora3D_bluetooth;
		
		setSize(449+16, 320+64);
		setModal(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("cmd: "+e.getActionCommand());
		if(e.getActionCommand()=="Laser On")
		{
			m_eora3D_bluetooth.setLaserStatus(true);
		} else
		if(e.getActionCommand()=="Laser Off")
		{
			m_eora3D_bluetooth.setLaserStatus(false);
		} else
		if(e.getSource()==this.ledRed || e.getSource()==this.ledGreen || e.getSource()==this.ledBlue)
		{
			int l_red = Integer.parseInt(ledRed.getText());
			if(l_red<0 || l_red>255) l_red = 0;
			ledRed.setText(Integer.toString(l_red));
			int l_green = Integer.parseInt(ledGreen.getText());
			if(l_green<0 || l_green>255) l_green = 0;
			ledGreen.setText(Integer.toString(l_green));
			int l_blue = Integer.parseInt(ledBlue.getText());
			if(l_blue<0 || l_blue>255) l_blue = 0;
			ledBlue.setText(Integer.toString(l_blue));
			m_eora3D_bluetooth.setLEDColour(l_red,  l_green, 
					l_blue );
		} else
		if(e.getSource()==motorPos)
		{
			int l_pos = Integer.parseInt(motorPos.getText());
			if(l_pos<0) l_pos = 0;
			if(l_pos>9500) l_pos = 9500;
			motorPos.setText(Integer.toString(l_pos));
			m_eora3D_bluetooth.setMotorPos(l_pos);
			System.out.println("Motor move complete");
		} else
		if(e.getSource()==motorSpeed)
		{
			int l_speed = Integer.parseInt(motorSpeed.getText());
			if(l_speed<0) l_speed = 0;
			if(l_speed>255) l_speed = 255;
			motorSpeed.setText(Integer.toString(l_speed));
			m_eora3D_bluetooth.setMotorSpeed(l_speed);
		}
		
	}
}

package eora3D;

import javax.swing.JDialog;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class eora3D_turntable_controller extends JDialog implements ActionListener {
	eora3D_bluetooth m_eora3D_bluetooth = null;
	private JTextField motorPos;
	private JTextField motorSpeed;
	private JTextField motorAccel;
	
	eora3D_turntable_controller(eora3D_bluetooth a_eora3D_bluetooth)
	{
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 10, 10);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblMotorPosition = new JLabel("Motor position:");
		lblMotorPosition.setBounds(15, 37, 117, 15);
		getContentPane().add(lblMotorPosition);
		
		motorPos = new JTextField();
		motorPos.setName("MotorPos");
		motorPos.setText("0");
		motorPos.setBounds(150, 35, 114, 19);
		getContentPane().add(motorPos);
		motorPos.setColumns(10);
		motorPos.addActionListener(this);
		
		JLabel lblSpeed = new JLabel("Speed:");
		lblSpeed.setBounds(73, 64, 55, 15);
		getContentPane().add(lblSpeed);
		
		motorSpeed = new JTextField();
		motorSpeed.setText("0");
		motorSpeed.setBounds(150, 66, 114, 19);
		getContentPane().add(motorSpeed);
		motorSpeed.setColumns(10);
		motorSpeed.addActionListener(this);
		
		JLabel lblAcceleration = new JLabel("Acceleration:");
		lblAcceleration.setBounds(27, 99, 93, 15);
		getContentPane().add(lblAcceleration);
		
		motorAccel = new JTextField();
		motorAccel.setText("0");
		motorAccel.setBounds(150, 97, 114, 19);
		getContentPane().add(motorAccel);
		motorAccel.setColumns(10);
		motorAccel.addActionListener(this);
		
		m_eora3D_bluetooth = a_eora3D_bluetooth;
		
		setSize(325, 201);
		setModal(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("cmd: "+e.getActionCommand());
		if(e.getSource()==motorPos)
		{
			System.out.println("Turntable motor pos");
			int l_pos = Integer.parseInt(motorPos.getText());
			if(l_pos<0) l_pos = 0;
			if(l_pos>0x2300) l_pos = 0x2300;
			motorPos.setText(Integer.toString(l_pos));
			m_eora3D_bluetooth.setTurntableMotorPos(l_pos);
		} else
		if(e.getSource()==motorSpeed)
		{
			System.out.println("Turntable motor speed");
			int l_speed = Integer.parseInt(motorSpeed.getText());
			if(l_speed<0) l_speed = 0;
			if(l_speed>255) l_speed = 255;
			motorSpeed.setText(Integer.toString(l_speed));
			m_eora3D_bluetooth.setTurntableMotorSpeed(l_speed);
		} else
		if(e.getSource()==motorAccel)
		{
			System.out.println("Turntable motor accel");
			int l_accel = Integer.parseInt(motorAccel.getText());
			if(l_accel<0) l_accel = 0;
			if(l_accel>255) l_accel = 255;
			motorAccel.setText(Integer.toString(l_accel));
			m_eora3D_bluetooth.setTurntableMotorAccel(l_accel);
		}
		
	}
}

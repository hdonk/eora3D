package eora3D;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class eora3d_scan extends JPanel implements ActionListener {
	Eora3D_MainWindow m_e3d = null;
	
	public eora3d_scan(Eora3D_MainWindow a_e3d) {
		super();
		m_e3d = a_e3d;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Scan")
		{
//			Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
//			Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
//			Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
			captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
					Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
					Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
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
			// TODO Auto-generated catch block
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
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
	}


}

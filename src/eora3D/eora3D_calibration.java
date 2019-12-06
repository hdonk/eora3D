package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;

class PaintImage extends JPanel
{
  public BufferedImage m_image = null;
 
  public PaintImage ()
  {
    super();
  }

  public void paintComponent(Graphics g)
  {
	  if(m_image == null) return;
    g.drawImage(m_image, 0, 0, null);
    repaint();
  }

}

public class eora3D_calibration extends JDialog implements ActionListener {
	private Webcam m_camera;
	private PaintImage image;

	eora3D_calibration(Webcam a_camera)
	{
		getContentPane().setLayout(null);
		
		JButton btnCapture = new JButton("Capture");
		btnCapture.setBounds(12, 779, 91, 25);
		getContentPane().add(btnCapture);
		btnCapture.addActionListener(this);
		
		image = new PaintImage();
		image.setBounds(12, 12, 1042, 732);
		getContentPane().add(image);
		m_camera = a_camera;
		
		setModal(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand()=="Capture")
		{
			BufferedImage l_image = m_camera.getImage();
			l_image.flush();
			image.m_image = l_image;
			image.invalidate();
		}
	}
}

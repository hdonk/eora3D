package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


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
		
		setSize(1300,900);
		setModal(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Capture")
		{
			BufferedImage l_image = m_camera.getImage();
			l_image.flush();
			
			Mat input = new Mat(l_image.getHeight(), l_image.getWidth(), CvType.CV_8UC3);
			byte data[] = ((DataBufferByte)l_image.getRaster().getDataBuffer()).getData();
			input.put(0,  0,  data);
			Mat grey = new Mat();
			Imgproc.cvtColor(input,  grey,  Imgproc.COLOR_BGRA2GRAY);
			Mat circles = new Mat();
			Imgproc.HoughCircles(grey, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90, 0, 1000);
			
			System.out.println(String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()) );
			
			image.m_image = l_image;
			image.repaint();
		}
	}
}

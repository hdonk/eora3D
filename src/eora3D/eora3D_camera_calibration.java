package eora3D;

import javax.swing.JDialog;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class eora3D_camera_calibration extends JDialog implements ActionListener, Runnable, WindowListener {
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	
	private PaintImage m_image;
	private JComboBox calibrationImageSelection;
	
	Eora3D_MainWindow m_e3d;
	
	boolean m_stop_camera = false;
	
	MatOfPoint2f m_last_detected_points = null;
	ArrayList<Mat> m_detected_points = null;
	Mat m_saved_image;
	
	static final int m_pointset_count = 5; 
	
	public eora3D_camera_calibration(Eora3D_MainWindow a_e3d) {
		super();
		setSize(new Dimension(901, 700));
		setResizable(false);
		m_e3d = a_e3d;
		getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 11, 86, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(123, 11, 531, 638);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		panel.add(m_image = new PaintImage(true), BorderLayout.CENTER);
		m_image.pos=0;
		
		calibrationImageSelection = new JComboBox();
		calibrationImageSelection.setBounds(10, 42, 86, 22);
		m_detected_points = new ArrayList<Mat>();
		
		for(int i=1; i<=m_pointset_count; ++i)
		{
			calibrationImageSelection.addItem(""+i);
			m_detected_points.add(null);
		}
		calibrationImageSelection.setSelectedIndex(0);
		getContentPane().add(calibrationImageSelection);
		
		JButton btnNewButton = new JButton("Snapshot");
		btnNewButton.setBounds(7, 75, 89, 23);
		getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);
		
		JButton btnNewButton_1 = new JButton("Calibrate");
		btnNewButton_1.setBounds(7, 109, 89, 23);
		getContentPane().add(btnNewButton_1);
		btnNewButton_1.addActionListener(this);
		
		textField_1 = new JTextField();
		textField_1.setBounds(7, 165, 86, 20);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(10, 151, 46, 14);
		getContentPane().add(lblNewLabel);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(7, 202, 86, 20);
		getContentPane().add(textField_2);
		
		JLabel label = new JLabel("New label");
		label.setBounds(10, 188, 46, 14);
		getContentPane().add(label);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(7, 244, 86, 20);
		getContentPane().add(textField_3);
		
		JLabel label_1 = new JLabel("New label");
		label_1.setBounds(10, 230, 46, 14);
		getContentPane().add(label_1);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(7, 285, 86, 20);
		getContentPane().add(textField_4);
		
		JLabel label_2 = new JLabel("New label");
		label_2.setBounds(10, 271, 46, 14);
		getContentPane().add(label_2);
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(7, 330, 86, 20);
		getContentPane().add(textField_5);
		
		JLabel label_3 = new JLabel("New label");
		label_3.setBounds(10, 316, 46, 14);
		getContentPane().add(label_3);
		
		btnNewButton.addActionListener(this);
		
		this.addWindowListener(this);
		
		new Thread(this).start();
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand() == "Snapshot")
		{
			m_detected_points.set(calibrationImageSelection.getSelectedIndex(), m_last_detected_points);
			System.out.println("Captured snapshot at "+calibrationImageSelection.getSelectedIndex());
		} else
		if(ae.getActionCommand() == "Calibrate")
		{
			for(int i=0; i<m_pointset_count; ++i)
			{
				if(m_detected_points.get(i) == null)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Pointset at "+(i+1)+" not set", "Missing points", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			MatOfPoint3f obj = new MatOfPoint3f();
			for(int y=0; y<20; ++y)
			{
				for(int x=0; x<14; ++x)
				{
					obj.push_back(new MatOfPoint3f(new Point3(y*(191.0f/19.0f), x*(130.0f/13.0f), 0.0f)));
				}
			}
			
			List<Mat> objectPoints = new ArrayList<>();
			
			
			
			for(int i=0; i<5; ++i) objectPoints.add(obj);

			List<Mat> l_rvecs = null;
			List<Mat> l_tvecs = null;

			l_rvecs = new ArrayList<>();
			l_tvecs = new ArrayList<>();
			m_e3d.m_e3d_config.m_camera_calibration_intrinsic = new Mat();
			m_e3d.m_e3d_config.m_camera_calibration_intrinsic.put(0, 0, 1);
			m_e3d.m_e3d_config.m_camera_calibration_intrinsic.put(1, 1, 1);
			m_e3d.m_e3d_config.m_camera_calibration_distCoeffs = new Mat();
			// calibrate!
			Calib3d.calibrateCamera(objectPoints, m_detected_points, m_saved_image.size(), m_e3d.m_e3d_config.m_camera_calibration_intrinsic,
					m_e3d.m_e3d_config.m_camera_calibration_distCoeffs, l_rvecs, l_tvecs);
			m_e3d.m_e3d_config.m_camera_calibration = true;
			double[] l_intrinsic_double = new double[3*3];
			m_e3d.m_e3d_config.m_camera_calibration_intrinsic.get(0, 0, l_intrinsic_double);
			
			System.out.println("Calibration Array");
			for(int i=0; i<3; ++i)
			{
				for(int j=0; j<3; ++j)
				{
					System.out.print(l_intrinsic_double[i*3+j]+" ");
				}
				System.out.println("");
			}
						
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi, int ct)
	{
		BufferedImage l_inter_bi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		l_inter_bi.getGraphics().drawImage(bi, 0, 0, null);
		
		Mat l_mat1 = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
		byte[] data = ((DataBufferByte) l_inter_bi.getRaster().getDataBuffer()).getData();
		l_mat1.put(0, 0, data);
		
		return l_mat1;
	}
	
	public static BufferedImage matToBufferedImage(Mat m)
	{
		BufferedImage l_rgb = new BufferedImage(m.width(), m.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		byte[] l_dst_data = ((DataBufferByte) l_rgb.getRaster().getDataBuffer()).getData();
		byte[] l_src_data = new byte[m.width() * m.height()*3];
		m.get(0, 0, l_src_data);
		for(int i=0; i<m.width() * m.height(); ++i)
		{
			l_dst_data[(i*4)+1] = l_src_data[(i*3)+0];
			l_dst_data[(i*4)+2] = l_src_data[(i*3)+1];
			l_dst_data[(i*4)+3] = l_src_data[(i*3)+2];
			if(l_dst_data[(i*4)+1]!=0x00 ||
					l_dst_data[(i*4)+2]!=0x00 ||
					l_dst_data[(i*4)+3]!=0x0 )
			l_dst_data[(i*4)+0] = (byte)0xff;
		}
		
		
		return l_rgb;
	}
	
	@Override
	public void run() {
		while(!m_stop_camera)
		{
			BufferedImage l_image = m_e3d.getImage();
			if(l_image != null)
			{
				m_image.m_image = l_image;

				// OpenCV image analysis
				Mat greyImage = bufferedImageToMat(l_image, CvType.CV_8UC1);
				
				Size patternSize = new Size(14, 20);
//				Size patternSize = new Size(6, 9);
				MatOfPoint2f centers  = new MatOfPoint2f();
				boolean l_detected = Calib3d.findCirclesGrid(greyImage, patternSize, centers, Calib3d.CALIB_CB_SYMMETRIC_GRID);
//				boolean l_detected = Calib3d.findChessboardCorners(greyImage, patternSize, centers);
				
				if(l_detected)
				{
					// optimization
//					TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
//					Imgproc.cornerSubPix(greyImage, centers, new Size(11, 11), new Size(-1, -1), term);
					
					m_last_detected_points = centers;
					
					Mat circles = new Mat(l_image.getHeight(), l_image.getWidth(), CvType.CV_8UC3);
					Calib3d.drawChessboardCorners(circles, patternSize, centers, l_detected);
					
					m_image.m_overlay = matToBufferedImage(circles);
					m_saved_image = greyImage;
				}
				else
				{
					System.out.println("No grid detected");
					m_image.m_overlay = null;
				}
				
				m_image.repaint();
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

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		m_stop_camera = true;
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

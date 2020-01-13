package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
/*import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;*/
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengles.GLESCapabilities;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;

import org.lwjgl.opengles.*;
import org.lwjgl.opengles.GLES.*;
//import org.lwjgl.opengles.GLES32.*;
import org.lwjgl.opengles.OESMapbuffer.*;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;

import org.lwjgl.system.*;
import org.lwjgl.egl.*;
import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.glfw.*;
//import org.lwjgl.opengl.GLUtil.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWNativeEGL.*;
import static org.lwjgl.egl.EGL10.*;
import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.JScrollBar;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

class PaintImage extends JPanel
{
  public BufferedImage m_image = null;
  public BufferedImage m_overlay = null;
  //public Mat circles;
  public CalibrationData m_cal_data = null;
 
  public int pos = 1;
  
  public PaintImage ()
  {
    super();
  }

  public void paintComponent(Graphics g)
  {
	  if(m_image == null) return;
	g.clearRect(0,  0,  getWidth(), getHeight());
    g.drawImage(m_image, 0, 0, null);
    if(m_overlay != null) g.drawImage(m_overlay, 0, 0, null);
    
    g.setColor(Color.blue);
    if(pos==1)
    {
	    g.drawRect(
	    		m_cal_data.pos_1_board.x,
	    		m_cal_data.pos_1_board.y,
	    		m_cal_data.pos_1_board.width,
	    		m_cal_data.pos_1_board.height
	    		);
	    g.drawRect(
	    		0,
	    		0,
	    		m_cal_data.capture_w,
	    		m_cal_data.capture_h
	    		);
	    g.drawOval(
	    		m_cal_data.pos_1_tl.x,
	    		m_cal_data.pos_1_tl.y,
	    		m_cal_data.pos_1_tl.width,
	    		m_cal_data.pos_1_tl.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_1_tl.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_tl.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_tl.width+m_cal_data.detection_box,
	    		m_cal_data.pos_1_tl.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_1_tr.x,
	    		m_cal_data.pos_1_tr.y,
	    		m_cal_data.pos_1_tr.width,
	    		m_cal_data.pos_1_tr.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_1_tr.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_tr.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_tr.width+m_cal_data.detection_box,
	    		m_cal_data.pos_1_tr.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_1_bl.x,
	    		m_cal_data.pos_1_bl.y,
	    		m_cal_data.pos_1_bl.width,
	    		m_cal_data.pos_1_bl.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_1_bl.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_bl.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_bl.width+m_cal_data.detection_box,
	    		m_cal_data.pos_1_bl.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_1_br.x,
	    		m_cal_data.pos_1_br.y,
	    		m_cal_data.pos_1_br.width,
	    		m_cal_data.pos_1_br.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_1_br.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_br.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_1_br.width+m_cal_data.detection_box,
	    		m_cal_data.pos_1_br.height+m_cal_data.detection_box
	    		);
    }
    else
    {
	    g.drawRect(
	    		m_cal_data.pos_2_board.x,
	    		m_cal_data.pos_2_board.y,
	    		m_cal_data.pos_2_board.width,
	    		m_cal_data.pos_2_board.height
	    		);
	    g.drawRect(
	    		0,
	    		0,
	    		m_cal_data.capture_w,
	    		m_cal_data.capture_h
	    		);
	    g.drawOval(
	    		m_cal_data.pos_2_tl.x,
	    		m_cal_data.pos_2_tl.y,
	    		m_cal_data.pos_2_tl.width,
	    		m_cal_data.pos_2_tl.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_2_tl.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_tl.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_tl.width+m_cal_data.detection_box,
	    		m_cal_data.pos_2_tl.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_2_tr.x,
	    		m_cal_data.pos_2_tr.y,
	    		m_cal_data.pos_2_tr.width,
	    		m_cal_data.pos_2_tr.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_2_tr.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_tr.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_tr.width+m_cal_data.detection_box,
	    		m_cal_data.pos_2_tr.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_2_bl.x,
	    		m_cal_data.pos_2_bl.y,
	    		m_cal_data.pos_2_bl.width,
	    		m_cal_data.pos_2_bl.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_2_bl.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_bl.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_bl.width+m_cal_data.detection_box,
	    		m_cal_data.pos_2_bl.height+m_cal_data.detection_box
	    		);
	    g.drawOval(
	    		m_cal_data.pos_2_br.x,
	    		m_cal_data.pos_2_br.y,
	    		m_cal_data.pos_2_br.width,
	    		m_cal_data.pos_2_br.height
	    		);
	    g.drawRect(
	    		m_cal_data.pos_2_br.x-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_br.y-m_cal_data.detection_box/2,
	    		m_cal_data.pos_2_br.width+m_cal_data.detection_box,
	    		m_cal_data.pos_2_br.height+m_cal_data.detection_box
	    		);
    }
    repaint();
  }
}

public class eora3D_calibration extends JDialog implements ActionListener, AdjustmentListener, WindowListener, Runnable {
	private Webcam m_camera;
	private PaintImage image;
	private JTextField txtThreshold;
	private JTextField txtMinHits;
	private JTextField txtMinRad;
	private JTextField txtMaxRad;
	private BufferedImage capturedImage = null;
	private CalibrationData m_cal_data;
	private JTextField txtCalibImg;
	private JScrollBar sbHeightOffset;
	private JTextField txtRedthreshold;
	private JTextField txtGreenthreshold;
	private JTextField txtBluethreshold;
	private JComboBox<String> cbThresholdLogic;
	private Thread m_calibration_thread = null;
	private boolean m_stop_calibration_thread;
	private JTextField txtTopleftmotorpos;
	private JTextField txtToprightmotorpos;
	private JLabel lblTopRightMotor;
	private JLabel lblTopLeftMotor;
	private JLabel lblTopLeftMotor_1;
	private JLabel lblTopRightMotor_1;
	private JTextField txtTopleftmotorpos_2;
	private JTextField txtToprightmotorpos_2;
	private JLabel lblCalibrationPosition;
	private JRadioButton radioButton;
	private JRadioButton radioButton_1;

	EGLCapabilities m_egl;
	GLESCapabilities m_gles;
	private long m_window;
	private int m_main_program;
	private int m_point_program;
	private int m_point_program_col;
	private int displayW;
	private int displayH;
//	PointCloudData m_pcd = null;
	PointCloudObject m_pco = null;
	float m_rot = 0.0f;
	private Thread m_thread = null;
	
	long lastTime = 0;
	private Thread m_detect_thread = null;
	private JLabel lblPointSize;
	private JLabel lblScale;
	private JScrollBar sbPointSize;
	private JScrollBar sbScale;
	private JTextField tfScanStartAngle;
	private JTextField tfScanEndAngle;
	private JTextField tfScanStepSize;

	eora3D_calibration(Webcam a_camera)
	{
		super();
		this.addWindowListener(this);
		getContentPane().setLayout(null);
		
		JButton btnCapture = new JButton("Capture");
		btnCapture.setBounds(12, 779, 91, 25);
		getContentPane().add(btnCapture);
		btnCapture.addActionListener(this);
		
		image = new PaintImage();
		image.setBounds(12, 12, 1300, 732);
		getContentPane().add(image);
		
		JLabel lblResRatio = new JLabel("Threshold");
		lblResRatio.setBounds(121, 756, 203, 15);
		getContentPane().add(lblResRatio);
		
		txtThreshold = new JTextField();
		txtThreshold.setText("280");
		txtThreshold.setBounds(320, 754, 114, 19);
		getContentPane().add(txtThreshold);
		txtThreshold.setColumns(10);
		
		JLabel lblMinHits = new JLabel("Min Hits");
		lblMinHits.setBounds(121, 811, 70, 15);
		getContentPane().add(lblMinHits);
		
		JLabel lblMinRad = new JLabel("Min rad");
		lblMinRad.setBounds(473, 784, 70, 15);
		getContentPane().add(lblMinRad);
		
		JLabel lblMaxRad = new JLabel("Max rad");
		lblMaxRad.setBounds(473, 811, 70, 15);
		getContentPane().add(lblMaxRad);
		
		txtMinHits = new JTextField();
		txtMinHits.setText("200");
		txtMinHits.setBounds(320, 813, 114, 19);
		getContentPane().add(txtMinHits);
		
		txtMinRad = new JTextField();
		txtMinRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad);
		txtMinRad.setBounds(571, 782, 114, 19);
		getContentPane().add(txtMinRad);
		txtMinRad.setColumns(10);
		
		txtMaxRad = new JTextField();
		txtMaxRad.setText(""+Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad);
		txtMaxRad.setBounds(571, 809, 114, 19);
		getContentPane().add(txtMaxRad);
		txtMaxRad.setColumns(10);
		
		JButton btnCircles = new JButton("Circles");
		btnCircles.setBounds(12, 810, 91, 25);
		getContentPane().add(btnCircles);
		btnCircles.addActionListener(this);
		
		JButton btnScan = new JButton("Scan");
		btnScan.setBounds(12, 849, 91, 25);
		getContentPane().add(btnScan);
		btnScan.addActionListener(this);
		
		JButton btnDetect = new JButton("Detect");
		btnDetect.setBounds(12, 886, 117, 25);
		getContentPane().add(btnDetect);
		btnDetect.addActionListener(this);
		
		txtCalibImg = new JTextField();
		txtCalibImg.setText("3600");
		txtCalibImg.setBounds(320, 889, 114, 19);
		getContentPane().add(txtCalibImg);
		txtCalibImg.setColumns(10);
		
		JLabel lblCompareimg = new JLabel("Compare Image Number");
		lblCompareimg.setBounds(133, 891, 179, 15);
		getContentPane().add(lblCompareimg);
		
		sbHeightOffset = new JScrollBar();
		sbHeightOffset.setMaximum(8);
		sbHeightOffset.setOrientation(JScrollBar.HORIZONTAL);
		sbHeightOffset.setBounds(571, 754, 221, 17);
		getContentPane().add(sbHeightOffset);
		sbHeightOffset.addAdjustmentListener(this);
		
		JLabel lblHeightOffset = new JLabel("Vertical offset");
		lblHeightOffset.setBounds(462, 756, 102, 15);
		getContentPane().add(lblHeightOffset);
		
		JLabel lblRedThreshold = new JLabel("Red threshold");
		lblRedThreshold.setBounds(462, 838, 102, 15);
		getContentPane().add(lblRedThreshold);
		
		JLabel lblGreenThreshold = new JLabel("Green threshold");
		lblGreenThreshold.setBounds(447, 865, 117, 15);
		getContentPane().add(lblGreenThreshold);
		
		JLabel lblBlueThreshold = new JLabel("Blue threshold");
		lblBlueThreshold.setBounds(457, 891, 107, 15);
		getContentPane().add(lblBlueThreshold);
		
		txtRedthreshold = new JTextField();
		txtRedthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r);
		txtRedthreshold.setBounds(571, 836, 114, 19);
		getContentPane().add(txtRedthreshold);
		txtRedthreshold.setColumns(10);
		txtRedthreshold.addActionListener(this);
		
		txtGreenthreshold = new JTextField();
		txtGreenthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g);
		txtGreenthreshold.setBounds(571, 863, 114, 19);
		getContentPane().add(txtGreenthreshold);
		txtGreenthreshold.setColumns(10);
		txtGreenthreshold.addActionListener(this);
		
		txtBluethreshold = new JTextField();
		txtBluethreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b);
		txtBluethreshold.setBounds(571, 889, 114, 19);
		getContentPane().add(txtBluethreshold);
		txtBluethreshold.setColumns(10);
		txtBluethreshold.addActionListener(this);
		
		JLabel lblThresholdLogic = new JLabel("Threshold logic");
		lblThresholdLogic.setBounds(202, 864, 110, 15);
		getContentPane().add(lblThresholdLogic);
		
		cbThresholdLogic = new JComboBox<String>();
		cbThresholdLogic.setBounds(320, 849, 64, 24);
		getContentPane().add(cbThresholdLogic);
		cbThresholdLogic.addItem("Or");
		cbThresholdLogic.addItem("And");
		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("Or"))
		{
			cbThresholdLogic.setSelectedIndex(0);
		}
		else
		{
			cbThresholdLogic.setSelectedIndex(1);
		}
		cbThresholdLogic.addActionListener(this);
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(214, 920, 281, 25);
		getContentPane().add(btnCalibrate);
		btnCalibrate.addActionListener(this);
		
		lblTopLeftMotor = new JLabel("Top left motor pos 1");
		lblTopLeftMotor.setBounds(722, 784, 143, 15);
		getContentPane().add(lblTopLeftMotor);
		
		lblTopRightMotor = new JLabel("Top right motor pos 1 ");
		lblTopRightMotor.setBounds(713, 811, 152, 15);
		getContentPane().add(lblTopRightMotor);
		
		txtTopleftmotorpos = new JTextField();
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		txtTopleftmotorpos.setBounds(883, 782, 114, 19);
		getContentPane().add(txtTopleftmotorpos);
		txtTopleftmotorpos.setColumns(10);
		
		txtToprightmotorpos = new JTextField();
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		txtToprightmotorpos.setBounds(883, 809, 114, 19);
		getContentPane().add(txtToprightmotorpos);
		txtToprightmotorpos.setColumns(10);
		
		lblTopLeftMotor_1 = new JLabel("Top left motor pos 2");
		lblTopLeftMotor_1.setBounds(722, 838, 143, 15);
		getContentPane().add(lblTopLeftMotor_1);
		
		lblTopRightMotor_1 = new JLabel("Top right motor pos 2");
		lblTopRightMotor_1.setBounds(713, 865, 152, 15);
		getContentPane().add(lblTopRightMotor_1);
		
		txtTopleftmotorpos_2 = new JTextField();
		txtTopleftmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2);
		txtTopleftmotorpos_2.setBounds(883, 836, 114, 19);
		getContentPane().add(txtTopleftmotorpos_2);
		txtTopleftmotorpos_2.setColumns(10);
		
		txtToprightmotorpos_2 = new JTextField();
		txtToprightmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2);
		txtToprightmotorpos_2.setBounds(883, 863, 114, 19);
		getContentPane().add(txtToprightmotorpos_2);
		txtToprightmotorpos_2.setColumns(10);
		
		lblCalibrationPosition = new JLabel("Calibration position");
		lblCalibrationPosition.setBounds(121, 784, 143, 15);
		getContentPane().add(lblCalibrationPosition);
		
		ButtonGroup calibrationPositionGroup = new ButtonGroup();
		radioButton = new JRadioButton("1");
		radioButton.setBounds(278, 780, 33, 23);
		getContentPane().add(radioButton);
		radioButton.addActionListener(this);
		calibrationPositionGroup.add(radioButton);
		
		radioButton_1 = new JRadioButton("2");
		radioButton_1.setBounds(330, 780, 47, 23);
		getContentPane().add(radioButton_1);
		radioButton_1.addActionListener(this);
		calibrationPositionGroup.add(radioButton_1);
		
		radioButton.setSelected(true);
		
		lblPointSize = new JLabel("Point size");
		lblPointSize.setBounds(706, 891, 70, 15);
		getContentPane().add(lblPointSize);
		
		lblScale = new JLabel("Scale");
		lblScale.setBounds(927, 891, 70, 15);
		getContentPane().add(lblScale);
		
		sbPointSize = new JScrollBar();
		sbPointSize.setBlockIncrement(10);
		sbPointSize.setUnitIncrement(10);
		sbPointSize.setMinimum(10);
		sbPointSize.setValue(10);
		sbPointSize.setMaximum(80);
		sbPointSize.setOrientation(JScrollBar.HORIZONTAL);
		sbPointSize.setBounds(782, 886, 129, 17);
		getContentPane().add(sbPointSize);
		
		sbScale = new JScrollBar();
		sbScale.setValue(10);
		sbScale.setOrientation(JScrollBar.HORIZONTAL);
		sbScale.setMinimum(1);
		sbScale.setMaximum(40);
		sbScale.setBounds(976, 886, 129, 17);
		getContentPane().add(sbScale);

		JLabel lblScanStartAngle = new JLabel("Scan Start Angle");
		lblScanStartAngle.setBounds(12, 960, 143, 15);
		getContentPane().add(lblScanStartAngle);
		
		tfScanStartAngle = new JTextField();
		tfScanStartAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle);
		tfScanStartAngle.setBounds(143, 958, 47, 19);
		getContentPane().add(tfScanStartAngle);
		tfScanStartAngle.setColumns(10);
		
		JLabel lblScanEndAngle = new JLabel("Scan End Angle");
		lblScanEndAngle.setBounds(207, 960, 117, 15);
		getContentPane().add(lblScanEndAngle);
		
		tfScanEndAngle = new JTextField();
		tfScanEndAngle.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle);
		tfScanEndAngle.setBounds(342, 958, 47, 19);
		getContentPane().add(tfScanEndAngle);
		tfScanEndAngle.setColumns(10);
		
		JLabel lblScanStepSize = new JLabel("Scan Step Size");
		lblScanStepSize.setBounds(407, 960, 117, 15);
		getContentPane().add(lblScanStepSize);
		
		tfScanStepSize = new JTextField();
		tfScanStepSize.setText(""+Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
		tfScanStepSize.setBounds(542, 958, 47, 19);
		getContentPane().add(tfScanStepSize);
		tfScanStepSize.setColumns(10);
		
		JButton btnExportPoints = new JButton("Export points");
		btnExportPoints.setBounds(1114, 886, 129, 25);
		getContentPane().add(btnExportPoints);
		btnExportPoints.addActionListener(this);

		m_camera = a_camera;
		
		setSize(1300,1020);
		
		calculateCalibrationPositions();
		
		setModal(true);

//		m_pcd = new PointCloudData();
		m_pco = new PointCloudObject();
		
}
	
	private static String readFileAsString(String filename) throws Exception {
		StringBuilder source = new StringBuilder();

		FileInputStream in = new FileInputStream(filename);

		Exception exception = null;

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			Exception innerExc = null;
			try {
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception exc) {
				exception = exc;
			} finally {
				try {
					reader.close();
				} catch (Exception exc) {
					if (innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}

			if (innerExc != null)
				throw innerExc;
		} catch (Exception exc) {
			exception = exc;
		} finally {
			try {
				in.close();
			} catch (Exception exc) {
				if (exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}

			if (exception != null)
				throw exception;
		}

		return source.toString();
	}

	boolean GLok(String message) {
		int errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("GL Error " + errorCheckValue + " " + message);
			Thread.dumpStack();
			return false;
		}
		return true;
	}


	private int shaderInit(String vsn, String fsn) {

		String vertexShader;
		String fragmentShader;
		int errorCheckValue;
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			System.out.println("Current relative path is: " + s);
			vertexShader = readFileAsString("src/eora3D/" + vsn);
			fragmentShader = readFileAsString("src/eora3D/" + fsn);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		int program = glCreateProgram();
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to create shader program");
			return -1;
		}
		int vs = glCreateShader(GL_VERTEX_SHADER);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to create vertex shader");
			return -1;
		}
		glShaderSource(vs, vertexShader);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to load vertex shader string");
			return -1;
		}
		glCompileShader(vs);
		int isCompiled;
		isCompiled = glGetShaderi(vs, GL_COMPILE_STATUS);
		if (isCompiled == GL_FALSE) {
			String shaderLog = glGetShaderInfoLog(vs);
			System.err.println("Failed to compile vertex shader");
			System.err.println(shaderLog);
			return -1;
		}
		glAttachShader(program, vs);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to attach vertex shader to program");
			return -1;
		}

		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to create fragment shader");
			return -1;
		}
		glShaderSource(fs, fragmentShader);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to load fragment shader");
			return -1;
		}
		glCompileShader(fs);
		isCompiled = glGetShaderi(fs, GL_COMPILE_STATUS);
		if (isCompiled == GL_FALSE) {
			String shaderLog = glGetShaderInfoLog(fs);
			System.err.println("Failed to compile fragment shader");
			System.err.println(shaderLog);
			return -1;
		}
		glAttachShader(program, fs);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("Failed to attach fragment shader to program");
			return -1;
		}
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Failed to link program");
			System.err.println(glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH)));
			return -1;
		}

		System.out.println("Created program Id " + program);
		return program;
	}

	private void render(EGLCapabilities egl, GLESCapabilities gles) {

		int modelViewLoc;
		int scaleLoc;

		int errorCheckValue;

		// Create a simple quad
		int vbo = glGenBuffers();
		int ibo = glGenBuffers();
		float[] vertices = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // Red X
				250.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				
				0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,// Green Y
				0.0f, 500.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Blue Z
				0.0f, 0.0f, 250.0f, 0.0f, 0.0f, 1.0f, 0.0f,
				0 };
		int[] indices = { 1, 0, 2, 3, 4, 5 };
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(),
				GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4 + 4 * 4, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 3 * 4 + 4 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,
				(IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip(), GL_STATIC_DRAW);

		glClearColor(0.0f, 0.5f, 1.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, displayW, displayH);
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("GL Error " + errorCheckValue);
			Thread.dumpStack();
			return;
		}

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glFrontFace(GL_CCW);

		glDepthRangef(0.0f, 1.0f);
		if (!GLok("Setting glDepthRange"))
			return;
		glEnable(GL_DEPTH_TEST);

		glDisable(GL_CULL_FACE);
		if (!GLok("Disabling GL_CULL_FACE"))
			return;

		Matrix4f projectM = new Matrix4f();
		Matrix4f viewM = new Matrix4f();
		Matrix4f modelM = new Matrix4f();
		Matrix4f modelView = new Matrix4f();
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);

		Quaternionf q = new Quaternionf();
		Quaternionf q0 = new Quaternionf();
		Quaternionf q1 = new Quaternionf();
		projectM
				.setOrtho(-1000.0f, 1000.0f, -1000.0f, 1000.0f, -30000.0f, 30000.0f);
//				.setPerspective((3.14159f * 2.0f) / 3.0f, (float) displayW / (float) displayH, 0.01f, 6000.0f);
		viewM.identity();
//		viewM
//				.lookAt(m_pcd.x_pos, m_pcd.y_pos, m_pcd.z_pos+2000.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		viewM
		.lookAt(0.0f, 10.0f, 15000.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
/*		viewM.translate(m_pcd.x_pos, m_pcd.y_pos, m_pcd.z_pos);
		viewM.rotate(q.rotateZ((float) Math.toRadians(m_pcd.z_rot)).normalize())
				.rotate(q0.rotateY((float) Math.toRadians(m_pcd.y_rot)).normalize())
				.rotate(q1.rotateX((float) Math.toRadians(m_pcd.x_rot)).normalize());*/
		modelM.identity();
		modelM.rotate(q.rotateX((float) Math.toRadians(m_rot)).normalize());
		//modelM.translate(0.0f, 0.0f, 0.0f);
		// System.out.println("Z "+(-2f+rot/120.0f));

		glUseProgram(m_main_program);
		if (!GLok(""))
			return;
		modelViewLoc = glGetUniformLocation(m_main_program, "modelView");
		if (!GLok(""))
			return;
		modelView.identity();
		modelView.mul(projectM).mul(viewM).mul(modelM);
//		modelView = projectM.mul(viewM).mul(modelM);
		glUniformMatrix4fv(modelViewLoc, false, modelView.get(fb));
		if (!GLok(""))
			return;
		glBindAttribLocation(m_main_program, 0, "vertex");
		if (!GLok(""))
			return;
		glBindAttribLocation(m_main_program, 0, "color");
		if (!GLok(""))
			return;
		glDrawElements(GL_LINES, 6, GL_UNSIGNED_INT, 0);
		if (!GLok(""))
			return;

		modelM.identity();
		modelM.rotate(q.rotateX((float) Math.toRadians(m_rot)).normalize());
		modelM.translate(0.0f, 0.0f, -2000.0f);
//		modelM.rotate(q.rotateY((float) Math.toRadians(m_rot)).normalize());
		//modelM.rotate(q.rotateX((float) Math.toRadians(m_rot)).normalize());
//				.translate(m_pcd.x_pos, m_pcd.y_pos, m_pcd.z_pos)

		/* .rotate(q.rotateZ((float) Math.toRadians(rot)).normalize()) */;
		int l_program;
/*		if(m_pcd.m_layers.get(i).fixedColor)
		{
			glUseProgram(m_point_program);
			l_program = m_point_program;
			if (!GLok("glUseProgram(m_point_program)"))
				return;
			int l_colorloc = glGetUniformLocation(m_point_program, "color");
			GLok("Retrieving scale uniform location");
				glUniform4f(l_colorloc,
						m_pcd.m_layers.get(i).r/255.0f,
						m_pcd.m_layers.get(i).g/255.0f,
						m_pcd.m_layers.get(i).b/255.0f,
						0.0f
						);
		}
		else*/
	//	{
			glUseProgram(m_point_program_col);
			l_program = m_point_program_col;
			if (!GLok("glUseProgram(m_point_program_col)"))
				return;
			glBindAttribLocation(m_point_program, 1, "color");
			if (!GLok("Setting glBindAttribLocation"))
				return;
		//}
		modelViewLoc = glGetUniformLocation(l_program, "modelView");
		if (!GLok("Calling glGetUniformLocation"))
			return;
		modelView.identity();
		modelView.mul(projectM).mul(viewM).mul(modelM);
		glUniformMatrix4fv(modelViewLoc, false, modelView.get(fb));
		if (!GLok("Setting glUniformMatrix4fv"))
			return;
		scaleLoc = glGetUniformLocation(l_program, "scale");
		GLok("Retrieving scale uniform location");
		//System.out.println("Scale to "+((float)sbScale.getValue()/10.0f));
		glUniform1f(scaleLoc, ((float)sbScale.getValue()/10.0f)); // m_scalefactor 
		GLok("Set scale uniform");
		int pointsizeLoc = glGetUniformLocation(l_program, "pointsize");
		GLok("Retrieving pointsize uniform location");
		//System.out.println("Point size to "+sbPointSize.getValue()/10);
		glUniform1f(pointsizeLoc, (float)sbPointSize.getValue()/10.0f); // m_scalefactor 
		GLok("Set pointsize uniform");

		glBindAttribLocation(l_program, 0, "vertex");
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("GL Error " + errorCheckValue);
			Thread.dumpStack();
			return;
		}
		m_pco.draw();

		long thisTime = System.nanoTime();
		float delta = (thisTime - lastTime) / 1E9f;
		m_rot += delta * 20f;
		if (m_rot > 360.0f) {
			m_rot = 0.0f;
		}
		//m_rot = 176.0f;
//		System.out.println("Rot: "+m_rot);
		lastTime = thisTime;

	}



	private void calculateCalibrationPositions() {
		if(m_cal_data == null)
		{
			m_cal_data = new CalibrationData();
			m_cal_data.v_offset = Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset;
		}
		
		if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
		{
			m_cal_data.capture_h = (int)m_camera.getViewSize().getWidth();
			m_cal_data.capture_w = (int)m_camera.getViewSize().getHeight();
		}
		else
		{
			m_cal_data.capture_w = (int)m_camera.getViewSize().getWidth();
			m_cal_data.capture_h = (int)m_camera.getViewSize().getHeight();
		}
		
		m_cal_data.calculate();
		
		image.m_cal_data = m_cal_data;
		
		sbHeightOffset.removeAdjustmentListener(this);
		sbHeightOffset.setMinimum(-m_cal_data.v_offset_minmax);
		sbHeightOffset.setMaximum(m_cal_data.v_offset_minmax);
		System.out.println("Set minmax "+m_cal_data.v_offset_minmax);
		//sbHeightOffset.setValue(1);
		sbHeightOffset.addAdjustmentListener(this);

	}
	
	BufferedImage rotate(BufferedImage a_image)
	{
		BufferedImage l_rot_image = new BufferedImage(a_image.getHeight(), a_image.getWidth(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D l_g2d = (Graphics2D) l_rot_image.getGraphics();
		AffineTransform l_backup = l_g2d.getTransform();
		AffineTransform l_at = AffineTransform.getRotateInstance(Math.PI/2.0f, a_image.getHeight()/2, a_image.getHeight()/2);
		l_g2d.setTransform(l_at);
		l_g2d.drawImage(a_image, 0, 0, null);
		l_g2d.setTransform(l_backup);
		return l_rot_image;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Capture")
		{
			BufferedImage l_image = m_camera.getImage();
			l_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				l_image = rotate(l_image);
			}
			
//			image.circles=null;
			
			image.m_image = l_image;
			image.m_overlay = null;
			capturedImage = l_image;
			image.repaint();
		} else
		if(e.getActionCommand()=="Circles")
		{
			if(capturedImage == null) return;
			
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
				l_cd.initTotalCircles(capturedImage);
//				System.out.println("Detecting circles");
				if(image.pos==1)
				{
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_tl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_tl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_tr.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tr.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_tr.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_tr.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_bl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_bl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_bl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_bl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_1_br.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_br.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_1_br.width+m_cal_data.detection_box,
						    		m_cal_data.pos_1_br.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
				}
				else
				{
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_tl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_tl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_tr.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tr.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_tr.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_tr.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_bl.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_bl.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_bl.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_bl.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
					l_cd.detect(capturedImage,
							new Rectangle(
						    		m_cal_data.pos_2_br.x-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_br.y-m_cal_data.detection_box/2,
						    		m_cal_data.pos_2_br.width+m_cal_data.detection_box,
						    		m_cal_data.pos_2_br.height+m_cal_data.detection_box
									));
					l_cd.findFirstCircle(
							capturedImage
							);
				}
//				System.out.println("Detection complete");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			image.m_overlay = l_cd.displayimage;
			image.repaint();
/*
			Mat input = new Mat(l_image.getHeight(), l_image.getWidth(), CvType.CV_8UC3);
			byte data[] = ((DataBufferByte)l_image.getRaster().getDataBuffer()).getData();
			input.put(0,  0,  data);
			Mat grey = new Mat();
			Imgproc.cvtColor(input,  grey,  Imgproc.COLOR_BGRA2GRAY);
			image.circles = new Mat(10, 10, CvType.CV_64FC1);
			System.out.println("Detecting circles");
			Imgproc.HoughCircles(grey, image.circles, Imgproc.CV_HOUGH_GRADIENT,
					Integer.parseInt(txtResratio.getText()),
					Integer.parseInt(txtMinDist.getText()),
					Integer.parseInt(txtParam.getText()),
					Integer.parseInt(txtParam_1.getText()),
					Integer.parseInt(txtMinRad.getText()),
					Integer.parseInt(txtMaxRad.getText()));
			System.out.println("Detection complete");
*/
		} else
		if(e.getActionCommand()=="Scan")
		{
			Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
			Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
			captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle,
					Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle,
					Eora3D_MainWindow.m_e3d_config.sm_scan_step_size);
		} else
		if(e.getActionCommand()=="Detect")
		{
			if(m_detect_thread == null)
			{
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = Integer.parseInt(txtTopleftmotorpos.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = Integer.parseInt(txtToprightmotorpos.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = Integer.parseInt(txtTopleftmotorpos_2.getText());
				Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = Integer.parseInt(txtToprightmotorpos_2.getText());
				if(m_thread == null)
				{
					m_thread = new Thread(this);
					m_thread.start();
				}
				Runnable l_runnable = () -> {
					Detect();
				};
				m_detect_thread = new Thread(l_runnable);
				m_detect_thread.start();
			}
		} else
		if(e.getActionCommand()=="Calibrate")
		{
			if(m_calibration_thread == null)
			{
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
				Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
				m_stop_calibration_thread = false;
				Runnable l_runnable = () -> {
					Calibrate();
				};
				m_calibration_thread = new Thread(l_runnable);
				m_calibration_thread.start();
			}
			else
			{
				m_stop_calibration_thread = true;
			}
		} else
		if(e.getSource().equals(radioButton))
		{
			if(radioButton.isSelected()) image.pos = 1;
		} else
		if(e.getSource().equals(radioButton_1))
		{
			if(radioButton_1.isSelected()) image.pos = 2;
		} else
		if(e.getActionCommand()=="Export points")
		{
			m_pco.save("test.ply");
		}
	}

	void Calibrate()
	{
		Rectangle l_found[] = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
		int l_corner_pos[] = {0, 0, 0, 0};
		// Phase one, make sure board is in right place for pos1
		image.pos = 1;
		int l_goodenough;
		do
		{
			l_goodenough = 0;
			capturedImage = m_camera.getImage();
			capturedImage.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				capturedImage = rotate(capturedImage);
			}
			image.m_image = capturedImage;
			image.m_overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.m_overlay.getGraphics();
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
//				System.out.println("Detecting circles");
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tl.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_1_tl.x+m_cal_data.pos_1_tl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_1_tl.y+m_cal_data.pos_1_tl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_1_tl.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				else if(l_found[0] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tr.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_tr.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_tr.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_1_tr.x+m_cal_data.pos_1_tr.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_1_tr.y+m_cal_data.pos_1_tr.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_1_tr.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				else if(l_found[1] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_bl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_bl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_bl.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_1_bl.x+m_cal_data.pos_1_bl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_1_bl.y+m_cal_data.pos_1_bl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_1_bl.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				else if(l_found[2] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_br.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_1_br.width+m_cal_data.detection_box,
					    		m_cal_data.pos_1_br.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_1_br.x+m_cal_data.pos_1_br.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_1_br.y+m_cal_data.pos_1_br.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_1_br.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
				else if(l_found[3] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			image.repaint();
			if(m_stop_calibration_thread)
			{
				m_calibration_thread = null;
				return;
			}
		} while (l_goodenough < 4);
		// Hunt for laser in detection circles
		{
			int l_pos, l_pos_slow;
//			System.out.println("Move to base pos");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-2))
			{
				m_calibration_thread = null;
				return;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			BufferedImage l_base_image = m_camera.getImage();
			l_base_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				l_base_image = rotate(l_base_image);
			}

			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
			for(int corner=0; corner<2; ++corner)
			{
				Rectangle l_search_box = null;
				System.out.println("Scan corner "+corner);
				switch(corner)
				{
					case 0: l_search_box = m_cal_data.pos_1_tl; break;
					case 1: l_search_box = m_cal_data.pos_1_tr; break;
					case 2: l_search_box = m_cal_data.pos_1_bl; break;
					case 3: l_search_box = m_cal_data.pos_1_br; break;
				}
				if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-1))
				{
					m_calibration_thread = null;
					return;
				}
				// Fast search
				for(
						l_pos = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset;
						l_pos < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						l_pos += Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg/4)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						m_calibration_thread = null;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
					{
						l_image = rotate(l_image);
					}

					int l_found_offset = findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					return;
				}
				// Slow search
				for(
						l_pos_slow = l_pos - Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
						l_pos_slow < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						++l_pos_slow)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos_slow))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						m_calibration_thread = null;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
					{
						l_image = rotate(l_image);
					}
	
					int l_found_offset = findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos_slow >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					m_calibration_thread = null;
					return;
				}
				l_corner_pos[corner] = l_pos_slow;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		}
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = l_corner_pos[0];
		txtTopleftmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1);
		System.out.println("Motorpos TL 1: "+l_corner_pos[0]);
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = l_corner_pos[1];
		txtToprightmotorpos.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1);
		System.out.println("Motorpos TR 1: "+l_corner_pos[1]);
		
		
		
		
		image.pos = 2;
		// Repeat for pos2
		int l_corner_pos_2[] = {0, 0, 0, 0};
		// Phase one, make sure board is in right place for pos1
		do
		{
			l_goodenough = 0;
			capturedImage = m_camera.getImage();
			capturedImage.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				capturedImage = rotate(capturedImage);
			}
			image.m_image = capturedImage;
			image.m_overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.m_overlay.getGraphics();
			CircleDetection l_cd = new CircleDetection();
			l_cd.threshold = Integer.parseInt(txtThreshold.getText());
			l_cd.minHits = Integer.parseInt(txtMinHits.getText());
			l_cd.minr = Integer.parseInt(txtMinRad.getText());
			l_cd.maxr = Integer.parseInt(txtMaxRad.getText());

			try {
//				System.out.println("Detecting circles");
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_2_tl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_tl.height+m_cal_data.detection_box
								));
				l_found[0] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[0] != null &&
						Math.abs(l_found[0].x-(m_cal_data.pos_2_tl.x+m_cal_data.pos_2_tl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].y-(m_cal_data.pos_2_tl.y+m_cal_data.pos_2_tl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[0].width-(m_cal_data.pos_2_tl.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				else if(l_found[0] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[0].x- l_found[0].width,
				    		l_found[0].y - l_found[0].height,
				    		l_found[0].width * 2,
				    		l_found[0].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_2_tr.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tr.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_tr.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_tr.height+m_cal_data.detection_box
								));
				l_found[1] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[1] != null &&
						Math.abs(l_found[1].x-(m_cal_data.pos_2_tr.x+m_cal_data.pos_2_tr.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].y-(m_cal_data.pos_2_tr.y+m_cal_data.pos_2_tr.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[1].width-(m_cal_data.pos_2_tr.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				else if(l_found[1] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[1].x- l_found[1].width,
				    		l_found[1].y - l_found[1].height,
				    		l_found[1].width * 2,
				    		l_found[1].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_2_bl.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_bl.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_bl.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_bl.height+m_cal_data.detection_box
								));
				l_found[2] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[2] != null &&
						Math.abs(l_found[2].x-(m_cal_data.pos_2_bl.x+m_cal_data.pos_2_bl.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].y-(m_cal_data.pos_2_bl.y+m_cal_data.pos_2_bl.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[2].width-(m_cal_data.pos_2_bl.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				else if(l_found[2] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[2].x- l_found[2].width,
				    		l_found[2].y - l_found[2].height,
				    		l_found[2].width * 2,
				    		l_found[2].height * 2
				    		);
				}
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_2_br.x-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_br.y-m_cal_data.detection_box/2,
					    		m_cal_data.pos_2_br.width+m_cal_data.detection_box,
					    		m_cal_data.pos_2_br.height+m_cal_data.detection_box
								));
				l_found[3] = l_cd.findFirstCircle(
						capturedImage
						);
				if(l_found[3] != null &&
						Math.abs(l_found[3].x-(m_cal_data.pos_2_br.x+m_cal_data.pos_2_br.width/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].y-(m_cal_data.pos_2_br.y+m_cal_data.pos_2_br.height/2))<m_cal_data.circle_center_threshold &&
						Math.abs(l_found[3].width-(m_cal_data.pos_2_br.width/2))<m_cal_data.circle_radius_threshold)
				{
					++l_goodenough;
					g.setColor(Color.green);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
				else if(l_found[3] != null)
				{
					g.setColor(Color.red);
				    g.fillOval(
				    		l_found[3].x- l_found[3].width,
				    		l_found[3].y - l_found[3].height,
				    		l_found[3].width * 2,
				    		l_found[3].height * 2
				    		);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			image.repaint();
			if(m_stop_calibration_thread)
			{
				m_calibration_thread = null;
				image.pos = 1;
				return;
			}
		} while (l_goodenough < 4);
		// Hunt for laser in detection circles
		{
			int l_pos, l_pos_slow;
//			System.out.println("Move to base pos");
			if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-2))
			{
				image.pos = 1;
				return;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
			BufferedImage l_base_image = m_camera.getImage();
			l_base_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				l_base_image = rotate(l_base_image);
			}

			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(true);
			for(int corner=0; corner<2; ++corner)
			{
				Rectangle l_search_box = null;
				System.out.println("Scan corner "+corner);
				switch(corner)
				{
					case 0: l_search_box = m_cal_data.pos_2_tl; break;
					case 1: l_search_box = m_cal_data.pos_2_tr; break;
					case 2: l_search_box = m_cal_data.pos_2_bl; break;
					case 3: l_search_box = m_cal_data.pos_2_br; break;
				}
				if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset-1))
				{
					image.pos = 1;
					return;
				}
				// Fast search
				for(
						l_pos = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset;
						l_pos < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						l_pos += Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg/4)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						image.pos = 1;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
					{
						l_image = rotate(l_image);
					}

					int l_found_offset = findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						image.pos = 1;
						break;
					}
				}
				if(l_pos >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					image.pos = 1;
					return;
				}
				// Slow search
				for(
						l_pos_slow = l_pos - Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
						l_pos_slow < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
						++l_pos_slow)
				{
//					System.out.println("Move to base step "+l_pos);
					if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(l_pos_slow))
					{
						Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
						image.pos = 1;
						return;
					}
//					System.out.println("Moved to base step "+l_pos);
					BufferedImage l_image = m_camera.getImage();
					l_image.flush();
					if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
					{
						l_image = rotate(l_image);
					}
	
					int l_found_offset = findLaserPoint(l_base_image, l_image, l_found[0].y , l_search_box.x-m_cal_data.detection_box/2,
							l_search_box.x+m_cal_data.detection_box/2);
					if(l_found_offset!=-1)
					{
						System.out.println("Found corner "+corner);
						break;
					}
				}
				if(l_pos_slow >= Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90)
				{
					JOptionPane.showMessageDialog(getContentPane(), "Failed to detect laser in first phase", "Laser Detection Failed", JOptionPane.ERROR_MESSAGE);
					image.pos = 1;
					return;
				}
				l_corner_pos_2[corner] = l_pos_slow;
			}
			Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		}
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = l_corner_pos_2[0];
		txtTopleftmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2);
		System.out.println("Motorpos TL 2: "+l_corner_pos_2[0]);
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = l_corner_pos_2[1];
		txtToprightmotorpos_2.setText(""+Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2);
		System.out.println("Motorpos TR 2: "+l_corner_pos_2[1]);
		invalidate();
		
		image.pos = 1;
		m_calibration_thread = null;

	}

	void Detect()
	{
		m_pco.clear();
		m_cal_data.calculateBaseCoords();
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
		File l_basefile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_base.png");
		for (int l_pos = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset;
				l_pos < Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*45;
				++l_pos)
		{
			File l_infile;
			try {
				l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_"+l_pos+".png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_detect_thread = null;
				return;
			}
			if(!l_infile.exists()) continue;
			System.out.println("Analysing "+l_infile.toString());
			BufferedImage l_inimage, l_baseimage;

			try {
				l_baseimage = ImageIO.read(l_basefile);
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				e1.printStackTrace();
				m_detect_thread = null;
				return;
			}
			
			image.m_image = l_baseimage;
//			image.m_overlay = analyzeImage(l_baseimage, l_inimage);
			
			Thread l_threads[] = new Thread[Eora3D_MainWindow.m_e3d_config.sm_threads];
			for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
			{
				final int l_lambda_thread = l_thread;
				final int l_lambda_pos = l_pos;
				Runnable l_task = () -> { 
					int l_x_points[];
					// TBC correctify this!
					int l_range_start = (l_baseimage.getHeight()/8)*l_lambda_thread;
					int l_range_end = (l_baseimage.getHeight()/8)*(l_lambda_thread+1);
					l_x_points = analyzeImagetoArray(l_baseimage, l_inimage, l_range_start, l_range_end);
					for( int i=l_range_start; i<l_range_end; ++i)
					{
						if(l_x_points[i]>=0)
						{
							RGB3DPoint l_point = m_cal_data.getPointOffset(l_lambda_pos, l_x_points[i], (l_baseimage.getHeight()-i)-1);
							l_point.m_r = (l_baseimage.getRGB(l_x_points[i], i) & 0xff0000)>>16;
							l_point.m_g = (l_baseimage.getRGB(l_x_points[i], i) & 0xff00)>>8;
							l_point.m_b = l_baseimage.getRGB(l_x_points[i], i) & 0x00;
		//					System.out.println(l_point.m_x+","+l_point.m_y+","+l_point.m_z);
							//System.out.println("Z calculated as "+l_x_points[i]+" -> "+m_cal_data.getZoffset(l_pos, l_x_points[i]));
							m_pco.addPoint(l_point);
						}
					}
				};
				l_threads[l_thread] = new Thread(l_task);
				l_threads[l_thread].start();
			}
			for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
			{
				try {
					l_threads[l_thread].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			image.repaint();
			System.out.println("Complete "+l_infile.toString());
		}
//		System.out.println("Found "+l_points+" points");
		System.out.println("Min Z: "+m_cal_data.m_minz);
		System.out.println("Max Z: "+m_cal_data.m_maxz);

		m_detect_thread = null;
	}
	
	BufferedImage analyzeImage(BufferedImage a_base, BufferedImage a_in)
	{
		BufferedImage a_out = new BufferedImage(a_in.getWidth(), a_in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int x, y;

		for(y = 0; y< a_in.getHeight(); ++y)
		{
			for(x = a_in.getWidth()-1; x>=0; --x)
			{
				int argb = a_in.getRGB(x, y);
				int r = (argb & 0xff0000) >> 16;
            	int g = (argb & 0x00ff00) >> 8;
        		int b = (argb & 0x0000ff);
        		
				int argb_base = a_base.getRGB(x, y);
				int r_base = (argb_base & 0xff0000) >> 16;
            	int g_base = (argb_base & 0x00ff00) >> 8;
        		int b_base = (argb_base & 0x0000ff);
        		
        		//r = ((255-r)-(255-r_base));
        		//g = ((255-g)-(255-g_base));
        		//b = ((255-b)-(255-b_base));
        		r = Math.abs(r_base-r);
        		g = Math.abs(g_base-g);
        		b = Math.abs(b_base-b);
//        		g = b = 0;
/*        		r = Math.abs(r-r_base);
        		g = Math.abs(g-g_base);
        		b = Math.abs(b-b_base);*/
        		
/*        		r = Math.max(0, r);
        		g = Math.max(0, g);
        		b = Math.max(0, b);*/

//        		r = 255-r;
//        		g = 255-g;
//        		b = 255-b;

        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) argb = 0xff00ff00;
	        		else argb = 0xff000000;
        		} else
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) argb = 0xff00ff00;
	        		else argb = 0xff000000;
        		}
//        		argb = (r << 16) | (g << 8) | (b);

//				argb = 0x00ffffff - argb;				
//				argb |= 0xff000000;
				a_out.setRGB(x, y, argb);
				if(argb == 0xff00ff00) break;
			}
		}
		return a_out;
	}
	
	int[] analyzeImagetoArray(BufferedImage a_base, BufferedImage a_in, int a_start, int a_end)
	{
		int[] a_out = new int[a_in.getHeight()];
		int x, y;

		for(y = a_start; y < a_end; ++y)
		{
			for(x = a_in.getWidth()-1; x>=0; --x)
			{
				int argb = a_in.getRGB(x, y);
				int r = (argb & 0xff0000) >> 16;
            	int g = (argb & 0x00ff00) >> 8;
        		int b = (argb & 0x0000ff);
        		
				int argb_base = a_base.getRGB(x, y);
				int r_base = (argb_base & 0xff0000) >> 16;
            	int g_base = (argb_base & 0x00ff00) >> 8;
        		int b_base = (argb_base & 0x0000ff);
        		
        		r = Math.abs(r_base-r);
        		g = Math.abs(g_base-g);
        		b = Math.abs(b_base-b);

        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) break;
        		} else
        		{
	        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) break;
        		}
			}
			a_out[y] = x;
		}
		return a_out;
	}
	
	int findLaserPoint(BufferedImage a_base, BufferedImage a_in, int a_y, int a_x_min, int a_x_max)
	{
		for(int x = a_x_max-1; x>=a_x_min; --x)
		{
			int argb = a_in.getRGB(x, a_y);
			int r = (argb & 0xff0000) >> 16;
        	int g = (argb & 0x00ff00) >> 8;
    		int b = (argb & 0x0000ff);
    		
			int argb_base = a_base.getRGB(x, a_y);
			int r_base = (argb_base & 0xff0000) >> 16;
        	int g_base = (argb_base & 0x00ff00) >> 8;
    		int b_base = (argb_base & 0x0000ff);
    		
    		r = Math.abs(r_base-r);
    		g = Math.abs(g_base-g);
    		b = Math.abs(b_base-b);

    		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
    		{
        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) return x;
    		} else
    		{
        		if(r>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
        				b>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) return x;
    		}
		}
		return -1;
	}
	
	void captureScanChain(int a_start, int a_end, int a_stepsize)
	{
		if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.isDirectory())
			if(!Eora3D_MainWindow.m_e3d_config.sm_image_dir.mkdir())
		{
			JOptionPane.showMessageDialog(getContentPane(), "Failed", "Creating scanned imag store", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!Eora3D_MainWindow.m_e3D_bluetooth.setMotorPos(a_start-1))
		{
			return;
		}
		Eora3D_MainWindow.m_e3D_bluetooth.setLaserStatus(false);
		File l_outfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_base.png");
		BufferedImage l_image = m_camera.getImage();
		l_image.flush();
		if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
		{
			l_image = rotate(l_image);
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
			l_image = m_camera.getImage();
			l_image.flush();
			if(Eora3D_MainWindow.m_e3d_config.sm_rotate_camera)
			{
				l_image = rotate(l_image);
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

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(e.getSource().equals(sbHeightOffset))
		{
//			System.out.println("Scrollbar at "+sbHeightOffset.getValue());
			m_cal_data.v_offset = sbHeightOffset.getValue();
			calculateCalibrationPositions();
			image.repaint();
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		m_stop_calibration_thread = true;
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(this.txtRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(this.txtGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(this.txtBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)this.cbThresholdLogic.getSelectedItem();
		Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset = m_cal_data.v_offset;
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1 = Integer.parseInt(txtTopleftmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1 = Integer.parseInt(txtToprightmotorpos.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2 = Integer.parseInt(txtTopleftmotorpos_2.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2 = Integer.parseInt(txtToprightmotorpos_2.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_min_rad = Integer.parseInt(this.txtMinRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_circle_max_rad = Integer.parseInt(this.txtMaxRad.getText());
		Eora3D_MainWindow.m_e3d_config.sm_calibration_v_offset = sbHeightOffset.getValue();
		Eora3D_MainWindow.m_e3d_config.sm_scan_start_angle = Integer.parseInt(tfScanStartAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_end_angle = Integer.parseInt(tfScanEndAngle.getText());
		Eora3D_MainWindow.m_e3d_config.sm_scan_step_size = Integer.parseInt(tfScanStepSize.getText());
	}

	@Override
	public void windowClosed(WindowEvent e) {
		glfwSetWindowShouldClose(m_window, true);
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

	@Override
	public void run() {
		// Render with OpenGL ES

		System.out.println("Run");
		float scale = java.awt.Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;
		System.out.println("Res: " + java.awt.Toolkit.getDefaultToolkit().getScreenResolution());
		displayW = 1000;
		displayH = 680;
		/*
		 * try (MemoryStack stack = stackPush()) { IntBuffer peError =
		 * stack.mallocInt(1);
		 * 
		 * int token = VR_InitInternal(peError, 0); if (peError.get(0) == 0) { try {
		 * OpenVR.create(token);
		 * 
		 * System.err.println("Model Number : " +
		 * VRSystem_GetStringTrackedDeviceProperty( k_unTrackedDeviceIndex_Hmd,
		 * ETrackedDeviceProperty_Prop_ModelNumber_String, peError));
		 * System.err.println("Serial Number: " +
		 * VRSystem_GetStringTrackedDeviceProperty( k_unTrackedDeviceIndex_Hmd,
		 * ETrackedDeviceProperty_Prop_SerialNumber_String, peError));
		 * 
		 * IntBuffer w = stack.mallocInt(1); IntBuffer h = stack.mallocInt(1);
		 * VRSystem_GetRecommendedRenderTargetSize(w, h);
		 * System.err.println("Recommended width : " + w.get(0));
		 * System.err.println("Recommended height: " + h.get(0)); displayW = w.get(0);
		 * displayH = h.get(0); } finally { VR_ShutdownInternal(); } } else {
		 * System.out.println("INIT ERROR SYMBOL: " +
		 * VR_GetVRInitErrorAsSymbol(peError.get(0)));
		 * System.out.println("INIT ERROR  DESCR: " +
		 * VR_GetVRInitErrorAsEnglishDescription(peError.get(0))); } }
		 */
		{
			// OpenGL ES 3.0 EGL init
			GLFWErrorCallback.createPrint().set();
			if (!glfwInit()) {
				throw new IllegalStateException("Unable to initialize glfw");
			}

			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
			glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
			//glfwWindowHint(GLFW_FLOATING, GLFW_TRUE);

			// GLFW setup for EGL & OpenGL ES
			glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_EGL_CONTEXT_API);
			glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

			m_window = glfwCreateWindow(displayW, displayH, "glpanel", NULL, NULL);
			if (m_window == NULL) {
				throw new RuntimeException("Failed to create the GLFW window");
			}
			/*glfwSetWindowPos(m_window, (int) (m_glpanel.getLocationOnScreen().getX() * scale),
					(int) (m_glpanel.getLocationOnScreen().getY() * scale));*/

			glfwSetKeyCallback(m_window, (windowHnd, key, scancode, action, mods) -> {
				/*
				 * if (action == GLFW_RELEASE && key == GLFW_KEY_ESCAPE) {
				 * glfwSetWindowShouldClose(windowHnd, true); }
				 */
			});
			glfwSetWindowPosCallback(m_window, (windowHnd, xpos, ypos) -> {
				System.out.println("Moved by system to " + xpos + "," + ypos);
			});
			glfwSetWindowSizeCallback(m_window, (windowHnd, width, height) -> {
				System.out.println("Resized to " + width+ "," + height);
				displayW = width;
				displayH = height;
			});

			// EGL capabilities
			long dpy = glfwGetEGLDisplay();

			try (MemoryStack stack = stackPush()) {
				IntBuffer major = stack.mallocInt(1);
				IntBuffer minor = stack.mallocInt(1);

				if (!eglInitialize(dpy, major, minor)) {
					throw new IllegalStateException(String.format("Failed to initialize EGL [0x%X]", eglGetError()));
				}

				m_egl = EGL.createDisplayCapabilities(dpy, major.get(0), minor.get(0));
			}
/*
			try {
				System.out.println("EGL Capabilities:");
				for (Field f : EGLCapabilities.class.getFields()) {
					if (f.getType() == boolean.class) {
						if (f.get(m_egl).equals(Boolean.TRUE)) {
							System.out.println("\t" + f.getName());
						}
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
*/
			// OpenGL ES capabilities
			glfwMakeContextCurrent(m_window);
			m_gles = GLES.createCapabilities();
/*
			try {
				System.out.println("OpenGL ES Capabilities:");
				for (Field f : GLESCapabilities.class.getFields()) {
					if (f.getType() == boolean.class) {
						if (f.get(m_gles).equals(Boolean.TRUE)) {
							System.out.println("\t" + f.getName());
						}
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
*/
			System.out.println("GL_VENDOR: " + glGetString(GL_VENDOR));
			System.out.println("GL_VERSION: " + glGetString(GL_VERSION));
			System.out.println("GL_RENDERER: " + glGetString(GL_RENDERER));

			m_main_program = shaderInit("mainVertexShader.glsl", "mainFragmentShader.glsl");
			if (m_main_program == -1) {
				System.err.println("Failed to initialise main shaders");
				GLES.setCapabilities(null);

				glfwFreeCallbacks(m_window);
				glfwTerminate();
				System.exit(1);
			}
			m_point_program = shaderInit("pointVertexShader.glsl", "pointFragmentShader.glsl");
			if (m_point_program == -1) {
				System.err.println("Failed to initialise point shaders");
				GLES.setCapabilities(null);

				glfwFreeCallbacks(m_window);
				glfwTerminate();
				System.exit(1);
			}
			m_point_program_col = shaderInit("pointVertexShaderCol.glsl", "pointFragmentShaderCol.glsl");
			if (m_point_program_col == -1) {
				System.err.println("Failed to initialise colored point shaders");
				GLES.setCapabilities(null);

				glfwFreeCallbacks(m_window);
				glfwTerminate();
				System.exit(1);
			}
		}
		
		glfwShowWindow(m_window);
		
		while (!glfwWindowShouldClose(m_window)) {
			glfwPollEvents();
/*			if (m_reload) {
				glfwMakeContextCurrent(m_window);
				m_pcos = new ArrayList<>();
				for (int i = 0; i < m_pcd.m_layers.size(); ++i) {
					m_pcos.add(new PointCloudObject(m_pcd, i));
					if (!m_pcos.get(i).load()) {
						System.err.println("Failed to load point cloud");
						GLES.setCapabilities(null);

						glfwFreeCallbacks(m_window);
						glfwTerminate();
						System.exit(1);

					}
				}
				m_reload = false;
				m_pause = false;
			}
			if (m_pause) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {*/
				//System.out.println("Render");
				render(m_egl, m_gles);
				//System.out.println("Rendered");

				glfwSwapBuffers(m_window);
				
//			}
		}
		m_pco.glclear();
        glfwHideWindow(m_window);
		GLES.setCapabilities(null);
		glfwFreeCallbacks(m_window);
		glfwTerminate();
	}
}

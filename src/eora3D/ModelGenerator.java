package eora3D;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import javax.swing.JRadioButton;

final class RawPoint implements Serializable
{
	public int m_x;
	public int m_y;
	public int m_angle;
	public int m_r;
	public int m_g;
	public int m_b;
}

public class ModelGenerator extends JDialog implements ActionListener, WindowListener, AdjustmentListener {
	Eora3D_MainWindow m_e3d;
	boolean m_block_updates = false;
	
	private JTextField tfRedthreshold;
	private JTextField tfGreenthreshold;
	private JTextField tfBluethreshold;
	private JTextField tfPercentagechange;
	private PaintImage imagePanel;
	private PaintImage glPanel;
	private JScrollBar sbScaling;
	private JScrollBar sbPointsize;
	private JComboBox cbDetectionmethod;
	
	PointCloudObject m_pco;

	Thread m_detect_thread = null;
	boolean m_stop_detection = false;
	private JTextField tfTestframe;
	
	private Socket m_socket = null;
	
	ArrayList<RGB3DPoint> m_points;
	private JCheckBox chckbxTurntableScan;
	private JTextField tfZrotoff;
	private JTextField tfXrotoff;
//	private JTextField tfTTrot;
	
	private JScrollPane imageScrollPane;
	private JTextField tfMinpointsperlaser;
	private JTextField tfMaxPointsPerLine;
	
	boolean m_config_verify = true;
	private JTextField tfLeftFilter;
	private JTextField tfRightFilter;
	private JTextField tfTopFilter;
	private JTextField tfBottomFilter;
	private JTextField tfFrontFilter;
	private JTextField tfBackFilter;
	private JTextField tfYModelOffset;
	private JTextField tfTestrotation;
	private Thread m_pco_thread = null;
	
	long m_minx = 0;
	long m_maxx = 0;
	long m_miny = 0;
	long m_maxy = 0;
	long m_minz = 0;
	long m_maxz = 0;
	
	private ArrayList<ArrayList<RawPoint>> m_raw_points;
	private JCheckBox cbKeepDetectedPoints;
	private JCheckBox cbStopRotation;	
	private JTextField tfRotationModifier;
	private JRadioButton rdbtnColourmap;
	private JRadioButton rdbtnBase;
	private JRadioButton rdbtnLasered;
	private JRadioButton rdbtn3d;
	
	controlSetState m_ControlSetState;
	
	ArrayList<JCheckBox> m_layer_visible;
	ArrayList<JCheckBox> m_layer_coloured;
	ArrayList<JTextField> m_red;
	ArrayList<JTextField> m_green;
	ArrayList<JTextField> m_blue;
	ArrayList<JTextField> m_z_off;
	ArrayList<JTextField> m_x_off;
	ArrayList<JTextField> m_rot_off;
	private JScrollBar sbViewYRotation;
	private JScrollBar sbViewXRotation;
	private JRadioButton rdbtnDetected;
	
	BufferedImage m_test_image;
	private JTextField tfUX;
	private JTextField tfUZ;
	private JTextField tfVX;
	private JTextField tfVZ;
	private JLabel lblOVXR;
	private JLabel lblOVYR;
	private JTextField tfWX;
	private JTextField tfWZ;
	
	public ModelGenerator(Eora3D_MainWindow a_e3d) {
		setResizable(false);
		setModal(true);
		m_e3d = a_e3d;
		setTitle("Generate Model");
		getContentPane().setLayout(null);
		setSize(1237,670);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Laser Detection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 0, 141, 633);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel label_1 = new JLabel("Detection method");
		label_1.setBounds(6, 22, 122, 15);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("Red threshold");
		label_2.setBounds(6, 61, 106, 15);
		panel.add(label_2);
		
		tfRedthreshold = new JTextField();
		tfRedthreshold.setEnabled(false);
		tfRedthreshold.setColumns(10);
		tfRedthreshold.setBounds(6, 74, 122, 27);
		panel.add(tfRedthreshold);
		
		JLabel label_3 = new JLabel("Green threshold");
		label_3.setBounds(6, 103, 106, 15);
		panel.add(label_3);
		
		tfGreenthreshold = new JTextField();
		tfGreenthreshold.setEnabled(false);
		tfGreenthreshold.setColumns(10);
		tfGreenthreshold.setBounds(6, 121, 122, 27);
		panel.add(tfGreenthreshold);
		
		JLabel label_4 = new JLabel("Blue threshold");
		label_4.setBounds(6, 151, 106, 15);
		panel.add(label_4);
		
		tfBluethreshold = new JTextField();
		tfBluethreshold.setEnabled(false);
		tfBluethreshold.setColumns(10);
		tfBluethreshold.setBounds(6, 169, 122, 27);
		panel.add(tfBluethreshold);
		
		JLabel label_5 = new JLabel("% change");
		label_5.setBounds(6, 198, 106, 15);
		panel.add(label_5);
		
		tfPercentagechange = new JTextField();
		tfPercentagechange.setEnabled(false);
		tfPercentagechange.setColumns(10);
		tfPercentagechange.setBounds(6, 214, 122, 27);
		panel.add(tfPercentagechange);
		
		cbDetectionmethod = new JComboBox();
		cbDetectionmethod.setBounds(6, 37, 106, 25);
		panel.add(cbDetectionmethod);
		cbDetectionmethod.addItem("Or");
		cbDetectionmethod.addItem("And");
		cbDetectionmethod.addItem("%");
		cbDetectionmethod.addItem("Weighted %");
		cbDetectionmethod.addItem("No base peak threshold");
		
		cbDetectionmethod.addActionListener(this);
		
		JButton btnTest = new JButton("Test");
		btnTest.setBounds(6, 421, 100, 27);
		panel.add(btnTest);
		btnTest.addActionListener(this);
		
		tfTestframe = new JTextField();
		tfTestframe.setBounds(6, 393, 122, 27);
		panel.add(tfTestframe);
		tfTestframe.setColumns(10);
		
		JLabel lblTestFrame = new JLabel("Test frame");
		lblTestFrame.setBounds(6, 376, 72, 15);
		panel.add(lblTestFrame);
		
		JLabel lblMinLaserBand = new JLabel("Min laser band pts");
		lblMinLaserBand.setBounds(6, 242, 129, 15);
		panel.add(lblMinLaserBand);
		
		tfMinpointsperlaser = new JTextField();
		tfMinpointsperlaser.setText("3");
		tfMinpointsperlaser.setBounds(6, 262, 122, 27);
		panel.add(tfMinpointsperlaser);
		tfMinpointsperlaser.setColumns(10);
		tfMinpointsperlaser.addActionListener(this);

		JLabel lblMaxPointsPer = new JLabel("Max points per line");
		lblMaxPointsPer.setBounds(6, 285, 174, 15);
		panel.add(lblMaxPointsPer);
		
		tfMaxPointsPerLine = new JTextField();
		tfMaxPointsPerLine.setText("3");
		tfMaxPointsPerLine.setBounds(6, 301, 122, 27);
		panel.add(tfMaxPointsPerLine);
		tfMaxPointsPerLine.setColumns(10);
		tfMaxPointsPerLine.addActionListener(this);
		

		
		JLabel lblTestRotation = new JLabel("Test rotation");
		lblTestRotation.setBounds(6, 326, 106, 15);
		panel.add(lblTestRotation);
		
		tfTestrotation = new JTextField();
		tfTestrotation.setText("0");
		tfTestrotation.setBounds(6, 343, 122, 27);
		panel.add(tfTestrotation);
		tfTestrotation.setColumns(10);
		tfTestrotation.addActionListener(this);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(6, 453, 129, 111);
		panel.add(panel_3);
		panel_3.setLayout(null);
		
		rdbtnColourmap = new JRadioButton("Colourmap");
		rdbtnColourmap.setSelected(true);
		rdbtnColourmap.setBounds(6, 16, 84, 18);
		panel_3.add(rdbtnColourmap);
		rdbtnColourmap.addActionListener(this);
		
		rdbtnBase = new JRadioButton("Base");
		rdbtnBase.setBounds(6, 32, 115, 18);
		panel_3.add(rdbtnBase);
		rdbtnBase.addActionListener(this);
		
		rdbtnLasered = new JRadioButton("Lasered");
		rdbtnLasered.setBounds(6, 48, 115, 18);
		panel_3.add(rdbtnLasered);
		rdbtnLasered.addActionListener(this);
		
		rdbtnDetected = new JRadioButton("Detected");
		rdbtnDetected.setBounds(6, 65, 94, 18);
		panel_3.add(rdbtnDetected);
		rdbtnDetected.addActionListener(this);

		rdbtn3d = new JRadioButton("3D");
		rdbtn3d.setBounds(6, 81, 115, 18);
		panel_3.add(rdbtn3d);
		rdbtn3d.addActionListener(this);

		ButtonGroup l_rbg = new ButtonGroup();
		l_rbg.add(rdbtnColourmap);
		l_rbg.add(rdbtnBase);
		l_rbg.add(rdbtnLasered);
		l_rbg.add(rdbtn3d);
		l_rbg.add(rdbtnDetected);
		
		imagePanel = new PaintImage(false);
		imagePanel.setBounds(156, 0, 460, 633);
		imagePanel.m_cal_data = m_e3d.m_cal_data;
		imagePanel.pos = 0;
		
		glPanel = new PaintImage(false);
		glPanel.setBounds(156, 0, 460, 633);
		glPanel.m_cal_data = m_e3d.m_cal_data;
		glPanel.pos = 0;
		
		imageScrollPane = new JScrollPane(imagePanel);
		imageScrollPane.setBounds(156, 0, 460, 633);
		getContentPane().add(imageScrollPane);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(626, 11, 129, 27);
		getContentPane().add(btnGenerate);
		btnGenerate.addActionListener(this);
		
		JButton btnExport = new JButton("Export");
		btnExport.setBounds(626, 44, 129, 27);
		getContentPane().add(btnExport);
		btnExport.addActionListener(this);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(738, 606, 129, 27);
		getContentPane().add(btnFinish);
		btnFinish.addActionListener(this);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Model Mods", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		panel_1.setBounds(773, 17, 150, 464);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblScaling = new JLabel("Scaling");
		lblScaling.setBounds(6, 26, 60, 15);
		panel_1.add(lblScaling);
		
		sbScaling = new JScrollBar();
		sbScaling.setOrientation(JScrollBar.HORIZONTAL);
		sbScaling.setBounds(6, 45, 122, 27);
		sbScaling.setMinimum(1);
		sbScaling.setMaximum(50000);
		sbScaling.setValue(1000);
		sbScaling.setUnitIncrement(1000);
		panel_1.add(sbScaling);
		sbScaling.addAdjustmentListener(this);
		
		JLabel lblPointSize = new JLabel("Point size");
		lblPointSize.setBounds(6, 74, 60, 15);
		panel_1.add(lblPointSize);
		
		sbPointsize = new JScrollBar();
		sbPointsize.setOrientation(JScrollBar.HORIZONTAL);
		sbPointsize.setBounds(6, 91, 122, 27);
		sbPointsize.setBlockIncrement(10);
		sbPointsize.setUnitIncrement(10);
		sbPointsize.setMinimum(10);
		sbPointsize.setValue(10);
		sbPointsize.setMaximum(80);
		sbPointsize.setVisibleAmount(4);
		panel_1.add(sbPointsize);
		sbPointsize.addAdjustmentListener(this);
		
		JLabel lblZRotOff = new JLabel("Z rot off");
		lblZRotOff.setBounds(6, 123, 60, 15);
		panel_1.add(lblZRotOff);
		
		tfZrotoff = new JTextField();
		tfZrotoff.setText("1400");
		tfZrotoff.setBounds(6, 139, 122, 27);
		panel_1.add(tfZrotoff);
		tfZrotoff.setColumns(10);
		tfZrotoff.addActionListener(this);
		
		JLabel lblXRotOff = new JLabel("X rot off");
		lblXRotOff.setBounds(6, 169, 60, 15);
		panel_1.add(lblXRotOff);
		
		tfXrotoff = new JTextField();
		tfXrotoff.setText("0");
		tfXrotoff.setBounds(6, 186, 122, 27);
		panel_1.add(tfXrotoff);
		tfXrotoff.setColumns(10);
		tfXrotoff.addActionListener(this);
		
		JLabel lblYDisplayOffset = new JLabel("Y object offset");
		lblYDisplayOffset.setBounds(6, 214, 122, 15);
		panel_1.add(lblYDisplayOffset);
		
		tfYModelOffset = new JTextField();
		tfYModelOffset.setText("500");
		tfYModelOffset.setBounds(6, 228, 122, 27);
		panel_1.add(tfYModelOffset);
		tfYModelOffset.setColumns(10);
		tfYModelOffset.addActionListener(this);
		
		JLabel lblNewLabel_2 = new JLabel("Rotation modifier");
		lblNewLabel_2.setBounds(11, 413, 117, 16);
		panel_1.add(lblNewLabel_2);
		
		tfRotationModifier = new JTextField();
		tfRotationModifier.setText("0");
		tfRotationModifier.setBounds(6, 430, 122, 28);
		panel_1.add(tfRotationModifier);
		tfRotationModifier.setColumns(10);
		tfRotationModifier.addActionListener(this);
		
/*		JLabel lblTtRot = new JLabel("tt rot");
		lblTtRot.setBounds(6, 292, 60, 15);
		panel_1.add(lblTtRot);
		
		tfTTrot = new JTextField();
		tfTTrot.setText("0.0");
		tfTTrot.setBounds(6, 319, 122, 27);
		panel_1.add(tfTTrot);
		tfTTrot.setColumns(10);
		tfTTrot.addActionListener(this);*/
		
		
		
		JButton btnExportMerged = new JButton("Export merged");
		btnExportMerged.setBounds(626, 78, 129, 27);
		getContentPane().add(btnExportMerged);
		btnExportMerged.addActionListener(this);
		
		JButton btnConfig = new JButton("Config");
		btnConfig.setBounds(628, 606, 100, 27);
		getContentPane().add(btnConfig);
		btnConfig.addActionListener(this);
		
		JButton btnImport = new JButton("Import");
		btnImport.setBounds(626, 116, 129, 27);
		getContentPane().add(btnImport);
		btnImport.addActionListener(this);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(626, 181, 150, 300);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblLeft = new JLabel("Left");
		lblLeft.setBounds(6, 22, 60, 15);
		panel_2.add(lblLeft);
		
		tfLeftFilter = new JTextField();
		tfLeftFilter.setBounds(6, 38, 122, 27);
		panel_2.add(tfLeftFilter);
		tfLeftFilter.setColumns(10);
		tfLeftFilter.addActionListener(this);
		
		JLabel lblRight = new JLabel("Right");
		lblRight.setBounds(6, 67, 60, 15);
		panel_2.add(lblRight);
		
		tfRightFilter = new JTextField();
		tfRightFilter.setColumns(10);
		tfRightFilter.setBounds(6, 83, 122, 27);
		panel_2.add(tfRightFilter);
		tfRightFilter.addActionListener(this);
		
		JLabel lblTop = new JLabel("Top");
		lblTop.setBounds(6, 114, 60, 15);
		panel_2.add(lblTop);
		
		tfTopFilter = new JTextField();
		tfTopFilter.setColumns(10);
		tfTopFilter.setBounds(6, 130, 122, 27);
		panel_2.add(tfTopFilter);
		tfTopFilter.addActionListener(this);
		
		JLabel lblBottom = new JLabel("Bottom");
		lblBottom.setBounds(6, 159, 60, 15);
		panel_2.add(lblBottom);
		
		tfBottomFilter = new JTextField();
		tfBottomFilter.setColumns(10);
		tfBottomFilter.setBounds(6, 175, 122, 27);
		panel_2.add(tfBottomFilter);
		tfBottomFilter.addActionListener(this);
		
		JLabel lblFront = new JLabel("Front");
		lblFront.setBounds(6, 198, 60, 15);
		panel_2.add(lblFront);
		
		tfFrontFilter = new JTextField();
		tfFrontFilter.setColumns(10);
		tfFrontFilter.setBounds(6, 214, 122, 27);
		panel_2.add(tfFrontFilter);
		tfFrontFilter.addActionListener(this);
		
		tfBackFilter = new JTextField();
		tfBackFilter.setColumns(10);
		tfBackFilter.setBounds(6, 263, 122, 27);
		panel_2.add(tfBackFilter);
		tfBackFilter.addActionListener(this);
		
		JLabel lblBack = new JLabel("Back");
		lblBack.setBounds(6, 247, 60, 15);
		panel_2.add(lblBack);
		
		cbKeepDetectedPoints = new JCheckBox("Keep detected points");
		cbKeepDetectedPoints.setBounds(628, 487, 138, 18);
		cbKeepDetectedPoints.setSelected(true);
		getContentPane().add(cbKeepDetectedPoints);
		
		JButton btnReCalculate3D = new JButton("ReCalculate 3D");
		btnReCalculate3D.setBounds(628, 509, 127, 28);
		getContentPane().add(btnReCalculate3D);
		btnReCalculate3D.addActionListener(this);
		
		JButton btnSave2DPoints = new JButton("Save2DPoints");
		btnSave2DPoints.setBounds(628, 537, 127, 28);
		getContentPane().add(btnSave2DPoints);
		btnSave2DPoints.addActionListener(this);
		
		JButton btnLoad2DPoints = new JButton("Load2DPoints");
		btnLoad2DPoints.setBounds(628, 566, 127, 28);
		getContentPane().add(btnLoad2DPoints);
		btnLoad2DPoints.addActionListener(this);
		
		chckbxTurntableScan = new JCheckBox("Turntable scan");
		chckbxTurntableScan.setBounds(628, 155, 115, 18);
		getContentPane().add(chckbxTurntableScan);

		JPanel panel_lc = new JPanel();
		panel_lc.setBorder(new TitledBorder(null, "Layer control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_lc.setBounds(923, 17, 302, 464);
		getContentPane().add(panel_lc);
		panel_lc.setLayout(null);

		// Header line
		JLabel lblLayerHeader = new JLabel("L");
		lblLayerHeader.setBounds(17, 22, 20, 20);
		lblLayerHeader.setOpaque(true);
		lblLayerHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblLayerHeader);
		
		JLabel lblVisibleHeader= new JLabel("V");
		lblVisibleHeader.setBounds(17+20+5, 22, 20, 20);
		lblVisibleHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblVisibleHeader);

		JLabel lblColouredHeader= new JLabel("C");
		lblColouredHeader.setBounds(17+20+4+20, 22, 20, 20);
		lblColouredHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblColouredHeader);

		JLabel lblColouredRHeader= new JLabel("R");
		lblColouredRHeader.setBounds(17+20+4+20+24, 22, 20, 20);
		lblColouredRHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblColouredRHeader);

		JLabel lblColouredGHeader= new JLabel("G");
		lblColouredGHeader.setBounds(17+20+4+20+24+34, 22, 20, 20);
		lblColouredGHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblColouredGHeader);

		JLabel lblColouredBHeader= new JLabel("B");
		lblColouredBHeader.setBounds(17+20+4+20+24+34+34, 22, 20, 20);
		lblColouredBHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblColouredBHeader);

		JLabel lblZRotHeader= new JLabel("Z");
		lblZRotHeader.setBounds(17+20+4+20+24+34+34+34, 22, 20, 20);
		lblZRotHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblZRotHeader);

		JLabel lblXRotHeader= new JLabel("X");
		lblXRotHeader.setBounds(17+20+4+20+24+34+34+34+34, 22, 20, 20);
		lblXRotHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblXRotHeader);

		JLabel lblRotOffHeader= new JLabel("Rot");
		lblRotOffHeader.setBounds(17+20+4+20+24+34+34+34+34+32, 22, 20, 20);
		lblRotOffHeader.setBackground(new Color(220, 220, 220));
		panel_lc.add(lblRotOffHeader);

		m_layer_visible = new ArrayList<JCheckBox>();
		m_layer_coloured = new ArrayList<JCheckBox>();
		m_z_off = new ArrayList<JTextField>();
		m_x_off = new ArrayList<JTextField>();
		m_rot_off = new ArrayList<JTextField>();
		m_red = new ArrayList<JTextField>();
		m_green = new ArrayList<JTextField>();
		m_blue = new ArrayList<JTextField>();
		for(int l_layer = 0; l_layer < 20; ++l_layer)
		{
			int x = 17;
			JLabel lblLayerNumber = new JLabel(""+(l_layer+1));
			lblLayerNumber.setBounds(x, 44+l_layer*20, 20, 20);
			x+=20;
			lblLayerNumber.setOpaque(true);
			if((l_layer%2)==0) lblLayerNumber.setBackground(new Color(180, 180, 180));
			else lblLayerNumber.setBackground(new Color(200, 200, 200));
			panel_lc.add(lblLayerNumber);
			
			JCheckBox l_visible_chkb = new JCheckBox();
			l_visible_chkb.setSelected(true);
			m_layer_visible.add(l_visible_chkb);
			l_visible_chkb.setBounds(x, 44+l_layer*20, 20, 20);
			x+=20;
			if((l_layer%2)==0) l_visible_chkb.setBackground(new Color(180, 180, 180));
			else l_visible_chkb.setBackground(new Color(200, 200, 200));
			panel_lc.add(l_visible_chkb);
			l_visible_chkb.addActionListener(this);

			JCheckBox l_coloured_chkb = new JCheckBox();
			m_layer_coloured.add(l_coloured_chkb);
			l_coloured_chkb.setBounds(x, 44+l_layer*20, 20, 20);
			x+=20;
			if((l_layer%2)==0) l_coloured_chkb.setBackground(new Color(180, 180, 180));
			else l_coloured_chkb.setBackground(new Color(200, 200, 200));
			panel_lc.add(l_coloured_chkb);
			l_coloured_chkb.addActionListener(this);

			JTextField l_red = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getRed());
			m_red.add(l_red);
			l_red.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_red);
			l_red.addActionListener(this);
		
			JTextField l_green = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getGreen());
			m_green.add(l_green);
			l_green.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_green);
			l_green.addActionListener(this);
		
			JTextField l_blue = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getBlue());
			m_blue.add(l_blue);
			l_blue.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_blue);
			l_blue.addActionListener(this);
		
			JTextField l_z_off = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_z_off.get(l_layer));
			m_z_off.add(l_z_off);
			l_z_off.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_z_off);
			l_z_off.addActionListener(this);
		
			JTextField l_x_off = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_x_off.get(l_layer));
			m_x_off.add(l_x_off);
			l_x_off.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_x_off);
			l_x_off.addActionListener(this);
		
			JTextField l_rot_off = new JTextField(""+Eora3D_MainWindow.m_e3d_config.m_layer_rot_off.get(l_layer));
			m_rot_off.add(l_rot_off);
			l_rot_off.setBounds(x, 44+l_layer*20, 34, 22);
			x+=34;
			panel_lc.add(l_rot_off);
			l_rot_off.addActionListener(this);
		
		}
		
		JLabel lblNewLabel_3 = new JLabel("Object View Y Rotation");
		lblNewLabel_3.setBounds(783, 488, 140, 16);
		getContentPane().add(lblNewLabel_3);

		sbViewYRotation = new JScrollBar();
		sbViewYRotation.setOrientation(JScrollBar.HORIZONTAL);
		sbViewYRotation.setBounds(783, 509, 442, 27);
		getContentPane().add(sbViewYRotation);
		sbViewYRotation.setMinimum(0);
		sbViewYRotation.setMaximum(360);
		sbViewYRotation.setValue(180);
		sbViewYRotation.setUnitIncrement(1);
		sbViewYRotation.addAdjustmentListener(this);
		
		JLabel lblNewLabel_1 = new JLabel("Object View X Rotation");
		lblNewLabel_1.setBounds(783, 543, 129, 16);
		getContentPane().add(lblNewLabel_1);
		
		sbViewXRotation = new JScrollBar();
		sbViewXRotation.setOrientation(JScrollBar.HORIZONTAL);
		sbViewXRotation.setBounds(783, 566, 442, 27);
		getContentPane().add(sbViewXRotation);
		sbViewXRotation.setMinimum(0);
		sbViewXRotation.setMaximum(360);
		sbViewXRotation.setValue(180);
		sbViewXRotation.setUnitIncrement(2);
		sbViewXRotation.addAdjustmentListener(this);

		JLabel lblXFocalMod = new JLabel("UX");
		lblXFocalMod.setBounds(6, 257, 42, 16);
		panel_1.add(lblXFocalMod);
		
		tfUX = new JTextField();
		tfUX.setText("1.0");
		tfUX.setBounds(6, 277, 60, 28);
		panel_1.add(tfUX);
		tfUX.setColumns(10);
		
		JLabel lblYFocalMod = new JLabel("UZ");
		lblYFocalMod.setBounds(70, 257, 58, 16);
		panel_1.add(lblYFocalMod);
		
		tfUZ = new JTextField();
		tfUZ.setText("0.0");
		tfUZ.setBounds(68, 277, 60, 28);
		panel_1.add(tfUZ);
		tfUZ.setColumns(10);

		JLabel lblNewLabel = new JLabel("VX");
		lblNewLabel.setBounds(11, 306, 42, 16);
		panel_1.add(lblNewLabel);
		
		JLabel lblNewLabel_4 = new JLabel("VZ");
		lblNewLabel_4.setBounds(70, 306, 55, 16);
		panel_1.add(lblNewLabel_4);
		
		tfVX = new JTextField();
		tfVX.setText("0.0");
		tfVX.setBounds(6, 330, 60, 28);
		panel_1.add(tfVX);
		tfVX.setColumns(10);
		
		tfVZ = new JTextField();
		tfVZ.setText("1.0");
		tfVZ.setBounds(70, 330, 60, 28);
		panel_1.add(tfVZ);
		tfVZ.setColumns(10);

		cbStopRotation = new JCheckBox("Stop auto rotation");
		cbStopRotation.setBounds(1039, 487, 129, 18);
		getContentPane().add(cbStopRotation);
		cbStopRotation.addActionListener(this);
		
		lblOVYR = new JLabel("180");
		lblOVYR.setBounds(919, 488, 55, 16);
		getContentPane().add(lblOVYR);
		
		lblOVXR = new JLabel("90");
		lblOVXR.setBounds(919, 543, 55, 16);
		getContentPane().add(lblOVXR);
		
		m_pco = new PointCloudObject(glPanel);
		m_pco.m_finished = true;
		m_pco.m_Pointsize = sbPointsize.getValue();
		m_pco.m_Scale = sbScaling.getValue();
		m_pco.m_layer_coloured = Eora3D_MainWindow.m_e3d_config.m_layer_coloured;
		m_pco.m_layer_colour = Eora3D_MainWindow.m_e3d_config.m_layer_colour;
		m_pco.m_layer_z_off = Eora3D_MainWindow.m_e3d_config.m_layer_z_off;
		m_pco.m_layer_x_off = Eora3D_MainWindow.m_e3d_config.m_layer_x_off;
		m_pco.m_layer_rot_off = Eora3D_MainWindow.m_e3d_config.m_layer_rot_off;

		m_pco_thread = new Thread(m_pco);
		m_pco_thread.start();
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation==90)
		{
			m_e3d.m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_e3d.m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		else
		{
			m_e3d.m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_e3d.m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		m_e3d.m_cal_data.calculate();
		m_e3d.m_cal_data.calculateBaseCoords();
		setFromConfig();
		m_config_verify = false;
		addWindowListener(this);
		
		m_ControlSetState = new controlSetState();
		m_ControlSetState.addControl(btnReCalculate3D);
		m_ControlSetState.addControl(btnReCalculate3D);
		m_ControlSetState.addControl(chckbxTurntableScan);

		m_ControlSetState.addControl(panel);
		m_ControlSetState.addControl(panel_1);
		
		JLabel lblWx = new JLabel("WX");
		lblWx.setBounds(11, 360, 42, 16);
		panel_1.add(lblWx);
		
		JLabel lblWz = new JLabel("WZ");
		lblWz.setBounds(70, 360, 42, 16);
		panel_1.add(lblWz);
		
		tfWX = new JTextField();
		tfWX.setText("0.0");
		tfWX.setColumns(10);
		tfWX.setBounds(6, 373, 60, 28);
		panel_1.add(tfWX);
		
		tfWZ = new JTextField();
		tfWZ.setText("0.0");
		tfWZ.setColumns(10);
		tfWZ.setBounds(68, 373, 60, 28);
		panel_1.add(tfWZ);
		
		m_ControlSetState.addControl(panel_2);
		m_ControlSetState.addControl(panel_3);
		
		m_ControlSetState.addControl(btnGenerate);
		m_ControlSetState.addControl(btnExport);
		m_ControlSetState.addControl(btnFinish);

		
		// Group 2: During sub thread
		m_ControlSetState.addControlOff(2, btnReCalculate3D);
		m_ControlSetState.addControlOff(2, panel);
//		m_ControlSetState.addControlOff(2, panel_1);
		m_ControlSetState.addControlOff(2, panel_2);
//		m_ControlSetState.addControlOff(2, panel_3);

		m_ControlSetState.addControlOff(2, chckbxTurntableScan);
		m_ControlSetState.addControlOff(2, btnGenerate);
		m_ControlSetState.addControlOff(2, btnExport);
		m_ControlSetState.addControlOff(2, btnFinish);
		
	}

	void setFromConfig()
	{
		tfRedthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r);
		tfGreenthreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g);
		tfBluethreshold.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b);		
		tfPercentagechange.setText(""+Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent);
		
		tfLeftFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_leftfilter);
		tfRightFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_rightfilter);
		tfTopFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_topfilter);
		tfBottomFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_bottomfilter);
		tfFrontFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_frontfilter);
		tfBackFilter.setText(""+Eora3D_MainWindow.m_e3d_config.sm_backfilter);
		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("Or"))
		{
			cbDetectionmethod.setSelectedIndex(0);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("And"))
		{
			cbDetectionmethod.setSelectedIndex(1);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("%"))
		{
			cbDetectionmethod.setSelectedIndex(2);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("Weighted %"))
		{
			cbDetectionmethod.setSelectedIndex(3);
		}
		else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.equals("No base peak threshold"))
		{
			cbDetectionmethod.setSelectedIndex(4);
		}
		tfTestrotation.setText(""+Eora3D_MainWindow.m_e3d_config.sm_test_rotation);
		tfTestframe.setText(""+Eora3D_MainWindow.m_e3d_config.sm_test_frame);
		tfMinpointsperlaser.setText(""+Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser);
		tfMaxPointsPerLine.setText(""+Eora3D_MainWindow.m_e3d_config.sm_max_points_per_line);
		chckbxTurntableScan.setSelected(Eora3D_MainWindow.m_e3d_config.sm_turntable_scan);
		
		for(int l_layer = 0; l_layer < 20; ++ l_layer)
		{
			m_layer_coloured.get(l_layer).setSelected(Eora3D_MainWindow.m_e3d_config.m_layer_coloured.get(l_layer));
			m_red.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getRed());
			m_green.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getGreen());
			m_blue.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_colour.get(l_layer).getBlue());

			m_z_off.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_z_off.get(l_layer));
			m_x_off.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_x_off.get(l_layer));
			m_rot_off.get(l_layer).setText(""+Eora3D_MainWindow.m_e3d_config.m_layer_rot_off.get(l_layer));
		}

		m_pco.m_layer_coloured = Eora3D_MainWindow.m_e3d_config.m_layer_coloured;
		m_pco.m_layer_colour = Eora3D_MainWindow.m_e3d_config.m_layer_colour;
		m_pco.m_layer_z_off = Eora3D_MainWindow.m_e3d_config.m_layer_z_off;
		m_pco.m_layer_x_off = Eora3D_MainWindow.m_e3d_config.m_layer_x_off;
		m_pco.m_layer_rot_off = Eora3D_MainWindow.m_e3d_config.m_layer_rot_off;
	}
	
	void putToConfig()
	{
		if(m_config_verify) return;
		Eora3D_MainWindow.m_e3d_config.sm_test_rotation = Integer.parseInt(tfTestrotation.getText());
		Eora3D_MainWindow.m_e3d_config.sm_test_frame = Integer.parseInt(tfTestframe.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r = Integer.parseInt(tfRedthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g = Integer.parseInt(tfGreenthreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b = Integer.parseInt(tfBluethreshold.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent = Float.parseFloat(tfPercentagechange.getText());
		Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic = (String)cbDetectionmethod.getSelectedItem();
		Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser = Integer.parseInt(tfMinpointsperlaser.getText());
		Eora3D_MainWindow.m_e3d_config.sm_max_points_per_line = Integer.parseInt(tfMaxPointsPerLine.getText());
		
		Eora3D_MainWindow.m_e3d_config.sm_leftfilter = Integer.parseInt(tfLeftFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_rightfilter = Integer.parseInt(tfRightFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_topfilter = Integer.parseInt(tfTopFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_bottomfilter = Integer.parseInt(tfBottomFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_frontfilter = Integer.parseInt(tfFrontFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_backfilter = Integer.parseInt(tfBackFilter.getText());
		Eora3D_MainWindow.m_e3d_config.sm_turntable_scan = chckbxTurntableScan.isSelected();

		for(int l_layer = 0; l_layer < 20; ++ l_layer)
		{
			Eora3D_MainWindow.m_e3d_config.m_layer_coloured.set(l_layer, new Boolean(m_layer_coloured.get(l_layer).isSelected()));
			Eora3D_MainWindow.m_e3d_config.m_layer_colour.set(l_layer, new Color(
					Integer.parseInt(m_red.get(l_layer).getText()),
					Integer.parseInt(m_green.get(l_layer).getText()),
					Integer.parseInt(m_blue.get(l_layer).getText())) );
			Eora3D_MainWindow.m_e3d_config.m_layer_z_off.set(l_layer, Integer.parseInt(m_z_off.get(l_layer).getText()));
			Eora3D_MainWindow.m_e3d_config.m_layer_x_off.set(l_layer, Integer.parseInt(m_x_off.get(l_layer).getText()));
			Eora3D_MainWindow.m_e3d_config.m_layer_rot_off.set(l_layer, Integer.parseInt(m_rot_off.get(l_layer).getText()));
		}

		m_config_verify = true;
		Eora3D_MainWindow.m_e3d_config.verify();
		setFromConfig();
		m_config_verify = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
//		System.out.println(ae.getActionCommand());
		if(m_block_updates) return;
	    putToConfig();
		for(int l_layer = 0; l_layer < 20; ++ l_layer)
		{
			m_pco.m_layer_visible.set(l_layer, new Boolean(m_layer_visible.get(l_layer).isSelected()));
		}
		if(ae.getActionCommand() == "Generate")
		{
			if(m_detect_thread==null)
			{
				m_ControlSetState.setState(2); validate(); repaint();
				getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Runnable l_runnable = () -> {
					Detect(-1, -1, false);
					getContentPane().setCursor(Cursor.getDefaultCursor());
					m_ControlSetState.setState(0); validate(); repaint();
				};
				m_detect_thread = new Thread(l_runnable);
				m_detect_thread.start();
			}
		} else
		if(ae.getActionCommand() == "Export")
		{
			/*
			JFileChooser l_fc;
			l_fc = new JFileChooser();
			l_fc.setSelectedFile(new File("export.ply"));
			l_fc.setFileFilter(new extensionFileFilter("ply"));
			int l_ret = l_fc.showSaveDialog(this);

			if (l_ret == JFileChooser.APPROVE_OPTION) {
				File l_file = l_fc.getSelectedFile();
				if(!l_file.toString().contains("."))
				{
					l_file = new File(l_file.toString()+".ply");
				}
				
				m_pco.save(l_file);
			}*/
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			m_pco.setTT((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()),
					Integer.parseInt(tfZrotoff.getText()),
					Integer.parseInt(tfXrotoff.getText()),
					Integer.parseInt(tfYModelOffset.getText()),
					Eora3D_MainWindow.m_e3d_config.sm_leftfilter,
					Eora3D_MainWindow.m_e3d_config.sm_rightfilter,
					Eora3D_MainWindow.m_e3d_config.sm_topfilter,
					Eora3D_MainWindow.m_e3d_config.sm_bottomfilter,
					Eora3D_MainWindow.m_e3d_config.sm_frontfilter,
					Eora3D_MainWindow.m_e3d_config.sm_backfilter);
			for(int l_layer = 0; l_layer < 20; ++ l_layer)
			{
//				if(m_pco.m_layer_visible.get(l_layer) == null)
//					m_pco.m_layer_visible.add(new Boolean(true));
				m_pco.m_layer_visible.set(l_layer, new Boolean(m_layer_visible.get(l_layer).isSelected()));
				Eora3D_MainWindow.m_e3d_config.m_layer_coloured.set(l_layer, new Boolean(m_layer_coloured.get(l_layer).isSelected()));
			}
			m_pco.m_pix_to_mm = m_e3d.m_cal_data.m_pix_to_mm;
			File l_file = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"export");
			m_pco.save(l_file);
			getContentPane().setCursor(Cursor.getDefaultCursor());
			
		} else
		if(ae.getActionCommand() == "Export merged")
		{
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			File l_file = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"export");
			m_pco.setTT((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()),
					Integer.parseInt(tfZrotoff.getText()),
					Integer.parseInt(tfXrotoff.getText()),
					Integer.parseInt(tfYModelOffset.getText()),
					Eora3D_MainWindow.m_e3d_config.sm_leftfilter,
					Eora3D_MainWindow.m_e3d_config.sm_rightfilter,
					Eora3D_MainWindow.m_e3d_config.sm_topfilter,
					Eora3D_MainWindow.m_e3d_config.sm_bottomfilter,
					Eora3D_MainWindow.m_e3d_config.sm_frontfilter,
					Eora3D_MainWindow.m_e3d_config.sm_backfilter);
			for(int l_layer = 0; l_layer < 20; ++ l_layer)
			{
//				if(m_pco.m_layer_visible.get(l_layer) == null)
//					m_pco.m_layer_visible.add(new Boolean(true));
				m_pco.m_layer_visible.set(l_layer, new Boolean(m_layer_visible.get(l_layer).isSelected()));
				Eora3D_MainWindow.m_e3d_config.m_layer_coloured.set(l_layer, new Boolean(m_layer_coloured.get(l_layer).isSelected()));
			}
			m_pco.m_pix_to_mm = m_e3d.m_cal_data.m_pix_to_mm;
			m_pco.save_apply_offsets(l_file);
			getContentPane().setCursor(Cursor.getDefaultCursor());
		} else
		if(ae.getActionCommand() == "Import")
		{
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			File l_file = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"export");
			m_pco.load(l_file, Eora3D_MainWindow.m_e3d_config.sm_turntable_steps_per_rotation);

			DataOutputStream l_dos = null;
		    DataInputStream l_dis = null;
		    if(!m_e3d.m_is_windows10)
		    {
				try {
					//m_socket = new Socket(InetAddress.getLoopbackAddress(), 7778);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					m_socket = null;
				}
			}
			if(m_socket!=null)
			{
				try {
					l_dos = new DataOutputStream(m_socket.getOutputStream());
					l_dis = new DataInputStream(m_socket.getInputStream());
					l_dos.flush();
					l_dos.writeInt(0);
					l_dos.writeInt(1);
				    l_dos.writeFloat(m_pco.m_Scale);
				    l_dos.writeFloat(m_pco.m_Pointsize);
				    for(int j=0; j<m_pco.m_points.size(); ++j)
				    {
			        	for(int i=0; i<m_pco.m_points.get(j).size(); ++i)
				        {
				        	try {
				        		l_dos.writeInt(2);
				        		l_dos.writeInt(j);
				        		m_pco.m_points.get(j).get(i).write(l_dos);
								l_dos.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								m_socket = null;
							}
						}
		    			System.out.println("Sent layer "+j);
				    }
	    			try {
	    				l_dos.writeInt(3);
	    				l_dis.readInt();
	    			} catch (Exception e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    				m_socket = null;
		    		}
					System.out.println("Sent points");
					l_dos.close();
					l_dis.close();
					m_socket.close();
					m_socket = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					m_socket = null;
				}
			}
			getContentPane().setCursor(Cursor.getDefaultCursor());
		} else
		if(ae.getActionCommand() == "Finish")
		{
			System.out.println("Closing pco");
			m_pco.Close();
			this.setVisible(false);
		} else
		if(ae.getActionCommand() == "Test")
		{
			//imageScrollPane.setViewportView(imagePanel);
			m_e3d.m_cal_data.calculate();
			m_e3d.m_cal_data.calculateBaseCoords();

			Eora3D_MainWindow.m_e3d_config.sm_test_rotation= Integer.parseInt(tfTestrotation.getText());
			Eora3D_MainWindow.m_e3d_config.sm_test_frame = Integer.parseInt(tfTestframe.getText());
			Detect(Eora3D_MainWindow.m_e3d_config.sm_test_frame, Eora3D_MainWindow.m_e3d_config.sm_test_rotation, true);
		} else
		if(ae.getSource().equals(rdbtnColourmap) && rdbtnColourmap.isSelected())
		{
			imageScrollPane.setViewportView(imagePanel);
			File l_infile;
			String l_tt = "";
			if(Eora3D_MainWindow.m_e3d_config.sm_turntable_scan) l_tt="tt"+Eora3D_MainWindow.m_e3d_config.sm_test_rotation+"_";
			try {
				l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+"colourmap.png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_detect_thread=null;
				return;
			}
			if(!l_infile.exists()) return;
//				System.out.println("Analysing "+l_infile.toString());
			BufferedImage l_inimage;

			try {
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				e1.printStackTrace();
				m_detect_thread=null;
				return;
			}
			imagePanel.m_overlay = null;
			imagePanel.m_image = l_inimage;
			imagePanel.repaint();
		}
		else
		if(ae.getSource().equals(rdbtnBase) && rdbtnBase.isSelected())
		{
			imageScrollPane.setViewportView(imagePanel);
			File l_infile;
			String l_tt = "";
			if(Eora3D_MainWindow.m_e3d_config.sm_turntable_scan) l_tt="tt"+Eora3D_MainWindow.m_e3d_config.sm_test_rotation+"_";
			try {
				l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+"base.png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_detect_thread=null;
				return;
			}
			if(!l_infile.exists()) return;
//			System.out.println("Analysing "+l_infile.toString());
			BufferedImage l_inimage;

			try {
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				e1.printStackTrace();
				m_detect_thread=null;
				return;
			}
			imagePanel.m_overlay = null;
			imagePanel.m_image = l_inimage;
			imagePanel.repaint();
		}
		else
		if(ae.getSource().equals(rdbtnLasered) && rdbtnLasered.isSelected())
		{
			imageScrollPane.setViewportView(imagePanel);
			Eora3D_MainWindow.m_e3d_config.sm_test_frame = Integer.parseInt(tfTestframe.getText());
			File l_infile;
			String l_tt = "";
			if(Eora3D_MainWindow.m_e3d_config.sm_turntable_scan) l_tt="tt"+Eora3D_MainWindow.m_e3d_config.sm_test_rotation+"_";
			try {
				l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+Eora3D_MainWindow.m_e3d_config.sm_test_frame+".png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_detect_thread=null;
				return;
			}
			if(!l_infile.exists()) return;
//				System.out.println("Analysing "+l_infile.toString());
			BufferedImage l_inimage;

			try {
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				e1.printStackTrace();
				m_detect_thread=null;
				return;
			}
			imagePanel.m_overlay = l_inimage;
			imagePanel.repaint();
		}
		else
		if(ae.getSource().equals(rdbtn3d) && rdbtn3d.isSelected())
		{
			imageScrollPane.setViewportView(glPanel);
		}
		else
		if(ae.getSource().equals(rdbtnDetected) && rdbtnDetected.isSelected())
		{
			imagePanel.m_overlay = m_test_image;
			imagePanel.m_image = null;
			imageScrollPane.setViewportView(imagePanel);
			imagePanel.repaint();
		}
		else
		if(ae.getSource().equals(cbDetectionmethod))
		{
			switch(cbDetectionmethod.getSelectedIndex())
			{
				case 0:
				case 1:
				case 4:
					System.out.println("Or/And/Thresholdnobase");
					tfRedthreshold.setEnabled(true);
					tfGreenthreshold.setEnabled(true);
					tfBluethreshold.setEnabled(true);
					tfPercentagechange.setEnabled(false);
					break;
				case 2:
				case 3:
					System.out.println("%/Weighted %");
					tfRedthreshold.setEnabled(false);
					tfGreenthreshold.setEnabled(false);
					tfBluethreshold.setEnabled(false);
					tfPercentagechange.setEnabled(true);
					break;
			}
		}
		else
		if(ae.getActionCommand()=="Config")
		{
			putToConfig();
			eora3D_configuration_editor l_editor = new eora3D_configuration_editor(m_e3d);
			l_editor.setVisible(true);
			m_block_updates = true;
			setFromConfig();
			m_block_updates = false;
		} else
		if(ae.getSource().equals(tfXrotoff) || ae.getSource().equals(tfZrotoff)/* || ae.getSource().equals(tfTTrot)*/
				|| ae.getSource().equals(tfLeftFilter)
				|| ae.getSource().equals(tfRightFilter)
				|| ae.getSource().equals(tfBottomFilter)
				|| ae.getSource().equals(tfTopFilter)
				|| ae.getSource().equals(tfFrontFilter)
				|| ae.getSource().equals(tfBackFilter)
				|| ae.getSource().equals(tfYModelOffset)
				|| ae.getSource().equals(cbStopRotation)
				|| ae.getSource().equals(tfRotationModifier)
				)
		{
			if(m_e3d.m_is_windows10 || m_detect_thread == null)
			{
			    DataOutputStream l_dos = null;
			    
			    putToConfig();
			    
			    m_pco.m_stoprotation = cbStopRotation.isSelected();
			    
			    if(!m_e3d.m_is_windows10) try {
				//	m_socket = new Socket(InetAddress.getLoopbackAddress(), 7778);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					m_socket = null;
				}
				m_pco.setTT((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()),
						Integer.parseInt(tfZrotoff.getText()),
						Integer.parseInt(tfXrotoff.getText()),
						Integer.parseInt(tfYModelOffset.getText()),
						Eora3D_MainWindow.m_e3d_config.sm_leftfilter,
						Eora3D_MainWindow.m_e3d_config.sm_rightfilter,
						Eora3D_MainWindow.m_e3d_config.sm_topfilter,
						Eora3D_MainWindow.m_e3d_config.sm_bottomfilter,
						Eora3D_MainWindow.m_e3d_config.sm_frontfilter,
						Eora3D_MainWindow.m_e3d_config.sm_backfilter);
				for(int l_layer = 0; l_layer < 20; ++ l_layer)
				{
//					if(m_pco.m_layer_visible.get(l_layer) == null)
//						m_pco.m_layer_visible.add(new Boolean(true));
					m_pco.m_layer_visible.set(l_layer, new Boolean(m_layer_visible.get(l_layer).isSelected()));
					Eora3D_MainWindow.m_e3d_config.m_layer_coloured.set(l_layer, new Boolean(m_layer_coloured.get(l_layer).isSelected()));
				}
				if(m_socket!=null)
				{
					try {
						l_dos = new DataOutputStream(m_socket.getOutputStream());
						l_dos.flush();
			    		l_dos.writeInt(4);
			    		l_dos.writeFloat((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()));
//			    		l_dos.writeFloat((float)Float.parseFloat(tfTTrot.getText()));
			    		l_dos.writeInt(Integer.parseInt(tfZrotoff.getText()));
			    		l_dos.writeInt(Integer.parseInt(tfXrotoff.getText()));
			    		l_dos.writeInt(Integer.parseInt(tfYModelOffset.getText()));
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_leftfilter);
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_rightfilter);
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_topfilter);
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_bottomfilter);
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_frontfilter);
			    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_backfilter);
					    l_dos.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						try
						{
							l_dos.close();
							m_socket.close();
						}
						catch(Exception z)
						{
						
						}
						m_socket = null;
					}
				}
				if(m_socket!=null)
				{
					try
					{
						l_dos.close();
						m_socket.close();
						m_socket = null;
					} catch(Exception e2)
					{
						e2.printStackTrace();
						m_socket = null;
						return;
					}
				}
				System.out.println("Done");
			}
		} else
		if(ae.getActionCommand()=="ReCalculate 3D")
		{
			m_ControlSetState.setState(2); validate(); repaint();
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			reCalculate();
			getContentPane().setCursor(Cursor.getDefaultCursor());
			m_ControlSetState.setState(0); validate(); repaint();
		}
		else
		if(ae.getActionCommand()=="Save2DPoints")
		{
			JFileChooser l_fc;
			l_fc = new JFileChooser(System.getProperty("user.home"));
			l_fc.setSelectedFile(new File("detected.2dp"));
			l_fc.setFileFilter(new extensionFileFilter("2dp"));
			l_fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int l_ret = l_fc.showSaveDialog(this);
			if (l_ret == JFileChooser.APPROVE_OPTION)
			{
				File l_file = l_fc.getSelectedFile();
				if(!l_file.toString().contains("."))
				{
					l_file = new File(l_file.toString()+".2dp");
				}
				try {
					FileOutputStream l_fos = new FileOutputStream(l_file);
					ObjectOutputStream l_oos = new ObjectOutputStream(l_fos);
					getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					synchronized(m_raw_points)
					{
						Integer l_val = new Integer(m_raw_points.size());
						l_oos.writeObject(l_val);
						for(ArrayList<RawPoint> l_rpal : m_raw_points)
						{
							l_val = new Integer(l_rpal.size());
							l_oos.writeObject(l_val);
							for(RawPoint l_rp : l_rpal)
							{
								l_oos.writeObject(l_rp);
							}
						}
					}
					System.gc();
					getContentPane().setCursor(Cursor.getDefaultCursor());
					l_oos.close();
					l_fos.close();
					JOptionPane.showMessageDialog(getContentPane(), "Ok", "Save 2D Points", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					JOptionPane.showMessageDialog(getContentPane(), "Failed", "Save 2D Points", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else
		if(ae.getActionCommand()=="Load2DPoints")
		{
			JFileChooser l_fc;
			l_fc = new JFileChooser(System.getProperty("user.home"));
			l_fc.setFileFilter(new extensionFileFilter("2dp"));
			l_fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			l_fc.setSelectedFile(new File("detected.2dp"));
			int l_ret = l_fc.showOpenDialog(null);

			if (l_ret == JFileChooser.APPROVE_OPTION) {
				File l_file = l_fc.getSelectedFile();
				try {
					FileInputStream l_fis = new FileInputStream(l_file);
					ObjectInputStream l_ois = new ObjectInputStream(l_fis);
					m_raw_points = new ArrayList<>() ;
					getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					synchronized(m_raw_points)
					{
						Integer l_layers = (Integer)l_ois.readObject();
						for(int i = 0; i < l_layers; ++ i)
						{
							ArrayList<RawPoint> l_rpal = new ArrayList<RawPoint>();
							m_raw_points.add(l_rpal);
							Integer l_points = (Integer)l_ois.readObject();
							for(int j = 0; j < l_points; ++j)
							{
								RawPoint l_rp = (RawPoint)l_ois.readObject();
								l_rpal.add(l_rp);
							}
						}
					}
					System.gc();
					l_ois.close();
					l_fis.close();
					reCalculate();
					getContentPane().setCursor(Cursor.getDefaultCursor());
					JOptionPane.showMessageDialog(getContentPane(), "Ok", "Load 2D Points", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Serialized data loaded from " + l_file);

				} catch (Exception ioe) {
					ioe.printStackTrace();
					System.err.println("Failed to open " + l_file);
					JOptionPane.showMessageDialog(getContentPane(), "Failed", "Load 2D Points", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	void Detect(int a_frame, int a_rotation, boolean a_test)
	{
	    DataOutputStream l_dos = null;
	    DataInputStream l_dis = null;
	    m_pco.clear();
	    m_raw_points = new ArrayList<>() ;
		m_minx = 0;
		m_maxx = 0;
		m_miny = 0;
		m_maxy = 0;
		m_minz = 0;
		m_maxz = 0;

		m_e3d.m_cal_data.calculate();
		m_e3d.m_cal_data.calculateBaseCoords();
		
		m_pco.setTT((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()),
				Integer.parseInt(tfZrotoff.getText()),
				Integer.parseInt(tfXrotoff.getText()),
				Integer.parseInt(tfYModelOffset.getText()),
				Eora3D_MainWindow.m_e3d_config.sm_leftfilter,
				Eora3D_MainWindow.m_e3d_config.sm_rightfilter,
				Eora3D_MainWindow.m_e3d_config.sm_topfilter,
				Eora3D_MainWindow.m_e3d_config.sm_bottomfilter,
				Eora3D_MainWindow.m_e3d_config.sm_frontfilter,
				Eora3D_MainWindow.m_e3d_config.sm_backfilter);
		for(int l_layer = 0; l_layer < 20; ++ l_layer)
		{
//			if(m_pco.m_layer_visible.get(l_layer) == null)
//				m_pco.m_layer_visible.add(new Boolean(true));
			m_pco.m_layer_visible.set(l_layer, new Boolean(m_layer_visible.get(l_layer).isSelected()));
			Eora3D_MainWindow.m_e3d_config.m_layer_coloured.set(l_layer, new Boolean(m_layer_coloured.get(l_layer).isSelected()));
		}
/*
	    if(m_e3d.m_is_windows10 && m_pco_thread==null)
    	{
	    	m_pco_thread = new Thread(m_pco);
	    	m_pco_thread.start();
    	}*/
	    if(!m_e3d.m_is_windows10) try {
			//m_socket = new Socket(InetAddress.getLoopbackAddress(), 7778);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			m_socket = null;
		}
		if(m_socket!=null)
		{
			try {
				l_dos = new DataOutputStream(m_socket.getOutputStream());
				l_dis = new DataInputStream(m_socket.getInputStream());
				l_dos.flush();
				l_dos.writeInt(0);
				l_dos.writeInt(1);
			    l_dos.writeFloat(m_pco.m_Scale);
			    l_dos.writeFloat(m_pco.m_Pointsize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				m_socket = null;
			}
		}
		if(Eora3D_MainWindow.m_e3d_config.sm_camera_rotation==90)
		{
			m_e3d.m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_e3d.m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		else
		{
			m_e3d.m_cal_data.capture_w_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_w;
			m_e3d.m_cal_data.capture_h_pix = Eora3D_MainWindow.m_e3d_config.sm_camera_res_h;
		}
		m_e3d.m_cal_data.calculate();
		m_e3d.m_cal_data.calculateBaseCoords();
		int l_tt_start = 0;
		int l_tt_end = 18;
		String l_tt = "";
		if(!Eora3D_MainWindow.m_e3d_config.sm_turntable_scan)
		{
			l_tt_end = 1;
		}
		if(a_test)
		{
			if(!Eora3D_MainWindow.m_e3d_config.sm_turntable_scan)
			{
				l_tt_end = 1;
			}
			else
			{
				l_tt_start = a_rotation;
				l_tt_end = a_rotation+1;
			}
		}
		if(m_socket != null)
		{
	    	try {
	    		l_dos.writeInt(4);
	    		l_dos.writeFloat((float)Eora3D_MainWindow.m_e3d_config.sm_turntable_step_size/18.1f+Float.parseFloat(tfRotationModifier.getText()));
	    		l_dos.writeInt(Integer.parseInt(tfZrotoff.getText()));
	    		l_dos.writeInt(Integer.parseInt(tfXrotoff.getText()));
	    		l_dos.writeInt(Integer.parseInt(tfYModelOffset.getText()));
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_leftfilter);
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_rightfilter);
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_topfilter);
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_bottomfilter);
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_frontfilter);
	    		l_dos.writeInt(Eora3D_MainWindow.m_e3d_config.sm_backfilter);
				l_dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				m_socket = null;
			}
		}
    	
		for(int l_tt_point = l_tt_start; l_tt_point < l_tt_end; ++l_tt_point)
		{
			final int l_tt_point_f = l_tt_point;
			if(Eora3D_MainWindow.m_e3d_config.sm_turntable_scan) l_tt="tt"+l_tt_point+"_";
			File l_basefile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+"base.png");
			File l_colourmapfile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+"colourmap.png");
			int l_start = 0;
			int l_end = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
			synchronized(m_raw_points)
			{
				m_raw_points.add(new ArrayList<RawPoint>());	
			}
			if(a_frame != -1)
			{
				l_start = a_frame;
				l_end = a_frame+1;
			}
			for (int l_pos = l_start;
					l_pos < l_end;
					++l_pos)
			{
				File l_infile;
			    m_points = new ArrayList<RGB3DPoint>();
				try {
					l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"scan_"+l_tt+l_pos+".png");
				}
				catch(Exception e)
				{
					e.printStackTrace();
					m_detect_thread=null;
					if(m_socket!=null) try {
						m_socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					m_socket = null;
					getContentPane().setCursor(Cursor.getDefaultCursor());
					m_ControlSetState.setState(0); validate(); repaint();
					return;
				}
				if(!l_infile.exists()) continue;
				System.out.println("Analysing "+l_infile.toString());
				BufferedImage l_inimage, l_baseimage, l_colourmapimage;
	
				try {
					l_baseimage = ImageIO.read(l_basefile);
					l_colourmapimage = ImageIO.read(l_colourmapfile);
					l_inimage = ImageIO.read(l_infile);
				} catch (IOException e1) {
					e1.printStackTrace();
					m_detect_thread=null;
					if(m_socket!=null) try {
						m_socket.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					m_socket = null;
					getContentPane().setCursor(Cursor.getDefaultCursor());
					m_ControlSetState.setState(0); validate(); repaint();
					return;
				}
				
				if(!a_test) imagePanel.m_image = l_inimage;
				else imagePanel.m_image = null;
				if(a_test)
				{
					imagePanel.m_overlay = analyzeImage(l_baseimage, l_inimage);
					m_test_image = imagePanel.m_overlay;
				}
				else imagePanel.m_overlay = null;
				
				Thread l_threads[] = new Thread[Eora3D_MainWindow.m_e3d_config.sm_threads];
				for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
				{
					final int l_lambda_thread = l_thread;
					final int l_lambda_pos = l_pos;
					Runnable l_task = () -> {
						ArrayList<Point> l_points;
						ArrayList<RGB3DPoint> l_rgb3dpoints = new ArrayList<RGB3DPoint>();
						ArrayList<RawPoint> l_rawpoints = new ArrayList<RawPoint>();
//						System.out.println("Thread "+l_lambda_thread);
						int l_range_start = (l_baseimage.getHeight()/Eora3D_MainWindow.m_e3d_config.sm_threads)*l_lambda_thread;
						int l_range_end = (l_baseimage.getHeight()/Eora3D_MainWindow.m_e3d_config.sm_threads)*(l_lambda_thread+1);
						if(l_lambda_thread == Eora3D_MainWindow.m_e3d_config.sm_threads-1) l_range_end = l_baseimage.getHeight()-1;
						l_points = analyzeImagetoArray(l_baseimage, l_inimage, l_range_start, l_range_end);
//						System.out.println("Found count: "+l_points.size());
						for(Point l_found_point: l_points)
						{
							{
								RGB3DPoint l_point = m_e3d.m_cal_data.getPointOffset(l_lambda_pos, l_found_point.x, (l_baseimage.getHeight()-l_found_point.y)-1);
								l_point.m_r = (l_colourmapimage.getRGB(l_found_point.x, l_found_point.y) & 0xff0000)>>16;
								l_point.m_g = (l_colourmapimage.getRGB(l_found_point.x, l_found_point.y) & 0xff00)>>8;
								l_point.m_b = l_colourmapimage.getRGB(l_found_point.x, l_found_point.y) & 0xff;
								//l_point.m_x -= l_baseimage.getWidth();
								//l_point.m_x = -l_point.m_x;
//								System.out.println(l_point.m_x+","+l_point.m_y+","+l_point.m_z);
								//System.out.println(l_point.m_r+":"+l_point.m_g+":"+l_point.m_b);
/*								if(Math.abs(l_point.m_x)<=10000 &&
										Math.abs(l_point.m_y)<=10000 &&
										Math.abs(l_point.m_z)<=10000)*/
/*								m_minx = Math.min(m_minx, l_point.m_x);
								m_maxx = Math.max(m_maxx, l_point.m_x);
								m_miny = Math.min(m_miny, l_point.m_y);
								m_maxy = Math.max(m_maxy, l_point.m_y);
								m_minz = Math.min(m_minz, l_point.m_z);
								m_maxz = Math.max(m_maxz, l_point.m_z);*/
//								m_pco.addPoint(l_tt_point_f, l_point);
								if(cbKeepDetectedPoints.isSelected())
								{
									RawPoint l_raw_point = new RawPoint();
									
									l_raw_point.m_x = l_found_point.x;
									l_raw_point.m_y = (l_baseimage.getHeight()-l_found_point.y)-1;
									l_raw_point.m_angle = l_lambda_pos;
									l_raw_point.m_r = l_point.m_r;
									l_raw_point.m_g = l_point.m_g;
									l_raw_point.m_b = l_point.m_b;
									l_rawpoints.add(l_raw_point);
//									m_raw_points.get(l_tt_point_f).add(l_raw_point);
								}
								l_rgb3dpoints.add(l_point);
							}
						}
						synchronized(m_points)
						{
							m_points.addAll(l_rgb3dpoints);
							synchronized(m_raw_points)
							{
								m_raw_points.get(l_tt_point_f).addAll(l_rawpoints);
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
				for(RGB3DPoint l_point: m_points)
				{
					m_pco.addPoint(l_tt_point_f, l_point);
				}
				m_pco.m_refresh = true;
				imagePanel.repaint();
				System.out.println("Raw pts "+m_raw_points.get(l_tt_point_f).size());
				// In linux, send the point off to the possible viewer
		        if(m_socket != null)
		        {
					System.out.println("Sending points "+m_points.size());
		        	for(int i=0; i<m_points.size(); ++i)
			        {
			        	RGB3DPoint l_point = m_points.get(i);
			        	try {
			        		l_dos.writeInt(2);
			        		l_dos.writeInt(l_tt_point);
			        		l_point.write(l_dos);
							l_dos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							m_socket = null;
						}
			        	//System.out.println("Sent "+i);
					}
	    			try {
	    				l_dos.writeInt(3);
	    				l_dis.readInt();
	    			} catch (Exception e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    				m_socket = null;
		    		}
					System.out.println("Sent points");
		        }
				if(m_stop_detection) {
					try {
						m_socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					getContentPane().setCursor(Cursor.getDefaultCursor());
					m_ControlSetState.setState(0); validate(); repaint();
					return;
				}
				System.out.println("Complete "+l_infile.toString());
				//System.gc();
			}
		}
//		System.out.println("Found "+l_points+" points");
		System.out.println("Min X: "+m_minx);
		System.out.println("Max X: "+m_maxx);
		System.out.println("Min Y: "+m_miny);
		System.out.println("Max Y: "+m_maxy);
		System.out.println("Min Z: "+m_minz);
		System.out.println("Max Z: "+m_maxz);
		m_detect_thread=null;
		if(m_socket != null)
		{
			try {
				l_dos.close();
				l_dis.close();
				m_socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		m_socket = null;
		if(!a_test) JOptionPane.showMessageDialog(getContentPane(), "Done", "Model generation", JOptionPane.INFORMATION_MESSAGE);
		m_ControlSetState.setState(0); validate(); repaint();
		getContentPane().setCursor(Cursor.getDefaultCursor());
	}
	
	BufferedImage analyzeImage(BufferedImage a_base, BufferedImage a_in)
	{
		BufferedImage a_out = new BufferedImage(a_in.getWidth(), a_in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int x, y;

		for(y = 0; y< a_in.getHeight(); ++y)
		{
			boolean l_in_laser = false;
			int l_start_of_high_point = -1;
			int l_end_of_high_point = -1;
			int mr=0, mg=0, mb=0;
			int l_laser_point_count = 0;
			for(x = 0; x<a_in.getWidth(); ++x)
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
        		int rd;
        		int gd;
        		int bd;
        		if(!Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("No base peak threshold"))
        		{
            		rd = Math.abs(r_base-r);
            		gd = Math.abs(g_base-g);
            		bd = Math.abs(b_base-b);
        		}
        		else
        		{
        			rd = r;
        			gd = g;
        			bd = b;
        		}
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

        		boolean l_detected = false;
        		int l_argb_out = 0;
        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("And"))
        		{
	        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("%"))
        		{
        			float pc = ((float)rd/255.0f + (float)gd/255.0f + (float)bd/255.0f)*100.0f;
	        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Weighted %"))
        		{
        			float pc = (((float)rd/255.0f)*40.0f + ((float)gd/255.0f)*20.0f + ((float)bd/255.0f)*40.0f);
	        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("No base peak threshold"))
        		{
	    			if(r<Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b<Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) l_detected = true;
	        	}
        		
        		if(l_detected)
    			{
        			l_argb_out = 0xff00ff00; // Green spot if threshold passed
        			++l_laser_point_count;
    			}
        		if(!l_in_laser)
        		{
        			if(l_detected)
        			{
	        			l_in_laser = true;
	        			l_start_of_high_point = x;
	    				l_end_of_high_point = x;
	        			mr = rd;
	        			mg = gd;
	        			mb = bd;
        			}
        		}
        		else if(l_in_laser)
        		{
        			if(!l_detected)
        			{
        				// Got to end of laser detection area
        				l_end_of_high_point = x-1;
        				if(l_start_of_high_point!=-1 && l_laser_point_count>Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser)
        				{
        					for(x=l_start_of_high_point; x<=l_end_of_high_point; ++x)
        					{
        						a_out.setRGB(x, y, 0xff0000ff);
        					}
        					int l_mid_hight_point = (l_start_of_high_point+l_end_of_high_point)/2;
        					a_out.setRGB(l_mid_hight_point, y, 0xffff0000);
        				}
        				l_in_laser = false;
        				l_start_of_high_point = -1;
        				l_end_of_high_point = -1;
        				
        				l_laser_point_count = 0;
        				
        			} else
        			if(rd>mr || gd>mg || bd>mb)
        			{
            			l_start_of_high_point = x;
        				l_end_of_high_point = x;
            			mr = rd;
            			mg = gd;
            			mb = bd;
        			}
        			else if(rd<mr || gd<mg || bd<mb)
        			{
        				l_end_of_high_point = x;
        			}
        		}
        		if(l_argb_out != 0) a_out.setRGB(x, y, l_argb_out);
			}
			if(l_start_of_high_point!=-1 && l_laser_point_count>Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser)
			{
				//System.out.println("Y: "+y+" S: "+l_start_of_high_point+" E: "+l_end_of_high_point);
				if(l_end_of_high_point == -1) l_end_of_high_point = a_in.getWidth()-1;
				for(x=l_start_of_high_point; x<=l_end_of_high_point; ++x)
				{
					a_out.setRGB(x, y, 0xff0000ff);
				}
				int l_mid_hight_point = (l_start_of_high_point+l_end_of_high_point)/2;
				a_out.setRGB(l_mid_hight_point, y, 0xffff0000);
			}
			l_in_laser = false;
			l_start_of_high_point = -1;
			l_end_of_high_point = -1;
			l_laser_point_count = 0;
		}
		/*try {
			ImageIO.write(a_out, "png", new File("/home/nickh/test.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/

		return a_out;
	}
	
	ArrayList<Point> analyzeImagetoArray(BufferedImage a_base, BufferedImage a_in, int a_start, int a_end)
	{
		ArrayList<Point> a_out = new ArrayList<Point>();
		int x, y;

		for(y = a_start; y < a_end; ++y)
		{
			int l_hits = Eora3D_MainWindow.m_e3d_config.sm_max_points_per_line;
			boolean l_in_laser = false;
			int l_start_of_high_point = -1;
			int l_end_of_high_point = -1;
			int mr=0, mg=0, mb=0;
			int l_laser_point_count = 0;
			// First pass, find brightest green in the line
			int l_g_max = 0;
			for(x = 0; x < a_in.getWidth(); ++x)
			{
				int argb = a_in.getRGB(x, y);
            	int g = (argb & 0x00ff00) >> 8;
				l_g_max = Math.max(g,  l_g_max);
			}
			for(x = 0; x < a_in.getWidth(); ++x)
			{
				int argb = a_in.getRGB(x, y);
				int r = (argb & 0xff0000) >> 16;
            	int g = (argb & 0x00ff00) >> 8;
        		int b = (argb & 0x0000ff);
        		
				int argb_base = a_base.getRGB(x, y);
				int r_base = (argb_base & 0xff0000) >> 16;
            	int g_base = (argb_base & 0x00ff00) >> 8;
        		int b_base = (argb_base & 0x0000ff);
        		
        		int rd;
        		int gd;
        		int bd;
        		if(!Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("No base peak threshold"))
        		{
            		rd = Math.abs(r_base-r);
            		gd = Math.abs(g_base-g);
            		bd = Math.abs(b_base-b);
        		}
        		else
        		{
        			rd = r;
        			gd = g;
        			bd = b;
        		}

        		boolean l_detected = false;
        		if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Or"))
        		{
	        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r ||
	        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g ||
	        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("And"))
        		{
	        		if(rd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				gd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				bd>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("%"))
        		{
        			float pc = ((float)rd/255.0f + (float)gd/255.0f + (float)bd/255.0f)*100.0f;
	        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("Weighted %"))
        		{
        			float pc = (((float)rd/255.0f)*40.0f + ((float)gd/255.0f)*20.0f + ((float)bd/255.0f)*40.0f);
	        		if(pc>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_percent) l_detected = true;
	        	} else if(Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_logic.contentEquals("No base peak threshold"))
        		{
	    			if(r<Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_r &&
	        				g>Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_g &&
	        				b<Eora3D_MainWindow.m_e3d_config.sm_laser_detection_threshold_b
	        				&& g >= l_g_max) l_detected = true;
	        	}

        		if(l_detected)
    			{
        			++l_laser_point_count;
    			}
        		if(!l_in_laser)
        		{
        			if(l_detected)
        			{
	        			l_in_laser = true;
	        			l_start_of_high_point = x;
	    				l_end_of_high_point = x;
	        			mr = rd;
	        			mg = gd;
	        			mb = bd;
        			}
        		}
        		else if(l_in_laser)
        		{
        			if(!l_detected)
        			{
        				// Got to end of laser detection area
        				l_end_of_high_point = x-1;
        				if(l_start_of_high_point!=-1 && l_laser_point_count>Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser)
        				{
        					int l_mid_hight_point = (l_start_of_high_point+l_end_of_high_point)/2;
        					a_out.add(new Point(l_mid_hight_point, y));
                			--l_hits;
                			if(l_hits == 0) break;
        				}
        				l_in_laser = false;
        				l_start_of_high_point = -1;
        				l_end_of_high_point = -1;
        				
        				l_laser_point_count = 0;
        				
        			} else
        			if(rd>mr || gd>mg || bd>mb)
        			{
            			l_start_of_high_point = x;
        				l_end_of_high_point = x;
            			mr = rd;
            			mg = gd;
            			mb = bd;
        			}
        			else if(rd<mr || gd<mg || bd<mb)
        			{
        				l_end_of_high_point = x;
        			}
        		}
			}
			if(l_start_of_high_point!=-1 && l_laser_point_count>Eora3D_MainWindow.m_e3d_config.sm_min_points_per_laser && l_hits!=0)
			{
				//System.out.println("Y: "+y+" S: "+l_start_of_high_point+" E: "+l_end_of_high_point);
				if(l_end_of_high_point == -1) l_end_of_high_point = a_in.getWidth()-1;
				int l_mid_hight_point = (l_start_of_high_point+l_end_of_high_point)/2;
				a_out.add(new Point(l_mid_hight_point, y));
			}
			l_in_laser = false;
			l_start_of_high_point = -1;
			l_end_of_high_point = -1;
			l_laser_point_count = 0;
		}
		return a_out;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		m_stop_detection = true;
		m_pco.Close();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		m_pco.Close();
		putToConfig();
		if(m_socket!=null)
		{
			try {
				m_socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
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
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(m_block_updates) return;
		if(e.getSource().equals(sbPointsize))
		{
			m_pco.m_Pointsize = sbPointsize.getValue();
		} else
		if(e.getSource().equals(sbScaling))
		{
			m_pco.m_Scale = sbScaling.getValue();
		}
		else
		if(e.getSource().equals(this.sbViewYRotation))
		{
	    	m_pco.m_y_rot = sbViewYRotation.getValue();
	    	lblOVYR.setText(""+m_pco.m_y_rot);
		}
		else
		if(e.getSource().equals(this.sbViewXRotation))
		{
	    	m_pco.m_x_rot = sbViewXRotation.getValue()/2;
	    	lblOVXR.setText(""+m_pco.m_x_rot);
		}
		if(m_detect_thread == null)
		{
		    DataOutputStream l_dos = null;

			if(!m_e3d.m_is_windows10) try {
				//m_socket = new Socket(InetAddress.getLoopbackAddress(), 7778);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				m_socket = null;
			}
			if(m_socket!=null)
			{
				try {
					l_dos = new DataOutputStream(m_socket.getOutputStream());
					l_dos.flush();
					l_dos.writeInt(1);
				    l_dos.writeFloat(m_pco.m_Scale);
				    l_dos.writeFloat(m_pco.m_Pointsize);
				    l_dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					m_socket = null;
				}
			}
			if(m_socket!=null)
			{
				try
				{
					l_dos.close();
					m_socket.close();
				} catch(Exception e2)
				{
					e2.printStackTrace();
					m_socket = null;
					return;
				}
			}
		}

	}
	
	void reCalculate()
	{
	    m_pco.clear();
		m_minx = 0;
		m_maxx = 0;
		m_miny = 0;
		m_maxy = 0;
		m_minz = 0;
		m_maxz = 0;
		m_points = new ArrayList<RGB3DPoint>();

		m_e3d.m_cal_data.ux = Float.parseFloat(tfUX.getText());
		m_e3d.m_cal_data.uz = Float.parseFloat(tfUZ.getText());
		m_e3d.m_cal_data.vx = Float.parseFloat(tfVX.getText());
		m_e3d.m_cal_data.vz = Float.parseFloat(tfVZ.getText());
		m_e3d.m_cal_data.wx = Float.parseFloat(tfWX.getText());
		m_e3d.m_cal_data.wz = Float.parseFloat(tfWZ.getText());
		
		m_e3d.m_cal_data.calculate();
		m_e3d.m_cal_data.calculateBaseCoords();
		
		int l_tt_layer = 0;
		if(m_raw_points == null) return;
		synchronized(m_raw_points)
		{
			for(ArrayList<RawPoint> l_rpal : m_raw_points)
			{
				int l_end = l_rpal.size();
				int l_segment = l_end/Eora3D_MainWindow.m_e3d_config.sm_threads;
				final int l_tt_point = l_tt_layer++;
				
				
				
				
				Thread l_threads[] = new Thread[Eora3D_MainWindow.m_e3d_config.sm_threads];
				for(int l_thread = 0; l_thread < Eora3D_MainWindow.m_e3d_config.sm_threads; ++l_thread)
				{
					final int l_lambda_thread = l_thread;
					Runnable l_task = () -> {
						int l_range_start = l_segment*l_lambda_thread;
						int l_range_end = l_segment*(l_lambda_thread+1);
						if(l_lambda_thread == Eora3D_MainWindow.m_e3d_config.sm_threads-1) l_range_end = l_end;
	
						ArrayList<RGB3DPoint> l_points = new ArrayList<RGB3DPoint>();
						for(int i=l_range_start; i < l_range_end; ++i)
						{
							RawPoint l_rp = l_rpal.get(i);
							RGB3DPoint l_point = m_e3d.m_cal_data.getPointOffset(l_rp.m_angle, l_rp.m_x, l_rp.m_y);
							l_point.m_r = l_rp.m_r;
							l_point.m_g = l_rp.m_g;
							l_point.m_b = l_rp.m_b;
							
							
							m_pco.addPoint(l_tt_point, l_point);
							l_points.add(l_point);
	/*						m_minx = Math.min(m_minx, l_point.m_x);
							m_maxx = Math.max(m_maxx, l_point.m_x);
							m_miny = Math.min(m_miny, l_point.m_y);
							m_maxy = Math.max(m_maxy, l_point.m_y);
							m_minz = Math.min(m_minz, l_point.m_z);
							m_maxz = Math.max(m_maxz, l_point.m_z);*/
						}
						m_points.addAll(l_points);
	
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
				m_pco.m_refresh = true;
			}
		}
/*		System.out.println("Min X: "+m_minx);
		System.out.println("Max X: "+m_maxx);
		System.out.println("Min Y: "+m_miny);
		System.out.println("Max Y: "+m_maxy);
		System.out.println("Min Z: "+m_minz);
		System.out.println("Max Z: "+m_maxz);*/
/*		for(RGB3DPoint l_point: m_points)
		{
			m_pco.addPoint(0, l_point);
		}*/
		System.out.println("Recalculated "+m_points.size()+" points");

	}
}

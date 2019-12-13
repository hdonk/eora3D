package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
/*import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;*/
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;





import javax.imageio.ImageIO;
import java.awt.*;

class CalibrationData
{
	// mm
	public double board_w = 160.0f;
	public double board_h = 220.0f;
	public double spot_r = 2.0f;
	public double spot_sep_w = 130.0f;
	public double spot_sep_h = 191.0f;
	
	// Calculation inputs
	// pixels
	public int capture_w = 1280;
	public int capture_h = 720;
	
	private double cal_pos_1_per = 0.90f;
	private double cal_pos_2_per = 0.70f;
	
	// Calculation outputs
	public Rectangle pos_1_tl;
	public Rectangle pos_1_tr;
	public Rectangle pos_1_bl;
	public Rectangle pos_1_br;

	public Rectangle pos_2_tl;
	public Rectangle pos_2_tr;
	public Rectangle pos_2_bl;
	public Rectangle pos_2_br;
	
	public Rectangle pos_1_board;
	public Rectangle pos_2_board;
	
	void calculate()
	{
		if(capture_w > capture_h) // landscape
		{
			double target_h = ((double)capture_h * cal_pos_1_per);
			double target_w = (target_h/board_h)*board_w;
			pos_1_board = new Rectangle();
			pos_1_board.height = (int)target_h;
			pos_1_board.width = (int)target_w;
			pos_1_board.x = (capture_w-pos_1_board.width)/2;
			pos_1_board.y = (capture_h-pos_1_board.height)/2;
			
			pos_1_tl = new Rectangle();
			pos_1_tl.width = (int)(((target_w/board_w)*spot_r))*2;
			pos_1_tl.height = (int)(((target_h/board_h)*spot_r))*2;
			pos_1_tl.x = (int)(capture_w -((target_w/board_w)*spot_sep_w))/2 - pos_1_tl.width;
			pos_1_tl.y = (int)(capture_h -((target_h/board_h)*spot_sep_h))/2 - pos_1_tl.height;

			pos_1_tr = new Rectangle();
			pos_1_tr.width = pos_1_tl.width;
			pos_1_tr.height = pos_1_tl.height;
			pos_1_tr.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
			pos_1_tr.y = pos_1_tl.y;

			pos_1_bl = new Rectangle();
			pos_1_bl.width = pos_1_tl.width;
			pos_1_bl.height = pos_1_tl.height;
			pos_1_bl.x = pos_1_tl.x;
			pos_1_bl.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2;

			pos_1_br = new Rectangle();
			pos_1_br.width = pos_1_tl.width;
			pos_1_br.height = pos_1_tl.height;
			pos_1_br.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
			pos_1_br.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2;
		}
	}
}

class CircleDetection {
    private BufferedImage grey;
    private int[][] sobelX;
    private int[][] sobelY;
    private double[][] sobelTotal;
    public int threshold = 280;
	public int[][][] A;
	
	int minr = 10;
	int maxr = 40;
	
	BufferedImage displayimage = null;
	public int minHits = 200;
	
	Rectangle searcharea = null;
	private BufferedImage totalCircles;

    void detect(BufferedImage image, Rectangle a_searcharea) throws Exception{

    	searcharea = a_searcharea;
    	
        //runs the required processes for circle detection, ie sobel edge detection
        toGrayScale(image);
        edgeDetection();

        //creates and outputs images for the sobel sweep in x and y direction, and the combined sobel output
/*        BufferedImage img = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                img.setRGB(i, j, sobelX[i][j]);
            }
        }
        displayimage = img;*/


/*        BufferedImage ing = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                ing.setRGB(i, j, sobelY[i][j]);
            }
        }*/

        BufferedImage total = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        double max = 0;
        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                if(sobelTotal[i][j]>max){
                    max = sobelTotal[i][j];
                }
            }
        }
        //displayimage = total;
      for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                //maps every pixel to a grayscale value between 0 and 255 from between 0 and the max value in sobelTotal
                int rgb = new Color((int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255), 255).getRGB();
                total.setRGB(i,j,rgb);
            }
        }
        total = changeBrightness(20.0f, total);
        //displayimage = total;
        
        //outputs an image showing every pixel that is above the threshold
        BufferedImage totalColour = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < grey.getWidth(); i++) {
        	for(int j = 0; j < grey.getHeight(); j++) {
        		Color c1 = new Color(0,255,0);
        		if(sobelTotal[i][j] > threshold) {
        			totalColour.setRGB(i, j, c1.getRGB());
        		}
        	}
        }
        //displayimage = totalColour;
        
        //circleDetection(total);
    }

    //maps the given value between startCoord1 and endCoord1 to a value between startCoord2 and endCoord2
    private double map(double valueCoord1,
                             double startCoord1, double endCoord1,
                             double startCoord2, double endCoord2) {


        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + startCoord2;
    }

    //converts given image into a grayscale image
    private void toGrayScale(BufferedImage img) throws Exception{
        grey = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_BYTE_GRAY);
        grey.getGraphics().drawImage(img, 0 , 0, null);
    }

    //runs all the functions required to complete edge detection
    private void edgeDetection(){
        calcSobelX();
        calcSobelY();
        combineSobel();
    }

    //performs the horizontal sobel sweep, sets up the matrix using a 3x3 array and runs through
    //every pixel to calculate the sobel result
    private void calcSobelX(){
        sobelX = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[1][0] = -2;
        base[2][0] = -1;
        base[0][1] = 0;
        base[1][1] = 0;
        base[2][1] = 0;
        base[0][2] = 1;
        base[1][2] = 2;
        base[2][2] = 1;


        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelX[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    //performs the vertical sobel sweep, sets up the matrix using a 3x3 array and runs through
    //every pixel to calculate the sobel result
    private void calcSobelY(){
        sobelY = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[0][1] = -2;
        base[0][2] = -1;
        base[1][0] = 0;
        base[1][1] = 0;
        base[1][2] = 0;
        base[2][0] = 1;
        base[2][1] = 2;
        base[2][2] = 1;


        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelY[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    //a series of if statements to account for any edge cases to ensure no errors, calculates
    //the sobel result for any pixel and kernel
    private int getSobelResult(int x, int y, int[][] base){
        int result = 0;
        if(x==0 && y ==0){
            for(int i = 0; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y==grey.getHeight()-1){
            for(int i = 0; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y !=0){
            for (int i = 0; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result += new Color(grey.getRGB(x + i, y + j)).getRed()* base[j + 1][i + 1];
                }
            }

        }else if(x==grey.getWidth()-1 && y ==0){
            for(int i = -1; i <= 0; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=0 && y ==0){
            for(int i = -1; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y!=grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else{
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }

        return result;
    }

    //performs the algorithm to combine the horizontal sweep and vertical sweep
    private void combineSobel(){
        sobelTotal = new double[grey.getWidth()][grey.getHeight()];
        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelTotal[i][j] = Math.round(Math.sqrt(Math.pow((double)sobelX[i][j],2) + Math.pow((double)sobelY[i][j],2)));
            }
        }
    }

    void initTotalCircles(BufferedImage image)
    {
    	totalCircles = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	
    }
    
    Rectangle findFirstCircle(BufferedImage image) throws Exception 
    {
        int radius = maxr;
        A = new int[image.getWidth()][image.getHeight()][radius];

        for (int rad = 0; rad < radius; rad++) {
            for (int x = searcharea.x; x < searcharea.x+searcharea.width; x++) {
                for (int y = searcharea.y; y < searcharea.y+searcharea.height; y++) {
                    //if the given pixel is above the threshold, a circle will be drawn at radius rad around it and if it
                    //is a valid coordinate it will be accumulated in the A array and plotted in the pointSpace image
                    if (sobelTotal[x][y] > threshold) {
                        for (int t = 0; t <= 360; t++) {
                            Integer a = (int) Math.floor(x - rad * Math.cos(t * Math.PI / 180));
                            Integer b = (int) Math.floor(y - rad * Math.sin(t * Math.PI / 180));
                            if (!((0 > a || a > image.getWidth() - 1) || (0 > b || b > image.getHeight() - 1))) {
                                if (!(a.equals(x) && b.equals(y))) {
                                   A[a][b][rad] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(totalCircles == null) initTotalCircles(image);
        displayimage = totalCircles;
        Graphics2D g = totalCircles.createGraphics();
        g.setColor(Color.RED);

        System.out.println("Looking for potential centers");
        for (int x = searcharea.x; x < searcharea.x+searcharea.width; x++) {
            for (int y = searcharea.y; y < searcharea.y+searcharea.height; y++) {
                for (int r = minr; r < maxr; r++) {
                    if (A[x][y][r] > minHits) {
                        double a =  x - r * Math.cos(0 * Math.PI / 180);
                        double b =  y - r * Math.sin(90 * Math.PI / 180);
                        g.drawOval((int)a,(int)b,2*r,2*r);
                        System.out.println("A "+A[x][y][r]+ "("+x+","+y+") r="+r);
                        return new Rectangle(x, y, r, r);
//                        break;
                    }
                }
            }
        }
        return null;
    }

    //changes the brightness of an image by the factor given
    private static BufferedImage changeBrightness(float brightenFactor, BufferedImage image){
        RescaleOp op = new RescaleOp(brightenFactor, 0, null);
        image = op.filter(image, image);
        return image;
    }
}













class PaintImage extends JPanel
{
  public BufferedImage m_image = null;
  public BufferedImage m_overlay = null;
  //public Mat circles;
  public CalibrationData m_cal_data = null;
 
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
    		m_cal_data.pos_1_tl.x-10,
    		m_cal_data.pos_1_tl.y-10,
    		m_cal_data.pos_1_tl.width+20,
    		m_cal_data.pos_1_tl.height+20
    		);
    g.drawOval(
    		m_cal_data.pos_1_tr.x,
    		m_cal_data.pos_1_tr.y,
    		m_cal_data.pos_1_tr.width,
    		m_cal_data.pos_1_tr.height
    		);
    g.drawRect(
    		m_cal_data.pos_1_tr.x-10,
    		m_cal_data.pos_1_tr.y-10,
    		m_cal_data.pos_1_tr.width+20,
    		m_cal_data.pos_1_tr.height+20
    		);
    g.drawOval(
    		m_cal_data.pos_1_bl.x,
    		m_cal_data.pos_1_bl.y,
    		m_cal_data.pos_1_bl.width,
    		m_cal_data.pos_1_bl.height
    		);
    g.drawRect(
    		m_cal_data.pos_1_bl.x-10,
    		m_cal_data.pos_1_bl.y-10,
    		m_cal_data.pos_1_bl.width+20,
    		m_cal_data.pos_1_bl.height+20
    		);
    g.drawOval(
    		m_cal_data.pos_1_br.x,
    		m_cal_data.pos_1_br.y,
    		m_cal_data.pos_1_br.width,
    		m_cal_data.pos_1_br.height
    		);
    g.drawRect(
    		m_cal_data.pos_1_br.x-10,
    		m_cal_data.pos_1_br.y-10,
    		m_cal_data.pos_1_br.width+20,
    		m_cal_data.pos_1_br.height+20
    		);
/*    if(circles!=null)
    {
	   if (circles.cols() > 0) {
	        for (int x=0; x < Math.min(circles.cols(), 5); x++ ) {
	            double circleVec[] = circles.get(0, x);

	            if (circleVec == null) {
	                break;
	            }

	            int radius = (int) circleVec[2];

	            g.drawOval((int) circleVec[0], (int) circleVec[1], radius*2, radius*2);
	        }
	    }
    }*/
    repaint();
  }
}

public class eora3D_calibration extends JDialog implements ActionListener {
	private Webcam m_camera;
	private PaintImage image;
	private JTextField txtThreshold;
	private JTextField txtMinHits;
	private JTextField txtMinRad;
	private JTextField txtMaxRad;
	private BufferedImage capturedImage = null;
	private CalibrationData m_cal_data;
	private JTextField txtCalibImg;

	eora3D_calibration(Webcam a_camera)
	{
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
		txtMinRad.setText("5");
		txtMinRad.setBounds(571, 782, 114, 19);
		getContentPane().add(txtMinRad);
		txtMinRad.setColumns(10);
		
		txtMaxRad = new JTextField();
		txtMaxRad.setText("10");
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
		
		txtCalibImg = new JTextField();
		txtCalibImg.setText("3600");
		txtCalibImg.setBounds(320, 889, 114, 19);
		getContentPane().add(txtCalibImg);
		txtCalibImg.setColumns(10);
		
		JLabel lblCompareimg = new JLabel("Compare Image Number");
		lblCompareimg.setBounds(133, 891, 179, 15);
		getContentPane().add(lblCompareimg);
		btnDetect.addActionListener(this);
		
		
		m_camera = a_camera;
		
		setSize(1300,957);
		
		calculateCalibrationPositions();
		
		setModal(true);
	}

	private void calculateCalibrationPositions() {
		m_cal_data = new CalibrationData();
		
		m_cal_data.capture_w = (int)m_camera.getViewSize().getWidth();
		m_cal_data.capture_h = (int)m_camera.getViewSize().getHeight();
		
		m_cal_data.calculate();
		
		image.m_cal_data = m_cal_data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Capture")
		{
			BufferedImage l_image = m_camera.getImage();
			l_image.flush();
			
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
				System.out.println("Detecting circles");
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tl.x-10,
					    		m_cal_data.pos_1_tl.y-10,
					    		m_cal_data.pos_1_tl.width+20,
					    		m_cal_data.pos_1_tl.height+20
								));
				l_cd.findFirstCircle(
						capturedImage
						);
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_tr.x-10,
					    		m_cal_data.pos_1_tr.y-10,
					    		m_cal_data.pos_1_tr.width+20,
					    		m_cal_data.pos_1_tr.height+20
								));
				l_cd.findFirstCircle(
						capturedImage
						);
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_bl.x-10,
					    		m_cal_data.pos_1_bl.y-10,
					    		m_cal_data.pos_1_bl.width+20,
					    		m_cal_data.pos_1_bl.height+20
								));
				l_cd.findFirstCircle(
						capturedImage
						);
				l_cd.detect(capturedImage,
						new Rectangle(
					    		m_cal_data.pos_1_br.x-10,
					    		m_cal_data.pos_1_br.y-10,
					    		m_cal_data.pos_1_br.width+20,
					    		m_cal_data.pos_1_br.height+20
								));
				l_cd.findFirstCircle(
						capturedImage
						);
				System.out.println("Detection complete");
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
			captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset,
					Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90,
					Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg/2);
		} else
		if(e.getActionCommand()=="Detect")
		{
			File l_basefile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_base.png");
			File l_infile = new File(Eora3D_MainWindow.m_e3d_config.sm_image_dir.toString()+File.separatorChar+"calib_"+txtCalibImg.getText()+".png");
			BufferedImage l_inimage, l_baseimage;

			try {
				l_baseimage = ImageIO.read(l_basefile);
				l_inimage = ImageIO.read(l_infile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
			image.m_image = analyzeImage(l_baseimage, l_inimage);;
			image.m_overlay = null;
			image.repaint();
		}
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

        		int l_thresh = 140;
        		if(r>l_thresh || g>l_thresh || b>l_thresh) argb = 0xff00ff00;
        		else argb = 0xff000000;
//        		argb = (r << 16) | (g << 8) | (b);

//				argb = 0x00ffffff - argb;				
//				argb |= 0xff000000;
				a_out.setRGB(x, y, argb);
				if(argb == 0xff00ff00) break;
			}
		}
		return a_out;
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

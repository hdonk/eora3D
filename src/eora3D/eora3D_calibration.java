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

import javax.swing.JButton;
import javax.swing.JPanel;
/*import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;*/
import javax.swing.JLabel;
import javax.swing.JTextField;





import javax.imageio.ImageIO;
import java.awt.*;

class CircleDetection {
    private BufferedImage grey;
    private int[][] sobelX;
    private int[][] sobelY;
    private double[][] sobelTotal;
    public int threshold = 300;
	public int[][][] A;
	
	int minr = 10;
	int maxr = 40;
	
	BufferedImage displayimage = null;
	public int minHits = 200;

    void detect(BufferedImage image) throws Exception{

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
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                if(sobelTotal[i][j]>max){
                    max = sobelTotal[i][j];
                }
            }
        }
        //displayimage = total;
      for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
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
/*        BufferedImage totalColour = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < grey.getWidth(); i++) {
        	for(int j = 0; j < grey.getHeight(); j++) {
        		Color c1 = new Color(0,255,0);
        		if(sobelTotal[i][j] > threshold) {
        			totalColour.setRGB(i, j, c1.getRGB());
        		}
        	}
        }
        displayimage = totalColour;*/
        
        circleDetection(total);
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


        for(int i = 0; i<grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight();j++){
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


        for(int i = 0; i<grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight();j++){
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
        for(int i = 0; i <grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                sobelTotal[i][j] = Math.round(Math.sqrt(Math.pow((double)sobelX[i][j],2) + Math.pow((double)sobelY[i][j],2)));
            }
        }
    }
    
    private void circleDetection(BufferedImage image) throws Exception {
        //sets the radius relative to 1/6 of the smallest side of the image, helps reduce space taken in memory during
        //runtime
        int radius = maxr;
/*        if (image.getHeight() < image.getWidth()) {
            radius = image.getHeight() / 6;
        } else {
            radius = image.getWidth() / 6;
        }*/
        A = new int[image.getWidth()][image.getHeight()][radius];

//        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int rad = 0; rad < radius; rad++) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    //if the given pixel is above the threshold, a circle will be drawn at radius rad around it and if it
                    //is a valid coordinate it will be accumulated in the A array and plotted in the pointSpace image
                    if (sobelTotal[x][y] > threshold) {
//                    	System.out.println("Circle @ ("+x+","+y+") r="+rad);
                        for (int t = 0; t <= 360; t++) {
                            Integer a = (int) Math.floor(x - rad * Math.cos(t * Math.PI / 180));
                            Integer b = (int) Math.floor(y - rad * Math.sin(t * Math.PI / 180));
                            if (!((0 > a || a > image.getWidth() - 1) || (0 > b || b > image.getHeight() - 1))) {
/*                                Color c = new Color(newImage.getRGB(a, b));
                                Color c1;
                                if (c.getBlue() == 255) {
                                    c1 = new Color(c.getRed(), c.getGreen() + 1, 0);
                                } else if (c.getGreen() == 255) {
                                    c1 = new Color(c.getRed() + 1, 0, c.getBlue());
                                } else {
                                    c1 = new Color(c.getRed(), c.getGreen(), c.getBlue() + 1);
                                }
                                newImage.setRGB(a, b, c1.getRGB());*/
                                if (!(a.equals(x) && b.equals(y))) {
                                   A[a][b][rad] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        //displayimage = newImage;

        BufferedImage totalCircles = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = totalCircles.createGraphics();
        g.setColor(Color.RED);

        System.out.println("Looking for potential centers");
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int r = minr; r < maxr; r++) {
                    if (A[x][y][r] > minHits) {
                        double a =  x - r * Math.cos(0 * Math.PI / 180);
                        double b =  y - r * Math.sin(90 * Math.PI / 180);
                        g.drawOval((int)a,(int)b,2*r,2*r);
                        System.out.println("A "+A[x][y][r]+ "("+x+","+y+") r="+r);
//                        break;
                    }
                }
            }
        }
        displayimage = totalCircles;
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
 
  public PaintImage ()
  {
    super();
  }

  public void paintComponent(Graphics g)
  {
	  if(m_image == null) return;
    g.drawImage(m_image, 0, 0, null);
    if(m_overlay != null) g.drawImage(m_overlay, 0, 0, null);
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
		
		JLabel lblResRatio = new JLabel("Threshold");
		lblResRatio.setBounds(121, 756, 203, 15);
		getContentPane().add(lblResRatio);
		
		txtThreshold = new JTextField();
		txtThreshold.setText("300");
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
				System.out.println("Detecting circles");
				l_cd.detect(capturedImage);
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
		}
	}
}

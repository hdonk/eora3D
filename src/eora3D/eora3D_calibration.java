package eora3D;

import javax.swing.JDialog;

import com.github.sarxos.webcam.Webcam;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
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
import java.util.ArrayList;

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
import static org.lwjgl.opengles.GLES30.glBindVertexArray;
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

class PointCloudData
{
	float x_pos = 0.0f;
	float y_pos = 0.0f;
	float z_pos = 0.0f;

	float x_rot = 0.0f;
	float y_rot = 0.0f;
	float z_rot = 0.0f;
};

class RGBPoint
{
	public int m_x;
	public int m_y;
	public int m_z;
	public int m_r;
	public int m_g;
	public int m_b;
	
	public RGBPoint(int a_x, int a_y, int a_z, int a_r, int a_g, int a_b)
	{
		m_x = a_x;
		m_y = a_y;
		m_z = a_z;
		m_r = a_r;
		m_g = a_g;
		m_b = a_b;
	}
}

class PointCloudObject {
	int m_point_vbo;
	int m_point_ibo;
	float m_scalefactor = 1.0f;
	ArrayList<RGBPoint> m_points;
	boolean m_refresh = false;
	private IntBuffer m_intbuffer;
	
	public PointCloudObject()
	{
		clear();
	}
	
	public void clear()
	{
		m_points = new ArrayList<RGBPoint>();
/*		m_point_vbo = -1;
		m_point_ibo = -1;
        if(m_intbuffer != null)
        {
        	glBindBuffer(GL_ARRAY_BUFFER, 0);
        	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        	glDeleteBuffers(m_intbuffer);
        	m_intbuffer = null;
        }*/
	}

	public void glclear()
	{
    	glBindBuffer(GL_ARRAY_BUFFER, 0);
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    	if(m_intbuffer!=null) glDeleteBuffers(m_intbuffer);
	}
	
	boolean GLok(String message)
	{
		int errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("GL Error "+errorCheckValue+" "+message);
			Thread.dumpStack();
			return false;
		}
		return true;
	}

	public void draw()
	{
		synchronized(this)
		{
			if(m_refresh)
			{
				int l_vertexcount = m_points.size();
		        // Number of bytes we need per vertex.
		        int l_vertexsize = 3*4 + 4*4;
	
		        System.out.println("Refreshing points");
		        if(m_intbuffer != null)
		        {
		        	glBindBuffer(GL_ARRAY_BUFFER, 0);
		        	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		        	glDeleteBuffers(m_intbuffer);
		        }
		        m_intbuffer = BufferUtils.createIntBuffer(2);
		        glGenBuffers(m_intbuffer);
		        GLok("glGenBuffers");
		        m_point_vbo = m_intbuffer.get(0);
		        m_point_ibo = m_intbuffer.get(1);
		        glBindBuffer(GL_ARRAY_BUFFER, m_point_vbo);
		        GLok("glBindBuffer");
		        glBufferData(GL_ARRAY_BUFFER, l_vertexcount*l_vertexsize, GL_STATIC_DRAW);
		        GLok("glBufferData");
		        FloatBuffer vertexBuffer = OESMapbuffer.glMapBufferOES(GL_ARRAY_BUFFER,
		                GLES32.GL_WRITE_ONLY, null).asFloatBuffer();
		        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_point_ibo);
		        GLok("glBindBuffer");
		        glBufferData(GL_ELEMENT_ARRAY_BUFFER, l_vertexcount*4, GL_STATIC_DRAW);
		        GLok("glBufferData");
		        IntBuffer indexBuffer = OESMapbuffer.glMapBufferOES(GL_ELEMENT_ARRAY_BUFFER,
		                GLES32.GL_WRITE_ONLY, null).asIntBuffer();
	
		        for(int i=0; i<m_points.size(); ++i)
		        {
		        	RGBPoint l_pt = m_points.get(i);
		        	float x = (float)l_pt.m_x;
		        	float y = (float)l_pt.m_y;
		        	float z = (float)l_pt.m_z;
		        	float r = (float)l_pt.m_r/255.0f;
		        	float g = (float)l_pt.m_g/255.0f;
		        	float b = (float)l_pt.m_b/255.0f;
	                vertexBuffer.put((float) x);
	                vertexBuffer.put((float) y);
	                vertexBuffer.put((float) z);
	                vertexBuffer.put((float) r);
	                vertexBuffer.put((float) g);
	                vertexBuffer.put((float) b);
	                vertexBuffer.put((float) 1.0f);
	                indexBuffer.put(i);
		        }
			            
		        // Tell openGL that we filled the buffers.
		        OESMapbuffer.glUnmapBufferOES(GL_ARRAY_BUFFER);
		        OESMapbuffer.glUnmapBufferOES(GL_ELEMENT_ARRAY_BUFFER);
	
		        m_refresh = false;
		        System.out.println("Refreshed points "+m_point_vbo+" "+m_point_ibo);
		        System.gc();
			}
			if(m_points.size()==0) return;
			//System.out.println("Displaying "+m_points.size()+" points "+m_point_vbo+" "+m_point_ibo);
			glBindBuffer(GL_ARRAY_BUFFER, m_point_vbo);
			if(!GLok("Setting glBindBuffer)")) return;
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_point_ibo);
			if(!GLok("Setting glBindBuffer")) return;
			glEnableVertexAttribArray(0);
			if(!GLok("Setting glEnableVertexAttribArray (vertex)")) return;
			glEnableVertexAttribArray(1);
			if(!GLok("Setting glEnableVertexAttribArray (color)")) return;
			
			int l_vertexstride = 3 * 4 + 4 * 4;
			glVertexAttribPointer(0, 3, GL_FLOAT, false, l_vertexstride, 0);
			if(!GLok("Setting glVertexAttribPointer (vertex)")) return;
			glVertexAttribPointer(1, 4, GL_FLOAT, false, l_vertexstride, 3*4);
			if(!GLok("Setting glVertexAttribPointer (color)")) return;
			glBindVertexArray(0);
			if(!GLok("Setting glBindVertexArray")) return;
			
			
			glDrawElements(GL_POINTS, m_points.size(), GL_UNSIGNED_INT, 0);
			if(!GLok("glDrawElements")) return;
		}
	}

	public void addPoint(int a_x, int a_y, int a_z, int a_r, int a_g, int a_b)
	{
		synchronized(this)
		{
			RGBPoint l_point = new RGBPoint(a_x, a_y, a_z, a_r, a_g, a_b);
			this.m_points.add(l_point);
			m_refresh = true;
		}
	}
}

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
	public int v_offset = 0;
	public int v_offset_2 = 0;
	
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
	
	public int v_offset_minmax = 0;

	public int detection_box = 20;
	public int circle_center_threshold = 5;
	public int circle_radius_threshold = 3;
	
	public Point cal_square_bl;
	public Point cal_square_br;
	public Point cal_square_tl;
	public Point cal_square_tr;
	
	public int m_minz = 0;
	public int m_maxz = 0;
	
	void calculate()
	{
		if(capture_w > capture_h) // landscape
		{
			double target_h = ((double)capture_h * cal_pos_1_per);
			double target_w = (target_h/board_h)*board_w;
			pos_1_board = new Rectangle();
			pos_1_board.height = (int)target_h;
			v_offset_minmax = (capture_h - (int)target_h)/2;
			pos_1_board.width = (int)target_w;
			pos_1_board.x = (capture_w-pos_1_board.width)/2;
			pos_1_board.y = (capture_h-pos_1_board.height)/2 + v_offset;
			
			pos_1_tl = new Rectangle();
			pos_1_tl.width = (int)(((target_w/board_w)*spot_r))*2;
			pos_1_tl.height = (int)(((target_h/board_h)*spot_r))*2;
			pos_1_tl.x = (int)(capture_w -((target_w/board_w)*spot_sep_w))/2 - pos_1_tl.width;
			pos_1_tl.y = (int)(capture_h -((target_h/board_h)*spot_sep_h))/2 - pos_1_tl.height + v_offset;

			pos_1_tr = new Rectangle();
			pos_1_tr.width = pos_1_tl.width;
			pos_1_tr.height = pos_1_tl.height;
			pos_1_tr.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
			pos_1_tr.y = pos_1_tl.y;

			pos_1_bl = new Rectangle();
			pos_1_bl.width = pos_1_tl.width;
			pos_1_bl.height = pos_1_tl.height;
			pos_1_bl.x = pos_1_tl.x;
			pos_1_bl.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2 + v_offset*2;

			pos_1_br = new Rectangle();
			pos_1_br.width = pos_1_tl.width;
			pos_1_br.height = pos_1_tl.height;
			pos_1_br.x = (int)(capture_w - pos_1_tl.x) - pos_1_tl.width*2;
			pos_1_br.y = (int)(capture_h - pos_1_tl.y) - pos_1_tl.height*2 + v_offset*2;

		
			target_h = ((double)capture_h * cal_pos_2_per);
			target_w = (target_h/board_h)*board_w;
			v_offset_2 = (int)(((double)v_offset) * (cal_pos_2_per/cal_pos_1_per));
			pos_2_board = new Rectangle();
			pos_2_board.height = (int)target_h;
			pos_2_board.width = (int)target_w;
			pos_2_board.x = (capture_w-pos_2_board.width)/2;
			pos_2_board.y = (capture_h-pos_2_board.height)/2 + v_offset_2;
			
			pos_2_tl = new Rectangle();
			pos_2_tl.width = (int)(((target_w/board_w)*spot_r))*2;
			pos_2_tl.height = (int)(((target_h/board_h)*spot_r))*2;
			pos_2_tl.x = (int)(capture_w -((target_w/board_w)*spot_sep_w))/2 - pos_2_tl.width;
			pos_2_tl.y = (int)(capture_h -((target_h/board_h)*spot_sep_h))/2 - pos_2_tl.height + v_offset_2;

			pos_2_tr = new Rectangle();
			pos_2_tr.width = pos_2_tl.width;
			pos_2_tr.height = pos_2_tl.height;
			pos_2_tr.x = (int)(capture_w - pos_2_tl.x) - pos_2_tl.width*2;
			pos_2_tr.y = pos_2_tl.y;

			pos_2_bl = new Rectangle();
			pos_2_bl.width = pos_2_tl.width;
			pos_2_bl.height = pos_2_tl.height;
			pos_2_bl.x = pos_2_tl.x;
			pos_2_bl.y = (int)(capture_h - pos_2_tl.y) - pos_2_tl.height*2 + v_offset_2*2;

			pos_2_br = new Rectangle();
			pos_2_br.width = pos_2_tl.width;
			pos_2_br.height = pos_2_tl.height;
			pos_2_br.x = (int)(capture_w - pos_2_tl.x) - pos_2_tl.width*2;
			pos_2_br.y = (int)(capture_h - pos_2_tl.y) - pos_2_tl.height*2 + v_offset_2*2;
		}
	}
	
	void calculateBaseCoords()
	{//cal_square_bl
		double l_to_camera_steps = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
		double alpha = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double beta = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double alpha_prime = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tr_motorpos_2)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double beta_prime = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_2)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		
		double x, y;
		
		cal_square_bl = new Point();
		cal_square_br = new Point();
		cal_square_tl = new Point();
		cal_square_tr = new Point();
		
		x = (((double)pos_1_board.width)*Math.tan(Math.toRadians(alpha)))/(Math.tan(Math.toRadians(beta))-Math.tan(Math.toRadians(alpha)));
		cal_square_bl.x = (int)x;
		y = x*Math.tan(Math.toRadians(beta));
		cal_square_bl.y = (int)y;
		cal_square_br.y = (int)y;
		cal_square_br.x = cal_square_bl.x + pos_1_board.width;
		
		x = (((double)pos_2_board.width)*Math.tan(Math.toRadians(alpha_prime)))/(Math.tan(Math.toRadians(beta_prime))-Math.tan(Math.toRadians(alpha_prime)));
		cal_square_tl.x = (int)x;
		y = x*Math.tan(Math.toRadians(beta_prime));
		cal_square_tl.y = (int)y;
		cal_square_tr.y = (int)y;
		cal_square_tr.x = cal_square_bl.x + pos_2_board.width;
	}
	
	int getZoffset(int a_steps, int screen_x)
	{
		double l_to_camera_steps = Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90;
		double alpha = (l_to_camera_steps - Eora3D_MainWindow.m_e3d_config.sm_calibration_tl_motorpos_1)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		double theta = (l_to_camera_steps - a_steps)/Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg;
		
		double x_pos = (double)cal_square_bl.x + (double)screen_x;
		
		double tan_theta = Math.tan(Math.toRadians(theta));
		double tan_alpha = Math.tan(Math.toRadians(alpha));
		
		double delta = x_pos*tan_theta -  x_pos*tan_alpha;

//		if(delta<0f)
		{
			System.out.println("Delta:" + delta);
		}
		int z = (int)(delta + (double)cal_square_bl.y)+500;
		if(z<m_minz)
		{
			m_minz = z;
			System.out.println("Min Z: "+m_minz);
		}
		if(z>m_maxz)
		{
			m_maxz = z;
			System.out.println("Max Z: "+m_maxz);
		}
		return z;
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
	private BufferedImage totalCircles = null;

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

        Graphics2D g = null;
        if(totalCircles != null)
        {
        	displayimage = totalCircles;
        	g = totalCircles.createGraphics();
        	g.setColor(Color.RED);
        }

//        System.out.println("Looking for potential centers");
        for (int x = searcharea.x; x < searcharea.x+searcharea.width; x++) {
            for (int y = searcharea.y; y < searcharea.y+searcharea.height; y++) {
                for (int r = minr; r < maxr; r++) {
                    if (A[x][y][r] > minHits) {
                        double a =  x - r * Math.cos(0 * Math.PI / 180);
                        double b =  y - r * Math.sin(90 * Math.PI / 180);
                      if(g != null) g.drawOval((int)a,(int)b,2*r,2*r);
//                        System.out.println("A "+A[x][y][r]+ "("+x+","+y+") r="+r);
                        return new Rectangle(x, y, r, r);
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
	PointCloudData m_pcd = null;
	PointCloudObject m_pco = null;
	float m_rot = 0.0f;
	private Thread m_thread = null;
	
	long lastTime = 0;
	private Thread m_detect_thread = null;
	private JLabel lblPointSize;
	private JLabel lblScale;
	private JScrollBar sbPointSize;
	private JScrollBar sbScale;

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
		sbScale.setMinimum(10);
		sbScale.setMaximum(40);
		sbScale.setBounds(976, 886, 129, 17);
		getContentPane().add(sbScale);

		m_camera = a_camera;
		
		setSize(1300,1020);
		
		calculateCalibrationPositions();
		
		setModal(true);

		m_pcd = new PointCloudData();
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
		float[] vertices = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // Color
				100.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // Color

				0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // Color
				0.0f, 200.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // Color

				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Color
				0.0f, 0.0f, 100.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Color
				0 };
		int[] indices = { 0, 1, 2, 3, 4, 5 };
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
//				.setOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f)
				.setPerspective((3.14159f * 2.0f) / 3.0f, (float) displayW / (float) displayH, 0.01f, 6000.0f);
		viewM.identity();
//		viewM
//				.lookAt(m_pcd.x_pos, m_pcd.y_pos, m_pcd.z_pos+2000.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		viewM
		.lookAt((m_rot-180.0f)*10.0f, 0.0f, 2000.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
/*		viewM.translate(m_pcd.x_pos, m_pcd.y_pos, m_pcd.z_pos);
		viewM.rotate(q.rotateZ((float) Math.toRadians(m_pcd.z_rot)).normalize())
				.rotate(q0.rotateY((float) Math.toRadians(m_pcd.y_rot)).normalize())
				.rotate(q1.rotateX((float) Math.toRadians(m_pcd.x_rot)).normalize());*/
		modelM.identity();
		modelM.translate(0.0f, 0.0f, 100.0f);
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
		//modelM.translate(0.0f, 0.0f, m_rot);
//		modelM.rotate(q.rotateY((float) Math.toRadians(m_rot)).normalize());
		//.rotate(q.rotateZ((float) Math.toRadians(m_rot)).normalize())
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
		m_rot += delta * 10f;
		if (m_rot > 360.0f) {
			m_rot = 0.0f;
		}
		//m_rot = 176.0f;
		System.out.println("Rot: "+m_rot);
		lastTime = thisTime;
	}



	private void calculateCalibrationPositions() {
		if(m_cal_data == null)
		{
			m_cal_data = new CalibrationData();
			m_cal_data.v_offset = Eora3D_MainWindow.m_e3d_config.sm_calibration_vertical_offset;
		}
		
		m_cal_data.capture_w = (int)m_camera.getViewSize().getWidth();
		m_cal_data.capture_h = (int)m_camera.getViewSize().getHeight();
		
		m_cal_data.calculate();
		
		image.m_cal_data = m_cal_data;
		
		sbHeightOffset.removeAdjustmentListener(this);
		sbHeightOffset.setMinimum(1);
		sbHeightOffset.setMaximum(m_cal_data.v_offset_minmax);
		sbHeightOffset.setValue(1);
		sbHeightOffset.addAdjustmentListener(this);

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
			captureScanChain(Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset,
					Eora3D_MainWindow.m_e3d_config.sm_laser_0_offset+Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg*90,
					Eora3D_MainWindow.m_e3d_config.sm_laser_steps_per_deg/2);
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
		int l_points = 0;
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
			
			int l_x_points[];
			image.m_image = l_baseimage;
			image.m_overlay = analyzeImage(l_baseimage, l_inimage);
			l_x_points = analyzeImagetoArray(l_baseimage, l_inimage);
			image.repaint();
			
			for( int i=0; i<l_baseimage.getHeight(); ++i)
			{
				if(l_x_points[i]>=0)
				{
					++l_points;
					System.out.println("Z calculated as "+l_x_points[i]+" -> "+m_cal_data.getZoffset(l_pos, l_x_points[i]));
					m_pco.addPoint(l_x_points[i]/50-l_baseimage.getWidth(),
							l_baseimage.getHeight()-i-1-l_baseimage.getHeight()/2,
							m_cal_data.getZoffset(l_pos, l_x_points[i])/50,
							(l_baseimage.getRGB(l_x_points[i], i) & 0xff0000)>>16,
							(l_baseimage.getRGB(l_x_points[i], i) & 0xff00)>>8,
							(l_baseimage.getRGB(l_x_points[i], i) & 0x00)
							);
				}
			}
			
			System.out.println("Complete "+l_infile.toString());
		}
		System.out.println("Found "+l_points+" points");
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
	
	int[] analyzeImagetoArray(BufferedImage a_base, BufferedImage a_in)
	{
		int[] a_out = new int[a_in.getHeight()];
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
		displayW = 1800;
		displayH = 1600;
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
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			}
		}
		m_pco.glclear();
        glfwHideWindow(m_window);
		GLES.setCapabilities(null);
		glfwFreeCallbacks(m_window);
		glfwTerminate();
	}
}

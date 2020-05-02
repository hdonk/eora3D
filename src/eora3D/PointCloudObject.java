package eora3D;

import static org.lwjgl.opengles.GLES30.GL_RGBA8;
import static org.lwjgl.opengles.GLES30.glBindVertexArray;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengles.GLES32;
import org.lwjgl.opengles.OESMapbuffer;

import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengles.GLESCapabilities;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
//import org.lwjgl.glfw.*;
//import org.lwjgl.opengl.GLUtil.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWNativeEGL.*;
import static org.lwjgl.egl.EGL15.*;
import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import eora3D.RGB3DPoint;

import org.smurn.jply.util.RectBounds;
import org.smurn.jply.*;
import org.smurn.jply.util.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class PointCloudObject/* extends JFrame*/ implements Runnable/*, AdjustmentListener, ActionListener, WindowListener*/ {
	EGLCapabilities m_egl;
	GLESCapabilities m_gles;

	public long m_window;
	private int m_main_program;
	private int m_point_program;
	private int m_point_program_col;
	private int displayW;
	private int displayH;
//	PointCloudData m_pcd = null;
	float m_rot = 0.0f;
	private Thread m_thread = null;
	
	long lastTime = 0;

	int m_point_vbo;
	int m_point_ibo;
	ArrayList<ArrayList<RGB3DPoint>> m_points;
	public boolean m_refresh = false;
	private IntBuffer m_intbuffer;

	public float m_Pointsize;
	public float m_Scale;

	public boolean m_finished = false;
	
	ArrayList<Integer> m_vertexcount = null;
	ArrayList<Integer> m_vertexdisplaycount = null;
	ArrayList<Integer> m_vertexoffset = null;
	public float m_tt_angle = 0.0f;
	public int m_Zrotoff = 0;
	public int m_Xrotoff = 0;
	public int m_Ymodeloff = 0;

	private int m_leftfilter = -5000;
	private int m_rightfilter = 5000;
	private int m_topfilter = 5000;
	private int m_bottomfilter = -5000;
	private int m_frontfilter = -5000;
	private int m_backfilter = 5000;
	
	public double m_pix_to_mm = 1.0f;
	public boolean m_stoprotation;
	public int m_YViewOffset = 0;
/*	private JTextField tfObjectYRotation;
	private JScrollBar sbPointsize;
	private JScrollBar sbScale;
	private JCheckBox cbContinuousRotation;*/
	private PaintImage glpanel;
	boolean m_stop_gl_thread = false;
	
	public PointCloudObject(PaintImage a_paintimage)
	{
		glpanel = a_paintimage;
		clear();

/*		getContentPane().setLayout(null);
		
		setResizable(false);
		setSize(1024,690);
		addWindowListener(this);
		
		glpanel = new PaintImage(false);
		glpanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		glpanel.setBounds(6, 6, 492, 639);
		getContentPane().add(glpanel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(507, 6, 266, 504);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Scale");
		lblNewLabel.setBounds(6, 23, 55, 16);
		panel_1.add(lblNewLabel);
		
		sbScale = new JScrollBar();
		sbScale.setOrientation(JScrollBar.HORIZONTAL);
		sbScale.setBounds(16, 41, 230, 21);
		sbScale.setValue(1000);
		sbScale.setMinimum(1);
		sbScale.setMaximum(6000);
		panel_1.add(sbScale);
		sbScale.addAdjustmentListener(this);
		panel_1.add(sbScale);
		
		JLabel lblNewLabel_1 = new JLabel("Point size");
		lblNewLabel_1.setBounds(6, 66, 55, 16);
		panel_1.add(lblNewLabel_1);
		
		sbPointsize = new JScrollBar();
		sbPointsize.setOrientation(JScrollBar.HORIZONTAL);
		sbPointsize.setBounds(16, 85, 230, 21);
		sbPointsize.setMinimum(10);
		sbPointsize.setValue(10);
		sbPointsize.setMaximum(80);
		sbPointsize.addAdjustmentListener(this);
		panel_1.add(sbPointsize);
		
		JLabel lblObjectRotation = new JLabel("Object rotation");
		lblObjectRotation.setBounds(6, 118, 136, 16);
		panel_1.add(lblObjectRotation);
		
		tfObjectYRotation = new JTextField();
		tfObjectYRotation.setBounds(16, 142, 122, 28);
		panel_1.add(tfObjectYRotation);
		tfObjectYRotation.setColumns(10);
		
		cbContinuousRotation = new JCheckBox("Continuous");
		cbContinuousRotation.setBounds(16, 176, 104, 18);
		cbContinuousRotation.addActionListener(this);
		panel_1.add(cbContinuousRotation);
		
		setVisible(true);*/
	}
	
	public void clear()
	{
		m_vertexcount = new ArrayList<Integer>();
		m_vertexdisplaycount = new ArrayList<Integer>();
		m_vertexoffset = new ArrayList<Integer>();
		m_points = new ArrayList<ArrayList<RGB3DPoint>>();
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
	
	public void update()
	{
		synchronized(this)
		{
//			System.out.println("Refresh: "+m_refresh);
			if(m_refresh)
			{
				int l_tot_vertex = 0;
				for(int i=0; i < m_points.size(); ++i)
				{
					m_vertexcount.set(i, Integer.valueOf(m_points.get(i).size()) );
					m_vertexoffset.set(i, Integer.valueOf(l_tot_vertex) );
					l_tot_vertex += m_vertexcount.get(i);
				}
				if(l_tot_vertex==0)
				{
					m_refresh = false;
					return;
				}
//				System.out.println("Points: "+l_vertexcount);
		        // Number of bytes we need per vertex.
		        int l_vertexsize = 3*4 + 4*4;
	
		        //System.out.println("Refreshing points");
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
		        glBufferData(GL_ARRAY_BUFFER, l_tot_vertex*l_vertexsize, GL_STATIC_DRAW);
		        GLok("glBufferData");
		        FloatBuffer vertexBuffer = OESMapbuffer.glMapBufferOES(GL_ARRAY_BUFFER,
		                GLES32.GL_WRITE_ONLY, null).asFloatBuffer();
		        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_point_ibo);
		        GLok("glBindBuffer");
		        glBufferData(GL_ELEMENT_ARRAY_BUFFER, l_tot_vertex*4, GL_STATIC_DRAW);
		        GLok("glBufferData");
		        IntBuffer indexBuffer = OESMapbuffer.glMapBufferOES(GL_ELEMENT_ARRAY_BUFFER,
		                GLES32.GL_WRITE_ONLY, null).asIntBuffer();
		        
		        int l_count = 0;
		        for(int al=0; al<m_points.size(); ++al)
		        {
		        	int l_vc = 0;
		        	for(int j=0; j<m_points.get(al).size(); ++j)
			        {
		        		
			        	RGB3DPoint l_pt = m_points.get(al).get(j);
			        	if(
			        			l_pt.m_x >= m_leftfilter &&
	        					l_pt.m_x <= m_rightfilter &&
			    		
    							l_pt.m_y >= m_bottomfilter &&
								l_pt.m_y <= m_topfilter  &&
			    		
								l_pt.m_z >= m_frontfilter &&
								l_pt.m_z <= m_backfilter )
			        	{

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
			                indexBuffer.put(l_count++);
			                ++l_vc;
			        	}
			        }
		        	//System.out.println("filtered out "+(m_points.get(al).size()-l_vc));
		        	for(int j=0; j<m_points.get(al).size()-l_vc; ++j)
		        	{
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		vertexBuffer.put(0.0f);
		        		indexBuffer.put(l_count++);
		        	}
		        	m_vertexdisplaycount.set(al, Integer.valueOf(l_vc) );
		        }
			            
		        // Tell openGL that we filled the buffers.
		        OESMapbuffer.glUnmapBufferOES(GL_ARRAY_BUFFER);
		        OESMapbuffer.glUnmapBufferOES(GL_ELEMENT_ARRAY_BUFFER);
	
		        //System.out.println("Refreshed points "+m_point_vbo+" "+m_point_ibo);
		        System.gc();
			}
	        m_refresh = false;
		}
	}
	
	public void draw(int a_list)
	{
		synchronized(this)
		{
			if(m_vertexcount.get(a_list)==0) return;
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
			
			
			glDrawElements(GL_POINTS, m_vertexdisplaycount.get(a_list), GL_UNSIGNED_INT, m_vertexoffset.get(a_list)*4);
			if(!GLok("glDrawElements")) return;
		}
	}

	public void addPoint(int a_list, int a_x, int a_y, int a_z, int a_r, int a_g, int a_b)
	{
		synchronized(this)
		{
			RGB3DPoint l_point = new RGB3DPoint(a_x, a_y, a_z, a_r, a_g, a_b);
			if(m_points.size()<=a_list)
			{
				m_points.add(new ArrayList<RGB3DPoint>());
				m_vertexcount.add(Integer.valueOf(0));
				m_vertexoffset.add(Integer.valueOf(0));
				m_vertexdisplaycount.add(Integer.valueOf(0));
			}
			this.m_points.get(a_list).add(l_point);
			m_refresh = true;
		}
	}
	public void addPoint(int a_list, RGB3DPoint a_point)
	{
		synchronized(this)
		{
			while(m_points.size()<=a_list)
			{
				m_points.add(new ArrayList<RGB3DPoint>());
				m_vertexcount.add(Integer.valueOf(0));
				m_vertexoffset.add(Integer.valueOf(0));
				m_vertexdisplaycount.add(Integer.valueOf(0));
			}
			this.m_points.get(a_list).add(a_point);
			m_refresh = true;
		}
	}

	public void save(File a_file) {
		synchronized(this) {
			if(m_points.isEmpty())
				return;
			System.out.println("Starting");
			try
			{
		        for(int j=0; j<m_points.size(); ++j)
		        {
					FileOutputStream fos = new FileOutputStream(a_file+"_"+j+".ply");
			        Writer writer= new OutputStreamWriter(fos, "UTF8");
			        writer.write("ply\n");
			        writer.write("format ");
			        writer.write("ascii");
			        writer.write(" 1.0\n");
			        writer.write("comment ");
			        writer.write("Java E3D tool");
			        writer.write('\n');
		
			        // Count through how many points pass filter
			        int l_vc = 0;
			        for(int i=0; i<m_points.get(j).size(); ++i)
			        {
			    		if(
			    				m_points.get(j).get(i).m_x >= m_leftfilter &&
	    						m_points.get(j).get(i).m_x <= m_rightfilter &&
			    		
			    				m_points.get(j).get(i).m_y >= m_bottomfilter &&
	    						m_points.get(j).get(i).m_y <= m_topfilter  &&
			    		
			    				m_points.get(j).get(i).m_z >= m_frontfilter &&
			    				m_points.get(j).get(i).m_z <= m_backfilter ) ++l_vc;
			        }

			        
			        writer.write("element vertex "+l_vc+"\n");
			        writer.write("property double x\n");
			        writer.write("property double y\n");
			        writer.write("property double z\n");
			        writer.write("property uint8 red\n");
		    		writer.write("property uint8 green\n");
					writer.write("property uint8 blue\n");
		
			        writer.write("end_header\n");
			        writer.flush();
		        
		/*	        DataOutputStream dos=new DataOutputStream(fos);
			        dos.writeDouble(x);
			        dos.writeDouble(y);
			        dos.writeDouble(z);
		        dos.close();*/
			        for(int i=0; i<m_points.get(j).size(); ++i)
			        {
			        	if(
			    				m_points.get(j).get(i).m_x >= m_leftfilter &&
	    						m_points.get(j).get(i).m_x <= m_rightfilter &&
			    		
			    				m_points.get(j).get(i).m_y >= m_bottomfilter &&
	    						m_points.get(j).get(i).m_y <= m_topfilter  &&
			    		
			    				m_points.get(j).get(i).m_z >= m_frontfilter &&
			    				m_points.get(j).get(i).m_z <= m_backfilter )
			        	{
				        	writer.write(m_points.get(j).get(i).m_x+" ");
				        	writer.write(m_points.get(j).get(i).m_y+" ");
				        	writer.write(m_points.get(j).get(i).m_z+" ");
				        	writer.write(m_points.get(j).get(i).m_r+" ");
				        	writer.write(m_points.get(j).get(i).m_g+" ");
				        	writer.write(m_points.get(j).get(i).m_b+"\n");
			        	}
			        }
			        writer.close();
		        }
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Finished");
		}
	}
	
	public void save_apply_offsets(File a_file) {
		synchronized(this) {
			if(m_points.isEmpty())
				return;
			System.out.println("Starting");
			try
			{
				FileOutputStream fos = new FileOutputStream(a_file+".ply");
		        Writer writer= new OutputStreamWriter(fos, "UTF8");
		        writer.write("ply\n");
		        writer.write("format ");
		        writer.write("ascii");
		        writer.write(" 1.0\n");
		        writer.write("comment ");
		        writer.write("Java E3D tool");
		        writer.write('\n');

		        int l_vc = 0;
		        // Count through how many points pass filter
		        for(int j=0; j<m_points.size(); ++j)
		        {
			        for(int i=0; i<m_points.get(j).size(); ++i)
			        {
			    		if(
			    				m_points.get(j).get(i).m_x >= m_leftfilter &&
	    						m_points.get(j).get(i).m_x <= m_rightfilter &&
			    		
			    				m_points.get(j).get(i).m_y >= m_bottomfilter &&
	    						m_points.get(j).get(i).m_y <= m_topfilter  &&
			    		
			    				m_points.get(j).get(i).m_z >= m_frontfilter &&
			    				m_points.get(j).get(i).m_z <= m_backfilter ) ++l_vc;
			        }
		        }

		        writer.write("element vertex "+l_vc+"\n");
		        writer.write("property double x\n");
		        writer.write("property double y\n");
		        writer.write("property double z\n");
		        writer.write("property uint8 red\n");
	    		writer.write("property uint8 green\n");
				writer.write("property uint8 blue\n");
	
		        writer.write("end_header\n");
		        writer.flush();

		        for(int j=0; j<m_points.size(); ++j)
		        {
			        Matrix4f modelM = new Matrix4f();
					modelM.identity();
					Quaternionf q = new Quaternionf();
					modelM.rotate(q.rotateY((float) Math.toRadians(j*m_tt_angle)).normalize());
					modelM.translate(m_Xrotoff, m_Ymodeloff, -m_Zrotoff);
			        for(int i=0; i<m_points.get(j).size(); ++i)
			        {
			        	if(
			    				m_points.get(j).get(i).m_x >= m_leftfilter &&
	    						m_points.get(j).get(i).m_x <= m_rightfilter &&
			    		
			    				m_points.get(j).get(i).m_y >= m_bottomfilter &&
	    						m_points.get(j).get(i).m_y <= m_topfilter  &&
			    		
			    				m_points.get(j).get(i).m_z >= m_frontfilter &&
			    				m_points.get(j).get(i).m_z <= m_backfilter )
			        	{
			        		Vector3f v = new Vector3f(m_points.get(j).get(i).m_x, m_points.get(j).get(i).m_y, m_points.get(j).get(i).m_z);
			        		modelM.transformPosition(v);
				        	writer.write(((float)v.x)*m_pix_to_mm+" ");
				        	writer.write(((float)v.y)*m_pix_to_mm+" ");
				        	writer.write(((float)v.z)*m_pix_to_mm+" ");
				        	writer.write(m_points.get(j).get(i).m_r+" ");
				        	writer.write(m_points.get(j).get(i).m_g+" ");
				        	writer.write(m_points.get(j).get(i).m_b+"\n");
			        	}
			        }
		        }
		        writer.close();
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Finished");
		}
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

	private void render() {

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

		float l_x_scale = 1.0f;
		float l_y_scale = 1.0f;
		if(displayW>displayH)
		{
			l_x_scale = (float)displayH/(float)displayW;
		}
		else
		{
			l_y_scale = (float)displayW/(float)displayH;			
		}
		
		
/*		
		float aspect = width / height;
		glViewport(0, 0, width, height);
		if (aspect >= 1.0)
		  glOrtho(-50.0 * aspect, 50.0 * aspect, -50.0, 50.0, 1.0, -1.0);
		else
		  glOrtho(-50.0, 50.0, -50.0 / aspect, 50.0 / aspect, 1.0, -1.0);*/
		
		
		
		
		projectM
				.setOrtho(-2000.0f/l_x_scale, 2000.0f/l_x_scale, -2000.0f/l_y_scale, 2000.0f/l_y_scale, -20000.0f, 20000.0f);
		viewM.identity();
		// User controlled front view
		viewM.lookAt(0.0f, (float)m_YViewOffset, 15000.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// Corner view
//		viewM.lookAt(0.0f, m_Ydispoff, 100.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		// Top view
		//viewM.lookAt(0.0f, 1000.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		modelM.identity();
		
		Quaternionf q = new Quaternionf();
		modelM.rotate(q.rotateY((float) Math.toRadians(m_rot)).normalize());

		glUseProgram(m_main_program);
		if (!GLok(""))
			return;
		modelViewLoc = glGetUniformLocation(m_main_program, "modelView");
		if (!GLok(""))
			return;
		modelView.identity();
		modelView.mul(projectM);
		modelView.mul(viewM);
		modelView.mul(modelM);
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
		scaleLoc = glGetUniformLocation(l_program, "scale");
		GLok("Retrieving scale uniform location");
		//glUniform1f(scaleLoc, (m_Scale/1000.0f)); 
		glUniform1f(scaleLoc, 1.0f);
		GLok("Set scale uniform");
		int pointsizeLoc = glGetUniformLocation(l_program, "pointsize");
		GLok("Retrieving pointsize uniform location");
		//System.out.println("Point size to "+sbPointSize.getValue()/10);
		glUniform1f(pointsizeLoc, m_Pointsize/10.0f); 
		GLok("Set pointsize uniform");

		glBindAttribLocation(l_program, 0, "vertex");
		errorCheckValue = glGetError();
		if (errorCheckValue != GL_NO_ERROR) {
			System.err.println("GL Error " + errorCheckValue);
			Thread.dumpStack();
			return;
		}
		//System.out.println("Running "+m_points.size()+" point sets");
		for(int i=0; i<m_points.size(); ++i)
		{
			modelM.identity();
//			System.out.println("Scale to "+(m_Scale/1000.0f));
			modelM.scale(m_Scale/1000.0f);
			q = new Quaternionf();
			modelM.rotate(q.rotateY((float) Math.toRadians(i*m_tt_angle+m_rot)).normalize());
			modelM.translate(m_Xrotoff, m_Ymodeloff, -m_Zrotoff);
			//modelM.translate(0.0f, 0.0f, -2000.0f);
			modelViewLoc = glGetUniformLocation(l_program, "modelView");
			if (!GLok("Calling glGetUniformLocation"))
				return;
			modelView.identity();
			modelView.mul(projectM);
			modelView.mul(viewM);
			modelView.mul(modelM);
			glUniformMatrix4fv(modelViewLoc, false, modelView.get(fb));
			if (!GLok("Setting glUniformMatrix4fv"))
				return;
			//System.out.println("Drawing "+i+" point set");
			draw(i);
		}

		long thisTime = System.nanoTime();
		float delta = (thisTime - lastTime) / 1E9f;
		if(!m_stoprotation) m_rot += delta * 10f;
		if (m_rot > 360.0f) {
			m_rot = 0.0f;
		}
		//m_rot = 176.0f;
//		System.out.println("Rot: "+m_rot);
		lastTime = thisTime;
		
	}

	int makeFBOTexture(int a_width, int a_height)
	{
		int l_fbo;
		int l_FBOTexture;
		int l_depthbuffer;
		
		l_fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, l_fbo);
		if (!GLok("Bind Framebuffer"))
			return 0;
		
		l_depthbuffer = glGenRenderbuffers();
		if (!GLok("glGenRenderbuffers"))
			return 0;
		glBindRenderbuffer(GL_RENDERBUFFER, l_depthbuffer);
		if (!GLok("l_depthbuffer"))
			return 0;
	
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, a_width, a_height);
		if (!GLok("glRenderbufferStorage"))
			return 0;

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, l_depthbuffer);
		if (!GLok("glFramebufferRenderbuffer"))
			return 0;

		l_FBOTexture = glGenTextures();
		if (!GLok("glGenTextures"))
			return 0;
		glBindTexture(GL_TEXTURE_2D, l_FBOTexture);
		if (!GLok("glBindTexture"))
			return 0;
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		if (!GLok("glTexParameterf"))
			return 0;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, a_width, a_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer)null);
		if (!GLok("glTexImage2D"))
			return 0;
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, l_FBOTexture, 0);
		if (!GLok("glFramebufferTexture2D"))
			return 0;
		
		int l_err = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if(l_err == GL_FRAMEBUFFER_COMPLETE)
		{
			System.out.println("Framebuffer creation successful");
			return l_fbo;
		}
		else
		{
			System.out.println("Failed to create FBO: "+l_err);
			return 0;
		}
	}
	
	BufferedImage getBufferedImageFromFBO(int a_width, int a_height)
	{
		ByteBuffer l_buffer = BufferUtils.createByteBuffer(a_width * a_height * 4);
		glReadPixels(0, 0, a_width, a_height, GL_RGBA, GL_UNSIGNED_BYTE, l_buffer);
		GLok("glReadPixels");
		BufferedImage l_image = new BufferedImage(a_width, a_height, BufferedImage.TYPE_3BYTE_BGR);
		byte[] array = ((DataBufferByte) l_image.getRaster().getDataBuffer()).getData();
		for(int y = 0; y < a_height; ++y)
		{
			for(int x = 0; x < a_width; ++x)
			{
				int i = (y*a_width + x)*3;
				int j = ((a_height-y-1)*a_width + x)*4;
				array[i+0] = l_buffer.get(j+2);
				array[i+1] = l_buffer.get(j+1);
				array[i+2] = l_buffer.get(j+0);
			}
		}
		return l_image;
	}
	
@Override
	public void run() {
		// Render with OpenGL ES

		System.out.println("Run");
		
		// Render with OpenGL ES

		System.out.println("Run");
		float scale = java.awt.Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;
		System.out.println("Res: " + java.awt.Toolkit.getDefaultToolkit().getScreenResolution());
		displayW = 1;
		displayH = 1;
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
		
		int l_current_width = glpanel.getWidth();
		int l_current_height = glpanel.getHeight();
		int l_fbo = this.makeFBOTexture(l_current_width, l_current_height);
		displayW = l_current_width;
		displayH = l_current_height;
		
//		glfwShowWindow(m_window);

		while (!m_stop_gl_thread && !glfwWindowShouldClose(m_window)) {
			glfwPollEvents();
			glBindFramebuffer(GL_FRAMEBUFFER, l_fbo);
			update();
			render();
			BufferedImage l_image;
			l_image = getBufferedImageFromFBO(l_current_width, l_current_height);
			glpanel.m_image = l_image;
			glpanel.repaint();
				//System.out.println("Rendered");
		}
		
		glclear();
		m_finished = true;
	}

	public void Close()
	{
		if(m_finished) return;
		//glfwSetWindowShouldClose(m_window, true);
	}

	public void load(File a_file, int a_max_layer) {
		synchronized(this) {
			clear();
	        for(int j=0; j<a_max_layer; ++j)
	        {
	        	if(!load(j, new File(a_file+"_"+j+".ply"))) return;
	        }
		}
	}

	boolean load(int a_layer, File a_file)
	{
		PlyReaderFile l_prf;
		try
		{
			l_prf = new PlyReaderFile(a_file.getAbsolutePath());
		}
		catch(Exception e)
		{
			System.err.println("Failed to load "+a_file.getAbsolutePath());
			return false;
		}
		int pointcount = l_prf.getElementCount("vertex");
//		System.out.println("Vertex count "+pointcount);

		{
			int l_vertexcount = l_prf.getElementCount("vertex");

	        try
	        {
		        ElementReader reader = l_prf.nextElementReader();
		        for(int l_count = 0; l_count < l_vertexcount; ++l_count)
		        {
		            Element vertex = reader.readElement();
	                double x = vertex.getDouble("x");
	                double y = vertex.getDouble("y");
	                double z = vertex.getDouble("z");
	                double r = vertex.getInt("red");
	                double g = vertex.getInt("green");
	                double b = vertex.getInt("blue");
	                RGB3DPoint l_pt = new RGB3DPoint((int)x,(int) y, (int)z, (int)r, (int)g, (int)b);
	                this.addPoint(a_layer, l_pt);
		        }
	        }
	        catch(Exception e)
	        {
	        	System.err.println("Failed to read ply file "+a_file.getAbsolutePath());
	        	e.printStackTrace();
	    		try
	    		{
	    			l_prf.close();
	    		}
	    		catch(Exception ec)
	    		{
	    			System.err.println("Failed to close "+a_file.getAbsolutePath());
	    			ec.printStackTrace();
	    		}
	        	return false;
	        }
		            
		}
		try
		{
			l_prf.close();
		}
		catch(Exception e)
		{
			System.err.println("Failed to close "+a_file.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void setTT(float a_angle, int a_Zrotoff, int a_Xrotoff, int a_Ydispoff,
			int a_leftfilter,
			int a_rightfilter,
			int a_topfilter,
			int a_bottomfilter,
			int a_frontfilter,
			int a_backfilter) {
		m_tt_angle = a_angle;
		m_Zrotoff = a_Zrotoff;
		m_Xrotoff = a_Xrotoff;
		m_Ymodeloff = a_Ydispoff;
		
		m_leftfilter = a_leftfilter;
		m_rightfilter = a_rightfilter;
		m_topfilter = a_topfilter;
		m_bottomfilter = a_bottomfilter;
		m_frontfilter = a_frontfilter;
		m_backfilter = a_backfilter;

	}
/*
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		m_Pointsize = sbPointsize.getValue();
		m_Scale = sbScale.getValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		m_stoprotation = !this.cbContinuousRotation.isSelected();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		m_stop_gl_thread = true;
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
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
	*/
}

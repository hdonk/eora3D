package eora3D;

import static org.lwjgl.opengles.GLES20.GL_ARRAY_BUFFER;
import static org.lwjgl.opengles.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengles.GLES20.GL_FLOAT;
import static org.lwjgl.opengles.GLES20.GL_NO_ERROR;
import static org.lwjgl.opengles.GLES20.GL_POINTS;
import static org.lwjgl.opengles.GLES20.GL_STATIC_DRAW;
import static org.lwjgl.opengles.GLES20.GL_UNSIGNED_INT;
import static org.lwjgl.opengles.GLES20.glBindBuffer;
import static org.lwjgl.opengles.GLES20.glBufferData;
import static org.lwjgl.opengles.GLES20.glDeleteBuffers;
import static org.lwjgl.opengles.GLES20.glDrawElements;
import static org.lwjgl.opengles.GLES20.glEnableVertexAttribArray;
import static org.lwjgl.opengles.GLES20.glGenBuffers;
import static org.lwjgl.opengles.GLES20.glGetError;
import static org.lwjgl.opengles.GLES20.glVertexAttribPointer;
import static org.lwjgl.opengles.GLES30.glBindVertexArray;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengles.GLES32;
import org.lwjgl.opengles.OESMapbuffer;

class PointCloudObject {
	int m_point_vbo;
	int m_point_ibo;
	float m_scalefactor = 1.0f;
	ArrayList<RGB3DPoint> m_points;
	boolean m_refresh = false;
	private IntBuffer m_intbuffer;
	
	public PointCloudObject()
	{
		clear();
	}
	
	public void clear()
	{
		m_points = new ArrayList<RGB3DPoint>();
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
		        	RGB3DPoint l_pt = m_points.get(i);
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
			RGB3DPoint l_point = new RGB3DPoint(a_x, a_y, a_z, a_r, a_g, a_b);
			this.m_points.add(l_point);
			m_refresh = true;
		}
	}
	public void addPoint(RGB3DPoint a_point)
	{
		synchronized(this)
		{
			this.m_points.add(a_point);
			m_refresh = true;
		}
	}

	public void save(String a_file) {
		if(m_points.isEmpty())
			return;
		System.out.println("Starting");
		try
		{
			FileOutputStream fos = new FileOutputStream(a_file);
	        Writer writer= new OutputStreamWriter(fos, "UTF8");
	        writer.write("ply\n");
	        writer.write("format ");
	        writer.write("ascii");
	        writer.write(" 1.0\n");
	        writer.write("comment ");
	        writer.write("Java E3D tool");
	        writer.write('\n');
	        // lat,lon,alt as example
	        writer.write("element vertex "+m_points.size()+"\n");
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
	        for(int i=0; i<m_points.size(); ++i) {
	        	writer.write(m_points.get(i).m_x+"\n");
	        	writer.write(m_points.get(i).m_y+"\n");
	        	writer.write(m_points.get(i).m_z+"\n");
	        	writer.write(m_points.get(i).m_r+"\n");
	        	writer.write(m_points.get(i).m_g+"\n");
	        	writer.write(m_points.get(i).m_b+"\n");
	        }
	        writer.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Finished");
	}
}
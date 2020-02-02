package eora3D;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RGB3DPoint
{
	public int m_x;
	public int m_y;
	public int m_z;
	public int m_r;
	public int m_g;
	public int m_b;
	
	public RGB3DPoint(int a_x, int a_y, int a_z, int a_r, int a_g, int a_b)
	{
		m_x = a_x;
		m_y = a_y;
		m_z = a_z;
		m_r = a_r;
		m_g = a_g;
		m_b = a_b;
	}
	
	public RGB3DPoint(int a_x, int a_y, int a_z)
	{
		m_x = a_x;
		m_y = a_y;
		m_z = a_z;
		m_r = 0;
		m_g = 0;
		m_b = 0;
	}
	
	public RGB3DPoint()
	{
		m_x = 0;
		m_y = 0;
		m_z = 0;
		m_r = 0;
		m_g = 0;
		m_b = 0;
	}
	
	public void write(DataOutputStream a_dos)
	{
		try
		{
			a_dos.writeInt(m_x);
			a_dos.writeInt(m_y);
			a_dos.writeInt(m_z);
			a_dos.writeInt(m_r);
			a_dos.writeInt(m_g);
			a_dos.writeInt(m_b);
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public void read(DataInputStream a_dis)
	{
		try
		{
			m_x = a_dis.readInt();
			m_y = a_dis.readInt();
			m_z = a_dis.readInt();
			m_r = a_dis.readInt();
			m_g	 = a_dis.readInt();
			m_b = a_dis.readInt();
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
}
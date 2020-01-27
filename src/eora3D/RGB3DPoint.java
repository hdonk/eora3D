package eora3D;

import java.io.Serializable;

public class RGB3DPoint implements Serializable
{
	public int m_x;
	public int m_y;
	public int m_z;
	public int m_r;
	public int m_g;
	public int m_b;
	
	public float m_pointsize = 1;
	public float m_scale = 1.0f;
	
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
}
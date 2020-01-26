package eora3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

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
//	System.out.println("PI "+m_image);
	  float x_scale = 1.0f;
	  float y_scale = 1.0f;
    if(m_image == null) return;
	g.clearRect(0,  0,  getWidth(), getHeight());
	
	x_scale = (float)(m_image.getWidth())/(float)(getWidth());
	y_scale = x_scale;
	//System.out.println("1. x scale "+x_scale+", y scale "+y_scale);
	
	if(y_scale*(float)m_image.getHeight()>getHeight())
	{
		x_scale *= ((float)(getHeight()))/(((float)m_image.getHeight()));
		y_scale = x_scale;
		//System.out.println("2. x scale "+x_scale+", y scale "+y_scale);
	}
	
	
    g.drawImage(m_image, 0, 0, (int)(m_image.getWidth()*x_scale), (int)(m_image.getHeight()*y_scale), null);
    if(m_overlay != null) g.drawImage(m_overlay, 0, 0, (int)(m_image.getWidth()*x_scale), (int)(m_image.getHeight()*y_scale), null);
    
    g.setColor(Color.blue);
    if(pos==1)
    {
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_board.x*x_scale),
	    		(int)(m_cal_data.pos_1_board.y*x_scale),
	    		(int)(m_cal_data.pos_1_board.width*x_scale),
	    		(int)(m_cal_data.pos_1_board.height*x_scale)
	    		);
	    g.drawRect(
	    		0,
	    		0,
	    		(int)(m_cal_data.capture_w*x_scale),
	    		(int)(m_cal_data.capture_h*y_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_tl.x*x_scale),
				(int)(m_cal_data.pos_1_tl.y*y_scale),
				(int)(m_cal_data.pos_1_tl.width*x_scale),
				(int)(m_cal_data.pos_1_tl.height*y_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_tl.x*x_scale-m_cal_data.detection_box*x_scale/2),
				(int)(m_cal_data.pos_1_tl.y*y_scale-m_cal_data.detection_box*y_scale/2),
				(int)(m_cal_data.pos_1_tl.width*x_scale+m_cal_data.detection_box*x_scale),
				(int)(m_cal_data.pos_1_tl.height*y_scale+m_cal_data.detection_box*y_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_tr.x*x_scale),
				(int)(m_cal_data.pos_1_tr.y*x_scale),
				(int)(m_cal_data.pos_1_tr.width*x_scale),
				(int)(m_cal_data.pos_1_tr.height*y_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_tr.x*x_scale-m_cal_data.detection_box*x_scale/2),
				(int)(m_cal_data.pos_1_tr.y*y_scale-m_cal_data.detection_box*y_scale/2),
				(int)(m_cal_data.pos_1_tr.width*x_scale+m_cal_data.detection_box*x_scale),
				(int)(m_cal_data.pos_1_tr.height*y_scale+m_cal_data.detection_box*y_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_bl.x*x_scale),
				(int)(m_cal_data.pos_1_bl.y*y_scale),
				(int)(m_cal_data.pos_1_bl.width*x_scale),
				(int)(m_cal_data.pos_1_bl.height*y_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_bl.x*x_scale-m_cal_data.detection_box*x_scale/2),
				(int)(m_cal_data.pos_1_bl.y*y_scale-m_cal_data.detection_box*y_scale/2),
				(int)(m_cal_data.pos_1_bl.width*x_scale+m_cal_data.detection_box*x_scale),
				(int)(m_cal_data.pos_1_bl.height*y_scale+m_cal_data.detection_box*y_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_br.x*x_scale),
				(int)(m_cal_data.pos_1_br.y*y_scale),
				(int)(m_cal_data.pos_1_br.width*x_scale),
				(int)(m_cal_data.pos_1_br.height*y_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_br.x*x_scale-m_cal_data.detection_box*x_scale/2),
				(int)(m_cal_data.pos_1_br.y*y_scale-m_cal_data.detection_box*y_scale/2),
				(int)(m_cal_data.pos_1_br.width*x_scale+m_cal_data.detection_box*x_scale),
				(int)(m_cal_data.pos_1_br.height*y_scale+m_cal_data.detection_box*y_scale)
	    		);
    }
/*    else if(pos==2)
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
    }*/
  }
}
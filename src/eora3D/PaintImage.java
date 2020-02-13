package eora3D;

import java.awt.Color;
import java.awt.Dimension;
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
  boolean m_scale_to_fit = false;
  
  public PaintImage (boolean a_scale_to_fit)
  {
	  super();
	  m_scale_to_fit = a_scale_to_fit;
  }

  public void paintComponent(Graphics g)
  {
//	System.out.println("PI "+m_image);
	  float l_scale = 1.0f;
	BufferedImage l_image = m_image;
    if(l_image == null) l_image = m_overlay;
    if(l_image == null) return;
	g.clearRect(0,  0,  getWidth(), getHeight());
	
	if(m_scale_to_fit)
	{
		l_scale = (float)(getWidth())/(float)(l_image.getWidth());
		//System.out.println("1. x scale "+x_scale+", y scale "+y_scale);
		
		if(l_scale*(float)l_image.getHeight()>getHeight())
		{
			l_scale = ((float)(getHeight()))/(((float)l_image.getHeight()));
			//System.out.println("2. x scale "+x_scale+", y scale "+y_scale);
		}
	}
	else
	{
		l_scale = 1.0f;
		setPreferredSize(new Dimension(l_image.getWidth(), l_image.getHeight()));
		revalidate();
	}

    g.drawImage(l_image, 0, 0, (int)(l_image.getWidth()*l_scale), (int)(l_image.getHeight()*l_scale), null);
    if(m_overlay != null) g.drawImage(m_overlay, 0, 0, (int)(l_image.getWidth()*l_scale), (int)(l_image.getHeight()*l_scale), null);
    
    g.setColor(Color.blue);
    if(pos==1)
    {
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_board_pix.x*l_scale),
	    		(int)(m_cal_data.pos_1_board_pix.y*l_scale),
	    		(int)(m_cal_data.pos_1_board_pix.width*l_scale),
	    		(int)(m_cal_data.pos_1_board_pix.height*l_scale)
	    		);
	    g.drawRect(
	    		0,
	    		0,
	    		(int)(m_cal_data.capture_w_pix*l_scale),
	    		(int)(m_cal_data.capture_h_pix*l_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_tl_det_spot.x*l_scale),
				(int)(m_cal_data.pos_1_tl_det_spot.y*l_scale),
				(int)(m_cal_data.pos_1_tl_det_spot.width*l_scale),
				(int)(m_cal_data.pos_1_tl_det_spot.height*l_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_tl_det_spot.x*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_tl_det_spot.y*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_tl_det_spot.width*l_scale+m_cal_data.detection_box*l_scale),
				(int)(m_cal_data.pos_1_tl_det_spot.height*l_scale+m_cal_data.detection_box*l_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_tr_det_spot.x*l_scale),
				(int)(m_cal_data.pos_1_tr_det_spot.y*l_scale),
				(int)(m_cal_data.pos_1_tr_det_spot.width*l_scale),
				(int)(m_cal_data.pos_1_tr_det_spot.height*l_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_tr_det_spot.x*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_tr_det_spot.y*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_tr_det_spot.width*l_scale+m_cal_data.detection_box*l_scale),
				(int)(m_cal_data.pos_1_tr_det_spot.height*l_scale+m_cal_data.detection_box*l_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_bl_det_spot.x*l_scale),
				(int)(m_cal_data.pos_1_bl_det_spot.y*l_scale),
				(int)(m_cal_data.pos_1_bl_det_spot.width*l_scale),
				(int)(m_cal_data.pos_1_bl_det_spot.height*l_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_bl_det_spot.x*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_bl_det_spot.y*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_bl_det_spot.width*l_scale+m_cal_data.detection_box*l_scale),
				(int)(m_cal_data.pos_1_bl_det_spot.height*l_scale+m_cal_data.detection_box*l_scale)
	    		);
	    g.drawOval(
	    		(int)(m_cal_data.pos_1_br_det_spot.x*l_scale),
				(int)(m_cal_data.pos_1_br_det_spot.y*l_scale),
				(int)(m_cal_data.pos_1_br_det_spot.width*l_scale),
				(int)(m_cal_data.pos_1_br_det_spot.height*l_scale)
	    		);
	    g.drawRect(
	    		(int)(m_cal_data.pos_1_br_det_spot.x*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_br_det_spot.y*l_scale-m_cal_data.detection_box*l_scale/2),
				(int)(m_cal_data.pos_1_br_det_spot.width*l_scale+m_cal_data.detection_box*l_scale),
				(int)(m_cal_data.pos_1_br_det_spot.height*l_scale+m_cal_data.detection_box*l_scale)
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
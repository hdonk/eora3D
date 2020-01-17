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
    if(m_image == null) return;
	g.clearRect(0,  0,  getWidth(), getHeight());
    g.drawImage(m_image, 0, 0, getSize().width, getSize().height, null);
    if(m_overlay != null) g.drawImage(m_overlay, 0, 0, getSize().width, getSize().height, null);
    
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
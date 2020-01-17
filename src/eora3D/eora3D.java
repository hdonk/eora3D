package eora3D;

import java.awt.Dimension;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class eora3D {

	static Eora3D_MainWindow m_mainWindow;
	
	public static void main(String[] args) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
//		            UIManager.put( "ScrollBar.minimumThumbSize", new Dimension( 1, 1 ) ); 
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		m_mainWindow = new Eora3D_MainWindow();
	}

}

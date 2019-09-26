package unbbayes.io.mebn.em;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.junit.Test;

import unbbayes.gui.mebn.extension.output.OutputRenderer;

/**
 * Default test unit for {@link ReasoningOutputTableModel}
 * @author cardialfly
 */
public class ReasoningOutputTableModelTest {

	/**
	 * Test method for {@link unbbayes.io.mebn.em.ReasoningOutputTableModel#getDataFromStream(java.io.InputStream)}.
	 */
	@Test
	public void testGetDataFromStream() {

		// show dialog if environment variable is set 
		// (to indicate that we're running from eclipse IDE)
		boolean isToShowDialog = false;
		if (System.getenv("eclipse_IDE") != null
				&& "true".equalsIgnoreCase(System.getenv("eclipse_IDE").trim())) {
			isToShowDialog = true;
		}


        // create an input stream which reads a string
        String txt = "genderstyle__T11 female \r\n" + 
        		" 3,12%\r\n" + 
        		"genderstyle__T11 male \r\n" + 
        		" 96,88%\r\n" + 
        		"genderstyle__T6 female \r\n" + 
        		" 2,28%\r\n" + 
        		"genderstyle__T6 male \r\n" + 
        		" 97,72%\r\n";
        ByteArrayInputStream stream = new ByteArrayInputStream(txt.getBytes());
        
        // instantiate the object to test
        ReasoningOutputTableModel model = new ReasoningOutputTableModel(stream);
        assertEquals(3, model.getColumnCount());	// 3 columns: (node, state, prob)
        
        // make sure data was loaded from stream
        Object[][] data = model.getData();
        assertNotNull(data);
        assertEquals(4, data.length);		// 4 tuples of (node, state, prob) were read
        assertEquals(3, data[0].length);	// 3 columns: (node, state, prob)
        
        assertEquals("genderstyle__T11", data[0][0]);
        assertEquals("female", data[0][1]);
        assertEquals(3.12f, Float.parseFloat(data[0][2].toString().substring(0, data[0][2].toString().length()-1)), 0.005);
        
        assertEquals("genderstyle__T11", data[1][0]);
        assertEquals("male", data[1][1]);
        assertEquals(96.88f, Float.parseFloat(data[1][2].toString().substring(0, data[1][2].toString().length()-1)), 0.005);
        
        assertEquals("genderstyle__T6", data[2][0]);
        assertEquals("female", data[2][1]);
        assertEquals(2.28f, Float.parseFloat(data[2][2].toString().substring(0, data[2][2].toString().length()-1)), 0.005);
        
        assertEquals("genderstyle__T6", data[3][0]);
        assertEquals("male", data[3][1]);
        assertEquals(97.72f, Float.parseFloat(data[3][2].toString().substring(0, data[3][2].toString().length()-1)), 0.005);
        
        
        if (isToShowDialog) {
        	
			JOptionPane.showMessageDialog(null,
					new OutputRenderer(model), 
					"Output rendererer sample",
					JOptionPane.PLAIN_MESSAGE, null);
			

//	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//	            public void run() {
//	            	 JFrame frame = new JFrame("Output renderer sample");
//	                 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	                 
//	                 //Create and set up the content pane.
//	                 OutputRenderer newContentPane = new OutputRenderer(model);
//	                 newContentPane.setOpaque(true); //content panes must be opaque
//	                 frame.setContentPane(newContentPane);
//
//	                 //Display the window.
//	                 frame.pack();
//	                 frame.setVisible(true);
//	            }
//	        });
//	        
//	        try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        
        }
    
    
	}

}

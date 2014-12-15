package ann;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
  * Java Neural Network Example
  * Handwriting Recognition
  * by Jeff Heaton (http://www.jeffheaton.com) 1-2002
  * -------------------------------------------------
  * This class is used to provide a small component that
  * the user can draw handwritten letters into. This class
  * also contains the routines necessary to crop and downsample
  * the written character.
  *
  * @author Jeff Heaton (http://www.jeffheaton.com)
  * @version 1.0
  */
public class Entry extends JPanel {

   /**
    * The constructor.
    */
	Entry() {
		enableEvents(
			AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK
		);
	}

   /**
    * Paint the drawn image and cropping box
    * (if active).
    *
    * @param g The graphics context
    */
	public void paint(Graphics g) {		
		g.setColor(Color.black);
		g.drawRect(0,0,getWidth(),getHeight());
		g.setColor(Color.red);		
	}

	/**
	 * Process messages.
	 *
	 * @param e The event.
	 */
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() != MouseEvent.MOUSE_PRESSED)
			return;     
	}

	/**
	 * Process messages.
	 *
	 * @param e The event.
	 */
	protected void processMouseMotionEvent(MouseEvent e) {
		if (e.getID() != MouseEvent.MOUSE_DRAGGED)
			return;
	}
}
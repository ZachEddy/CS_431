/**
 * This object is a tool to accompany the <code>GraphicsWindow</code>, when use
 * of a mouse is desired.
 *
 * @author      Adam A. Smith
 * @version     1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MouseHandler extends MouseAdapter {
    private GraphicsWindow window;
    private int clickX = -1, clickY = -1, releaseX = -1, releaseY = -1;
    private int mouseX = -1, mouseY = -1;
    private boolean isMove = false, isClick = false;
    private int buttonPressed;

    public MouseHandler(GraphicsWindow window) {
	this.window = window;
    }

    /**
     * Clears both clicks and movement.
     * @since 1.0   
     */
    public void clearAll() {
	clearClick();
	clearMovement();
    }

    /**
     * Clears memory of the mouse clicking (but keeps the location data).
     * @since 1.0
     */
    public void clearClick() {
	isClick = false;
    }

    /**
     * Clears memory of a move event (but keeps the position data).
     * @since 1.0
     */
    public void clearMovement() {
	isMove = false;
    }

    /**
     * Grabs an int array with the coordinates of the last mouse click, or
     * <code>null</code> if there was no such click. It then clears this
     * information, so that subsequent calls will return <code>null</code>
     * unless there is another click.
     * @return the mouse-click coordinates, relative to the upper-left of the window's active area
     * @since 1.0
     */
    public int[] getClick() {
	if (clickX == -1) return null;
	int[] coords = new int[2];
	coords[0] = clickX;
	coords[1] = clickY;
	isClick = false;
	return coords;
    }

    /**
     * Returns the button that was pressed. Possible values are
     * <code>MouseEvent.BUTTON1</code> (left), <code>MouseEvent.BUTTON2</code>
     * (center), and <code>MouseEvent.BUTTON3</code> (right).
     * @return the button that was pressed
     * @since 1.0
     */
    public int getClickButton() {
	return buttonPressed;
    }

    /**
     * Returns the x-location of the click.
     * @return the x-location of the click within the active area of the
     * <code>GraphicsWindow</code>, or -1 if it is outside the region.
     * @since 1.0
     */
    public int getClickX() {
	return clickX;
    }

    /**
     * Returns the y-location of the click.
     * @return the y-location of the click within the active area of the
     * <code>GraphicsWindow</code>, or -1 if it is outside the region.
     * @since 1.0
     */
    public int getClickY() {
	return clickY;
    }

    /**
     * Return's the mouse's current location.
     * @return a 2D array indicating the mouse's location, or null if there is none
     * @since 1.0
     */
    public int[] getPosition() {
	if (mouseX == -1) return null;
	int[] coords = new int[2];
	coords[0] = mouseX;
	coords[1] = mouseY;
	return coords;
    }

    /**
     * Returns the x-location of the mouse.
     * @return the x-location of the mouse within the active area of the
     * <code>GraphicsWindow</code>, or -1 if it is outside the region.
     * @since 1.0
     */
    public int getPositionX() {
	return mouseX;
    }

    /**
     * Returns the y-location of the mouse.
     * @return the y-location of the mouse within the active area of the
     * <code>GraphicsWindow</code>, or -1 if it is outside the region.
     * @since 1.0
     */
    public int getPositionY() {
	return mouseY;
    }

    /**
     * Returns true if the <code>GraphicsWindow</code> has been clicked on.
     * @return true if the <code>GraphicsWindow</code> has been clicked on, otherwise false
     * @since 1.0
     */
    public boolean isClick() {
	return (isClick);
    }

    /**
     * Returns true if the mouse has moved on or through the
     * <code>GraphicsWindow</code>.
     * @return true if the <code>GraphicsWindow</code> has been moved on or
     * through, otherwise false
     * @since 1.0
     */
    public boolean isMovement() {
	return (isMove);
    }

    /**
     * Returns true if the <code>GraphicsWindow</code> has been shut down. This
     * is one of the possible reasons that the mouse got woken up, and so needs
     * to be tested for.
     * @return true if the <code>GraphicsWindow</code> has been shut down,
     * otherwise false
     * @since 1.0
     */
    public boolean isShutDown() {
	return (!window.isDisplayable());
    }

    /**
     * This method is called whenever the mouse is clicked. There is no need 
     * for your code to call it.
     * @param e a collection of properties of the mouse click
     * @since 1.0
     */
    @Override
    public void mouseClicked(MouseEvent e) {
	clickX = window.translateWindowX(e.getX());
	clickY = window.translateWindowY(e.getY());
	buttonPressed = e.getButton();
	isClick = true;
    }

    /**
     * This method is called whenever the mouse is moved. There is no need 
     * for your code to call it.
     * @param e a collection of properties of the mouse movement
     * @since 1.0
     */
    @Override
    public void mouseMoved(MouseEvent e) {
	int newX = window.translateWindowX(e.getX());
	int newY = window.translateWindowY(e.getY());

	// only set vars if there was some change
	if (newX != mouseX || newY != mouseY) {
	    mouseX = newX;
	    mouseY = newY;
	    isMove = true;
	}
    }

    /*@Override
    public void mouseEntered(MouseEvent e) {
	System.out.println(e);
	}*/

    /**
     * This method is called whenever the mouse is moved out of the active area
     * of the <code>GraphicsWindow</code>. There is no need for your code to
     * call it.
     * @param e a collection of properties of the mouse movement
     * @since 1.0
     */
    @Override
    public void mouseExited(MouseEvent e) {
	mouseX = mouseY = -1;
	isMove = true;
    }

    /**
     * This method is called whenever the mouse is released, after a drag.
     * There is no need for your code to call it.
     * @param e a collection of properties of the mouse movement
     * @since 1.0
     */
    @Override
    public void mouseReleased(MouseEvent e) {
	mouseClicked(e); // just pretend it was a click
    }

    /** 
     * Puts the program to sleep, until the mouse is clicked.
     * <code>GraphicsWindow</code>.
     * @since 1.0
     */
    public void waitForClick() {
	while (window.isDisplayable() && !isClick) {
	    GraphicsWindow.sleep(50); // sleep for 0.05s
	}
    }

    /** 
     * Puts the program to sleep, until the mouse moves or is clicked within the
     * <code>GraphicsWindow</code>.
     * @since 1.0
     */
    public void waitForMouse() {
	while (window.isDisplayable() && !isMove && !isClick) {
	    GraphicsWindow.sleep(50); // sleep for 0.05s
	}
    }
}
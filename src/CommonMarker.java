


import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;
import processing.core.PApplet;

/** Implements a common marker for airports on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * 
 */
public abstract class CommonMarker extends SimplePointMarker {
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	
	// Common piece of drawing method for markers; 
	// YOU WILL IMPLEMENT. 
	// Note that you should implement this by making calls 
	// drawMarker and showTitle, which are abstract methods 
	// implemented in subclasses
	public void draw(PGraphics pg, float x, float y) {

		if (!hidden) {
			drawMarker(pg, x, y); //jane: draw the normal airport markers 
			if (selected) {
				drawCenterMarker(pg, x, y); //jane: draw the airport marker selected by user
			}
		}	
	}
	
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void drawCenterMarker(PGraphics pg, float x, float y);
}
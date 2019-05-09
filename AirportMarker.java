package jane_assignment;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import parsing.ParseFeed;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development MOOC team
 * 
 * @author JANE 
 */
public class AirportMarker extends CommonMarker {
	public static List<Marker> routesList;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	//	System.out.println(city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		
			float altitude = getAltitude();
			
			if (altitude < 1000) {
				pg.fill(255, 255, 0); //jane: yellow
			}
			else if (altitude < 2000) {
				pg.fill(0, 0, 255); //jane: blue
			}
			else {
				pg.fill(255, 0, 0); //jane: red
			}
		
		pg.pushStyle();
		pg.strokeWeight(1);
		pg.ellipse(x, y, 8, 8);
		pg.popStyle();			
	}

	@Override //jane
	public void drawCenterMarker(PGraphics pg, float x, float y) {
		
		pg.pushStyle();
		pg.fill(255);
		pg.strokeWeight(1);
		pg.ellipse(x, y, 15, 15);
		pg.popStyle();

	}

	
	private String getAirport()
	{
		return getStringProperty("name");
	}
	
	private String getCity()
	{
		return getStringProperty("city");
	}
	private String getCountry()
	{
		return getStringProperty("country");
	}
	
	public String getId() {
		
		return getStringProperty("id");
	}
	
	private float getAltitude() {
		return Float.parseFloat(getProperty("altitude").toString());	
	}

//jane properties example: {country="United States", altitude=0, code="OLT", city="San Diego", name="San Diego Old Town Transit Center"}
}

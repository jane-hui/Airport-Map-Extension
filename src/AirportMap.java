
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.StamenMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/** An applet that shows airports (and routes) on a world map.  
 * 
 * @author Adam Setters and the UC San Diego Intermediate Software Development MOOC team
 * 
 * @author JANE
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private List<Marker> matchedS;
	
	//jane: create a buffer to store text label in the end
	PGraphics buffer;
	
	public void setup() {
		// setting up PAppler
		size(920, 750, OPENGL);
		
		//jane: setting up buffer
		buffer = createGraphics(900,700);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 215, 100, 660, 600, new StamenMapProvider.WaterColor());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);

			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//jane: Example: {destination=2912, source=2913}
//			System.out.println(sl.getProperties());
			
			routeList.add(sl);
			sl.setHidden(true);
			
		}
		
		map.addMarkers(routeList);	
		map.addMarkers(airportList);
	}

	
	public void draw() {
		background(100);
		map.draw();
		addKey();
		//jane
		image(buffer,0,0);
		
	}
	

	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected = null;
			buffer.clear();
		
		}
		selectMarkerIfHover(airportList);
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				
				if(!marker.isHidden()) {
					showTitle(marker);
				return;
				}				
			}
							
		}
	}
	
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked.setSelected(false);
			lastClicked = null;
		}
		else
		{
			checkAirportRoutesForClick();
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
		
		for(Marker marker : routeList) {
			marker.setHidden(true);
		}
	}
	
	//jane: helper method to display airport markers when user clicked
	private void checkAirportRoutesForClick()
	{
		if (lastClicked != null) 
			return;
		
		//jane: Loop over the airport markers to see if one of them is selected
		for (Marker m : airportList) {
			AirportMarker marker = (AirportMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				marker.setSelected(true);

				//jane: display routes from the selected airports
				matchedS = new ArrayList<Marker>();
				for (Marker route : routeList) {					
					if(route.getProperty("source").equals(lastClicked.getId())) {
						route.setHidden(false);		
						matchedS.add(route);
					}		
				} 
				
				//jane: display the selected source airport and possible destination airports
				for (Marker mhide : airportList) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
						
						if(matchedS != null) {							
							for(Marker match : matchedS) {						
								if(mhide.getId().equalsIgnoreCase(match.getStringProperty("destination"))) {
									mhide.setHidden(false);
								}
							}
														
						}
					}
				}
			

				
				return;
			}
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Processing's graphics methods can be used here
		
		//jane: Title Box
		fill(255, 250, 240);	
		rect(25, 20, 865, 50);
		
		//jane: Title
		fill(255,0,0);
		textAlign(CENTER, CENTER);
		textSize(25);
		text("AIRPORT MAP", 50+375, 20+22);
		
		//jane: legend box
		int xbase = 25;
		int ybase = 100;
		fill(255, 250, 240);
		rect(xbase, ybase, 165, 200);
		
		//jane: legend
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(15);
		text("Airport Altitudes : ", xbase+15, ybase+15);
		
		fill(color(255, 255, 0));
		ellipse(xbase+25, ybase+40, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+25, ybase+60, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+25, ybase+80, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Below 1000 ft", xbase+40, ybase+40);
		text("1000 - 2000 ft", xbase+40, ybase+60);
		text("Above 3000 ft", xbase+40, ybase+80);
		
		line(xbase+15, ybase+110, xbase+150, ybase+110);

		text("Selected Airport", xbase+40, ybase+140);
		text("(When user clicks)", xbase+20, ybase+160);
		
		fill(255, 255, 255);
		int centerx = xbase+25;
		int centery = ybase+140;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		ellipse(centerx, centery, 22, 22);
		
	}
	
	//jane: Helper method to show title of airport
	private void showTitle(CommonMarker marker) {

		String name = "Airport: " + marker.getProperty("name") + " (" + marker.getProperty("altitude") + " ft high) ";
		String location = marker.getProperty("city") + ", " + marker.getProperty("country") + " ";
		String altitude = marker.getProperty("altitude") + " feet high.";

		buffer.beginDraw();	
		buffer.pushStyle();	
		buffer.strokeWeight(1);
		buffer.fill(255, 255, 255);
		buffer.textSize(14);
		buffer.rectMode(PConstants.CORNER);
		buffer.rect(marker.getScreenPosition(map).x, marker.getScreenPosition(map).y-5-39, Math.max(buffer.textWidth(name), buffer.textWidth(location)) + 6, 39);
		buffer.fill(0, 0, 255);
		buffer.textAlign(PConstants.LEFT, PConstants.TOP);
		buffer.text(name, marker.getScreenPosition(map).x+3, marker.getScreenPosition(map).y-5-35);
		buffer.fill(0);
		buffer.text(location, marker.getScreenPosition(map).x+3, marker.getScreenPosition(map).y-5-20);
		buffer.popStyle();
		buffer.endDraw();
	}
	
}

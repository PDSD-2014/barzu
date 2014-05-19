package com.example.coffeeshopfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button search;
	static Spinner s;
	GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        search = (Button)findViewById(R.id.search);
	        s = (Spinner) findViewById(R.id.search_type);

	        if (savedInstanceState == null) {
	            getFragmentManager().beginTransaction()
	                    .add(R.id.container, new PlaceholderFragment())
	                    .commit();
	        }
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void onClick_Search(View v) {

    	String response = "";
    	boolean all = false;
		gps = new GPSTracker(MainActivity.this);
		
		String URL = "https://maps.googleapis.com/maps/api/place/search/xml?";
		String type = s.getSelectedItem().toString();
		
		if(type.compareTo("All") == 0) {
			all = true;
		} else if(type.compareTo("Restaurant") == 0) {
			type = "restaurant";
		} else if(type.compareTo("Cafe") == 0) {
			type = "cafe";
		} else if(type.compareTo("Bar") == 0) {
			type = "bar";
		} else if(type.compareTo("Night Club") == 0) {
			type = "night_club";
		}

		if(gps.canGetLocation()) {
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			
			if(!all) {
				URL += "types=" + type;
				URL += "&location=" + latitude + "," + longitude;
				URL += "&radius=1000";
				URL += "&senzor=false";
				URL += "&key=AIzaSyC6uhKND6kMcssKqNuTq04dJjByCZLmDoQ";
				
				try {
					HttpGet get = new HttpGet(URL);
					response = this.compute(get);
					
					this.ParserXml(response, latitude, longitude);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				// get restaurants nearby
				URL += "types=restaurant";
				URL += "&location=" + gps.getLatitude() + "," + gps.getLongitude();
				URL += "&radius=1000";
				URL += "&senzor=false";
				URL += "&key=AIzaSyC6uhKND6kMcssKqNuTq04dJjByCZLmDoQ";
				
				try {
					HttpGet get = new HttpGet(URL);
					response += this.compute(get);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				// get cafes nearby
				URL.replace("restaurant", "cafe");
				
				try {
					HttpGet get = new HttpGet(URL);
					response += this.compute(get);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				// get bars nearby
				URL.replace("cafe", "bar");
				
				try {
					HttpGet get = new HttpGet(URL);
					response += this.compute(get);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				// get Night Clubs nearby
				URL.replace("bar", "night_club");
				
				try {
					HttpGet get = new HttpGet(URL);
					response += this.compute(get);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				// show all
				this.ParserXml(response, latitude, longitude);
			}
		} else {
			Toast.makeText(this, "No internet connection or GPS is not activated", Toast.LENGTH_LONG).show();
		}
    }
    
    public void ParserXml(String response, double latitude, double longitude) {
    	
    	ListView list = (ListView) findViewById(R.id.list);
    	ArrayList<String> names = new ArrayList<String>();
    	ArrayList<CoffeeShop> coffeeShops = new ArrayList<CoffeeShop>();
    	
    	Location locationA = new Location("pointA");
		Location locationB = new Location("pointB");
    	
    	int startR, start;
    	int endR, end;
    	
    	locationA.setLatitude(latitude);
    	locationA.setLongitude(longitude);
    	
    	while(response.contains("<result>")) {

    		String placeXml = "";
    		String name = "";
    		String address = "";
    		
    		double lat;
    		double lng;
    		double dist;
    		
    		startR = response.indexOf("<result>");
    		endR = response.indexOf("</result>");
    		
    		placeXml = response.subSequence(startR, endR).toString();
    		
    		start = placeXml.indexOf("<name>");
    		end = placeXml.indexOf("</name>");
    		name = placeXml.subSequence(start + 6, end).toString();
    		
    		start = placeXml.indexOf("<vicinity>");
    		end = placeXml.indexOf("</vicinity>");
    		address = placeXml.subSequence(start + 10, end).toString();
    		
    		start = placeXml.indexOf("<lat>");
    		end = placeXml.indexOf("</lat>");
    		lat = Double.parseDouble(placeXml.subSequence(start + 5, end).toString());
    		
    		start = placeXml.indexOf("<lng>");
    		end = placeXml.indexOf("</lng>");
    		lng = Double.parseDouble(placeXml.subSequence(start + 5, end).toString());
    		
    		locationB.setLatitude(lat);
        	locationB.setLongitude(lng);
    		
    		dist = locationA.distanceTo(locationB);
    		
    		CoffeeShop coffeeShop = new CoffeeShop(name, address, "Distance : " + dist + " meters");
    		
    		if(!names.contains(name)) {
    			names.add(name);
    			coffeeShops.add(coffeeShop);
    		}
    		
    		response = response.substring(endR + 8);
    	}
    	
    	CustomAdapter adapter = new CustomAdapter(this, coffeeShops);
    	list.setAdapter(adapter);
    }
    
    public String compute(final HttpGet get) {
    	
    	final StringBuilder content = new StringBuilder();
    	Thread computeThread = new Thread(new Runnable(){

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				try {
					content.append(client.execute(get, responseHandler));
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	});
    	
    	computeThread.start();
    	try {
			computeThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return content.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);     
            
            s = (Spinner) rootView.findViewById(R.id.search_type);
	        List<String> array_spinner = new ArrayList<String>();
	        
	        array_spinner.add("All");
	        array_spinner.add("Restaurant");
	        array_spinner.add("Cafe");
	        array_spinner.add("Bar");
	        array_spinner.add("Night Club");
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, array_spinner);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        s.setAdapter(adapter);
            
            return rootView;
        }
    }

}

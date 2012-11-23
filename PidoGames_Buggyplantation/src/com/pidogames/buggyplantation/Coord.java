package com.pidogames.buggyplantation;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class Coord {
	
	private static final String JSON_X = "x";
	private static final String JSON_Y = "y";
	
	private static Random rnd = new Random(System.currentTimeMillis());
	
	public int x;
	public int y;
	
	public Coord(JSONObject o) throws JSONException {
		x = o.getInt(JSON_X);
		y = o.getInt(JSON_Y);
	}
	
	public Coord(){
		x=0;
		y=0;
	}
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void set(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void randomize(int d){
		//double a = rnd.nextDouble() % (2.0*Math.PI);
		//x += (d*Math.sin(a));
		//y -= (d*Math.cos(a));
		
		double rad = 2.0*Math.PI/360.0 * rnd.nextInt(360);
		d = rnd.nextInt(d/2) + d/2;
		
		x += (d*Math.sin(rad));
		y -= (d*Math.cos(rad));
	}
	
	public void randomize(int rx, int ry){
		x += (rnd.nextInt(2*rx)-rx);
		y += (rnd.nextInt(2*ry)-ry);
	}
	
	public long d2(Coord c){
		return (x-c.x)*(x-c.x) + (y-c.y)*(y-c.y);
	}
	
	public Coord avg(Coord c){
		return new Coord((x+c.x)/2, (y+c.y)/2);
	}
	
	public double angle(Coord c){
		double dx = this.x - c.x;
		double dy = this.y - c.y;		
		
		double adx = Math.abs(dx);
		double ady = Math.abs(dy);
		
		double angle = 0.0;
		if(dx<0 && dy>0){
			angle = Math.PI/2 - Math.atan(ady/adx);			
		}
		else if(dx<0 && dy==0){
			angle = Math.PI/2;
		}
		else if(dx<0 && dy<0){
			angle = Math.PI/2 + Math.atan(ady/adx);			
		}
		else if(dx==0 && dy<0){
			angle = Math.PI;
		}
		else if(dx>0 && dy<0){
			angle = 3*Math.PI/2 - Math.atan(ady/adx);			
		}
		else if(dx>0 && dy==0){
			angle = 3*Math.PI/2;
		}
		else if(dx>0 && dy>0){
			angle = 3*Math.PI/2 + Math.atan(ady/adx); 
		}
		
		return angle;
	}
	
	@Override
	public boolean equals(Object o){
		Coord c =(Coord)o;
		return (c.x==x) && (c.y==y);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put(JSON_X, x);
		o.put(JSON_Y, y);
		return o;
	}
}

package com.pidogames.buggyplantation;

import org.json.JSONException;
import org.json.JSONObject;

public class Poison {
	
	private static final String JSON_PLAYER   = "p";
	private static final String JSON_DAMAGE   = "d";
	private static final String JSON_PERIOD   = "e";
	private static final String JSON_DURATION = "u";
	
	public int player;
	public int damage;
	public int period;
	public int duration;
	
	public Poison(JSONObject o) throws JSONException {
		player   = o.getInt(JSON_PLAYER);
		damage   = o.getInt(JSON_DAMAGE);
		period   = o.getInt(JSON_PERIOD);
		duration = o.getInt(JSON_DURATION);
	}
	
	public Poison(int player, int damage, int duration, int period){
		this.player   = player;
		this.damage   = damage;
		this.duration = duration;
		this.period   = period;
	}	
	
	public JSONObject toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put(JSON_DAMAGE, damage);
		o.put(JSON_DURATION, duration);
		o.put(JSON_PERIOD, period);
		return o;
	}
}

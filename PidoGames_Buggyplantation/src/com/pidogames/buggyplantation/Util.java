package com.pidogames.buggyplantation;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Rect;

public class Util {
	
	public static JSONObject rect2JSON(Rect rect) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("l", rect.left);
		o.put("t", rect.top);
		o.put("r", rect.right);
		o.put("b", rect.bottom);		
		return o;
	}
	
	public static Rect JSON2Rect(JSONObject o) throws JSONException {
		return new Rect(o.getInt("l"),o.getInt("t"),o.getInt("r"),o.getInt("b"));
	}
}

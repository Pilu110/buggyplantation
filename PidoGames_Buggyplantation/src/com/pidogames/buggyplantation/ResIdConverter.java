package com.pidogames.buggyplantation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.pidogames.buggyplantation.interfaces.Constants;

public class ResIdConverter implements Constants {
	
	ArrayList<Integer> convertResIds;
	
	public ResIdConverter(){
		convertResIds = new ArrayList<Integer>();
	}
	
	public ResIdConverter(JSONArray o) throws JSONException {
		convertResIds = new ArrayList<Integer>(o.length());
		for(int i=0; i<o.length(); i++){
			int resId = getResIdForName(o.getString(i));
			if(resId<0) throw new JSONException("RESOURCE NOT FOUND: "+o.getString(i));
			convertResIds.add(resId);
		}
	}
	
	public static String getNameForResid(int resId){
		Class c = R.drawable.class;
		for( Field f : c.getDeclaredFields()){
			try {
				if(resId == f.getInt(null)) return f.getName();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return resId+"";
	}
	
	public static int getResIdForName(String name){
		Class c = R.drawable.class;
		for( Field f : c.getDeclaredFields()){
			try {
				if(name.equals(f.getName())) return f.getInt(null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return -1;
	}
		
	public int resIdToType(int resId) {
		
		for(int i=0; i<convertResIds.size(); i++) {
			if(convertResIds.get(i) == resId) return i;
		}
		
		convertResIds.add(resId);		
		return convertResIds.size()-1;
	}
	
	public int typeToResId(int type) {
		return convertResIds.get(type);
	}
	
	public JSONArray toJSONArray(){
		JSONArray o = new JSONArray();
		
		for(int i=0; i<convertResIds.size(); i++) {
			o.put(getNameForResid(convertResIds.get(i)));
		}
		
		return o;
	}
}

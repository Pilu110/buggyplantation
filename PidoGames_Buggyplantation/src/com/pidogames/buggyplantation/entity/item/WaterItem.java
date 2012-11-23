package com.pidogames.buggyplantation.entity.item;


import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.Water;

public class WaterItem extends Item implements Water {
	
	static {
		PHASES_RESID.put(R.drawable.i_water_mouth_a1, new int[]{
			R.drawable.i_water_mouth_a1,R.drawable.i_water_mouth_a2,R.drawable.i_water_mouth_a3,R.drawable.i_water_mouth_a4
		});
	}

	public WaterItem(int player, int type, int level, int x, int y) {
		super(player, type, level, x, y);
	}
	
	public WaterItem(JSONObject o, ResIdConverter rc) throws JSONException{
		super(o, rc);
	}
	
	@Override
	public int getDisplayedType(){
		int type = super.getDisplayedType();
		int [] resids = PHASES_RESID.get(type);
		if(resids!=null) return resids[(int)((SceneView.getTic()/4)%resids.length)];
		else return type;
	}
	
	@Override
	public int getFocusObject(Entity e){
		if(e.getMovingType() == MOVING_FLY) return FOCUS_NONE;
		else return FOCUS_BLOCK;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_WATER);
		return o;
	}
}

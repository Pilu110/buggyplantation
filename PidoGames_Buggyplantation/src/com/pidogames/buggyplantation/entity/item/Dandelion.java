package com.pidogames.buggyplantation.entity.item;

import java.util.HashSet;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.effect.Effect;

public class Dandelion extends Seed {
	
	public Dandelion(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}

	public Dandelion(int player, int type, int level, int x, int y) {
		super(player, type, level, x, y, 1, 400*400);
	}
	
	@Override
	public int getMovingType() {
		return MOVING_FLY;
	}
	
	@Override
	public boolean isRotationCached(){
		return false;
	}
	
	@Override
	protected void afterPlantedStep(long tic){
		if(angle>290 || angle<70) {
			if(lean_right) setAngle(angle+4);
			else setAngle(angle-4);
		}
		else {
			if(!this.hasEffect(DisappearEffect.class)) {
				DisappearEffect e = new DisappearEffect(20);
				e.setEffectListener(this);
				this.addEffect(e);
			}
		}
	}	
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_DANDELION);
		return o;
	}

}

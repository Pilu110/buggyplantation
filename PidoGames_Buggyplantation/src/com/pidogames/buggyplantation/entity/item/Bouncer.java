package com.pidogames.buggyplantation.entity.item;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.effect.DisappearEffect;



public class Bouncer extends Shrapnel {

	public Bouncer(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	public Bouncer(int player, int type, int level, int x, int y, int angle, float velocity, float resistance) {
		super(player, type, level, x, y, angle, velocity, resistance, null, 0, false);
	}

	@Override
	protected void onStop(long tic){
		DisappearEffect e = new DisappearEffect(10);
		e.setEffectListener(this);
		this.addEffect(e);
		resistance = -resistance;
		velocity -= resistance;
		back    = true;
		stopped = false;
	}
		
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_BOUNCER);
		return o;
	}
}

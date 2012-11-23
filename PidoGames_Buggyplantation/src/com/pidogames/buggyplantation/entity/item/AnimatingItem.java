package com.pidogames.buggyplantation.entity.item;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;

public class AnimatingItem extends Item {
	private static HashMap<Integer,int[]> PHASES_RESID = new HashMap<Integer,int[]>();
	private static HashMap<Integer,int[]> PHASES_DELAY = new HashMap<Integer,int[]>();
	static {
		PHASES_RESID.put(R.drawable.i_damage1,new int[]{R.drawable.i_damage1,R.drawable.i_damage2,R.drawable.i_damage3,R.drawable.i_damage4});
		PHASES_DELAY.put(R.drawable.i_damage1, new int[]{2,1,2,2});
		PHASES_RESID.put(R.drawable.i_acid_effect_a1,new int[]{R.drawable.i_acid_effect_a1,R.drawable.i_acid_effect_a2,R.drawable.i_acid_effect_a3,R.drawable.i_acid_effect_a4,R.drawable.i_acid_effect_a5,R.drawable.i_acid_effect_a6});
		PHASES_DELAY.put(R.drawable.i_acid_effect_a1, new int[]{0,1,1,1,1,2});
		PHASES_RESID.put(R.drawable.i_firespot_effect_a1,new int[]{R.drawable.i_firespot_effect_a1,R.drawable.i_firespot_effect_a2,R.drawable.i_firespot_effect_a3,R.drawable.i_firespot_effect_a4});
		PHASES_DELAY.put(R.drawable.i_firespot_effect_a1, new int[]{0,1,2,3});
	}
	
	private int phase;
	private int delay;
	private boolean animation_started;

	@Override
	public int getDisplayedType(){
		int type = super.getDisplayedType();
		int [] resids = PHASES_RESID.get(type);
		if(resids!=null) return resids[phase];
		else return type;
	}
	
	public AnimatingItem(int player, int type, int level, int x, int y, boolean animation_started) {
		super(player, type, level, x, y);
		setSelectable(false);
		this.animation_started = animation_started;
		phase = 0;
		delay = 0;		
	}

	public AnimatingItem(JSONObject o, ResIdConverter rc) throws JSONException{
		super(o, rc);
	}
	
	public boolean startAnimation(){
		animation_started = true;
		return PHASES_RESID.get(type)!=null;
	}
	
	public void onAnimationEnd(){
		Scene scene = SceneView.getScene();
		scene.removeItem(this);
	}
	
	@Override
	public int getFocusObject(Entity e){
		return FOCUS_NONE;
	}
	
	@Override
	public int getFocusObject(Entity e, boolean attacked){
		return FOCUS_NONE;
	}
	
	@Override
	public void step(long tic) {
		super.step(tic);
		if(animation_started){
			int [] resids = PHASES_RESID.get(type);
			int [] delays = PHASES_DELAY.get(type);
			if(resids!=null) {
				if(delay<delays[phase]) delay++;
				else {
					delay = 0;
					if(phase<resids.length-1) phase++;
					else onAnimationEnd();
				}
			}
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_ANIMATING);
		return o;
	}
}

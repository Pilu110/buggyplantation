package com.pidogames.buggyplantation.entity.item;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.entity.item.bug.Bug;

public class Egg extends Item {

	private int  child_type;
	private long duration;
	
	public Egg(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	public Egg(int player, int type, int level, int x, int y, int child_type, long duration) {
		super(player, type, level, x, y);
		this.child_type = child_type;
		this.duration = duration;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_EGG);
		return o;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		duration--;
		if(duration<=0){
			Scene scene = SceneView.getScene(); 
			Bug bug = Bug.getInstance(player, child_type, (int)x, (int)y, angle);
			scene.removeItem(this);
			scene.addItem(bug, true);
			
			for(int i=0; i<3; i++){
				Item floyd = new Item(PLAYER_NEUTRAL, R.drawable.i_squash_floyd, GROUND_LEVEL, (int)x+rnd.nextInt(30)-15, (int)y+rnd.nextInt(30)-15);
				floyd.addEffect(new DisappearEffect(50));
				floyd.setAngle(rnd.nextInt(360));
				scene.addItem(floyd, true);
			}
		}
		else if(duration<40){
			int d = (int)(20-duration)/2;
			if(d>0){
				x += (rnd.nextInt(d)-d/2);
				y += (rnd.nextInt(d)-d/2);
				setAngle(rnd.nextInt(d)-d/2);
			}
		}

	}
}

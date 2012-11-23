package com.pidogames.buggyplantation.entity.item.bug;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Shrapnel;

public class Ant extends Bug {

	public Ant(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	protected Ant(int player, int type, int x, int y, double angle) {
		super(player, type, x, y, angle);
	}
	
	@Override
	public void onDie(){
		if(type!=TYPE_ANT_WORKER){
			Scene scene = SceneView.getScene();
			scene.removeItem(this);
			
			Coord o = getPosition();
			for(int i=0; i<3; i++){
				Item floyd = new Item(PLAYER_NEUTRAL, R.drawable.i_squash_floyd, GROUND_LEVEL, o.x+rnd.nextInt(40)-20, o.y+rnd.nextInt(40)-20);
				floyd.addEffect(new DisappearEffect(rnd.nextInt(150)+100));
				floyd.setAngle(rnd.nextInt(360));
				scene.addItem(floyd, true);
			}
			
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip5, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip6, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip7, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip5, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip6, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip7, AIR_LEVEL, o.x, o.y, rnd.nextInt(360), 6+rnd.nextInt(5), 1f, null, 0, true), false);
			
			switch(type){
				case TYPE_ANT_QUEEN: {
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip1, AIR_LEVEL, o.x, o.y, (int)angle, 6+rnd.nextInt(3), 1f, null, 0, true), false);
					
					int a = (((int)angle+90)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip2, AIR_LEVEL, o.x, o.y, a, 4+rnd.nextInt(2), 1f, null, 1, true), false);
					
					a = (((int)angle+180)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip3, AIR_LEVEL, o.x, o.y, a, 3+rnd.nextInt(1), 1f, null, 1, true), false);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip4, AIR_LEVEL, o.x, o.y, a, 5+rnd.nextInt(3), 1f, null, 2, true), false);
					
					a = (((int)angle+270)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_queen_chip2, AIR_LEVEL, o.x, o.y, a, 4+rnd.nextInt(2), 1f, null, 1, true), false);
					
					break;
				}
				
				case TYPE_ANT_SOLDIER: {
					
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_soldier_chip1, AIR_LEVEL, o.x, o.y, (int)angle, 6+rnd.nextInt(3), 1f, null, 0, true), false);
					
					int a = (((int)angle+90)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_soldier_chip2, AIR_LEVEL, o.x, o.y, a, 4+rnd.nextInt(2), 1f, null, 1, true), false);
					
					a = (((int)angle+180)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_soldier_chip3, AIR_LEVEL, o.x, o.y, a, 5+rnd.nextInt(3), 1f, null, 2, true), false);
					
					a = (((int)angle+270)%360);
					scene.addItem(new Shrapnel(PLAYER_NEUTRAL, R.drawable.i_ant_soldier_chip2, AIR_LEVEL, o.x, o.y, a, 4+rnd.nextInt(2), 1f, null, 1, true), false);
					
					break;
				}
			}
		}
		else {
			super.onDie();
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_ANT);
		return o;
	}
	
}

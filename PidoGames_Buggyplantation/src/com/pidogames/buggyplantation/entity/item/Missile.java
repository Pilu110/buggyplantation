package com.pidogames.buggyplantation.entity.item;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.SoundManager;
import com.pidogames.buggyplantation.R.drawable;
import com.pidogames.buggyplantation.entity.block.Block;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Missile extends Item {
		
	public static final int LOADED = 0;
	public static final int FIRED  = 1;
	public static final int DISAPPEARED = 2;
		
	private static final String JSON_RANGE = registerJSONKey("r", Missile.class);
	private static final String JSON_FIRED_AT = registerJSONKey("f", Missile.class);
	
	private int state;
	private int range;
	private long fired_at;
	
	public Missile(JSONObject o, ResIdConverter rc) throws JSONException{
		super(o, rc);
		setSelectable(false);
		state = FIRED;
		range = o.getInt(JSON_RANGE);
		fired_at = o.getLong(JSON_FIRED_AT);
	}
	
	public Missile(int player, int type, int level, int x, int y, int range){
		super(player, type, level, x, y);
		setSelectable(false);
		this.range = range;
		setLevel(STALK_LEVEL);
		setState(LOADED);
		fired_at = 0;
	}
	
	public void fire(long tic){
		if(state==LOADED){
			//Log.d("FIRE","FIRED AT "+tic);
			fired_at = tic;
			setLevel(LEAF_LEVEL);
			setState(FIRED);
			SoundManager.playIt(R.raw.shoot, new Coord((int)x, (int)y));
		}
	}
		
	public boolean blowIt(Item i){
		int dx = (int)x - (int)i.getX();
		int dy = (int)y - (int)i.getY();
		int range = getBlowRange();
		if(dx*dx + dy*dy < range*range) {
			i.damage(getDamageAmount(), this, player);
			setState(DISAPPEARED);
			return true;
		}
		return false;
	}
	
	public long getFiredAt(){
		return fired_at;
	}
	
	public boolean isOverRangeAt(long tic){
		return (fired_at + range/getSpeed() < tic);
	}
	
	public void setState(int state){
		this.state = state;		
	}
	
	public int getState(){
		return state;
	}
	
	
	public int getSpeed(){
//		switch(type){
//			case R.drawable.m_thorn: return 15;
//		}
		return 15;
	}
	public int getBlowRange(){
//		switch(type){
//			case R.drawable.m_thorn: return 20;
//		}
		return 20;
	}
	public int getDamageAmount(){
//		switch(type){
//			case R.drawable.m_thorn: return 10;
//		}
		return 20;
	}
	
	public void forward(){
		forward(getSpeed());
	}
	
	public void forward(int d){
		double rad = 2.0*Math.PI/360.0 * angle;
		x += (d*Math.sin(rad));
		y -= (d*Math.cos(rad));
	}

	@Override
	public int getMaxHitPoints() {
		return 0;
	}

	@Override
	public boolean isRotationCached() {
		return false;
	}

	@Override
	public void step(long tic) {
		Scene scene = SceneView.getScene();
		switch(getState()) {
			case Missile.FIRED:
				if(!isOverRangeAt(tic)) {
					forward();
					
					boolean blowed = false; 
					
					final int ax = (int)(x/Block.getAbsoluteWidth());
					final int ay = (int)(y/Block.getAbsoluteHeight());
					final int ix1 = ax>0?ax-1:0;
					final int iy1 = ay>0?ay-1:0;
					final int ix2 = ax<scene.getWidth()-1?ax+1:scene.getWidth()-1;
					final int iy2 = ay<scene.getHeight()-1?ay+1:scene.getHeight()-1;
										
					for(int ix=ix1;ix<=ix2;ix++){
						for(int iy=iy1;iy<=iy2;iy++){
							Map<Item,Boolean> items = scene.getItemsAt(ix, iy);
							if(items!=null){
								for(Item item : items.keySet()){
									if(getAlignment(item)==ALIGN_ENEMY && item.getHitPoints()>0){
										if(blowIt(item)){
											blowed = true;
											break;
										}
									}
								}
							}
							if(blowed) break;
						}
						if(blowed) break;
					}
				}
				else scene.removeItem(this);
			break;
			case Missile.DISAPPEARED:
				scene.removeItem(this);
			break;
		}	
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_RANGE, range);
		o.put(JSON_FIRED_AT, fired_at);
		o.put(JSON_INSTANCEOF, JSON_CLASS_MISSILE);
		return o;
	}
}

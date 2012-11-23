package com.pidogames.buggyplantation.entity.item;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.effect.Effect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;

public class Seabean extends Seed {
	
	private static final String JSON_OPEN_BEAN = registerJSONKey("ob",Seabean.class);
	
	private Item open_bean;
	private boolean coast_mode;
	
	public Seabean(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		coast_mode = false;
	}

	public Seabean(int player, int type, int level, int x, int y) {
		super(player, type, level, x, y, 2, Integer.MAX_VALUE);
		coast_mode = false;
	}
	
	@Override
	public int getMovingType() {
		return coast_mode?MOVING_MOVE:MOVING_FLOAT;
	}
	
	@Override
	protected void afterPlantedStep(long tic){
		if(open_bean==null){
			open_bean = new Item(player, R.drawable.i_seabean_open, GROUND_LEVEL, (int)x, (int)y);
			open_bean.setAngle(angle);
			SceneView.getScene().addItem(open_bean, true);
		}
		if(!this.hasEffect(DisappearEffect.class)) {
			DisappearEffect e = new DisappearEffect(50);
			e.setEffectListener(this);
			this.addEffect(e);
		}
	}	

	public int [][][] getDistanceMap(){
		Scene scene = SceneView.getScene();
		int[][][] src_dm = scene.getDistanceMapFrom(this, sqr_range, null);
		
		int w = scene.getWidth();
		int h = scene.getHeight();
		int[][][] dm = new int[2][w][h];
		
		
		coast_mode = true;
		
		int sx = (int)x/Block.getAbsoluteWidth();
		int sy = (int)y/Block.getAbsoluteHeight();
		src_dm[0][sx][sy] = Integer.MAX_VALUE;
		src_dm[1][sx][sy] = NONE;
		
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				if(src_dm[0][x][y]<Integer.MAX_VALUE){
					dm[0][x][y] = Integer.MAX_VALUE;
					dm[1][x][y] = src_dm[1][x][y];
				}
				else {
					Block b = scene.getHigherBlock(x, y);
					if(scene.getBlock(STALK_LEVEL,x,y)==null && (b==null || b.getFocusObject(this)!=FOCUS_BLOCK)) {
						
						int d   = src_dm[0][x][y];
						int dir = src_dm[1][x][y];
						
						if(x-1>=0 && src_dm[0][x-1][y]<Integer.MAX_VALUE && src_dm[0][x-1][y] + 1 < d) {
							d   = src_dm[0][x-1][y] + 1;
							dir = N8_L;
						}
						if(x+1<w && src_dm[0][x+1][y]<Integer.MAX_VALUE && src_dm[0][x+1][y] + 1 < d) {
							d   = src_dm[0][x+1][y] + 1;
							dir = N8_R;
						}
						if(y-1>=0 && src_dm[0][x][y-1]<Integer.MAX_VALUE && src_dm[0][x][y-1] + 1 < d) {
							d   = src_dm[0][x][y-1] + 1;
							dir = N8_U;
						}
						if(y+1<h && src_dm[0][x][y+1]<Integer.MAX_VALUE && src_dm[0][x][y+1] + 1 < d) {
							d   = src_dm[0][x][y+1] + 1;
							dir = N8_D;
						}
						
						dm[0][x][y] = d;
						dm[1][x][y] = dir;
					}
					else {
						dm[0][x][y] = Integer.MAX_VALUE;
						dm[1][x][y] = NONE;						
					}
				}
				
				/*
				if(scene.getBlock(STALK_LEVEL,x,y)!=null) dm[0][x][y] = Integer.MAX_VALUE;
				else {
					Block b = scene.getHigherBlock(x, y);
					int fo = b.getFocusObject(this);
					if(fo == Entity.FOCUS_BLOCK) dm[0][x][y] = Integer.MAX_VALUE;
				}
				*/
			}
		}
		coast_mode = false;
		
		this.dm = dm;
		return dm;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		double na = av/Math.PI*180.0 - angle;
		if(na<-180.0) na+=360.0;
		else if (na>180.0) na-=360.0;
		this.turnTo(na, 4); //setAngle(av/Math.PI*180.0);
	}
	
	@Override
	public void OnEffectEnd(Effect e) {
		super.OnEffectEnd(e);
		if(e instanceof DisappearEffect){
			if(open_bean!=null && !open_bean.hasEffect(DisappearEffect.class)) {
				DisappearEffect e2 = new DisappearEffect(100);
				e2.setEffectListener(this);
				open_bean.addEffect(e2);
			}
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_SEABEAN);
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_OPEN_BEAN)){
			int id = o.optInt(JSON_OPEN_BEAN, -1);
			if(id!=-1){
				open_bean = (Item)tmp_ids.get(id);
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(open_bean!=null) {
			if(o==null) o = new JSONObject();
			
			Integer id = tmp_ids.get(open_bean);
			if(id!=null){
				o.put(JSON_OPEN_BEAN, id);
			}
		}
		
		return o;
	}
}

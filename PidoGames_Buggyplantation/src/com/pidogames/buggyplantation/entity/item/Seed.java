package com.pidogames.buggyplantation.entity.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.PlantBlock;

public class Seed extends TurningItem {
	private Entity destination;
	private Stack<Entity> dest_path;
	private boolean is_planted;
	private int speed;
	
	protected int[][][] dm;
	protected int sqr_range;
	protected boolean lean_right;
	protected double av;
	
	private static final String JSON_DESTINATION = registerJSONKey("d",Seed.class);
	private static final String JSON_SPEED       = registerJSONKey("s",Seed.class);
	private static final String JSON_SQR_RANGE   = registerJSONKey("r",Seed.class);
	private static final String JSON_IS_PLANTED  = registerJSONKey("ip",Seed.class);
	private static final String JSON_LEAN_RIGHT  = registerJSONKey("lr",Seed.class);
	
	public Seed(JSONObject o,ResIdConverter rc) throws JSONException {
		super(o, rc);
		speed = o.getInt(JSON_SPEED);
		sqr_range = o.getInt(JSON_SQR_RANGE);
		if(!o.isNull(JSON_IS_PLANTED)) is_planted = o.getBoolean(JSON_IS_PLANTED);
		else is_planted = false;
		if(!o.isNull(JSON_LEAN_RIGHT)) lean_right = o.getBoolean(JSON_LEAN_RIGHT);
		else lean_right = false;
	}

	public Seed(int player, int type, int level, int x, int y) {
		this(player,type,level,x,y,10,200*200);
	}
	
	public Seed(int player, int type, int level, int x, int y, int speed, int sqr_range) {
		super(player, type, level, x, y);
		this.speed = speed;
		this.sqr_range = sqr_range;
		is_planted = false;		
	}
	
	
	
	protected void afterPlantedStep(long tic){
		if(!this.hasEffect(DisappearEffect.class)) {
			DisappearEffect e = new DisappearEffect(20);
			e.setEffectListener(this);
			this.addEffect(e);
		}
	}
	
	@Override
	public int getFocusObject(Entity e){
		if(getAlignment(e)==ALIGN_ENEMY) return FOCUS_FOOD;
		else return FOCUS_NONE;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		
		if(is_planted){
			afterPlantedStep(tic);
		}
		else if(destination!=null){
			Coord p  = this.getPosition();
			Coord p2 = destination.getPosition();
			if(p.d2(p2) > speed*speed){
				av = p.angle(p2);
				//double rad = 2.0*Math.PI/360.0 * angle;
				x += (speed*Math.sin(av));
				y -= (speed*Math.cos(av));
				lean_right = (av<=Math.PI);
			}
			else {
				if(dest_path!=null && !dest_path.isEmpty()){
					destination = dest_path.pop();					
				}
				else {
					int px, py;
					if(destination instanceof Block){
						Block b = (Block)destination;
						px = b.getX();
						py = b.getY();
					}
					else {
						px = (int)x/Block.getAbsoluteWidth();
						py = (int)y/Block.getAbsoluteHeight();
					}
					Scene scene = SceneView.getScene();
					if(scene.getBlock(STALK_LEVEL, px, py)==null) scene.setBlock(STALK_LEVEL, px, py, new PlantBlock(PLAYER_PLANT, R.drawable.b_seed, px, py, STALK_LEVEL, true));
					is_planted = true;
					dest_path = null;
					destination = null;
				}
			}
		}
	}
	
	public int [][][] getDistanceMap(){
		Scene scene = SceneView.getScene();
		int[][][] dm = scene.getDistanceMapFrom(this, sqr_range, null);
		
		for(int y=0; y<scene.getHeight(); y++){
			for(int x=0; x<scene.getWidth(); x++){
				if(scene.getBlock(STALK_LEVEL,x,y)!=null) dm[0][x][y] = Integer.MAX_VALUE;
				else {
					Block b = scene.getHigherBlock(x, y);
					int fo = b.getFocusObject(this);
					if(fo == Entity.FOCUS_BLOCK) dm[0][x][y] = Integer.MAX_VALUE;
				}
			}
		}
		
		this.dm = dm;
		return dm;
	}
		
	public void setDestination(Entity destination){
		if(destination==null){
			dest_path = null;
			this.destination = null;
		}
		else {
			Scene scene = SceneView.getScene();
			dest_path = scene.getDestPathFor(destination, this, dm);
			this.destination = dest_path.pop();			
			dm = null;
		}
	}	

	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_SEED);		
		o.put(JSON_SPEED, speed);
		o.put(JSON_SQR_RANGE, sqr_range);
		if(is_planted) o.put(JSON_IS_PLANTED, is_planted);
		if(lean_right) o.put(JSON_LEAN_RIGHT, lean_right);
		return o;
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_DESTINATION)){
			int id = o.optInt(JSON_DESTINATION, -1);
			if(id!=-1){
				this.setDestination(tmp_ids.get(id));
			}
			else {
				Coord c = new Coord(o.getJSONObject(JSON_DESTINATION));
				Block b = SceneView.getScene().getHigherBlock(c.x/Block.getAbsoluteWidth(), c.y/Block.getAbsoluteHeight());
				this.setDestination(b);
			}
		}
		
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(destination!=null) {
			if(o==null) o = new JSONObject();
			
			Entity d = destination;
			if(dest_path!=null && !dest_path.isEmpty()) d = dest_path.firstElement();
			
			Integer id = tmp_ids.get(d);
			if(id==null){
				o.put(JSON_DESTINATION, d.getPosition().toJSON());
			}
			else {
				o.put(JSON_DESTINATION, id);
			}
		}
		
		return o;
	}
	
}

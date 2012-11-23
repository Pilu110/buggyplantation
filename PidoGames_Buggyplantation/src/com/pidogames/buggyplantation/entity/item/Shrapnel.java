package com.pidogames.buggyplantation.entity.item;

import java.util.HashMap;
import java.util.Map;

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

public class Shrapnel extends AnimatingItem {
	
	protected float velocity;
	protected float resistance;
	protected boolean stopped;
	protected boolean back;
	
	private Entity target;
	private int damage;
	private boolean explode;
	
	public static final String JSON_TARGET     = registerJSONKey("tg", Shrapnel.class);
	public static final String JSON_DAMAGE     = registerJSONKey("d", Shrapnel.class);
	public static final String JSON_VELOCITY   = registerJSONKey("v", Shrapnel.class);
	public static final String JSON_RESISTANCE = registerJSONKey("r", Shrapnel.class);

	public Shrapnel(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		if(!o.isNull(JSON_VELOCITY)) velocity = (float)o.getDouble(JSON_VELOCITY);
		else velocity = 0;
		if(!o.isNull(JSON_DAMAGE)) damage = o.getInt(JSON_DAMAGE);
		else damage = 0;
		resistance = (float)o.getDouble(JSON_RESISTANCE);
		stopped = false;
		back    = false;
		target  = null;
	}

	public Shrapnel(int player, int type, int level, int x, int y) {
		this(player,type,level,x,y,0,0,0,null,0,false);
	}
	
	public Shrapnel(int player, int type, int level, int x, int y, int angle, float velocity, float resistance, Entity target, int damage, boolean explode) {
		super(player, type, level, x, y, false);
		setAngle(angle);
		this.velocity   = velocity;
		this.resistance = resistance;
		this.target     = target;
		this.damage     = damage;
		this.explode    = explode;
		stopped = false;
		back    = false;
	}
	
	@Override
	public int getMaxHitPoints(){
		return 0;
	}
	
	@Override
	public int getHitPoints(){
		return 0;
	}
	
	public void setVelocity(float v){
		this.velocity = v;
	}

	public void setResistance(float r){
		this.resistance = r;
	}
	
	protected void onStop(long tic){
		stopped = true;
		velocity = 0;
		Coord p = getPosition();
		if(target!=null && p.d2(target.getPosition())<1300){
			target.damage(damage, this, player);
		}
		if(!startAnimation()){
			Scene scene = SceneView.getScene();
			if(!explode && scene.getHigherBlockLevel((int)(x/Block.getAbsoluteWidth()), (int)(y/Block.getAbsoluteHeight()))==GROUND_LEVEL ) this.setLevel(GROUND_LEVEL);
			else {
				DisappearEffect e = new DisappearEffect(10+rnd.nextInt(100));
				e.setEffectListener(this);
				this.addEffect(e);
				
				if(explode && damage>0){
					for(int i=0; i<3; i++){
						int    r = rnd.nextInt(60);
						double a = rnd.nextDouble() % (Math.PI*2.0);
						scene.addItem(new AnimatingItem(PLAYER_NEUTRAL, R.drawable.i_damage1, AIR_LEVEL, p.x + (int)(r*Math.cos(a)), p.y - (int)(r*Math.sin(a)), true), false);
					}
				}
			}
		}
	}
	
	@Override
	protected boolean hasDamageEffect(){
		return !explode;
	}
		
	@Override
	public void step(long tic){
		super.step(tic);
		if(velocity>0){
			double rad = 2.0*Math.PI/360.0 * (back?(angle+180):angle);
			x += (velocity*Math.sin(rad));
			y -= (velocity*Math.cos(rad));
			
			velocity -= resistance;
			
			if(explode && rnd.nextInt(3)==0){
				Coord p = getPosition();
				SceneView.getScene().addItem(new AnimatingItem(PLAYER_NEUTRAL, R.drawable.i_damage1, AIR_LEVEL, p.x, p.y, true), false);
				
				Map<Item,Boolean> li = SceneView.getScene().getItemsAt(p.x/Block.getAbsoluteWidth(), p.y/Block.getAbsoluteHeight());
				for(Item item : li.keySet()){
					//if(getAlignment(item)!=ALIGN_ALLY){
						item.damage(damage, this, player);
					//}
				}
			}
		}
		else if (!stopped){
			onStop(tic);
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_SHRAPNEL);
		if(velocity>0) o.put(JSON_VELOCITY, velocity);
		if(damage>0)   o.put(JSON_DAMAGE, damage);
		o.put(JSON_RESISTANCE, resistance);
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_TARGET)){
			int id = o.optInt(JSON_TARGET, -1);
			if(id!=-1){
				target = tmp_ids.get(id);
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(target!=null) {
			if(o==null) o = new JSONObject();
			
			Integer id = tmp_ids.get(target);
			if(id!=null){
				o.put(JSON_TARGET, id);
			}
		}
		
		return o;
	}
}

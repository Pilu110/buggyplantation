package com.pidogames.buggyplantation.entity.item.bug;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.entity.item.DisplayedTypeItem;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Nest;

public class AntWorker extends Ant {

	public static final int LEAF_CHIP_HITPOINTS = 25;
	public static final int CARRY_MAX_DURATION  = 1000;
	
	private Item carry;
	private long carry_from;
	private int eaten;
	
	private static final String JSON_CARRY = registerJSONKey("cr",AntWorker.class);
	private static final String JSON_CARRY_FROM = registerJSONKey("cf",AntWorker.class);
	private static final String JSON_EATEN = registerJSONKey("en",AntWorker.class);
	
	public AntWorker(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		
		if(!o.isNull(JSON_CARRY_FROM)) carry_from = o.getLong(JSON_CARRY_FROM);
		else carry_from = 0;
		
		if(!o.isNull(JSON_EATEN)) eaten = o.getInt(JSON_EATEN);
		else eaten = 0;
	}
	
	protected AntWorker(int player, int type, int x, int y, double angle) {
		super(player, type, x, y, angle);
		carry = null;
		carry_from = 0;
		eaten = 0;
		/* TEST CARRY LEAF
		Item leaf = new DisplayedTypeItem(Entity.PLAYER_NEUTRAL, R.drawable.i_leaf_chip1, getLevel(), x, y);
		SceneView.getScene().addItem(leaf, false);
		carry = leaf;
		// END TEST CARRY LEAF */
		
	}
	
	@Override
	protected boolean canEat(){
		return super.canEat() && (carry==null);
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_ANT_WORKER);
		if(carry_from>0) o.put(JSON_CARRY_FROM, carry_from);
		if(eaten>0) o.put(JSON_EATEN, eaten);
		return o;
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_CARRY)){
			int id = o.optInt(JSON_CARRY, -1);
			if(id!=-1){
				carry = (Item)tmp_ids.get(id);
			}
			else {
				carry = null;
			}
		}
		else {
			carry = null;
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(carry!=null) {
			if(o==null) o = new JSONObject();
			
			Integer id = tmp_ids.get(carry);
			if(id!=null){
				o.put(JSON_CARRY, id);
			}
		}
		
		return o;
	}
	
	private void pickUp(Item carry) {
		this.carry = carry;
		this.carry_from = SceneView.getTic();
	}
	
	@Override
	protected void onAttack(Entity e){
		super.onAttack(e);
		if(e instanceof PlantBlock) {
			eaten += ATTACK_DAMAGE[type];
			if(eaten>LEAF_CHIP_HITPOINTS){
				Item leaf = new DisplayedTypeItem(Entity.PLAYER_NEUTRAL, R.drawable.i_leaf_chip1, getLevel(), (int)x, (int)y);
				leaf.setHitpoints(eaten);
				eaten = 0;
				Nest hole = findNearestAntHole();
				if(hole!=null) {
					SceneView.getScene().addItem(leaf, false);
					pickUp(leaf);
					setAngle(angle+180);
					forward(SPEED[type]/2);
					
					//if(hole!=null) 
					setDestination(hole, false, 4000, 0, false, false, null);
					//else setDestination(null, false, 0, 0, false, true, null);
				}
				
				setState(MOVING);
			}
		}
	}
	
	@Override
	public void onReachDestination(boolean is_final){
		super.onReachDestination(is_final);
		if(carry!=null){
			Nest hole = findNearestAntHole();
			if(hole!=null && (getPosition().d2(hole.getPosition())<4000)){
				SceneView.getScene().removeItem(carry);
				hole.hideBug(this, 60);
				hole.addFood(carry.getHitPoints());
				carry = null;
			}
		}
	}
	
	
	private Nest findNearestAntHole(){
		List<Item> items = SceneView.getScene().getItems(GROUND_LEVEL);
		Item nest = null;
		if(items!=null){
			long d = Integer.MAX_VALUE;
			Coord c = getPosition();
			for(Item item : items){
				if(item.getPlayer()==player && item.getType()==R.drawable.i_ant_hole1){
					long d2 = c.d2(item.getPosition());
					if(nest==null || d2<d){
						nest = item;
						d = d2;
					}
				}
			}			
		}
		
		return (Nest)nest;
	}
	
	@Override
	public void step(long tic) {		
		super.step(tic);
		if(state==SQUASHED) return;
		if(carry!=null){
			if(tic-carry_from>CARRY_MAX_DURATION){
				carry.addEffect(new DisappearEffect(100));
				carry = null;
			}
			else {
				carry.setXY(x, y);
				carry.setAngle(angle);
				carry.forward(20);
				
				if(getDestination()==null){
					Nest hole = findNearestAntHole();
					if(hole!=null) setDestination(hole, false, 4000, 0, false, false, null);
				}
			}
		}
	}
	
}

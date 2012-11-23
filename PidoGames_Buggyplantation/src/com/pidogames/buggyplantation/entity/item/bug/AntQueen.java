package com.pidogames.buggyplantation.entity.item.bug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Egg;
import com.pidogames.buggyplantation.entity.item.Nest;

public class AntQueen extends Ant {
	
	private static final String JSON_NEST = registerJSONKey("ns",AntQueen.class);
	
	private static final Map<Integer,Integer> CHILD_COST;
	static {
		HashMap<Integer,Integer> tempMap = new HashMap<Integer,Integer>();
		tempMap.put(Bug.TYPE_ANT_WORKER, 10);
		tempMap.put(Bug.TYPE_ANT_SOLDIER, 50);
		tempMap.put(Bug.TYPE_ANT_WINGED, 250);
		CHILD_COST = Collections.unmodifiableMap(tempMap);
	}
	
	private long birth_from;
	private Nest nest;
	
	public AntQueen(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	protected AntQueen(int player, int type, int x, int y, double angle) {
		super(player, type, x, y, angle);
		nest = null;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_ANT_QUEEN);
		return o;
	}
	
	private int getChildCost(int type){
		return CHILD_COST.get(type);
	}
	
	private int getNextChildType(int stored_food){
		int nct = Bug.TYPE_ANT_WORKER;
		
		int i = rnd.nextInt(100);
		if(i<15) nct = Bug.TYPE_ANT_WINGED;
		else if (i<55) nct = Bug.TYPE_ANT_SOLDIER;
		else nct = Bug.TYPE_ANT_WORKER;
		
		while(getChildCost(nct)>stored_food && nct!=Bug.TYPE_ANT_WORKER){
			switch(nct){
				case Bug.TYPE_ANT_WINGED:	nct = Bug.TYPE_ANT_SOLDIER; break;
				case Bug.TYPE_ANT_SOLDIER:	nct = Bug.TYPE_ANT_WORKER; break;
			}
		}
		
		return nct;
	}
	
	public void setNest(Nest nest){
		this.nest = nest;
	}
	
	@Override
	public void step(long tic) {
		super.step(tic);
		if(state==SQUASHED) return;
		if(state!=EATING){
			if(birth_from==0 && rnd.nextInt(100)==0 && (nest!=null && nest.getStoredFood()>=getChildCost(Bug.TYPE_ANT_WORKER))){
				birth_from = tic;
				setState(GIVE_BIRTH);
			}
			else if(birth_from>0 && tic-birth_from>50){
				int nct = getNextChildType(nest.getStoredFood());
				nest.subFood(getChildCost(nct));
				
				Egg egg = new Egg(player, R.drawable.i_ant_egg, GROUND_LEVEL, (int)x, (int)y, nct, 500);
				egg.setAngle(angle);
				egg.forward(-30);
				SceneView.getScene().addItem(egg, true);
				setState(MOVING);
				birth_from = 0;
			}
		}
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_NEST)){
			int id = o.optInt(JSON_NEST, -1);
			if(id!=-1){
				nest = (Nest)tmp_ids.get(id);
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(nest!=null && nest.getHitPoints()>0) {
			if(o==null) o = new JSONObject();
			
			Integer id = tmp_ids.get(nest);
			if(id!=null){
				o.put(JSON_NEST, id);
			}
		}
		
		return o;
	}
	
}

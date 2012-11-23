package com.pidogames.buggyplantation.entity.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.bug.AntQueen;
import com.pidogames.buggyplantation.entity.item.bug.Bug;

public class Nest extends Item {
	
	static class NestParams {
		int bug_type;
		int birth_period;
		int max_birth_number;
		
		NestParams(int bug_type, int birth_period, int max_birth_number){
			this.bug_type = bug_type;
			this.birth_period = birth_period;
			this.max_birth_number = max_birth_number;
		}
	}
	
	static class HidedBug {
		public static final String JSON_HB_DURATION = registerJSONKey("d", HidedBug.class);
		public static final String JSON_HB_BUG      = registerJSONKey("b", HidedBug.class);
		
		int duration;
		Bug bug;
		
		HidedBug(Bug bug, int duration){
			this.bug = bug;
			this.duration = duration;
		}
		
		HidedBug(JSONObject o, ResIdConverter rc) throws JSONException {
			bug = (Bug)Bug.getItemFromJSON(o.getJSONObject(JSON_HB_BUG), rc);
			duration = o.getInt(JSON_HB_DURATION);
		}
		
		public JSONObject toJSON(ResIdConverter rc) throws JSONException {
			JSONObject o = new JSONObject();
			o.put(JSON_HB_BUG, bug.toJSON(rc));
			o.put(JSON_HB_DURATION, duration);
			return o;
		}
	}
	
	public static final String JSON_BUGS = registerJSONKey("b", Nest.class);
	public static final String JSON_HIDED_BUGS = registerJSONKey("hb", Nest.class);
	public static final String JSON_QUEEN = registerJSONKey("qn", Nest.class);
	public static final String JSON_STORED_FOOD = registerJSONKey("sf", Nest.class);
	
	private static final int SWARM_QUEEN_AMOUNT = 500;
	
	private ArrayList<Bug> bugs;
	private ArrayList<HidedBug> hided_bugs;
	
	private AntQueen queen;
	private int stored_food;
	
	private static HashMap<Integer,NestParams> BUG_NEST = new HashMap<Integer,NestParams>();
	static {
		BUG_NEST.put(R.drawable.i_bug_hole, new NestParams(Bug.TYPE_BUG,100,5));
		BUG_NEST.put(R.drawable.i_ant_hole1, new NestParams(Bug.TYPE_ANT_WORKER,500,100));
		BUG_NEST.put(R.drawable.i_scarab_lair, new NestParams(Bug.TYPE_SCARAB,500,1));
		BUG_NEST.put(R.drawable.i_wasp_hole, new NestParams(Bug.TYPE_WASP,50,1));
	}
	
	public Nest(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		
		hided_bugs = new ArrayList<HidedBug>();
		bugs = new ArrayList<Bug>();
		
		if(!o.isNull(JSON_STORED_FOOD)){
			stored_food = o.getInt(JSON_STORED_FOOD);
		}
		else {
			stored_food = 0;
		}
		
		if(!o.isNull(JSON_HIDED_BUGS)){
			JSONArray a = o.getJSONArray(JSON_HIDED_BUGS);
			for(int i=0; i<a.length(); i++){
				hided_bugs.add(new HidedBug(a.getJSONObject(i), rc));
			}
		}
	}
	
	public Nest(int player, int type, int level, int x, int y){
		super(player, type, level, x,y);
		bugs = new ArrayList<Bug>();
		hided_bugs = new ArrayList<HidedBug>();
		stored_food = 0;
		queen = null;
	}
	
	public void hideBug(Bug bug, int duration){
		SceneView.getScene().removeItem(bug);
		hided_bugs.add(new HidedBug(bug, duration));
	}
	
	public void addFood(int amount){
		stored_food += amount;
	}
	
	public void subFood(int amount){
		stored_food -= amount;		
	}
	
	public int getStoredFood(){
		return stored_food;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		
		ArrayList<HidedBug> hb_to_remove = null;
		for(HidedBug hb : hided_bugs){
			if(hb.duration--<=0){
				if(hb_to_remove == null) hb_to_remove = new ArrayList<HidedBug>();
				hb_to_remove.add(hb);
			}
		}
		if(hb_to_remove!=null) for(HidedBug hb : hb_to_remove) {
			hided_bugs.remove(hb);
			SceneView.getScene().addItem(hb.bug, true);
		}
		
		NestParams params = BUG_NEST.get(type);
		if(rnd.nextInt(params.birth_period)==0){
			ArrayList<Bug> to_remove = null;
			for(Bug b : bugs){
				if(b.getHitPoints() <= 0) {
					if(to_remove == null) to_remove = new ArrayList<Bug>();
					to_remove.add(b);
				}
			}
			if(to_remove!=null) for(Bug b : to_remove) bugs.remove(b);
			
			if(stored_food >= SWARM_QUEEN_AMOUNT && (queen==null || queen.getHitPoints()<=0)){
				stored_food -= SWARM_QUEEN_AMOUNT;
				AntQueen b = (AntQueen)Bug.getInstance(player, Bug.TYPE_ANT_QUEEN, (int)x, (int)y, angle);
				b.setNest(this);
				queen = b;
				bugs.add(b);
				SceneView.getScene().addItem(b, true);
			}
			else {
				if(bugs.size()<params.max_birth_number){
					
					Bug b = Bug.getInstance(player, params.bug_type, (int)x, (int)y, angle);
					bugs.add(b);
					SceneView.getScene().addItem(b, true);
				}
			}
		}				
	}
	
	@Override
	public int getFocusObject(Entity e){
		return FOCUS_NONE;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_NEST);
		
		if(hided_bugs.size()>0){
			JSONArray a = new JSONArray();
			for(HidedBug hb : hided_bugs) a.put(hb.toJSON(rc));
			o.put(JSON_HIDED_BUGS, a);
		}
		
		if(stored_food>0) o.put(JSON_STORED_FOOD, stored_food);
		
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_BUGS)){
			JSONArray json_bugs = o.getJSONArray(JSON_BUGS);
			for(int i=0; i<json_bugs.length(); i++){
				Entity e = tmp_ids.get(json_bugs.get(i));
				if(e!=null) bugs.add((Bug)e);
			}
		}
		
		if(!o.isNull(JSON_QUEEN)){
			queen = (AntQueen)tmp_ids.get(o.get(JSON_QUEEN));
		}
		else {
			queen = null;
		}
		
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(bugs!=null) {
			if(o==null) o = new JSONObject();
			
			JSONArray json_bugs = new JSONArray();
			for(Bug b : bugs){
				json_bugs.put(tmp_ids.get(b));
			}
			o.put(JSON_BUGS, json_bugs);
		}
		
		if(queen!=null) {
			if(o==null) o = new JSONObject();
			o.put(JSON_QUEEN, tmp_ids.get(queen));
		}
			
		return o;
	}

}

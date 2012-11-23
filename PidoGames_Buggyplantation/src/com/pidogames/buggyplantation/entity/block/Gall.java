package com.pidogames.buggyplantation.entity.block;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Nest;

public class Gall extends PlantBlock {
	
	private static final String JSON_NEST = registerJSONKey("nst",Gall.class);
	
	private ArrayList<Nest> nests;
	
	public Gall(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}

	public Gall(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
		nests = new ArrayList<Nest>();
	}
	
	public Gall(Block b){
		this(b.getPlayer(), b.type==R.drawable.b_stalk_ud?R.drawable.b_gall_ud:b.type==R.drawable.b_stalk_rl?R.drawable.b_gall_rl:b.type, b.getX(), b.getY(), b.getLevel(), b.isBuilded());
		//TODO: kell egy ilyen copy kontruktor is
	}
	
	public int getNestCount(){
		return nests.size();
	}
	
	public void addNest(int bug_type) {
		int nx,ny;
		do {
			nx = x*Block.getAbsoluteWidth() + rnd.nextInt(Block.getAbsoluteWidth());
			ny = y*Block.getAbsoluteHeight() + rnd.nextInt(Block.getAbsoluteHeight());		
		} while(!this.isCollision(nx, ny));
		Nest n = new Nest(player, R.drawable.i_wasp_hole, STALK_LEVEL, nx, ny);
		nests.add(n);
		SceneView.getScene().addItem(n, false);
	}
	
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_GALL);
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_NEST)){
			nests = new ArrayList<Nest>();
			JSONArray json_nests = o.getJSONArray(JSON_NEST);
			for(int i=0; i<json_nests.length(); i++){
				Entity e = tmp_ids.get(json_nests.get(i));
				if(e!=null) nests.add((Nest)e);
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(nests!=null) {
			if(o==null) o = new JSONObject();
			
			JSONArray json_nests = new JSONArray();
			for(Nest n : nests){
				json_nests.put(tmp_ids.get(n));
			}
			o.put(JSON_NEST, json_nests);
		}
		
		return o;
	}


}

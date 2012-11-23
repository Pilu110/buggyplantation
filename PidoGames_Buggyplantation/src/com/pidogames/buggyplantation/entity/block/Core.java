package com.pidogames.buggyplantation.entity.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.Poison;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.effect.Effect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Seed;

public class Core extends PlantBlock {

	public static final String JSON_SEED = registerJSONKey("s", Core.class);
	
	private Seed seed;
	
	public Core(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}

	public Core(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
		Coord s = this.getPosition();
		seed = new Seed(this.player, R.drawable.b_seed, AIR_LEVEL, s.x, s.y);
		seed.setSelectable(false);
		SceneView.getScene().addItem(seed, false);
	}

	@Override
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items;
		if(seed!=null){
			items = super.getMenuItems();
			if(is_builded) {
				Scene scene = SceneView.getScene();
				scene.addMenuItemTo(items, R.drawable.menu_sprouting_seed,GameMenu.MENU_TF_ACTION,0);
				if(seed.getType() == R.drawable.b_seed){
					scene.addMenuItemsTo(items, new int[]{
						R.drawable.i_dandelion,
						R.drawable.i_seabean
					}, GameMenu.MENU_TF_BUILD, 107);
				}
			}
		}
		else {
			items = new HashSet<MenuItem>();
		}
		
		return items;
	}
	
	public void setSeed(Seed seed){
		Scene scene = SceneView.getScene();
		if (this.seed!=null) scene.removeItem(this.seed);
		seed.setSelectable(false);
		this.seed = seed;
		scene.addItem(seed, false);
		if(menu!=null) menu.setValid(false);
	}
	
	public int [][][] getDistanceMap(){
		if(seed!=null){
			return seed.getDistanceMap();
		}
		return null;
	}

	public void setDestination(Entity destination){
		if(seed!=null){
			seed.setDestination(destination);
			seed = null;
			if(menu!=null) menu.setValid(false);
			
			DisappearEffect e = new DisappearEffect(50);
			e.setEffectListener(this);
			addEffect(e);
		}
	}
	
	@Override
	public void OnEffectEnd(Effect e) {
		super.OnEffectEnd(e);
		if(e instanceof DisappearEffect) {
			SceneView.getScene().setBlock(LEAF_LEVEL, x, y, null);
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_CORE);
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_SEED)){
			int id = o.optInt(JSON_SEED, -1);
			if(id!=-1){
				seed = (Seed)tmp_ids.get(id);
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(seed!=null) {
			if(o==null) o = new JSONObject();
			
			Integer id = tmp_ids.get(seed);
			if(id!=null){
				o.put(JSON_SEED, id);
			}
		}
		
		return o;
	}
	
	@Override
	public void damage(int hitpoints, Entity e, int player){
		super.damage(hitpoints, e, player);
		Seed seed = this.seed;
		if(seed!=null && this.hitpoints<=0) {
			SceneView.getScene().removeItem(seed);
			this.seed = null;
		}
	}
}

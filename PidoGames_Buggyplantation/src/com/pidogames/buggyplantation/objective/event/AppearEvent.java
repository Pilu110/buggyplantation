package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.interfaces.GroupSelect;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class AppearEvent extends ObjectiveEvent implements GroupSelect {

	private static final String JSON_TARGETS = Entity.registerJSONKey("a", AppearEvent.class);
	
	private HashSet<Entity> targets;
	
	public AppearEvent(int id, Objective parent) {
		super(id, parent);
		targets = new HashSet<Entity>();
	}
	
	@Override
	public HashSet<Entity> getTargets(){
		return targets;
	}
	
	@Override
	public void toggleTarget(Entity e){
		if(e!=null && (e.getLevel()!=Entity.GROUND_LEVEL || (e instanceof Item) || (e instanceof Block && ((Block)e).isNitrogenBlock()))){
			HashSet<Entity> hs = (HashSet<Entity>)targets.clone();
			if(hs.contains(e)) hs.remove(e);
			else hs.add(e);			
			targets = hs;
		}
	}

	@Override
	public int getType() {
		return OBJECTIVE_EVENT_APPEAR;
	}
	
	@Override
	public void onChangeGameMode(int game_mode){
		super.onChangeGameMode(game_mode);
		showHideTargets(getParent().isAchieved() | (game_mode == SceneView.GAME_MODE_MAP_EDITOR), false);
	}	

	@Override
	public void triggerFunction(ObjectiveListener listener) {
		showHideTargets(true,true);
		fulfill();
	}
	
	
	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_SELECT_TARGETS+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.mm_obj_evt_targets)+": "+((targets==null)?SceneView.getString(R.string.none):targets.size()));
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		items.put(o);
	}
	
	private void showHideTargets(boolean show, boolean concurrent){
		Scene scene = getParent().getScene();
		for(Entity e : targets){
			if(e instanceof Item){
				if(show) scene.addItem(((Item)e), concurrent);
				else scene.removeItem((Item)e);
			}
			else if(e instanceof Block){
				Block b = (Block)e;
				scene.setBlock(b.getLevel(), b.getX(), b.getY(), show?b:null);
			}
		}
	}

	@Override
	public void setGroupSelectFlag(int flag) {
	}
	
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		JSONArray json_targets = o.getJSONArray(JSON_TARGETS);
		for(int i=0; i<json_targets.length(); i++){
			Objective.preloadEntityJSON(json_targets.get(i), tmp_ids, rc);
		}
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
		JSONArray json_targets = o.getJSONArray(JSON_TARGETS);
		for(int i=0; i<json_targets.length(); i++){
			Entity e = Objective.convertJSON2Entity(json_targets.get(i), tmp_ids);
			targets.add(e);
		}
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		
		JSONArray json_targets = new JSONArray();
		for(Entity e : targets){
			json_targets.put(Objective.convertEntity2JSON(e, tmp_ids, counter, rc));
		}
		
		o.put(JSON_TARGETS, json_targets);
		
		return o;
	}
	
}

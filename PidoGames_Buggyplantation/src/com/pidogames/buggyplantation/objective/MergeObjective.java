package com.pidogames.buggyplantation.objective;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;

public class MergeObjective extends Objective {

	private static final String JSON_PREREQUISITES = Entity.registerJSONKey("q",MergeObjective.class);
	
	private HashSet<Objective> prerequisites;
	
	public MergeObjective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener){
		super(id, scene, player, title, description, achieved_description, enabled, listener);
		prerequisites = new HashSet<Objective>();
	}
	
	public MergeObjective(int id) {
		super(id);
		prerequisites = new HashSet<Objective>();
	}
	
	public void addPrerequisite(Objective o){
		if(o!=null && o!=this) prerequisites.add(o);
	}
	
	public void removePrerequisite(Objective o){
		prerequisites.remove(o);
	}
	
	@Override
	public void refreshReferences(Scene scene){
		super.refreshReferences(scene);
		HashSet<Objective> p2 = new HashSet<Objective>();
		for(Objective o : prerequisites){
			Objective o2 = scene.getObjectiveById(o.getId());			
			if(o2!=null) p2.add(o2);
		}
		prerequisites = p2;
	}
	
	@Override
	public int getType() {
		return OBJECTIVE_TYPE_MERGE;
	}

	@Override
	protected boolean checkIfAchieved(long tic) {
		for(Objective o : prerequisites){
			if(!o.isAchieved()) return false;
		}
		return true;
	}
	
	
	public JSONArray getPrerequisiteDetailsForMenu(int prereq_id){
		JSONArray items = new JSONArray();
		
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_REMOVE_PREREQUISITE+"="+getId()+","+prereq_id);
			items.put(o);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;		
	}

	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			Resources res = SceneView.getInstance(null).getResources();
			
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_ADD_PREREQUISITE+"="+getId());
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_CHILDREN, true);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_PREREQUISITES);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.prerequisites)+": ");
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
			for(Objective prereq : prerequisites){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_PREREQUISITE+"="+getId()+","+prereq.getId());
				o.put(DialogBox.JSON_TITLE, prereq.getDisplayableTitle());
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				o.put(DialogBox.JSON_CHILDREN, true);
				items.put(o);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
		JSONArray json_prereq = o.getJSONArray(JSON_PREREQUISITES);
		for(int i=0; i<json_prereq.length(); i++){
			Objective objective = scene.getObjectiveById(json_prereq.getInt(i));
			addPrerequisite(objective);
		}
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		
		JSONArray json_prereq = new JSONArray();
		for(Objective prereq : prerequisites){
			json_prereq.put(prereq.getId());
		}
		o.put(JSON_PREREQUISITES, json_prereq);
		
		return o;
	}
	
}

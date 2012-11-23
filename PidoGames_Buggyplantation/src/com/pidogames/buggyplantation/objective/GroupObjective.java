package com.pidogames.buggyplantation.objective;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.interfaces.CountSlider;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.interfaces.GroupSelect;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;

public abstract class GroupObjective extends Objective implements CountSlider, GroupSelect, DisplayableState {

	private static final String JSON_COUNT = Entity.registerJSONKey("o",GroupObjective.class);
	private static final String JSON_GROUP = Entity.registerJSONKey("g",GroupObjective.class);
	
	protected HashSet<Entity> group;
	protected int count;
	private float cr;
	
	public GroupObjective(int id) {
		super(id);
		group = new HashSet<Entity>();
		setCount(1);
	}
	
	public abstract int achievedAt(Entity type);
	public abstract int getMaxCount();
	
	@Override
	public HashSet<Entity> getTargets(){
		return group;
	}
	
	@Override
	public void toggleTarget(Entity e){
		if(e!=null && (e instanceof Item || e.getLevel()!=Entity.GROUND_LEVEL || (e instanceof Block && ((Block)e).isNitrogenBlock()))){
			HashSet<Entity> hs = (HashSet<Entity>)group.clone();
			if(hs.contains(e)) hs.remove(e);
			else hs.add(e);
			group = hs;
			
			int max = getMaxCount();
			if(count>max) setCount(max);
			else setCount(count);
		}		
	}
	
	public void setCount(int count){
		if(count<1) count = 1;
		this.count = count;
		int max = getMaxCount();
		if(max>0) this.cr = (float)count/max;
		else this.cr = 1.0f;
	}
	
	@Override
	public int getCount(){
		return count;
	}
	
	@Override
	public float getCountRate(){
		return cr;
	}
	
	@Override
	public void setCountRate(float cr){
		this.cr = cr;
		int count = (int)(cr*getMaxCount());
		if(count<1) count = 1;
		this.count = count;
	}
	
	public int getAchievedCount(){
		int sum=0;
		for(Entity e : group){
			sum += achievedAt(e);
			if(sum>=count) return count;
		}
		return sum;
	}
	
	@Override
	protected boolean checkIfAchieved(long tic) {
		int sum=0;
		for(Entity e : group){
			sum += achievedAt(e);
			if(sum>=count) return true;
		}
		
		return false;
	}

	
	@Override
	public String getCountSliderTitle(){
		return count+"";
	}
	
	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			
			Resources res = SceneView.getInstance(null).getResources();
			
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECT_GROUP+"="+getId());
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_group_members)+": "+((group==null)?res.getString(R.string.none):group.size()));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_NUMBER+"="+getId());
			o.put(DialogBox.JSON_TITLE, getCountSliderTitle());
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			items.put(o);				
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	@Override
	public String getDisplayableState() {
		return getAchievedCount()+" / "+count;
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o,tmp_ids,rc);
		setCount(o.getInt(JSON_COUNT));
		
		JSONArray json_group = o.getJSONArray(JSON_GROUP);
		for(int i=0; i<json_group.length(); i++){
			preloadEntityJSON(json_group.get(i), tmp_ids, rc);
		}
		
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
		
		JSONArray json_group = o.getJSONArray(JSON_GROUP);
		for(int i=0; i<json_group.length(); i++){
			Entity e = convertJSON2Entity(json_group.get(i), tmp_ids);
			if(e!=null) group.add(e);
		}
	}
		
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		o.put(JSON_COUNT, count);
		
		JSONArray json_group = new JSONArray();
		for(Entity e : group){
			json_group.put(convertEntity2JSON(e, tmp_ids, counter, rc));
		}
		o.put(JSON_GROUP, json_group);
		
		return o;
	}
	
}

package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.GroupSelect;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class CommandMoveEvent extends ObjectiveEvent implements GroupSelect {
	
	private static final String JSON_GROUP = Entity.registerJSONKey("g", CommandMoveEvent.class);
	private static final String JSON_DEST = Entity.registerJSONKey("d", CommandMoveEvent.class);
	private static final String JSON_ATTACK = Entity.registerJSONKey("a", CommandMoveEvent.class);
	private static final String JSON_FULFILL_MODE = Entity.registerJSONKey("u", CommandMoveEvent.class);
	private static final String JSON_AUTO_FOCUS = Entity.registerJSONKey("o", CommandMoveEvent.class);
	
	public static final int FULFILL_PARALELL   = 0;
	public static final int FULFILL_REACH_DEST = 1;
	public static final int FULFILL_ELIMINATE_TARGET = 2;
	
	private HashSet<Entity> group;
	private Entity destination;
	
	private HashSet<Entity> dest_set;
	private boolean select_dest;
	
	private boolean attack_dest;
	private int fulfill_mode;
	private boolean auto_focus;

	public CommandMoveEvent(int id, Objective parent) {
		super(id, parent);
		group = new HashSet<Entity>();
		destination = null;
		attack_dest = false;
		dest_set = new HashSet<Entity>();
		fulfill_mode = FULFILL_PARALELL;
		auto_focus = false;
	}
	
	public boolean isAttackDest(){
		return attack_dest;
	}
	
	public void setAttackDest(boolean attack_dest){
		this.attack_dest = attack_dest;
	}
	
	public boolean isAutoFocus(){
		return auto_focus;
	}
	
	public void setAutoFocus(boolean auto_focus){
		this.auto_focus = auto_focus;
	}
	
	public int getFulfillMode(){
		return fulfill_mode;
	}
	
	public void setFulfillMode(int fulfill_mode){
		this.fulfill_mode = fulfill_mode;
	}
	
	public Coord getFocusCoord(){
		long sx  = 0;
		long sy  = 0;
		int  cnt = 0;
		for(Entity e : group){
			if(e.getHitPoints()>0){
				Coord c2 = e.getPosition();
				sx += c2.x;
				sy += c2.y;
				cnt++;
			}
		}
		return (cnt>0)?new Coord((int)(sx/cnt),(int)(sy/cnt)):null;
	}

	public void checkIfFulfilled(){
		switch(fulfill_mode){
			case FULFILL_PARALELL: fulfill(); return;
			case FULFILL_REACH_DEST:
				for(Entity e : group){
					Bug b = (Bug)e;
					if(b.getHitPoints()>0 && !b.isDestinationReached(destination)) return; //b.getFinalDestination()==destination) return;
				}
				fulfill();
			return;
			case FULFILL_ELIMINATE_TARGET:
				if(destination==null || destination.getHitPoints()<=0) fulfill();
			return;
		}
	}
	
	
	@Override
	public HashSet<Entity> getTargets() {
		if(select_dest){
			return dest_set;
		}
		else {
			return group;			
		}
	}

	@Override
	public void toggleTarget(Entity e) {
		if(select_dest){
			if(e!=null){
				destination = e;
				HashSet<Entity> hs = new HashSet<Entity>();
				hs.add(e);
				dest_set = hs;
			}
		}
		else {
			if(e!=null && e instanceof Bug){
				HashSet<Entity> hs = (HashSet<Entity>)group.clone();
				if(hs.contains(e)) hs.remove(e);
				else hs.add(e);
				group = hs;
			}
		}
	}
	
	public void setDestination(Entity destination){
		this.destination = destination;
	}

	@Override
	public String getDisplayableTitle() {
		return getTitle();
	}

	@Override
	public int getType() {
		return OBJECTIVE_EVENT_COMMAND_MOVE;
	}

	@Override
	protected void triggerFunction(ObjectiveListener listener) {
		int [][][] dm = null;
		int march_index = 0;
		int s = group.size();
		int dest_range_d2 = Block.getAbsoluteWidth()*Block.getAbsoluteWidth();
		Scene scene = getParent().getScene();
		for(Entity e : group){
			dm = scene.getDistanceMapFrom(e, 0, dm);
			((Bug)e).setDestination(destination, false, dest_range_d2, march_index, attack_dest, false, dm);
			march_index++;
			if(s<6)			while(march_index%8!=5 && march_index%8!=6) march_index++;
			else if (s<11)	while(march_index%4!=1 && march_index%4!=2) march_index++;
		}
		if(fulfill_mode==FULFILL_PARALELL) fulfill();
		else listener.setEvent(this);
	}

	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_SELECT_TARGETS+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.mm_obj_group_members)+": "+((group==null)?SceneView.getString(R.string.none):group.size()));
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		String dest_pos;
		if(destination!=null){
			Coord pos = destination.getPosition();
			dest_pos = "["+pos.x/Block.getAbsoluteWidth() + "," + pos.y/Block.getAbsoluteHeight()+"]";
		}
		else {
			dest_pos = SceneView.getString(R.string.none);
		}
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_SELECT_DESTINATION+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.mm_obj_evt_destination)+": "+dest_pos);
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_ATTACK_DESTINATION+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_FULFILL_MODE+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
		o.put(DialogBox.JSON_MAX_VALUE, 3);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_AUTO_FOCUS+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
	}

	@Override
	public void setGroupSelectFlag(int flag) {
		select_dest = (flag==1);
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		
		fulfill_mode = o.getInt(JSON_FULFILL_MODE);
		
		if(!o.isNull(JSON_ATTACK)) attack_dest = o.getBoolean(JSON_ATTACK);
		else attack_dest = false;
		
		if(!o.isNull(JSON_AUTO_FOCUS)) auto_focus = o.getBoolean(JSON_AUTO_FOCUS);
		else auto_focus = false;
		
		if(!o.isNull(JSON_DEST)) Objective.preloadEntityJSON(o.get(JSON_DEST), tmp_ids, rc);
				
		JSONArray json_group = o.getJSONArray(JSON_GROUP);
		for(int i=0; i<json_group.length(); i++){
			Objective.preloadEntityJSON(json_group.get(i), tmp_ids, rc);
		}
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
		
		if(!o.isNull(JSON_DEST)) destination = Objective.convertJSON2Entity(o.get(JSON_DEST), tmp_ids);
		else destination = null;
		
		JSONArray json_group = o.getJSONArray(JSON_GROUP);
		for(int i=0; i<json_group.length(); i++){
			Entity e = Objective.convertJSON2Entity(json_group.get(i), tmp_ids);
			group.add(e);
		}
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		JSONArray json_group = new JSONArray();
		
		o.put(JSON_FULFILL_MODE, fulfill_mode);
		if(attack_dest) o.put(JSON_ATTACK, attack_dest);
		if(auto_focus) o.put(JSON_AUTO_FOCUS, auto_focus);
		
		if(destination!=null) o.put(JSON_DEST, Objective.convertEntity2JSON(destination, tmp_ids, counter, rc));		
		
		for(Entity e : group){
			json_group.put(Objective.convertEntity2JSON(e, tmp_ids, counter, rc));
		}		
		o.put(JSON_GROUP, json_group);
		
		return o;
	}
	
}

package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;

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
import com.pidogames.buggyplantation.objective.Objective;

public class StarterEvent extends ObjectiveEvent {
	
	public static final int ACTION_START   = 0;
	public static final int ACTION_STOP    = 1;
	public static final int ACTION_RESTART = 2;
	public static final int [] ACTIONS = {
		ACTION_START,
		ACTION_STOP,
		ACTION_RESTART
	};
	
	private static final String JSON_TARGET = Entity.registerJSONKey("r", StarterEvent.class);
	private static final String JSON_ACTION = Entity.registerJSONKey("a", StarterEvent.class);

	private Objective target;
	private int action;
	
	public StarterEvent(int id, Objective parent, Objective target, int action){
		super(id, parent);
		this.target = target;
		this.action = action;
	}
	
	public StarterEvent(int id, Objective parent){
		super(id, parent);
		target = null;
		action = ACTION_START;
	}
	
	public void setTarget(Objective target){
		this.target = target;
	}
	
	public void setAction(int action) {
		this.action = action;
	}
	
	public int getAction(){
		return action;
	}
	
	@Override
	public void refreshReferences(Scene scene){
		super.refreshReferences(scene);
		if(target!=null) {
			target = scene.getObjectiveById(target.getId());
		}
	}
	
	@Override
	public void triggerFunction(ObjectiveListener listener) {
		if(target!=null) {
			switch(action){
				case ACTION_START: target.setEnabled(true, true); break;
				case ACTION_STOP:  target.setEnabled(false, true); break;
				case ACTION_RESTART: target.restart(); break;
			}
		}
		fulfill();
	}

	@Override
	public int getType(){
		return OBJECTIVE_EVENT_ENABLE;
	}
	
	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		Resources res = SceneView.getInstance(null).getResources();
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_TARGET+"="+getId());
		o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_evt_target)+": ");
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_CHILDREN, true);
		items.put(o);
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_TARGET+"="+getId());
		o.put(DialogBox.JSON_TITLE, (target==null)?res.getString(R.string.none):target.getDisplayableTitle());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		o.put(DialogBox.JSON_CHILDREN, true);
		items.put(o);
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_STARTER_ACTION+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
		o.put(DialogBox.JSON_MAX_VALUE, ACTIONS.length);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
	}
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
		action = o.getInt(JSON_ACTION);
		if(!o.isNull(JSON_TARGET)) target = getParent().getScene().getObjectiveById(o.getInt(JSON_TARGET));
		else target = null;
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		o.put(JSON_ACTION, action);
		if(target!=null) o.put(JSON_TARGET, target.getId());
		return o;
	}
}

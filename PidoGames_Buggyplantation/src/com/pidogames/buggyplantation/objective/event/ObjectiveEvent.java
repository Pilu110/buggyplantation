package com.pidogames.buggyplantation.objective.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.DefeatAreaObjective;
import com.pidogames.buggyplantation.objective.DelayObjective;
import com.pidogames.buggyplantation.objective.Objective;

public abstract class ObjectiveEvent {
	public static final int OBJECTIVE_EVENT_VICTORY      = 0;
	public static final int OBJECTIVE_EVENT_DEFEATED     = 1;
	public static final int OBJECTIVE_EVENT_ENABLE       = 2;
	public static final int OBJECTIVE_EVENT_APPEAR       = 3;
	public static final int OBJECTIVE_EVENT_AUTO_SCROLL  = 4;
	public static final int OBJECTIVE_EVENT_POPUP_TEXT   = 5;
	public static final int OBJECTIVE_EVENT_COMMAND_MOVE = 6;
	public static final int OBJECTIVE_EVENT_DELAY        = 7;
	public static final int OBJECTIVE_EVENT_CINEMATIC    = 8;
	public static final int OBJECTIVE_EVENT_MENU_ITEM    = 9;
	
	public static final Class [] OBJECTIVE_EVENT_CLASSES = {
		VictoryEvent.class,
		DefeatedEvent.class,
		StarterEvent.class,
		AppearEvent.class,
		AutoScrollEvent.class,
		PopupTextEvent.class,
		CommandMoveEvent.class,
		DelayEvent.class,
		CinematicEvent.class,
		MenuItemEvent.class
	};
	
	public  static final String JSON_ID        = Entity.registerJSONKey("i", ObjectiveEvent.class);
	private static final String JSON_CLASS     = Entity.registerJSONKey("c", ObjectiveEvent.class);
	private static final String JSON_FULFILLED = Entity.registerJSONKey("f", ObjectiveEvent.class);
	private static final String JSON_TRIGGERED = Entity.registerJSONKey("t", ObjectiveEvent.class);
	
	private int id;	
	private Objective parent;
	private boolean fulfilled;
	private boolean triggered;
	
	public static ObjectiveEvent getEvent(int event_type, int event_id, Objective parent){
		try {
			Constructor<ObjectiveEvent> co = ObjectiveEvent.OBJECTIVE_EVENT_CLASSES[event_type].getConstructor(new Class[]{int.class, Objective.class});
			ObjectiveEvent event = co.newInstance(event_id, parent);
			parent.addEvent(event);
			return event;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ObjectiveEvent getEvent(JSONObject o, Objective parent, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		ObjectiveEvent event = getEvent(o.getInt(JSON_CLASS), o.getInt(JSON_ID), parent);
		event.initJSON(o, tmp_ids, rc);
		return event;
	}
		
	public ObjectiveEvent(int id, Objective parent){
		this.id = id;
		this.parent = parent;
		this.fulfilled = false;
		this.triggered = false;
	}
	
	public abstract int getType();
	protected abstract void triggerFunction(ObjectiveListener listener);
	
	public void trigger(){
		trigger(parent.getListener());
	}
	
	public void trigger(ObjectiveListener listener){
		triggered = true;
		fulfilled = false;
		Log.d("APPEAREVT", "TRIGGER EVT--:"+this);
		triggerFunction(listener);
	}
	
	public void fulfill(){
		fulfilled = true;
		triggered = false;
	}
	
	public boolean isFulfilled(){
		return fulfilled;
	}
	
	public boolean isTriggered(){
		return triggered;
	}
	
	public int getId(){
		return id;
	}
	
	public String getTitle(){
		String [] titles = SceneView.getInstance(null).getResources().getStringArray(R.array.objective_event);
		return titles[getType()] + " - "+id;
	}
	
	public String getDisplayableTitle(){
		return getTitle();
	}
	
	public Objective getParent(){
		return parent;
	}
	
	public void refreshReferences(Scene scene){
		if(parent!=null) {
			parent = scene.getObjectiveById(parent.getId());
		}
	}
	
	public void onChangeGameMode(int game_mode){
	}
	
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
	}
	
	public JSONArray getEventDetailsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			JSONObject o = new JSONObject();
			Resources res = SceneView.getInstance(null).getResources();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_REMOVE_EVENT+"="+getId());
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_remove_event));
			items.put(o);
			
			addEventTypeDetailsForMenu(items);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_FULFILLED)) fulfilled = o.getBoolean(JSON_FULFILLED);
		if(!o.isNull(JSON_TRIGGERED)) triggered = o.getBoolean(JSON_TRIGGERED);		
	}		
	
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
	}
	
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = new JSONObject();
		
		o.put(JSON_ID, id);
		o.put(JSON_CLASS, getType());
		if(SceneView.getInstance(null).getGameMode()!=SceneView.GAME_MODE_MAP_EDITOR){
			if(fulfilled) o.put(JSON_FULFILLED, fulfilled);
			if(triggered) o.put(JSON_TRIGGERED, triggered);
		}
		
		return o;
	}
}

package com.pidogames.buggyplantation.objective;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;

public abstract class Objective {
	
	public static final int NO_ID = -1;
	
	public static final int OBJECTIVE_TYPE_DEFEAT       = 0;
	public static final int OBJECTIVE_TYPE_EXIST        = 1;
	public static final int OBJECTIVE_TYPE_COUNTDOWN    = 2;
	public static final int OBJECTIVE_TYPE_MERGE        = 3;
	public static final int OBJECTIVE_TYPE_DEFEAT_GROUP = 4;
	public static final int OBJECTIVE_TYPE_RESOURCE		= 5;
	public static final int OBJECTIVE_TYPE_STATISTICS	= 6;
	public static final int OBJECTIVE_TYPE_STARTUP		= 7;
	
	public static final Class [] OBJECTIVE_CLASSES = {
		DefeatAreaObjective.class,
		ExistAreaObjective.class,
		DelayObjective.class,
		MergeObjective.class,
		DefeatGroupObjective.class,
		ResourceObjective.class,
		StatisticsObjective.class,
		StartUpObjective.class
	};
	
	public static final String JSON_ID = Entity.registerJSONKey("i",Objective.class);
	private static final String JSON_CLASS = Entity.registerJSONKey("c",Objective.class);
	private static final String JSON_ACHIEVED = Entity.registerJSONKey("a",Objective.class);
	private static final String JSON_ENABLED = Entity.registerJSONKey("e",Objective.class);
	private static final String JSON_PLAYER = Entity.registerJSONKey("p",Objective.class);
	private static final String JSON_EVENTS = Entity.registerJSONKey("v",Objective.class);
	private static final String JSON_TITLE = Entity.registerJSONKey("t",Objective.class);
	private static final String JSON_DESC  = Entity.registerJSONKey("d",Objective.class);
	private static final String JSON_ADESC = Entity.registerJSONKey("s",Objective.class);
	
	private boolean achieved;
	private boolean enabled;
	protected int player;
	protected Scene scene;
	private ObjectiveListener listener;
	private LinkedList<ObjectiveEvent> events;
	private String title;
	private String description;
	private String achieved_description;
	
	private int id;	
	
	public static Objective getObjective(int type, int id){
		/*
		Objective o;
		try {
			o = (Objective)Objective.OBJECTIVE_CLASSES[type].newInstance();
			return o;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
		*/
		try {
			Constructor<Objective> co = Objective.OBJECTIVE_CLASSES[type].getConstructor(new Class[]{int.class});
			Objective o = co.newInstance(id);
			return o;
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
	
	public static Objective getObjective(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		Objective objective = getObjective(o.getInt(JSON_CLASS), o.getInt(JSON_ID));
		objective.initJSON(o,tmp_ids,rc);
		return objective;
	}
	
	public Objective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener){
		this.id = id;
		this.scene = scene;
		this.listener = listener;
		this.player = player;
		this.title = title;
		this.description = description;
		this.achieved_description = achieved_description;
		this.enabled = enabled;
		this.events = new LinkedList<ObjectiveEvent>();
		achieved = false;
	}
		
	public Objective(int id) {
		this.scene = SceneView.getScene();
		this.id = id;
		this.listener = scene.getSceneListener();
		this.player = Entity.PLAYER_PLANT;
		this.title = null;
		this.description = null;
		this.achieved_description = null;
		this.enabled = true;
		this.events = new LinkedList<ObjectiveEvent>();
		achieved = false;
	}
	
	//called in toJSON
	public static Object convertEntity2JSON(Entity e, HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		Integer id = tmp_ids.get(e);
		if(id==null){
			id = counter.useId();
			JSONObject json_e = e.toJSON(rc);
			json_e.put(Entity.JSON_ID, id);
			if(e instanceof Block) json_e.put(Entity.JSON_IS_BLOCK, true);
			tmp_ids.put(e, id);
			return json_e;
		}
		return id;
	}
	
	//called in initJSON
	public static void preloadEntityJSON(Object o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {		
		if(o instanceof JSONObject){
			JSONObject json_e = (JSONObject)o;
			Entity e = Entity.getEntityFromJSON(json_e, rc);
			tmp_ids.put(json_e.getInt(Entity.JSON_ID), e);
		}
	}

	//called in referenciesFromJSON
	public static Entity convertJSON2Entity(Object o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		
		Integer id = null;
		if(o instanceof Integer){
			id = (Integer)o;
		}
		else if(o instanceof JSONObject){
			JSONObject json_e = (JSONObject)o;
			id = json_e.getInt(Entity.JSON_ID);
		}
		else if(o instanceof String){
			try {
				id = Integer.parseInt((String)o);
			}
			catch(NumberFormatException e){
				id = null;
			}
		}
		
		return id!=null?tmp_ids.get(id):null;		
	}
	
	public ObjectiveListener getListener(){
		return listener;
	}
	
	public abstract int getType();
	protected abstract boolean checkIfAchieved(long tic);
	
	public void setEnabled(boolean enabled, boolean call_event){
		if(call_event && listener!=null) listener.onObjectiveEnabled(this, enabled);
		this.enabled = enabled;
	}
	
	public void restart(){
		achieved = false;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public boolean isAchieved() {
		return achieved;
	}
	
	public int getId(){
		return id;
	}
	
	public int getPlayer(){
		return player;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getDisplayableTitle(){
		return title!=null?title:(EditorMenu.MENU_CODE_OBJECTIVE +"="+ id);
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getAchievedDescription(){
		return achieved_description;
	}
	
	public Scene getScene(){
		return scene;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setPlayer(int player){
		this.player = player;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setAchievedDescription(String achieved_description){
		this.achieved_description = achieved_description;
	}
	
	public List<ObjectiveEvent> getEvents(){
		return events;
	}
	
	public void addEvent(ObjectiveEvent event){
		events.add(event);
	}
	
	public void removeEvent(ObjectiveEvent event){
		events.remove(event);
	}
	
	public boolean checkAchieved(long tic) {
		if(enabled && !achieved) {
			achieved = checkIfAchieved(tic);
			if(achieved && listener!=null) {
				listener.onObjectiveAchieved(this); 
				for(ObjectiveEvent event : events) {
					//event.trigger(listener);
					listener.addEventToTriggeredList(event);
				}
			}
		}
		return achieved;
	}
	
	public void refreshReferences(Scene scene){
		for(ObjectiveEvent event : events){
			event.refreshReferences(scene);
		}
	}
	
	public JSONArray getTypeSettingsForMenu(){
		return null;
	}
	
	public JSONArray getEventTypesForMenu(){
		JSONArray items = new JSONArray();
		try {
			String [] titles = SceneView.getInstance(null).getResources().getStringArray(R.array.objective_event);
			
			int i=0;
			for(String event_name : titles){
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_TYPE + "=" + getId()+","+i);
				o.put(DialogBox.JSON_TITLE, event_name);
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				items.put(o);
				i++;
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return items;		
	}
	
	public JSONArray getEventsForMenu(){
		JSONArray items = new JSONArray();
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_ADD_EVENT + "=" + getId());
			o.put(DialogBox.JSON_CHILDREN, true);
			items.put(o);
						
			for(ObjectiveEvent event : events){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVENT + "=" + event.getId());
				o.put(DialogBox.JSON_TITLE, event.getTitle());
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
	
	public ObjectiveEvent getEventById(int id){
		for(ObjectiveEvent event : events){
			if(event.getId()==id) return event;
		}
		return null;
	}
	
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		player = o.getInt(JSON_PLAYER);
		if(!o.isNull(JSON_ACHIEVED)) achieved = o.getBoolean(JSON_ACHIEVED);
		if(!o.isNull(JSON_ENABLED)) enabled = o.getBoolean(JSON_ENABLED);
		if(!o.isNull(JSON_TITLE)) title = o.getString(JSON_TITLE);
		if(!o.isNull(JSON_DESC)) description = o.getString(JSON_DESC);
		if(!o.isNull(JSON_ADESC)) achieved_description = o.getString(JSON_ADESC);
		
		JSONArray json_events = o.getJSONArray(JSON_EVENTS);
		for(int i=0; i<json_events.length(); i++){
			ObjectiveEvent.getEvent(json_events.getJSONObject(i), this, tmp_ids, rc);
		}
	}
	
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		JSONArray json_events = o.getJSONArray(JSON_EVENTS);
		for(int i=0; i<json_events.length(); i++){
			try {
				int event_id = Integer.parseInt(json_events.getJSONObject(i).getString(ObjectiveEvent.JSON_ID));
				ObjectiveEvent event = this.getEventById(event_id);
				event.referenciesFromJSON(json_events.getJSONObject(i), tmp_ids);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = new JSONObject();
		o.put(JSON_ID, id);
		o.put(JSON_CLASS, getType());
		o.put(JSON_PLAYER, player);
		if(achieved && SceneView.getInstance(null).getGameMode()!=SceneView.GAME_MODE_MAP_EDITOR) o.put(JSON_ACHIEVED, achieved);
		if(!enabled) o.put(JSON_ENABLED, enabled);
		if(title!=null) o.put(JSON_TITLE, title);
		if(description!=null) o.put(JSON_DESC, description);
		if(achieved_description!=null) o.put(JSON_ADESC, achieved_description);
		
		JSONArray json_events = new JSONArray();
		for(ObjectiveEvent event : events){
			json_events.put(event.toJSON(tmp_ids, counter, rc));
		}
		o.put(JSON_EVENTS, json_events);
		
		return o;
	}
}

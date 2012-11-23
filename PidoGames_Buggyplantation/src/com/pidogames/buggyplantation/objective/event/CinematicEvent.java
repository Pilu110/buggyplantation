package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Cinematic;
import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.interfaces.OnSetTextListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class CinematicEvent extends ObjectiveEvent implements OnSetTextListener {

	private static final String JSON_CINEMATIC = Entity.registerJSONKey("cn", CinematicEvent.class);
	
	private String cinematic;
	
	public CinematicEvent(int id, Objective parent) {
		super(id, parent);
	}
	
	@Override
	public void setText(String text){
		this.cinematic = text;
	}
	
	@Override
	public String getText(){
		return cinematic;
	}

	@Override
	public String getTitle(){
		if(cinematic==null || cinematic.trim().equals("")) return super.getTitle();
		else {
			String title = SceneView.getString(R.string.cinematic_short) + cinematic;
			if(title.length()>15) title = title.substring(0,12)+"...";
			return title;
		}
	}

	@Override
	public int getType() {
		return OBJECTIVE_EVENT_CINEMATIC;
	}

	public Cinematic getNewCinematic(){
		return new Cinematic(cinematic, true);
	}
	
	@Override
	protected void triggerFunction(ObjectiveListener listener) {
		listener.setEvent(this);
		//listener.onCinematicStart(new Cinematic(cinematic, true));
	}

	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		if(!o.isNull(JSON_CINEMATIC)) cinematic = o.getString(JSON_CINEMATIC);
		else cinematic = null;
	}		
	
	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_TEXT+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.name));
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		if(cinematic!=null) o.put(JSON_CINEMATIC, cinematic);
		return o;
	}

}

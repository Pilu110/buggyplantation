package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.interfaces.OnSetTextListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class PopupTextEvent extends ObjectiveEvent implements OnSetTextListener {
	
	private static final String JSON_TEXT = Entity.registerJSONKey("e", PopupTextEvent.class);
	
	private String text;
	
	public PopupTextEvent(int id, Objective parent) {
		super(id, parent);
		text = null;
	}
	
	@Override
	public void setText(String text){
		this.text = text;
	}
	
	@Override
	public String getText(){
		return text;
	}

	@Override
	public String getTitle(){
		if(text==null || text.trim().equals("")) return super.getTitle();
		else {
			String title = text;
			if(title.length()>10) title = title.substring(0,7)+"...";
			return title;
		}
	}

	@Override
	public void triggerFunction(ObjectiveListener listener) {
		if(listener!=null) {
			listener.setEvent(this);
			listener.onPopupText(text);
		}
		else fulfill();
	}
	
	@Override
	public int getType(){
		return OBJECTIVE_EVENT_POPUP_TEXT;
	}

	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_TEXT+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		if(!o.isNull(JSON_TEXT)) text = o.getString(JSON_TEXT);
		else text = null;
	}		
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		if(text!=null) o.put(JSON_TEXT, text);
		return o;
	}
}

package com.pidogames.buggyplantation.interfaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.menu.DialogBox;

public interface DialogBoxListener {
	public void onDialogBoxItemSelected(DialogBox src, JSONObject item) throws JSONException ;
	public void cancelNextTap();
	public float getSlidebarRate(DialogBox src, String code);
	public boolean getCheckboxState(DialogBox src, String code);
	public int getListState(DialogBox src, String code);
	public JSONArray getChildrenFor(DialogBox src, String code);
	public void onToggleCheckbox(DialogBox src, String code);
	public void onToggleList(DialogBox src, String code, int min, int max);
	public void onSetSlidebarRate(DialogBox src, String code, float rate);
}

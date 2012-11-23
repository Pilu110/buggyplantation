package com.pidogames.buggyplantation.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.R.array;
import com.pidogames.buggyplantation.R.string;
import com.pidogames.buggyplantation.menu.DialogBox;

import android.content.Context;

public class EditorMenu extends DialogBox {

	public static final String MENU_CODE_TEST_MAP = "TEST_MAP";
	public static final String MENU_CODE_MAP_SETTINGS = "MAP_SETTINGS";
	public static final String MENU_CODE_EXIT_EDITOR = "EXIT_EDITOR";
	
	public static final String MENU_CODE_MAP_TYPE = "MAP_TYPE";

	public static final String MENU_CODE_MAP_DESCRIPTION = "MAP_DESCRIPTION";
	public static final String MENU_CODE_MAP_WIDTH       = "MAP_WIDTH";
	public static final String MENU_CODE_MAP_HEIGHT      = "MAP_HEIGHT";
	public static final String MENU_CODE_NEXT_MAP        = "NEXT_MAP";
	public static final String MENU_CODE_NMFILE          = "NMFILE";
	public static final String MENU_CODE_SELECT_NEXT_MAP = "SELECT_NEXT_MAP";
	public static final String MENU_CODE_CLEAR_NEXT_MAP  = "CLEAR_NEXT_MAP";
	
	public static final String MENU_CODE_ADD_OBJECTIVE  = "ADD_OBJECTIVE";
	public static final String MENU_CODE_REMOVE_OBJECTIVE  = "REMOVE_OBJECTIVE";
	
	public static final String MENU_CODE_OBJ_TITLE  = "OBJ_TITLE";
	public static final String MENU_CODE_OBJ_DESC   = "OBJ_DESC";
	public static final String MENU_CODE_OBJ_ACHIEVED_DESC = "OBJ_ACHIEVED_DESC";
	public static final String MENU_CODE_OBJ_STATUS = "OBJ_STATUS";
	public static final String MENU_CODE_OBJ_PLAYER = "OBJ_PLAYER";
	public static final String MENU_CODE_OBJ_EVENTS = "OBJ_EVENTS";
	public static final String MENU_CODE_OBJ_TYPE   = "OBJ_TYPE";
	public static final String MENU_CODE_OBJ_TYPE_ARGS = "OBJ_TYPE_ARGS";
	
	public static final String MENU_CODE_OBJ_ADD_EVENT    = "OBJ_ADD_EVENT";
	public static final String MENU_CODE_OBJ_REMOVE_EVENT = "OBJ_REMOVE_EVENT";
	public static final String MENU_CODE_OBJ_EVENT        = "OBJ_EVENT";
	public static final String MENU_CODE_OBJ_EVT_TYPE     = "OBJ_EVT_TYPE";
	public static final String MENU_CODE_OBJ_EVT_TARGET   = "OBJ_EVT_TARGET";
	public static final String MENU_CODE_OBJ_EVT_STARTER_ACTION = "OBJ_EVT_ACTION";
	public static final String MENU_CODE_OBJ_EVT_SELECT_TARGETS = "OBJ_EVT_SELECT_TARGETS";
	
	public static final String MENU_CODE_OBJ_EVT_MI_SET_ENABLED = "OBJ_EVT_MI_SET_ENABLED";
	public static final String MENU_CODE_OBJ_EVT_SELECT_MENU_ITEMS = "OBJ_EVT_SELECT_MENU_ITEMS";
	public static final String MENU_CODE_OBJ_EVT_MENU_ITEM_LIST = "OBJ_EVT_MENU_ITEM_LIST";
	public static final String MENU_CODE_OBJ_EVT_ADD_MENU_ITEM  = "OBJ_EVT_ADD_MENU_ITEM";
	public static final String MENU_CODE_OBJ_EVT_REMOVE_MENU_ITEM = "OBJ_EVT_REMOVE_MENU_ITEM";
	public static final String MENU_CODE_OBJ_EVT_MI_ITEM = "OBJ_EVT_MI_ITEM";
	public static final String MENU_CODE_OBJ_EVT_MI_ITEM_DETAILS = "OBJ_EVT_MI_ITEM_DETAILS";
	
	public static final String MENU_CODE_OBJ_EVT_SELECT_DESTINATION = "OBJ_EVT_SELECT_DESTINATION";
	public static final String MENU_CODE_OBJ_EVT_ATTACK_DESTINATION = "OBJ_EVT_ATTACK_DESTINATION";
	public static final String MENU_CODE_OBJ_EVT_FULFILL_MODE = "OBJ_EVT_FULFILL_MODE";
	public static final String MENU_CODE_OBJ_EVT_AUTO_FOCUS   = "OBJ_EVT_AUTO_FOCUS";
	public static final String MENU_CODE_TARGET = "TARGET";
	public static final String MENU_CODE_OBJ_EVT_TEXT = "OBJ_EVT_TEXT";
	public static final String MENU_CODE_OBJ_EVT_SET_VIEWPOINT = "OBJ_EVT_SET_VIEWPOINT";
	public static final String MENU_CODE_OBJ_EVT_DELAY_SECONDS  = "OBJ_EVT_DELAY_SECONDS";
	public static final String MENU_CODE_OBJ_EVT_DELAY_TICS  = "OBJ_EVT_DELAY_TICS";
	
	public static final String MENU_CODE_OBJ_SELECTED_AREA  = "OBJ_SELECTED_AREA";
	public static final String MENU_CODE_OBJ_SELECT_GLOBAL_AREA  = "OBJ_SELECT_GLOBAL";
	public static final String MENU_CODE_OBJ_SELECTED_TYPE  = "OBJ_SELECTED_TYPE";
	public static final String MENU_CODE_OBJ_SELECTED_TYPE_RESID = "OBJ_ST_RESID";
	public static final String MENU_CODE_OBJ_SELECT_ALL_TYPE = "OBJ_SELECT_ALL_TYPE";
	public static final String MENU_CODE_OBJ_TOGGLE_POISONOUS = "OBJ_TOGGLE_POISONOUS";
	public static final String MENU_CODE_OBJ_TOGGLE_SPIKY = "OBJ_TOGGLE_SPIKY";
	public static final String MENU_CODE_OBJ_TOGGLE_MISSILES_ABOVE = "OBJ_TOGGLE_MISSILES_ABOVE";
	public static final String MENU_CODE_OBJ_SELECT_POISONOUS = "OBJ_SELECT_POISONOUS";
	public static final String MENU_CODE_OBJ_SELECTED_NUMBER = "OBJ_SELECTED_NUMBER";
	public static final String MENU_CODE_OBJ_DELAY_SECONDS  = "OBJ_DELAY_SECONDS";
	public static final String MENU_CODE_OBJ_DELAY_MINUTES  = "OBJ_DELAY_MINUTES";
	public static final String MENU_CODE_OBJ_DELAY_HOURS    = "OBJ_DELAY_HOURS";
	public static final String MENU_CODE_OBJ_SELECT_GROUP   = "OBJ_SELECT_GROUP";
	
	public static final String MENU_CODE_OBJ_AM_TITLE      = "OBJ_AM_TITLE";
	public static final String MENU_CODE_OBJ_AM_HUNDREDS   = "OBJ_AM_HUNDREDS";
	public static final String MENU_CODE_OBJ_AM_THOUSANDS  = "OBJ_AM_THOUSANDS";
	public static final String MENU_CODE_OBJ_RESOURCE_TYPE = "OBJ_RESOURCE_TYPE";
	public static final String MENU_CODE_OBJ_STAT_TYPE     = "OBJ_STAT_TYPE";
	public static final String MENU_CODE_OBJ_COUNT_FROM_START = "OBJ_FROM_START";
	
	public static final String MENU_CODE_OBJ_PREREQUISITES  = "OBJ_PREREQS";
	public static final String MENU_CODE_OBJ_PREREQUISITE   = "OBJ_PREREQ";
	public static final String MENU_CODE_PREREQUISITE       = "PREREQ";
	public static final String MENU_CODE_OBJ_ADD_PREREQUISITE    = "OBJ_ADD_PREREQ";
	public static final String MENU_CODE_OBJ_REMOVE_PREREQUISITE = "OBJ_REMOVE_PREREQ";
	
	private static final Map<String, Integer> TITLE_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		
		tempMap.put(MENU_CODE_TEST_MAP, R.string.mm_test_map);
		tempMap.put(MENU_CODE_MAP_SETTINGS, R.string.mm_map_settings);
		tempMap.put(MENU_CODE_EXIT_EDITOR, R.string.mm_exit_editor);
		tempMap.put(MENU_CODE_NEXT_MAP, R.string.mm_next_map);
		tempMap.put(MENU_CODE_MAP_WIDTH, R.string.mm_map_width);
		tempMap.put(MENU_CODE_MAP_HEIGHT, R.string.mm_map_height);
		tempMap.put(MENU_CODE_MAP_DESCRIPTION, R.string.mm_map_description);
		
		tempMap.put(MENU_CODE_MAP_TYPE, R.string.mm_map_type);
		
		tempMap.put(MENU_CODE_ADD_OBJECTIVE, R.string.mm_add_objective);
		tempMap.put(MENU_CODE_REMOVE_OBJECTIVE, R.string.mm_remove_objective);
		
		tempMap.put(MENU_CODE_OBJ_TITLE, R.string.mm_obj_title);
		tempMap.put(MENU_CODE_OBJ_DESC, R.string.mm_obj_desc);
		tempMap.put(MENU_CODE_OBJ_ACHIEVED_DESC, R.string.mm_obj_achieved_desc);
		tempMap.put(MENU_CODE_OBJ_PLAYER, R.string.mm_obj_player);
		tempMap.put(MENU_CODE_OBJ_EVENTS, R.string.mm_obj_events);
		tempMap.put(MENU_CODE_OBJ_STATUS, R.string.mm_obj_status);
		
		tempMap.put(MENU_CODE_OBJ_ADD_EVENT, R.string.mm_obj_add_event);
		tempMap.put(MENU_CODE_OBJ_REMOVE_EVENT, R.string.mm_obj_remove_event);
		tempMap.put(MENU_CODE_OBJ_EVT_STARTER_ACTION, R.string.mm_obj_evt_starter_action);
		
		tempMap.put(MENU_CODE_OBJ_SELECTED_AREA, R.string.mm_obj_selected_area);
		tempMap.put(MENU_CODE_OBJ_SELECT_GLOBAL_AREA, R.string.mm_obj_select_global_area);
		tempMap.put(MENU_CODE_OBJ_SELECTED_TYPE, R.string.mm_obj_selected_type);
		tempMap.put(MENU_CODE_OBJ_SELECT_ALL_TYPE, R.string.mm_obj_select_all_type);
		tempMap.put(MENU_CODE_OBJ_SELECTED_NUMBER, R.string.mm_obj_selected_number);
		tempMap.put(MENU_CODE_OBJ_TOGGLE_SPIKY, R.string.mm_obj_only_spiky);
		tempMap.put(MENU_CODE_OBJ_TOGGLE_POISONOUS, R.string.mm_obj_only_poisonous);
		tempMap.put(MENU_CODE_OBJ_TOGGLE_MISSILES_ABOVE, R.string.mm_obj_only_missiles_above);
		
		tempMap.put(MENU_CODE_OBJ_ADD_PREREQUISITE, R.string.mm_add_objective);
		tempMap.put(MENU_CODE_OBJ_REMOVE_PREREQUISITE, R.string.mm_remove_objective);
		tempMap.put(MENU_CODE_OBJ_EVT_TEXT, R.string.mm_obj_evt_text);
		tempMap.put(MENU_CODE_OBJ_EVT_SET_VIEWPOINT, R.string.mm_obj_evt_set_viewpoint);
		tempMap.put(MENU_CODE_OBJ_EVT_ATTACK_DESTINATION, R.string.mm_obj_evt_attack);
		tempMap.put(MENU_CODE_OBJ_EVT_FULFILL_MODE, R.string.mm_obj_evt_fulfill_mode);
		tempMap.put(MENU_CODE_OBJ_EVT_AUTO_FOCUS, R.string.mm_obj_evt_auto_focus);
		tempMap.put(MENU_CODE_OBJ_COUNT_FROM_START, R.string.mm_obj_count_from_start);
		
		//overrides
		tempMap.put(MENU_CODE_OBJECTIVES, R.string.mm_edit_objectives);
		tempMap.put(MENU_CODE_SAVE, R.string.mm_save_map);
		tempMap.put(MENU_CODE_LOAD, R.string.mm_load_map);
		
		TITLE_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> ARRAY_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_OBJ_PLAYER, R.array.objective_player);
		tempMap.put(MENU_CODE_OBJ_EVT_STARTER_ACTION, R.array.starter_event_actions);
		tempMap.put(MENU_CODE_OBJ_EVT_FULFILL_MODE, R.array.command_event_fulfill_mode);
		tempMap.put(MENU_CODE_OBJ_RESOURCE_TYPE, R.array.resource_type);
		tempMap.put(MENU_CODE_OBJ_STAT_TYPE, R.array.statistics);
		tempMap.put(MENU_CODE_MAP_TYPE, R.array.map_types);
		tempMap.put(MENU_CODE_MAP_WIDTH, R.array.map_size);
		tempMap.put(MENU_CODE_MAP_HEIGHT, R.array.map_size);
		ARRAY_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> SURE_DIALOG_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_EXIT_EDITOR, R.string.sure_dialog_exit_editor);
		SURE_DIALOG_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static EditorMenu instance;
	
	private EditorMenu(Context context){
		super(context, "editor");
	}
	
	public static EditorMenu getInstance(Context context){
		if(instance==null) instance = new EditorMenu(context);
		return instance;
	}

	@Override
	public Integer getTitleResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		Integer resid = TITLE_RESID.get(code);
		if(resid!=null) return resid;
		
		return super.getTitleResidByCode(code);
	}
	
	@Override
	public Integer getArrayResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		Integer resid = ARRAY_RESID.get(code);
		if(resid!=null) return resid;
		
		return super.getArrayResidByCode(code);
	}
	
	@Override
	public Integer getSureDialogResIdByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		Integer resid = SURE_DIALOG_RESID.get(code);
		if(resid!=null) return resid;
		
		return super.getSureDialogResIdByCode(code);
	}

}

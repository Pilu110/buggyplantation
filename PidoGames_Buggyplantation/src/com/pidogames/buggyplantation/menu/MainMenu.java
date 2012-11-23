package com.pidogames.buggyplantation.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.R.array;
import com.pidogames.buggyplantation.R.string;
import com.pidogames.buggyplantation.menu.DialogBox;

import android.content.Context;

public class MainMenu extends DialogBox {
	
	public static final String MENU_CODE_NEW = "NEW";
	public static final String MENU_CODE_RESTART = "RESTART";
	public static final String MENU_CODE_END = "END";
	public static final String MENU_CODE_MISSION = "MISSION";
	public static final String MENU_CODE_CUSTOM_MAP = "CUSTOM_MAP";
	public static final String MENU_CODE_START_SELECTED = "START_SELECTED";
	public static final String MENU_CODE_START_MISSION  = "START_MISSION";
	
	public static final String MENU_CODE_GAME = "GAME";
	public static final String MENU_CODE_SETTINGS = "SETTINGS";
	public static final String MENU_CODE_MAP_EDITOR = "MAP_EDITOR";
	
	public static final String MENU_CODE_VIDEO = "VIDEO";
	public static final String MENU_CODE_MM_SIZE = "MM_SIZE";
	public static final String MENU_CODE_MM_TRANSPARENCY = "MM_TRANSPARENCY";
	public static final String MENU_CODE_DISPLAY_DEBUG = "DISPLAY_DEBUG";
	public static final String MENU_CODE_DISPLAY_BUG_LIFE = "DISPLAY_BUG_LIFE";
	
	public static final String MENU_CODE_AUDIO = "AUDIO";
	public static final String MENU_CODE_GAME_SETTINGS = "GAME_SETTINGS";
	public static final String MENU_CODE_SOUND = "SOUND";
	public static final String MENU_CODE_MUSIC = "MUSIC";
	public static final String MENU_CODE_BRIGHTNESS = "BRIGHTNESS";
		
	private static final Map<String, Integer> ARRAY_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_MM_SIZE, R.array.minimap);
		ARRAY_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> TITLE_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_NEW, R.string.mm_new);
		tempMap.put(MENU_CODE_RESTART, R.string.mm_restart);
		tempMap.put(MENU_CODE_END, R.string.mm_end);
		tempMap.put(MENU_CODE_MISSION, R.string.mm_mission);
		tempMap.put(MENU_CODE_CUSTOM_MAP, R.string.mm_custom_map);
		tempMap.put(MENU_CODE_START_SELECTED, R.string.mm_start_selected);
		tempMap.put(MENU_CODE_START_MISSION, R.string.mm_start_selected);
		tempMap.put(MENU_CODE_GAME, R.string.mm_game);
		tempMap.put(MENU_CODE_MAP_EDITOR, R.string.mm_map_editor);
		tempMap.put(MENU_CODE_SETTINGS, R.string.mm_settings);
		tempMap.put(MENU_CODE_VIDEO, R.string.mm_video);
		tempMap.put(MENU_CODE_MM_SIZE, R.string.mm_minimap_size);
		tempMap.put(MENU_CODE_MM_TRANSPARENCY, R.string.mm_minimap_transparency);
		tempMap.put(MENU_CODE_DISPLAY_DEBUG, R.string.mm_display_debug);
		tempMap.put(MENU_CODE_DISPLAY_BUG_LIFE, R.string.mm_display_bug_life);
		tempMap.put(MENU_CODE_AUDIO, R.string.mm_audio);
		tempMap.put(MENU_CODE_SOUND, R.string.mm_sound);
		tempMap.put(MENU_CODE_MUSIC, R.string.mm_music);
		tempMap.put(MENU_CODE_BRIGHTNESS, R.string.mm_brightness);
		TITLE_RESID = Collections.unmodifiableMap(tempMap);
	}

	private static MainMenu instance;
	
	private MainMenu(Context context){
		super(context, "main");
	}
	
	protected MainMenu(Context context, String file_name){
		super(context, file_name);
	}
	
	public static MainMenu getInstance(Context context){
		if(instance==null) instance = new MainMenu(context);
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

}

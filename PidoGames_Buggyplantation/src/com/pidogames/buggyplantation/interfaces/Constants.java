package com.pidogames.buggyplantation.interfaces;

public interface Constants {
	public static final boolean DEBUG = true;
	
	public static final boolean QUICK_START = true;
	
	//how many times will take to draw one frame
	public static final long MINIMUM_FRAME_MILIS = 1000/25;
	
	public static final int TITLE_HEIGHT = 0; //25;
	
	public static final int AUTOSCROLL_SPEED  = 50;
	public static final double AUTOZOOM_SPEED = 0.2;
	
	public static final int MINIMAP_MARGIN = 5;
	public static final int TOP_FILTER_MARGIN = 5;

	public static final int GAME_MODE_MAP_EDITOR = 0;
	public static final int GAME_MODE_MISSION    = 1;
	public static final int GAME_MODE_TEST_MAP   = 2;
	
	public static final int BUG_SMASH_RANGE  = 30;
	public static final int BUG_SMASH_DAMAGE = 100;
	
	public static final int ITEM_SELECTOR_CENTER = 25;
	public static final int ITEM_SELECTOR_RADIUS = 50;
	
	public static final int ITEM_SELECTOR_WIDTH         = 15;
	public static final int ITEM_SELECTOR_INNER_WIDTH   = 10;
	public static final int ITEM_SELECTOR_INNER_PADDING = 5;
	
	public static final int MAX_SCROLL_PATH_X = 3;
	public static final int MAX_SCROLL_PATH_Y = 3;
	
	public static final int LEVEL_FRAME = 5;
	
	public static final int MAP_LEVELS   = 3;
	
	public static final int NO_LEVEL	 = -1;
	public static final int GROUND_LEVEL = 0;
	public static final int STALK_LEVEL  = 1;
	public static final int LEAF_LEVEL   = 2;
	public static final int BUG_LEVEL    = 3;
	public static final int AIR_LEVEL    = 4;
	
	public static final int NONE  = -1;
	
	public static final int UP    = 0;
	public static final int RIGHT = 1;
	public static final int DOWN  = 2;
	public static final int LEFT  = 3;	
	public static final int OVER  = 4;	
	public static final int UNDER = 5;	

	public static final int B_E	= 0;
	public static final int N4_U = 1;
	public static final int N4_R = 2;
	public static final int N4_D = 4;
	public static final int N4_L = 8;
	
	public static final int N4_I = 16; //inverse block
	
	public static final int N8_UL = 128;
	public static final int N8_U  = 64;
	public static final int N8_UR = 32;
	public static final int N8_L  = 16;
	public static final int N8_R  = 8;
	public static final int N8_DL = 4;
	public static final int N8_D  = 2;
	public static final int N8_DR = 1;
	
	public static final String PREF_KEY_STARTED_SCENE_PREFIX = "STSC_";
	public static final String PREF_KEY_STARTED_MISSION_PREFIX = "STMS_";
	public static final String PREF_KEY_MISSION_WON_PREFIX   = "MSWN_";
	
	public static final String PREF_KEY_SOUND_VOLUME = "SV";
	public static final String PREF_KEY_MUSIC_VOLUME = "MV";
	public static final String PREF_KEY_BRIGHTNESS   = "BR";
	
	public static final int MAX_BUGS_LIMT = 200;
	
}

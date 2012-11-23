package com.pidogames.buggyplantation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.effect.LightingEffect;
import com.pidogames.buggyplantation.entity.BackgroundPattern;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.Nutrient;
import com.pidogames.buggyplantation.entity.block.Bait;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.Core;
import com.pidogames.buggyplantation.entity.block.FlyTrap;
import com.pidogames.buggyplantation.entity.block.Gall;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.entity.block.ShooterBlock;
import com.pidogames.buggyplantation.entity.block.WaterBlock;
import com.pidogames.buggyplantation.entity.item.Bouncer;
import com.pidogames.buggyplantation.entity.item.Dandelion;
import com.pidogames.buggyplantation.entity.item.DisplayedTypeItem;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Nest;
import com.pidogames.buggyplantation.entity.item.Seabean;
import com.pidogames.buggyplantation.entity.item.Seed;
import com.pidogames.buggyplantation.entity.item.Shrapnel;
import com.pidogames.buggyplantation.entity.item.WaterItem;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.AmountSlider;
import com.pidogames.buggyplantation.interfaces.Constants;
import com.pidogames.buggyplantation.interfaces.CountSlider;
import com.pidogames.buggyplantation.interfaces.DialogBoxListener;
import com.pidogames.buggyplantation.interfaces.EditTextListener;
import com.pidogames.buggyplantation.interfaces.GroupSelect;
import com.pidogames.buggyplantation.interfaces.MenuListener;
import com.pidogames.buggyplantation.interfaces.OnSetTextListener;
import com.pidogames.buggyplantation.interfaces.SceneListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.menu.MainMenu;
import com.pidogames.buggyplantation.menu.TestMapMenu;
import com.pidogames.buggyplantation.objective.AreaObjective;
import com.pidogames.buggyplantation.objective.DelayObjective;
import com.pidogames.buggyplantation.objective.ExistAreaObjective;
import com.pidogames.buggyplantation.objective.MergeObjective;
import com.pidogames.buggyplantation.objective.Objective;
import com.pidogames.buggyplantation.objective.ResourceObjective;
import com.pidogames.buggyplantation.objective.StatisticsObjective;
import com.pidogames.buggyplantation.objective.event.AppearEvent;
import com.pidogames.buggyplantation.objective.event.AutoScrollEvent;
import com.pidogames.buggyplantation.objective.event.CinematicEvent;
import com.pidogames.buggyplantation.objective.event.CommandMoveEvent;
import com.pidogames.buggyplantation.objective.event.DelayEvent;
import com.pidogames.buggyplantation.objective.event.MenuItemEvent;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;
import com.pidogames.buggyplantation.objective.event.PopupTextEvent;
import com.pidogames.buggyplantation.objective.event.StarterEvent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class SceneView extends SurfaceView implements Constants, SurfaceHolder.Callback {
	
	public static final int COMMAND_NONE   = 0;
	public static final int COMMAND_SELECT = 1;
	public static final int COMMAND_MOVE   = 2;
	public static final int COMMAND_ATTACK = 3;
	
	private static final int MAX_SAVED_GROUPS = 5;
	
	private SceneThread thread;
	private static SceneView instance;
	public static float scale;
	public static float brightness;
	private StartActivity activity;
	
	private static Random rnd = new Random(System.currentTimeMillis());
	
	public static SceneView getInstance(StartActivity activity){
		if(instance == null && activity!=null) new SceneView(activity);
		else if(activity!=null) instance.setActivity(activity);
		return instance;
	}
	
	public void clearInstance(){
		instance=null;
		thread.setRunning(false);
	}
	
	public void setActivity(StartActivity activity){
		this.activity = activity;
	}
	
	public SharedPreferences getPreferences(){
		return activity.getPreferences(Context.MODE_PRIVATE);
	}
	
	private SceneView(StartActivity activity) {
		super(activity.getBaseContext());
		instance = this;
		this.activity = activity;
		scale = getContext().getResources().getDisplayMetrics().density;
		/*
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        Log.d("SCREEN INIT","WIDTH:"+width+", HEIGHT:"+height+", SCALE:"+scale);
        */
        
		getHolder().addCallback(this);
		thread = new SceneThread(getHolder(),this);		
	}
	
	public SceneThread getThread(){return thread;}
	public static Scene getScene(){
		return instance.
		thread.
		scene;
	}
	
	public static String getString(int resid){
		return instance.getResources().getString(resid);
	}
	
	public static String [] getStringArray(int resid){
		return instance.getResources().getStringArray(resid);
	}
	
	public static int getCanvasWidth(){
		return instance.thread.canvas_width;
	}
	
	public static int getCanvasHeight(){
		return instance.thread.canvas_height;		
	}
	
	public static boolean hasMessages(){
		return !instance.thread.messages.isEmpty();
	}
		
	public static long getTic(){return instance.thread.tic; }
	
	public static int getSX(){return instance.thread.scroll_x;}
	public static int getSY(){return instance.thread.scroll_y;}
	public static double getZoom(){return instance.thread.zoom;}
	
	public static int getPlayer(){
		return instance.thread.player;
	}
	
	public void onChangeBlock(int level, int x, int y){
		thread.onChangeBlock(level, x, y);
	}
		
	public static int getMiniMapMultiplier(){
		return 1;
	}
	
	public static int getGameMode(){
		return instance.thread.game_mode;
	}
	
	public static DialogBox getMenuInstance(){
		return instance.thread.main_menu;
	}
	
	public static String getLanguage(){
		return "en";
	}
	
	public static String getDefaultLanguage(){
		return "en";
	}
	
	class SceneThread extends Thread implements MenuListener, DialogBoxListener, SceneListener {
		
		private static final String JSON_SCROLL_X = "x";
		private static final String JSON_SCROLL_Y = "y";
		private static final String JSON_ZOOM  = "z";
		private static final String JSON_SCENE = "s";
		private static final String JSON_TIC   = "t";
		private static final String JSON_RESIDS = "r";
		//private static final String JSON_GAME_MODE = "gm";
		
		private static final String JSON_SELECTED_ITEM_LIST = "sil";
		private static final String JSON_SAVED_GROUPS = "sg";
		
		private MusicManager music;
		private SoundManager sound;
		private FileManager file;
		
		private Scene scene;
		private Block soil;
		private Block searingIcon;
		private Block [] arrow;
		
		private BackgroundPattern bg_pattern;
		private Nutrient [] nutrient_icon;
		
		private Paint lifeStatBgPaint;
		private Paint lifeStatPaint;
		private Paint buildStatBgPaint;
		private Paint buildStatPaint;
		private Paint reachMapReachPaint;
		private Paint reachMapBlockedPaint;
		private Paint crosshairPaint;
		
		private DialogBox main_menu;
		private int active_level;
		
		private SurfaceHolder holder;
		private SceneView view;
		private double zoom;
		private boolean zoom_changed;
		private int scroll_x;
		private int scroll_y;
		private boolean run;
		private boolean started;
		private boolean shut_down;
		private boolean clear_scene;
		
		private int canvas_width;
		private int canvas_height;
		
		private int player;
		private int game_mode;
		private int command_mode;
		private Coord command_cursor;
		private Rect selection_rect;
		
		private LinkedList<Message> messages;
		private boolean cancel_next_tap;
		
		private ArrayList<HashSet<Item>> saved_groups;
		private HashSet<Item> selected_item_list;
		private Item selected_item;
		private int[][][] reach_grid;
		private boolean place_item;
		private boolean rotate_item;
		
		private ObjectiveEvent event;
		private AreaObjective obj_area;
		private GroupSelect   group_select;
		private ObjectiveEvent dialog_event;
		
		private Entity mb;
		private int mx, my;
		private MenuItem mi;
		private boolean is_menu_visible;
				
		private boolean is_placer_visible;
		private int is_arrow_visible;
		private int place_type;
		private int place_level;
		private boolean placer_is_plant;
		private boolean placer_is_builded;
		
		private boolean display_debug;
		private boolean display_bug_life;
		private int minimap_size;
		private boolean minimap_transparency;
		
		//private ArrayList<Coord> debug_tap_coords;
		private long tic;
		private long svsound_played_at;
	
		private long last_save_at;
		private int saves_in_last_minute;
		
		private String  selected_filename;
		private String  selected_file_title;
		private String  selected_description;
		private String  selected_mission_starter;
		private boolean selected_is_asset;
		private boolean selected_is_mission;
		
		private long final_stat_appeared_at;
		private int final_stat_group;
		private boolean final_stat_visible;
		private boolean final_stat_is_won;
		
		private JSONObject test_map;
		
		private Cinematic cinematic;
		
		private FlowerField flower_field;
		
		private boolean autoPlaceBlockMode;
		private int lastSelectedMenuItemResId;
		private Paint autoPlacePaint;
		private RectF autoPlaceRect;
		
		private boolean loading;
		private boolean saveing;
		private String FILE_SEMAFOR = "FILE_SEMAFOR";
		
		private boolean map_size_changed;
		private int new_map_width;
		private int new_map_height;
		
		SceneThread(SurfaceHolder holder, SceneView view){
			view.thread = this;
			
			shut_down = false;
			clear_scene = false;
			
			autoPlaceBlockMode = false;
			autoPlacePaint = new Paint();
			autoPlacePaint.setTextSize(18*scale);
			autoPlacePaint.setTypeface(Typeface.MONOSPACE);
			autoPlacePaint.setTextAlign(Align.CENTER);
			autoPlaceRect = new RectF();
			
			tic = 0;
			svsound_played_at = 0;
			
			player = Entity.PLAYER_PLANT;
			game_mode = GAME_MODE_MISSION;
			command_mode = COMMAND_NONE;
			command_cursor = new Coord();
			
			flower_field = new FlowerField();
			
			last_save_at = System.currentTimeMillis();
			saves_in_last_minute = 1;
			
			soil = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_soil,0,0,GROUND_LEVEL,true);			
			//crosshair = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_crosshair,0,0,AIR_LEVEL,true);
			bg_pattern = new BackgroundPattern();

			nutrient_icon = new Nutrient[Block.NUTRIENTS];
			for(int i=0; i<Block.NUTRIENTS; i++){
				nutrient_icon[i] = new Nutrient(Block.NUTRIENT_ICON_RESID[i]);
			}
			
			arrow = new Block[4];
			arrow[UP] = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_arrow_up,0,0,AIR_LEVEL,true);
			arrow[RIGHT] = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_arrow_right,0,0,AIR_LEVEL,true);
			arrow[DOWN] = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_arrow_down,0,0,AIR_LEVEL,true);
			arrow[LEFT] = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_arrow_left,0,0,AIR_LEVEL,true);
			
			searingIcon = new Block(Entity.PLAYER_NEUTRAL,R.drawable.menu_delete,0,0,AIR_LEVEL,true);

			main_menu = getCorrectMenu();
			/*
			int state = getListState(main_menu, EditorMenu.MENU_CODE_MAP_WIDTH);
			int map_width = 8 << state;
			state = getListState(main_menu, EditorMenu.MENU_CODE_MAP_HEIGHT);
			int map_height = 8 << state;
			int map_type = getListState(main_menu, EditorMenu.MENU_CODE_MAP_TYPE);
			*/
			
			//scene = new Scene(this, MAP_LEVELS, 16, 16, Scene.MAP_TYPE_SOIL, null, null); 
			scene = null;
			lifeStatBgPaint = new Paint();
			lifeStatPaint = new Paint();
			lifeStatBgPaint.setARGB(255, 128, 0, 0);
			lifeStatPaint.setARGB(255, 0, 128, 0);
			
			buildStatBgPaint = new Paint();
			buildStatBgPaint.setColor(Block.getNutrientBgColor(Block.SUGAR));
			buildStatPaint = new Paint();
			buildStatPaint.setColor(Block.getNutrientColor(Block.SUGAR));
			
			reachMapReachPaint = new Paint();
			reachMapReachPaint.setColor(0x40008000);
			reachMapBlockedPaint = new Paint();
			reachMapBlockedPaint.setColor(0x40800000);
			
			crosshairPaint = new Paint();
			crosshairPaint.setColor(0xff008000);
			crosshairPaint.setStyle(Paint.Style.STROKE);
			crosshairPaint.setTextAlign(Align.CENTER);
			
			Context context = SceneView.this.getContext();
			
			main_menu.setMainMenuListener(this);
			
			obj_area = null;
			group_select = null;
			dialog_event = null;
			
			saved_groups = new ArrayList<HashSet<Item>>(MAX_SAVED_GROUPS);
			selected_item_list = null;
			selected_item = null;
			place_item = false;
			rotate_item = false;
			
			//debug_tap_coords = new ArrayList<Coord>();
			is_menu_visible = false;
			is_placer_visible = false;
			
			display_debug    = getCheckboxState(main_menu, MainMenu.MENU_CODE_DISPLAY_DEBUG);
			display_bug_life = getCheckboxState(main_menu, MainMenu.MENU_CODE_DISPLAY_BUG_LIFE);
			
			int state = getListState(main_menu, MainMenu.MENU_CODE_MM_SIZE);
			if(state == 5) state = 8;
			else if(state == 6) state = 12;
			minimap_size = state;
			
			SharedPreferences pref = getPreferences();
			//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
			brightness = pref.getFloat(PREF_KEY_BRIGHTNESS, 0.5f);
			Entity.setBrightness(brightness);
			
			//hack to start mission level 1
			pref.edit().putBoolean(PREF_KEY_STARTED_SCENE_PREFIX+"mission1/m1s1", true).commit();
			//end of hack
			
			minimap_transparency = getCheckboxState(main_menu, MainMenu.MENU_CODE_MM_TRANSPARENCY);
			//scene.load(SceneView.this.getContext(), 0);
			setZoom(1.0);
			
			sound = SoundManager.getInstance(context);
			music = MusicManager.getInstance(context);
			if(!music.isInitialized()) music.init();
			
			file = FileManager.getInstance(context);
			
			messages = new LinkedList<Message>();
			cancel_next_tap = false;
			
			final_stat_appeared_at = -1;
			final_stat_visible = false;
			
			loading = false;
			saveing = false;
			
			//showFinalStat();
			displayMainMenu();
			
			init(holder,view);
		}
		
		private DialogBox getCorrectMenu(){
			switch(game_mode){
				case GAME_MODE_MAP_EDITOR:	return EditorMenu.getInstance(getContext());
				case GAME_MODE_TEST_MAP:	return TestMapMenu.getInstance(getContext());
				default:					return MainMenu.getInstance(getContext());
			}
		}
		
		public boolean restartTestMap(){
			try {
				loadScene(test_map,test_map.optString(DialogBox.JSON_DESCRIPTION,null), false, false);
				setGameMode(GAME_MODE_TEST_MAP);
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		public boolean startTestMap(){
			try {
				test_map = this.toJSON();
				test_map.put(DialogBox.JSON_DESCRIPTION, scene.getDescription());
				setGameMode(GAME_MODE_TEST_MAP);
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
				test_map = null;
			}
			return false;
		}
		
		public boolean endTestMap(){
			try {
				setGameMode(GAME_MODE_MAP_EDITOR);
				loadScene(test_map,test_map.optString(DialogBox.JSON_DESCRIPTION,null), false, false);
				test_map = null;
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		public FileManager getFileManager(){
			return file;
		}
		
		public void init(SurfaceHolder holder, SceneView view){
			this.holder = holder;
			this.view = view;
			music.play();
		}
		
		public void shutDown(){
			shut_down = true;
			setRunning(false);
		}
		
		public void setRunning(boolean run) {
			if(run) started=true;
	        this.run = run;
	    }
		
		public boolean isStarted(){
			return started;
		}
		
		public Scene getScene(){
			return scene;
		}
		
		public int getActiveLevel(){
			return active_level;
		}
		
		public ObjectiveEvent getDialogEvent(){
			return dialog_event;
		}
		
		public GroupSelect getGroupSelect(){
			return group_select;
		}
		
		public boolean [] getEnabledLevels(){
			boolean [] el = new boolean[scene.getLevels()];
			for(int level=0; level<scene.getLevels(); level++){
				if(scene.getBlock(level, mx, my)!=null) el[level]=true;
				else el[level]=false;
			}
			return el;
		}
		
		@Override
		public void setEvent(ObjectiveEvent event){
			if(this.event!=null) this.event.fulfill(); //avoid event freeze
			this.event = event;
		}
		
		public double getZoom() {return zoom; }
		
		public void setZoom(double zoom, Coord center){
			if(main_menu.isVisible()) return;
			
			if(zoom<0.25) zoom=0.25;
			else if (zoom>2.0) zoom=2.0;
			
			// ZOOM correction to prevent position differences between objects and tile board
			zoom = (int)(Block.getAbsoluteWidth()*zoom) / (double)Block.getAbsoluteWidth();
			
			double ax = (center.x + scroll_x)/this.zoom;
			double ay = (center.y + scroll_y)/this.zoom;
			double dx = ax*this.zoom - ax*zoom;
			double dy = ay*this.zoom - ay*zoom;
			scroll_x -= dx; 
			scroll_y -= dy; 
			setZoom(zoom);
		}
		
		public void setZoom(double zoom){
			if(zoom!=this.zoom){
				if(zoom<0.25) zoom=0.25;
				else if (zoom>2.0) zoom=2.0;
				
				this.zoom = zoom;
				zoom_changed = true;
			}
		}
				
		public int getScrollX(){return scroll_x;}
		public int getScrollY(){return scroll_y;}
		
		public void setScrollX(int scroll_x){
			this.scroll_x=scroll_x;
		}
		
		public void setScrollY(int scroll_y){
			this.scroll_y=scroll_y;
		}
		
		public void setScroll(Coord c){
			this.scroll_x = c.x;
			this.scroll_y = c.y;
		}
		
		public void focusToCoord(Coord c){
			scroll_x = (int)(c.x*zoom) - canvas_width/2;
			scroll_y = (int)(c.y*zoom) - canvas_height/2;
		}
		
		public Coord getScreenCenter(){
	        //int c_width  = SceneView.getCanvasWidth();
	        //int c_height = SceneView.getCanvasHeight();
			//return new Coord(c_width/2, c_height/2);
			return new Coord(canvas_width/2, canvas_height/2);
		}
		
		public Coord getAbsoluteCenter(){
			return getAbsoluteCenter(scroll_x, scroll_y);
		}
		
		public Coord getAbsoluteCenter(int scroll_x, int scroll_y){
	        //int c_width  = SceneView.getCanvasWidth();
	        //int c_height = SceneView.getCanvasHeight();
			//return new Coord((int)((scroll_x+c_width/2)/zoom), (int)((scroll_y+c_height/2)/zoom));
			return new Coord((int)((scroll_x+canvas_width/2)/zoom), (int)((scroll_y+canvas_height/2)/zoom));
		}
		
		public boolean isReachGridVisible(){
			return reach_grid!=null;
		}
		
		public void cancelReachGrid(){
			reach_grid = null;
		}
		
		public void setReachGridDestination(int bx, int by){
			int [][][] rg = reach_grid;
			if(rg!=null) {
				if(rg[0][bx][by]!=Integer.MAX_VALUE){
					reach_grid = null;
					Entity b = mb;
					if(b!=null) {
						if(b instanceof Dandelion){
							((Dandelion)b).setDestination(scene.getBlock(GROUND_LEVEL,bx,by));
						}
						else if(b instanceof Core){
							((Core)b).setDestination(scene.getBlock(GROUND_LEVEL,bx,by));							
						}
					}
				}
			}
		}
		
		public boolean attack(boolean only_enemy, int x, int y){
			int ox = (scroll_x+x)/Block.getWidth();
			int oy = (scroll_y+y)/Block.getHeight();
			Map<Item,Boolean> dest_items = scene.getItemsAt(ox, oy);
			HashSet<Item> items = selected_item_list;
			Item sel_item = items!=null && !items.isEmpty() ? items.iterator().next() : null;
			Entity destination = null;
			boolean attack = false;
			
			Item s_ally  = null;
			Item s_ally2 = null;
			if(dest_items!=null && sel_item!=null){
				Item s_enemy = null;
				Item s_neutral = null;
				Item s_enemy2 = null;
				Item s_neutral2 = null;
				
				int tx = scroll_x + x;
				int ty = scroll_y + y;
				for(Item item : dest_items.keySet()){
					if(item.getHitPoints()>0){
						int align = sel_item.getAlignment(item);
						if(align==Entity.ALIGN_ENEMY) {
							if(item.isCollision(tx, ty)) s_enemy = item;
							else s_enemy2 = item;
						}
						else if(align == Entity.ALIGN_NEUTRAL){										
							if(item.isCollision(tx, ty)) s_neutral = item;
							else s_neutral2 = item;
						}
						else {
							if(item.isCollision(tx, ty)) s_ally = item;
							else s_ally2 = item;
						}
					}
				}
				
				if(s_enemy!=null) destination = s_enemy;
				else if (s_enemy2!=null) destination = s_enemy2;
				else if (!only_enemy && s_neutral!=null)  destination = s_neutral;
				else if (!only_enemy && s_neutral2!=null) destination = s_neutral2;
				else if (!only_enemy && s_ally!=null)     destination = s_ally;
			}
			
			if(sel_item!=null) {
				Block b = scene.getHigherBlock(ox, oy);
				if(destination==null || (sel_item.getAlignment(destination)!=Entity.ALIGN_ENEMY && sel_item.getAlignment(b)==Entity.ALIGN_ENEMY)){
					if(b.getFocusObject(sel_item,true)!=Entity.FOCUS_NONE) {
						if(!only_enemy || sel_item.getAlignment(b)==Entity.ALIGN_ENEMY) destination = b;
					}
				}
				
				if(destination!=null){
					if(items!=null){
						int [][][] dm = null;
						int march_index = 0;
						int s = items.size();
						for(Item item: items){
							if(item instanceof Bug && item.isSelectable()){
								Bug bug = (Bug)item;
								dm = scene.getDistanceMapFrom(item, 0, dm);
								bug.setDestination(destination, false, Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), march_index, true, false, dm);
								march_index++;
								if(s<6)			while(march_index%8!=5 && march_index%8!=6) march_index++;
								else if (s<11)	while(march_index%4!=1 && march_index%4!=2) march_index++;
								attack = true;
							}
						}
					}
					
					if(attack) {
						destination.addEffect(new LightingEffect(5, 20));
						return true;
					}
				}
			}
			
			if(only_enemy) {
				if(s_ally!=null) {
					selectItem(s_ally);
					return true;
				}
				else if(s_ally2!=null) {
					selectItem(s_ally2);
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public void cancelNextTap(){
			cancel_next_tap = true;
		}
		
		public void showFinalStat(boolean is_won){
			final_stat_group = Scene.STAT_GROUP_BUILDINGS;
			final_stat_appeared_at = -1;
			final_stat_visible = true;
			final_stat_is_won = is_won;
		}
		
		public void onTapXY(int x, int y){
			
			y -= (TITLE_HEIGHT*scale);
			
			if(cancel_next_tap){
				cancel_next_tap = false;
				return;
			}
			
			if(main_menu.isVisible() || main_menu.getAnimPercent()>0){
				main_menu.onTapXY(x, y, false);
				return;
			}
			
			if(cinematic!=null){
				cinematic.moveToNext();
				return;
			}
			
			Scene scene = this.scene;
			if(scene==null) return;
			
			if(!messages.isEmpty()){
				Message m = messages.peek();
				if(m.onTap()) {
					return;
				}
			}
			
			if(final_stat_visible){
				final_stat_appeared_at = -1;
				RectF [] btnRect = scene.getStatBtnRect();
				for(int i=0; i<btnRect.length; i++){
					RectF r = btnRect[i];
					if(r.left<=x && x<=r.right && r.top<=y && y<=r.bottom){
						switch(i){
							case Scene.STAT_BTN_PREV:
								if(final_stat_group>0){
									final_stat_group--;
								}
								else {
									final_stat_group = Scene.STAT_GROUP_COUNT-1;									
								}
							break;
							case Scene.STAT_BTN_NEXT:
								final_stat_group = (final_stat_group+1) % Scene.STAT_GROUP_COUNT;
							break;
							case Scene.STAT_BTN_OK:
								final_stat_visible = false;
								scene.closeFinalStatistics();
								restartScene(final_stat_is_won);
								//file.load(getSaveDir(), selected_filename);
							break;
						}
					}
				}
				return;
			}			
			
			//debug_tap_coords.add(new Coord(x,y));
			boolean processed = false;
			int ox = (scroll_x+x)/Block.getWidth();
			int oy = (scroll_y+y)/Block.getHeight();
			
			if(minimap_size>0
			&&  x >= MINIMAP_MARGIN*scale 
			&&  x <= (MINIMAP_MARGIN+scene.getWidth()*minimap_size)*scale
			&&  y >= MINIMAP_MARGIN*scale 
			&&  y <= (MINIMAP_MARGIN+scene.getHeight()*minimap_size)*scale) {
		        int c_width  = SceneView.getCanvasWidth();
		        int c_height = SceneView.getCanvasHeight();
				int sx = (int)((x - MINIMAP_MARGIN*scale)/minimap_size/scale * Block.getAbsoluteWidth() * zoom) - c_width/2;
				int sy = (int)((y - MINIMAP_MARGIN*scale)/minimap_size/scale * Block.getAbsoluteHeight() * zoom) - c_height/2;
				setScroll(new Coord(sx,sy));
				return;
			}
			
	        if(game_mode == GAME_MODE_MAP_EDITOR && autoPlaceRect.contains(x, y)) {
	        	autoPlaceBlockMode = !autoPlaceBlockMode;
	        	return;
	        }
			
			//if(game_mode != GAME_MODE_BUG_SMASHER) {
					
				if(is_menu_visible && mb!=null) {
					int start_y = mb.getMenu().getMenuTopY();
					if(start_y>0 && start_y<y){
						mb.getMenu().onTapXY(x,y,this, false);
						processed = true;
					}
				}
				
				if(!processed && reach_grid!=null){
					if(ox>=0 && ox<scene.getWidth() && oy>=0 && oy<scene.getHeight()) {
						setReachGridDestination(ox,oy);
					}
					processed = true;
				}
				
				if(!processed && is_placer_visible){
					if(ox>=0 && ox<scene.getWidth() && oy>=0 && oy<scene.getHeight()){
						if(((is_arrow_visible & N4_U) != 0) && ox==mx && oy==my-1) {
							scene.placeBlock(player,place_level,mx,my,place_type,N4_U,placer_is_plant,placer_is_builded,mb,mi);
							hidePlacer();
							//displayMenu(ox,oy);
						}
						else if(((is_arrow_visible & N4_R) != 0) && ox==mx+1 && oy==my) {
							scene.placeBlock(player,place_level,mx,my,place_type,N4_R,placer_is_plant,placer_is_builded,mb,mi);
							hidePlacer();
							//displayMenu(ox,oy);
						}
						else if(((is_arrow_visible & N4_D) != 0) && ox==mx && oy==my+1) {
							scene.placeBlock(player,place_level,mx,my,place_type,N4_D,placer_is_plant,placer_is_builded,mb,mi);
							hidePlacer();
							//displayMenu(ox,oy);
						}
						else if(((is_arrow_visible & N4_L) != 0) && ox==mx-1 && oy==my) {
							scene.placeBlock(player,place_level,mx,my,place_type,N4_L,placer_is_plant,placer_is_builded,mb,mi);
							hidePlacer();
							//displayMenu(ox,oy);
						}
					}
					processed=true;
				}
				
				if(!processed && command_mode != COMMAND_NONE){
					command_cursor = new Coord((int)((x+scroll_x)/zoom), (int)((y+scroll_y)/zoom));
					selection_rect = new Rect(command_cursor.x,command_cursor.y,command_cursor.x,command_cursor.y);
					
					if(ox>=0 && ox<scene.getWidth() && oy>=0 && oy<scene.getHeight()){
						if(command_mode == COMMAND_MOVE) {
							if(!attack(true,x,y)){
								Entity destination = scene.getHigherBlock(ox, oy);
								HashSet<Item> items = selected_item_list;
								if(items!=null){
									int [][][] dm = null;
									int march_index = 0;
									int s = items.size();
									for(Item item: items){
										if(item instanceof Bug && item.isSelectable()){
											dm = scene.getDistanceMapFrom(item, 0, dm);
											((Bug)item).setDestination(destination, false, Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), march_index, false, false, dm);
											march_index++;
											if(s<6)			while(march_index%8!=5 && march_index%8!=6) march_index++;
											else if (s<11)	while(march_index%4!=1 && march_index%4!=2) march_index++;
										}
									}
								}
								
								Coord dp   = destination.getPosition();
								scene.addItem(new Bouncer(player,R.drawable.b_arrow_up,AIR_LEVEL, dp.x , dp.y+50, 0, 5, 1),false);
								scene.addItem(new Bouncer(player,R.drawable.b_arrow_up,AIR_LEVEL, dp.x-50 , dp.y, 90, 5, 1),false);
								scene.addItem(new Bouncer(player,R.drawable.b_arrow_up,AIR_LEVEL, dp.x , dp.y-50, 180, 5, 1),false);
								scene.addItem(new Bouncer(player,R.drawable.b_arrow_up,AIR_LEVEL, dp.x+50 , dp.y, 270, 5, 1),false);
							}
						}
						else if(command_mode == COMMAND_ATTACK) {
							attack(false, x, y);
						}
					}
					
					processed = true;
				}
				

				if(!processed){
					Item selected_item = this.selected_item;
					
					Coord c_pos = new Coord((int)x,(int)y);
					long min_d2=-1;
					if(selected_item!=null){
						double x1 = selected_item.getX();
						double y1 = selected_item.getY();
						Bitmap bm = selected_item.getBitmap(Entity.BITMAP_NORMAL);
						int w = bm.getWidth();
						int h = bm.getHeight();
						Coord i_pos = new Coord((int)((x1-w/2)*zoom)-scroll_x,(int)((y1-h/2)*zoom)-scroll_y);
						min_d2 = c_pos.d2(i_pos);
					}
					
					for(Entry<Integer, List<Item>> e: scene.getItems().entrySet()){
						for(Item i : e.getValue()){
							if(i.isSelectable()){
								double x1 = i.getX();
								double y1 = i.getY();
								Bitmap bm = i.getBitmap(Entity.BITMAP_NORMAL);
								int w = bm.getWidth();
								int h = bm.getHeight();
								if((int)((x1-w/2)*zoom)-scroll_x <= x && (int)((y1-h/2)*zoom)-scroll_y <= y && x <= (int)((x1+w/2)*zoom)-scroll_x && y <= (int)((y1+h/2)*zoom)-scroll_y){
									if(selected_item==null) {
										selected_item = i;
										Coord i_pos = new Coord((int)((x1-w/2)*zoom)-scroll_x,(int)((y1-h/2)*zoom)-scroll_y);
										min_d2 = c_pos.d2(i_pos);
									}
									else {
										Coord i_pos = new Coord((int)((x1-w/2)*zoom)-scroll_x, (int)((y1-h/2)*zoom)-scroll_y);
										long d2 = c_pos.d2(i_pos);
										if(d2<=min_d2){
											selected_item = i;
											min_d2 = d2;
										}
									}
								}
							}
						}
					}
					
					if(selected_item!=null) {
						Item i = selected_item;
						Coord i_pos = new Coord((int)(i.getX()*zoom)-scroll_x,(int)(i.getY()*zoom)-scroll_y);
						
						long d2 = i_pos.d2(c_pos);
						double z2s2 = zoom*scale;
						z2s2 *= z2s2;
						
						if (d2<ITEM_SELECTOR_RADIUS*ITEM_SELECTOR_RADIUS*z2s2) {
							if(selected_item.getPlayer()==player){
								selectItem(selected_item);
								return;
							}
							else {
								Block b = scene.getHigherBlock(ox, oy);
								if(b==null || b.getPlayer()!=player){
									selectItem(selected_item);									
									return;
								}
								else {
									//selectItem(null);
									clearItemSelection();
								}
							}
						}
						else {
							clearItemSelection();
						}
					}
					else {
						//selectItem(null);
						clearItemSelection();
					}
					
					if(ox>=0 && ox<scene.getWidth() && oy>=0 && oy<scene.getHeight()){
						displayMenu(ox,oy,false,true);
					}
				}
			//}
		}
		
		public void selectItem(Item i){
			
			selected_item = i;
			
			if(i!=null){
				HashSet<Item> g = new HashSet<Item>();
				g.add(i);
				if(Entity.getAlignment(player, i.getPlayer())==Entity.ALIGN_ALLY) {
					storeToSavedGroups(g);
					if(game_mode!=GAME_MODE_MAP_EDITOR) {
						selected_item_list = g;
						setCommandMode(COMMAND_MOVE);						
					}
				}
				selected_item_list = g;
			}
			else {
				this.selected_item_list = null;				
			}
			
			if(i!=null) displayMenu(i);
			
			if(group_select!=null && i!=null){
				group_select.toggleTarget(i);
			}
			
		}
		
		public void displayPlacer(int type, int level, boolean is_plant, boolean is_builded){
			place_type = type;
			place_level = level;
			this.placer_is_plant   = is_plant;
			this.placer_is_builded = is_builded;
			
			if(mb!=null && mb instanceof Block){
				is_arrow_visible = ((Block)mb).getAllowedDirections(type);				
			}
			else {
				is_arrow_visible = N4_U|N4_R|N4_D|N4_L;
			}
			
			is_placer_visible = true;
		}
		
		public void hidePlacer(){
			is_placer_visible = false;
		}
		
		@Override
		public void onMenuItemReady(MenuItem mi){			
			int icon_resid = mi.getIconResid();			
			Entity me = mi.getParent().getParent();
			if(me instanceof Block){
				Block mb = (Block)me;
				int mx = mb.getX();
				int my = mb.getY();
				
				//Log.w("READY", "ITEM: "+ResIdConverter.getNameForResid(icon_resid));
				if(mb!=null){
					switch(icon_resid){
						case R.drawable.menu_poison:
							((PlantBlock)mb).addPoison(PlantBlock.MAX_POISON);
							//scene.poisonBugs(Entity.PLAYER_PLANT, mx, my);
						break;
						
						case R.drawable.bug_wasp_eating1:
							if(mb instanceof Gall && mb.getHitPoints()>0) {
								//((Gall)mb).addNest(Bug.TYPE_WASP);
								Coord p = mb.getRandomPosition();
								Bug wasp = Bug.getInstance(player, Bug.TYPE_WASP, p.x, p.y, rnd.nextInt(360));
								scene.addItem(wasp, false);
							}
						break;
						
						case R.drawable.menu_heal:
							if(mb instanceof PlantBlock) ((PlantBlock)mb).heal(100, 500);
						break;
						
						case R.drawable.b_tendrill_sprout_thorn_d:
						case R.drawable.b_stalk2_ud:
							if(mb instanceof PlantBlock) ((PlantBlock)mb).setSpiky(true);
						break;
						
						case R.drawable.b_core:
							/*
							if(mb.getType()==R.drawable.b_flower) {
								mb.setType(R.drawable.b_core);
							}
							*/
							Block core = new Core(mb.getPlayer(), icon_resid, mb.getX(), mb.getY(), mb.getLevel(), true);
							scene.setBlock(mb.getLevel(), mb.getX(), mb.getY(), core);
						break;
						
						case R.drawable.i_dandelion:
							if(mb instanceof Core){
								Core c = (Core)mb;
								Coord s = c.getPosition();
								Seed seed = new Dandelion(c.getPlayer(), icon_resid, AIR_LEVEL, s.x, s.y);
								c.setSeed(seed);
							}
						break;
						
						case R.drawable.i_seabean:
							if(mb instanceof Core){
								Core c = (Core)mb;
								Coord s = c.getPosition();
								Seed seed = new Seabean(c.getPlayer(), icon_resid, LEAF_LEVEL, s.x + 10, s.y - 26);
								c.setSeed(seed);
							}
						break;
						
						case R.drawable.b_gall_ud:
							//if(mb.getType()==R.drawable.b_stalk_ud) mb.setType(R.drawable.b_gall_ud);
							//else if(mb.getType()==R.drawable.b_stalk_rl) mb.setType(R.drawable.b_gall_rl);
							
							int level = mb.getLevel();
							scene.setBlock(level, mb.getX(), mb.getY(), new Gall(mb));
						break;
						
						case R.drawable.menu_roots:
							scene.takeRoot(mx,my);
						break;
						
						case R.drawable.b_shooter4:
							if(mb instanceof ShooterBlock) {
								((ShooterBlock)mb).setMissiles(4);
							}
						break;
						
						case R.drawable.b_shooter5:
							if(mb instanceof ShooterBlock) {
								((ShooterBlock)mb).setMissiles(5);
							}
						break;
						
						case R.drawable.b_shooter6:
							if(mb instanceof ShooterBlock) {
								((ShooterBlock)mb).setMissiles(6);
							}
						break;
					}			
					mb.getMenu().setValid(false);
				}
			}
		}
		
		@Override
		public void onMenuItemSelected(MenuItem mi){
			
			this.mi = mi;
			int icon_resid = mi.getIconResid();
			int place_level = Block.getPlaceLevel(icon_resid);
			
			if(game_mode!=GAME_MODE_MAP_EDITOR){
				int [] price = mi.getPrice();
				int ret_n = mb.payPrice(price);
				if(ret_n!=-1){
					showMessage(new Message("Not enough "+view.getResources().getString(Block.NUTRIENT_TITLE_RESID[ret_n]),0,false,null));
					return;
				}
			}
			else {
				lastSelectedMenuItemResId = icon_resid;
			}
						
			boolean processed = false;
			switch(icon_resid){
				case R.drawable.menu_select_items: {
					setCommandMode(COMMAND_SELECT);
					processed = true;
					break;
				}
				
				case R.drawable.menu_attack: {
					setCommandMode(COMMAND_ATTACK);
					processed = true;
					break;
				}
			
				case R.drawable.menu_move: {
					setCommandMode(COMMAND_MOVE);
					processed = true;
					break;
				}
				
				case R.drawable.menu_create_group: {
					selectLastSavedGroup();
					processed = true;
					break;
				}
				case R.drawable.bug1_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug = Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_BUG, p.x, p.y, 0);
					scene.addItem(bug,false);
					selectItem(bug);
					processed = true;
					break;
				}
				
				case R.drawable.bug_ant_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_ANT_WORKER, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}
				
				case R.drawable.bug_ant_soldier_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_ANT_SOLDIER, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}							
				
				case R.drawable.bug_ant_winged_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_ANT_WINGED, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}							
				
				case R.drawable.bug_ant_queen_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_ANT_QUEEN, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}							
				
				case R.drawable.bug3_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_BUG, Bug.TYPE_SCARAB, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}
				
				case R.drawable.bug_wasp_moving1: {
					Coord p = mb.getPosition();
					p.randomize(50);
					Bug bug =  Bug.getInstance(Block.PLAYER_PLANT, Bug.TYPE_WASP, p.x, p.y, 0);
					scene.addItem(bug, false);
					selectItem(bug);
					processed = true;
					break;
				}
				
			}
		
			
			if(!processed){
				if(mb!=null && mb instanceof Item){
					Item item = (Item)mb;
					switch(icon_resid){
						case R.drawable.menu_minus: {
							if(!scene.putFirst(item)){
								int level = item.getLevel();
								if(level>0) item.setLevel(level-1);
							}
							break;
						}
						
						case R.drawable.menu_plus: {
							if(!scene.putLast(item)){
								int level = item.getLevel();
								if(level<BUG_LEVEL) item.setLevel(level+1);
							}
							break;
						}
						
						case R.drawable.menu_delete: {
							closeMenu();
							clearItemSelection();
							scene.removeItem(item);
							break;
						}
						
						/*
						case R.drawable.menu_move: {
							if(mb instanceof Dandelion) {
								reach_grid = ((Dandelion)mb).getDistanceMap();
							}
							break;
						}
						*/
					
					}
				}
				else {
					switch(icon_resid){
						case R.drawable.menu_delete: {
							if(mb != null){
								Block b = (Block)mb;
								scene.setBlock(b.getLevel(), b.getX(), b.getY(), null);
							}
							break;
						}
						
						case R.drawable.menu_sear: {
							if(mb != null && mb instanceof PlantBlock){
								((PlantBlock)mb).toggleSearing();
							}
							break;
						}
					
						case R.drawable.i_ant_hole1: 
						case R.drawable.i_bug_hole: 
						case R.drawable.i_scarab_lair: {
							scene.addItem(new Nest(Entity.PLAYER_BUG, icon_resid, GROUND_LEVEL, mx*Block.getAbsoluteWidth() + rnd.nextInt(Block.getAbsoluteWidth()),my*Block.getAbsoluteHeight() + rnd.nextInt(Block.getAbsoluteHeight())), false);
							break;
						}
						
						case R.drawable.bug_wasp_eating1: {
							if(mb!=null && mb instanceof Gall) {
								mi.getParent().setBuildingProcess(mi, tic);
							}
							break;
						}
						
						case R.drawable.i_water_mouth_a1: {
							if(mb != null && mb instanceof Block) {
								Block b = (Block)mb;
								if(b.getOverlapType()!=Block.NO_OVERLAP){
									int os = Block.getOverlapShapeByType(b.getOverlapType());
									WaterItem wi = new WaterItem(Entity.PLAYER_NEUTRAL, icon_resid, GROUND_LEVEL, mx*Block.getAbsoluteWidth()+Block.getAbsoluteWidth()/2,my*Block.getAbsoluteHeight()+Block.getAbsoluteHeight()/2);
									
									switch(os){
										case N4_U: wi.setAngle(180); break;
										case N4_R: wi.setAngle(270); break;
										case N4_L: wi.setAngle(90);break;
									}
									
									if(os==N4_U || os==N4_L || os==N4_D || os==N4_R) {
										scene.addItem(wi, false);
									}
								}
							}
							break;
						}
							
						case R.drawable.i_branch: 
						case R.drawable.i_branch2: 
						case R.drawable.i_branch3: 
						case R.drawable.i_dried_leaf: 
						case R.drawable.i_dried_leaf2: 
						case R.drawable.i_dried_leaf3: 
							scene.addItem(new Item(Entity.PLAYER_NEUTRAL, icon_resid, GROUND_LEVEL, mx*Block.getAbsoluteWidth() + rnd.nextInt(Block.getAbsoluteWidth()),my*Block.getAbsoluteHeight() + rnd.nextInt(Block.getAbsoluteHeight())), false);
						break;
							
						case R.drawable.i_leaf_chip1:
							scene.addItem(new DisplayedTypeItem(Entity.PLAYER_NEUTRAL, icon_resid, GROUND_LEVEL, mx*Block.getAbsoluteWidth() + rnd.nextInt(Block.getAbsoluteWidth()),my*Block.getAbsoluteHeight() + rnd.nextInt(Block.getAbsoluteHeight())), false);
						break;
						
						case R.drawable.i_ant_egg:
							scene.addItem(new Item(Entity.PLAYER_BUG, icon_resid, GROUND_LEVEL, mx*Block.getAbsoluteWidth() + rnd.nextInt(Block.getAbsoluteWidth()),my*Block.getAbsoluteHeight() + rnd.nextInt(Block.getAbsoluteHeight())), false);
						break;
						
						case R.drawable.i_seabean: 
						case R.drawable.i_dandelion: 
							if(mb!=null && mb instanceof Core) {
								mi.getParent().setBuildingProcess(mi, tic);
							}
						break;
						
						case R.drawable.menu_sprouting_seed:
							if(mb instanceof Core){
								Core c = (Core)mb;
								reach_grid = c.getDistanceMap();
							}
						break;
						
						case R.drawable.b_soil:
							mb = new Block(Block.PLAYER_NEUTRAL,R.drawable.b_soil, mx, my, GROUND_LEVEL, true);
							scene.setBlock(GROUND_LEVEL, mx, my, (Block)mb);
						break;
						
						case R.drawable.b_nitrogen: {
							Block b = ((Block)mb);
							b.setHitpoints(1000);
							b.setOverlapType(icon_resid);							
							break;							
						}
						
						case R.drawable.b_nitrogen2: {
							Block b = ((Block)mb);
							b.setHitpoints(500);
							b.setOverlapType(icon_resid);							
							break;							
						}
						
						case R.drawable.b_nitrogen3: {
							Block b = ((Block)mb);
							b.setHitpoints(250);
							b.setOverlapType(icon_resid);							
							break;
						}
						
						case R.drawable.b_high_soil: {
							Block b = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_soil,mx,my,place_level,true);
							b.setHigh(true);
							scene.setBlock(place_level,mx,my,b);							
							break;							
						}
						case R.drawable.b_high_sand: {
							Block b = new Block(Entity.PLAYER_NEUTRAL,R.drawable.b_sand,mx,my,place_level,true);
							b.setHigh(true);
							scene.setBlock(place_level,mx,my,b);							
							break;
						}
						case R.drawable.b_high_water_ocean_a1: {
							Block b = new WaterBlock(Entity.PLAYER_NEUTRAL,R.drawable.b_water_ocean_a1,mx,my,place_level,true);
							b.setHigh(true);
							scene.setBlock(place_level,mx,my,b);							
							break;
						}
						
						case R.drawable.b_sand:
						case R.drawable.b_grass:
						case R.drawable.b_rock:
						case R.drawable.b_mount:
							Block b = new Block(Entity.PLAYER_NEUTRAL, icon_resid,mx,my,place_level,true);
							scene.setBlock(place_level,mx,my,b);
						break;
						
						case R.drawable.b_mount_ramp_u:
							if(mb!=null){
								int ot = ((Block)mb).getOverlapType();
								if(ot != Block.NO_OVERLAP){
									switch(Block.getOverlapShapeByType(ot)){
										case N4_U: ot=R.drawable.b_mount_ramp_u; break;
										case N4_R: ot=R.drawable.b_mount_ramp_r; break;
										case N4_D: ot=R.drawable.b_mount_ramp_d; break;
										case N4_L: ot=R.drawable.b_mount_ramp_l; break;
									}
									((Block)mb).setOverlapType(ot);
								}
							}
						break;
						
						case R.drawable.b_water_ud_a1:
						case R.drawable.b_water_ul_a1:
						case R.drawable.b_water_ur_a1:
						case R.drawable.b_water_ocean_a1:
						case R.drawable.b_water_mouth_a1:
							b = new WaterBlock(Entity.PLAYER_NEUTRAL, icon_resid,mx,my,place_level,true);
							scene.setBlock(place_level,mx,my,b);
						break;
						
						case R.drawable.b_plant:
							b = new PlantBlock(Entity.PLAYER_NEUTRAL, icon_resid,mx,my,place_level,true);
							scene.setBlock(place_level,mx,my,b);
						break;
						
						case R.drawable.b_seed:
						case R.drawable.b_leaf:
						case R.drawable.b_flower:
							b = new PlantBlock(player, icon_resid,mx,my,place_level,false);
							scene.setBlock(place_level,mx,my,b);
							mi.getParent().setBuildingProcess(mi, b);
						break;
						
						case R.drawable.b_flytrap_closed:
							if(mb!=null && mb instanceof FlyTrap){
								((FlyTrap)mb).setClosed(!((FlyTrap)mb).isClosed());
							}
						break;
						
						case R.drawable.b_flytrap:
							if(mb!=null && mb instanceof FlyTrap){
								((FlyTrap)mb).setClosed(!((FlyTrap)mb).isClosed());
							}
							else {
								b = new FlyTrap(player, icon_resid,mx,my,place_level,false);
								scene.setBlock(place_level,mx,my,b);
								mi.getParent().setBuildingProcess(mi, b);
							}
						break;
						
						case R.drawable.b_flower2:
						case R.drawable.b_core_mine:
							b = new Bait(player, icon_resid,mx,my,place_level,false);
							scene.setBlock(place_level,mx,my,b);
							mi.getParent().setBuildingProcess(mi, b);
						break;
						
						case R.drawable.menu_cm_explode:
							Coord o = mb.getPosition();
							scene.setBlock(LEAF_LEVEL, mx, my, null);
							for(int i=0; i<7; i++){
								int a = (int)(i*360.0/7.0) + 20 + rnd.nextInt(10)-5;
								double sa = Math.sin(a/180.0*Math.PI);
								double ca = -Math.cos(a/180.0*Math.PI);
								int ox = (int)(25*sa);
								int oy = (int)(25*ca);
								scene.addItem(new Shrapnel(player, R.drawable.i_cm_chip1, AIR_LEVEL, o.x+ox, o.y+oy, a, 15, 1f, null, 10, true), false);
								a -= (20 + rnd.nextInt(10)-5);
								ox = (int)(17*sa);
								oy = (int)(17*ca);
								scene.addItem(new Shrapnel(player, R.drawable.i_cm_chip2, AIR_LEVEL, o.x+ox, o.y+oy, a, 10+rnd.nextInt(2), 1f, null, 10, true), false);
								ox = (int)(7*sa);
								oy = (int)(7*ca);
								scene.addItem(new Shrapnel(player, R.drawable.i_cm_chip3, AIR_LEVEL, o.x+ox, o.y+oy, a, 5+rnd.nextInt(2), 1f, null, 10, true), false);
							}
							scene.addItem(new Shrapnel(player, R.drawable.i_cm_chip4, AIR_LEVEL, o.x, o.y), false);
															
						break;
							
						case R.drawable.b_shooter:
							b = new ShooterBlock(player, icon_resid,mx,my,place_level,false);
							scene.setBlock(place_level,mx,my,b);
							mi.getParent().setBuildingProcess(mi, b);
						break;
						
						case R.drawable.b_stone:
							if(scene.getBlock(place_level, mx, my)==null){
								b = new Block(Entity.PLAYER_NEUTRAL, icon_resid,mx,my,place_level,true);
								scene.setBlock(place_level,mx,my,b);
							}
							else {
								displayPlacer(icon_resid, place_level, false, true);							
							}
						break;
						
						case R.drawable.b_tendrill_sprout_d: {
							displayPlacer(icon_resid, place_level, true, false);
							break;
						}
						
						case R.drawable.b_sprout_d: {
							displayPlacer(icon_resid, place_level, true, false);
							break;
						}
						
						case R.drawable.menu_rotate:
							if(mb!=null){
								mb.setAngle(mb.getAngle()+90);
							}
						break;
						
						case R.drawable.menu_roots:
						case R.drawable.b_stalk2_ud:
						case R.drawable.b_tendrill_sprout_thorn_d:
						case R.drawable.b_gall_ud:
						case R.drawable.b_core:
						case R.drawable.menu_poison:
						case R.drawable.b_shooter4:
						case R.drawable.b_shooter5:
						case R.drawable.b_shooter6:
							mi.getParent().setBuildingProcess(mi, tic);
						break;
						
						case R.drawable.menu_heal:
							if(mb!=null && mb instanceof PlantBlock) {
								mi.getParent().setBuildingProcess(mi, tic);
							}
						break;
						
						case R.drawable.menu_cancel:
							if(mb!=null && mb instanceof Block){
								Block bmb = (Block)mb;
								if(!bmb.isBuilded()){
									b = bmb;
									scene.setBlock(b.getLevel(), b.getX(), b.getY(), null);
								}
							}
						break;
					}
				}
				}
		}
		
		public int getCommandMode(){
			return command_mode;
		}
		
		public void clearCommandMode(){
			command_mode = COMMAND_NONE;
			GameMenu.setSelectedType(0);
			clearItemSelection();
			if(is_menu_visible) closeMenu();
			
			if(obj_area!=null){
				displayMainMenu();
			}
		}
		
		public boolean hasSelectedAlly(){
			if(selected_item_list!=null){
				for(Item item : selected_item_list){
					if(Entity.getAlignment(player, item.getPlayer())==Entity.ALIGN_ALLY) return true;
				}
			}
			return false;
		}
		
		public void setCommandMode(int command_mode){
			if(command_mode == COMMAND_NONE) clearCommandMode();
			else {
				if(command_mode==COMMAND_SELECT || hasSelectedAlly()) {
					command_cursor = getAbsoluteCenter();
					this.command_mode = command_mode;
					int resid;
					switch(command_mode) {
						case COMMAND_SELECT:	resid = R.drawable.menu_select_items; break;
						case COMMAND_MOVE:		resid = R.drawable.menu_move; break;
						case COMMAND_ATTACK:	resid = R.drawable.menu_attack; break;
						default:				resid = 0; break;
					}
					GameMenu.setSelectedType(resid);
				}
			}
		}
		
		public void onUp(){
			
			main_menu.onUp();
			Scene scene = this.scene;
			if(scene==null) return;
			
			place_item = false;
			rotate_item = false;
			GameMenu.releasePressedBtn();
			Rect r = selection_rect;
			//if(command_mode == COMMAND_SELECT) command_mode = COMMAND_NONE;
			if(r!=null && (r.left!=r.right || r.top!=r.bottom)){
				command_cursor = new Coord(r.right,r.bottom);
				selection_rect = null;				
				int x1 = r.left/Block.getAbsoluteWidth();
				int y1 = r.top/Block.getAbsoluteWidth();
				int x2 = r.right/Block.getAbsoluteWidth();
				int y2 = r.bottom/Block.getAbsoluteWidth();
				if(x2<x1) {
					int tmp = x1;
					x1 = x2;
					x2 = tmp;
				}
				if(y2<y1) {
					int tmp = y1;
					y1 = y2;
					y2 = tmp;
				}
				
				int sw = scene.getWidth();
				int sh = scene.getHeight();
				
				if(x1<0) x1=0;
				else if(x1>=sw) x1 = sw-1;
				if(x2<0) x2=0;
				else if(x2>=sw) x2 = sw-1;
				if(y1<0) y1=0;
				else if(y1>=sh) y1 = sh-1;
				if(y2<0) y2=0;
				else if(y2>=sh) y2 = sh-1;
				
				if(obj_area!=null){
					//select objective area
					AreaObjective ao = obj_area;
					Rect area = new Rect(x1,y1,x2,y2);
					ao.setArea(area);
				}
				else {
					//select bugs
					HashSet<Item> g = new HashSet<Item>();
					for(int j=y1; j<=y2; j++){
						for(int i=x1; i<=x2; i++){
							Map<Item,Boolean> li = scene.getItemsAt(i, j);
							if(li!=null){
								for(Item item : li.keySet()){
									if(item.getHitPoints()>0 && item.getPlayer()==player && item instanceof Bug){
										g.add(item);
									}
								}
							}
						}
					}
					
					if(g.size()>0){
						storeToSavedGroups(g);
						selected_item_list = g;						
						setCommandMode(COMMAND_MOVE);
						
						Iterator<Item> it = selected_item_list.iterator();
						if(it.hasNext()) displayMenu(it.next());
					}
				}
			}
		}
		
		private void storeToSavedGroups(HashSet<Item> g) {
			boolean saved = false;
			for(HashSet<Item> g2: saved_groups) {
				if(g.size()==g2.size()){
					boolean contains = true;
					for(Item item : g) if(!g2.contains(item)) {
						contains = false;
						break;
					}
					if(contains) {
						saved = true;
						break;
					}
				}
			}
			if(!saved) {
				if(saved_groups.size()==MAX_SAVED_GROUPS) saved_groups.remove(0);
				saved_groups.add(g);
			}			
		}
		
		public boolean onDown(MotionEvent e) {
			int x = (int)e.getX();
			int y = (int)(e.getY()-(TITLE_HEIGHT*scale));
			
			if(main_menu.isVisible()){
				return main_menu.onTapXY((int)e.getX(), y, true);
			}
			
			Scene scene = this.scene;
			if(scene==null) return false;
			
			/*
			if(game_mode==GAME_MODE_BUG_SMASHER){
				Coord c_pos = new Coord((int)x,(int)y);
				double range2 = BUG_SMASH_RANGE*scale*zoom;
				range2 *= range2;
				
				List<Item> items = scene.getItems(BUG_LEVEL);
				if(items!=null){
					for(Item item : items){
						if(item instanceof Bug){
							Bug bug = (Bug)item;
							Coord b_pos = new Coord((int)(bug.getX()*zoom) - scroll_x, (int)(bug.getY()*zoom) - scroll_y);
							if(c_pos.d2(b_pos)<range2){
								bug.damage(BUG_SMASH_DAMAGE, null, player);
							}
						}
					}
				}
			}
			else {
			*/
				if(is_menu_visible && mb!=null){
					GameMenu menu = mb.getMenu();
					int start_y = menu.getMenuTopY();
					if(start_y<y && start_y>0) {
						menu.onTapXY((int)e.getX(), y, this, true);
						return false;
					}
				}
				
				if(command_mode != COMMAND_NONE){
					command_cursor = new Coord((int)((x+scroll_x)/zoom), (int)((y+scroll_y)/zoom));
					selection_rect = new Rect(command_cursor.x,command_cursor.y,command_cursor.x,command_cursor.y);
				}
				
			//}
				
			if(game_mode==GAME_MODE_MAP_EDITOR && selected_item!=null) {
				Item i = selected_item;
				Coord c_pos = new Coord((int)x,(int)y);
				Coord i_pos = new Coord((int)(i.getX()*zoom)-scroll_x,(int)(i.getY()*zoom)-scroll_y);
				
				long d2 = i_pos.d2(c_pos);
				double z2s2 = zoom*scale;
				z2s2 *= z2s2;
				
				if(d2<ITEM_SELECTOR_CENTER*ITEM_SELECTOR_CENTER*z2s2){
					place_item = true;
					return false;
				}
				else if (d2<ITEM_SELECTOR_RADIUS*ITEM_SELECTOR_RADIUS*z2s2) {
					i.setAngle(i_pos.angle(c_pos)/Math.PI*180);
					rotate_item = true;
					return false;
				}
			}
			
			
			return false;
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			if(main_menu.isVisible()){
				return main_menu.onFling(e1, e2, velocityX, velocityY);
			}
			else if(is_menu_visible && mb!=null){
				if(scene==null) return false;
				
				GameMenu menu = mb.getMenu();
				int start_y = menu.getStartY();
				if(start_y>0 && start_y<e1.getY() - (int)(TITLE_HEIGHT*SceneView.scale)){
					menu.flingScroll(velocityX);
					return true;
				}
			}
			return false;
		}
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(main_menu.isVisible()){
				return main_menu.onScroll(e1, e2, distanceX, distanceY);
			}
			
			Scene scene = this.scene;
			if(scene==null) return false;
			
			Message message = messages.peek();
			if(message!=null){
				if(message.isScrollable() && ((distanceY<0 && message.getTopPosition()<0) || (distanceY>0 && message.getBottomPosition()>canvas_height))){
					message.setScrollY(message.getScrollY()+(int)distanceY);
				}
				return false;
			}
				
			float x = e2.getX();
			float y = e2.getY() - TITLE_HEIGHT*scale;
			
			if(command_mode == COMMAND_SELECT){
				if(selection_rect!=null){
					
					selection_rect.right  = (int)((x+scroll_x)/zoom);
					selection_rect.bottom = (int)((y+scroll_y)/zoom);
					
					if(x+(int)(60*scale)>canvas_width){
						setScrollX(getScrollX()+(int)(3*scale));
					}
					else if(x-(int)(60*scale)<0){
						setScrollX(getScrollX()-(int)(3*scale));
					}
					
					if(y+(int)(60*scale)>canvas_height) {
						setScrollY(getScrollY()+(int)(3*scale));
					}
					else if(y-(int)(60*scale)<0) {
						setScrollY(getScrollY()-(int)(3*scale));
					}
					
				}
				return false;
			}
			
			if(minimap_size>0
			&&  x >= MINIMAP_MARGIN*scale 
			&&  x <= (MINIMAP_MARGIN+scene.getWidth()*minimap_size)*scale
			&&  y >= MINIMAP_MARGIN*scale 
			&&  y <= (MINIMAP_MARGIN+scene.getHeight()*minimap_size)*scale) {
		        int c_width  = SceneView.getCanvasWidth();
		        int c_height = SceneView.getCanvasHeight();
				int sx = (int)((x - MINIMAP_MARGIN*scale)/minimap_size/scale * Block.getAbsoluteWidth() * zoom) - c_width/2;
				int sy = (int)((y - MINIMAP_MARGIN*scale)/minimap_size/scale * Block.getAbsoluteHeight() * zoom) - c_height/2;
				setScroll(new Coord(sx,sy));
				return true;
			}
			
			if(selected_item!=null){				
				Item i = selected_item;
				double x1 = i.getX();
				double y1 = i.getY();
				
				if(place_item){
					i.setXY(x1-distanceX/zoom, y1-distanceY/zoom);
					return true;
				}
				else if(rotate_item){
					Coord c_pos = new Coord((int)e2.getX(),(int)e2.getY());
					Coord i_pos = new Coord((int)(i.getX()*zoom)-scroll_x,(int)(i.getY()*zoom)-scroll_y);
					//double angle = i.getAngle();
					i.setAngle(i_pos.angle(c_pos)/Math.PI*180);
					//Item.clearItemsFromCache();
					return true;
				}
			}
			
			if(is_menu_visible && mb!=null){
				int start_y = mb.getMenu().getStartY();
				if(start_y>0 && start_y<e1.getY() - (int)(TITLE_HEIGHT*SceneView.scale)){
					mb.getMenu().scroll((int)distanceX);
					return true;
				}
				else {
					setScrollX(getScrollX()+(int)distanceX);
					setScrollY(getScrollY()+(int)distanceY);					
				}
			}
			else {
				setScrollX(getScrollX()+(int)distanceX);
				setScrollY(getScrollY()+(int)distanceY);
			}
			return true;
		}
		
		public boolean isMainMenuVisible(){
			return main_menu.isVisible();
		}
		
		public boolean closeMainMenu(){
			if(main_menu!=null) {
				main_menu.close();
				return true;
			}
			else {
				return false;
			}
		}
		
		public boolean backMainMenu(){
			if(!main_menu.closeSureDialog()){
				if(!main_menu.selectParent()) return false; //closeMainMenu();
				else return true;
			}
			else return true;
		}
		
		public boolean isMenuVisible(){
			return is_menu_visible;
		}
		
		public boolean isPlacerVisible(){
			return is_placer_visible;			
		}
		
		public boolean isItemSelected(){
			return selected_item!=null;
		}
		
		public void clearItemSelection(){
			place_item = false;
			rotate_item = false;
			selected_item = null;
			selected_item_list = null;
			//if(is_menu_visible) closeMenu();
		}
		
		@Override
		public void closeMenu(){
			mb.getMenu().startAnim(DOWN, 5);
			is_menu_visible = false;
			clearCommandMode();				
		}
		
		public void closePlacer(){
			if(mb!=null && mi!=null) {
				mb.payBackPrice(mi.getPrice());
			}
			is_placer_visible = false;			
		}
		
		public void displayMenu(Item item){
			//if(game_mode==GAME_MODE_BUG_SMASHER) return;			
			hidePlacer();
			mb = item;
			if(!is_menu_visible) mb.getMenu().startAnim(UP, 5);
			is_menu_visible = true;
		}
		
		public void displayMenu(int mx, int my, boolean prefer_built, boolean autoPlace){
			//if(game_mode==GAME_MODE_BUG_SMASHER) return;
			//else 
			
			hidePlacer();
			
			int al = active_level - 1;
			
			//skip select ground level if no reason to use: more user friendly interface
			if(al==GROUND_LEVEL && scene.getBlock(STALK_LEVEL, mx, my)!=null) al--;
			
			if(mb!=null && mb instanceof Block && this.mx==mx && this.my==my && al>=0 && !prefer_built){
				mb = scene.getBlock(al, mx, my);
			}
			else {
				if(prefer_built) mb = scene.getHigherBuiltBlock(mx, my);
				if(!prefer_built || mb==null) mb = scene.getHigherBlock(mx, my);				
			}
			
			this.mx = mx;
			this.my = my;
			
			if(mb!=null) {
				active_level = mb.getLevel();
			}
			if(mb==null){
				mb = new Block(Entity.PLAYER_NEUTRAL, R.drawable.b_soil, mx, my, GROUND_LEVEL, true);
				scene.setBlock(GROUND_LEVEL, mx, my, (Block)mb);
				active_level = GROUND_LEVEL;
				//mb.getMenu().scroll(GameMenu.getLastSoilScroll());
			}
			
			if(!is_menu_visible) {
				mb.getMenu().startAnim(UP, 5);
			}
			is_menu_visible = true;
			
			if(group_select!=null && mb!=null) { // && (mb.getLevel()!=GROUND_LEVEL || (mb instanceof Block && ((Block)mb).isNitrogenBlock()))){
				group_select.toggleTarget(mb);
			}
			
			if(autoPlace && game_mode==GAME_MODE_MAP_EDITOR && autoPlaceBlockMode && lastSelectedMenuItemResId>0){
				MenuItem mi = mb.getMenu().getItemWithResId(lastSelectedMenuItemResId);
				if(mi!=null) onMenuItemSelected(mi);
			}
		}
		
		private HashSet<Item> getAliveEntities(HashSet<Item> items){
			if(items!=null){
				HashSet<Item> alive = new HashSet<Item>();
				for(Item item : items){
					if(item.getHitPoints()>0){
						alive.add(item);
					}
				}
				return !alive.isEmpty()?alive:null;
			}
			return null;
		}
		
	    public void moveIt() {
	    	if(clear_scene){
	    		clear_scene = false;
	    		this.cinematic = null;
	    		this.event  = null;
	    		this.scene  = null;
	    	}
	    	
	    	Scene scene = this.scene;
	    	if(cinematic!=null){
	    		cinematic.moveIt();
	    		if(cinematic.isEnded()) {
	    			if(event!=null) {
	    				event.fulfill();
	    				event = null;
	    			}
	    			cinematic = null;
	    		}
	    	}
	    	else if(scene!=null) {
	    		if(!main_menu.isVisible() && !loading && !saveing){
			    	if(is_menu_visible && mb!=null) mb.getMenu().selectEnabledTopFilter();
			    	if(game_mode!=GAME_MODE_MAP_EDITOR) scene.moveIt(tic);
					if(minimap_size>0 && tic%50==0) scene.calculateMiniMap(minimap_transparency);
					selected_item_list = getAliveEntities(selected_item_list);
					
					if(command_mode!=COMMAND_NONE && command_mode!=COMMAND_SELECT && selected_item_list==null){
						clearCommandMode();
					}
					
					if(event!=null){
						if(event instanceof AutoScrollEvent){
							AutoScrollEvent as = (AutoScrollEvent)event;
							Coord tc = as.getScroll();
							
							int sx = tc.x<scroll_x?-1:tc.x>scroll_x?1:0;
							int sy = tc.y<scroll_y?-1:tc.y>scroll_y?1:0;
							int dx = Math.abs(tc.x-scroll_x);
							int dy = Math.abs(tc.y-scroll_y);
							
							boolean changed = false;
							
							double dr;
							int sux,suy;
							
							double tz = as.getZoom();
							int sz = tz<zoom?-1:tz>zoom?1:0;
							double dz = Math.abs(tz-zoom);
							if(AUTOZOOM_SPEED<dz) {
								setZoom(zoom + sz*AUTOZOOM_SPEED);
								changed = true;
							}
							else {
								setZoom(tz);
							}
												
							if(!changed){
								if(dx<=dy){
									dr  = (double)dx/(double)dy;
									sux = (int)(AUTOSCROLL_SPEED*zoom*dr);
									suy = (int)(AUTOSCROLL_SPEED*zoom);
								}
								else {
									dr  = (double)dy/(double)dx;
									sux = (int)(AUTOSCROLL_SPEED*zoom);
									suy = (int)(AUTOSCROLL_SPEED*zoom*dr);						
								}
								
								if(sux<dx){
									setScrollX(scroll_x+sx*sux);
									changed = true;
								}
								else if(sx!=0){
									setScrollX(tc.x);
								}
								
								if(suy<dy){
									setScrollY(scroll_y+sy*suy);
									changed = true;
								}
								else if(sy!=0){
									setScrollY(tc.y);
								}
							}
							
							if(!changed) {
								event.fulfill();
								event = null;
							}
						}
						else if(event instanceof CommandMoveEvent){
							CommandMoveEvent ce = (CommandMoveEvent)event;
							if(tic%10==0) ce.checkIfFulfilled();
							if(ce.isFulfilled()){
								event = null;
							}
							else if(ce.isAutoFocus()){
								Coord fc = ce.getFocusCoord();
								if(fc!=null) focusToCoord(fc);
							}
						}
						else if(event instanceof DelayEvent){
							DelayEvent de = (DelayEvent)event;
							de.checkIfFulfilled();
							if(de.isFulfilled()){
								event = null;
							}
						}
						else if(event instanceof CinematicEvent){
							cinematic = ((CinematicEvent)event).getNewCinematic();
						}
						else if(tic%10==0){
							if(event instanceof PopupTextEvent){
								if(messages.isEmpty()){
									event.fulfill();
									event = null;
								}
							}
						}
					}
	    		}
	    	}
	    	else {
	    		flower_field.moveIt();
	    	}
	    }
	    
	    public synchronized void drawIt(Canvas canvas, boolean take_photo) {
	    	if(canvas==null) return;
	    	Scene scene = this.scene;
	    	/*
	    	boolean grayscale = Entity.getGrayScaleMode();
	    	if(take_photo && grayscale) {
	    		Entity.setGrayScaleMode(false);
	    	}
	    	*/
	        boolean main_menu_visible = main_menu.isVisible();
	        
	        if(cinematic!=null){
	        	cinematic.drawIt(canvas);
	        }
	        else if(scene!=null){
		    	int scroll_x = this.scroll_x;
		    	int scroll_y = this.scroll_y;
		    	
				DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
		        int c_width  = SceneView.getCanvasWidth(); //dm.widthPixels;
		        int c_height = SceneView.getCanvasHeight(); //dm.heightPixels;
		        				
		    	double zoom;
		    	if(zoom_changed) {
		    		zoom = this.zoom;
		    		Entity.setZoom(zoom);
		    		zoom_changed = false;
		    	}
		    	else zoom = this.zoom;
		    	
	        	Paint paint = new Paint();
	        	paint.setAlpha(160);
	        	
		    	int bw = Block.getWidth();
		    	int bh = Block.getHeight();
		    	
				int chw = bw/10;
				if(chw<1) chw = 1;
		    	
		    	int px, py;
		    	int sw = scene.getWidth();
		    	int sh = scene.getHeight();
		    	int sl = scene.getLevels();
		    	
		    	int s_x1 = scroll_x/bw;
		    	int s_y1 = scroll_y/bh;
		    	int s_x2 = s_x1 + c_width/bw + 2;
		    	int s_y2 = s_y1 + c_height/bh + 2;
		    	if(s_x1<0) s_x1 = 0;
		    	if(s_y1<0) s_y1 = 0;
		    	if(s_x2>sw) s_x2 = sw;
		    	if(s_y2>sh) s_y2 = sh;
		    	
		        int [][][] rg = reach_grid;
		    	for(int level=0; level<sl; level++){
			    	for(int y=s_y1; y<s_y2; y++){
			    		for(int x=s_x1; x<s_x2; x++){
			    			px = x*bw-scroll_x;
			    			py = y*bh-scroll_y;
			    			Block b = scene.getBlock(level, x, y);
			    			if(b!=null){
			    				if(b.getType()==R.drawable.b_rock){
			    					int hp_scale = (int)((1-b.getHitPointsRate())*10*scale);
			    					if(hp_scale>0){
			    						int dx = (rnd.nextInt(hp_scale)-hp_scale/2);
			    						int dy = (rnd.nextInt(hp_scale)-hp_scale/2);
			    						
			    						if      (dx<0 && x>0 && scene.getHigherBlockLevel(x-1, y)>=level) dx = 0;
			    						else if (dx>0 && x<sw-1 && scene.getHigherBlockLevel(x+1, y)>=level) dx = 0;
			    						if      (dy<0 && y>0 && scene.getHigherBlockLevel(x, y-1)>=level) dy = 0;
			    						else if (dy>0 && y<sh-1 && scene.getHigherBlockLevel(x, y+1)>=level) dy = 0;
			    						
				    					px += dx;
				    					py += dy;
			    					}
			    				}
			    				
			    				Bitmap bm = b.getBitmap(Entity.BITMAP_NORMAL);
			    				final int bmx = px - (bm.getWidth() - bw)/2;
			    				final int bmy = py - (bm.getHeight() - bh)/2;
			    				canvas.drawBitmap(bm, bmx, bmy, b.isBuilded()?null:paint);
					    		
					    		if(b.isBuilded()) {
					    			if(b instanceof PlantBlock){
					    				PlantBlock pb = (PlantBlock)b;
					    				for(int n=0; n<Block.NUTRIENTS; n++){
					    					if(pb.isNutrientLeaking(n)){
							    				bm = nutrient_icon[n].getBitmap(Entity.BITMAP_NOEFFECTS);
							    				canvas.drawBitmap(bm,px - (bm.getWidth() - bw)/2,py - (bm.getHeight() - bh)/2,null);
					    					}
					    				}
					    				
					    				if(pb.isSearing()) {
						    				canvas.drawBitmap(searingIcon.getBitmap(Entity.BITMAP_NOEFFECTS),px - (bm.getWidth() - bw)/2,py - (bm.getHeight() - bh)/2,null);					    					
					    				}
					    			}
					    			
					    			if(b.hasMenu()) {
					    				final double bp = b.getMenu().getBuildingPercent(tic);
					    				if(bp<1){
									        canvas.drawRect(bmx, bmy, bmx+bw, (float)(bmy+4*scale*zoom), buildStatBgPaint);
									        canvas.drawRect(bmx, bmy, (int)(bw*bp + bmx), (float)(bmy+4*scale*zoom), buildStatPaint);
					    				}
					    			}
				    			}
			    			}
			    			else if (level==0) {
			    				//soil.setDisplayedType(Block.getRandomDisplayedType(R.drawable.b_soil));
			    				canvas.drawBitmap(soil.getBitmap(Entity.BITMAP_NORMAL),px,py,null);
			    			}
			    			
			    			if(level==0 && rg!=null){
			    				canvas.drawRect(px, py, px+bw, py+bh, rg[0][x][y]<Integer.MAX_VALUE?reachMapReachPaint:reachMapBlockedPaint);
			    			}
			    			else if(obj_area!=null){
			    				Rect area = obj_area.getArea();
			    				if(x>=area.left && y>=area.top && x<=area.right && y<=area.bottom){
				    				canvas.drawRect(px, py, px+bw, py+bh, reachMapReachPaint);		    					
			    				}
			    				else {
				    				canvas.drawRect(px, py, px+bw, py+bh, reachMapBlockedPaint);		    					
			    				}
			    			}
			    		}
			    	}
			    	
					int mr = (int)(Item.MAX_BITMAP_RADIUS * scale * zoom);
					List<Item> items = scene.getItems(level);
					if(items!=null){
			    		for(Item item : items){
			    			if(item.getLevel()==level){
				    			px = (int)(zoom*item.getX())-scroll_x;
				    			py = (int)(zoom*item.getY())-scroll_y;
				    			if(px+mr>0 && py+mr>0 && px-mr<c_width && py-mr<c_height){
				    				Bitmap bm = item.getBitmap(Entity.BITMAP_NORMAL);
					    			canvas.drawBitmap(bm,px - bm.getWidth()/2,py - bm.getHeight()/2,null);	
				    			}
			    			}
			    		}
					}
			    	
		    	}
		    	
	        	
	        	if(DEBUG && display_debug){
			    	paint.setColor(0xff0b4400);
			    	if(scroll_x<0) canvas.drawRect(0, 0, -scroll_x+1, c_height, paint);
			    	if(scroll_y<0) canvas.drawRect(0, 0, c_width, -scroll_y+1, paint);
			    	int fx = scene.getWidth()*bw-scroll_x;
			    	if(fx<c_width) canvas.drawRect(fx, 0, c_width, c_height, paint);
			    	int fy = scene.getHeight()*bh-scroll_y;
			    	if(fy<c_height) canvas.drawRect(0, fy, c_width, c_height, paint);        		
	        	}
	        	
				int mr = (int)(Bug.MAX_BITMAP_RADIUS * scale * zoom);
				List<Item> bugs = scene.getItems(BUG_LEVEL);				
				
	        	for(int i=0; i<3; i++){
	        		if(i>0){
				        if (!take_photo && !main_menu_visible && selected_item_list!=null){
				        	paint.setStyle(Style.STROKE);
							for(Item item : selected_item_list){								
								if(item.getHitPoints()>0 && ((i==1 && item.getMovingType()!=Item.MOVING_FLY) || (i==2 && item.getMovingType()==Item.MOVING_FLY))){
									int x = (int)(item.getX() * zoom) - scroll_x;
									int y = (int)(item.getY() * zoom) - scroll_y;
									int r = (int)(item.getSelectionRadius()*zoom);
									paint.setColor(item.getCrosshairColor(player));					
						        	paint.setStrokeWidth(chw);
						        	canvas.drawCircle(x, y, r - chw/2, paint);
						        	paint.setAlpha(255);
						        	paint.setStrokeWidth(1);
						        	canvas.drawCircle(x, y, r-chw, paint);
								}
							}
				        	paint.setStyle(Style.FILL);
						}
	        		}
			        
					if(bugs!=null){
						for(Item item : bugs){
							if(item instanceof Bug){
								if((i==0 && item.getHitPoints()<=0) || (i==1 && item.getHitPoints()>0 && item.getMovingType()!=Item.MOVING_FLY) || (i==2 && item.getHitPoints()>0 && item.getMovingType()==Item.MOVING_FLY)){
									Bug bug = (Bug)item;
							    	int bx = (int)(zoom*bug.getX())-scroll_x;
							    	int by = (int)(zoom*bug.getY())-scroll_y;
					    			if(bx+mr>0 && by+mr>0 && bx-mr<c_width && by-mr<c_height){
								    	Bitmap bb = bug.getBitmap(Entity.BITMAP_NORMAL);
								        canvas.drawBitmap(bb, bx - bb.getWidth()/2, by - bb.getHeight()/2, null);
								    	if(display_bug_life && !take_photo && !main_menu_visible && bug.getState()!=Bug.SQUASHED) {
								        	int lx1 = (int)(bx - 12*scale*zoom);
								        	int ly1 = (int)(by - 20*scale*zoom);
								        	int lx2 = (int)(bx + 12*scale*zoom);
								        	int ly2 = (int)(by - 18*scale*zoom);
								        	
									        canvas.drawRect(lx1, ly1, lx2, ly2, lifeStatBgPaint);
									        float hp_percent = bug.getHitPointsRate();
									        canvas.drawRect(lx1, ly1, (int)((lx2-lx1)*hp_percent + lx1), ly2, lifeStatPaint);
								    	}
								    	
								        if(DEBUG && display_debug){
			
								        	Stack<Entity> dest_path = bug.getDestPath();
								        	if(dest_path!=null && !dest_path.isEmpty()){
								        		Coord prev = bug.getPosition();
								        		paint.setColor(0xffffff00);
									        	paint.setStrokeWidth(1);
								        		for(int j=dest_path.size()-1; j>=0; j--){
								        			Entity d = dest_path.elementAt(j);
								        			Coord act = bug.getMarchPosition(d);
								        			canvas.drawLine((int)(zoom*prev.x-scroll_x), (int)(zoom*prev.y-scroll_y), (int)(zoom*act.x-scroll_x), (int)(zoom*act.y-scroll_y), paint);
								        			canvas.drawCircle((int)(zoom*act.x-scroll_x), (int)(zoom*act.y-scroll_y), 2*scale, paint);
								        			prev = act;
								        		}
								        	}
								        	
									        ArrayList<Coord> pts = bug.getEyeSightPts();	        
									        if(pts!=null){
									        	paint.setARGB(255, 255, 0, 0);
									        	for(Coord c : pts){
									        		canvas.drawPoint((float)(zoom*c.x-scroll_x), (float)(zoom*c.y-scroll_y), paint);
									        	}
									        }
									        
									        
									        ArrayList<Entity> fm = bug.getFoodMemory();
									        if(fm!=null){
									        	paint.setARGB(63, 0, 255, 0);
									        	for(Entity e : fm){
									        		if(e instanceof Block){
									        			Block b = (Block)e;
									        			canvas.drawCircle(b.getX()*bw + bw/2 - scroll_x, b.getY()*bh + bh/2 - scroll_y, 10, paint);
									        		}
									        		else if (e instanceof Item){
									        			Item item2 = (Item)e;
									        			canvas.drawCircle((int)(item2.getX()*zoom) - scroll_x, (int)(item2.getY()*zoom) - scroll_y, 10, paint);
									        		}
									        	}
									        }
									        
								        }
					    			}
								}
							}
							else {
								//not bug items in BUG_LEVEL
				    			px = (int)(zoom*item.getX())-scroll_x;
				    			py = (int)(zoom*item.getY())-scroll_y;
				    			if(px+mr>0 && py+mr>0 && px-mr<c_width && py-mr<c_height){
				    				Bitmap bm = item.getBitmap(Entity.BITMAP_NORMAL);
					    			canvas.drawBitmap(bm,px - bm.getWidth()/2,py - bm.getHeight()/2,null);	
				    			}						
							}
						}
					}
	        	}
	        	
				mr = (int)(Item.MAX_BITMAP_RADIUS * scale * zoom);
				List<Item> items = scene.getItems(AIR_LEVEL);
				if(items!=null){
		    		for(Item item : items){
		    			if(item.getLevel()==AIR_LEVEL){
			    			px = (int)(zoom*item.getX())-scroll_x;
			    			py = (int)(zoom*item.getY())-scroll_y;
			    			if(px+mr>0 && py+mr>0 && px-mr<c_width && py-mr<c_height){
			    				Bitmap bm = item.getBitmap(Entity.BITMAP_NORMAL);
				    			canvas.drawBitmap(bm,px - bm.getWidth()/2,py - bm.getHeight()/2,null);	
			    			}
		    			}
		    		}
				}
				
				if(group_select!=null){
					HashSet<Entity> targets = group_select.getTargets();
					String evt_title = group_select.getDisplayableTitle();
					paint.setTextSize((float)(12*zoom*scale));
					paint.setColor(0xffff0000);
					paint.setTypeface(Typeface.DEFAULT_BOLD);
					paint.setTextAlign(Align.CENTER);
					for(Entity e : targets){
						Coord pos = e.getPosition();
	        			canvas.drawText(evt_title, (int)(zoom*pos.x-scroll_x), (int)(zoom*pos.y-scroll_y), paint);
					}
				}
				
				scene.applyQueuedRemove();
				scene.applyQueuedAdd();
				
				if(DEBUG && display_debug){
					try {
						for(Vector v : scene.getDebugVectors()){
							paint.setColor(v.color);
							canvas.drawCircle((int)(v.x*zoom) - scroll_x, (int)(v.y*zoom) - scroll_y, 3, paint);
							double rad = 2.0*Math.PI/360.0 * v.angle;
							double x2 = v.x + (20*Math.sin(rad));
							double y2 = v.y - (20*Math.cos(rad));
							canvas.drawLine((int)(v.x*zoom)-scroll_x, (int)(v.y*zoom)-scroll_y, (int)(x2*zoom)-scroll_x, (int)(y2*zoom)-scroll_y, paint);
						}
					}
					catch(ConcurrentModificationException e){}
				}
				/*
	        	paint.setARGB(255, 255, 0, 0);
				for(Coord c : debug_tap_coords){
					canvas.drawCircle(c.x, c.y, 3, paint);
				}
				*/
				
		    	if(!DEBUG || !display_debug){
			    	if(!main_menu_visible) paint.setColor(0xff0b4400);
			    	else paint.setColor(0xff1a1a1a);
			    		
			    	if(scroll_x<0) canvas.drawRect(0, 0, -scroll_x+1, c_height, paint);
			    	if(scroll_y<0) canvas.drawRect(0, 0, c_width, -scroll_y+1, paint);
			    	int fx = scene.getWidth()*bw-scroll_x;
			    	if(fx<c_width) canvas.drawRect(fx, 0, c_width, c_height, paint);
			    	int fy = scene.getHeight()*bh-scroll_y;
			    	if(fy<c_height) canvas.drawRect(0, fy, c_width, c_height, paint);
		    	
			    	//RIGHT
			    	bg_pattern.setAngle(270);
			    	Bitmap bm = bg_pattern.getBitmap(Entity.BITMAP_NORMAL);
					int pw = bm.getWidth();
					int ph = bm.getHeight();
					int j = 0;
					for(int x=fx; x<c_width+pw/2; x+=pw/2){
						int i = 0;
						for(int y=-ph/2-scroll_y-j*ph/2; y<fy+j*ph/2; y += ph/2){
							i = ((i+1) % 3);
							canvas.drawBitmap(bm, x-2*ph/(3 + i), y, null);
						}
						j++;
					}
					
			    	//LEFT
			    	bg_pattern.setAngle(90);
			    	bm = bg_pattern.getBitmap(Entity.BITMAP_NORMAL);
					pw = bm.getWidth();
					ph = bm.getHeight();
					j = 0;
					for(int x=-scroll_x; x>-pw/2; x-=pw/2){
						int i = 0;
						for(int y=-ph/2-scroll_y-j*ph/2; y<fy+j*ph/2; y += ph/2){
							i = ((i+1) % 3);
							canvas.drawBitmap(bm, x-2*ph/(3 + i), y, null);
						}
						j++;
					}
					
			    	//TOP
			    	bg_pattern.setAngle(180);
			    	bm = bg_pattern.getBitmap(Entity.BITMAP_NORMAL);
					pw = bm.getWidth();
					ph = bm.getHeight();
					j = 0;
					for(int y=-scroll_y; y>-ph/2; y-=ph/2){
						int i = 0;
						for(int x=-pw/2-scroll_x-j*pw/2; x<fx+j*pw/2; x += pw/2){
							i = ((i+1) % 3);
							canvas.drawBitmap(bm, x, y-2*ph/(3 + i), null);
						}
						j++;
					}
					
			    	//BOTTOM
			    	bg_pattern.setAngle(0);
			    	bm = bg_pattern.getBitmap(Entity.BITMAP_NORMAL);
					pw = bm.getWidth();
					ph = bm.getHeight();
					j = 0;
					for(int y=fy; y<c_height+ph/2; y+=ph/2){
						int i = 0;
						for(int x=-pw/2-scroll_x-j*pw/2; x<fx+j*pw/2; x += pw/2){
							i = ((i+1) % 3);
							canvas.drawBitmap(bm, x, y-2*ph/(3 + i), null);
						}
						j++;
					}
					
		    	}
		        
		    	Item si = selected_item;
		        if(!take_photo && !main_menu_visible && si!=null && game_mode == GAME_MODE_MAP_EDITOR){
		        	paint.setColor(0xff00ff00);
					int x1 = (int)(si.getX() * zoom) - scroll_x;
					int y1 = (int)(si.getY() * zoom) - scroll_y;
		        	paint.setAlpha(32);
		        	paint.setStrokeWidth(scale*3);
					int r = (int)(scale*ITEM_SELECTOR_RADIUS*zoom);
					int angle = (int)si.getAngle() - 90;				
		        	paint.setStyle(Style.FILL);
		        	canvas.drawCircle(x1, y1, r, paint);
		        	paint.setStyle(Style.STROKE);
		        	paint.setAlpha(255);
		        	canvas.drawCircle(x1, y1, r, paint);
		        	canvas.drawCircle(x1, y1, (int)(scale*ITEM_SELECTOR_CENTER*zoom), paint);
		        	canvas.drawArc(new RectF(x1-r,y1-r,x1+r,y1+r), angle-10, 20, true, paint);
			        	paint.setStyle(Style.FILL);
		        }
		        
		        /*
		        int [][][] rg = reach_grid;
		        if(rg!=null){	        	
			    	for(int y=s_y1; y<s_y2; y++){
			    		for(int x=s_x1; x<s_x2; x++){
			    			px = x*bw-scroll_x;
			    			py = y*bh-scroll_y;
			    			canvas.drawRect(px, py, px+bw, py+bh, rg[0][x][y]<Integer.MAX_VALUE?reachMapReachPaint:reachMapBlockedPaint);
				    		//canvas.drawBitmap(bm,px - (bm.getWidth() - bw)/2,py - (bm.getHeight() - bh)/2,null);
			    		}
				    }
				}
		        */
		        
				px = mx*bw-scroll_x;
				py = my*bh-scroll_y;
				
				if(!take_photo && !main_menu_visible){ // && game_mode != GAME_MODE_BUG_SMASHER){
					Entity me = mb;
					if(me!=null){
						GameMenu menu = me.getMenu();
						if((menu!=null) && (is_menu_visible || (menu.getAnimDir()==DOWN))){
							if(command_mode == COMMAND_NONE && me instanceof Block) {
								crosshairPaint.setStyle(Style.STROKE);
								crosshairPaint.setStrokeWidth(chw);
								crosshairPaint.setColor(me.getCrosshairColor(player));
								canvas.drawCircle(px+bw/2, py+bh/2, bw/2 - chw/2, crosshairPaint);
								crosshairPaint.setStrokeWidth(1);
								crosshairPaint.setAlpha(255);
								canvas.drawCircle(px+bw/2, py+bh/2, bw/2 - chw, crosshairPaint);
								crosshairPaint.setTextSize(bh/2);
								crosshairPaint.setStyle(Style.FILL);
								crosshairPaint.setColor(0xffe6ae16);
								canvas.drawText(me.getLevel()+"", px+bw/2, py+(crosshairPaint.getTextSize()+bh-chw)/2, crosshairPaint);
								crosshairPaint.setStyle(Style.STROKE);
								crosshairPaint.setColor(0x80008000);
								canvas.drawText(me.getLevel()+"", px+bw/2, py+(crosshairPaint.getTextSize()+bh-chw)/2, crosshairPaint);
							}
							menu.draw(canvas, tic);
						}				
					}
				}
				
		        if(is_placer_visible){
		        	if((is_arrow_visible & N4_U) != 0) canvas.drawBitmap(arrow[UP].getBitmap(Entity.BITMAP_NORMAL),px,py-bh/2,null);
		        	if((is_arrow_visible & N4_R) != 0) canvas.drawBitmap(arrow[RIGHT].getBitmap(Entity.BITMAP_NORMAL),px+bw/2,py,null);
		        	if((is_arrow_visible & N4_D) != 0) canvas.drawBitmap(arrow[DOWN].getBitmap(Entity.BITMAP_NORMAL),px,py+bh/2,null);
		        	if((is_arrow_visible & N4_L) != 0) canvas.drawBitmap(arrow[LEFT].getBitmap(Entity.BITMAP_NORMAL),px-bw/2,py,null);
		        }
		        
		        // draw life stat
		        if(is_menu_visible || is_placer_visible){
		        	//if(mb!=null && mb instanceof Block)
		        	Entity b = mb; //scene.getHigherBlock(mx, my);
		        	if(b!=null && b.getHitPoints()>0) {
		        		if(b instanceof Block){
		        			//int ot = ((Block)b).getOverlapType();
		        			if(((Block)b).isNitrogenBlock()){
		    	        		drawStat(canvas, paint, b.getHitPoints(), b.getMaxHitPoints(), 0, Block.getNutrientColor(Block.NITROGEN), Block.getNutrientBgColor(Block.NITROGEN));
		        			}
		        			else if(b.getLevel()>GROUND_LEVEL){
		    	        		drawStat(canvas, paint, b.getHitPoints(), b.getMaxHitPoints(), 0, 0xff008000, 0xff800000);	        				
		        			}
		        		}
		        		else {
			        		drawStat(canvas, paint, b.getHitPoints(), b.getMaxHitPoints(), 0, 0xff008000, 0xff800000);	        			
		        		}
		        		
		        		if(b instanceof PlantBlock){
		        			PlantBlock pb = (PlantBlock)b;
		        			
		        			for(int nutrient=0; nutrient<Block.NUTRIENTS; nutrient++){
				        		drawStat(canvas, paint, (int)pb.getNutrient(nutrient), pb.getMaxNutrient(nutrient), nutrient+1, Block.getNutrientColor(nutrient), Block.getNutrientBgColor(nutrient));
		        			}
		        			
		        			if(pb.getPoison()>0){
				        		drawStat(canvas, paint, pb.getPoison(), PlantBlock.MAX_POISON, Block.NUTRIENTS+1, PlantBlock.POISON_STAT_COLOR, PlantBlock.POISON_STAT_BG_COLOR);
		        			}
		        		}
		        	}
		        }
		        	        
		        switch(command_mode){
			        case COMMAND_SELECT: {
			        	paint.setColor(0xff00ff00);
			        	paint.setStrokeWidth(3*scale);
			        	
			        	if(selection_rect!=null && (selection_rect.top!=selection_rect.bottom || selection_rect.left!=selection_rect.right)){
			        		Rect r = selection_rect;
			        		paint.setStyle(Style.STROKE);
			        		int x1 = r.left<r.right?r.left:r.right;
			        		int y1 = r.top<r.bottom?r.top:r.bottom;
			        		int x2 = r.left>r.right?r.left:r.right;
			        		int y2 = r.top>r.bottom?r.top:r.bottom;
			        		canvas.drawRect((int)(x1*zoom) - scroll_x, (int)(y1*zoom) - scroll_y, (int)(x2*zoom) - scroll_x, (int)(y2*zoom) - scroll_y, paint);
			        	}
			        	else {
				        	canvas.drawLine((int)(command_cursor.x*zoom) - scroll_x, (int)(command_cursor.y*zoom) - 10*scale - scroll_y, (int)(command_cursor.x*zoom) - scroll_x, (int)(command_cursor.y*zoom) + 10*scale - scroll_y, paint);
				        	canvas.drawLine((int)(command_cursor.x*zoom) - 10*scale - scroll_x, (int)(command_cursor.y*zoom) - scroll_y, (int)(command_cursor.x*zoom) + 10*scale - scroll_x, (int)(command_cursor.y*zoom) - scroll_y, paint);		        	
			        	}
			        }
		        }
		        
		        if(!take_photo && !main_menu_visible){
		        	if(minimap_size>0) scene.drawMiniMap(canvas, (int)(MINIMAP_MARGIN*scale), (int)(MINIMAP_MARGIN*scale), (int)(scale*minimap_size));
			        float sy = scene.drawObjectiveStates(canvas, (int)(MINIMAP_MARGIN*scale), (minimap_size>0?((int)(MINIMAP_MARGIN*scale) + (int)(scale*minimap_size)*scene.getHeight()):0) + 10*scale, tic);
			        
			        Message message = messages.peek();
			        if(message!=null){
			        	if(!message.isExpired()){
			        		message.draw(canvas);
			        	}
			        	else {
			        		messages.poll();
			        	}
			        }
			        
			        if(game_mode == GAME_MODE_MAP_EDITOR){
			        	autoPlaceRect.top    = sy + 5*scale;
			        	autoPlaceRect.bottom = autoPlaceRect.top + autoPlacePaint.getTextSize() + 20*scale;
			        	autoPlaceRect.left   = MINIMAP_MARGIN*scale;
			        	autoPlaceRect.right  = autoPlaceRect.left + 120*scale;
			        	autoPlacePaint.setColor(Color.GRAY);
			        	canvas.drawRoundRect(autoPlaceRect, 5*scale, 5*scale, autoPlacePaint);
			        	autoPlacePaint.setColor(autoPlaceBlockMode?Color.GREEN:Color.RED);
			        	float cx = (autoPlaceRect.left+autoPlaceRect.right)/2;
			        	canvas.drawRect(cx - 15*scale, autoPlaceRect.bottom - 10*scale, cx + 15*scale, autoPlaceRect.bottom - 5*scale, autoPlacePaint);
			        	autoPlacePaint.setColor(Color.LTGRAY);
			        	canvas.drawText(getString(R.string.auto_place), cx, (autoPlaceRect.top+autoPlaceRect.bottom)/2, autoPlacePaint);
			        }
			        
			        if(final_stat_visible) {
				        if(final_stat_appeared_at<0) final_stat_appeared_at = tic;
				        scene.drawFinalStatistics(canvas, player, final_stat_group, (long)(8*scale*(tic - final_stat_appeared_at)));
			        }
		        }
	    	}
	    	else {
	    		//canvas.drawColor(0xff4a7723);
	    		flower_field.drawIt(canvas);
	    	}
	    	
		    if(!take_photo) {
		        
		        //main menu
		    	float ap = main_menu.getAnimPercent();
		    	
		        if(main_menu_visible || ap>0){
		        	canvas.drawColor((int)(ap*128) << 24);
		        	main_menu.draw(canvas);
		        }
	        }		    
	    }
	    
	    private void drawStat(Canvas canvas, Paint paint, int value, int max, int pos, int color, int bgcolor){
			DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
	        int s_width  = dm.widthPixels;
	        //int s_height = dm.heightPixels;
	        
    		float percent = (float)value/(float)max;
    		int lx1 = s_width - (int)(120*scale);
        	int ly1 = (int)((20+pos*30)*scale);
        	int lx2 = (int)(lx1 + 100*scale);
        	int ly2 = (int)(ly1 + 10*scale);
        	
	        paint.setARGB(255, 200, 200, 100);
	        canvas.drawText(value+"/"+max, lx1, ly1-7*scale, paint);
	        
	        paint.setColor(0xff707050);
	        canvas.drawRect(lx1-1, ly1-1, lx2+1, ly2+1, paint);
	        paint.setColor(bgcolor);
	        canvas.drawRect(lx1, ly1, lx2, ly2, paint);
	        paint.setColor(color);
	        canvas.drawRect(lx1, ly1, (int)((lx2-lx1)*percent + lx1), ly2, paint);	    	
	    }
		
		public synchronized Bitmap captureSreenShot(Rect bounds){
			Canvas canvas = new Canvas();
			canvas.clipRect(bounds);
			Log.d("SCREENSHOT","ALLOC BITMAP..");
			Log.d("SCREENSHOT","BOUNDS RECT:"+bounds+" = ("+bounds.left+","+bounds.top+","+bounds.right+","+bounds.bottom+")");
			
			Bitmap bm = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
			Log.d("SCREENSHOT","SET BITMAP..");
			canvas.setBitmap(bm);
			Log.d("SCREENSHOT","CREATE COPY FROM CANVAS..");
			scroll_x += bounds.left;
			scroll_y += bounds.top;
			getInstance(null).getThread().drawIt(canvas, true);
			scroll_x -= bounds.left;
			scroll_y -= bounds.top;
			return bm;
		}
		
	    @Override
	    public void run() {
	       
	        Canvas c;
			while (run) {
				
				if(map_size_changed && scene!=null){
					displayMenu(0,0,true,false);
					closeMenu();
					map_size_changed = false;
					scene.setMapSize(new_map_width, new_map_height);
					setScrollX(0);
					setScrollY(0);
				}
				
				long t1 = System.currentTimeMillis();
		    	synchronized(FILE_SEMAFOR){
		    		moveIt();
		    	}
		    	
				c = null;
				try {
					c = holder.lockCanvas(null);
					if(c!=null){
						synchronized (holder) {
							canvas_width  = c.getWidth();
							canvas_height = c.getHeight();
							int min_size = canvas_width;
							if(canvas_height<min_size) min_size = canvas_height;
							
							float gm_scale = (float)min_size/475;							
							//if(gm_scale>1) gm_scale = 1;
							//Log.w("SCALE", gm_scale+"");
							GameMenu.setScale(gm_scale);
							
							float mm_scale = (float)min_size/300;
							//if(mm_scale>2) mm_scale = 2;
							//Log.w("MMSCALE", mm_scale+);
							main_menu.setScale(mm_scale);
							
							//Entity.clearRefQueue();
							drawIt(c, false);
						}
					}
				}
				finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						holder.unlockCanvasAndPost(c);
					}
				}
				long t2 = System.currentTimeMillis();
				if(t2-t1<MINIMUM_FRAME_MILIS){
					try {
						Thread.sleep(MINIMUM_FRAME_MILIS-t2+t1);
					} catch(InterruptedException e){}
				}
				
				if(!main_menu.isVisible()) tic++;
			}
			music.pause();
			
			if(shut_down){
				clearInstance();
				SceneView.this.activity.finish();
			}
	    }

		@Override
		public void onSelectMenuLevel(int level) {
			Block b;
			while((b = scene.getBlock(level, mx, my))==null){
				level--;
				if(level<GROUND_LEVEL) return;
			}
			active_level = level;
			mb = b;
		}

		public void showExitSureDialog() {
			JSONObject item = new JSONObject();
			String msg;
			String code;
			
			if(game_mode==GAME_MODE_MAP_EDITOR){
				msg  = getString(R.string.sure_dialog_exit_editor);
				code = EditorMenu.MENU_CODE_EXIT_EDITOR;
			}
			else if(game_mode==GAME_MODE_TEST_MAP){
				msg  = getString(R.string.sure_dialog_stop_testing);
				code = MainMenu.MENU_CODE_MAP_EDITOR;
			}
			else if(scene!=null){
				msg  = getString(R.string.sure_dialog_end);
				code = MainMenu.MENU_CODE_END;				
			}
			else {
				//msg  = getString(R.string.sure_dialog_exit);
				//code = DialogBox.MENU_CODE_EXIT;
				shutDown();
				return;
			}
			
			try {
				item.put(DialogBox.JSON_CODE, code);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			main_menu.showSureDialog(msg, item, main_menu.isVisible());
		}
		
		@Override
		public void displayMainMenu() {
			main_menu.setVisible(true, true);
			Objective obj = null;
			ObjectiveEvent evt = null;
			if(obj_area!=null) {
				obj = obj_area;
				obj_area = null;				
			}
			
			if(command_mode!=COMMAND_NONE) clearCommandMode();			
			
			if(group_select!=null){
				if(group_select instanceof Objective){
					obj = (Objective)group_select;					
				}
				else if(group_select instanceof ObjectiveEvent){
					evt = (ObjectiveEvent)group_select;
				}
				group_select = null;
			}
			else if(dialog_event!=null){
				evt = dialog_event;
				if(evt instanceof AutoScrollEvent){
					((AutoScrollEvent)evt).setScroll(scroll_x, scroll_y);
					((AutoScrollEvent)evt).setZoom(zoom);
				}
				dialog_event = null;
			}
			
			if(game_mode==GAME_MODE_MISSION){
				main_menu.selectSubMenu(MainMenu.MENU_CODE_GAME, 0);
			}
			else if(obj!=null){
				int id = obj.getId();
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_MAP_SETTINGS, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJECTIVES, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJECTIVE+"="+id, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJ_TYPE_ARGS+"="+id, 0);
			}
			else if(evt!=null){
				int e_id = evt.getId();
				int o_id = evt.getParent().getId();
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_MAP_SETTINGS, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJECTIVES, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJECTIVE+"="+o_id, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJ_EVENTS+"="+o_id, 0);
				main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJ_EVENT+"="+e_id, 0);
			}
			
			//if(is_menu_visible) closeMenu();
		}
		
		public void selectLastSavedGroup(){
			if(saved_groups.size()>0){
				if(selected_item_list==null || selected_item_list.size()==0){
					selected_item_list = saved_groups.get(saved_groups.size()-1);
				}
				else {
					int si = saved_groups.indexOf(selected_item_list);
					si--;
					if(si<0) si = saved_groups.size()-1;
					selected_item_list = saved_groups.get(si);		
				}
				
				Iterator<Item> it = selected_item_list.iterator();
				if(it.hasNext()) displayMenu(it.next());
				
				setCommandMode(COMMAND_MOVE);
			}			
		}
		
		public void onTopFilterChanged(int tf){
			/*
			if(tf != GameMenu.MENU_TF_COMMAND) {
				clearCommandMode();				
			}
			else {
				selectLastSavedGroup();
			}
			*/
		}

		@Override
		public void onChangeBlock(int level, int x, int y) {
			if(is_menu_visible && x==mx && y==my) displayMenu(x,y,true,false);
		}
		
		private String getSaveDir() {
			return (game_mode==GAME_MODE_MAP_EDITOR)?FileManager.DIR_MAP:FileManager.DIR_GAME;
		}

		@Override
		public void onDialogBoxItemSelected(DialogBox src, JSONObject item) throws JSONException {
			String code = item.getString(DialogBox.JSON_CODE);
			Log.d("MAINMENU", "ITEM SELECTED: "+code);
			
			if(code.startsWith(MainMenu.MENU_CODE_SFILE) || code.startsWith(MainMenu.MENU_CODE_LFILE) || code.startsWith(MainMenu.MENU_CODE_CMFILE) || code.startsWith(MainMenu.MENU_CODE_MSFILE) || code.startsWith(MainMenu.MENU_CODE_SMFILE)){
				String fn = code.substring(code.indexOf('=')+1);
				boolean is_selected = main_menu.isSelected(item);
				Log.d("BITMAP", "IS_SELECTED:"+is_selected);
				if(!is_selected && !fn.equals(MainMenu.MENU_CODE_NEW_ENTRY)){
					Bitmap background = null;
					
					boolean is_mission = code.startsWith(MainMenu.MENU_CODE_MSFILE);
					boolean is_sm      = code.startsWith(MainMenu.MENU_CODE_SMFILE);
					
					if(!is_mission && !is_sm){
						String png_fn = file.getDir((code.startsWith(MainMenu.MENU_CODE_CMFILE)?FileManager.DIR_MAP:getSaveDir())).getAbsolutePath()+'/'+file.getFileName(fn,FileManager.EXT_PNG);
						Log.d("BITMAP", "FILE:"+png_fn);
						background = BitmapFactory.decodeFile(png_fn);
					}
					else {
						try {
							background = BitmapFactory.decodeStream(
								SceneView.this.getContext().getAssets().open(
									(is_mission?FileManager.DIR_MISSION:FileManager.DIR_MAP)+'/'+file.getFileName(fn,FileManager.EXT_PNG)
								)
							);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					Log.d("BITMAP", "BG:"+background);
					main_menu.setBackground(background);
					selected_filename = fn;
					selected_description = item.optString(DialogBox.JSON_DESCRIPTION, null);
					selected_mission_starter = item.optString(DialogBox.JSON_MISSION_STARTER, null);
					selected_is_asset = is_mission || is_sm;
					selected_is_mission = is_mission;;
				}
				
				if(code.startsWith(MainMenu.MENU_CODE_SFILE)){
					String title;
					boolean new_entry = fn.equals(MainMenu.MENU_CODE_NEW_ENTRY);
					final long ct = System.currentTimeMillis();
					final String new_fn;
					if(new_entry){
						SimpleDateFormat df = new SimpleDateFormat("MMM dd. HH:mm");
						
						new_fn = "S"+ct;
						title = df.format(ct)+(saves_in_last_minute>1?(" ("+saves_in_last_minute+")"):"");
					}
					else {
						title = item.getString(DialogBox.JSON_TITLE);
						new_fn = fn;
					}
					
					selected_file_title = title;

					if(new_entry) {
						activity.showEditTextInput(title, true, new EditTextListener(){
							@Override
							public void OnTextEntered(final String text) {
								if(last_save_at/60000 == ct/60000){
									saves_in_last_minute++;
								}
								else {
									saves_in_last_minute = 1;			
								}
								last_save_at = ct;
								
								selected_filename = null;
								main_menu.close();
								new Thread(){
									@Override
									public void run(){
										try {
											synchronized(FILE_SEMAFOR){
												saveing = true;
												if(!file.save(getSaveDir(), new_fn, text, scene.getDescription(), game_mode==GAME_MODE_MAP_EDITOR)){
													SceneView.this.post(new Runnable(){
														@Override
														public void run() {
															Toast.makeText(getContext(), getString(R.string.save_error), Toast.LENGTH_LONG).show();
														}
													});
												}
											}
										}
										finally {
											saveing = false;
										}
									}
								}.start();
							}
						});
					}
					else if (is_selected) {
						item.put(DialogBox.JSON_CLEARCHILDREN, true);
						item.put(DialogBox.JSON_CHILDREN, true);
					}
				}
				else {
					if (is_selected) {
						item.put(DialogBox.JSON_CLEARCHILDREN, true);
						item.put(DialogBox.JSON_CHILDREN, true);
						
						if (item.has(DialogBox.JSON_CHILDREN)) main_menu.selectSubMenu(code,0);
						else main_menu.refreshMenu();
					}
				}
			}
			else if(code.equals(MainMenu.MENU_CODE_EXIT)){
				//SceneView.this.activity.exit();
				//SceneView.this.clearInstance();
				shutDown();
			}
			else if(code.equals(MainMenu.MENU_CODE_RESTART)){
				restartScene(false);
			}
			else if(code.equals(MainMenu.MENU_CODE_END)){
				main_menu.selectRoot(); //.selectParent();
				displayMainMenu();
				clear_scene = true;
			}
			else if(code.equals(MainMenu.MENU_CODE_CANCEL)){
				main_menu.selectParent();
			}
			else if(code.equals(MainMenu.MENU_CODE_RENAME)){
				activity.showEditTextInput(main_menu.getDialogTitle(), true, new EditTextListener(){
					@Override
					public void OnTextEntered(String text) {
						if(file.rename(getSaveDir(), selected_filename, text)){
							main_menu.setTitle(text);
							main_menu.invalidateParent();
							selected_file_title = text;
						}
					}
				});
			}
			else if(code.equals(MainMenu.MENU_CODE_OVERWRITE_SELECTED)){
				closeMainMenu();
				new Thread(){
					@Override
					public void run(){
						try {
							synchronized(FILE_SEMAFOR){
								saveing = true;
								
								if(!file.save(getSaveDir(), selected_filename, selected_file_title, scene.getDescription(), game_mode==GAME_MODE_MAP_EDITOR)){
									SceneView.this.post(new Runnable(){
										@Override
										public void run() {
											Toast.makeText(getContext(), getString(R.string.save_error), Toast.LENGTH_LONG).show();
										}
									});
								}
							}
						}
						finally {
							saveing = false;
						}
					}
				}.start();
			}
			else if(code.equals(MainMenu.MENU_CODE_LOAD_SELECTED)){
				new Thread(){
					@Override
					public void run(){
						try {
							synchronized(FILE_SEMAFOR){
								loading = true;
								if(!file.load(getSaveDir(), selected_filename, false, false)){
									SceneView.this.post(new Runnable(){
										@Override
										public void run() {
											Toast.makeText(getContext(), getString(R.string.load_error), Toast.LENGTH_LONG).show();
										}
									});
								}
								closeMainMenu();
							}
						}
						finally {
							loading = false;
						}
					}
				}.start();
			}
			else if(code.equals(MainMenu.MENU_CODE_START_SELECTED)){
				new Thread(){
					@Override
					public void run(){
						try {
							synchronized(FILE_SEMAFOR){
								loading = true;
								if(!file.load(selected_is_mission?FileManager.DIR_MISSION:FileManager.DIR_MAP, selected_filename, selected_is_asset, selected_is_mission)){
									SceneView.this.post(new Runnable(){
										@Override
										public void run() {
											Toast.makeText(getContext(), getString(R.string.load_error), Toast.LENGTH_LONG).show();
										}
									});
								}
								closeMainMenu();
							}
						}
						finally {
							loading = false;
						}
					}
				}.start();
			}
			else if(code.startsWith(MainMenu.MENU_CODE_OBJECTIVE)) {
				if(game_mode!=GAME_MODE_MAP_EDITOR){
					closeMainMenu();
					int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					Objective objective = scene.getObjectiveById(id);
					if(objective!=null && objective.getDescription()!=null){
						cancelNextTap();
						showMessage(new Message(objective.getDescription(),0,true,null));
					}
				}
			}
			else if (code.equals(MainMenu.MENU_CODE_MAP_EDITOR)){
				closeMainMenu();
				if(game_mode==GAME_MODE_TEST_MAP){
					endTestMap();
				}
				else {
					cinematic = null;
					event = null;
					scene = new Scene(this, MAP_LEVELS, 16, 16, Scene.MAP_TYPE_SOIL, null, null, null, false, false);
					setGameMode(GAME_MODE_MAP_EDITOR);
				}
			}
			else if(src instanceof EditorMenu) {
				if(code.equals(EditorMenu.MENU_CODE_EXIT_EDITOR)){
					scene = null;
					setGameMode(GAME_MODE_MISSION);
				}
				else if (code.equals(EditorMenu.MENU_CODE_TEST_MAP)){
					startTestMap();
					closeMainMenu();
					//main_menu.close();
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_REMOVE_OBJECTIVE)){
					int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					scene.removeObjective(id);
					main_menu.selectParent();				
					main_menu.refreshMenu();
				}
				else if(code.equals(EditorMenu.MENU_CODE_MAP_DESCRIPTION)){
					activity.showEditTextInput(scene.getDescription(), false, new EditTextListener(){
						@Override
						public void OnTextEntered(String text) {
							scene.setDescription(text);
						}
					});
				}
				else if(code.equals(EditorMenu.MENU_CODE_CLEAR_NEXT_MAP)){
					scene.setNextMap(null, null);
					main_menu.refreshMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_NMFILE)){
					String fn = code.substring(code.indexOf('=')+1);
					scene.setNextMap(fn, item.getString(DialogBox.JSON_TITLE));
					main_menu.selectParent();				
					main_menu.refreshMenu();
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_TYPE)){
					int type = Integer.parseInt(code.substring(code.indexOf('=')+1));
					int new_o_id = scene.addObjective(type);
					main_menu.selectParent();
					main_menu.refreshMenu();
					main_menu.selectSubMenu(MainMenu.MENU_CODE_OBJECTIVE+"="+new_o_id, 0);				
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_TITLE)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
					if(o!=null){
						activity.showEditTextInput(o.getTitle(), true, new EditTextListener(){
							@Override
							public void OnTextEntered(String text) {
								if(text.trim().equals("")) text = null;
								o.setTitle(text);
								//main_menu.refreshMenuWithParent(MainMenu.MENU_CODE_OBJECTIVE+"="+id);
								main_menu.setTitle(o.getDisplayableTitle());
								main_menu.invalidateParent();
							}
						});
					}
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_DESC)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
					if(o!=null){
						activity.showEditTextInput(o.getDescription(), false, new EditTextListener(){
							@Override
							public void OnTextEntered(String text) {
								o.setDescription(text);
							}
						});
					}
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_TEXT)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final ObjectiveEvent event = scene.getObjectiveEventById(id);
					if(event!=null && event instanceof OnSetTextListener){
						final OnSetTextListener text_event = (OnSetTextListener)event;
						activity.showEditTextInput(text_event.getText(), false, new EditTextListener(){
							@Override
							public void OnTextEntered(String text) {
								text_event.setText(text);
								main_menu.setTitle(text_event.getTitle());
								main_menu.invalidateParent();
							}
						});
					}
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_SET_VIEWPOINT)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final ObjectiveEvent event = scene.getObjectiveEventById(id);
					if(event!=null && event instanceof AutoScrollEvent){
						//((AutoScrollEvent)event).setScroll(scroll_x, scroll_y);
						//((AutoScrollEvent)event).setZoom(zoom);
						dialog_event = event;
						main_menu.close();
					}
				}
				else if (code.startsWith(EditorMenu.MENU_CODE_OBJ_ACHIEVED_DESC)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
					if(o!=null){
						activity.showEditTextInput(o.getAchievedDescription(), false, new EditTextListener(){
							@Override
							public void OnTextEntered(String text) {
								o.setAchievedDescription(text);
							}
						});
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECTED_AREA)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective){
						AreaObjective ao = (AreaObjective)o;
						obj_area = ao;
						setCommandMode(COMMAND_SELECT);
						main_menu.close();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECT_GLOBAL_AREA)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective){
						AreaObjective ao = (AreaObjective)o;
						ao.setArea(new Rect(0,0,scene.getWidth()-1,scene.getHeight()-1));
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECT_ALL_TYPE)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
					if(o!=null && o instanceof AreaObjective){
						AreaObjective ao = (AreaObjective)o;
						ao.setType(AreaObjective.ALL_TYPE);
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE_RESID)){
					String [] args = code.substring(code.indexOf('=')+1).split(",");
					int o_id = Integer.parseInt(args[0]);
					int type = Integer.parseInt(args[1]);
					final Objective o = scene.getObjectiveById(o_id);
					if(o!=null && o instanceof AreaObjective){
						AreaObjective ao = (AreaObjective)o;
						ao.setType(type);
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_TYPE)){
					String [] args = code.substring(code.indexOf('=')+1).split(",");
					int o_id = Integer.parseInt(args[0]);
					int type = Integer.parseInt(args[1]);
					int new_e_id = scene.addObjectiveEvent(o_id, type);
					main_menu.selectParent();
					main_menu.refreshMenu();
					main_menu.selectSubMenu(EditorMenu.MENU_CODE_OBJ_EVENT+"="+new_e_id, 0);
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_REMOVE_EVENT)){
					int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					scene.removeObjectiveEvent(id);
					main_menu.selectParent();
					main_menu.refreshMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_TARGET)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int event_id  = Integer.parseInt(ids[0]);
					
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof StarterEvent){
						int target_id = Integer.parseInt(ids[1]);
						Objective target = null;
						if(target_id>-1) target = scene.getObjectiveById(target_id);
						((StarterEvent)event).setTarget(target);
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_PREREQUISITE)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int objective_id = Integer.parseInt(ids[0]);
					
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null && objective instanceof MergeObjective){
						int target_id = Integer.parseInt(ids[1]);
						Objective target = scene.getObjectiveById(target_id);
						if(target!=null) ((MergeObjective)objective).addPrerequisite(target);
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_REMOVE_PREREQUISITE)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int objective_id = Integer.parseInt(ids[0]);
					
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null && objective instanceof MergeObjective){
						int target_id = Integer.parseInt(ids[1]);
						Objective target = scene.getObjectiveById(target_id);
						if(target!=null) ((MergeObjective)objective).removePrerequisite(target);
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_SELECT_TARGETS)){
					final int event_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof GroupSelect){
						((GroupSelect)event).setGroupSelectFlag(0);
						group_select = (GroupSelect)event;
						main_menu.close();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_SELECT_DESTINATION)){
					final int event_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof GroupSelect){
						((GroupSelect)event).setGroupSelectFlag(1);
						group_select = (GroupSelect)event;
						main_menu.close();
					}				
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECT_GROUP)){
					final int objective_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null && objective instanceof GroupSelect){
						group_select = (GroupSelect)objective;
						main_menu.close();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_ADD_MENU_ITEM)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int event_id  = Integer.parseInt(ids[0]);
					
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof MenuItemEvent){
						((MenuItemEvent)event).addItemToList(Integer.parseInt(ids[1]));
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_REMOVE_MENU_ITEM)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int event_id  = Integer.parseInt(ids[0]);
					
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof MenuItemEvent){
						((MenuItemEvent)event).removeItemFromList(Integer.parseInt(ids[1]));
						main_menu.selectParent();
						main_menu.refreshMenu();
					}
				}
			}
		}
		
		private void setGameMode(int game_mode){
			clearCommandMode();
			this.game_mode = game_mode;
			DialogBox db = getCorrectMenu();
			db.setMainMenuListener(this);
			db.selectRoot();
			db.setVisible(main_menu.isVisible(), true);
			main_menu = db;
			if(scene!=null) {
				GameMenu.initCommandItems(scene);
				scene.onChangeGameMode(game_mode);
			}
		}

		@Override
		public int getListState(DialogBox src, String code) {
			int pi = code.indexOf('=');			
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				int state = 0;
				code = code.substring(0, pi);
				
				if(src instanceof EditorMenu){
					if(code.equals(EditorMenu.MENU_CODE_OBJ_PLAYER)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null) state = o.getPlayer();
					}
					if(code.equals(EditorMenu.MENU_CODE_OBJ_RESOURCE_TYPE)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof ResourceObjective){
							state = ((ResourceObjective)o).getResource();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_STAT_TYPE)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof StatisticsObjective){
							state = ((StatisticsObjective)o).getStatId();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_MISSILES_ABOVE)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							state = ((AreaObjective)o).getOnlyMissilesAbove();
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_STARTER_ACTION)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof StarterEvent) state = ((StarterEvent)e).getAction();
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_FULFILL_MODE)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) state = ((CommandMoveEvent)e).getFulfillMode();
					}
				}
				return state;
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_TYPE)){
				return scene.getMapType();
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_WIDTH)){
				int map_width = scene.getWidth();
				int state = 0;
				while(map_width>8) {
					state++;
					map_width >>= 1;
				}
				return state;
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_HEIGHT)){
				int map_height = scene.getHeight();
				int state = 0;
				while(map_height>8) {
					state++;
					map_height >>= 1;
				}
				return state;
			}
			else {
				SharedPreferences pref = getPreferences();
				//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
				return pref.getInt(code, 0);
			}
		}

		@Override
		public void onToggleList(DialogBox src, String code, int min, int max) {
			int state = (getListState(src, code) + 1) % max;
			if(state<min) state = min;
			
			int pi = code.indexOf('=');			
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				code = code.substring(0, pi);
				
				if(src instanceof EditorMenu){
					if(code.equals(EditorMenu.MENU_CODE_OBJ_PLAYER)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null) o.setPlayer(state);
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_RESOURCE_TYPE)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof ResourceObjective){
							((ResourceObjective)o).setResource(state);
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_STAT_TYPE)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof StatisticsObjective){
							((StatisticsObjective)o).setStatId(state);
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_MISSILES_ABOVE)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							((AreaObjective)o).setOnlyMissilesAbove(state);
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_STARTER_ACTION)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof StarterEvent) ((StarterEvent)e).setAction(state);
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_FULFILL_MODE)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) ((CommandMoveEvent)e).setFulfillMode(state);
					}
				}
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_TYPE)){
				scene.setMapType(state);
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_WIDTH)){
				new_map_width  = 8 << state;
				new_map_height = scene.getHeight();
				map_size_changed = true;
				//scene.setMapSize(map_width, scene.getHeight());
			}
			else if(code.equals(EditorMenu.MENU_CODE_MAP_HEIGHT)){
				new_map_width  = scene.getWidth();
				new_map_height = 8 << state;
				map_size_changed = true;
				//scene.setMapSize(scene.getWidth(), map_height);
			}
			else {
				SharedPreferences pref = getPreferences();
				//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
				pref.edit().putInt(code, state).commit();
				
				if(code.equals(MainMenu.MENU_CODE_MM_SIZE)){
					if(state == 5) state = 8;
					else if(state == 6) state = 12;
					minimap_size = state;
				}
			}
		}

		@Override
		public boolean getCheckboxState(DialogBox src, String code) {
			int pi = code.indexOf('=');
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				code = code.substring(0, pi);
				
				if(src instanceof EditorMenu) {
					if(code.equals(EditorMenu.MENU_CODE_OBJ_STATUS)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null) return o.isEnabled();
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_COUNT_FROM_START)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof StatisticsObjective) return ((StatisticsObjective)o).isCountFromStart();
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_POISONOUS)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							return((AreaObjective)o).getOnlyPoisonous();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_SPIKY)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							return ((AreaObjective)o).getOnlySpiky();
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_ATTACK_DESTINATION)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) {
							return ((CommandMoveEvent)e).isAttackDest();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_AUTO_FOCUS)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) {
							return ((CommandMoveEvent)e).isAutoFocus();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_MI_SET_ENABLED)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof MenuItemEvent) {
							return ((MenuItemEvent)e).isInEnableMode();
						}
					}
				}
				
				return false;
			}
			else {
				SharedPreferences pref = getPreferences();
				//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
				return pref.getBoolean(code, false);
			}
		}
		
		@Override
		public void onToggleCheckbox(DialogBox src, String code){
			int pi = code.indexOf('=');			
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				code = code.substring(0, pi);

				if(src instanceof EditorMenu) {
				
					if(code.equals(EditorMenu.MENU_CODE_OBJ_STATUS)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null) o.setEnabled(!o.isEnabled(), false);
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_COUNT_FROM_START)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof StatisticsObjective) ((StatisticsObjective)o).toggleCountFromStart();
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_POISONOUS)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							((AreaObjective)o).setOnlyPoisonous(!((AreaObjective)o).getOnlyPoisonous());
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_TOGGLE_SPIKY)) {
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AreaObjective) {
							((AreaObjective)o).setOnlySpiky(!((AreaObjective)o).getOnlySpiky());
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_ATTACK_DESTINATION)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) {
							CommandMoveEvent cme = (CommandMoveEvent)e;
							cme.setAttackDest(!cme.isAttackDest());
						}
					}				
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_AUTO_FOCUS)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof CommandMoveEvent) {
							CommandMoveEvent cme = (CommandMoveEvent)e;
							cme.setAutoFocus(!cme.isAutoFocus());
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_MI_SET_ENABLED)) {
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof MenuItemEvent) {
							MenuItemEvent cme = (MenuItemEvent)e;
							cme.setEnableMode(!cme.isInEnableMode());
						}
					}				
				}
			}
			else {
				SharedPreferences pref = getPreferences();
				//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
				boolean state = !pref.getBoolean(code, false);
				pref.edit().putBoolean(code, state).commit();
				if(code.equals(MainMenu.MENU_CODE_DISPLAY_DEBUG)){
					display_debug = state;
				}
				else if(code.equals(MainMenu.MENU_CODE_DISPLAY_BUG_LIFE)){
					display_bug_life = state;
				}
				else if(code.equals(MainMenu.MENU_CODE_MM_TRANSPARENCY)){
					minimap_transparency = state;
				}
			}
		}
		
		@Override
		public float getSlidebarRate(DialogBox src, String code) {
			int pi = code.indexOf('=');			
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				code = code.substring(0, pi);
				
				if(src instanceof EditorMenu){
					if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_HOURS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							return ((DelayObjective)o).getDelayHoursRate();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_MINUTES)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							return ((DelayObjective)o).getDelayMinutesRate();
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_SECONDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							return ((DelayObjective)o).getDelaySecondsRate();
						}					
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_SELECTED_NUMBER)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof CountSlider){
							return ((CountSlider)o).getCountRate();
						}					
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_AM_HUNDREDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AmountSlider){
							return ((AmountSlider)o).getAmountHundredsRate();
						}					
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_AM_THOUSANDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AmountSlider){
							return ((AmountSlider)o).getAmountThousandsRate();
						}					
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_DELAY_SECONDS)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof DelayEvent){
							return ((DelayEvent)e).getDelaySecondsRate();
						}					
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_DELAY_TICS)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof DelayEvent){
							return ((DelayEvent)e).getDelayTicsRate();
						}					
					}
				}
			}
			else if(code.equals(MainMenu.MENU_CODE_SOUND)){
				return sound.getVolumeRate();
			}
			else if(code.equals(MainMenu.MENU_CODE_MUSIC)){
				return music.getVolumeRate();
			}
			else if(code.equals(MainMenu.MENU_CODE_BRIGHTNESS)){
				return brightness;
			}
			
			return 0;
		}

		@Override
		public void onSetSlidebarRate(DialogBox src, String code, float rate) {
			Log.d("MAINMENU", "SET SLIDEBAR RATE: "+code+", V="+rate);
			float prev = getSlidebarRate(src, code);
			
			//if(prev<0.2 && rate<0.2) rate = 0;
			//else if(prev>0.8 && rate>0.8) rate = 1;
			
			int pi = code.indexOf('=');
			if(pi>-1){
				int id = Integer.parseInt(code.substring(pi+1));
				String full_code = code;
				code = code.substring(0, pi);
				
				if(src instanceof EditorMenu){
					if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_HOURS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							((DelayObjective)o).setDelayHoursRate(rate);
							main_menu.refreshTitleFor(full_code,((DelayObjective)o).getHoursTitle(SceneView.this.getResources()));
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_MINUTES)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							((DelayObjective)o).setDelayMinutesRate(rate);
							main_menu.refreshTitleFor(full_code,((DelayObjective)o).getMinutesTitle(SceneView.this.getResources()));
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_DELAY_SECONDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof DelayObjective){
							((DelayObjective)o).setDelaySecondsRate(rate);
							main_menu.refreshTitleFor(full_code,((DelayObjective)o).getSecondsTitle(SceneView.this.getResources()));
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_SELECTED_NUMBER)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof CountSlider){
							((CountSlider)o).setCountRate(rate);
							main_menu.refreshTitleFor(full_code,((CountSlider)o).getCountSliderTitle());
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_AM_HUNDREDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AmountSlider){
							((AmountSlider)o).setAmountHundredsRate(rate);
							main_menu.refreshTitleFor(EditorMenu.MENU_CODE_OBJ_AM_TITLE,((AmountSlider)o).getCountSliderTitle());
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_AM_THOUSANDS)){
						Objective o = scene.getObjectiveById(id);
						if(o!=null && o instanceof AmountSlider){
							((AmountSlider)o).setAmountThousandsRate(rate);
							main_menu.refreshTitleFor(EditorMenu.MENU_CODE_OBJ_AM_TITLE,((AmountSlider)o).getCountSliderTitle());
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_DELAY_SECONDS)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof DelayEvent){
							((DelayEvent)e).setDelaySecondsRate(rate);
							main_menu.refreshTitleFor(full_code,((DelayEvent)e).getSecondsTitle(SceneView.this.getResources()));
						}
					}
					else if(code.equals(EditorMenu.MENU_CODE_OBJ_EVT_DELAY_TICS)){
						ObjectiveEvent e = scene.getObjectiveEventById(id);
						if(e!=null && e instanceof DelayEvent){
							((DelayEvent)e).setDelayTicsRate(rate);
							main_menu.refreshTitleFor(full_code,((DelayEvent)e).getTicsTitle(SceneView.this.getResources()));
						}
					}
				}
			}
			else if(code.equals(MainMenu.MENU_CODE_SOUND)){
				sound.setVolumeRate(rate);
				long ct = System.currentTimeMillis();
				if(ct-svsound_played_at > 250){
					sound.play(R.raw.damage,null);
					svsound_played_at = ct;
				}
			}
			else if(code.equals(MainMenu.MENU_CODE_MUSIC)){
				music.setVolumeRate(rate);
			}
			else if(code.equals(MainMenu.MENU_CODE_BRIGHTNESS)){
				SharedPreferences pref = getPreferences();
				//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SceneView.this.getContext());
				if(pref.edit().putFloat(PREF_KEY_BRIGHTNESS, rate).commit()){
					brightness = rate;
					Entity.setBrightness(brightness);
					Entity.clearCache();
				}
			}
		}

		@Override
		public JSONArray getChildrenFor(DialogBox src, String code) {
			Log.d("MENU","GET CHILDREN FOR: "+code);
			JSONArray items = new JSONArray();
			if(code.equals(MainMenu.MENU_CODE_SAVE)){
				JSONObject o = new JSONObject();
				try {
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_SFILE+"="+MainMenu.MENU_CODE_NEW_ENTRY);
					o.put(DialogBox.JSON_TITLE, getContext().getString(R.string.new_entry));
					o.put(DialogBox.JSON_PADDING, 30);
					o.put(DialogBox.JSON_SELECTABLE, true);
					items.put(o);
				} catch (JSONException e) {
					e.printStackTrace();
				}
								
				items = file.getFileList(getSaveDir(), MainMenu.MENU_CODE_SFILE, null, items, true, false, false);
				return items;
			}
			else if(code.equals(MainMenu.MENU_CODE_NEW)){
				items = new JSONArray();
				try {
					JSONObject o = new JSONObject();
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_MISSION);
					o.put(DialogBox.JSON_CHILDREN, true);
					items.put(o);
					o = new JSONObject();
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_CUSTOM_MAP);
					o.put(DialogBox.JSON_CHILDREN, true);
					items.put(o);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return items;				
			}
			else if(code.equals(MainMenu.MENU_CODE_GAME)){
				items = new JSONArray();
				try {
					JSONObject o = new JSONObject();
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_NEW);
					o.put(DialogBox.JSON_CHILDREN, true);
					items.put(o);
					if(scene!=null){
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_SAVE);
						o.put(DialogBox.JSON_CHILDREN, true);
						items.put(o);
					}
					o = new JSONObject();
					o.put(DialogBox.JSON_CHILDREN, true);					
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_LOAD);
					items.put(o);
					if(scene!=null){
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_RESTART);
						o.put(DialogBox.JSON_SURE_DIALOG, getString(R.string.sure_dialog_restart));						
						items.put(o);
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_OBJECTIVES);
						o.put(DialogBox.JSON_CHILDREN, true);					
						items.put(o);
						/*
						o = new JSONObject();						
						o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_END);
						o.put(DialogBox.JSON_SURE_DIALOG, getString(R.string.sure_dialog_end));
						items.put(o);
						*/
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return items;
				/*
				children:[
							{code:"NEW",
								children:[
									{code:"MISSION", children:true},
									{code:"CUSTOM_MAP", children:true}
								]
							},
							{code:"SAVE", children:true},
							{code:"LOAD", children:true},
							{code:"RESTART"},
							{code:"END"}
						]
				*/
			}
			else if(code.equals(MainMenu.MENU_CODE_LOAD)){
				items = file.getFileList(getSaveDir(), MainMenu.MENU_CODE_LFILE, null, null, true, false, false);
				if(items!=null){
					try {
						items.getJSONObject(0).put(DialogBox.JSON_SELECTED, true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return items;
			}
			else if(code.equals(MainMenu.MENU_CODE_CUSTOM_MAP)){
				items = file.getFileList(FileManager.DIR_MAP, MainMenu.MENU_CODE_CMFILE, null, null, true, false, false);
				if(items!=null){
					try {
						items.getJSONObject(0).put(DialogBox.JSON_SELECTED, true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return items;				
			}
			else if(code.equals(MainMenu.MENU_CODE_MISSION)){
				if(QUICK_START){
					items = file.getFileList(FileManager.DIR_MAP+"/mission1", MainMenu.MENU_CODE_SMFILE, "mission1/",null, true, true, true);
				}
				else {
					items = file.getFileList(FileManager.DIR_MISSION, MainMenu.MENU_CODE_MSFILE, null, null, true, true, false);
				}
				
				if(items!=null && items.length()>0){
					try {
						items.getJSONObject(0).put(DialogBox.JSON_SELECTED, true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return items;
			}
			else if(code.startsWith(MainMenu.MENU_CODE_START_MISSION)){
				String name = code.substring(code.indexOf('=')+1);
				items = file.getFileList(FileManager.DIR_MAP+"/"+name, MainMenu.MENU_CODE_SMFILE, name+'/',null, true, true, true);
				if(items!=null && items.length()>0){
					try {
						items.getJSONObject(0).put(DialogBox.JSON_SELECTED, true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return items;
			}
			else if(code.equals(MainMenu.MENU_CODE_OBJECTIVES)){
				items = scene!=null?scene.getObjectivesForMenu():null;
				return items;
			}
			else if(code.startsWith(MainMenu.MENU_CODE_OBJECTIVE)){
				try {
					int objective_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					return scene.getObjectiveDetailsForMenu(objective_id);
				} catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
			else if(code.startsWith(MainMenu.MENU_CODE_CMFILE) || code.startsWith(MainMenu.MENU_CODE_MSFILE)|| code.startsWith(MainMenu.MENU_CODE_SMFILE)){
				String fn = code.substring(code.indexOf('=')+1);
								
				try {
					JSONObject item = new JSONObject();
					if(code.startsWith(MainMenu.MENU_CODE_MSFILE)){
						SharedPreferences prefs = getPreferences();
						//Log.e("MISSION", "SELECTED FILE: "+fn);
						Log.e("MISSION", "PREF GET MISSION: "+PREF_KEY_STARTED_MISSION_PREFIX+fn);
						if(!prefs.getBoolean(PREF_KEY_STARTED_MISSION_PREFIX+fn, false)) {
							selected_filename   = fn+"/"+selected_mission_starter;
							selected_is_asset   = true;
							selected_is_mission = false;
							//Log.e("MISSION", "SELECTED FILE name: "+selected_filename);
							item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_START_SELECTED);
						}
						else {
							item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_START_MISSION+"="+fn);
							item.put(MainMenu.JSON_CHILDREN, true);
						}
					}
					else {
						item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_START_SELECTED);
					}
					items.put(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				try {
					if(selected_description!=null){
						int i=0;
						for(String word : selected_description.split("\n")){
							JSONObject item = new JSONObject();
							item.put(MainMenu.JSON_CODE, "DESC-"+i);
							item.put(MainMenu.JSON_TITLE, word);
							item.put(DialogBox.JSON_TEXT_SIZE, 14);
							items.put(item);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
								
				try {
					JSONObject item = new JSONObject();
					item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_CANCEL);
					item.put(DialogBox.JSON_PADDING, 50);
					items.put(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
			else if(code.startsWith(MainMenu.MENU_CODE_SFILE) || code.startsWith(MainMenu.MENU_CODE_LFILE)){
				String fn = code.substring(code.indexOf('=')+1);
				
				
				JSONObject item = new JSONObject();
				try {
					item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_RENAME);
					item.put(DialogBox.JSON_PADDING, 25);
					items.put(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
				try {
					item = new JSONObject();
					if(code.startsWith(MainMenu.MENU_CODE_SFILE)){
						item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_OVERWRITE_SELECTED);
					}
					else if(code.startsWith(MainMenu.MENU_CODE_LFILE)){
						item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_LOAD_SELECTED);
					}
					item.put(DialogBox.JSON_PADDING, 25);
					items.put(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				try {
					item = new JSONObject();
					item.put(MainMenu.JSON_CODE, MainMenu.MENU_CODE_CANCEL);
					item.put(DialogBox.JSON_PADDING, 25);
					items.put(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else if(src instanceof EditorMenu) {
				if (code.equals(EditorMenu.MENU_CODE_ADD_OBJECTIVE)){
					return scene.getObjectiveTypesForMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVENTS)){
					try {
						int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
						Objective objective = scene.getObjectiveById(id);
						if(objective!=null) return objective.getEventsForMenu();
					} catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVENT)){
					try {
						int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
						ObjectiveEvent event = scene.getObjectiveEventById(id);
						if(event!=null) return event.getEventDetailsForMenu();
					} catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE)){
					final int id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					final Objective o = scene.getObjectiveById(id);
					if(o!=null && o instanceof AreaObjective){
						AreaObjective ao = (AreaObjective)o;
						return ao.getAllTypesForMenu();
					}
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_TYPE_ARGS)){
					int objective_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null) return objective.getTypeSettingsForMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_ADD_EVENT)){
					int objective_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null) return objective.getEventTypesForMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_SELECT_MENU_ITEMS)){
					int event_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof MenuItemEvent) return ((MenuItemEvent)event).getSelectedMenuItemsForMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_MENU_ITEM_LIST)){
					int event_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof MenuItemEvent) return ((MenuItemEvent)event).getAllMenuItemsForMenu();
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_MI_ITEM_DETAILS)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int event_id = Integer.parseInt(ids[0]);
					ObjectiveEvent event = scene.getObjectiveEventById(event_id);
					if(event!=null && event instanceof MenuItemEvent) return ((MenuItemEvent)event).getRemoveItemForMenu(Integer.parseInt(ids[1]));
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_EVT_TARGET)){
					int event_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					return scene.getTargetsForMenu(EditorMenu.MENU_CODE_TARGET, event_id, true);
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_ADD_PREREQUISITE)){
					int objective_id = Integer.parseInt(code.substring(code.indexOf('=')+1));
					return scene.getTargetsForMenu(EditorMenu.MENU_CODE_PREREQUISITE, objective_id, false);
				}
				else if(code.startsWith(EditorMenu.MENU_CODE_OBJ_PREREQUISITE)){
					String [] ids = code.substring(code.indexOf('=')+1).split(",");
					int objective_id = Integer.parseInt(ids[0]);
					
					Objective objective = scene.getObjectiveById(objective_id);
					if(objective!=null && objective instanceof MergeObjective){
						int prereq_id = Integer.parseInt(ids[1]);
						Objective prereq = scene.getObjectiveById(prereq_id);
						return ((MergeObjective)objective).getPrerequisiteDetailsForMenu(prereq_id);
					}
				}
				else if(code.equals(EditorMenu.MENU_CODE_NEXT_MAP)){
					JSONObject item = new JSONObject();
					try {
						item.put(EditorMenu.JSON_CODE, "-");
						item.put(EditorMenu.JSON_TITLE, getString(R.string.next_map));
						item.put(EditorMenu.JSON_ENABLED, false);
						items.put(item);
						
						String nmn = scene.getNextMapName();
						item = new JSONObject();
						item.put(EditorMenu.JSON_CODE, "-");
						item.put(EditorMenu.JSON_TITLE, (nmn==null)?"-":nmn);
						item.put(DialogBox.JSON_PADDING, 25);
						item.put(EditorMenu.JSON_ENABLED, false);
						items.put(item);
						
						item = new JSONObject();
						item.put(EditorMenu.JSON_CODE, EditorMenu.MENU_CODE_SELECT_NEXT_MAP);
						item.put(EditorMenu.JSON_TITLE, getString(R.string.select_next_map));
						item.put(DialogBox.JSON_PADDING, 25);
						item.put(EditorMenu.JSON_CHILDREN, true);
						items.put(item);
						
						item = new JSONObject();
						item.put(EditorMenu.JSON_CODE, EditorMenu.MENU_CODE_CLEAR_NEXT_MAP);
						item.put(EditorMenu.JSON_TITLE, getString(R.string.clear_next_map));
						item.put(DialogBox.JSON_PADDING, 25);
						items.put(item);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else if(code.equals(EditorMenu.MENU_CODE_SELECT_NEXT_MAP)){
					items = file.getFileList(FileManager.DIR_MAP, EditorMenu.MENU_CODE_NMFILE, null, items, false, false, false);
				}
			}			
			
			return items;
		}
		
		private void restartScene(boolean next_scene){
			if(game_mode==GAME_MODE_TEST_MAP){
				restartTestMap();
			}
			else {
				Scene scene = this.scene;
				if(scene!=null){
					final String fn = next_scene?scene.getNextMapFile():scene.getCurrentMapFile();
					final boolean is_asset = scene.isFromAsset();
					final boolean is_mission = scene.isMission();
					if(fn!=null){
						closeMainMenu();
						new Thread(){
							@Override
							public void run(){
								try {
									synchronized(FILE_SEMAFOR){
										loading = true;
										file.load(FileManager.DIR_MAP, fn, is_asset, is_mission);
									}
								}
								finally {
									loading = false;
								}
							}
						}.start();
					}
					else {
						clear_scene = true;
					}
				}
			}
		}
		
		public void loadScene(JSONObject data, String description, boolean is_asset, boolean is_mission) throws JSONException {
			is_menu_visible = false;
			is_placer_visible = false;
			cinematic = null;
			event = null;
			
			tic = data.getLong(JSON_TIC);
			setZoom(data.getDouble(JSON_ZOOM));
			setScrollX(data.getInt(JSON_SCROLL_X));
			setScrollY(data.getInt(JSON_SCROLL_Y));
			HashMap<Integer,Entity> tmp_ids = new HashMap<Integer,Entity>();
			
			ResIdConverter rc = new ResIdConverter(data.getJSONArray(JSON_RESIDS));
			scene = new Scene(this, data.getJSONObject(JSON_SCENE), tmp_ids, rc, description, is_asset, is_mission);
			//setGameMode(data.getInt(JSON_GAME_MODE));
			
			if(!data.isNull(JSON_SELECTED_ITEM_LIST)){
				HashSet<Item> selected_item_list = new HashSet<Item>();
				JSONArray json_sil = data.getJSONArray(JSON_SELECTED_ITEM_LIST);
				for(int i=0; i<json_sil.length(); i++){
					Entity e = tmp_ids.get(json_sil.get(i));
					if(e!=null && e instanceof Item) selected_item_list.add((Item)e);
				}
				this.selected_item_list = selected_item_list;
			}
			else {
				selected_item_list = null;
			}
			
			ArrayList<HashSet<Item>> saved_groups = new ArrayList<HashSet<Item>>(MAX_SAVED_GROUPS);
			if(!data.isNull(JSON_SAVED_GROUPS)){
				JSONArray json_sg = data.getJSONArray(JSON_SAVED_GROUPS);
				for(int i=0; i<json_sg.length(); i++){
					HashSet<Item> selected_item_list = new HashSet<Item>();
					JSONArray json_sil = json_sg.getJSONArray(i);
					for(int j=0; j<json_sil.length(); j++){
						Entity e = tmp_ids.get(json_sil.get(j));
						if(e!=null && e instanceof Item) selected_item_list.add((Item)e);
					}
					saved_groups.add(selected_item_list);
				}
			}
			this.saved_groups = saved_groups;
			
			setGameMode(game_mode); // triggers onChangeGameMode for appear events
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject o = new JSONObject();
			o.put(JSON_SCROLL_X, scroll_x);
			o.put(JSON_SCROLL_Y, scroll_y);
			o.put(JSON_ZOOM, zoom);
			o.put(JSON_TIC, tic);
			//o.put(JSON_GAME_MODE, game_mode);
			
			ResIdConverter rc = new ResIdConverter();
			HashMap<Entity,Integer> tmp_ids = new HashMap<Entity,Integer>();
			o.put(JSON_SCENE, scene.toJSON(tmp_ids, rc));
			o.put(JSON_RESIDS, rc.toJSONArray());
			
			ArrayList<HashSet<Item>> saved_groups = this.saved_groups;
			HashSet<Item> selected_item_list = this.selected_item_list;
			
			if(selected_item_list!=null){
				JSONArray json_sil = new JSONArray();
				for(Item i : selected_item_list){
					Integer id = tmp_ids.get(i);
					if(id!=null) json_sil.put(id);
				}
				o.put(JSON_SELECTED_ITEM_LIST, json_sil);
			}
			
			if(saved_groups!=null){
				JSONArray json_sg = new JSONArray();
				for(HashSet<Item> hsi : saved_groups){
					if(hsi!=null){
						JSONArray json_sgi = new JSONArray();
						for(Item i : hsi){
							Integer id = tmp_ids.get(i);
							if(id!=null) json_sgi.put(id);							
						}
						json_sg.put(json_sgi);
					}
				}
				o.put(JSON_SAVED_GROUPS, json_sg);
			}
			
			return o;
		}

		public void showMessage(Message message){
			if(message!=null && message.getText()!=null && !message.getText().trim().equals("")){
				Message last = messages.peek();
				if(last==null || !last.getText().equals(message.getText())) {
					messages.add(message);
				}
			}
		}

		@Override
		public void onObjectiveAchieved(Objective objective) {
			//showMessage(new Message(getScreenCenter(), (Entity.getAlignment(player, objective.getPlayer())==Entity.ALIGN_ENEMY?"Objective lost:\n":"Objective achieved:\n")+objective.,0,true,null));
			if(objective.getAchievedDescription()!=null) showMessage(new Message(objective.getAchievedDescription(),0,true,null));
		}
		
		@Override
		public void onMissionCompleted() {
			//showMessage(new Message("Mission completed",0,false,null));
			if(scene!=null){
				boolean is_mission = scene.isMission();
				String cm = scene.getCurrentMapFile();
				String nm = scene.getNextMapFile();
				if(is_mission && cm!=null){
					SharedPreferences prefs = SceneView.this.getPreferences();
					String ms = cm.substring(0, cm.indexOf('/'));
					Editor e = prefs.edit();
					e.putBoolean(PREF_KEY_STARTED_SCENE_PREFIX+cm, true);
					e.putBoolean(PREF_KEY_STARTED_MISSION_PREFIX+ms, true);
					if(nm!=null) {
						e.putBoolean(PREF_KEY_STARTED_SCENE_PREFIX+nm, true);
					}
					else e.putBoolean(PREF_KEY_MISSION_WON_PREFIX+ms, true);
					e.commit();
				}
			}
			showFinalStat(true);
		}

		@Override
		public void onMissionFailed() {
			showMessage(new Message("Mission failed",0,false,null));
			showFinalStat(false);
		}

		@Override
		public void onObjectiveEnabled(Objective objective, boolean enabled) {
			if(enabled && objective.getDescription()!=null){
				showMessage(new Message(objective.getDescription(),0,true,null));
			}
		}
		
		@Override
		public void onPopupText(String text){
			showMessage(new Message(text,0,true,null));
		}

		@Override
		public void addEventToTriggeredList(ObjectiveEvent event) {
			scene.addEventToTriggeredList(event);
		}

		@Override
		public void hookNewSceneReference(Scene scene) {
			this.scene = scene;
		}

		/*
		@Override
		public void onCinematicStart(Cinematic cinematic) {
			this.cinematic = cinematic;
		}
		*/

	}
		
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(thread.isStarted()){
			thread.init(holder, this);
			thread.setRunning(true);
			new Thread(){
				@Override
				public void run(){
					thread.run();
				}
			}.start();
		}
		else {
			thread.setRunning(true);
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    boolean retry = true;
	    thread.setRunning(false);
	    while (retry) {
	        try {
	            thread.join();
	            retry = false;
	        } catch (InterruptedException e) {
	            // we will try it again and again...
	        }
	    }
	}
}

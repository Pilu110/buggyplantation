package com.pidogames.buggyplantation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.effect.AHSVEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.Constants;
import com.pidogames.buggyplantation.interfaces.MenuListener;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

public class GameMenu implements Constants {
	HashMap<Integer, ArrayList<MenuItem>> imap;
	
	private static final int BTN_NONE = -1;
	private static final int BTN_GL = 0;
	private static final int BTN_SL = 1;
	private static final int BTN_LL = 2;
	private static final int BTN_CLOSE = 3;
	private static final int BTN_MENU  = 4;
	
	private static final int ITEM_FRAME = 0;
	private static final int BG_LEFT   = 1;
	private static final int BG_MIDDLE = 2;
	private static final int BG_RIGHT  = 3;
	private static final int BTN_CLOSE_OVER = 4;
	private static final int BTN_MENU_OVER = 5;
	private static final int BTN_LEVEL_OVER = 6;
	private static final int BTN_LEVEL_ACTIVE = 7;
	private static final int BTN_LEVEL_ENABLED = 8;
	private static final int BG_TOP_FILTER = 9;
	
	public static final int MENU_TF_NONE = 0;
	public static final int MENU_TF_BLOCK   = 10;
	public static final int MENU_TF_ITEM    = 11;
	public static final int MENU_TF_BUILD   = 12;
	public static final int MENU_TF_ACTION  = 13;
	public static final int MENU_TF_COMMAND = 14;
	
	private static final int BG_SELECTED_FRAME = 15;
	
	private static final int [] TOP_FILTERS_EDITOR = {
		MENU_TF_BLOCK,
		MENU_TF_ITEM,
		MENU_TF_BUILD,
		MENU_TF_ACTION,
		MENU_TF_COMMAND
	};
	
	private static final int [] TOP_FILTERS = {
		MENU_TF_BUILD,
		MENU_TF_ACTION,
		MENU_TF_COMMAND,
	};
	
	private static HashMap<Integer,Rect> top_filters_click_rect = new HashMap<Integer,Rect>();
	
	private static final int [] MENU_RESID = {
		R.drawable.menu_frame,
		R.drawable.menu_bg_left,
		R.drawable.menu_bg_middle,
		R.drawable.menu_bg_right,
		R.drawable.menu_btn_close_over,
		R.drawable.menu_btn_menu_over,
		R.drawable.menu_btn_level_over,
		R.drawable.menu_btn_level_active,
		R.drawable.menu_btn_level_enabled,
		R.drawable.menu_top_filter,
		R.drawable.menu_tf_block,
		R.drawable.menu_tf_item,
		R.drawable.menu_tf_build,
		R.drawable.menu_tf_action,
		R.drawable.menu_tf_command,
		R.drawable.menu_selected_frame
	};
	
	public static final Map<Integer,Integer> BUILD_TIME;
	static {
		HashMap<Integer,Integer> tempMap = new HashMap<Integer,Integer>();
		tempMap.put(R.drawable.menu_heal, 800);
		tempMap.put(R.drawable.menu_roots, 400);
		tempMap.put(R.drawable.b_stalk2_ud, 300);
		tempMap.put(R.drawable.b_tendrill_sprout_thorn_d, 200);
		tempMap.put(R.drawable.b_gall_ud, 2000);
		tempMap.put(R.drawable.b_core, 3000);
		tempMap.put(R.drawable.menu_poison, 50);
		tempMap.put(R.drawable.b_shooter4, 1000);
		tempMap.put(R.drawable.b_shooter5, 2000);
		tempMap.put(R.drawable.b_shooter6, 3000);
		tempMap.put(R.drawable.bug_wasp_eating1, 1200);
		tempMap.put(R.drawable.i_seabean, 2000);
		tempMap.put(R.drawable.i_dandelion, 1500);
		BUILD_TIME = Collections.unmodifiableMap(tempMap);
	};
	
	private static AHSVEffect selected_effect = new AHSVEffect(1.0f,1.2f,1f,1.8f, null);
	private static AHSVEffect disabled_effect = new AHSVEffect(0.5f,1f,0.8f,0.5f, null);
	private static Bitmap selected_icon;
	private static HashMap<Integer, Bitmap> disabled_icons = new HashMap<Integer, Bitmap>();
	
	private static ArrayList<MenuItem> command_items;
	private static ArrayList<MenuItem> command_bug_items;
	
	private static float scale = 0;
	
	private static Bitmap [] menu_bg = null;
	private static int last_y;
	private static int last_sw;
	private static int last_pressed = BTN_NONE;
	private float scroll_x;
	
	private static float fling_vx;
	private static int fling_dir;
	//private boolean elastic_mode;
	
	private static int top_filter = MENU_TF_BUILD;
	private static int selected_type;
	
	private static int  anim_dir;
	private static long anim_start;
	private static long anim_duration;
		
	private static final String JSON_BUILD_START    = Entity.registerJSONKey("bs", GameMenu.class);
	private static final String JSON_BUILD_DURATION = Entity.registerJSONKey("bd", GameMenu.class);

	private static final String JSON_TO_BUILD_BLOCK = Entity.registerJSONKey("tb", GameMenu.class);
	private static final String JSON_TO_BUILD_MENU  = Entity.registerJSONKey("tm", GameMenu.class);
	private static final String JSON_TO_BUILD_MENU_PARENT = Entity.registerJSONKey("tmp", GameMenu.class);
	
	private long build_start;
	private int build_duration;
	
	private Entity parent;
	private Block to_build_block;
	private MenuItem to_build_menu;
	private boolean valid;
	
	public GameMenu(Entity parent){
		imap = new HashMap<Integer, ArrayList<MenuItem>>();
		anim_dir = NONE;
		scroll_x = 0;
		fling_vx = 0;
		this.parent = parent;
		to_build_block = null;
		to_build_menu  = null;
		valid = false;
	}
	
	public static void setTopFilter(int top_filter){
		if(GameMenu.top_filter != top_filter){
			GameMenu.top_filter = top_filter;
			selected_icon = null;
			SceneView.getInstance(null).getThread().onTopFilterChanged(top_filter);
		}
	}
	
	public static void setSelectedType(int selected_type){
		GameMenu.selected_type = selected_type;
	}
	
	public static void initCommandItems(Scene scene){
		command_items = new ArrayList<MenuItem>();
		
		if(SceneView.getGameMode()==SceneView.GAME_MODE_MAP_EDITOR){
			scene.addMenuItemsTo(command_items, new int []{
					R.drawable.bug1_moving1,
					R.drawable.bug_ant_moving1,
					R.drawable.bug_ant_soldier_moving1,
					R.drawable.bug_ant_winged_moving1,
					R.drawable.bug_ant_queen_moving1,
					R.drawable.bug3_moving1,
					R.drawable.bug_wasp_moving1,
			}, GameMenu.MENU_TF_COMMAND, 10);
			command_bug_items = command_items;
		}
		else {
			command_bug_items = new ArrayList<MenuItem>();
			
			scene.addMenuItemsTo(command_items, new int []{
					R.drawable.menu_select_items,
					R.drawable.menu_create_group
			}, GameMenu.MENU_TF_COMMAND, 0);			
			
			scene.addMenuItemsTo(command_bug_items, new int []{
					R.drawable.menu_move,
					R.drawable.menu_attack,
					R.drawable.menu_select_items,
					R.drawable.menu_create_group
			}, GameMenu.MENU_TF_COMMAND, 0);			
		}
	}
	
	public boolean selectEnabledTopFilter(){
		int i = 0;
		int tf = top_filter;
		int [] top_filters = SceneView.getGameMode()==SceneView.GAME_MODE_MAP_EDITOR?TOP_FILTERS_EDITOR:TOP_FILTERS;
		int max = top_filters.length;
		int tfi;
		for(tfi=0;tfi<max;tfi++) if(top_filters[tfi]==tf) break;
		
		if(tfi==max) {
			tfi = 0;
			tf  = top_filters[tfi];
		}
		
		if(!isTopFilterEnabled(top_filters[tfi])){
			tfi = 0;
			while(tfi<max && !isTopFilterEnabled(top_filters[tfi])){
				tfi++;
			}
			if(tfi==max) return false;
		}
		
		setTopFilter(top_filters[tfi]);
		return true;
	}
	
	
	
	public boolean isTopFilterEnabled(int top_filter){
		ArrayList<MenuItem> items = imap.get(top_filter);
		if(items!=null){
			for(MenuItem item : items) if(item.isEnabled()) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public int getTopFilterCount(){
		return (SceneView.getGameMode()==GAME_MODE_MAP_EDITOR)?TOP_FILTERS_EDITOR.length:TOP_FILTERS.length;
	}
	
	public int getTopFilter(){
		return top_filter;
	}
	
	public Entity getParent(){
		return parent;
	}
	
	public boolean isValid(){
		return valid;
	}
	
	public void setValid(boolean valid){
		this.valid = valid;
	}
	
	public void setBuildingProcess(MenuItem mi, long tic){
		setBuildingProcess(mi, tic, BUILD_TIME.get(mi.getIconResid()));
	}
	
	public void setBuildingProcess(MenuItem mi, long tic, int duration){
		if(SceneView.getGameMode()!=GAME_MODE_MAP_EDITOR){
			to_build_block = null;
			build_duration = duration;
			build_start = tic;
			to_build_menu = mi;
			Log.d("MENU BUILDING","START AT: "+tic+", D:"+duration+", MI:"+mi);
		}
		else {
			SceneView.getInstance(null).getThread().onMenuItemReady(mi);			
		}
	}
	
	public void setBuildingProcess(MenuItem mi, Block block){
		if(SceneView.getGameMode()!=GAME_MODE_MAP_EDITOR){
			to_build_block = block;
			to_build_menu  = mi;
			int tft = mi.getTopFilterType();
			ArrayList<MenuItem> items = imap.get(tft);
			if(items==null) {
				items = new ArrayList<MenuItem>();
				imap.put(tft,items);
			}
			if(!items.contains(mi)) items.add(mi);
		}
		else {
			SceneView.getInstance(null).getThread().onMenuItemReady(mi);
		}
	}

	public static void setScale(float scale) {
		if(GameMenu.scale != scale){
			GameMenu.scale = scale;
			MenuItem.clearCache();
			clearCache();
		}
	}
	
	public static float getScale(){
		return scale;
	}
	
	/*
	public void setItems(ArrayList<Integer> new_resids, int top_filter_type, int sort_order){
		HashSet<MenuItem> new_items = new HashSet<MenuItem>();
		for(Integer resid : new_resids){
			new_items.add(new MenuItem(this,resid,top_filter_type, sort_order));
			sort_order++;
		}
		setItems(new_items);
	}
	*/
	
	public void setItems(HashSet<MenuItem> new_items){
		HashMap<Integer, ArrayList<MenuItem>> imap = new HashMap<Integer, ArrayList<MenuItem>>();
		for(MenuItem mi : new_items) {
			mi.setParent(this);
			int tft = mi.getTopFilterType();
			
			ArrayList<MenuItem> items = imap.get(tft);
			if(items==null) {
				items = new ArrayList<MenuItem>();
				imap.put(tft,items);
			}
			
			//if(!items.contains(mi)) {
			items.add(mi);
			Collections.sort(items);
			//}
		}
		
		if(command_items==null) initCommandItems(SceneView.getScene());
		if(parent!=null && parent instanceof Bug && parent.getPlayer()==SceneView.getPlayer()){
			imap.put(MENU_TF_COMMAND, command_bug_items);
		}
		else {
			imap.put(MENU_TF_COMMAND, command_items);			
		}
		
		this.imap = imap;
		/*
		if(items.size()>0){
			ArrayList<MenuItem> to_remove = new ArrayList<MenuItem>();
			for(MenuItem mi : items) if(!new_items.contains(mi)) to_remove.add(mi);
			for(MenuItem mi : to_remove) items.remove(mi);
			for(MenuItem mi : new_items) {
				if(!items.contains(mi)) items.add(mi);
			}
		}
		else items = new_items;
		*/
	}
	
	
	public MenuItem getItemWithResId(int resId){
		for(ArrayList<MenuItem> items : imap.values()){
			for(MenuItem mi : items){
				if(mi.getIconResid() == resId) return mi;
			}
		}
		
		return null;
	}
	
	public static void releasePressedBtn(){
		last_pressed = BTN_NONE;
	}
	
	public boolean onTapXY(int x, int y, MenuListener ml, boolean on_down){
		if(scale<=0) return false;
		if(on_down) fling_vx /= 2;
        loadMenuBgIfNull();
        
		int [] top_filters = SceneView.getGameMode()==SceneView.GAME_MODE_MAP_EDITOR?TOP_FILTERS_EDITOR:TOP_FILTERS;
		for(int tf : top_filters){
			Rect cr = top_filters_click_rect.get(tf);
			if(cr!=null){
				if(cr.contains(x, y) && isTopFilterEnabled(tf)) {
					GameMenu.setTopFilter(tf);
					return true;
				}
			}
		}
        
		if(x<menu_bg[BG_LEFT].getWidth()-32){
			int mh = menu_bg[BG_LEFT].getHeight();
			int rp = y-last_y;
			if(rp<=mh/3){
				if(on_down) last_pressed = BTN_LL;
				else ml.onSelectMenuLevel(LEAF_LEVEL);
			}
			else if(rp<=2*mh/3){
				if(on_down) last_pressed = BTN_SL;
				else ml.onSelectMenuLevel(STALK_LEVEL);				
			}
			else{
				if(on_down) last_pressed = BTN_GL;
				else ml.onSelectMenuLevel(GROUND_LEVEL);				
			}
		}
		else if(x>last_sw-menu_bg[RIGHT].getWidth()+32){
			int mh = menu_bg[BG_RIGHT].getHeight();
			int rp = y-last_y;
			if(rp<=mh/2){
				if(on_down) last_pressed = BTN_CLOSE;
				else ml.closeMenu();
			}
			else {
				if(on_down) last_pressed = BTN_MENU;
				else ml.displayMainMenu();
			}
		}
		else if (!on_down){
			if(to_build_menu==null){
				ArrayList<MenuItem> items = imap.get(top_filter);
				if(items!=null){
					for(MenuItem mi : items){
						if(mi.isEnabled()){
							Rect cr = mi.getClickRect();
							if(cr!=null){
								if(cr.contains(x, y)) {
									ml.onMenuItemSelected(mi);
									return true;
								}
							}
						}
					}
				}
			}
			else if(to_build_block!=null && !to_build_block.isBuilded()){
				MenuItem to_build_menu = this.to_build_menu;
				Block b = to_build_block;
				
				ArrayList<MenuItem> items = imap.get(top_filter);
				for(MenuItem mi : items){
					if(mi.equals(to_build_menu)){
						Rect cr = mi.getClickRect();
						if(cr!=null){
							if(cr.contains(x, y)) {
								SceneView.getScene().setBlock(b.getLevel(), b.getX(), b.getY(), null);
								b.payBackPrice(mi.getPrice());
								return true;
							}
						}
					}
				}
			}
			else {
				MenuItem to_build_menu = this.to_build_menu;
				Block b = to_build_block;
				
				ArrayList<MenuItem> items = imap.get(top_filter);
				for(MenuItem mi : items){
					if(mi.equals(to_build_menu)){
						Rect cr = mi.getClickRect();
						if(cr!=null){
							if(cr.contains(x, y)) {
								this.to_build_menu = null;
								return true;
							}
						}
					}
				}				
			}
		}
		return false;
	}
	
	public int getAnimDir(){
		return anim_dir;
	}
	
	public void startAnim(int anim_dir, long anim_duration){
		this.anim_dir = anim_dir;
		this.anim_start = -1;
		this.anim_duration = anim_duration;
	}
	
	public int getStartY(){
		return last_y;
	}

	public int getMenuTopY(){
		loadMenuBgIfNull();
		return last_y - menu_bg[BG_TOP_FILTER].getHeight();
	}
	
	private static void clearCache(){
		menu_bg = null;
	}
	private void loadMenuBgIfNull(){
		if(menu_bg==null && scale>0){
			Bitmap [] bg;
			SceneView sv = SceneView.getInstance(null);
			bg = new Bitmap[MENU_RESID.length];
			for(int i=0; i<bg.length; i++){
				bg[i] = BitmapFactory.decodeResource(sv.getResources(), MENU_RESID[i]);
				if(scale!=1.0) {
					int nx = (int)(bg[i].getWidth()*scale);
					int ny = (int)(bg[i].getHeight()*scale);
					if(nx<1) nx = 1;
					if(ny<1) ny = 1;
					bg[i] = Bitmap.createScaledBitmap(bg[i], nx, ny, false);
				}
			}
			menu_bg = bg;
		}		
	}
	
	public double getBuildingPercent(long tic){
		if(to_build_block!=null){
			if(!to_build_block.isBuilded()){
				return to_build_block.getBuildingPercent();
			}
		}
		else if(to_build_menu!=null){
			if(build_start+build_duration>tic){
				return (double)(tic-build_start) / (double)build_duration;
			}
		}
		
		return 1;
	}
	
	public void draw(Canvas canvas, long tic){
		if(scale<=0) return;
		
		if(anim_start==-1) anim_start = tic;
		SceneView sv = SceneView.getInstance(null);
		
        loadMenuBgIfNull();
        
		DisplayMetrics dm = sv.getResources().getDisplayMetrics();
		
        int c_width  = SceneView.getCanvasWidth(); //dm.widthPixels;
        int c_height = SceneView.getCanvasHeight(); //dm.heightPixels;
        
        
        double percent = 0.0;
        if(anim_dir!=NONE){
			if(anim_start+anim_duration>tic) percent = ((double)(tic-anim_start))/(double)anim_duration;
			else percent = 1.0;
			
			if(anim_dir==UP) percent = 1.0 - percent;
        }
        
		//top filter icons
		int tf_w = menu_bg[BG_TOP_FILTER].getWidth();
		int tf_h = menu_bg[BG_TOP_FILTER].getHeight();
		
        int oy = c_height - menu_bg[ITEM_FRAME].getHeight(); // - (int)(TITLE_HEIGHT*SceneView.scale);
        int offset_y = (int)((double)(menu_bg[ITEM_FRAME].getHeight()+tf_h)*percent);
		int my = c_height-menu_bg[BG_LEFT].getHeight()  /* - (int)(TITLE_HEIGHT*SceneView.scale) */ + offset_y;
		
		canvas.save();
		canvas.clipRect(0, my, c_width, c_height);
		canvas.drawARGB(128, 200, 255, 200);
		canvas.restore();
        
		
		int [] top_filters = SceneView.getGameMode()==SceneView.GAME_MODE_MAP_EDITOR?TOP_FILTERS_EDITOR:TOP_FILTERS;
		
		for(int i=0; i<top_filters.length; i++){
			int tf_x = (int)(tf_w*i + TOP_FILTER_MARGIN*scale*(i+1));
			int tf_y = my - tf_h;
			int tf = top_filters[i];
			canvas.drawBitmap(menu_bg[BG_TOP_FILTER], tf_x, tf_y, null);
			Bitmap tf_icon = menu_bg[tf];
			
			if(top_filter==tf){
				if(selected_icon!=null) tf_icon = selected_icon;
				else {
					tf_icon = tf_icon.copy(Config.ARGB_8888, true);
					selected_effect.applyEffect(tf_icon);
					selected_icon = tf_icon;
				}
			}
			
			ArrayList<MenuItem> items = imap.get(tf);
			boolean has_items = false;
			if(items!=null){
				for(MenuItem item : items) if(item.isEnabled()) {
					has_items = true;
					break;
				}
			}
			
			if(!has_items){
				Bitmap d_icon = disabled_icons.get(tf);
				if(d_icon!=null) tf_icon = d_icon;
				else {
					tf_icon = tf_icon.copy(Config.ARGB_8888, true);
					disabled_effect.applyEffect(tf_icon);
					disabled_icons.put(tf, tf_icon);
				}
			}
			
			int sx = tf_x + (tf_w-tf_icon.getWidth())/2;
			int sy = tf_y + (tf_h - tf_icon.getHeight())/2;
			canvas.drawBitmap(tf_icon, sx, sy, null);
			top_filters_click_rect.put(top_filters[i], new Rect(sx,sy,sx+tf_icon.getWidth(),sy+tf_icon.getHeight()));
		}
		
		
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(12*scale);
        
        int scroll_x = (int)this.scroll_x;
		int x = 1;
		
		final double building;
		
		if(to_build_block!=null && to_build_block.isBuilded()){
			to_build_block = null;
			to_build_menu  = null;
			building = 1;
			setValid(false);
		}
		else {
			building = getBuildingPercent(tic);
		}
				
		ArrayList<MenuItem> items = imap.get(top_filter);
		if(items!=null){
			int ei_size = 0;
			for(MenuItem item : items) if(item.isEnabled()) ei_size++;
			
	        int distance = c_width/(ei_size+1);
	        int min_d = menu_bg[ITEM_FRAME].getWidth()+ (int)(5*scale);
	        if(distance<min_d) distance = min_d;
	        
			int fw = menu_bg[ITEM_FRAME].getWidth();
			int fh = menu_bg[ITEM_FRAME].getHeight();
			
				
			if(fling_vx>=1){
				if (fling_dir==LEFT ) scroll(-fling_vx/32);
				else scroll(fling_vx/32);
				
				fling_vx *= 0.9;			
			}
	        
			if(this.scroll_x<-fw/2) {
				this.scroll_x = -fw/2;
				fling_vx = 0;
			}
			else if(this.scroll_x>distance*(ei_size+1)-c_width+fw/2) {
				this.scroll_x = distance*(ei_size+1)-c_width+fw/2;
				fling_vx = 0;
			}
			
			for(MenuItem item : items){
				if(item.isEnabled()){
					Bitmap icon;
					
					if(to_build_menu!=null){
						if(item.equals(to_build_menu)){
							icon = item.getIcon(tic,building);
						}
						else {
							icon = item.getIcon(tic,0);
						}
					}
					else {
						icon = item.getIcon(tic,item.isEnabled()?1:0);
					}
					
					if(icon!=null){
						int ox = x*distance;
						canvas.drawBitmap(icon, ox-icon.getWidth()/2-scroll_x, oy+fw/2-icon.getWidth()/2 + offset_y,null);
						canvas.drawBitmap(menu_bg[ITEM_FRAME], ox-fw/2-scroll_x, oy + offset_y,null);
						
						int [] price = item.getPrice();
						
						int text_y = oy + fh - (int)(5*scale) + offset_y; // + (int)paint.getTextSize(); // + fh + offset_y - (int)(fm.ascent + fm.descent)/2;
						int text_x = ox - scroll_x;
						paint.setTextAlign(Align.CENTER);
						
						int ax = (fw - (int)(5*scale))/Block.NUTRIENTS;
						text_x -= Block.NUTRIENTS/2 * ax;
		    			for(int nutrient=0; nutrient<Block.NUTRIENTS; nutrient++){
		    				paint.setColor(Block.getNutrientColor(nutrient));
		    				String s_price = price[nutrient]+"";
		    				canvas.drawText(s_price, text_x, text_y, paint);
		    				text_x  += ax;
		    			}
		    			
		    			if(item.getIconResid()==selected_type){
							canvas.drawBitmap(menu_bg[BG_SELECTED_FRAME], ox-fw/2-scroll_x, oy + offset_y,null);
		    			}
		
						item.setClickRect(new Rect(ox-icon.getWidth()/2-scroll_x,oy+menu_bg[ITEM_FRAME].getWidth()/2-icon.getWidth()/2 + offset_y, ox+icon.getWidth()/2-scroll_x, oy+menu_bg[ITEM_FRAME].getWidth()/2+icon.getWidth()/2 + offset_y));
						
						x++;
					}
				}
			}
		}
		
		for(x=menu_bg[BG_LEFT].getWidth(); x<c_width-menu_bg[BG_RIGHT].getWidth(); x += menu_bg[BG_MIDDLE].getWidth()){
			canvas.drawBitmap(menu_bg[BG_MIDDLE], x,my, null);
		}
		canvas.drawBitmap(menu_bg[BG_LEFT],0,my,null);
		canvas.drawBitmap(menu_bg[BG_RIGHT],c_width-menu_bg[BG_RIGHT].getWidth(),my,null);
		
		boolean [] el = sv.getThread().getEnabledLevels();
		if(el[LEAF_LEVEL]) canvas.drawBitmap(menu_bg[BTN_LEVEL_ENABLED],12*scale,my+2*scale, null);
		if(el[STALK_LEVEL]) canvas.drawBitmap(menu_bg[BTN_LEVEL_ENABLED],10*scale,my+34*scale, null);
		if(el[GROUND_LEVEL]) canvas.drawBitmap(menu_bg[BTN_LEVEL_ENABLED],6*scale,my+66*scale, null);
		
		int active_level = sv.getThread().getActiveLevel();
		if(active_level>-1){
			switch(active_level){
				case LEAF_LEVEL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_ACTIVE],12*scale,my+2*scale, null);					
				break;
				case STALK_LEVEL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_ACTIVE],10*scale,my+34*scale, null);					
				break;
				case GROUND_LEVEL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_ACTIVE],6*scale,my+66*scale, null);					
				break;
			}
		}
		
		if(last_pressed != BTN_NONE){
			switch(last_pressed){
				case BTN_LL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_OVER],12*scale,my+2*scale, null);					
				break;
				case BTN_SL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_OVER],10*scale,my+34*scale, null);					
				break;
				case BTN_GL:
					canvas.drawBitmap(menu_bg[BTN_LEVEL_OVER],6*scale,my+66*scale, null);					
				break;
				case BTN_CLOSE:
					canvas.drawBitmap(menu_bg[BTN_CLOSE_OVER],c_width-menu_bg[BTN_CLOSE_OVER].getWidth()-4*scale,my+4*scale, null);					
				break;
				case BTN_MENU:
					canvas.drawBitmap(menu_bg[BTN_MENU_OVER],c_width-menu_bg[BTN_MENU_OVER].getWidth()-5*scale,my+53*scale, null);					
				break;
			}
		}
		
		if((percent>=1.0 && anim_dir==DOWN) || (percent<=0.0 && anim_dir==UP)) anim_dir = NONE;
		last_y = my;
		last_sw = c_width;
	}
	
	public void flingScroll(float fling_vx){
		if(fling_vx<0) {
			fling_dir = RIGHT;
			//elastic_mode = false;
			this.fling_vx = -fling_vx/2;
		}
		else {
			fling_dir = LEFT;
			//elastic_mode = false;
			this.fling_vx = fling_vx/2;						
		}
	}
	
	public void scroll(float scroll_x){
		if(scale<=0) return;
		
		this.scroll_x += scroll_x;
		
		loadMenuBgIfNull();
		DisplayMetrics dm = SceneView.getInstance(null).getResources().getDisplayMetrics();
        int s_width  = dm.widthPixels;
        
		ArrayList<MenuItem> items = imap.get(top_filter);        
		int ei_size = 0;
		for(MenuItem item : items) if(item.isEnabled()) ei_size++;
		
        int distance = s_width/(ei_size+1);
        int min_d = menu_bg[ITEM_FRAME].getWidth()+ (int)(5*scale);
        if(distance<min_d) distance = min_d;
        
		int fw = menu_bg[ITEM_FRAME].getWidth();
		int fh = menu_bg[ITEM_FRAME].getHeight();
		if(this.scroll_x<-fw/2) {
			this.scroll_x = -fw/2;
			fling_vx = 0;
		}
		else if(this.scroll_x>distance*(ei_size+1)-s_width+fw/2) {
			this.scroll_x = distance*(ei_size+1)-s_width+fw/2;
			fling_vx = 0;
		}
		
	}	
	
	public void step(long tic){
		if(to_build_menu!=null && to_build_block==null){
			if(build_start+build_duration<=tic){
				SceneView.getInstance(null).getThread().onMenuItemReady(to_build_menu);
				to_build_menu = null;
			}
		}
	}
	
	public MenuItem getMenuItemByIconResId(int iconResId){
		for(Entry<Integer,ArrayList<MenuItem>> e : imap.entrySet()){
			for(MenuItem item : e.getValue()){
				if(item.getIconResid()==iconResId) return item;
			}
		}
		
		return null;
	}
	
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = new JSONObject();
		o.put(JSON_BUILD_START, build_start);
		o.put(JSON_BUILD_DURATION, build_duration);
		if(to_build_menu!=null) {
			o.put(JSON_TO_BUILD_MENU, rc.resIdToType(to_build_menu.getIconResid()));
		}
		return o;
	}
	
	public void initMenuFromJSON(JSONObject o, ResIdConverter rc) throws JSONException {
		build_start    = o.getLong(JSON_BUILD_START);
		build_duration = o.getInt(JSON_BUILD_DURATION);
		if(!o.isNull(JSON_TO_BUILD_MENU) && o.isNull(JSON_TO_BUILD_MENU_PARENT)) {
			int iconResId = rc.typeToResId(o.getInt(JSON_TO_BUILD_MENU));
			to_build_menu = getMenuItemByIconResId(iconResId);
		}
	}
	
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_TO_BUILD_BLOCK)){
			to_build_block = (Block)tmp_ids.get(o.getInt(JSON_TO_BUILD_BLOCK));
		}
		
		if(!o.isNull(JSON_TO_BUILD_MENU) && !o.isNull(JSON_TO_BUILD_MENU_PARENT)) {
			int iconResId = rc.typeToResId(o.getInt(JSON_TO_BUILD_MENU));
			Entity parent = tmp_ids.get(o.getInt(JSON_TO_BUILD_MENU_PARENT));
			to_build_menu = parent.getMenu().getMenuItemByIconResId(iconResId);
		}
	}
	
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = new JSONObject();
		if(to_build_block!=null){
			Integer id = tmp_ids.get(to_build_block);
			if(id!=null) o.put(JSON_TO_BUILD_BLOCK, id);
		}
		
		if(to_build_menu!=null) {
			Entity tmp = to_build_menu.getParent().getParent();
			Integer id = tmp_ids.get(tmp);
			if(tmp!=this.getParent() && id!=null) o.put(JSON_TO_BUILD_MENU_PARENT, id);
		}
		return o;
	}
	
}

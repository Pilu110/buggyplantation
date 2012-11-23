package com.pidogames.buggyplantation.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.Constants;
import com.pidogames.buggyplantation.interfaces.DialogBoxListener;

public abstract class DialogBox implements Constants {
	
	public static final int WIDTH  = 230;
	public static final int HEIGHT = 250;
	public static final int MARGIN = 25;	
	
	public static final int DEFAULT_TEXT_SIZE = 28;
	public static final int DEFAULT_TITLE_SIZE = 18;
	public static final int SMALL_TEXT_SIZE   = 22;
	
	public static final int SMALL_PADDING = 30;
	
	private static final long AFTER_SCROLL_DELAY = 25;
	
	public static final String JSON_CODE      = "code";
	public static final String JSON_TITLE     = "title";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_CHILDREN  = "children";
	public static final String JSON_CLEARCHILDREN  = "clearchildren";
	public static final String JSON_TYPE      = "type";
	public static final String JSON_PADDING   = "padding";
	public static final String JSON_TEXT_SIZE = "text_size";
	public static final String JSON_MAX_VALUE = "max_value";
	public static final String JSON_MIN_VALUE = "min_value";
	public static final String JSON_ENABLED = "enabled";
	public static final String JSON_SURE_DIALOG = "sure_dialog";
	public static final String JSON_MISSION_STARTER = "mission_starter";
		
	public static final String JSON_SCROLL_Y = "scroll_y";
	public static final String JSON_S_ITEM   = "s_item";
	public static final String JSON_PS_ITEM  = "ps_item";
	public static final String JSON_SELECTABLE = "selectable";
	public static final String JSON_SELECTED = "selected";
	public static final String JSON_DIALOG_TITLE = "dialog_title";
	
	public static final String ITEM_TYPE_SLIDEBAR = "SLIDEBAR";
	public static final String ITEM_TYPE_CHECKBOX = "CHECKBOX";
	public static final String ITEM_TYPE_LIST     = "LIST";
	
	private static final int DIALOG_TITLE_HEIGHT = 30;
	
	private static final int ENABLED_TEXT_COLOR  = 0xffa62c01;
	private static final int DISABLED_TEXT_COLOR = 0xff555555;
	
	public static final String MENU_CODE_EXIT = "EXIT";
	public static final String MENU_CODE_SAVE = "SAVE";
	public static final String MENU_CODE_SFILE = "SFILE";
	public static final String MENU_CODE_LOAD = "LOAD";
	public static final String MENU_CODE_LFILE = "LFILE";
	public static final String MENU_CODE_CMFILE = "CMFILE";
	public static final String MENU_CODE_MSFILE = "MSFILE";
	public static final String MENU_CODE_SMFILE = "SMFILE";
	public static final String MENU_CODE_NEW_ENTRY = "NEW_ENTRY";
	public static final String MENU_CODE_OBJECTIVES = "OBJECTIVES";
	public static final String MENU_CODE_OBJECTIVE  = "OBJECTIVE";
	
	public static final String MENU_CODE_SURE_DIALOG_NO = "SDNO";
	public static final String MENU_CODE_SURE_DIALOG_YES = "SDYES";
	
	public static final String MENU_CODE_CANCEL = "CANCEL";	
	public static final String MENU_CODE_OVERWRITE_SELECTED = "OVERWRITE_SELECTED";
	public static final String MENU_CODE_LOAD_SELECTED      = "LOAD_SELECTED";
	public static final String MENU_CODE_RENAME = "RENAME";	
		
	private static final Map<String, Integer> TITLE_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_SAVE, R.string.mm_save);
		tempMap.put(MENU_CODE_LOAD, R.string.mm_load);
		tempMap.put(MENU_CODE_OBJECTIVES, R.string.mm_objectives);
		tempMap.put(MENU_CODE_CANCEL, R.string.mm_cancel);
		tempMap.put(MENU_CODE_OVERWRITE_SELECTED, R.string.mm_overwrite);
		tempMap.put(MENU_CODE_LOAD_SELECTED, R.string.mm_load_selected);
		tempMap.put(MENU_CODE_RENAME, R.string.mm_rename);
		TITLE_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> ARRAY_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		ARRAY_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> SURE_DIALOG_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		SURE_DIALOG_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private Rect last_bounds;	
	
	private float scale;
	
	protected JSONArray root;
	protected JSONArray items;
	
	private JSONObject prev_selected_item;
	private JSONObject selected_item;
	private String parent_item_code;
	
	private Paint bg_paint;
	private Paint title_bg_paint;
	private Paint title_bg_line_paint;
	private Paint title_paint;
	private Paint text_paint;
	private Paint frame_paint;
	private Paint slidebar_paint;
	private Paint slidebar_bg_paint;
	
	private String title;
	private Bitmap background;
	private Bitmap prev_background;
	private int prev_background_alpha;
	
	private int scroll_y;
	private int items_bottom;
	private int frame_y2;
	
	private boolean invalid_parent;
	
	private static Bitmap [] menu_bg = null;
	private HashMap<String, Rect> click_rect;
	private Stack<JSONObject> parent;
	private Stack<Bitmap> bg_parent;
	private DialogBoxListener mml;
	
	private String sure_dialog_msg;
	private JSONObject sure_dialog_item;
	private boolean sure_dialog_visible_after;
	
	private static final float ANIM_STEP = (float)0.25;
	
	private static final int BG_FRAME_LU_CORNER = 0;
	private static final int BG_FRAME_TOP = 1;
	private static final int BG_FRAME_RU_CORNER = 2;
	private static final int BG_FRAME_RIGHT = 3;
	private static final int BG_FRAME_RD_CORNER = 4;
	private static final int BG_FRAME_BOTTOM = 5;
	private static final int BG_FRAME_LD_CORNER = 6;
	private static final int BG_FRAME_LEFT = 7;
	
	private static final int [] MENU_RESID = {
		R.drawable.menu_main_frame_bg_lu_corner,
		R.drawable.menu_main_frame_bg_top
	};
		
	private boolean visible;
	protected Context context;
	
	private float anim_percent;
	
	private boolean scroll_slider;
	
	public Rect getLastBounds(){
		return new Rect(last_bounds);
	}
	
	public void setScale(float scale){
		if(this.scale != scale){
			this.scale = scale;
			clearCache();
		}
	}
	
	protected DialogBox(Context context, String file_name){	
		this.context = context;
		visible  = false;
		scroll_y = 0;
		title    = null;
				
		bg_paint = new Paint();
		bg_paint.setColor(0xffcf9e18);
		
		frame_paint = new Paint();
		click_rect = new HashMap<String, Rect>();
		parent = new Stack<JSONObject>();
		bg_parent = new Stack<Bitmap>();
		
		//float scale = context.getResources().getDisplayMetrics().density;
		
		slidebar_bg_paint = new Paint();
		slidebar_bg_paint.setColor(0xffac7f07);
		slidebar_bg_paint.setShadowLayer(4*scale, scale, scale, 0xff5d1901);

		slidebar_paint = new Paint();
		slidebar_paint.setColor(ENABLED_TEXT_COLOR);
		
		text_paint = new Paint();
		text_paint.setColor(ENABLED_TEXT_COLOR);
		text_paint.setTextSize(DEFAULT_TEXT_SIZE*scale);
		text_paint.setTypeface(Typeface.SERIF);
		text_paint.setShadowLayer(3*scale, scale, scale, 0xff5d1901);
		
		title_paint = new Paint(); 
		title_paint.setTextSize(DEFAULT_TITLE_SIZE*scale);
		title_paint.setTypeface(Typeface.SERIF);
		title_paint.setColor(ENABLED_TEXT_COLOR);
		title_paint.setShadowLayer(2*scale, 0, 0, 0xff5d1901);
		
		title_bg_paint = new Paint(); 
		title_bg_paint.setColor(bg_paint.getColor());
		
		title_bg_line_paint = new Paint();
		title_bg_line_paint.setColor(0xffac8007);
		
		if(file_name!=null) loadItems("menu/"+file_name+".json");
		
		invalid_parent = false;
		scroll_slider  = false;
	}
	
	public Paint getTextPaint(){
		return text_paint;
	}
	
	public boolean isSelected(JSONObject item){
		return (prev_selected_item!=null)?(prev_selected_item==item):false;
	}
	
	public JSONObject getSelectedItem() {
		return selected_item;
	}
	
	public static JSONArray loadItems(Context context, String file_name) {
		JSONArray items;
		try {
			InputStream is = context.getAssets().open(file_name);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=br.readLine())!=null){
				sb.append(line);
			}
			items  = new JSONArray(sb.toString());
			is.close();
			return items;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean loadItems(String file_name) {
		JSONArray items = loadItems(context, file_name);
		if(items!=null){
			root = items;
			this.items = root;	
			return true;
		}
		else return false;
	}
	
	public void setMainMenuListener(DialogBoxListener mml){
		this.mml = mml;
	}
	
	public void setVisible(boolean visible, boolean clear_sure){
		if(this.visible!=visible){
			
			if(visible) {
				click_rect.clear();
				parent.clear();
				bg_parent.clear();
				items = root;
				anim_percent = 0;
				title = null;
				background = null;
				invalid_parent = false;
				scroll_y = 0;
			}
			else {
				anim_percent = 1;
			}
			this.visible = visible;
		}
		
		if(clear_sure){
			sure_dialog_msg = null;
			sure_dialog_item = null;
			sure_dialog_visible_after = visible;
		}
	}
	
	public void setBackground(Bitmap background){
		prev_background_alpha = 255;
		prev_background = this.background;
		this.background = background;
	}
	
	public Integer getTitleResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);		
		return TITLE_RESID.get(code);
	}
	
	public Integer getArrayResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		return ARRAY_RESID.get(code);
	}
	
	public Integer getSureDialogResIdByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		return SURE_DIALOG_RESID.get(code);		
	}
	
	public float getAnimPercent(){
		return anim_percent;
	}
	
	public boolean isVisible(){
		return visible;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		return true;
	}
	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(!scroll_slider && Math.abs(distanceX)<scale*MAX_SCROLL_PATH_X){
			if((scroll_y + distanceY >= 0 || distanceY>0) && (items_bottom-frame_y2 >= scroll_y || distanceY<0)){
				scroll_y += distanceY;
				return true;
			}
		}
		else if(Math.abs(distanceY)<scale*MAX_SCROLL_PATH_Y){
			if(click_rect!=null){
				int x = (int)e1.getX();
				int y = (int)e1.getY();
				y -= (TITLE_HEIGHT*scale);
				for(HashMap.Entry<String,Rect> entry : click_rect.entrySet()){
					try {
						Rect r = entry.getValue();
						if(x>=r.left && x<=r.right && y>=r.top && y<=r.bottom){
							JSONObject item = getItemByCode(entry.getKey());
							if(item!=null) {
								if(item.has(JSON_TYPE) && item.getString(JSON_TYPE).equals(ITEM_TYPE_SLIDEBAR)){
									float rate = (e2.getX() - r.left) / (float)(r.right - r.left);
									if(rate<0) rate = 0;
									else if(rate>1) rate = 1;
									if(mml!=null) mml.onSetSlidebarRate(this, entry.getKey(), rate);
									scroll_slider = true;
									return true;
								}
							}
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return false;
	}
	
	public void selectItem(JSONObject item, Float rate, boolean on_down, boolean ignore_dialog) throws JSONException {
		if(item!=null){
			if(on_down){
				prev_selected_item = selected_item;
				selected_item = item;
			}
			
			String code = item.getString(JSON_CODE);
			
			if(item!=null) {
				String type = null;
				if(item.has(JSON_TYPE)) type = item.getString(JSON_TYPE);
				
				if(on_down){
					if(type!=null && type.equals(ITEM_TYPE_SLIDEBAR)){
						//float rate = (x - r.left) / (float)(r.right - r.left);
						if(mml!=null) mml.onSetSlidebarRate(this, code, rate);
					}
					else if(mml!=null && !item.has(JSON_CHILDREN) && item.has(JSON_SELECTABLE)) {
						if(!ignore_dialog && item.has(JSON_SURE_DIALOG)){
							String msg;
							if(item.optBoolean(JSON_SURE_DIALOG)) msg = SceneView.getString(getSureDialogResIdByCode(code));
							else msg = item.getString(JSON_SURE_DIALOG);
							
							showSureDialog(msg, item, visible);							
						}
						else {
							mml.onDialogBoxItemSelected(this, item);
							mml.cancelNextTap();
						}
					}
				}
				else {
					if (item.has(JSON_CHILDREN)) selectSubMenu(code,0);
					else if(type!=null && type.equals(ITEM_TYPE_CHECKBOX)){
						if(mml!=null) mml.onToggleCheckbox(this, code);
					}
					else if(type!=null && type.equals(ITEM_TYPE_LIST)){
						int max_value = item.getInt(JSON_MAX_VALUE);
						int min_value;
						if(!item.isNull(JSON_MIN_VALUE)) min_value = item.getInt(JSON_MIN_VALUE);
						else min_value = 0;
						
						if(mml!=null) mml.onToggleList(this, code, min_value, max_value);
					}
					else if(mml!=null && !item.has(JSON_SELECTABLE)) {
						if(!ignore_dialog && item.has(JSON_SURE_DIALOG)){
							String msg;
							if(item.optBoolean(JSON_SURE_DIALOG)) msg = SceneView.getString(getSureDialogResIdByCode(code));
							else msg = item.getString(JSON_SURE_DIALOG);
							
							showSureDialog(msg, item, visible);
						}
						else {
							mml.onDialogBoxItemSelected(this, item);
						}
					}
				}				
			}			
		}
	}
	
	public void onUp(){
		scroll_slider = false;
	}
		
	public boolean onTapXY(int x, int y, boolean on_down){
		if(click_rect!=null){
			for(HashMap.Entry<String,Rect> entry : click_rect.entrySet()){
				try {
					Rect r = entry.getValue();
					if(x>=r.left && x<=r.right && y>=r.top && y<=r.bottom){
						if(sure_dialog_msg!=null){
							//String code = entry.getKey();
							//if(code.equals(MENU_CODE_SURE_DIALOG_NO)){
							//}
							if(entry.getKey().equals(MENU_CODE_SURE_DIALOG_YES)){
								selectItem(sure_dialog_item, (x - r.left) / (float)(r.right - r.left), on_down, true);
								if(!on_down) closeSureDialog();
							}
							else if(!on_down){
								closeSureDialog();
							}
							return true;
						}
						else {
							JSONObject item = getItemByCode(entry.getKey());
							selectItem(item, (x - r.left) / (float)(r.right - r.left), on_down, false);						
							return true;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private JSONObject getItemByCode(String code){
		for(int i=0; i<items.length(); i++){
			try {
				if(items.getJSONObject(i).getString(JSON_CODE).equals(code)){
					return items.getJSONObject(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void selectRoot(){
		while(selectParent());
	}
	
	public boolean selectParent(){
		if(!parent.empty()){
			scroll_y = 0;
			parent_item_code = null;
			prev_selected_item = null;
			selected_item = null;	
			
			JSONObject p = parent.pop();
			try {
				items    = p.getJSONArray(JSON_CHILDREN);
				scroll_y = p.getInt(JSON_SCROLL_Y);
				if(!p.isNull(JSON_S_ITEM)) selected_item = p.getJSONObject(JSON_S_ITEM);
				else selected_item = null;
				if(!p.isNull(JSON_PS_ITEM)) prev_selected_item = p.getJSONObject(JSON_PS_ITEM);				
				else prev_selected_item = null;
				if(!p.isNull(JSON_DIALOG_TITLE)) title = p.getString(JSON_DIALOG_TITLE);
				else title = null;
				if(!p.isNull(JSON_CODE)) parent_item_code = p.getString(JSON_CODE);
				else parent_item_code = null;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			background = bg_parent.pop();
			
			
			click_rect.clear();
			
			for(int i=0; i<items.length(); i++){
				try {
					JSONObject item = items.getJSONObject(i);
					if(!item.isNull(JSON_CLEARCHILDREN) && item.getBoolean(JSON_CLEARCHILDREN)){
						item.remove(JSON_CHILDREN);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			if(invalid_parent) {
				refreshMenu();
				invalid_parent = false;
			}
			
			return true;
		}
		else 
			return false;
	}
	
	public void refreshMenuWithParent(String code) {
		int sy = scroll_y;
		selectParent();
		refreshMenu();
		selectSubMenu(code, sy);		
	}
	
	public void refreshMenu() {
		Log.d("MENU","REFRESH MENU ITEMS...");
		Log.d("MENU","PARENT:"+parent_item_code);
		if(parent_item_code!=null){
			if(mml!=null) {
				JSONArray items = mml.getChildrenFor(this, parent_item_code);
				if(items!=null){
					this.items = items;
					click_rect.clear();
				}
			}
		}
	}
	
	public void refreshTitleFor(String code, String title){
		for(int i=0; i<items.length(); i++){
			try {
				JSONObject item = items.getJSONObject(i);
				if(item.getString(JSON_CODE).equals(code)) {
					item.put(JSON_TITLE, title);
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void invalidateParent() {
		invalid_parent = true;
	}
	
	public void setTitle(String title){
		this.title = title.toUpperCase();		
	}
	
	public void selectSubMenu(String code, int scroll_y){
		try {
			Log.d("MENU","STORE PARENT ITEM: "+parent_item_code);
			JSONObject p = new JSONObject();
			p.put(JSON_CODE, parent_item_code);
			p.put(JSON_CHILDREN, items);
			p.put(JSON_SCROLL_Y, this.scroll_y);
			p.put(JSON_S_ITEM, selected_item);
			p.put(JSON_PS_ITEM, prev_selected_item);
			p.put(JSON_DIALOG_TITLE, title);
			parent.push(p);
			bg_parent.push(background);
			

			this.scroll_y = scroll_y;
			JSONObject item = getItemByCode(code);
			
			String title;
			if(item.isNull(JSON_TITLE)){
				Integer title_resid = getTitleResidByCode(code);
				title = (title_resid!=null)?context.getString(title_resid):code;
				item.put(JSON_TITLE, title);
			}
			else {
				title = item.getString(JSON_TITLE);
			}			
			
			if(!item.isNull(JSON_DIALOG_TITLE)){
				title = item.getString(JSON_DIALOG_TITLE);
			}
			
			this.title = title.toUpperCase();
			parent_item_code = code;
			Object children = item.get(JSON_CHILDREN);
			if(children instanceof JSONArray) items = (JSONArray)children;
			else if(children instanceof Boolean && (mml!=null)){
				JSONArray items = mml.getChildrenFor(this, code);
				if(items==null){
					selectParent(); // error occured
					return;					
				}
				else this.items = items;
			}
			else {
				parent.pop();
				bg_parent.pop();
				return;
			}			
			click_rect.clear();
			
			for(int i=0; i<items.length(); i++){
				item = items.getJSONObject(i);
				if(!item.isNull(JSON_SELECTED) && item.getBoolean(JSON_SELECTED)){
					selectItem(item, null, true, false);
					break;
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();			
		}
	}
	
	public void close(){
		//Entity.setGrayScaleMode(false);
		setVisible(false, true);
	}
	
	public boolean closeSureDialog(){
		if(sure_dialog_msg!=null){
			setVisible(sure_dialog_visible_after, false);
			if(sure_dialog_visible_after){
				sure_dialog_msg  = null;
				sure_dialog_item = null;
				click_rect.clear();
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public void showSureDialog(String msg, JSONObject item, boolean visible_after){
		sure_dialog_msg  = msg;
		sure_dialog_item = item;
		sure_dialog_visible_after = visible_after;
		click_rect.clear();
		setVisible(true, false);
	}
	
	private static void clearCache(){
		menu_bg = null;
	}
	
	private void loadMenuBgIfNull(float scale){
		if(menu_bg==null){
			Bitmap [] bg;
			SceneView sv = SceneView.getInstance(null);
			bg = new Bitmap[MENU_RESID.length*4];
			for(int i=0; i<MENU_RESID.length; i++){
				bg[i] = BitmapFactory.decodeResource(sv.getResources(), MENU_RESID[i]);
				if(scale!=1.0) {
					int nx = (int)(bg[i].getWidth()*scale);
					int ny = (int)(bg[i].getHeight()*scale);
					if(nx<1) nx = 1;
					if(ny<1) ny = 1;
					bg[i] = Bitmap.createScaledBitmap(bg[i], nx, ny, false);
				}
			}
			
	        Matrix matrix = new Matrix();		        
			for(int i=1; i<4; i++){
		        matrix.postRotate(90);
		        for(int j=0; j<MENU_RESID.length; j++){
		        	Bitmap dst = bg[j];
		        	dst = Bitmap.createBitmap(dst, 0, 0, dst.getWidth(), dst.getHeight(), matrix, true);
		        	bg[i*MENU_RESID.length+j] = dst;
		        }
			}
			
			menu_bg = bg;
		}		
	}
	
	public String getDialogTitle(){
		return title;
	}
	
	public void draw(Canvas canvas){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float ap = anim_percent;
		
		//float scale = dm.density;
        int c_width  = SceneView.getCanvasWidth(); //dm.widthPixels;
        int c_height = SceneView.getCanvasHeight(); //dm.heightPixels - (int)(TITLE_HEIGHT*scale);
        
		loadMenuBgIfNull(scale);
		
		int sm = (int)(MARGIN*scale);
		int x1  = (c_width - (int)(WIDTH*scale))/2;
		if(x1<sm) x1 = sm;
		int y1  = (c_height - (int)(HEIGHT*scale))/2;
		if(y1<sm) y1 = sm;
		
		int cw = menu_bg[BG_FRAME_LU_CORNER].getWidth();
		int ch = menu_bg[BG_FRAME_LU_CORNER].getHeight();
		int x2 = c_width-x1-cw;
		int y2 = c_height-y1-ch;
		frame_y2 = y2;
		
		last_bounds = new Rect(x1+cw,y1+ch,x2,y2);
		
		int w = x2-x1;
		int h = y2-y1;
		
		int iap = (int)(ap*255);
		int bgap = iap + 20;
		if(bgap>255) bgap = 255;
		
		bg_paint.setAlpha(bgap);
		title_bg_paint.setAlpha(bgap);
		title_bg_line_paint.setAlpha(bgap);
		frame_paint.setAlpha(bgap);
		text_paint.setAlpha(iap);
		
		String title = this.title;
		if(title!=null){
			title_paint.setTextSize(DEFAULT_TITLE_SIZE*scale);
			canvas.drawRect(x1+cw, y1+ch, x2, y1+ch+(int)(DIALOG_TITLE_HEIGHT*scale), title_bg_paint);
			int tw = (int)title_paint.measureText(title);
			int tx = (last_bounds.right + last_bounds.left - tw)/2;
			int ty = y1 + ch + (int)((DIALOG_TITLE_HEIGHT*scale-title_paint.getFontSpacing())/2 - title_paint.ascent());
			canvas.drawText(title, tx, ty, title_paint);
			last_bounds.top = last_bounds.top + (int)(DIALOG_TITLE_HEIGHT*scale);
			canvas.drawRect(last_bounds.left, last_bounds.top-3, last_bounds.right, last_bounds.top, title_bg_line_paint);
		}
		
		if(background!=null && !background.isRecycled()){
			canvas.drawBitmap(background, null, last_bounds, bg_paint);
			
			if(prev_background_alpha>0){
				bg_paint.setAlpha(prev_background_alpha);			
				if(prev_background!=null && !prev_background.isRecycled()) 
					canvas.drawBitmap(prev_background, null, last_bounds, bg_paint);
				else
					canvas.drawRect(last_bounds, bg_paint);			
					
				prev_background_alpha-=15;
				if(prev_background_alpha<=0){
					Bitmap prev_bg = prev_background;
					if(prev_bg!=null){
						prev_background = null;
						prev_bg.recycle();
					}
				}
			}
			
			bg_paint.setAlpha(16);
			canvas.drawRect(last_bounds, bg_paint);			
			
		}
		else {
			canvas.drawRect(last_bounds, bg_paint);			
		}		
		
		Paint paint = (ap!=1)?frame_paint:null;
		
		int fw = menu_bg[BG_FRAME_TOP].getWidth();
		for(int x=x1+cw; x<x2; x+=fw) canvas.drawBitmap(menu_bg[BG_FRAME_TOP], x, y1, paint);
		for(int y=y1+ch; y<y2; y+=fw) canvas.drawBitmap(menu_bg[BG_FRAME_RIGHT], x2, y, paint);
		for(int x=x1+cw; x<x2; x+=fw) canvas.drawBitmap(menu_bg[BG_FRAME_BOTTOM], x, y2, paint);
		for(int y=y1+ch; y<y2; y+=fw) canvas.drawBitmap(menu_bg[BG_FRAME_LEFT], x1, y, paint);
		
		canvas.drawBitmap(menu_bg[BG_FRAME_LU_CORNER], x1, y1, paint);
		canvas.drawBitmap(menu_bg[BG_FRAME_RU_CORNER], x2, y1, paint);
		canvas.drawBitmap(menu_bg[BG_FRAME_LD_CORNER], x1, y2, paint);
		canvas.drawBitmap(menu_bg[BG_FRAME_RD_CORNER], x2, y2, paint);
		
		if(sure_dialog_msg==null){
			drawItems(canvas, items, new Rect(last_bounds), scale);
		}
		else {
			text_paint.setTextSize(DEFAULT_TEXT_SIZE*scale);
			text_paint.setShadowLayer(3*scale, scale, scale, 0xff5d1901);
			
			String [] words = sure_dialog_msg.split(" ");
			text_paint.setTextAlign(Align.CENTER);
			
			int alpha = text_paint.getAlpha();
			text_paint.setColor(ENABLED_TEXT_COLOR);
			text_paint.setShadowLayer(3*scale, scale, scale, 0x5d1901 | (alpha << 24));
			text_paint.setAlpha(alpha);
			
			ArrayList<String> pieces = new ArrayList<String>();
			String s = null;
			
			for(String word : words){
				String ns = (s==null)?word:s+" "+word;
				if(text_paint.measureText(ns)<w-2*sm){
					s = ns;
				}
				else {
					//canvas.drawText(s, cx, cy, text_paint);
					//cy += text_paint.getTextSize();
					pieces.add(s);
					s = word;
				}
			}
			if(s!=null) pieces.add(s); //canvas.drawText(s, cx, cy, text_paint);
			
			float ts = text_paint.getTextSize();
			int ascent = (int)text_paint.ascent();
			int cx = (last_bounds.right + last_bounds.left)/2;
			int cy = (last_bounds.bottom - last_bounds.top)/3 + last_bounds.top - (int)(ts*pieces.size()/2) - ascent;
			if(cy<last_bounds.top+sm/2-ascent) cy = last_bounds.top+sm/2-ascent;
			
			for(String p : pieces){
				canvas.drawText(p, cx, cy, text_paint);
				cy += text_paint.getTextSize();				
			}
			
			cy = last_bounds.bottom - (int)ts - ascent - sm;
			cx = last_bounds.right - sm;
			text_paint.setTextAlign(Align.RIGHT);
			String no = SceneView.getString(R.string.no);
			canvas.drawText(no, cx, cy, text_paint);
			Rect cr = new Rect(last_bounds.right-sm-(int)text_paint.measureText(no), cy-(int)ts, last_bounds.right-sm, cy);
			click_rect.put(MENU_CODE_SURE_DIALOG_NO, cr);
			
			cx = last_bounds.left + sm;
			text_paint.setTextAlign(Align.LEFT);
			String yes = SceneView.getString(R.string.yes);
			canvas.drawText(yes, cx, cy, text_paint);
			cr = new Rect(last_bounds.left+sm, cy-(int)ts, last_bounds.left+sm+(int)text_paint.measureText(yes), cy);
			click_rect.put(MENU_CODE_SURE_DIALOG_YES, cr);
			
		}
		
		if(visible && ap<1){
			ap += ANIM_STEP;
			if(ap>1) ap=1;
		}
		else if(!visible && ap>0){
			ap -= ANIM_STEP;
			if(ap<0) ap=0;
		}
		anim_percent = ap;
	}
	
	private int getItemsHeight(JSONArray items, float scale) {
		int height = 0;
		for(int i=0; i<items.length(); i++){
			try {
				JSONObject item = items.getJSONObject(i);
				if(item==null) continue;
				if(item.isNull(JSON_TEXT_SIZE)){
					text_paint.setTextSize(DEFAULT_TEXT_SIZE*scale);
				}
				else {
					text_paint.setTextSize(item.getInt(JSON_TEXT_SIZE)*scale);					
				}
				
				height += (int)text_paint.getFontSpacing();
				
				if(!item.isNull(JSON_PADDING)){
					height += item.getInt(JSON_PADDING);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return height;
	}
		
	private void drawItems(Canvas canvas, JSONArray items, Rect bounds, float scale){
		
		canvas.save();
		canvas.clipRect(bounds);
		
		int items_height = getItemsHeight(items, scale);
		
		int slidebars = 0;
		for(int i=0; i<items.length(); i++){
			try {
				JSONObject item = items.getJSONObject(i);
				if(!item.isNull(JSON_TYPE) && item.getString(JSON_TYPE).equals(ITEM_TYPE_SLIDEBAR)) slidebars++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		//int ascent = (int)text_paint.ascent();
		int ascent = 0;
		
		int free_space = (bounds.bottom - bounds.top - items_height - (int)(60*scale)*slidebars);
		//if(t_topm>0) bounds.top += t_topm;
		int fpm = free_space>0 ? (free_space / (items.length()+1)) : 0;
		
		int text_height = 0;
		for(int i=0; i<items.length(); i++){
			try {
				bounds.top += fpm;
				JSONObject item = items.getJSONObject(i);
				String code  = item.getString(JSON_CODE);
				String title;
				if(item.isNull(JSON_TITLE)){
					Integer title_resid = getTitleResidByCode(code);
					title = (title_resid!=null)?context.getString(title_resid):code;
					item.put(JSON_TITLE, title);
				}
				else {
					title = item.getString(JSON_TITLE);
				}
				
				String type = null;
				if(item.has(JSON_TYPE)) {
					type = item.getString(JSON_TYPE);
				}
				
				if(type!=null) {
					if(type.equals(ITEM_TYPE_CHECKBOX)){
						boolean state = false;
						if(mml!=null) state = mml.getCheckboxState(this, code);
						title += " - " + (state?context.getString(R.string.on):context.getString(R.string.off));
					}
					else if(type.equals(ITEM_TYPE_LIST)){
						int state = 0;
						if(mml!=null) state = mml.getListState(this, code);
						Integer array_resid = getArrayResidByCode(code);
						if(array_resid!=null){
							String [] labels = context.getResources().getStringArray(array_resid);
							title += " - " + labels[state];
						}
						else {
							title += " - " + state;							
						}
					}
				}

				if(item.isNull(JSON_TEXT_SIZE)){
					text_paint.setTextSize(DEFAULT_TEXT_SIZE*scale);
				}
				else {
					text_paint.setTextSize(item.getInt(JSON_TEXT_SIZE)*scale);					
				}
				
				int padding  = 0;
				int padding2 = 0;
				if(!item.isNull(JSON_PADDING)){
					padding = (int)(item.getInt(JSON_PADDING)*scale);
				}
				if(type!=null && type.equals(ITEM_TYPE_SLIDEBAR)) {
					padding2 = padding;
					padding = 0;
				}
				
				text_height = (int)text_paint.getFontSpacing();
				if(i==0) {
					ascent = (int)text_paint.ascent();
					bounds.top -= ascent;
				}
				
				int tw = (int)text_paint.measureText(title);
				int tm = (bounds.right - bounds.left - tw)/2;
				int alpha = text_paint.getAlpha();
				boolean enabled = true;
				
				if(selected_item!=null && code.equals(selected_item.get(JSON_CODE)) && (!selected_item.isNull(JSON_SELECTABLE) && selected_item.getBoolean(JSON_SELECTABLE))) {
					text_paint.setColor(0xffffef3f);
					text_paint.setShadowLayer(3*scale, scale, scale, 0x6d661b | (alpha << 24));
					text_paint.setAlpha(alpha);
				}
				else {
					if(!item.isNull(JSON_ENABLED)) {
						enabled = item.getBoolean(JSON_ENABLED);
					}
					
					if(enabled){
						text_paint.setColor(ENABLED_TEXT_COLOR);
						text_paint.setShadowLayer(3*scale, scale, scale, 0x5d1901 | (alpha << 24));
						text_paint.setAlpha(alpha);
					}
					else {
						text_paint.setColor(DISABLED_TEXT_COLOR);						
						text_paint.setShadowLayer(3*scale, scale, scale, 0x5d1901 | (alpha/2 << 24));
						text_paint.setAlpha(alpha/2);
					}
					
				}
				
				canvas.drawText(title, bounds.left+tm, bounds.top-scroll_y+padding/2, text_paint);
				
				if(!enabled) {
					text_paint.setAlpha(alpha);					
				}
				
				Rect cr = new Rect(bounds.left+tm, bounds.top+ascent-scroll_y+padding/2, bounds.right-tm, bounds.top+ascent-scroll_y+padding/2+text_height);
				click_rect.put(code, cr);
				
				if(type!=null) {
					if(type.equals(ITEM_TYPE_SLIDEBAR)){
						float rate;
						if(mml!=null) rate = mml.getSlidebarRate(this, code);
						else rate = 0;
						
						int y1 = bounds.top;
						int margin = (int)(10*scale);
						int sb_height = (int)(40*scale);
						
						RectF sb_rect = new RectF(bounds.left+margin,y1+margin-scroll_y,bounds.right-margin,y1+sb_height-margin-scroll_y);
						cr.left = (int)sb_rect.left;
						cr.right = (int)sb_rect.right;
						
						canvas.drawRoundRect(sb_rect, 3*scale, 3*scale, slidebar_bg_paint);						
						sb_rect.right = sb_rect.left + rate*(sb_rect.right-sb_rect.left);
						canvas.drawRoundRect(sb_rect, 3*scale, 3*scale, slidebar_paint);
						
						cr.bottom = (int)sb_rect.bottom + (int)(padding2*scale);
						bounds.top += sb_height + (int)(padding2*scale);
					}
				}
				
				bounds.top += text_height + padding;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
		items_bottom = bounds.top - text_height;
		//canvas.drawLine(0, frame_y2, canvas.getWidth(), frame_y2, text_paint);
		//canvas.drawLine(0, items_height, canvas.getWidth(), items_height, text_paint);
		canvas.restore();
	}
}

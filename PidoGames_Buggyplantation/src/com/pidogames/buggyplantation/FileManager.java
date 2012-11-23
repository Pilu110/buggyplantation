package com.pidogames.buggyplantation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.interfaces.Constants;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.MainMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.util.Comparator;

public class FileManager implements Constants {
	private static FileManager instance;
	
	private static final String ROOT_DIR    = "vinchenzogames";
	
	public static final  String DIR_MAP = "maps";
	public static final  String DIR_MISSION = "missions";
	public static final  String DIR_GAME = "games";
	public static final  String DIR_CINEMATIC = "cinematics";
	
	public static final  String EXT_JSON       = "json";
	public static final  String EXT_HEADER     = "h";
	public static final  String EXT_DATA       = "d";
	public static final  String EXT_PNG		   = "png";
	public static final  String EXT_TEXT       = "txt";
	
	public static final int MAX_SCREENSHOT_SIZE = 300;
	
	public static final String JSON_NAME = "name";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_MISSION_STARTER = "mission_starter";
				
	private Context context;
	
	private OrderByDate orderByDate;
	
	private class OrderByDate implements Comparator<String> {
		
		@Override
		public int compare(String a, String b) {
			try {
				Long ad = Long.parseLong(a.substring(1, a.lastIndexOf('.')-1));
				Long bd = Long.parseLong(b.substring(1, b.lastIndexOf('.')-1));			
				return bd.compareTo(ad); //DESC
			}
			catch(Exception e) {
				return a.compareTo(b);
			}		
		}
	}
	
	private FileManager(Context context){
		this.context = context;
		orderByDate  = new OrderByDate();
	}
	
	public static FileManager getInstance(Context context){
		if(instance==null) instance = new FileManager(context);
		return instance;
	}

	public File getDir(String name){
		return getDir(name, true);
	}

	public File getDir(String name, boolean create){
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ROOT_DIR+"/"+name);
		//File dir = new File(context.getCacheDir().getAbsolutePath()+"/"+name);
		if(create && !dir.exists()) dir.mkdirs();
		return dir;
	}

	public String getFileName(String file, String ext){
		int li = file.lastIndexOf('.');
		return (li!=-1)?(file.substring(0, li+1) + ext):(file+'.'+ext);
	}
	
	/*
	public JSONArray getMissionList(String dir_path, String code_prefix, JSONArray items, boolean is_selectable) {
		
		if(items==null) items = new JSONArray();
		
		String [] dir_list = null;
		String dir_absolute_path = null;
		try {
			dir_list = context.getAssets().list(dir_path);
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		if(dir_list==null) {
			Toast.makeText(context, context.getString(R.string.sdcard_unavailable), Toast.LENGTH_SHORT).show();
			return null;
		}
		
		for(String file : dir_list){
			String ext = file.substring(file.lastIndexOf('.')+1);
			if(ext.equals(EXT_JSON)){
			
				JSONObject o = new JSONObject();
				try {
					o.put(DialogBox.JSON_CODE, code_prefix+"="+file);
					
					try {
						JSONObject header;
						header = loadJSON(new InputStreamReader(context.getAssets().open(dir_path + '/' + file)));
						o.put(DialogBox.JSON_TITLE, header.getString(JSON_NAME));
						o.put(DialogBox.JSON_DESCRIPTION, header.optString(JSON_DESCRIPTION, null));
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
					o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
					if(is_selectable) o.put(DialogBox.JSON_SELECTABLE, true);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(!o.has(DialogBox.JSON_TITLE)) {
					try {
						o.put(DialogBox.JSON_TITLE, "< ??? >");
					} catch (JSONException e) {
						e.printStackTrace();
					}				
				}
				
				items.put(o);
			}
		}
		return items;		
	}
	*/
	
	public JSONArray getFileList(String dir_path, String code_prefix, String file_prefix, JSONArray items, boolean is_selectable, boolean is_asset, boolean is_mission) {
		
		if(items==null) items = new JSONArray();
		
		String [] dir_list = null;
		String dir_absolute_path = null;
		if(is_asset){
			try {
				dir_list = context.getAssets().list(dir_path);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		else {
			File dir = getDir(dir_path);
			//clearDir(dir);
			dir_list = dir.list();
			dir_absolute_path = dir.getAbsolutePath();
		}
		
		if(dir_list==null) {
			Toast.makeText(context, context.getString(R.string.sdcard_unavailable), Toast.LENGTH_SHORT).show();
			return null;
		}
		
		if(!is_asset) Arrays.sort(dir_list, orderByDate);
		
		SharedPreferences prefs = is_mission?SceneView.getInstance(null).getPreferences():null;
		
		for(String file : dir_list){
			String ext = file.substring(file.lastIndexOf('.')+1);
			file = file.substring(0, file.length()-ext.length()-1);
			
			if(ext.equals(EXT_HEADER)){
				if(!is_mission || prefs.getBoolean(PREF_KEY_STARTED_SCENE_PREFIX+file_prefix+file, false)){
					JSONObject o = new JSONObject();
					try {
						o.put(DialogBox.JSON_CODE, code_prefix+"="+(file_prefix!=null?(file_prefix+file):file));
						
						try {
							JSONObject header;
							if(is_asset){
								header = loadJSONObject(new InputStreamReader(context.getAssets().open(dir_path + '/' + getFileName(file,EXT_HEADER))));
							}
							else {
								header = loadJSONObject(new File(dir_absolute_path + '/' + getFileName(file,EXT_HEADER)));							
							}
							o.put(DialogBox.JSON_TITLE, header.getString(JSON_NAME));
							o.put(DialogBox.JSON_DESCRIPTION, header.optString(JSON_DESCRIPTION, null));
							o.put(DialogBox.JSON_MISSION_STARTER, header.optString(JSON_MISSION_STARTER, null));
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
						if(is_selectable) o.put(DialogBox.JSON_SELECTABLE, true);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(!o.has(DialogBox.JSON_TITLE)) {
						try {
							o.put(DialogBox.JSON_TITLE, "< ??? >");
						} catch (JSONException e) {
							e.printStackTrace();
						}				
					}
					
					items.put(o);
				}
			}
		}
		return items;
	}
	
	private void clearDir(File dir){
		for(String fn : dir.list()){
			File f = new File(dir.getAbsolutePath() + '/' + fn);
			if(f.isFile() && f.exists()) f.delete();
		}
	}
	
	public boolean rename(String dir_path, String file, String name){
		File dir = getDir(dir_path);
		try {
			File f = new File(dir.getAbsolutePath() + '/' + getFileName(file, EXT_HEADER));
			
			JSONObject header = loadJSONObject(f);						
			header.put(JSON_NAME, name);			
			saveJSON(f, header);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void saveJSON(File file, JSONObject o) throws IOException {
		PrintWriter o_fil = new PrintWriter(file);
		o_fil.write(o.toString());
		o_fil.flush();
		o_fil.close();
		Log.d("FILE","SUCCESFUL WRITE TO: "+file.getAbsolutePath());		
	}
	
	private JSONObject loadJSONObject(File f) throws IOException, JSONException {
		return loadJSONObject(new FileReader(f));
	}
	
	private JSONObject loadJSONObject(Reader in) throws IOException, JSONException {
		return new JSONObject(load(in));
	}
	
	private JSONArray loadJSONArray(File f) throws IOException, JSONException {
		return loadJSONArray(new FileReader(f));
	}
	
	private JSONArray loadJSONArray(Reader in) throws IOException, JSONException {
		return new JSONArray(load(in));
	}
	
	private String load(Reader in) throws IOException {
		BufferedReader input = new BufferedReader(in);
		String line;
		StringBuilder sb = new StringBuilder();
		while((line = input.readLine())!=null){
			sb.append(line);
		}		
		input.close();
		
		return sb.toString();
	}
	
	
	public boolean save(String dir_path, String file, String name, String description, boolean set_map_file){
		
		Log.d("FILE","SFILE:"+file);
		try {
			File f = new File(getDir(dir_path).getAbsolutePath()+"/"+getFileName(file, EXT_HEADER));
			JSONObject o = new JSONObject();
			o.put(JSON_NAME, name);
			o.put(JSON_DESCRIPTION, description);
			saveJSON(f, o);
			
			if(set_map_file) SceneView.getScene().setCurrentMapFile(file);
			f = new File(getDir(dir_path).getAbsolutePath()+"/"+getFileName(file, EXT_DATA));
			o = SceneView.getInstance(null).getThread().toJSON();
			saveJSON(f, o);
			
			Rect bounds = SceneView.getMenuInstance().getLastBounds();
			int size = bounds.right - bounds.left;
			if(size>MAX_SCREENSHOT_SIZE){
				size -= MAX_SCREENSHOT_SIZE;
				size /= 2;
				bounds.left  += size;
				bounds.right -= size;
			}
			size = bounds.bottom - bounds.top;
			if(size>MAX_SCREENSHOT_SIZE){
				size -= MAX_SCREENSHOT_SIZE;
				size /= 2;
				bounds.top  += size;
				bounds.bottom -= size;
			}
			
			Bitmap bm = SceneView.getInstance(null).getThread().captureSreenShot(bounds);			
			Log.d("SCREENSHOT","OPEN FILE..");
			File of = new File(getDir(dir_path).getAbsolutePath()+"/"+getFileName(file, EXT_PNG));
			FileOutputStream fos = new FileOutputStream(of);
			Log.d("SCREENSHOT","SAVE TO FILE..");
			bm.compress(CompressFormat.PNG, 1, fos);
			Log.d("SCREENSHOT","SUCCESSFULLY SAVED...");
			fos.flush();
			fos.close();
			bm.recycle();
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public JSONObject loadHeader(String dir_path, String fn, boolean is_asset){
		try {
			Log.d("FILE","L FILE HEADER:"+fn);			
			if(is_asset){
				return loadJSONObject(new InputStreamReader(context.getAssets().open(dir_path + '/' + getFileName(fn,EXT_HEADER))));
			}
			else {
				return loadJSONObject(new File(getDir(dir_path).getAbsolutePath()+'/'+getFileName(fn, EXT_HEADER)));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean loadCinematicScene(Cinematic cinematic) {		
		try {
			JSONArray text = null;
			Bitmap background = null;
			
			if(cinematic.isAsset()){
				try {
					text = loadJSONArray(new InputStreamReader(context.getAssets().open(DIR_CINEMATIC+'/'+cinematic.getName()+'/'+SceneView.getLanguage()+'/'+getFileName(cinematic.getScene()+"", EXT_TEXT))));
				} catch(FileNotFoundException e){
					text = loadJSONArray(new InputStreamReader(context.getAssets().open(DIR_CINEMATIC+'/'+cinematic.getName()+'/'+SceneView.getDefaultLanguage()+'/'+getFileName(cinematic.getScene()+"", EXT_TEXT))));
				}
				
				background = BitmapFactory.decodeStream(context.getAssets().open(DIR_CINEMATIC+'/'+cinematic.getName()+'/'+getFileName(cinematic.getScene()+"", EXT_PNG)));
				
			}
			else {
				File dir = getDir(DIR_CINEMATIC+'/'+cinematic.getName()+'/'+SceneView.getLanguage(), false);
				if(!dir.exists()) dir = getDir(DIR_CINEMATIC+'/'+cinematic.getName()+'/'+SceneView.getDefaultLanguage(), false);
				text = loadJSONArray(new File(dir.getAbsolutePath()+'/'+getFileName(cinematic.getScene()+"", EXT_TEXT)));
				
				background = BitmapFactory.decodeFile(getDir(DIR_CINEMATIC+'/'+cinematic.getName()).getAbsolutePath()+'/'+getFileName(cinematic.getScene()+"", EXT_PNG));
			}
			
			cinematic.onLoadScene(text, background);
			return true;
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean load(String dir_path, String fn, boolean is_asset, boolean is_mission){
		try {
			Log.d("FILE","LFILE:"+fn);
			JSONObject header;
			JSONObject data;
			if(is_asset){
				header = loadJSONObject(new InputStreamReader(context.getAssets().open(dir_path + '/' + getFileName(fn,EXT_HEADER))));
				data   = loadJSONObject(new InputStreamReader(context.getAssets().open(dir_path + '/' + getFileName(fn,EXT_DATA))));
			}
			else {
				header = loadJSONObject(new File(getDir(dir_path).getAbsolutePath()+'/'+getFileName(fn, EXT_HEADER)));
				data   = loadJSONObject(new File(getDir(dir_path).getAbsolutePath()+"/"+getFileName(fn, EXT_DATA)));
			}
			
			String description = header.optString(JSON_DESCRIPTION, null);
			SceneView.getInstance(null).getThread().loadScene(data, description, is_asset, is_mission);
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}


}

package com.pidogames.buggyplantation;
import java.util.HashMap;

import com.pidogames.buggyplantation.SceneView.SceneThread;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;

public class SoundManager implements Constants {
	
	
	class Sound {
		
		Sound(int id){
			this.id = id;
		}
		
		int id;
	}
	
	public static final int MAX_STREAMS  = 10;
	public static final int MAX_DISTANCE = 500;
	public static final float MIN_SHIFT  = (float)0.1;
	public static final float MAX_SHIFT  = (float)0.8;
	
	private static final HashMap<Integer, Float> sound_volume = new HashMap<Integer, Float>();
	static {
		sound_volume.put(R.raw.bug_eat1, (float)0.5);
		sound_volume.put(R.raw.bug_eat2, (float)0.5);
	}
	
	private  SoundPool sp; 
	private  HashMap<Integer, Sound> sounds; 
	private  AudioManager am;
	private  Context context;
	
	private float volume_rate;
	
	private static SoundManager instance;
	public static SoundManager getInstance(Context context){
		if(instance==null) instance = new SoundManager(context);
		return instance;
	}
	
	public static void playIt(int id, Coord position){
		if(instance!=null) instance.play(id, position);
	}
	
	public float getVolumeRate(){
		return volume_rate;
	}
	
	public void setVolumeRate(float rate){
		SharedPreferences pref = SceneView.getInstance(null).getPreferences();
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if(pref.edit().putFloat(PREF_KEY_SOUND_VOLUME, rate).commit()){
			volume_rate = rate;
		}
	}
	
	public static void stopIt(int id){
		if(instance!=null) instance.stop(id);		
	}
	
	public synchronized void stop(int id){
		Sound sound = sounds.get(id);
		if(sound!=null) {
			sp.stop(sound.id);
		}
	}
	
	public synchronized Sound load(int id){
		Sound sound = sounds.get(id);
		if(sound==null) {
			sound = new Sound(sp.load(context, id, 1));
			sounds.put(id, sound);
		}
		return sound;
	}
	
	public synchronized void play(int id, Coord position){
		if(volume_rate==0) return;
		
		Sound sound = load(id);
		if(sound==null) {
			sound = new Sound(sp.load(context, id, 1));
			sounds.put(id, sound);
		}
		
		SceneView sw = SceneView.getInstance(null);
		if(sw!=null){
			float volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			volume = volume / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			Log.d("STEREO","VOLUME:"+volume);
			SceneThread thread = sw.getThread();
			Coord center = thread.getAbsoluteCenter();
			//if(position==null) position = center;
			if(position!=null){
				float d = FloatMath.sqrt(center.d2(position));
				if(d<=MAX_DISTANCE){
					double angle = center.angle(position);
					double da = 0;
					boolean is_right = true;
					
					if(DEBUG && position!=null) SceneView.getScene().addDebugVector(new Vector(center.x,center.y,180.0/Math.PI * angle, 0xffffff00));
					Log.d("STEREO","A:"+angle);
					if(angle<=Math.PI) da = Math.abs(angle - Math.PI/2);
					else {
						da = Math.abs(angle - 3*Math.PI/2);
						is_right = false;
					}
					
					float shift = d / MAX_DISTANCE * (MAX_SHIFT - MIN_SHIFT) + MIN_SHIFT;
					da = (Math.PI/2 - da)/(Math.PI/2)*shift;
					
					float vl,vr;
					if(volume<0) volume=0;
					else if(volume>1) volume=1;
					if(is_right){
						vl = volume-(float)da;
						vr = volume+(float)da;				
					}
					else {
						vl = volume+(float)da;
						vr = volume-(float)da;
					}
					double dr = volume_rate*(1.0-d/MAX_DISTANCE);
					Float sv = sound_volume.get(id);
					if(sv!=null) dr *= sv;
					
					if(vl<0) vl=0;
					else if(vl>1) vl=1;
					if(vr<0) vr=0;
					else if(vr>1) vr=1;
					vl *= dr;
					vr *= dr;
					Log.d("STEREO","VL:"+vl+", VR:"+vr);
					sp.play(sound.id, vl, vr, (int)(MAX_DISTANCE - d), 0, 1f);
				}
			}
			else {
				sp.play(sound.id, volume*volume_rate, volume*volume_rate, MAX_DISTANCE, 0, 1f);				
			}
		}
	}
	
	private SoundManager(Context context){
		this.context = context;
		sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0); 
		sounds = new HashMap<Integer,Sound>(); 
		am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE); 	     
		
		SharedPreferences pref = SceneView.getInstance(null).getPreferences();
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		volume_rate = pref.getFloat(PREF_KEY_SOUND_VOLUME, 1);
	}
		
	public synchronized void clearSounds(){
		sounds.clear();
		sp.release();
		sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
	}
	
}
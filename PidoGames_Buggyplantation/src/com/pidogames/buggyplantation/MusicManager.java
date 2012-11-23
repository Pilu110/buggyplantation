package com.pidogames.buggyplantation;

import com.pidogames.buggyplantation.interfaces.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.JetPlayer;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

public class MusicManager implements Constants /*, JetPlayer.OnJetEventListener*/ {
	//private JetPlayer jet_player;
	private MediaPlayer media_player;
	private Context context;
	private boolean is_initialized;
	
	private float volume_rate;
	
	private static MusicManager instance;
	public static MusicManager getInstance(Context context){
		if(instance == null) instance = new MusicManager(context);
		return instance;
	}
	
	public float getVolumeRate(){
		return volume_rate;
	}
	
	public void setVolumeRate(float rate){
		SharedPreferences pref = SceneView.getInstance(null).getPreferences();
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if(pref.edit().putFloat(PREF_KEY_MUSIC_VOLUME, rate).commit()){
			volume_rate = rate;
			media_player.setVolume(volume_rate, volume_rate);
			if(volume_rate==0 && media_player.isPlaying()) pause();
			else if(volume_rate>0 && !media_player.isPlaying()) play();
		}
	}
	
	private MusicManager(Context context){
		this.context = context;
		
		SharedPreferences pref = SceneView.getInstance(null).getPreferences();
		//PreferenceManager.getDefaultSharedPreferences(context);
		volume_rate = pref.getFloat(PREF_KEY_MUSIC_VOLUME, 1);
		
		/*
		jet_player = JetPlayer.getJetPlayer();
		jet_player.clearQueue();
		jet_player.setEventListener(this);
		*/
		
		is_initialized = false;
	}
	
	
	public void init(){
		/*
		jet_player.loadJetFile(context.getResources().openRawResourceFd(R.raw.level1));
        byte sSegmentID = 0;
		jet_player.queueJetSegment(0, -1, -1, 0, 0, sSegmentID);		
		*/
		media_player = MediaPlayer.create(context, R.raw.track1);		
		media_player.setLooping(true);
		is_initialized = true;
	}
	
	public boolean isInitialized(){
		return is_initialized;
	}
	
	public void play(){
		if(volume_rate>0){
			media_player.setVolume(volume_rate, volume_rate);
			media_player.start();
			//jet_player.play();
		}
	}
	
	public void pause(){
		//jet_player.pause();
		media_player.pause();
	}
	
	/*

	@Override
	public void onJetEvent(JetPlayer player, short segment, byte track,
			byte channel, byte controller, byte value) {
	}

	@Override
	public void onJetNumQueuedSegmentUpdate(JetPlayer player, int nbSegments) {
	}

	@Override
	public void onJetPauseUpdate(JetPlayer player, int paused) {
	}

	@Override
	public void onJetUserIdUpdate(JetPlayer player, int userId, int repeatCount) {
	}
	*/
}

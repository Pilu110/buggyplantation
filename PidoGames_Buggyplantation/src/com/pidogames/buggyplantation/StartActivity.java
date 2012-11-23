package com.pidogames.buggyplantation;

import com.pidogames.buggyplantation.SceneView.SceneThread;
import com.pidogames.buggyplantation.interfaces.EditTextListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class StartActivity extends Activity implements OnGestureListener {
	
	/*
	private static final int MENU_ZOOM_IN  = 1;
	private static final int MENU_ZOOM_OUT = 2;
	private static final int MENU_DEBUG    = 3;
	*/
	
	private SceneView sceneView;
	private GestureDetector gestureScanner;
	private int prev_event_type;
	
	private static final int NOTHING = 0;
	private static final int ZOOM    = 1;
	private int motion_mode;
	private float old_dist;
	private double saved_zoom;
	private Coord mid;
    private EditText editText;
    private EditTextListener editTextListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
        // turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Context c = getBaseContext();
		float scale = getResources().getDisplayMetrics().density;
		
        RelativeLayout root = new RelativeLayout(c);
        root.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        
        sceneView = SceneView.getInstance(this);
        sceneView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        
        editText = new EditText(c);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin=(int)(20*scale);
        params.rightMargin=(int)(20*scale);
        params.topMargin=(int)(20*scale);
        editText.setLayoutParams(params);
        editText.setSingleLine(true);
    	editText.setVisibility(View.GONE);
    	
        root.addView(sceneView);
        root.addView(editText);
        setContentView(root);
        
        motion_mode = NOTHING;
        mid = new Coord();
        gestureScanner = new GestureDetector(this);
        
        editText.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				TransformationMethod tm = editText.getTransformationMethod();
				boolean singleLine = tm!=null && (tm instanceof SingleLineTransformationMethod);
				if(keyCode == KeyEvent.KEYCODE_ENTER && singleLine){
					if(editTextListener!=null){
						editTextListener.OnTextEntered(editText.getText().toString());
					}
					
					editText.setVisibility(View.GONE);					
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					return true;
				}
				else if(keyCode == KeyEvent.KEYCODE_BACK){
					if(!singleLine && editTextListener!=null){
						editTextListener.OnTextEntered(editText.getText().toString());
					}
					
					editText.setVisibility(View.GONE);
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					return true;					
				}
				return false;
			}
        });
    }
    
    public void showEditTextInput(String text, boolean singleLine, EditTextListener editTextListener){
    	if(text==null) text = "";
    	this.editTextListener = editTextListener;
        editText.setSingleLine(singleLine);
        if(!singleLine) editText.setLines(10);
    	editText.setText(text);
    	editText.setSelection(text.length());
    	editText.setVisibility(View.VISIBLE);
    	editText.selectAll();
    	editText.requestFocus();
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ZOOM_IN, 0, R.string.menu_zoom_in);
        menu.add(0, MENU_ZOOM_OUT, 0, R.string.menu_zoom_out);
        menu.add(0, MENU_DEBUG, 0, R.string.menu_debug);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
        switch (item.getItemId()) {
            case MENU_ZOOM_IN:
            	SceneThread t = scene.getThread();
            	t.setZoom(t.getZoom()+0.2);
            break;
            case MENU_ZOOM_OUT:
            	t = scene.getThread();
            	if(t.getZoom()>0.2) t.setZoom(t.getZoom()-0.2);
            break;
            case MENU_DEBUG:
            	t = scene.getThread();
            	t.setDisplayDebug(!t.getDisplayDebug());
            break;
        }
        return true;
    }
    */
    
    @Override
    public boolean onTouchEvent(MotionEvent me) {
    	//dumpEvent(me);
    	int type = me.getAction() & MotionEvent.ACTION_MASK;
    	boolean handled = false;
    	switch(type){
    		case MotionEvent.ACTION_POINTER_DOWN:
    			old_dist = spacing(me);
    			if (old_dist > 10f) {
                	SceneThread t = sceneView.getThread();
    				saved_zoom = t.getZoom();
    				midPoint(mid, me);
    				motion_mode = ZOOM;
    				handled = true;
    			}
	    	break;

    		case MotionEvent.ACTION_MOVE:
				if (motion_mode == ZOOM) {
					float new_dist = spacing(me);
					if (new_dist > 10f) {
						float scale = new_dist / old_dist;
	                	SceneThread t = sceneView.getThread();
	                	t.setZoom(saved_zoom*scale, mid);
	    				handled = true;
					}
				}
	    	break;
			
    		case MotionEvent.ACTION_UP:
            	SceneThread t = sceneView.getThread();
    			t.onUp();
    		case MotionEvent.ACTION_POINTER_UP:
    			if(motion_mode==ZOOM) {
    				motion_mode=NOTHING;
    				handled = true;
    			}
    			else {
    				if(type==MotionEvent.ACTION_UP && (prev_event_type == MotionEvent.ACTION_POINTER_UP)){
        				handled = true;
    				}
    			}
	    	break;
    		
    	}
    	
		if(motion_mode == NOTHING && !handled) return gestureScanner.onTouchEvent(me);
    
		prev_event_type = type;
		return true;
    }

    
    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event) {
       String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
          "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
       StringBuilder sb = new StringBuilder();
       int action = event.getAction();
       int actionCode = action & MotionEvent.ACTION_MASK;
       sb.append("event ACTION_" ).append(names[actionCode]);
       if (actionCode == MotionEvent.ACTION_POINTER_DOWN
             || actionCode == MotionEvent.ACTION_POINTER_UP) {
          sb.append("(pid " ).append(
          action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
          sb.append(")" );
       }
       sb.append("[" );
       for (int i = 0; i < event.getPointerCount(); i++) {
          sb.append("#" ).append(i);
          sb.append("(pid " ).append(event.getPointerId(i));
          sb.append(")=" ).append((int) event.getX(i));
          sb.append("," ).append((int) event.getY(i));
          if (i + 1 < event.getPointerCount())
             sb.append(";" );
       }
       sb.append("]" );
       Log.d("MOTIONEVENT", sb.toString());
    }    
    
    private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
    }
    
    private void midPoint(Coord point, MotionEvent event) {
		point.set((int)((event.getX(0) + event.getX(1))/2), (int)((event.getY(0) + event.getY(1))/2));
    }
    
    @Override
	public boolean onDown(MotionEvent e) {
		SceneThread t = sceneView.getThread();
		return t.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		SceneThread t = sceneView.getThread();
		return t.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		SceneThread t = sceneView.getThread();
		return t.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//Toast.makeText(getBaseContext(), "TAP:"+e.getX()+", "+e.getY(), 1000).show();
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		SceneThread t = sceneView.getThread();
		t.onTapXY((int)e.getX(), (int)e.getY());
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			if(editText.getVisibility()==View.GONE){
				SceneThread t = sceneView.getThread();
				if(t.isMainMenuVisible()) t.closeMainMenu();
				else t.displayMainMenu();
			}
			else {
				editText.setVisibility(View.GONE);
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);				
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneThread t = sceneView.getThread();
			if(t.isMainMenuVisible()){
				if(t.backMainMenu()) return true;
				else t.showExitSureDialog();
				return true;
			}
			else if(t.isReachGridVisible()){
				t.cancelReachGrid();
				return true;
			}
			else if(t.getGroupSelect()!=null || t.getDialogEvent()!=null){
				t.displayMainMenu();
				return true;
			}
			else if(t.getCommandMode()!=SceneView.COMMAND_NONE){
				t.clearCommandMode();
				return true;
			}
			else if(t.isItemSelected()){
				t.closeMenu();
				t.clearItemSelection();
				return true;
			}
			else if(t.isPlacerVisible()){
				t.closePlacer();
				return true;
			}
			else if(t.isMenuVisible()){
				t.closeMenu();
				return true;
			}
			
			t.showExitSureDialog();
			return true;
			
			/*
			ViewGroup p = (ViewGroup)(sceneView.getParent());
			if(p!=null) p.removeView(sceneView);
			*/
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	void exit(){
		sceneView.clearInstance();
		finish();
		//this.onDestroy();
	}
	*/
	
}
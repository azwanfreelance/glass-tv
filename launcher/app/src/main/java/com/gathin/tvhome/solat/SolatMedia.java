package com.gathin.tvhome.solat;
import android.content.*;import android.media.session.*;import android.provider.Settings;
public final class SolatMedia {
 public static boolean pause(Context c){if(!SolatStore.prefs(c).getBoolean("pause",true))return false;
  try{MediaSessionManager m=c.getSystemService(MediaSessionManager.class);for(MediaController controller:m.getActiveSessions(new ComponentName(c,SolatMediaListener.class))){PlaybackState s=controller.getPlaybackState();if(s!=null&&s.getState()==PlaybackState.STATE_PLAYING&&(s.getActions()&PlaybackState.ACTION_PAUSE)!=0){controller.getTransportControls().pause();return true;}}}catch(RuntimeException unavailable){}return false;
 }
 // Deliberately no PLAY, media-key toggles, Accessibility, force-stop or shell operations.
}

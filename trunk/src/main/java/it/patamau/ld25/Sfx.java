package it.patamau.ld25;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * A more reliable sound effects class
 * with some threading to wrap the clip start and preloading the stream which is waaay better
 * @author Rogue
 *
 */
public class Sfx {

	public static final String SFX_FOLDER = "sfx";

	public final String name;
	private Clip clip;
	
	public Sfx(final String soundFileName) {
		this.name = soundFileName;
		URL url = Sfx.class.getResource("/"+SFX_FOLDER+"/"+name+".wav");
		if(url==null){
			throw new NullPointerException("No such sound "+soundFileName);
		}
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(clip==null){
			throw new NullPointerException("Unable to load clip "+soundFileName);
		}
	}
	
	public void play(){
		//System.err.println(this+" play()");
		synchronized(clip){
			if(clip.isRunning() || clip.isActive()) {
				clip.stop();
				//System.err.println(this+" was running, stopped and reset");
			}
			clip.setFramePosition(0);
			clip.setMicrosecondPosition(0);
			clip.start();
		}
		//System.err.println(this+" started()");
	}
}

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
	
	public boolean exit = false; //this this to true when application quits

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
	}
	
	/**
	 * Trigger the thread loop
	 */
	public void play(){
		if (clip != null) {
			new Thread() {
				public void run() {
					synchronized (clip) { //this is the trick!
						clip.stop();
						clip.setFramePosition(0);
						clip.setMicrosecondPosition(0);
						clip.start();
					}
				}
			}.start();
		}
	}
}

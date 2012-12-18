package it.patamau.ld25;

import org.junit.Test;

public class TestColor {

	@Test
	public void colorTest(){
		Color4i c = new Color4i(255,255,255,255);
		String sc = Integer.toHexString(c.argb);
		System.err.println(c+" = 0x"+sc);
		c.setRed(0);
		sc = Integer.toHexString(c.argb);
		System.err.println(c+" = 0x"+sc);
	}
	
	@Test
	public void blendTest(){
		Color4i c = new Color4i(0xFF00FF00);
		String sc = Integer.toHexString(c.argb);
		System.err.println(c+" = 0x"+sc);
		c.setRed(0);
		sc = Integer.toHexString(c.argb);
		System.err.println(c+" = 0x"+sc);
	}
}

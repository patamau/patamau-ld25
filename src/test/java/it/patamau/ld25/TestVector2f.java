package it.patamau.ld25;

import org.junit.Test;

public class TestVector2f {

	@Test
	public void testNormalize(){
		Vector2f v = new Vector2f(10f,10f);
		System.out.println(v);
		v.normalize();
		System.out.println(v);
		v.mul(100f);
		System.out.println(v);
	}
}

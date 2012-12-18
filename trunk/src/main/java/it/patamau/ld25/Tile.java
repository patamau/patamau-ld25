package it.patamau.ld25;

public class Tile{
	
	private static final Tile[] tiles;
	static {
		tiles = new Tile[2];
		tiles[0] = new Tile(new Color4i(250,250,250,0), false); //floor
		tiles[1] = new Tile(new Color4i(50,50,50), true); //wall
	}
	
	public Color4i color;
	public boolean obstacle;
	
	private Tile(final Color4i c, final boolean o){
		this.color = c;
		this.obstacle = o;
	}

	static Tile getTile(final int i){
		return tiles[i%tiles.length];
	}
}

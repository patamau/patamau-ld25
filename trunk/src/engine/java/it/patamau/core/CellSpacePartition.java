package it.patamau.core;

import it.patamau.util.Vector2f;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Divide the space into cells
 *  @author Rogue
 *
 */
public class CellSpacePartition {

	private final int cellsize; //how big a cell actually is, used to transform from real coordinates to cellspace coordinates
	private final CellSpace[] cells; //where the cells are
	private final CellSpace[] support; //temporary workspace for cells, manly a copy of cells
	
	public CellSpacePartition(int cellsize){
		this.cellsize = cellsize;
		//initialize memory for cells
		cells = new CellSpace[9];
		support = new CellSpace[9];
		initCells(cells, 0, 0 , true);
		initCells(support, 0, 0, false);
	}
	
	/**
	 * Empty the cells entities lists (if any)
	 * @param cells
	 */
	private void clear(final CellSpace[] cells){
		for(int i=0; i<9; ++i){
			if(cells[i].entities!=null){
				cells[i].entities.clear();
			}
		}
	}
	
	private void copyCells(final CellSpace[] src, final CellSpace[] dest){
		for(int i=0; i<9; ++i){
			dest[i].entities=src[i].entities;
			dest[i].offsetx=src[i].offsetx;
			dest[i].offsety=src[i].offsety;
		}
	}
	
	/**
	 * Load the content of the source cells
	 * to the prepared destination cells
	 * where coordinates changed.
	 * @param src
	 * @param dest
	 * @param destx
	 * @param desty
	 */
	private void shift(final CellSpace[] src, final CellSpace[] dest, final int destx, final int desty, final int dx, final int dy){
		System.err.println("Loading cells for "+destx+"x"+desty+" with delta "+dx+","+dy);
		int x, y, i, di, dix, diy;
		for(y=-1; y<2; ++y){
			for(x=-1; x<2; ++x){
				i = x+y*3+4;
				
				dest[i].offsetx=destx+x;
				dest[i].offsety=desty+y;
				
				dix = x+dx;
				diy = y+dy;				
				if(dix<-1||dix>1||diy<-1||diy>1){
					//cell fault!
					dix%=3;
					diy%=3;
					dix = dix>1?dix-3:dix<-1?dix+3:dix;
					diy = diy>1?diy-3:diy<-1?diy+3:diy;
					di = dix+diy*3+4;
					System.err.println("Destination is new @ "+dest[i]+" copying memory from cell "+dix+"x"+diy+" @ "+di);
					src[di].entities.clear(); //in this case old memory must be cleared before copy unless we want to preserve the entities
					dest[i].entities = src[di].entities;
					src[di].entities=null;
				}else{
					di = dix+diy*3+4;
					System.err.println("Copying "+(dest[i].entities!=null?dest[i].entities.size():"<null>")+" entities from "+dix+"x"+diy+"["+di+"] to "+x+"x"+y+"["+i+"]");
					dest[i].entities = src[di].entities;
					src[di].entities=null;
				}
			}
		}
	}
	
	/**
	 * Initialize the cells the cells 
	 * using offx and offy as the coordinates of central cell
	 * @param offx
	 * @param offy
	 */
	private void initCells(final CellSpace[] cells, final int offx, final int offy, boolean allocate){
		int x, y, i;
		for(y=-1; y<2; ++y){
			for(x=-1; x<2; ++x){
				final CellSpace cs = new CellSpace();
				cs.offsetx=x+offx;
				cs.offsety=y+offy;
				if(allocate){
					cs.entities=new HashMap<String,Entity>();
				}
				i = x+y*3+4;
				cells[i] = cs;
				System.err.println("Loaded cell @ "+cs.offsetx+"x"+cs.offsety+" (index "+i+")");
			}
		}
	}
	
	/**
	 * Get the index of the cell in the loaded cells buffer frame.
	 * The current offset is automatically retrieved from the cell[4] cell (which is the central cell)
	 * @param x
	 * @param y
	 * @return
	 */
	private int getCellCoordinate(final int x, final int y){
		final CellSpace cs = cells[4];
		return (x-cs.offsetx)+(y-cs.offsety)*3+4;
	}
	
	/**
	 * Set the position where the space is centered on.
	 * This assumes that the cell is loaded and the 
	 * surrounding 8 cells are loaded as well.
	 * The remaining cells can be discarded 
	 * @param pos
	 */
	public void setCenter(final Vector2f pos){
		int ofx = (int)Math.floor(pos.x/cellsize);
		int ofy = (int)Math.floor(pos.y/cellsize);
		System.err.println("setCenter(): offset "+ofx+"x"+ofy);
		final CellSpace center = cells[4];
		int dx = ofx-center.offsetx;
		int dy = ofy-center.offsety;
		if(dx!=0||dy!=0){
			System.err.println("CellSpace update for "+ofx+"x"+ofy);
			shift(cells, support, ofx, ofy, dx, dy);
			copyCells(support, cells);
		}
	}
	
	/**
	 * Fills the given list with the entities from the cells.
	 * @param entities
	 */
	public void getEntities(final List<Entity> entities){
		for(int i=0; i<cells.length; ++i){
			entities.addAll(cells[i].getEntities());
		}
	}
	
	public void getEntity(final String id){
		
	}
	
	public void addEntity(final Entity ent){
		int ofx = (int)Math.floor(ent.pos.x/cellsize);
		int ofy = (int)Math.floor(ent.pos.y/cellsize);
		int i = getCellCoordinate(ofx, ofy);
		if(i<0||i>8){
			System.err.println(ent+" rejected because doesn't fit in the currently loaded cellspace");
		}else{
			cells[i].entities.put(ent.id, ent);
		}
	}
	
	public void dump(PrintStream stream){
		for(int i=0; i<9; ++i){
			stream.println(cells[i]);
		}
	}
}

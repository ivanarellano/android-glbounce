package ivan.game.bounce;

/** 
 * Base class defining the core set of information necessary to render (and move
 * an object on the screen.  This is an abstract type and must be derived to
 * add methods to actually draw (see GLSprite).
 */
public abstract class Renderable extends BasePhysics
{
    // Position
    public Vector3 pos = new Vector3();
    
	// Last Position
	public Vector3 oldPos = new Vector3();

    // Acceleration
    public Vector3 accel = new Vector3();
    
    // Size
    public short width;
    public short height;
}
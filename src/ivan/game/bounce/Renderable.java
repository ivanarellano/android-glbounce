package ivan.game.bounce;

/** 
 * Base class defining the core set of information necessary to render (and move
 * an object on the screen.  This is an abstract type and must be derived to
 * add methods to actually draw (see GLSprite).
 */
public abstract class Renderable extends BasePhysics
{
    // Position
    public Vec2 pos = new Vec2();
    
	// Last Position
	public Vec2 oldPos = new Vec2();
    
    // Velocity
    public Vec2 vel = new Vec2();
	
    // Size
    public short width;
    public short height;
}
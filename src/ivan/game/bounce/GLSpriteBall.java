package ivan.game.bounce;

/**
 * A sprite with circle information
 */
public class GLSpriteBall extends GLSprite {
	// Radius
	public float mRadius;
	
	// Center of ball
	public Vec2 mCenter;

	public GLSpriteBall(int resourceId) {
		super(resourceId);
		
		mCenter = new Vec2(mRadius + pos.x, mRadius + pos.y);
	}
}
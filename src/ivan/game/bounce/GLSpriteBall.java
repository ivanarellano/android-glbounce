package ivan.game.bounce;

public class GLSpriteBall extends GLSprite {
	public float mRadius;
	public Vec2 mCenter;

	public GLSpriteBall(int resourceId) {
		super(resourceId);
		
		mCenter = new Vec2(mRadius + pos.x, mRadius + pos.y);
	}
}
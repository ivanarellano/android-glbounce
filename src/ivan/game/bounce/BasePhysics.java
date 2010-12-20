package ivan.game.bounce;

/**
 * Basic physics variables
 */
public class BasePhysics {
	public float RESTITUTION;
    public float FRICTION;
	public float MASS;
	
	public BasePhysics() {
		RESTITUTION = 0.8f;
		FRICTION = 0.7f;
		MASS = 200.0f;
	}
}
package ivan.game.bounce;

public class BasePhysics {
	public float COEFFICIENT_OF_RESTITUTION;
    public float FRICTION;
	public float MASS;
	
	public BasePhysics() {
		COEFFICIENT_OF_RESTITUTION = 0.8f;
		FRICTION = 0.7f;
		MASS = 700.0f;
	}
}
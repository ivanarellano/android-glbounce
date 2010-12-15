package ivan.game.bounce;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

public class GLSprite extends Renderable {
	// The OpenGL ES texture handle to draw.
    public int mTextureHandle;

	// The id of the original resource that mTextureName is based on.
    public int mResourceID;
    
    public GLSprite() {
        super();
    }
    
    public GLSprite(int resourceID) {
        super();
        mResourceID = resourceID;
    }
    
	public void draw(GL10 gl) {      
		// Bind the texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureHandle);

		((GL11Ext) gl).glDrawTexfOES(pos.x, pos.y, pos.z, (float)width, (float)height);
	}
    
}
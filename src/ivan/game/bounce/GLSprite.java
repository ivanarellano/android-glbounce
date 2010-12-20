package ivan.game.bounce;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

/**
 * Simple texture holder that can draw itself
 * without vertex or texture uv arrays.
 */
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
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		// Bind the texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureHandle);
        
        // Draws a texture rectangle to the screen
		((GL11Ext) gl).glDrawTexfOES(pos.x, pos.y, 0.0f, (float)width, (float)height);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
    
}
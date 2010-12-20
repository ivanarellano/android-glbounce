package ivan.game.bounce;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Renders on a dedicated thread and does the actual rendering.
 */
class GLRenderer implements GLSurfaceView.Renderer {
	// Helper for resource import stream
	public Context mContext;
	
	// Reference to parent view
	public GLSurfaceViewInput mSV;
	
	// Contains balls and block to draw
	public GLSpriteBall[] mBallSprites;
	public GLRectangle mBlock;
	
	// Temporary texture storage
	public int[] mTextureNameWorkspace;
	public int[] mCropWorkspace;
	
	public boolean mAnimate;

	GLRenderer(Context context, GLSurfaceViewInput parentView) {
		mContext = context;
		mSV = parentView;
		
		mTextureNameWorkspace = new int[1];
		mCropWorkspace = new int[4];
		mAnimate = false;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        // For every ball in the array, load its texture, set its position,
        // and give it a radius. OldPos vector is for a Verlet integrator.
		if(mBallSprites != null) {
			for(GLSpriteBall b1 : mBallSprites) {
				b1.mTextureHandle = loadGLTexture(gl, mContext, b1);
				
				b1.pos.x = (float)(Math.random() * (mSV.mViewWidth - b1.width));
				b1.pos.y = (float)(Math.random() * (mSV.mViewHeight - b1.height));
				b1.oldPos.x = b1.pos.x;
				b1.oldPos.y = b1.pos.y;
				b1.mRadius = (float)(b1.width / 2);
			}
		}
		
		// Randomly place the blocking line on the screen
		if(mBlock != null) {
			mBlock.pos.x = (float)(Math.random() * (mSV.mViewWidth - mBlock.width));
			mBlock.pos.y = (float)(Math.random() * (mSV.mViewWidth - mBlock.height));
		}
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
		if(h == 0)
			h = 1;
		
        gl.glViewport(0, 0, w, h);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, w, 0.0f, h, -1.0f, 1.0f); // 2D projection
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
    }

    public void onDrawFrame(GL10 gl) {
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
    	// Run Physics
		if(mAnimate)
			mSV.queueEvent(mSV.mPhysics);        
        
		// Draw Balls
		if(mBallSprites != null) {
			for(GLSpriteBall b1 : mBallSprites)
				b1.draw(gl);
		}
		
		// Draw Blocking Line
		if(mBlock != null) {
			mBlock.draw(gl);
		}
    }

	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 * @param obj - The GLSprite with a resource pre-loaded
	 */
	public int loadGLTexture(GL10 gl, Context context, GLSprite obj) {
		int resourceID = obj.mResourceID;
		int textureName = -1;
		
        if(context != null && gl != null) {
			gl.glGenTextures(1, mTextureNameWorkspace, 0);
			
			textureName = mTextureNameWorkspace[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            // Fetch resource
			InputStream is = context.getResources().openRawResource(resourceID);
			Bitmap bitmap = null;
			
			// Test for a valid resource
			try {
				bitmap = BitmapFactory.decodeStream(is);
	
			} finally {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
			
			//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            mCropWorkspace[0] = 0;
            mCropWorkspace[1] = bitmap.getHeight();
            mCropWorkspace[2] = bitmap.getWidth();
            mCropWorkspace[3] = -bitmap.getHeight();

            // Store object width and height according to image dimensions
			obj.width = (short)bitmap.getWidth();
			obj.height = (short)bitmap.getHeight();
			
            bitmap.recycle();

            ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
                    GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR)
				Log.e("loadGLTexture", "Texture Load GLError: " + error);
        }
        
        return textureName;
	}

}
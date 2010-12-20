package ivan.game.bounce;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A simple quad created with a vertex buffer.
 */
public class GLRectangle extends Renderable {
    public FloatBuffer mVerticesBuffer;
    public ByteBuffer mIndicesBuffer; 

    public Line line;
    
	public GLRectangle(float w, float h) {
		super();
		
		line = new Line();
		
		float rectangle[] = {
			0.0f, 0.0f, 0.0f,
			0.0f, h, 	0.0f,
			w, 	  0.0f, 0.0f,
			w, 	  h, 	0.0f		
		};

		byte indices[] = {
			0,	1,	2,
			1,	2,	3
		};
		
		mVerticesBuffer = makeFloatBuffer(rectangle);
		mIndicesBuffer = makeByteBuffer(indices);
		
		width = (short)w;
		height = (short)h;
	}
	
	// Vertex buffers must be placed on the native heap
	// where the garbage collector cannot move them.
	
    // Buffers with multi-byte datatypes (e.g., short, int, float)
    // must have their byte order set to native order

    public FloatBuffer makeFloatBuffer(float[] vertices) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vertices);
        fb.position(0);
        
        return fb;
    }

    public ByteBuffer makeByteBuffer(byte[] indices) {
        ByteBuffer bb = ByteBuffer.allocateDirect(indices.length);
        bb.order(ByteOrder.nativeOrder());
        
		bb.put(indices);
		bb.position(0);        
        
        return bb;
    }
    
    public void draw(GL10 gl) {
    	// Define an array of vertex coordinates
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);
        
        // Vertex array is enabled for writing
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
		// Draw the quad. Translate according to touch position. (in this case)
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(pos.x, pos.y, 0.0f);
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mIndicesBuffer);
		gl.glPopMatrix();
		
		// Vertex array is disabled
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}

/*
 * Simple line with two 2D coordinates
 */
class Line {
	Vec2 p1 = new Vec2();
	Vec2 p2 = new Vec2();
}
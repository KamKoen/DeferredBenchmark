package deferred.ShaderPrepClasses;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_ARRAY_STRIDE;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Light;
import deferred.Model;
import deferred.ShaderProgram;

public class Cam extends PerFragmentForward {

	public Cam()
	{
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader("cam.vert");
		FPprogram.attachFragmentShader("cam.frag");
		FPprogram.link();
		
	
		camP = new ShaderProgram();
		camP.bind();
		camP.attachVertexShader("cam.vert");
		camP.attachFragmentShader("cam.frag");		
		camP.link();
		
	
	}
	
	@Override
	public String getName()
	{
		return "Camera";
	}
	
	
	
	
	
	
	
}

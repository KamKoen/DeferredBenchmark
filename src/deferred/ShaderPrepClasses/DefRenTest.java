package deferred.ShaderPrepClasses;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
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

public class DefRenTest extends Deferred{

	@Override
	public String getName()
	{
		return "DefRenTest";
	}
	
	public DefRenTest(){
		
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenFP.vert");
		FPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenFP.frag");
		FPprogram.link();
		
		
		SPprogram = new ShaderProgram();
		SPprogram.bind();
		SPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenSP.vert");
		SPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenSP.frag");
		SPprogram.link();
	
		camP = new ShaderProgram();
		camP.bind();
		camP.attachVertexShader("cam.vert");
		camP.attachFragmentShader("cam.frag");		
		camP.link();
		
		RendTextP = new ShaderProgram();
		RendTextP.bind();
		RendTextP.attachVertexShader("RendText.vert");
		RendTextP.attachFragmentShader("RendText.frag");
		RendTextP.link();
		
	}
	
	
	public void prepareDrawSP(int height,int width, int defDepthTexture, int normalTexture, int diffuseTexture, int lightTexture, int frameBuffer)
	{
		
		super.prepareDrawSP(height, width, defDepthTexture, normalTexture, diffuseTexture, lightTexture);
		
		glEnable(GL_TEXTURE_2D);
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
	}
	
	public void setModeRend(int i)
	{
		RendTextP.setInt("mode", i);
	}
	
	public void setTranslRend(Vector3f i)
	{
		RendTextP.setUniformVec3f("translate",i);
	}
	
	public void prepareDrawRend(int width, int height, Matrix4f ortho )
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		glEnable(GL_TEXTURE_2D);
		
		RendTextP.bind();
		
		int g = glGetUniformLocation(RendTextP.getID(), "Texture");
		glUniform1i(g,0);
		
		
		
		RendTextP.setUniformMatrix4f("view", view);
		//RendTextP.setUniformVec3f("translate",transl);
		RendTextP.setUniformMatrix4f("projection", proj);
		RendTextP.setUniformMatrix4f("ortho", ortho);
		RendTextP.setInt("width", width);
		RendTextP.setInt("height", height);
		
		
		
	}
	
	
	public void drawRend()
	{
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

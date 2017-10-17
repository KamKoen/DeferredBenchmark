package deferred.ShaderPrepClasses;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Model;
import deferred.ShaderProgram;

public class DeferredSphereStencil extends DeferredSphere{

	
	ShaderProgram StencilP;
	
	public DeferredSphereStencil(){
		
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenFPSepDepth.vert");
		FPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenFPSepDepth.frag");
		FPprogram.link();
		
		
		StencilP = new ShaderProgram();
		StencilP.bind();
		StencilP.attachVertexShader(folderOfAlgorithms+"DeferredSS.vert");
		//StencilP.attachFragmentShader("deferredRen/DeferredSS.frag");
		StencilP.link();
		
		
		SPprogram = new ShaderProgram();
		SPprogram.bind();
		SPprogram.attachVertexShader(folderOfAlgorithms+"drlpss.vert");
		SPprogram.attachFragmentShader(folderOfAlgorithms+"drlpss.frag");
		SPprogram.link();
	
		AmbientP = new ShaderProgram();
		AmbientP.bind();
		AmbientP.attachVertexShader(folderOfAlgorithms+"DeferredRenLPAmb.vert");
		AmbientP.attachFragmentShader(folderOfAlgorithms+"DeferredRenLPAmb.frag");
		AmbientP.link();
		
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
	
	
	public void prepareStencilPass()
	{
		
		StencilP.bind();
	}
	
	public void drawStencilPass(Vector3f pos, float distance, Model m)
	{
		
		Matrix4f MV = ModelView(view,pos, distance );
		Matrix4f MVP = ModelViewProjection(MV,proj);
		
		StencilP.setUniformMatrix4f("MVP", MVP);
		
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
	}
	
	public void drawStencilPassFS(Matrix4f ortho, int m)
	{
		
		
		
		
		StencilP.setUniformMatrix4f("MVP", ortho);
		
		
		
		glDrawArrays(GL_TRIANGLE_STRIP,0,m);
		
	}


	public void drawStencilPass(Vector3f pos, Model m, Object object,
			float xAngle, float yAngle, float zAngle) {
		Matrix4f M = new Matrix4f();
		Matrix4f MV = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
		Matrix4f normM = new Matrix4f();
		
		
		
		
		M = ModelView(new Matrix4f(),pos, xAngle, yAngle, zAngle);
		
		MV = ModelView(view,pos, xAngle, yAngle, zAngle);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		
		StencilP.setUniformMatrix4f("MVP", MVP);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
	}
	
	
	
	
}

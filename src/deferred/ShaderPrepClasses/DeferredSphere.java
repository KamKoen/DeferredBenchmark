package deferred.ShaderPrepClasses;

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

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Light;
import deferred.MatrixUtil;
import deferred.Model;
import deferred.ShaderProgram;

public class DeferredSphere extends Deferred{

	public ShaderProgram AmbientP;
	
	public DeferredSphere(){
		
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenFP.vert");
		FPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenFP.frag");
		FPprogram.link();
		
		
		
		SPprogram = new ShaderProgram();
		SPprogram.bind();
		SPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenLPSpheres.vert");
		SPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenLPSpheres.frag");
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
	
	
	
	public void prepareDrawSP(int height,int width,Matrix4f v, Matrix4f p, int defDepthTexture, int normalTexture, int diffuseTexture, int lightTexture, int frameBuffer)
	{
		
		
		glEnable(GL_BLEND);
		glDisable(GL_DEPTH_TEST);
	   	glBlendEquation(GL_FUNC_ADD);
	   	glBlendFunc(GL_ONE,GL_ONE);
		
		glCullFace(GL_FRONT);
		

		SPprogram.bind();
		
		

		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT);//| GL_DEPTH_BUFFER_BIT);

		
		view = v;
		proj = p;
		
		float A = proj.m00;
		float B = proj.m11;
		float C = proj.m22;
		float D = proj.m32;
		
		SPprogram.setFloat("A", A);
		SPprogram.setFloat("B", B);
		SPprogram.setFloat("C", C);
		SPprogram.setFloat("D", D);
		
		
		glActiveTexture(GL_TEXTURE0+1);
		glBindTexture(GL_TEXTURE_2D, defDepthTexture);
		SPprogram.setInt("depthTex", 1);
		
		glActiveTexture(GL_TEXTURE0+2);
		glBindTexture(GL_TEXTURE_2D, normalTexture);
		SPprogram.setInt("normalTex", 2);
		
		glActiveTexture(GL_TEXTURE0+3);
		glBindTexture(GL_TEXTURE_2D, diffuseTexture);
		SPprogram.setInt("diffTex", 3);
		
		glActiveTexture(GL_TEXTURE0+5);
		glBindTexture(GL_TEXTURE_2D, lightTexture);
		SPprogram.setInt("specTex", 5);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,0);
		
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		SPprogram.setUniformMatrix4f("ortho", ortho);
		
		SPprogram.setInt("kAmode", 0);
		
		AmbientP.bind();
		
		AmbientP.setFloat("A", A);
		AmbientP.setFloat("B", B);
		AmbientP.setFloat("C", C);
		AmbientP.setFloat("D", D);
		
		
		glActiveTexture(GL_TEXTURE0+1);
		glBindTexture(GL_TEXTURE_2D, defDepthTexture);
		AmbientP.setInt("depthTex", 1);
		
		glActiveTexture(GL_TEXTURE0+2);
		glBindTexture(GL_TEXTURE_2D, normalTexture);
		AmbientP.setInt("normalTex", 2);
		
		glActiveTexture(GL_TEXTURE0+3);
		glBindTexture(GL_TEXTURE_2D, diffuseTexture);
		AmbientP.setInt("diffTex", 3);
		
		glActiveTexture(GL_TEXTURE0+5);
		glBindTexture(GL_TEXTURE_2D, lightTexture);
		AmbientP.setInt("specTex", 5);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,0);
		
		
		
		AmbientP.setUniformMatrix4f("ortho", ortho);
		
		AmbientP.setInt("kAmode", 0);
		
		SPprogram.bind();
		
		
		
	}
	
	
	public void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight, int width, int height)
	{
		SPprogram.bind();
		SPprogram.setUniformVec3f("ambientLight", ambientLight);
		SPprogram.setPointlights("pLight", pLightA);
		SPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);

        SPprogram.setInt("width",width);
        SPprogram.setInt("height", height);
	
        
        AmbientP.bind();
        AmbientP.setUniformVec3f("ambientLight", ambientLight);
        AmbientP.setPointlights("pLight", pLightA);
        AmbientP.setInt("LIGHT_MAX", MAX_PLIGHT);

        AmbientP.setInt("width",width);
        AmbientP.setInt("height", height);
        
        camP.bind();
		camP.setUniformVec3f("ambientLight", ambientLight);
		camP.setPointlights("pLight", pLightA);
		camP.setInt("LIGHT_MAX", MAX_PLIGHT);

		camP.unbind();
	}
	
	public void drawSP(Vector3f pos, float distance, Vector3f col, int ID, Model m){
		
		Matrix4f MV = ModelView(view,pos, distance );
		Matrix4f MVP = ModelViewProjection(MV,proj);

		setSPProgramParamA((Matrix4f)null,view,(Matrix4f)null,MV,MVP,col);
		
		SPprogram.setInt("CUR_LIGHT_ID",ID);
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
	}
	
	
	public void prepareDrawSPLights()
	{
		SPprogram.bind();
		glBlendEquation(GL_FUNC_ADD);
	   	glBlendFunc(GL_ONE,GL_ONE_MINUS_SRC_ALPHA);
		SPprogram.setInt("kAmode", 1);
		
	//	glDisable(GL_BLEND);
		
		
		
		
	}
	public void prepdrawAmbLight(int width, int height)
	{
		
		
		
		AmbientP.bind();
		//AmbientP.setInt("kAmode", 1);
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		AmbientP.setUniformMatrix4f("view", view);
		AmbientP.setUniformMatrix4f("projection", proj);
		AmbientP.setUniformMatrix4f("ortho", ortho);
		AmbientP.setInt("width", width);
		AmbientP.setInt("height", height);
		
	} 
	
	public void drawAmbLight(Vector3f ambientLight)
	{
		
		
		AmbientP.setUniformVec3f("ambientLight", ambientLight);
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
	}
	
	public void endDrawSP(int renderedTexture, int width, int height)
	{
		
		glDisable(GL_BLEND);
		glCullFace(GL_BACK);
		
		
		/*
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(1,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		*/
		
		glEnable(GL_TEXTURE_2D);
		
		
	
	
		glBindTexture(GL_TEXTURE_2D,renderedTexture);
	
		
		
		RendTextP.bind();
		
		
		
		int g = glGetUniformLocation(RendTextP.getID(), "Texture");
		glUniform1i(g,0); // die Zahl w�hlt die Textur in GLACTIVETEXTURE aus
	
		
			
		
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		RendTextP.setUniformMatrix4f("view", view);
		RendTextP.setUniformMatrix4f("projection", proj);
		RendTextP.setUniformMatrix4f("ortho", ortho);
		RendTextP.setInt("width", width);
		RendTextP.setInt("height", height);
		
		RendTextP.setInt("mode", 0);
		
		
		
	
	
	}
	
	@Override
	public String getName()
	{
		return "DeferredSphere";
	}
	
	public void drawTexture()
	{
		
		
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
		
		
		
	}
	
	public void drawSPLights(Vector3f pos, float scale, Vector3f col, int ID, Model m){
		

		Matrix4f MV = ModelView(view, pos, scale);
		Matrix4f MVP = ModelViewProjection(MV,proj);

		setSPProgramParamA((Matrix4f)null,view,(Matrix4f)null,null,MVP,col);
		
		SPprogram.setInt("CUR_LIGHT_ID",ID);
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
	   	
	   	
		
	}
	

	@Override
	public void preDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareSP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA, Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		view = v;
		proj = p;
		
		SPprogram.bind();

		SPprogram.setUniformMatrix4f("normalMLight", normMLight);
		SPprogram.updateLightPositions("pLight", pLightA);
		SPprogram.setUniformMatrix4f("projection", proj);
		SPprogram.setUniformMatrix4f("view", view);
		SPprogram.setUniformMatrix4f("normalM", normM);
		SPprogram.setUniformVec3f("camPos", camPos);
		SPprogram.setUniformVec3f("worldAmbient", WORLD_AMBIENT);
		
		SPprogram.unbind();
		
	}

	

	

}

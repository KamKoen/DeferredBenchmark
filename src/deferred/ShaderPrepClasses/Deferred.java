package deferred.ShaderPrepClasses;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Light;
import deferred.MatrixUtil;
import deferred.Model;
import deferred.ShaderProgram;

public class Deferred extends Algo{

	
	public Deferred(){
		
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
		
		
		
	}
	
	@Override
	public void setModelInf(Model m)
	{
		FPprogram.setInt("mtlUse", 1);
		FPprogram.setFloat("Ns", m.Ns);
		FPprogram.setUniformVec3f("Ka",m.Ka);
		FPprogram.setUniformVec3f("Kd",m.Kd);
		
		if(m.difTexture != -1)
		{
		glActiveTexture(GL_TEXTURE0+6);
		glBindTexture(GL_TEXTURE_2D, m.difTexture);
		FPprogram.setInt("difModTex", 6);
		FPprogram.setInt("difTexUse", 1);
		}
		else FPprogram.setInt("difTexUse",0);
		
		if(m.ambTexture != -1)
		{
		glActiveTexture(GL_TEXTURE0+7);
		glBindTexture(GL_TEXTURE_2D, m.ambTexture);
		FPprogram.setInt("ambModTex", 7);
		FPprogram.setInt("ambTexUse", 1);
		
		}
		else FPprogram.setInt("ambTexUse", 0);
			
		if(m.specTexture != -1)
		{
		glActiveTexture(GL_TEXTURE0+7);
		glBindTexture(GL_TEXTURE_2D, m.specTexture);
		FPprogram.setInt("specModTex", 7);
		FPprogram.setInt("specTexUse", 1);
		
		}
		else FPprogram.setInt("specTexUse", 0);
		
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,0);
		
	}
	
	@Override
	public void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight){
		

		SPprogram.bind();
		SPprogram.setUniformVec3f("ambientLight", ambientLight);
		SPprogram.setPointlights("pLight", pLightA);
		SPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);
   
	
        camP.bind();
		camP.setUniformVec3f("ambientLight", ambientLight);
		camP.setPointlights("pLight", pLightA);
		camP.setInt("LIGHT_MAX", MAX_PLIGHT);
		
		camP.unbind();
	
	
	}
	
	
	@Override
	public void drawFP(Vector3f position, float size, Model m, Vector3f color) {
		Matrix4f M = new Matrix4f();
		Matrix4f MV = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
		Matrix4f normM = new Matrix4f();
		
		
		
		
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setFPProgramParamA(normM,view,M,MV,MVP,color);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
	}
	
	
	@Override
	public void drawFP(Vector3f position, float size, Model m, Vector3f color, int c) {
		
		
		
		
		
		
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setFPProgramParamA(normM,view,M,MV,MVP,color);
		
		glDrawArrays(GL_TRIANGLES,0,c);
		
			
		
	}
	

	@Override
	public void drawFP(Vector3f position, Model m, Vector3f color,float xAngle,float yAngle,float zAngle) {
		
		Matrix4f M = new Matrix4f();
		Matrix4f MV = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
		Matrix4f normM = new Matrix4f();
		
		
		
		
		M = ModelView(new Matrix4f(),position, xAngle, yAngle, zAngle);
		
		MV = ModelView(view,position, xAngle, yAngle, zAngle);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setFPProgramParamA(normM,view,M,MV,MVP,color);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
			
		
	}
	
	
	
	public void prepareDrawSP(int height,int width, int defDepthTexture, int normalTexture, int diffuseTexture, int lightTexture)
	{
		SPprogram.bind();
		
		glEnable(GL_TEXTURE_2D);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		
		//setParamDefS
		
		Matrix4f proj = this.proj;
		
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
			
		
	}
	
	
	public void drawSP(int vertCount){
		
		glDrawArrays(GL_TRIANGLE_STRIP,0,vertCount);
		
	}
	
	
	@Override
	public void preDraw() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getName()
	{
		return "Deferred";
	}
	
	
	@Override
	public void prepareSP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA,  Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		// TODO Auto-generated method stub
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
	
	@Override
	public void prepareFP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA,Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		// TODO Auto-generated method stub
		view = v;
		proj = p;
		
		FPprogram.bind();

		FPprogram.setUniformMatrix4f("normalMLight", normMLight);
		FPprogram.updateLightPositions("pLight", pLightA);
		FPprogram.setUniformMatrix4f("projection", proj);
		FPprogram.setUniformMatrix4f("view", view);
		FPprogram.setUniformMatrix4f("normalM", normM);
		FPprogram.setUniformVec3f("camPos", camPos);
		FPprogram.setUniformVec3f("worldAmbient", WORLD_AMBIENT);
		
		FPprogram.unbind();
		
		
		
		
	}

	@Override
	public void drawCam(Vector3f position, float size, Model m, Vector3f color) {
		
		FPprogram.setInt("isLight", 1);
		Matrix4f MV = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
	
				
		
		MV = ModelView(view,position, size);
		
				
		MVP = ModelViewProjection(MV,proj);
		
				
		setFPProgramParamA((Matrix4f)null,view,(Matrix4f)null,MV,MVP,color);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
		FPprogram.setInt("isLight", 0);
		
			
		
		
	}

	@Override
	public void drawCamStart() {
		// TODO Auto-generated method stub
		FPprogram.bind();
	}

	@Override
	public void drawCamEnd() {
		// TODO Auto-generated method stub
		FPprogram.unbind();
	}

	@Override
	public void useShini() {
		// TODO Auto-generated method stub
	//	FPprogram.bind();
		FPprogram.setInt("useShini", 1);
	
		SPprogram.bind();
		SPprogram.setInt("useShini", 1);
		
		FPprogram.bind();
		
	}

}

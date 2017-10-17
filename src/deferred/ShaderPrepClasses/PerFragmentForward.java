package deferred.ShaderPrepClasses;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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
import org.lwjgl.util.vector.Vector4f;

import deferred.Light;
import deferred.Model;
import deferred.ShaderProgram;

public class PerFragmentForward extends Algo{

	public PerFragmentForward()
	{
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader("PerFragPhong.vert");
		FPprogram.attachFragmentShader("PerFragPhong.frag");
		FPprogram.link();
		
	
		camP = new ShaderProgram();
		camP.bind();
		camP.attachVertexShader("cam.vert");
		camP.attachFragmentShader("cam.frag");		
		camP.link();
		
	}
	
	
	
	@Override
	public void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight) {
		FPprogram.bind();
		FPprogram.setUniformVec3f("ambientLight", ambientLight);
		FPprogram.setPointlights("pLight", pLightA);
		FPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);
	
		
		FPprogram.unbind();
		
		
		camP.bind();
		camP.setUniformVec3f("ambientLight", ambientLight);
		camP.setPointlights("pLight", pLightA);
		camP.setInt("LIGHT_MAX", MAX_PLIGHT);
	
		
		camP.unbind();
		
		
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
	public void setStar(int star)
	{

		FPprogram.bind();
		FPprogram.setInt("starMode", star);
		
		
	}

	@Override
	public void drawFP(Vector3f position, float size, Model m, Vector3f color) {
		
		
		
		
		
		
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
	@Override
	public void drawFP(Vector3f position, float size, Model m, Vector4f color) {
		
		
		
		
		
		
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setFPProgramParamA(normM,view,M,MV,MVP,color);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
			
		
	}
	@Override
	public void drawFP(Vector3f position, Model m, Vector4f color,float xAngle,float yAngle,float zAngle) {
		
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
	
	@Override
	public void drawCam(Vector3f position, float size, Model m, Vector3f color) {
		
		
		Matrix4f MV = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
	
				
		
		MV = ModelView(view,position, size);
		
				
		MVP = ModelViewProjection(MV,proj);
		
				
		setCamProgramParamA((Matrix4f)null,view,(Matrix4f)null,MV,MVP,color);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
			
		
	}
	
	

	@Override
	public void preDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareFP(Matrix4f v, Matrix4f p, Matrix4f normMLight, Light[] pLightA, Matrix4f normM, Vector3f camPos, Vector3f WORLD_AMBIENT) 
	{
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
	public void prepareSP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA,  Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public String getName()
	{
		return "PerPixelForward";
	}
	
	@Override
	public void drawCamStart() {
		// TODO Auto-generated method stub
		camP.bind();
	}



	@Override
	public void drawCamEnd() {
		// TODO Auto-generated method stub
		camP.unbind();
	}



	@Override
	public void useShini() {
		
		FPprogram.setInt("useShini", 1);
		
	}






}

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
import org.lwjgl.util.vector.Vector4f;

import deferred.Light;
import deferred.Model;
import deferred.ShaderProgram;

public class PerPixelForward extends Algo{

	public PerPixelForward()
	{
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader("PerPixelPhong.vert");
		FPprogram.attachFragmentShader("PerPixelPhong.frag");
		FPprogram.link();
		
	
		camP = new ShaderProgram();
		camP.bind();
		camP.attachVertexShader("cam.vert");
		camP.attachFragmentShader("cam.frag");		
		camP.link();
		
	}
	
	
	
	public void init(Light[] pLightA, int MAX_PLIGHT, Light[] sLightA,  int MAX_SLIGHT, float matShin) {
		FPprogram.bind();
		FPprogram.setFloat("matShin", matShin);
		FPprogram.setPointlights("pLight", pLightA);
		FPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);
		FPprogram.setInt("SPOT_MAX", MAX_SLIGHT);
		FPprogram.unbind();
		
		
		camP.bind();
		camP.setFloat("matShin", matShin);
		camP.setPointlights("pLight", pLightA);
		camP.setInt("LIGHT_MAX", MAX_PLIGHT);
		camP.setInt("SPOT_MAX", MAX_SLIGHT);
		camP.unbind();
		
		
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

	public void prepareFP(Matrix4f v, Matrix4f p, Matrix4f normMLight, Light[] pLightA, Light[] sLightA, Matrix4f normM, Vector3f camPos, Vector3f WORLD_AMBIENT) 
	{
		// TODO Auto-generated method stub
		view = v;
		proj = p;
		
		FPprogram.bind();

		FPprogram.setUniformMatrix4f("normalMLight", normMLight);
		FPprogram.updateLightPositions("pLight", pLightA);
		FPprogram.updateLightPositions("sLight", sLightA);
		FPprogram.setUniformMatrix4f("projection", proj);
		FPprogram.setUniformMatrix4f("view", view);
		FPprogram.setUniformMatrix4f("normalM", normM);
		FPprogram.setUniformVec3f("camPos", camPos);
		FPprogram.setUniformVec3f("worldAmbient", WORLD_AMBIENT);
		
		FPprogram.unbind();
		
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



	@Override
	public void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void prepareSP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA, Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		// TODO Auto-generated method stub
		
	}






}

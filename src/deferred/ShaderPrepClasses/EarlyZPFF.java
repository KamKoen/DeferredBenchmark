package deferred.ShaderPrepClasses;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Model;
import deferred.ShaderProgram;

public class EarlyZPFF extends PerFragmentForward{

	
	ShaderProgram DPprogram;
	
	public EarlyZPFF(){
		
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
		
		DPprogram = new ShaderProgram();
		DPprogram.bind();
		DPprogram.attachVertexShader("depthPP.vert");
		DPprogram.attachFragmentShader("depthPP.frag");
		DPprogram.link();
		
	}
	
	public ShaderProgram getDP()
	{
		return DPprogram;
	}
	
	public void drawDepthOnly(Vector3f position, float size, Model m)
	{
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setDPProgramParamA(normM,view,M,MV,MVP);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
		
		
		
	}
	
	public void drawDepthOnly(Vector3f position, float size, int c)
	{
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setDPProgramParamA(normM,view,M,MV,MVP);
		
		
		glDrawArrays(GL_TRIANGLES,0,c);
		
		
		
		
	}
	
	
	

	private void setDPProgramParamA(Matrix4f normM, Matrix4f view, Matrix4f m,
			Matrix4f mV, Matrix4f mVP) {
		// TODO Auto-generated method stub
		
		
		DPprogram.setUniformMatrix4f("M", m);
		DPprogram.setUniformMatrix4f("normalM", normM);
		DPprogram.setUniformMatrix4f("view", view);
		DPprogram.setUniformMatrix4f("MV", MV);
		DPprogram.setUniformMatrix4f("MVP", MVP);
		
		
	}
	
	public void drawDepthOnly(Vector3f position, Model m,float xAngle,float yAngle,float zAngle)
	{
		
		
		
		
		M = ModelView(new Matrix4f(),position, xAngle, yAngle, zAngle);
		
		MV = ModelView(view,position, xAngle, yAngle, zAngle);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setDPProgramParamA(normM,view,M,MV,MVP);
		
		
		glDrawArrays(GL_TRIANGLES,0,m.faces.size() * 3);
		
	}
	
	
	
	
}

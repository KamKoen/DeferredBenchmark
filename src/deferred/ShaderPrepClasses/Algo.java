package deferred.ShaderPrepClasses;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;




import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.input.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import deferred.Camera;
import deferred.Light;
import deferred.Model;
import deferred.ShaderProgram;


import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.input.Mouse.*;
import java.applet.Applet;
import java.awt.Canvas;

/*
 * Abstrakte Algorithmenklasse, alle anderen Algorithmenklassen erben von ihr
 */
public abstract class Algo {
	
	Matrix4f M = new Matrix4f();
	Matrix4f MV = new Matrix4f();
	Matrix4f MVP = new Matrix4f();
	Matrix4f normM = new Matrix4f();
	
	public String folderOfAlgorithms = "ShaderAlgorithms"+File.separator;
	
	public ShaderProgram FPprogram; //First Pass
	public ShaderProgram SPprogram; //Second Pass
	public ShaderProgram ComputeZ; //ComputeShader for Z min max
	public ShaderProgram ComputeTile; //ComputeShader for Tiles
	
	public ShaderProgram RendTextP; //Render a Texture
	public ShaderProgram camP;

	public Camera camera;
	
	
	Matrix4f view;
	Matrix4f proj;
	
	public void checkE(boolean a)
	{
		try{
			Util.checkGLError();
		}
		catch(OpenGLException e )
		{
			if(a)System.out.println(e);
		}
	}
	
	
	static public long getTimeD()
	{
		
		return System.nanoTime() / 1000 ;
		
		
	}
	
	public ShaderProgram getFP() {
		return FPprogram;
	}
	
	
	public abstract String getName();
	
	
	public void setInts(int bindingPointInfB,int infoSSBO,int bindingPointIndB,int indexSSBO, 
			int bindingPointInfSubB, int infoSubSSBO, int bindingPointIndSubB, int indexSubSSBO,
			int bindingPointInfSubB2, int infoSub2SSBO ,int bindingPointIndSubB2 ,int indexSub2SSBO,
			int width,int height,int tileWidth,int tileHeight,int tileWidthSub,int tileHeightSub,
			int tileWidthSub2,int tileHeightSub2,int MAX_PLIGHT,int maxLightsPerTile){
		
	}
	
	public abstract void drawFP(Vector3f position, float size, Model m, Vector3f color);
	
	public abstract void preDraw();
	

	public abstract void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight);
	
	public abstract void prepareSP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA, Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT);

	
	public void setRasterMode(int raster)
	{
		
		SPprogram.bind();
		SPprogram.setInt("RasterMode", raster);
		
		
	}

	
	public void setStar(int star)
	{

		SPprogram.bind();
		SPprogram.setInt("starMode", star);
		
		
	}
	
	public Matrix4f rotateX(Matrix4f model, float angle)
	{
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
	
		Matrix4f.rotate(angle, new Vector3f(1,0,0), model, ret);
		return ret;		
	}
	
	public Matrix4f rotateY(Matrix4f model, float angle)
	{
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
	
		Matrix4f.rotate(angle, new Vector3f(0,1,0), model, ret);
		return ret;		
	}
	
	public Matrix4f rotateZ(Matrix4f model, float angle)
	{
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
	
		Matrix4f.rotate(angle, new Vector3f(0,0,1), model, ret);
		return ret;		
	}
	
	public Matrix4f rotateAroundAxs(Matrix4f model, float angle, Vector3f axis)
	{
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
	
		Matrix4f.rotate(angle, axis, model, ret);
		return ret;		
	}
	
	
	
	public Matrix4f transformPos(Matrix4f view, Vector3f translate){
		
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
		Matrix4f.translate(translate, ret, ret);		
		
		Matrix4f.mul(view, ret, ret);
		
		return ret;
		
		
		
		
	}
	
	public Matrix4f transformPos(Matrix4f view, Vector3f translate, float scale){
		
		Matrix4f ret = new Matrix4f();
		ret.setIdentity();
		
		Matrix4f.translate(translate, ret, ret);		
		
		Matrix4f.mul(view, ret, ret);
		
		ret.scale(new Vector3f(scale,scale,scale));
		
		return ret;
	
	
		
	}
	
	
	
	
	
	public Matrix4f ModelView(Matrix4f view)
	{
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		
		Matrix4f ret = new Matrix4f();
		
		Matrix4f.mul(view, model, ret);
		
		return ret;
		
	}
	
	public Matrix4f ModelView(Matrix4f view, float scale)
	{
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		
		Matrix4f ret = new Matrix4f();
		Matrix4f.mul(view, model, ret);
		ret.scale(new Vector3f(scale,scale,scale));
		
		return ret;
		
	}
	public Matrix4f ModelView(Matrix4f view, Vector3f translate)
	{
		
		Matrix4f model = new Matrix4f();
		
		model.setIdentity();
		
		model.translate(translate);
		
		Matrix4f ret = new Matrix4f();
		
		
		Matrix4f.mul(view, model, ret);
		
		
		return ret;
		
	}
	
	public Matrix4f ModelView(Matrix4f view, Vector3f translate, Matrix4f model)
	{
		
		
		
		model.setIdentity();
		
		model.translate(translate);
		
		//Matrix4f ret = new Matrix4f();
		
		
		Matrix4f.mul(view, model, model);
		
		
		return model;
		
	}
	
	
	
	public Matrix4f ModelView(Matrix4f view, Vector3f translate, float xAngle, float yAngle, float zAngle)
	{
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		
		
		Matrix4f rotation = new Matrix4f();
		
		
		
		
		rotation = rotateZ(rotation,(float) Math.toRadians(zAngle));
		rotation = rotateY(rotation,(float) Math.toRadians(yAngle));
		rotation = rotateX(rotation,(float) Math.toRadians(xAngle));
		
		Matrix4f translation = new Matrix4f();
		
		
		
		translation.translate(translate);
		
		
		Matrix4f.mul(rotation, model, model);
		Matrix4f.mul(translation, model, model);
		
		
		Matrix4f ret = new Matrix4f();
		Matrix4f.mul(view, model, ret);
		
		
		return ret;
		
	}
	
	
	
	public Matrix4f ModelView(Matrix4f view, Vector3f translate, float xAngle, float yAngle, float zAngle, float scale)
	{
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		
		model = rotateX(model,xAngle);
		model = rotateY(model,yAngle);
		model = rotateZ(model,zAngle);
		
		model.translate(translate);
		
		Matrix4f ret = new Matrix4f();
		Matrix4f.mul(view, model, ret);
		

		ret.scale(new Vector3f(scale,scale,scale));
		
		return ret;
		
	}
	
	
	
	
	public Matrix4f ModelView(Matrix4f view, Vector3f translate, float scale)
	{
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		model.translate(translate);
		
		Matrix4f ret = new Matrix4f();
		Matrix4f.mul(view, model, ret);
		
		ret.scale(new Vector3f(scale,scale,scale));
		
		return ret;
		
	}
	
	public Matrix4f ModelView(Matrix4f view, Vector3f translate, float scale, Matrix4f model)
	{
		
		model.setIdentity();
		model.translate(translate);
		
		
		Matrix4f.mul(view, model, model);
		
		model.scale(new Vector3f(scale,scale,scale));
		
		return model;
		
	}
	
	public Matrix4f createInverseTranspose(Matrix4f view)
	{
		Matrix4f ret = new Matrix4f(view);
		
		Matrix4f.invert(ret, ret);
		ret.transpose();
		
		return ret;
	}
	
	public Matrix4f createInverseTranspose(Matrix4f view,Matrix4f ret)
	{
		
		
		Matrix4f.invert(view, ret);
		ret.transpose();
		
		return ret;
	}
	
	
	
	public Matrix4f ModelViewProjection(Matrix4f modelView, Matrix4f proj)
	{
		Matrix4f ret = new Matrix4f();
		
		
		Matrix4f.mul(proj, modelView, ret);
		
		return ret;
		
		
		
	}
	
	public Matrix4f ModelViewProjection(Matrix4f modelView, Matrix4f proj, Matrix4f ret)
	{
		
		
		
		Matrix4f.mul(proj, modelView, ret);
		
		return ret;
		
		
		
	}
	
	public void setCamProgramParamA(Matrix4f normalM,Matrix4f view,Matrix4f model, Matrix4f MV, Matrix4f MVP, Vector3f col)
	{
		
		if(col == null) col = new Vector3f (-1,-1,-1);
		camP.setUniformMatrix4f("M", model);
		camP.setUniformMatrix4f("normalM", normalM);
		camP.setUniformMatrix4f("view", view);
		camP.setUniformMatrix4f("MV", MV);
		camP.setUniformMatrix4f("MVP", MVP);
		camP.setUniformVec3f("setCol", col);
		
		
	}
	

	public void setSPProgramParamA(Matrix4f normalM,Matrix4f view,Matrix4f model, Matrix4f MV, Matrix4f MVP, Vector3f col)
	{
		
		if(col == null) col = new Vector3f (-1,-1,-1);
		SPprogram.setUniformMatrix4f("M", model);
		SPprogram.setUniformMatrix4f("normalM", normalM);
		SPprogram.setUniformMatrix4f("view", view);
		SPprogram.setUniformMatrix4f("MV", MV);
		SPprogram.setUniformMatrix4f("MVP", MVP);
		SPprogram.setUniformVec3f("setCol", col);
		
		
	}
	
	
	public void setFPProgramParamA(Matrix4f normalM,Matrix4f view,Matrix4f model, Matrix4f MV, Matrix4f MVP, Vector3f col)
	{
		
		if(col == null)
			{
			setFPProgramParamA( normalM, view, model,  MV,  MVP,(Vector4f) null);
			}
		else
			{
			setFPProgramParamA( normalM, view, model,  MV,  MVP, new Vector4f (col.x,col.y,col.z,1));
			}
		
		
	}

	public void setFPProgramParamA(Matrix4f normalM,Matrix4f view,Matrix4f model, Matrix4f MV, Matrix4f MVP, Vector4f col)
	{
		
		if(col == null) col = new Vector4f (-1,-1,-1,-1);
		FPprogram.setUniformMatrix4f("M", model);
		FPprogram.setUniformMatrix4f("normalM", normalM);
		FPprogram.setUniformMatrix4f("view", view);
		FPprogram.setUniformMatrix4f("MV", MV);
		FPprogram.setUniformMatrix4f("MVP", MVP);
		FPprogram.setUniformVec4f("setCol", col);
		
	}
	
	
	public void drawSP(){
		
	}
	
	public abstract void drawCamStart();
	
	public abstract void drawCamEnd();
	
	public abstract void drawCam(Vector3f position, float size, Model m, Vector3f color);

	

	public abstract void drawFP(Vector3f position, Model m, Vector3f color, float xAngle,
			float yAngle, float zAngle);

	

	

	public void prepareFP(Matrix4f v, Matrix4f p, Matrix4f normMLight,
			Light[] pLightA, Matrix4f normM, Vector3f camPos,
			Vector3f WORLD_AMBIENT) {
		// TODO Auto-generated method stub
		
	}

	public void setTestInt(int tileTestW0, int tileTestH0, int infoSSBOTest,
			int indexSSBOTest, int tileTestW1, int tileTestH1, int tileTestW2,
			int tileTestH2, int tileTestW3, int tileTestH3, int infoSSBOTest2,
			int indexSSBOTest2) {
		// TODO Auto-generated method stub
		
	}

	public void clearBuffers() {
		// TODO Auto-generated method stub
		

	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
	}

	public abstract void useShini();

	public void drawFP(Vector3f position, float size, Model m, Vector4f color) {
		// TODO Auto-generated method stub
		
	}

	public void drawFP(Vector3f position, Model m, Vector4f color,
			float xAngle, float yAngle, float zAngle) {
		// TODO Auto-generated method stub
		
	}


	public void drawFP(Vector3f position, float size, Model m, Vector3f color,
			int c) {
		// TODO Auto-generated method stub
		
	}


	public void setModelInf(Model m2) {
		// TODO Auto-generated method stub
		
	}

	

	
}









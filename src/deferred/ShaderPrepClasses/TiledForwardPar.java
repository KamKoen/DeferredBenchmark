package deferred.ShaderPrepClasses;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_R32I;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBClearBufferObject;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import deferred.Model;
import deferred.ShaderProgram;

public class TiledForwardPar extends PerFragmentForward {


	ShaderProgram DPprogram;
	
	public TiledForwardPar(float C, float D, int tileWidth, int tileHeight)
	{
		ComputeTile = new ShaderProgram();
        ComputeTile.attachComputeShader(folderOfAlgorithms+"ComputeTilePar.comp");
        ComputeTile.link();
        ComputeTile.bind();
       
        
        ComputeTile.setFloat("C", C);
		ComputeTile.setFloat("D", D);
		
		int x = tileWidth > 39 ? 20 : tileWidth;
		int y = tileHeight > 39 ? 20 : tileHeight;
        
		String a = "#version 430\n#define TWIDTH " + x + "\n#define THEIGHT " + y + "\n";
		
		
		ComputeZ = new ShaderProgram();
        ComputeZ.attachComputeShader(folderOfAlgorithms+"ComputeZ.comp",a);
        ComputeZ.link();
        ComputeZ.bind();
        
        ComputeZ.setFloat("C", C);	
		ComputeZ.setFloat("D", D);
		
		
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader(folderOfAlgorithms+"TiledForward.vert");
		FPprogram.attachFragmentShader(folderOfAlgorithms+"TiledForward.frag");
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
		
		RendTextP = new ShaderProgram();
		RendTextP.bind();
		RendTextP.attachVertexShader("RendText.vert");
		RendTextP.attachFragmentShader("RendText.frag");
		RendTextP.link();
	
		byteZero = createBBZ(1, 0);
		
	}

	int bindingPointInfB;
	int infoSSBO;
	int bindingPointIndB;
	
	int indexSSBO;
	int bindingPointInfSubB;
	int infoSubSSBO;
	int bindingPointIndSubB;
	int indexSubSSBO;
	int bindingPointInfSubB2;
	int infoSub2SSBO;
	int bindingPointIndSubB2; 
	int indexSub2SSBO;
	
	
	int width, height,  tileWidth,  tileHeight,  tileWidthSub,  tileHeightSub;
	int tileWidthSub2,  tileHeightSub2,  MAX_PLIGHT,  maxLightsPerTile;
	
	@Override
	public void setInts(int bindingPointInfB,int infoSSBO,int bindingPointIndB,int indexSSBO, 
			int bindingPointInfSubB, int infoSubSSBO, int bindingPointIndSubB, int indexSubSSBO,
			int bindingPointInfSubB2, int infoSub2SSBO ,int bindingPointIndSubB2 ,int indexSub2SSBO,
			int width,int height,int tileWidth,int tileHeight,int tileWidthSub,int tileHeightSub,
			int tileWidthSub2,int tileHeightSub2,int MAX_PLIGHT,int maxLightsPerTile)
	{
		this.bindingPointInfB = bindingPointInfB;
		this.infoSSBO = infoSSBO;
		this.bindingPointIndB=bindingPointIndB;
		this.indexSSBO=indexSSBO;
		
		this.bindingPointInfSubB=bindingPointInfSubB;
		this.infoSubSSBO=infoSubSSBO;
		this.bindingPointIndSubB=bindingPointIndSubB;
		this.indexSubSSBO=indexSubSSBO;
		
		this.bindingPointInfSubB2=bindingPointInfSubB2; 
		this.infoSub2SSBO=infoSub2SSBO;
		this.bindingPointIndSubB2=bindingPointIndSubB2; 
		this.indexSub2SSBO=indexSub2SSBO;
		
		
		this.width=width;
		this.height=height;
		this.tileWidth=tileWidth;
		this.tileHeight=tileHeight;
		this.tileWidthSub=tileWidthSub;
		this.tileHeightSub=tileHeightSub;
		this.tileWidthSub2=tileWidthSub2;
		this.tileHeightSub2=tileHeightSub2;
		this.MAX_PLIGHT=MAX_PLIGHT;
		this.maxLightsPerTile=maxLightsPerTile;
		
		
		FPprogram.bind();
		FPprogram.setInt("tileWidth", tileWidth);
		FPprogram.setInt("tileHeight", tileHeight);
		FPprogram.setInt("width", width);
		FPprogram.setInt("height", height);
		FPprogram.unbind();
		
		
		
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
	public void drawDepthOnly(Vector3f position, float size, int m)
	{
		M = ModelView(new Matrix4f(),position);
		
		MV = ModelView(view,position, size);
		
		
		
		MVP = ModelViewProjection(MV,proj);
		
		normM = createInverseTranspose(MV);
		
		
		setDPProgramParamA(normM,view,M,MV,MVP);
		
		
		glDrawArrays(GL_TRIANGLES,0,m);
		
		
		
		
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
	
	
	
	
	
	
	
	
	@Override
	public String getName()
	{
		return "TiledForward";
	}
	
	@Override
	public void setRasterMode(int raster)
	{
		FPprogram.bind();
		FPprogram.setInt("RasterMode", raster);
		FPprogram.unbind();
	}
	
	int tileTestW0;
	int tileTestH0;
	int infoSSBOTest;
	int indexSSBOTest;
	
	int tileTestW1;
	int tileTestH1;
	
	int tileTestW2;
	int tileTestH2;
	
	int tileTestW3;
	int tileTestH3;
	
	int infoSSBOTest2;
	int indexSSBOTest2;
	
	@Override
	public void setTestInt(
			int tileTestW0,
			int tileTestH0,
			int infoSSBOTest,
			int indexSSBOTest,
			
			int tileTestW1,
			int tileTestH1,
			
			int tileTestW2,
			int tileTestH2,
			
			int tileTestW3,
			int tileTestH3,
			
			int infoSSBOTest2,
			int indexSSBOTest2)
	{
		 this.tileTestW0=tileTestW0;
		 this.tileTestH0=tileTestH0;
		 this.infoSSBOTest=infoSSBOTest;
		 this.indexSSBOTest=indexSSBOTest;
		
		 this.tileTestW1=tileTestW1;
		 this.tileTestH1=tileTestH1;
		
		 this.tileTestW2=tileTestW2;
		 this.tileTestH2=tileTestH2;
		
		 this.tileTestW3=tileTestW3;
		 this.tileTestH3=tileTestH3;
		
		 this.infoSSBOTest2=infoSSBOTest2;
		 this.indexSSBOTest2=indexSSBOTest2;		
		
		
		
	}
	ByteBuffer byteZero;
	
	

	public ByteBuffer createBBZ(int size, int j)
	{
		ByteBuffer iB = BufferUtils.createByteBuffer(size * Integer.SIZE/8);
		byte b[] = new byte[size];
		
		
		
		for(int i = 0; i < b.length/4; i++ )
		{
			for(int k = 0; k < 4; k++)
				{
					int offset = (4 - 1 - k) * 8;
					b[i + k] = (byte)((j >>> offset) & 0xFF);
				}
		}
		
		iB.put(b);
		iB.flip();
		
		return iB;
		
	}
	public void computeZ(int tW, int tH,int defDepthTexture)
	{
		ComputeZ.bind();
		
		ComputeZ.setInt("width", width);
		ComputeZ.setInt("height", height);
		ComputeZ.setInt("tileWidth", tW);
		ComputeZ.setInt("tileHeight", tH);
		
		
		
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, defDepthTexture);
		ComputeZ.setInt("depthTex", 0);
		
		GL43.glDispatchCompute((width / tW), (height / tH),1);
		GL42.glMemoryBarrier(GL42.GL_ALL_BARRIER_BITS);
		
		/*
		int w = width / tileW;
		int h = height / tileH;
		
		FloatBuffer ib = BufferUtils.createFloatBuffer(w*h*2);
		glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, 3, zMinMaxBuf);
		glGetBufferSubData(GL42.GL_ATOMIC_COUNTER_BUFFER,0,ib);
		ib.rewind();
		for(int i = 0; i < 1; i++)
		{
		System.out.println(i + " : " + ib.get(0));
			
		}
		*/
		
		
		ComputeTile.bind();
	}
	
	public void clearBuffer(int bindingPoint, int SSBO)
	{
		glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, 4, SSBO);
		
		ARBClearBufferObject.glClearBufferData(GL42.GL_ATOMIC_COUNTER_BUFFER, GL_R32I,GL_RED,GL_INT, byteZero);
		
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPoint, SSBO);
		
	}
	
	
	public void computeTiles( boolean ZCULLING, int TiledMode, int defDepthTexture)
	{
		Matrix4f mat = new Matrix4f();
		Matrix4f proj;
		
		
		
		proj = new Matrix4f(this.proj);
		
	
		
		Matrix4f.invert(proj, mat);
		
		
		
		
		
			
		ComputeTile.bind();
		ComputeTile.setUniformMatrix4f("invProj", mat);
		ComputeTile.setUniformMatrix4f("view", view);
		ComputeTile.setInt("width", width);
		ComputeTile.setInt("height", height);
		ComputeTile.setInt("tileWidth", tileWidth);
		ComputeTile.setInt("tileHeight", tileHeight);
		ComputeTile.setInt("tileWidthSub", tileWidthSub);
		ComputeTile.setInt("tileHeightSub", tileHeightSub);
		ComputeTile.setInt("tileWidthSub2", tileWidthSub2);
		ComputeTile.setInt("tileHeightSub2", tileHeightSub2);
		ComputeTile.setInt("LIGHT_MAX", MAX_PLIGHT);
		ComputeTile.setInt("MAX_LIGHT_PER_TILE", maxLightsPerTile);
		
		
		/*
		 * modes:
		 * 0. tiled without additional subdivision
		 * count : (width/tileWidth),(height/tileHeight),1
		 * 
		 * 1. first add. subdivision
		 * count : (width/tileWidthSub),(height / tileHeightSub),1
		 * 
		 * 2. second add. subdivision - only if first was done before
		 * count : (width/tileWidthSub2),(height / tileHeightSub2),1
		 * 
		 * 3. read from second subdivision - only if first and second were done before
		 * count : (width/tileWidth),(height / tileHeight),1
		 * 
		 * 4. read from first subdivision - only if first was done before
		 * count : (width/tileWidth),(height / tileHeight),1
		 * 
		 * 5. subdivision with seconds's width/height
		 * count : (width/tileWidthSub2),(height / tileHeightSub2),1
		 * 
		 */
		
		int zCulling = 0;
		/*
		 * Funktioniert noch nicht richtig
		 * 
		 */
		
		if(ZCULLING)
		{
			
			zCulling = 1;
			computeZ( tileWidth,  tileHeight,defDepthTexture);
			
		
		}
		
		ComputeTile.setInt("zCulling", zCulling);
		
		
		switch(TiledMode){
		
		case 0:
		case 1:
		case 2:
		case 3:
			
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfB, infoSSBO);
		
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndB, indexSSBO);
			
	
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB, infoSubSSBO);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB, indexSubSSBO);
	
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB2, infoSub2SSBO);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB2, indexSub2SSBO);
			
			
			break;
			
		case 4:
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfB, infoSSBO);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndB, indexSSBO);
			
			break;
		
		}
		
		
		
		
		
		switch(TiledMode)
		{
			
		case 0://without additional subdivision
			ComputeTile.setInt("mode", 0);
			
			//computeZ( tileWidth,  tileHeight);
			
			
			
			GL43.glDispatchCompute((width / tileWidth), (height / tileHeight),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			
			
			break;
			
			
		case 1://with one add. subdivision (first's height/width)
			ComputeTile.setInt("mode", 1);
			
			//computeZ( tileWidth,  tileHeight);
			
			GL43.glDispatchCompute((width / tileWidthSub), (height / tileHeightSub),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
	
			
			//read from first subdivision
			ComputeTile.setInt("mode", 4);
			
			
			GL43.glDispatchCompute((width / tileWidth), (height / tileHeight),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			break;
			
		case 2://with one add. subdivision (second's height/width)
			
			ComputeTile.setInt("mode", 5);
					
			//computeZ( tileWidth,  tileHeight);
			
			GL43.glDispatchCompute((width / tileWidthSub2), (height / tileHeightSub2),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
		
			
			
			//read from second subdivision
			ComputeTile.setInt("mode", 3);
			
			
			
			
			
			GL43.glDispatchCompute((width / tileWidth), (height / tileHeight),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
		
			break;
				
		case 3://with two add. subdivisions
			
			//first subdivision
			ComputeTile.setInt("mode", 1);
			//computeZ( tileWidth,  tileHeight);	

			
			ComputeTile.setInt("zCulling", 0);
			
			GL43.glDispatchCompute((width / tileWidthSub), (height / tileHeightSub),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
		
			
			//second subdivision
			ComputeTile.setInt("mode", 2);
						
			
			GL43.glDispatchCompute((width / tileWidthSub2), (height / tileHeightSub2),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
		
			
			
			
			
			//read from second subdivision
			ComputeTile.setInt("mode", 3);
			

			
			ComputeTile.setInt("zCulling", zCulling);
			
			
			
			GL43.glDispatchCompute((width / tileWidth), (height / tileHeight),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
		
			
			break;
			
			
			
			
		
		case 4:

			/*
			clearBuffer(infoSSBO);
			clearBuffer(indexSSBO);
			
			clearBuffer(infoSSBOTest);

			clearBuffer(indexSSBOTest);

			clearBuffer(infoSSBOTest2);

			clearBuffer(indexSSBOTest2);
			*/
			
			//computeZ( tileWidth,  tileHeight);
			
			int tileW = tileTestW0;
			int tileH = tileTestH0;
			

			
			ComputeTile.setInt("zCulling", 0);
			//first
			ComputeTile.setInt("mode", 1);
			ComputeTile.setInt("tileWidthSub", tileW);
			ComputeTile.setInt("tileHeightSub", tileH);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB, infoSSBOTest);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB, indexSSBOTest);
			
			GL43.glDispatchCompute((width / tileW), (height / tileH),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			
			//second
			
			tileW = tileTestW1;
			tileH = tileTestH1;
			
			ComputeTile.setInt("mode", 2);
			ComputeTile.setInt("tileWidthSub2", tileW);
			ComputeTile.setInt("tileHeightSub2", tileH);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB2, infoSSBOTest2);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB2, indexSSBOTest2);
			
			GL43.glDispatchCompute((width / tileW), (height / tileH),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			//clear first
			clearBuffer(bindingPointInfSubB, infoSSBOTest);
			clearBuffer(bindingPointIndSubB, indexSSBOTest);
			
			
			//swap
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB2, infoSSBOTest);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB2, indexSSBOTest);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB, infoSSBOTest2);
			
			glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB, indexSSBOTest2);
			
			//third
			
			ComputeTile.setInt("tileWidthSub", tileW);
			ComputeTile.setInt("tileHeightSub", tileH);
			
			tileW = tileTestW2;
			tileH = tileTestH2;
			
			ComputeTile.setInt("mode", 2);
			ComputeTile.setInt("tileWidthSub2", tileW);
			ComputeTile.setInt("tileHeightSub2", tileH);
			
			
			GL43.glDispatchCompute((width / tileW), (height / tileH),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			//last
			
		
			
			ComputeTile.setInt("mode", 4);
			
			tileW = tileTestW3;
			tileH = tileTestH3;
						
			ComputeTile.setInt("tileWidth", tileW);
			ComputeTile.setInt("tileHeight", tileH);
			

			
			ComputeTile.setInt("zCulling", zCulling);
			
			GL43.glDispatchCompute((width / tileW), (height / tileH),1);
			GL42.glMemoryBarrier( GL42.GL_ALL_BARRIER_BITS);
			
			
			
			break;
		
		
		
		
		}
	}


	public void bindDText(int depthTexture) {
		// TODO Auto-generated method stub
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		DPprogram.setInt("depthTex", 0);
	}


	public void drawToScreen() {
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
		
	}
	

	
	public void prepareDrawRend(int width, int height, Matrix4f ortho, int renderedTexture)
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		glBindTexture(GL_TEXTURE_2D,renderedTexture);
		
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


	
	
}

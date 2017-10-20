package deferred.ShaderPrepClasses;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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

import deferred.Light;
import deferred.MatrixUtil;
import deferred.ShaderProgram;

public class TiledDeferredPar extends Deferred {
	public ShaderProgram AmbientP;
	
	public TiledDeferredPar(float C, float D, int tileWidth, int tileHeight) {
		
		
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
        ComputeZ.attachComputeShader(folderOfAlgorithms+"ComputeZ.comp", a);
        ComputeZ.link();
        ComputeZ.bind();
        
        ComputeZ.setFloat("C", C);	
		ComputeZ.setFloat("D", D);
		
		
		FPprogram = new ShaderProgram();
		FPprogram.bind();
		FPprogram.attachVertexShader(folderOfAlgorithms+"DeferredRenFP.vert");
		FPprogram.attachFragmentShader(folderOfAlgorithms+"DeferredRenFP.frag");
		FPprogram.link();
		
		AmbientP = new ShaderProgram();
		AmbientP.bind();
		AmbientP.attachVertexShader(folderOfAlgorithms+"DeferredRenLPAmb.vert");
		AmbientP.attachFragmentShader(folderOfAlgorithms+"DeferredRenLPAmb.frag");
		AmbientP.link();
		
		SPprogram = new ShaderProgram();
		SPprogram.bind();
		SPprogram.attachVertexShader(folderOfAlgorithms+"TiledDef.vert");
		SPprogram.attachFragmentShader(folderOfAlgorithms+"TiledDef.frag");
		SPprogram.link();
	
		byteZero = createBBZ(1, 0);
		
		
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
	

	@Override
	public String getName()
	{
		return "TiledDeferred";
	}

	@Override
	public void init(Light[] pLightA, int MAX_PLIGHT, Vector3f ambientLight){
		

		SPprogram.bind();
		SPprogram.setUniformVec3f("ambientLight", ambientLight);
		SPprogram.setPointlights("pLight", pLightA);
		SPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);
   
	
        
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

	
	public void prepareDrawSP(int RasterMode,int defDepthTexture, int normalTexture, int diffuseTexture, int lightTexture) {
		
		
		SPprogram.bind();
		
		glEnable(GL_TEXTURE_2D);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		
		//setParamDefS
		
		
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
		SPprogram.setInt("width", width);
		SPprogram.setInt("height", height);
		SPprogram.setInt("tileWidth", tileWidth);
		SPprogram.setInt("tileHeight", tileHeight);
		SPprogram.setInt("LIGHT_MAX", MAX_PLIGHT);
		SPprogram.setInt("MAX_LIGHT_PER_TILE", maxLightsPerTile);
		SPprogram.setInt("RasterMode", RasterMode);
		
		
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
		
		//checkE(true);
		
		GL42.glMemoryBarrier(GL42.GL_ALL_BARRIER_BITS);
	
		
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
		
		if(ZCULLING)
		{
			
			zCulling = 1;
			computeZ( tileWidth,  tileHeight,defDepthTexture);
			
		//	System.out.println(tileWidth);
			
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
	
	
	
	
	
	
	
}

package deferred;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;


/*
 * 
 * Klasse zu einfacherem Ansprechen und Behandeln von Klassen, teilweise r�berkopiert von 
 * den Examples von http://arcsynthesis.org/gltut/
 */

public class ShaderProgram {
	
	
	int programID;
	
	int vertexShaderID;
	
	int fragmentShaderID;
	
	int computeShaderID;
	
	FloatBuffer buffer;
	
	public ShaderProgram()
	{
		buffer = BufferUtils.createFloatBuffer(16);
		
		programID = glCreateProgram();
		
	}
	
	
	
	public static String readFully(InputStream is) {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			StringBuilder s = new StringBuilder();
			String l;
			
			while((l = reader.readLine()) != null)
				s.append(l).append('\n');
			
			return s.toString();
		} catch(Exception exc) {
			throw new RuntimeException("Failure reading input stream", exc);
		}
	}
	
	public String readFromFile(String file) {
		try {
			return readFully(getClass().getResourceAsStream(file));
		} catch(Exception exc) {
			throw new RuntimeException("Failure reading file " + file, exc);
		}
	}

	
	
	public void attachVertexShader(String name, String a)
	{
		
		
		vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderID, a + readFromFile(name));
		
		glCompileShader(vertexShaderID);
		
		if(glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failure in compiling vertex shader: " + name + ". Error log:\n" + glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
			System.exit(0);
		}
		else
		{
			System.out.print("Success vert\n");
			
			
		}
		
		glAttachShader(programID, vertexShaderID);
		
		
	}
	
	public void attachFragmentShader(String name, String a)
	{
		
		
		
		fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID,a + readFromFile(name));
		
		glCompileShader(fragmentShaderID);
		
		if(glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failure in compiling fragment shader: " + name + ". Error log:\n" + glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
			Display.destroy();
			System.exit(0);
		}
		else
		{
			System.out.print("Success frag\n");
			
			
		}
		
		
		glAttachShader(programID, fragmentShaderID);
		
		
	}
	
	
	
	public void attachVertexShader(String name)
	{
		
		attachVertexShader(name,"");
		
	}
	
	public void attachFragmentShader(String name)
	{
		
		attachFragmentShader(name,"");
	}
	
	
	
	
	public void attachComputeShader(String name)
	{
		
		
		attachComputeShader(name,"");
		
	}
	
	public void attachComputeShader(String name, String a)
	{
		
		
		
		computeShaderID = glCreateShader(GL_COMPUTE_SHADER);
		String b = a + readFromFile(name);
		
		
		glShaderSource(computeShaderID,b);
		
		glCompileShader(computeShaderID);
		
		if(glGetShaderi(computeShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failure in compiling fragment shader: " + name + ". Error log:\n" + glGetShaderInfoLog(computeShaderID, glGetShaderi(computeShaderID, GL_INFO_LOG_LENGTH)));
			Display.destroy();
			System.exit(0);
		}
		else
		{
			System.out.print("Success Compute\n");
			
			
		}
		
		
		glAttachShader(programID, computeShaderID);
		
		
	}
	
	
	public static void unbind()
	{
		
		glUseProgram(0);
		
		
	}
	
	public void bind()
	{
		
		glUseProgram(programID);
	}
	
	public void dispose()
	{
		
		unbind();
		
		glDetachShader(programID,vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
	
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		
		
		glDeleteProgram(programID);
		
		
		
	}
	
	public int getID()
	{
		return programID;
		
		
	}
	
	public void link()
	{
		
		
		glLinkProgram(programID);
		
		String infoLog = glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH));
		
		if(glGetProgrami(programID, GL_LINK_STATUS)==GL_FALSE)
		{
			System.err.println("Unable to link shader program:" + infoLog);
			dispose();
		}
		
		
		
		
		
	}
	
	public void setUniformMatrix4f(String name, Matrix4f value)
	{
		long b = Main.getTime();
		
		if(value != null){
		int i = glGetUniformLocation(programID,name);
		
		glUniformMatrix4(i,false,MatrixUtil.toFloatBuffer(value, buffer));
		
		}
	}

	public void setUniformVec3f(String name, Vector3f value)
	{
		int i = glGetUniformLocation(programID,name);
		
		glUniform3f(i,value.x, value.y, value.z);
		
	}
	
	public void setUniformVec4f(String name, Vector4f value)
	{
		int i = glGetUniformLocation(programID,name);
		
		glUniform4f(i,value.x, value.y, value.z, value.w);
		
	}
	
	public void setFloat(String name, float value)
	{
		
		int i = glGetUniformLocation(programID, name);
		
		glUniform1f(i, value);		
	}
	
	public void setInt(String name, int value)
	{
		int i = glGetUniformLocation(programID, name);
		
		glUniform1i(i, value);		
	}
	
	
	
	
	public void setPointlights(String name, Light li[])
	{
		
		
		for(int i = 0; i < li.length; i++)
		{
			int pos = glGetUniformLocation(programID, name + "[" + i + "].position");
			int ambient = glGetUniformLocation(programID, name + "[" + i + "].ambient");
			int diff = glGetUniformLocation(programID, name + "[" + i + "].diff");
			int spec = glGetUniformLocation(programID, name + "[" + i + "].specular");
			int disA = glGetUniformLocation(programID, "lDistance[" + i + "]");
			
			glUniform3f(pos, li[i].getPos().x,li[i].getPos().y,li[i].getPos().z);
			glUniform3f(ambient, li[i].ambient.x,li[i].ambient.y,li[i].ambient.z);
			glUniform3f(diff, li[i].diff.x,li[i].diff.y,li[i].diff.z);
			glUniform3f(spec, li[i].specular.x,li[i].specular.y,li[i].specular.z);
			glUniform1f(disA, li[i].distance);
			
			
			
			
		}
		
	}
	
	
	
	public void setUniformMatrix4fA(String name, Matrix4f a[], int MAX)
	{
		
		for(int i = 0; i < MAX; i++)
		{
			
			int pos = glGetUniformLocation(programID,name + "[" + i + "]");
			
			glUniformMatrix4(pos,false,MatrixUtil.toFloatBuffer(a[i]));
			
			
			
		}
		
	}
	
	
	
	
	
	
	
	
	public void updateLightPositions(String name, Light li[])
	{
		
		for(int i = 0; i < li.length; i++)
		{
			int pos = glGetUniformLocation(programID, name + "[" + i + "].position");
			
			glUniform3f(pos, li[i].getPos().x,li[i].getPos().y,li[i].getPos().z);
					
		}
		
		
	}
	

}

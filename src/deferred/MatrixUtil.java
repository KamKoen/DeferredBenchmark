package deferred;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/*
 * Sonstige Matrix-Funktionen wie Porjektionsmatrixberechnung, oder zur Floatbufferbenutzung wegen Uniform-
 * �bergabe an die Shader
 */
public class MatrixUtil {
	/*
	 * Mache einen Floatbuffer aus einer Matrize
	 */
	public static FloatBuffer toFloatBuffer(Matrix4f mat)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		mat.store(buffer);
		buffer.flip();
		return buffer;
		
		
	}
	
	/*
	 * Mache einen Floatbuffe aus einer Matrixze im �bergebenen Floatbuffer
	 */
	public static FloatBuffer toFloatBuffer(Matrix4f mat, FloatBuffer buffer)
	{
		long b = Main.getTime();
		buffer.clear();
		mat.store(buffer);
		buffer.flip();
		if(Main.getTime() - b > 10)System.out.println("buffer" + " "  + (Main.getTime()-b));
		return buffer;
		
		
	}


	
	
	/*
	 * Multiplikation von Matrizen
	 */
	public static Vector4f mul(Vector4f vec, Matrix4f mat)
	{
		Vector4f ret = new Vector4f();
		
		Matrix4f matMul = new Matrix4f();
		
		matMul.setZero();
		
		matMul.m00 = vec.x;
		matMul.m01 = vec.y;
		matMul.m02 = vec.z;
		matMul.m03 = vec.w;
		
		Matrix4f.mul(mat, matMul, matMul);
		
		ret.x = matMul.m00;
		ret.y = matMul.m01;
		ret.z = matMul.m02;
		ret.w = matMul.m03;
		
		return ret;
		
		
		
		
	}
	
	public static Matrix4f createPerspectiveProjection(float fov, float aspect, float zNear, float zFar)
	{
		
		
		Matrix4f mat = new Matrix4f();
	
		float yScale = 1f / (float) Math.tan(Math.toRadians(fov/2f));
		
		float xScale = yScale / aspect;
        float frustumLength = zFar - zNear;

        mat.m00 = xScale;
        mat.m11 = yScale;
        mat.m22 = -((zFar + zNear) / frustumLength);
        mat.m23 = -1;
        mat.m32 = -((2 * zFar * zNear) / frustumLength);
        mat.m33 = 0;

        return mat;		
	}
	
	public static Matrix4f createIdentityMatrix()
    {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        return mat;
    }
	
	public static Matrix4f createOrthogonalMatrix(float left, float bottom, float top, float right, float far, float near)
	{
		
		Matrix4f mat = new Matrix4f();
		
		mat.setZero();
		mat.m00 = (2 / (right - left));
		mat.m11 = (2 / (top - bottom));
		mat.m22 = (-2 / (far - near));
		mat.m30 = -(right + left)/(right - left);
		mat.m31 = -(top + bottom)/(top - bottom);
		mat.m32 = (far + near)/(far - near);
		mat.m33 = 1;
		
		
		
		
		
		return mat;
		
		
		
		
		
	}
	
	
	
	

}

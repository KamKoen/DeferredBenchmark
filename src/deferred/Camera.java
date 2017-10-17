package deferred;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


/*
 * Kameraklasse mit Bezierkurvenbildung für Benchmarktest
 * 
 * 
 */

public class Camera {
	
	
	
	
	/*
	 * 
	 * Bezier curve spline
	 * 
	 */
	
	public List<Vector3f> points = new ArrayList<Vector3f>();
	
	int curPoint = 0;
	
	int maxPoints = 0;
	
	
	public float length = 0.0f;
	public int curveCount = 0;
	public float timePerCurve = 0;
	
	
	
	public void setLength(float l)
	{
		
		length = l;
		if(curveCount > 0)timePerCurve = length / (float)curveCount;
	}
	
	
	
	public void setUpBench(Vector3f[] vA, float length ){
		
		points.clear();
		
		for(Vector3f i:vA)
		{
			
			points.add(i);
			
		}		
		
		curPoint = 0;
		
		maxPoints = vA.length;
		
		curveCount = (maxPoints - 1)/3;
		
		this.length = length;
		
		timePerCurve = length / (float)curveCount;
	}
	
	public float C0(float u)
	{
		
		return (1 - u) *(1 - u) * (1 - u);
	}
	
	public float C1(float u)
	{
		return 3 * u * (1-u)*(1-u);
	}
	
	public float C2(float u)
	{
		return 3 * u * u * (1-u);
	}
	
	public float C3(float u)
	{
		return u*u*u;
	}
	
	
	public Vector3f calcNewP(Vector3f P, Vector3f Q, Vector3f R, Vector3f S, float u)
	{
		
		
		
		float x = P.x * C0(u) + C1(u) * Q.x + C2(u) * R.x + C3(u)*S.x;
		float y = P.y * C0(u) + C1(u) * Q.y + C2(u) * R.y + C3(u)*S.y;
		float z = P.z * C0(u) + C1(u) * Q.z + C2(u) * R.z + C3(u)*S.z;
		
		Vector3f res = new Vector3f(x,y,z);
		
		
		return res;
		
	}
	
	
	
	
	
	public boolean start = false;
	
	//public boolean end = false;
	
	public void startBench()
	{
		
		start = true;
		setPosition(points.get(0));
	
	}
	
	public void startBench(float t)
	{
		
		start = true;
		setPosition(points.get(0));
		length = t;
		if(curveCount > 0)timePerCurve = length / (float)curveCount;
		
	}
	
	float BenchElapsed = 0.0f;
	
	
	public void endBench()
	{
		start = false;
		BenchElapsed = 0;
		curPoint = 0;
	}
	
	
	public void setUpWithPForKnots(Vector3f[] K, float length)
	{
		/*P0 und P3 sind Knoten
		 *P1 und P2 müssen berechnet werden
		 *
		 *Code-quelle : http://www.particleincell.com/wp-content/uploads/2012/06/bezier-spline.js
		 *
		 *
		 *Für den ersten Knoten gilt:
		 *
		 *es gibt kein P1 von -1, daher a = 0
		 *
		 *2 * P1 von i + P1 von 1 = K0 + 2 K1
		 *
		 *Für den vorletzten Knoten gilt:
		 *
		 *2 * P1 von n-2 + 7*P1 von n-1 = 8 * Kn-1 + Kn
		 *
		 */
		
		List <Vector3f> P1List = new ArrayList<Vector3f>();
		List <Vector3f> P2List = new ArrayList<Vector3f>();
		
		//Skalare für P1
		List <Float> AList = new ArrayList<Float>();	//P1 von i-1
		List <Float> BList = new ArrayList<Float>();	//P1 von i
		List <Float> CList = new ArrayList<Float>();	//P1 von i+1
		List <Vector3f> RList = new ArrayList<Vector3f>();	//Knoten
		
		int AnzahlSegmente = K.length - 1;
		
		//Von der oberen Formel, 
		//Erster Knoten
		AList.add(0.0f);
		BList.add(2.0f);
		CList.add(1.0f);
		Vector3f temp = new Vector3f(K[1]);
		temp.scale(2);
		Vector3f.add(K[0],temp, temp);
		RList.add(temp);
		//restliche Knoten bis auf den vorletzten
		
		for(int i = 1; i < AnzahlSegmente - 1; i++)
		{
			AList.add(1.0f);
			BList.add(4.0f);
			CList.add(1.0f);
			Vector3f temp2 = new Vector3f(K[i]);
			temp2.scale(4);
			Vector3f temp3 = new Vector3f(K[i + 1]);
			temp3.scale(2);
			Vector3f.add(temp2, temp3, temp3);
			RList.add(temp3);
		}
		
		//vorletzter Knoten
		
		AList.add(2.0f);
		BList.add(7.0f);
		CList.add(0.0f);
		Vector3f temp4 = new Vector3f(K[AnzahlSegmente-1]);
		temp4.scale(8);
		Vector3f.add(temp4, new Vector3f(K[AnzahlSegmente]), temp4);
		RList.add(temp4);
		
		//Berechne über Thomas Algorithmus
		
		
		for(int i = 1; i < AnzahlSegmente; i++)
		{
			
			float m = AList.get(i) / BList.get(i-1);
			BList.set(i, BList.get(i) - m * CList.get(i-1));
			Vector3f temp6 = new Vector3f(RList.get(i-1));
			temp6.scale(m);
			Vector3f.sub(RList.get(i), temp6, temp6);
			RList.set(i, temp6);
		}
		
		
		for(int i = 0; i < AnzahlSegmente; i++)
		{
			P1List.add(new Vector3f(0,0,0));	
		}
		
		
		//P1 berechnen
		
		Vector3f temp7 = new Vector3f(RList.get(AnzahlSegmente-1));
		temp7.scale(1 / BList.get(AnzahlSegmente-1));
		
		P1List.set(AnzahlSegmente-1, temp7);
		
		for(int i = AnzahlSegmente - 2; i >= 0; --i)
		{
			Vector3f temp8 = new Vector3f(RList.get(i));
			Vector3f temp9 = new Vector3f(P1List.get(i + 1));
			
			float newP1x = (temp8.x - CList.get(i) * temp9.x) / BList.get(i);
			float newP1y = (temp8.y - CList.get(i) * temp9.y) / BList.get(i);
			float newP1z = (temp8.z - CList.get(i) * temp9.z) / BList.get(i);	
		
			P1List.set(i, new Vector3f(newP1x,newP1y,newP1z));			
		}
		
		//Aus P1 und K P2 berechnen
		for(int i = 0; i < AnzahlSegmente - 1; i++)
		{
			
			Vector3f temp11 = P1List.get(i + 1);
			Vector3f temp10 = K[i+1];
			
			float P2x = 2 * temp10.x - temp11.x;
			float P2y = 2 * temp10.y - temp11.y;
			float P2z = 2 * temp10.z - temp11.z;
			
			P2List.add(new Vector3f(P2x, P2y,P2z));
			
			
		}
		
		Vector3f temp12 = K[AnzahlSegmente];
		Vector3f temp13 = P1List.get(AnzahlSegmente - 1);
		float P2x = 0.5f * (temp12.x + temp13.x);
		float P2y = 0.5f * (temp12.y + temp13.y);
		float P2z = 0.5f * (temp12.z + temp13.z);
		
		
		
		P2List.add(new Vector3f(P2x,P2y,P2z));
		
		
		
		
		
		Vector3f VA[] = new Vector3f[AnzahlSegmente *3 + 1];
		
		for(int i = 0; i < AnzahlSegmente; i++)
		{
			VA[i * 3] = (new Vector3f(K[i]));
			VA[i * 3 + 1]=(new Vector3f(P1List.get(i)));
			VA[i * 3 + 2]=(new Vector3f(P2List.get(i)));
		}
		
		VA[AnzahlSegmente * 3]=(new Vector3f(K[AnzahlSegmente]));

		
		this.setUpBench(VA,length);
		

		
	}
	
	
	
	public void updatePosBench(float elapsedTime)
	{
		if(start)
		{
			elapsedTime /= 1000.0f; //sollte durch's teilen in sekunden sein
			
			if(this.position == points.get(maxPoints -1))
			{
				endBench();
				return;
			}
			int test = (int) (BenchElapsed / timePerCurve);
			
			
			
			if(elapsedTime > 0.5f) elapsedTime = 0.5f;
			
			
			BenchElapsed += elapsedTime;
			
			int cur = (int) (BenchElapsed / timePerCurve);
			
			if(test != cur)//über den nächsten Punkt hinaus
			{
				//position = points.get(curPoint+3);
				curPoint += 3;
			}
			
			if(BenchElapsed >= length)
			{
				this.position = points.get(maxPoints-1);
			}
			
			
			
			else
			{
				float curUPre = BenchElapsed - (timePerCurve * cur);
				
				float u = curUPre / timePerCurve;
				
				Vector3f nP = calcNewP(points.get(curPoint), points.get(curPoint+1),points.get(curPoint+2),points.get(curPoint+3),u);
			
				
				Vector3f DirFromBench = new Vector3f(0,0,1);
				
				Vector3f.sub(nP, position, DirFromBench);
				
				
				DFB = new Vector3f(DirFromBench);
				
				
				position = nP;
				
			}
			
				
			
			
			
			
		}
		
	}
	
	
	Vector3f DFB = new Vector3f(0,0,1);
	
	public Vector3f getDirFromBench()
	{
		
		return new Vector3f(DFB);
		
	}
	
	
	
	
	
	
	
	
	
	/* 
	 * /\/\/\
	 * ||||||
	 * Kamerafahrten
	 * 
	 * 
	 * 
	 * Kameraclass
	 * 
	 * ||||||
	 * VVVVVV
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	
	private float fov;
	
	private float aspect;
	
	private float zNear;
	
	private float zFar;
	
	private Matrix4f projection;
	
	private Matrix4f view;
	
	private Vector3f position;
	
	private Vector3f rotation;
	
	private Vector3f xAxis, yAxis, zAxis;

	private int mouseSpeed = 2;

	private float kA = 0.16f;
	
	private float maxLookDown = -90;
	private float maxLookUp = 90;
	
	private Vector3f dir;
	
	
	
	public Camera(float fov, float aspect, float zNear, float zFar)
	{
		
		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;
		
		projection = MatrixUtil.createPerspectiveProjection(fov, aspect, zNear, zFar);
		view = MatrixUtil.createIdentityMatrix();
		
		
		position = new Vector3f(0,0,0);
		rotation = new Vector3f(0,0,0);
		
		xAxis = new Vector3f(1,0,0);
		yAxis = new Vector3f(0,1,0);
		zAxis = new Vector3f(0,0,1);
		
		glEnable(GL_DEPTH_TEST);
		
		dir = new Vector3f(0,0,1);
	}
	
	
	public void setFOV(float fov)
	{
		this.fov = fov;
		
		projection = MatrixUtil.createPerspectiveProjection(fov, aspect, zNear, zFar);
		
	}
	
	public void changeAspect (float aspect)
	{
		
		this.aspect = aspect;
		projection = MatrixUtil.createPerspectiveProjection(fov, aspect, zNear, zFar);
		apply();
	}
	
	public void addRotation(float x, float y, float z)
	{
		
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
		
	}
	
	public void addPosition(float x, float y, float z)
	{
		
		position.x += x;
		position.y += y;
		position.z += z;
		
		
	}
	
	public void addPosition(Vector3f vec)
	{
		
		addPosition(vec.x, vec.y, vec.z);
		
	}
	
	public void addRotation(Vector3f vec)
	{
		
		addRotation(vec.x, vec.y, vec.z);
		
	}
	
	public void rotateByMouse(int mDx, int mDy)
	{
		
		
		
		float mouseDx = mDx * mouseSpeed  * kA;
		
	
		
		float mouseDy = mDy * mouseSpeed  * kA;
			
		
		rotation.y += mouseDx;
		
		if(rotation.x - mouseDy >= maxLookDown && rotation.x - mouseDy <= maxLookUp){
			rotation.x -= mouseDy;
		}
		else if(rotation.x - mouseDy < maxLookDown){
			
			rotation.x = maxLookDown;
		}
		else if(rotation.x -mouseDy > maxLookUp){
			rotation.x = maxLookUp;
		}
		
		
		
		
		//System.out.print("X: " + rotation.x + " Y: " + rotation.y + " " + mouseDx + "\n");
		
	}
	
	
	
	
	public void apply()
	{
		
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(rotation.x), xAxis, view, view);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), yAxis, view, view);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), zAxis, view, view);
		
		
		Matrix4f com = new Matrix4f();
		com.setZero();
		
		com.m00 = 0;
		com.m01 = 0;
		com.m02 = -1;
		
		Matrix4f.mul(view, com, com);
		
		dir.x = com.m00; //camera richtung
		dir.y = com.m01;
		dir.z = com.m02;
		
		dir.normalise();
		
		
		//System.out.println(com.m00 + " " + com.m01 + " " + com.m02 +  "\n");
		//System.out.println(view.m00 + " " + view.m01 + " " + view.m02 +  "\n");
		
		Matrix4f.translate(new Vector3f(-position.x, -position.y, -position.z),  view,  view);
		
		
	}
	
	
	
	
	
	public void lookAt(Vector3f pos)
	{
	
		Vector3f direction = new Vector3f();

		
		Vector3f.sub(pos, position , direction);
		direction.normalise();
		
		Vector3f initDir = new Vector3f(0,0,-1);
		float angleHor = 0;
		float angleVer = 0;
		
		//horiz / X_Z_EBENE PROJ
		Vector3f projDirXZ = new Vector3f(direction.x,0,direction.z);
		projDirXZ.normalise();
		Vector3f cross = new Vector3f();
		
		
		float cos = Vector3f.dot(initDir, projDirXZ); 
		
		angleHor = (float) Math.toDegrees(Math.acos(cos));
		
		Vector3f.cross(initDir, projDirXZ,cross);
		
		if(cross.y > 0) angleHor *= -1;
		
		if(!Float.isNaN(angleHor))
			rotation.y = angleHor;
		//System.out.println("a" + angleHor + " " + rotation.y + " " + cross.y);
		
		//horiz / Y_Z_EBENE PROJ
		
		cross = new Vector3f();
		
		
		cos = Vector3f.dot(projDirXZ, direction); 
		
		angleVer = (float) Math.toDegrees(Math.acos(cos));
		
		Vector3f.cross(projDirXZ, direction ,cross);
		
		
				
		
		if(direction.y < 0) 
			if(angleVer < 0) angleVer *= -1;

		if(direction.y > 0)
			if(angleVer > 0) angleVer *= -1;
		
		//System.out.println("a" + angleVer + " " + rotation.x + " " + direction.y );
		
		if(!Float.isNaN(angleVer))
			rotation.x = angleVer;
		
		
	}
	
	
	
	public void lookInDirection(Vector3f givendirection)
	{
	
		Vector3f direction = new Vector3f(givendirection);
		
		if(direction.x == 0 && direction.z == 0){
			
			
			
			rotation.y = 0;

			rotation.x = 90;
			
			
		}
		else{
		
		direction.normalise();

		Vector3f initDir = new Vector3f(0,0,-1);
		float angleHor = 0;
		float angleVer = 0;
		
		//horizontale / X_Z_EBENE PROJ
		Vector3f projDirXZ = new Vector3f(direction.x,0,direction.z);
		
		projDirXZ.normalise();
		Vector3f cross = new Vector3f();
		
		
		float cos = Vector3f.dot(initDir, projDirXZ); 
		
		angleHor = (float) Math.toDegrees(Math.acos(cos));
		
		Vector3f.cross(initDir, projDirXZ,cross);
		
		if(cross.y > 0) angleHor *= -1;
		
		if(!Float.isNaN(angleHor))
			rotation.y = angleHor;
		
		
		//horizontale / Y_Z_EBENE PROJ
		
		cross = new Vector3f();
		
		
		cos = Vector3f.dot(projDirXZ, direction); 
		
		angleVer = (float) Math.toDegrees(Math.acos(cos));
		
		Vector3f.cross(projDirXZ, direction ,cross);
		
		
				
		
		if(direction.y > 0) 
			if(angleVer > 0) angleVer *= -1;

		if(direction.y < 0)
			if(angleVer < 0) angleVer *= -1;
	
		if(!Float.isNaN(angleVer))
			rotation.x = angleVer;
		}
		
		
	}
	
	
	
	
	
	
	
	public void lookAt(float x, float y)
	{
		rotation.x = x;
		rotation.y = y;
		
		
	}
	
	public void applyM()
	{
		
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(rotation.x), xAxis, view, view);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), yAxis, view, view);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), zAxis, view, view);
		
		
		
		Matrix4f.translate(position,  view,  view);
		
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(rotation.x,1,0,0);
		glRotatef(rotation.y,0,1,0);
		glRotatef(rotation.z,0,0,1);
		glTranslatef(-position.x,-position.y,-position.z);
		glPopAttrib();
		
	}
	
	public void move(float amount, float direction) //direction = {1 vor/rück, 0 links/rechts}
	{
	    position.z += amount * Math.sin(Math.toRadians(rotation.y + 90 * direction))* Math.cos(Math.toRadians(rotation.x * direction ));
	    position.x += amount * Math.cos(Math.toRadians(rotation.y + 90 * direction))* Math.cos(Math.toRadians(rotation.x * direction));
	    position.y += amount * Math.sin(Math.toRadians(rotation.x * direction));
	}
	
	public void up(float amount, boolean up)
	{
		if(up)position.y += amount;
		else position.y -= amount;

	}
	
	public Vector3f getPosition()
	{
		
		return position;
		
	}
	
	public Vector3f getNormal()
	{
		return position;		
	}
	
	
	public void setPosition(Vector3f pos)
	{
		
		position = new Vector3f(pos);
		
	}
	
	public Vector3f getRotation()
	{
		
		return rotation;
		
	}
	public Vector3f getxAxis()
	{
		
		return xAxis;
		
	}
	public Vector3f getyAxis()
	{
		
		return yAxis;
		
	}
	public Vector3f getzAxis()
	{
		
		return zAxis;
		
	}


	
	
	
	
	
	
	public void setRotation(Vector3f vec)
	{
		
		rotation = vec;
		
	}
	public void setxAxis(Vector3f vec)
	{
		
		xAxis = vec;
		
	}
	public void setyAxis(Vector3f vec)
	{
		
		yAxis = vec;
		
	}
	public void setzAxis(Vector3f vec)
	{
		
		zAxis = vec;
		
	}
	
	

	
	public Matrix4f getProj()
	{
		
		return projection;
		
	}
	
	
	public void setProj(Matrix4f mat)
	{
		
		projection = new Matrix4f(mat);
		
	}


	public Matrix4f view(){
		
		
		
		
		return view;
	}

	
	public void setView(Matrix4f mat)
	{
		
		view = new Matrix4f(mat);
		
	}
	public Matrix4f getView()
	{
		Matrix4f ret = new Matrix4f(view);
		
		return ret;
		
	}

	

}

package deferred;

import org.lwjgl.util.vector.Vector3f;


/*
 * Klasse für Lichter
 */
public class Light{
	
	
	public Light(){
		
		
	}
	
	public Vector3f ambient;
	public Vector3f diff;
	

	private Vector3f pos;
	public Vector3f specular; 
	public float attenQuad; 
	public float attenLin;
	public float attenConst;
	public float distance;
	public float proz;
	public Vector3f delta = new Vector3f(0,0,0);
	
	
	public void setPos(Vector3f p )
	{
		pos = new Vector3f(p);
		delta = new Vector3f(0,0,0);
	}
	
	public void setX(float x )
	{
		pos.x = x;
		delta.x = 0;
	}
	
	public void setY(float y )
	{
		pos.y = y;
		delta.y = 0;
	}
	
	public void setZ(float z )
	{
		pos.z = z;
		delta.z = 0;
	}
	
	public Vector3f getPos()
	{
		Vector3f res = new Vector3f();
		Vector3f.add(pos, delta, res);
		return res;
	}
	

	
	
	public float calcDistanceFromAtten()
	{
		
		if(attenQuad > 0 && attenLin > 0)
		{
			distance = (float)(-(attenLin /(2 * attenQuad)) + Math.sqrt(Math.pow(attenLin/(2 * attenQuad),2) - ((attenConst - proz)/attenQuad)));
		}
		
		else if (attenLin > 0)
		{
			distance = (proz - attenConst) / attenLin;
		}
		
		else if (attenQuad > 0)
		{
			distance = (float)(Math.sqrt((proz - attenConst) / attenQuad));
			
		}
		
		else
		{
			distance = 0;
			return 1;
		}
		
		distance *= 1f;
		
		return distance;
		
	}
	
	public void setDist(float dist)
	{
		
		
		distance = dist;
		
		attenConst = 1;
		attenLin = 0;
		
		attenQuad = (proz - attenConst)/(distance * distance);
		
		
	}
	
	
	
	
	
	
}

package deferred;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.opengl.Texture;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
/*
 * 
 * Model-Klasse mit allen Informationen, die aus den OBJ-Files gelesen wurden
 * 
 * 
 */
public class Model {

	public String name;
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texcoord = new ArrayList<Vector2f>();
	public List<Face> faces = new ArrayList<Face>();
	
	public int difTexture = -1;
	public int ambTexture = -1;
	public int dTexture = -1;
	public int specTexture = -1;
	
	public String sdifTexture;
	public String sambTexture;
	public String sspecTexture;
	public String sdTexture;
	
	public Vector3f Ka = new Vector3f(1,1,1);
	public Vector3f Kd= new Vector3f(1,1,1);
	public Vector3f Ks= new Vector3f(1,1,1);
	public Vector3f Ke= new Vector3f(1,1,1);
	
	
	
	
	
	public int count;
	public int vao;
	public Float Ns;
	public Model()
	{
		
		
		
		
		
		
	}
	
	public Model(Model m)
	{
		
		name = new String(m.name);
		
		for(Vector3f v : m.vertices)
		{
			vertices.add(new Vector3f(v));
		}
		
		for(Vector3f v : m.normals)
		{
			normals.add(new Vector3f(v));
		}
		
		for(Vector2f v : m.texcoord)
		{
			texcoord.add(new Vector2f(v));
		}
		
		for(Face f: m.faces)
		{
			faces.add(new Face(f));
			
		}
		
		difTexture = m.difTexture;
		ambTexture = m.ambTexture;
		dTexture = m.difTexture;
		specTexture = m.specTexture;
		
		sdifTexture = m.sdifTexture != null ? new String(m.sdifTexture) : "";
		
		sambTexture = m.sambTexture != null ? new String(m.sambTexture) : "";
		
		sdTexture = m.sdTexture != null ? new String(m.sdTexture) : "";
		
		sspecTexture = m.sspecTexture != null ? new String(m.sspecTexture): "";
		
		Ka = new Vector3f(m.Ka);
		Kd = new Vector3f(m.Kd);
		Ks = new Vector3f(m.Ks);
		Ke = new Vector3f(m.Ke);
		
		count = new Integer(m.count);
		
		vao = new Integer(m.vao);
		
		if(m.Ns != (Float) null)
		{
			Ns = new Float(m.Ns);
		}
		
		
		
		
		
	}
	
	
}

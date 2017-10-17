package deferred;


import org.lwjgl.util.vector.Vector4f;
/*
 * OBJ-Files haben sogenannte Faces, dies ist eine Klasse dazu, um im Modell die Infos abspeichern zu können
 */
public class Face {
	
	public Vector4f vertex = new Vector4f(); //drei Indizes, nicht die Vertize/Normalen selber
	public Vector4f normal = new Vector4f();
	public Vector4f texcoord = new Vector4f();
	public boolean hasTex = false;
	public int type = 1;       //1 = triangle 2 = Quads
	public Face(Vector4f vertex, Vector4f normal, int Itype)
	{
		this.vertex = vertex;
		this.normal = normal;
		type = Itype;
		
	}
	public Face(Vector4f vertex, Vector4f normal, Vector4f texcoord, int Itype)
	{
		this.vertex = vertex;
		this.normal = normal;
		this.texcoord = texcoord;
		hasTex = true;
		type = Itype;
	}
	
	
	public Face(Face f)
	{
		vertex = new Vector4f(f.vertex);
		normal = new Vector4f(f.normal);
		texcoord = new Vector4f(f.texcoord);
		hasTex = new Boolean(f.hasTex);
		type = new Integer(f.type);
		
		
		
	}

}

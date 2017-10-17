package deferred;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import org.newdawn.slick.opengl.Texture;

/*
 * Klasse für "Szenen",  besteht aus mehreren Modellen und Texturen
 */
public class Scene {
	
	
	public List<Model> Models = new ArrayList<Model>();
	public List<Texture> Textures = new ArrayList<Texture>();
	public List<String> Texturenames = new ArrayList<String>();
	public Scene()
	{
		
		
		
	}
	
	

}

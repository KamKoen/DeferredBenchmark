package deferred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/*
 * 
 * Klasse zum Laden von OBJ-Files, funktioniert mindestens mit den Files in der Arbeit, andere 
 * OBJ-Files wurden nicht getestet
 * 
 */

public class OBJLoader {

	public static Scene loadScene(InputStream is, Main am) throws NumberFormatException, IOException
	{
		Scene scene = new Scene();
		int countV = 0;
		int defcountV = 0;
		int countN = 0;
		int defcountN = 0;
		int countT = 0;
		int defcountT = 0;
		
		int failsafecount = 0;
		
		boolean hasTex = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		Model m = new Model();
		String mtllib = "";
		
		
		
		
		
		
		
		String line;
		boolean firstObject = true;
		while ((line = reader.readLine()) != null){
		
			if(line.contains("mtllib"))
			{
			
				mtllib = line.split(" ")[1];
				
				
			}
			
			if(line.contains("usemtl"))
			{
				String mtlName = line.split(" ")[1];
				BufferedReader mtlRead = new BufferedReader(new InputStreamReader(am.getClass().getResourceAsStream(mtllib),"UTF-8"));
				
				String line2;
				while((line2 = mtlRead.readLine()) != null)
				{
					if(line2.contains(mtlName))
					{
						
						
						
						while((line2 = mtlRead.readLine()) != null)
						{
							
							if(line2.startsWith("\tNs"))
							{
								m.Ns = Float.valueOf(line2.split(" ")[1]);
							}
							else if(line2.startsWith("\tKa"))
							{	
								String[] split = line2.split(" ");
								m.Ka = new Vector3f(
										Float.valueOf(split[1]),
										Float.valueOf(split[2]),
										Float.valueOf(split[3])
										);
							}
							else if(line2.startsWith("\tKd"))
							{
								String[] split = line2.split(" ");
								m.Kd = new Vector3f(
										Float.valueOf(split[1]),
										Float.valueOf(split[2]),
										Float.valueOf(split[3])
										);
								
							}
							else if(line2.startsWith("\tKs"))
							{
								String[] split = line2.split(" ");
								m.Ks = new Vector3f(
										Float.valueOf(split[1]),
										Float.valueOf(split[2]),
										Float.valueOf(split[3])
										);
							}
							else if(line2.startsWith("\tKe"))
							{
								String[] split = line2.split(" ");
								m.Ke = new Vector3f(
										Float.valueOf(split[1]),
										Float.valueOf(split[2]),
										Float.valueOf(split[3])
										);
							}
							else if(line2.startsWith("\tmap_Ka"))
							{
								m.sambTexture = line2.split(" ")[1];
								
								boolean found = false;
								for(String s : scene.Texturenames)
								{
									if(s.equals(m.sambTexture))
									{
										found = true;
										break;
									}
								}
								
								if(!found) scene.Texturenames.add(m.sambTexture);
								
							}
							else if(line2.startsWith("\tmap_Kd"))
							{
								m.sdifTexture = line2.split(" ")[1];
								System.out.println(m.sdifTexture);
								boolean found = false;
								for(String s : scene.Texturenames)
								{
									if(s.equals(m.sdifTexture))
									{
										found = true;
										break;
									}
								}
								
								if(!found) scene.Texturenames.add(m.sdifTexture);
								
							}
							else if(line2.startsWith("\tmap_Ks"))
							{
								
								m.sspecTexture = line2.split(" ")[1];
								System.out.println(m.sspecTexture);
								boolean found = false;
								for(String s: scene.Texturenames)
								{
									
									if(s.equals(m.sspecTexture))
									{
										found = true;
										break;
										
									}
									
								}
								
								if(!found) scene.Texturenames.add(m.sspecTexture);
								
							}
							else if(line2.startsWith("bump"))
							{
								
							}
							else if(line2.contains("newmtl"))break;
							
							
							
						}
						
						
					}
				}
				
			}
			
			else if(line.contains("object"))
			{
				if(!firstObject)
				{
					
					if(failsafecount > 0) scene.Models.add(new Model(m));
					
					failsafecount = 0;
					
					m = new Model();
					
					defcountV = countV;
					defcountN = countN;
					defcountT = countT;
					
					
					
				}
				else
				{
					firstObject = false;
				}
				m.name = line.split(" ")[2];
				System.out.println(m.name + " parsed");
				
			}
			
			
			
			
			
			
			
			if(line.startsWith("v  ")) {
				
				float x = Float.valueOf(line.split(" ")[2]);
				float y = Float.valueOf(line.split(" ")[3]);
				float z = Float.valueOf(line.split(" ")[4]);
				m.vertices.add(new Vector3f(x,y,z));
				countV +=1;
				failsafecount += 1;
			}
			else if(line.startsWith("v ")) {
				
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Vector3f(x,y,z));
				countV +=1;
				failsafecount += 1;
			}
			else if (line.startsWith("vn ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Vector3f(x,y,z));
				countN += 1;
			}
			else if(line.startsWith("vt ")){
				hasTex = true;
				float u = Float.valueOf(line.split(" ")[1]);
				float v = Float.valueOf(line.split(" ")[2]);
				m.texcoord.add(new Vector2f(u,v));
				countT += 1;
			}
			
			else if (line.startsWith("f ")){
				String[] s = line.split(" ");
				Vector4f vertexIndices;
				Vector4f normalIndices;
				Vector4f texCoordIndices;
				switch(s.length)
				{
				
				
				case 4:
					
					vertexIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[0]) - defcountV,
							Float.valueOf(line.split(" ")[2].split("/")[0]) - defcountV,
							Float.valueOf(line.split(" ")[3].split("/")[0]) - defcountV,
							0);
					normalIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[2]) -defcountN,
							Float.valueOf(line.split(" ")[2].split("/")[2]) -defcountN,
							Float.valueOf(line.split(" ")[3].split("/")[2]) -defcountN,
							0);
					
					if(!line.split(" ")[1].split("/")[1].isEmpty())
					{
						texCoordIndices = new Vector4f(
								Float.valueOf(line.split(" ")[1].split("/")[1])-defcountT,
								Float.valueOf(line.split(" ")[2].split("/")[1])-defcountT,
								Float.valueOf(line.split(" ")[3].split("/")[1])-defcountT,
								0
								);
						
						
						m.faces.add(new Face(vertexIndices, normalIndices, texCoordIndices,1));
					}
					else
					{
						m.faces.add(new Face(vertexIndices, normalIndices,1));
					}
					break;
					
					
				case 5:
					
					vertexIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[0])-defcountV,
							Float.valueOf(line.split(" ")[2].split("/")[0])-defcountV,
							Float.valueOf(line.split(" ")[3].split("/")[0])-defcountV,
							Float.valueOf(line.split(" ")[4].split("/")[0])-defcountV);
					normalIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[2])-defcountN,
							Float.valueOf(line.split(" ")[2].split("/")[2])-defcountN,
							Float.valueOf(line.split(" ")[3].split("/")[2])-defcountN,
							Float.valueOf(line.split(" ")[4].split("/")[2])-defcountN);
					
					if(!line.split(" ")[1].split("/")[1].isEmpty())
					{
						texCoordIndices = new Vector4f(
								Float.valueOf(line.split(" ")[1].split("/")[1])-defcountT,
								Float.valueOf(line.split(" ")[2].split("/")[1])-defcountT,
								Float.valueOf(line.split(" ")[3].split("/")[1])-defcountT,
								Float.valueOf(line.split(" ")[4].split("/")[1])-defcountT
								);
						
						
						m.faces.add(new Face(vertexIndices, normalIndices, texCoordIndices,2));
					}
					else
					{
						m.faces.add(new Face(vertexIndices, normalIndices,2));
					}
					
					
					
					
				
				
				}
			}
			
			
			
		}
		
		System.out.println("closing scene");
		reader.close();
		
		return scene;
		
		
		
		
	}
	
	public static Model loadModel(InputStream is) throws FileNotFoundException, IOException{
		boolean hasTex = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		Model m = new Model();
		String line;
		while ((line = reader.readLine()) != null){
			if(line.startsWith("v  ")) {
				
				float x = Float.valueOf(line.split(" ")[2]);
				float y = Float.valueOf(line.split(" ")[3]);
				float z = Float.valueOf(line.split(" ")[4]);
				m.vertices.add(new Vector3f(x,y,z));
			}
			else if(line.startsWith("v ")) {
				
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Vector3f(x,y,z));
			}
			else if (line.startsWith("vn ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Vector3f(x,y,z));
			}
			else if(line.startsWith("vt ")){
				hasTex = true;
				float u = Float.valueOf(line.split(" ")[1]);
				float v = Float.valueOf(line.split(" ")[2]);
				m.texcoord.add(new Vector2f(u,v));
			}
			
			else if (line.startsWith("f ")){
				String[] s = line.split(" ");
				Vector4f vertexIndices;
				Vector4f normalIndices;
				Vector4f texCoordIndices;
				switch(s.length)
				{
				
				
				case 4:
					
					vertexIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[0]),
							Float.valueOf(line.split(" ")[2].split("/")[0]),
							Float.valueOf(line.split(" ")[3].split("/")[0]),
							0);
					normalIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[2]),
							Float.valueOf(line.split(" ")[2].split("/")[2]),
							Float.valueOf(line.split(" ")[3].split("/")[2]),
							0);
					
					if(!line.split(" ")[1].split("/")[1].isEmpty())
					{
						texCoordIndices = new Vector4f(
								Float.valueOf(line.split(" ")[1].split("/")[1]),
								Float.valueOf(line.split(" ")[2].split("/")[1]),
								Float.valueOf(line.split(" ")[3].split("/")[1]),
								0
								);
						
						
						m.faces.add(new Face(vertexIndices, normalIndices, texCoordIndices,1));
					}
					else
					{
						m.faces.add(new Face(vertexIndices, normalIndices,1));
					}
					break;
					
					
				case 5:
					
					vertexIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[0]),
							Float.valueOf(line.split(" ")[2].split("/")[0]),
							Float.valueOf(line.split(" ")[3].split("/")[0]),
							Float.valueOf(line.split(" ")[4].split("/")[0]));
					normalIndices = new Vector4f(
							Float.valueOf(line.split(" ")[1].split("/")[2]),
							Float.valueOf(line.split(" ")[2].split("/")[2]),
							Float.valueOf(line.split(" ")[3].split("/")[2]),
							Float.valueOf(line.split(" ")[4].split("/")[2]));
					
					if(!line.split(" ")[1].split("/")[1].isEmpty())
					{
						texCoordIndices = new Vector4f(
								Float.valueOf(line.split(" ")[1].split("/")[1]),
								Float.valueOf(line.split(" ")[2].split("/")[1]),
								Float.valueOf(line.split(" ")[3].split("/")[1]),
								Float.valueOf(line.split(" ")[4].split("/")[1])
								);
						
						
						m.faces.add(new Face(vertexIndices, normalIndices, texCoordIndices,2));
					}
					else
					{
						m.faces.add(new Face(vertexIndices, normalIndices,2));
					}
					
					
					
					
				
				
				}
			}
			
			
			
		}
		System.out.println("closing obj");
		reader.close();
		return m;
	}
	
	
	
}

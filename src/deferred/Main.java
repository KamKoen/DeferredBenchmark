package deferred;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS;




import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.input.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;




import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.input.Mouse.*;
import java.applet.Applet;
import java.awt.Canvas;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JList;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import deferred.Main.LightPlacement;
import deferred.Main.BallPlacement;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import deferred.Main.KameraFahrt;
import deferred.ShaderPrepClasses.*;

import java.awt.Component;
import javax.swing.ListModel;
import javax.swing.JToggleButton;







public class Main extends JFrame{

	
	/*
	 * Booleans
	 */
	
	boolean DEFAULTBENCHMARK = true; //Default Benchmark an?
	boolean LIGHTOBJECTTEST = true; //true-> light, false -> object

	
	
	boolean SPONZATRUE = true; //Sponza laden? Falls man das Programm mehrmals starten m�chte, die Sponza
								//aber nicht braucht, sollte man das hier auf "false" stellen, da ansonsten man
								//immer warten muss

	
	
	boolean SPONZATEST = false; // Sponza Benchmark an?
	boolean GL_INIT_DONE = false; //Vorbereitung der OpenGl pipeline, wie auch Objekte geladen, etc. fertig?
	boolean reallyEnd = false; //tats�chlich Programm zu ende gef�hrt, und nicht nur der Thread?
	boolean MOUSEGRAB = false; //Mousebewegung aktivieren f�r die Kamera?
	static boolean showStrings = true;	//String aufm Bildschirm wegen Infos anzeigen?
	boolean show_Lights = false; //kleine Sph�ren an Lichterposition zu Repr�sentation zeigen?
	boolean ZCULLING = false; //ZCulling an?
	boolean TF_EARLYZ = true; // Tile Forward mit Early Z?
	private int STARMODE = 0; //0 = Quadratische normale attenuation, 1 = hellere, alternative attenuation
	boolean BenchMarkTestGO = false; 	//Benchmarktest starten? Wird durch Starten true gesetzt, nach
										//dem Benchmarktest auf false gesetzt

	boolean end = false;//Ende des Threads
	
	
	//Funktioniert wie ein Boolean, nur Objektorientiert benutzt	
	public class Toggle
	{
		
		boolean toggled = false;
		
		
		
		public Toggle()
		{
			
		}
		
		
		public void toggleOn()
		{
			toggled = true;
		}
		
		public void toggleOff()
		{
			toggled = false;
		}
		
		public void toggle()
		{
			
			toggled = !toggled;
		}
		
		public boolean isToggled()
		{
			return toggled;
		}
		
	}
	

	Toggle LightStatsToggle = new Toggle();
	Toggle UpdateShaderToggle = new Toggle();
	Toggle LightPlacementToggle = new Toggle();
	Toggle BallPlacementToggle = new Toggle();
	
	
	
	
	//Ambientenlichtfarbe	
	float AMBSCALE = 0.05f;
	Vector3f AMBIENTCOLOR = new Vector3f(1 * AMBSCALE,1 * AMBSCALE,1 * AMBSCALE);
	

	

	
	/*
	 * Für Licht attenuation
	 * 
	 * 
	 * 
	 * Es gibt zwei auswählbare Dämpfungsfunktionen, die beide über die gleiche Distanz arbeiten
	 * Die erste wird vergleichsweise schneller dunkler und funktioniert über die unteren Werte bei der Farbberechnung.
	 * Die Zweite benutzt nur die aus den unteren Werten berechnete Distanz, wobei dessen 
	 * Dämpfung erst nach einiger Entfernung stark hochzieht
	 * 
	 * 
	 */
	
	float CONSTATTEN = 0f;
	float LINATTEN = 0f;
	float QUADATTEN = 0.005f;
	
	//Prozentangabe des Lichts, d.h. bei  z.b. PROZENT = 200;  endet der Lichtradius bei der ersten Dämpfungsfunktion,
	//wenn der Lichtwert 1/200 des originalen Lichtwerts beträgt
	//die zweite Dämpfungsfunktion benutzt dennoch diesen Wert zur Radienberechnung
	float PROZENT = 200.0f;
	
	
	/*
	 * Max/Min Position für Lichter in Sponza
	 */
	
	float SPONZA_MIN_X = 1285;
	float SPONZA_MAX_X = -1321;
	
	float SPONZA_MIN_Y = 55;
	float SPONZA_MAX_Y = 979;
	
	float SPONZA_MIN_Z = 542;
	float SPONZA_MAX_Z = -630;
	
	
	/*
	 * F�r den Default-Benchmarktest, er l�uft alle drei arrays durch
	 */
	int SPHERE_TEST_ARRAY[] = {
			
			0,
			50,
			100,
			200,
			500,
			750,
			1000	
			
			
			
	};
	
	int LIGHT_TEST_ARRAY[] = {
			
			0,
			10,
			20,
			50,
			100,
			200,
			500,
			750,
			1024	
			
		
			
	};
	
	int RADIUS_TEST_ARRAY[] = {
			
			50,
			100,
			200,
			350,
			500
			
		
			
			
	};
	
	int SPHERE_FIXED = 500;
	
	int LIGHT_FIXED = 500;
	
	int RADIUS_FIXED = 200;
	
	
	//Abst�nde f�r die Lichter- und Sph�renplatzierung
	float SSX = 100.0f;
	float SSY = 53.0f;
	float SSZ = 100.0f;
	
	float STEP = 50.0f;
	
	float XSTEP = 111.0f;
	
	float YSTEP = 55.0f;
	
	float ZSTEP = 111.0f;
	
	float XSTEP3 = 111.0f * 3;
	
	float YSTEP3 = 55.0f * 2;
	
	float ZSTEP3 = 111.0f * 3;
	
	
	int XORDER1 = 10;
	int YORDER1 = 10;
	int ZORDER1 = 10;
	
	int XORDER2 = 32;
	int YORDER2 = 10;
	int ZORDER2 = 32;
	
	int XORDER3 = 10;
	int YORDER3 = 10;
	int ZORDER3 = 10;
	
	float XSTEP2 = 111.0f;
	
	float YSTEP2 = 55.0f;
	
	float ZSTEP2 = 111.0f;
	
	//Anzahl Punktlichter - nicht gr��er als 1024
	int MAX_PLIGHT = 1024;
	//Anzahl Punktlichter in Sponza
	int LIGHT_SPONZA_FIXED = 100;
	//Anzahl Sph�ren
	int SPHERE_COUNT = 1000;
	
	//Einfach nur ein skalar f�r maximale Anzahl an Sph�ren
	int SPHERE_COUNT_SCALE = 10;
	int SPHERE_MAX = SPHERE_COUNT * SPHERE_COUNT_SCALE;
	float SPHERE_SPEC[] = new float[SPHERE_MAX];
	
	
	//Radiengr��en f�r Sponzatest
	float MINRADIUS = 100f;
	float MAXRADIUS = 800f;
	

	float FIZZLE = 1.1f; //da die benutzte Sph�re nicht perfekt rund ist
						 //wird hier ein Wert benutzt der die Gr��e der benutzten Lichtervolumen-Sph�re 
						//minimal erh�ht, sodass die theoretische Gr��e des Lichtes mit der Sph�re 
						//gr��tenteils �bereinstimmt
	
	//Maximale/Minimal y-wert bei der Box f�r Lichter
	float ROOF = 600;
	
	float GROUND = -230;
	
	
	
	Vector3f SPHERE_START_POS1 = new Vector3f(-450,12,-450);
	Vector3f STARTPOS1 = new Vector3f(-500,0,-500);
	Vector3f STARTPOS2 = new Vector3f(-500,0,-500);
	Vector3f STARTPOS3 = new Vector3f(-1500,0,-1500);
	Vector3f LOOK_AT_POSITION = new Vector3f(0,0,1);
	Vector3f LOOK_IN_DIR = new Vector3f(0,0,1);
	
	
	
	/*
	 * 
	 * Enums
	 * 
	 * 
	 */
	
	

	
	
	
	public enum Shader{
		EarlyZ,
		DefRenTest,
		DefRen,
		DefSphereCul,
		DefSphereStencil,
		TiledDefPar,
		TiledForwardPar,
		PerPixel
		
		
	};
	
	public enum CLA //camera look at 
	{
		Position,
		Direction,
		MovingDir
	}
	

	CLA CAMERA_LOOK_AT = CLA.Position;
	
	
	public enum LightPlacement
	{
		
		Random,
		OrderedAsCube,
		OrderedOnPlane,
		OrderedAsCubeCrytek
		
		
		
	}
	
	
	public enum LightMovement
	{
		Still,
		InCircle,
		InCircleUpDown,
		AroundAPoint
		
	}
	
	LightMovement LM = LightMovement.Still;
	
	public enum TestScenes
	{
		SpheresInBox,
		SpheresOnPlane,
		CrytekBuilding
		
	}
	
	TestScenes TS = TestScenes.SpheresInBox;
	
	LightPlacement LP = LightPlacement.Random;
	
	
	public enum BallPlacement
	{
		
		Random,
		OrderedAsCube,
		OrderedOnPlane
		
	}
	
	public enum KameraFahrt
	{
		
		Box,
		PlaneAll,
		PlaneSide,
		PlaneAbove,
		CrytekBuildInside
		
	}
	
	KameraFahrt KF = KameraFahrt.Box;
	
	BallPlacement BP = BallPlacement.Random;
	
	
	
	
	/*
	 * END Enums
	 * 
	 * 
	 */
	
	
	

	
	
	
	float BENCHMARKLENGTH = 15.0f; //L�nge der Benchmarktests

	//Knotenlisten f�r Kamerafahrt
	/*
	
	Vector3f KLIST[] = 
		{
			new Vector3f(0,250,-1000),
			
			new Vector3f(-1000,250, 0),
			
			new Vector3f(0,250,1000),
			
			new Vector3f(1000,250,0),
			
			new Vector3f(0,250,-1000),
			
			new Vector3f(0,250,1000)
		};


	 */
	
	
	/*
	Vector3f KLIST[] = 
		{
			new Vector3f(0,250,-1000),
			
			new Vector3f(0,400, 200),
			
			new Vector3f(200,100,-200),
			
			new Vector3f(-300,250,0),
			
			
			
			new Vector3f(300,250,0),
			
			
			new Vector3f(100,200,-400),
			
			new Vector3f(100,200,300)
		};
	*/
	
	
	Vector3f KLIST[] = 
		{
			new Vector3f(0,250,-1000),
			
			new Vector3f(-819.17804f, 269.7582f, -789.079f),
			
			new Vector3f(-1074.2115f, 269.7582f, -34.336494f),
			
			new Vector3f(-857.0774f, 275.8431f, 1050.8276f),
			
			
			
			new Vector3f(-30.374489f, 273.47147f, 1128.8185f),
			
			
			new Vector3f(830.81934f, 274.21558f, 926.829f),
			
			new Vector3f(1219.6528f, 274.21558f, -6.783511f),
			new Vector3f(104.66288f, 87.5007f, -901.5161f),
			new Vector3f(-642.8075f, 83.52948f, -649.8948f),
			new Vector3f(-896.699f, 83.52948f, 67.671715f)
		};
	
	Vector3f PLANEALL[] = 
		{
			new Vector3f(0,750,-1000),
			
			new Vector3f(2000,100,2000),
			
			new Vector3f(250,100,1200),
			
			new Vector3f(1500,20,2000),
			
			new Vector3f(700,10,500),
			
			new Vector3f(2000,-100,3000),
			
			new Vector3f(3000,-100,0),
			
			new Vector3f(1000,-150,1000),
			
			new Vector3f(2000,-150,3000),

			new Vector3f(3000,-150,0),
			
			new Vector3f(1000,-150,1000)
			
			};
	
	
	Vector3f PLANESIDE0[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,-236),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE50[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,-236),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE100[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,-236),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE200[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,61),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE500[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,879),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE750[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,1792),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	Vector3f PLANESIDE1000[] = 
		{
			
			new Vector3f(-827f,-80f,-268f),
			new Vector3f(-827f,-80,2426),
			new Vector3f(-827f,-80f,-268f)
			
			
		};
	
	
	Vector3f PLANEABOVE[] = 
		{
			new Vector3f(527,1189,167),
			
			new Vector3f(1557,1189,167),
			
			new Vector3f(527,1189,167)
			
			
			};
	
	Vector3f CRYTEKINSIDE[] =
		{
			new Vector3f(1103.65f,162.193f,-461),
			
			new Vector3f(21,155,-460),
			
			new Vector3f(-1143,200,-452),
			
			new Vector3f(-1202,190,-49),
			
			new Vector3f(-598,258,-26),
			
			new Vector3f(-143.336f, 370.40485f, -21.463144f),
			
			new Vector3f(355.17264f, 511.5658f, 6.534431f),
			
			new Vector3f(897.9064f, 595.76935f, 60.510387f),
			
			new Vector3f(814.5116f, 627.4438f, 379.59625f),

			new Vector3f(161.47644f, 609.6412f, 365.62714f),
			
			new Vector3f(-898.55054f, 608.1263f, 361.34946f),
			
			new Vector3f(-1269.513f, 609.88184f, 242.40497f),
			
			new Vector3f(-1026.7842f, 599.7263f, -129.14871f),
			
			new Vector3f(-433.62393f, 383.69513f, -78.173386f),
			
			new Vector3f(173.68277f, 171.42809f, -52.803856f),
			
			new Vector3f(524.9424f, 355.01904f, -55.536755f),
			
			
			new Vector3f(907.89215f, 213.86232f, -28.456886f),
			
			new Vector3f(1032.2803f, 248.93036f, 362.85864f),
			
			new Vector3f(659.51953f, 227.47792f, 393.12363f),
			
			new Vector3f(85.78265f, 236.65677f, 429.2741f),

			new Vector3f(-29.589115f, 308.91187f, 260.4461f),
			
			new Vector3f(-68.56703f, 364.3352f, 82.83789f)
			
			
			
			
			
			
		};
	
	//maximale FPS-Rate
	int syncrate = 1000;
	//Bildschirmgr��e
	int width = 1600;
	int height = 900;
	
	//kleinste Tilegr��e, d.h. letzte Unterteilung
	int tileWidth = 20;
	int tileHeight = 25;
	//�nderung am tileWidth /tileHeight
	//wert m�ssen auch in ComputeZ.comp bei den
	//#define Werten wegen Paralellit�t ge�ndert werden
	
	//Erster Unterteilung, falls benutzt
	int tileWidthSub = 320;
	int tileHeightSub = 150;
	
	//Erste/Zweite Unterteilung, falls benutzt
	int tileWidthSub2 = 80;
	int tileHeightSub2 = 50;

//	int tileWidthSub2 = 320;
//	int tileHeightSub2 = 150;

	
	//Unterteilungsreihe f�r vier Unterteilungen hintereinander
	int tileTestW0 = 320;
	int tileTestH0 = 300;
	
	int tileTestW1 = 160;
	int tileTestH1 = 150;
	
	int tileTestW2 = 80;
	int tileTestH2 = 50;
	
	int tileTestW3 = 20;
	int tileTestH3 = 25;
	
	//Display mit OpenGl f�r die Gui
	Canvas display_parent;
	
	//Thread gameThread;
	
	int GUIWIDTH = 50;
	
	
	JButton button = new JButton("Exit");
	JPanel guiP = new JPanel();
	
	//Listen/Infors f�r Gui-Benutzung

	int NUMBER = 0;
	
	List<Integer> NUMBERLIST = new ArrayList<Integer>();
	int frameCount = 0;
	List <TiledModeEnum> TMEList = new ArrayList<TiledModeEnum>();
	List <Boolean> zCulList = new ArrayList<Boolean>();
	List <Boolean> EAZList = new ArrayList<Boolean>();
	
	//Der Thread des OpenGl-Programms, ausgef�hrt parallel zur Gui
	Thread gameThread = new Thread()
	{
		
		public void run()
		{
			THREADFUNC();
			
		}
				
	};
	
	

	File BenchFileString = new File("defaultFile.txt");

	String BenchmarkString = "";


	
	
	//MillisekundenListe f�r Benchmarktest
	List<Float> MilList = new ArrayList<Float>();
	
	//QueryBufferIndex im Array, nicht tats�chliche IDs
	int queryBackBuffer = 0;
	int queryFrontBuffer = 1;
	int queryID[] = new int[2];

	
	
	//Liste f�r benutzte Algorithmen, gespeichert als Strings
	List <String> BenchMarkTest = new ArrayList<String>();
	
	
	//default Shininess wert
	float matShin = 12.0f;
	
	
	
	
	
	
	//Falls man maximale Anzahl an Lichter pro Tile benutzen m�chte, generell nicht zu empfehlen
	int maxLightsPerTile = MAX_PLIGHT;
	
	
	
	float fov = 50.0f;
	float zNear = 1f;
	float zFar = 5000f;
	
	Camera camera;
	
	
	
	
	
	
	
	
	/*
	 * 
	 * Die Modelle
	 * 
	 * 
	 */
	
	Model sphere = null;
	Model plane = null;
	Model lightsphere = null;
	
	//OBJ LOAD SCALES
	float SPHERESCALE = 1.0f;
	float PLANESCALE = 100.0f;
	
	
	//MODELSPACESCALES
	
	public Light abcT = new Light();
	float COL_SCALE = 1.0f;
	float LIGHT_SIZE = 1f;
	float SPHERE_SIZE = 20.0f;
	
	
	/*
	 * VAOs und VBOs der Modelle
	 */
	
	int vaoSphereID;
	int vboVertexID;
	int vboColorID;
	int vaoID;
	int vboNormalID;
	int vboSphereVertexID;
	int vboSphereColorID;
	int vboSphereNormalID;
	int vboSphereTexID;
	int vboSphereTanID;
	
	int vaoLSID;
	int vboLSID;
	
	
	int vaoTestID;
	int vboTestVertID;
	int vboTestTexID;
	
	
	int vaoPlaneID;
	int vboPlaneVertID;
	int vboPlaneColID;
	int vboPlaneNorID;
	
	
	/*
	 * Pixelkoordinaten f�r den "Test"-Shader mit den vier abgebildeten Texturen des Deferred Renderers
	 */
	
	float testOnScreenX = 0;
	float testOnScreenY = 0;
	float testOnScreenWidth = width/2;
	float testOnScreenHeight = height/2;
	
	
	//render to texture
	
	FloatBuffer g_FloatB = BufferUtils.createFloatBuffer(3);
	FloatBuffer g_FloatLightAllB = BufferUtils.createFloatBuffer(16);
	
	
	int frameBuffer;

	/*
	 * Texturen-ID
	 */
	
	int renderedTexture;
	int depthTexture; // FBO Tiefentextur
	int defDepthTexture; //Deferred Tiefentextur
	
	
	int depthRenderBuffer;
	
	
	int indexTexture;
	int infoTexture;
	
	int textureSampler;
	int depthSampler;
	
	IntBuffer drawBuffs;
	
	float MAX_SPEED = 10.0f;
	float speed = 1.0f;
	float MIN_SPEED = 0.25f;
	float amount = 0.05f;
	
	Vector3f planePos = new Vector3f(0,-1,0);
	
	Vector3f[] SPHERE_POS = new Vector3f[SPHERE_COUNT*SPHERE_COUNT_SCALE];
	Vector3f[] SPHERE_FAR_POS = new Vector3f[SPHERE_COUNT*SPHERE_COUNT_SCALE];

	
	Vector3f[] SPHERE_COL = new Vector3f[SPHERE_COUNT*SPHERE_COUNT_SCALE];
	
	int SPHERE_POS_MIN = -450;
	int SPHERE_POS_MAX = SPHERE_POS_MIN * -2;
	
	int SPHERE_FAR_POS_MIN = -1500;
	int SPHERE_FAR_POS_MAX = SPHERE_POS_MIN * -2;
	
	int SPHERE_FAR_POS_Z_MIN = 0;
	int SPHERE_FAR_POS_Z_MAX = 1500;
	
	int SPHERE_POS_Z_MIN = 0;
	int SPHERE_POS_Z_MAX = 500;
	
	Vector3f pLight_TEST = new Vector3f(1,1,1);
	
	
	Light pLightA[] = new Light[MAX_PLIGHT];
	Light ambientLight = new Light();
	
	Vector3f WORLD_AMBIENT = new Vector3f(0,0,0);//0.005f,0.005f,0.01f);
	
	long lastFrame;
	private int mouseDx;
	private int mouseDy;
	private int mouseX;
	private int mousePrevx;
	private int mouseY;
	private int mousePrevy;
	
	float FRAME_SECOND = 0;
	
	Vector3f directionTest = new Vector3f(0,-1,1);
	Vector3f sphereTest = new Vector3f(10,0,0);
	
	private int vaoFSQuadID;
	private int vboFSQVertID;
	private int vboFSQTexID;
	private int deferredRenFBO;
	private int diffuseTexture;
	private int normalTexture;
	int coordTexture;
	
	private IntBuffer drawdefBuffs;
	
	float shaderSwitchSecond = 0.0f;
	private int pLightBufferID;
	private FloatBuffer pLData;
	private int lightTexture;
	
	
	/*
	 * 
	 * ALGOS
	 * 
	 */
	ShaderProgram displayStringShader; //wichtig für Text aufm Bildschirm
	PerFragmentForward PFF;
	Deferred DF;
	DeferredSphere DFSP;
	TiledDeferredPar TDP;
	TiledForwardPar TFP;
	Cam CamAlgo;
	DefRenTest DRT;
	EarlyZPFF EAZ;
	DeferredSphereStencil DSS;
	
	
	
	
	
	
	
	
	float SCALE = 0.5f;
	
	int mode = 0;
	
	int MAX_MODE_COUNT = 2;
	
	Shader shade = Shader.DefRen;
	
	
	
	
	
	
	
	int infoSSBO;
	int indexSSBO;
	int infoSubSSBO;
	int indexSubSSBO;
	
	
	int infoSSBOTest;
	int indexSSBOTest;
	int infoSSBOTest2;
	int indexSSBOTest2;
	
	IntBuffer infBTest;
	IntBuffer indBTest;
	IntBuffer infBTest2;
	IntBuffer indBTest2;
	
	
	ByteBuffer byteZero;
	ByteBuffer byteZ1;
	ByteBuffer byteZ2;
	
	IntBuffer infB;
	IntBuffer indB;
	IntBuffer infSubB;
	IntBuffer indSubB;
	int indexSub2SSBO;
	IntBuffer indSubB2;
	IntBuffer infSubB2;
	int infoSub2SSBO;
	
	FloatBuffer zMinMaxB;

	
	

	int bindingPointzMinMax = 1;
	int bindingPointInfB = 4;
	int bindingPointIndB = 5;
	int bindingPointInfSubB = 6;
	int bindingPointIndSubB = 7;
	int bindingPointInfSubB2 = 2;
	int bindingPointIndSubB2 = 3;
	int zMinMaxBuf;
	
	
	
	
	TrueTypeFont font;
	
	

	float LOCKTIME = 250.0f;
	
	
	
	Lock lock250 = new Lock(LOCKTIME);
	
	
	Lock[] lockA = {lock250};
	
	
	int TiledMode = 0;
	
	public enum TiledModeEnum 
		{
		
		OneSubdivision,
		TwoSubdivisons_1,
		TwoSubdivisons_2,
		ThreeSubdivisions,
		FourSubdivisions		
		
		
		};
		
	TiledModeEnum TME = TiledModeEnum.OneSubdivision;
	int RasterMode = 0;
	int MAX_TILED_MODE = 4;
	String TiledModeA[] = {
		
			"Nur TileWidth/Height",
			"TileWidth/Height und TileWidthSub",
			"TileWidth/Height und TileWidthSub2",
			"TileWidth/Height und TileWidthSub + TileWidthSub2",
			"TileWidth/Height mit drei Unterteilungen"
			
	};
	
	

	
	
	float fElapsedTime = 0;

	
	int sponzaVAO;
	
	Scene sponzaScene;
	Model sponza = new Model();
	
	   /*
	    * Normalmap-Textur
		* 
		* 
		* Originales Bild dazu:
		* http://www.texturex.com/albums/Stone-Textures/TextureX%20Stone%20Pebble%20Rocky%20Beach%20Grey%20Texture.jpg
		* bearbeitet mit CrazyBump:
		* http://www.crazybump.com/
		* um eine Normalmap zu bekommen
		*
		*
		*/
	Texture sphNor; 
	
	
	int StencilB;
	int seperateDepthTexture;
	int fbo2;
	float passed = 0;
	float CircleConst = 250.0f;
	int NORMALNUM = 1;
	
	
	
	
	
	
	
	
	
	
	public Main()
	{
		
		System.setProperty("org.lwjgl.librarypath", new File("lib"+File.separator+"lwjgl-2.9.1"+File.separator+"all_native").getAbsolutePath());
		
		setMinimumSize(new Dimension(1900, 950));
		getContentPane().setMinimumSize(new Dimension(1000, 938));
		init();
	}
	
	
	public static void main(String args[])
	{
		
		
		Main am = new Main();
		
	}
	
	
	
	/*
	 * Falls ende, schlie�e Fenster
	 * 
	 */
	public void endP(){
		if(reallyEnd) dispatchEvent(new WindowEvent(this, Event.WINDOW_DESTROY));
		
	}
	
	public void updateShader()
	{
		PFF.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR);
        DF.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR);
        DFSP.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR, width, height);
        DSS.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR, width, height);
        TDP.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR);
        TFP.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        CamAlgo.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        DRT.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        EAZ.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);

        setUpTileInts(TDP);
        setUpTileInts(TFP);
		
	}
	
	/*
	 * 
	 * 
	 * 
	 * Der Thread mit der OpenGl-Anwendung
	 * 
	 * 
	 * 
	 */
	public void THREADFUNC()
	{
		
		
		
		
		try {
			
			
			Display.setParent(display_parent);
			
	        Display.setDisplayMode(new DisplayMode(width, height));
	        
		
			Display.create(new PixelFormat().withDepthBits(24).withStencilBits(8));
			System.out.println(glGetInteger(GL_STENCIL_BITS));
			
			
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,width,0, height,1,-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		
		System.out.println(glGetInteger(GL_STENCIL_BITS));
		
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		glDepthFunc(GL_LEQUAL);
		glDepthRange(0.0f, 1.0f);
	
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		glEnable(GL_LIGHTING);
		
		System.setProperty("org.lwjgl.util.Debug", "true");
		
		
		ByteBuffer ba = ByteBuffer.allocateDirect(128);
		IntBuffer i = ba.asIntBuffer();
		
		GL11.glGetInteger(GL11.GL_STENCIL_BITS, i);
		System.out.print(ba.get());
	
		
		initGL();
		System.out.println(glGetInteger(GL_STENCIL_BITS));
		
		GL_INIT_DONE = true;
		
		lastFrame = getTime();
		
		
		mouseX = Mouse.getX();
		mousePrevx = Mouse.getX();
		
		mouseY = Mouse.getY();
		mousePrevy = Mouse.getY();
		
		
		
		String s = "Hallo";
		String as = "";
		
		long a = 0;
		
		
		while(!Display.isCloseRequested() && !end)
		{
			
			if(LightStatsToggle.isToggled())
			{
				
				LightStatsToggle.toggleOff();
				changeLightStats(CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
				
			}
			
			if(UpdateShaderToggle.isToggled())
			{
				UpdateShaderToggle.toggleOff();
				
				updateShader();
				
			}
			
			if(LightPlacementToggle.isToggled())
			{
				LightPlacementToggle.toggleOff();
				switch(LP)
				{
				
				case Random:
					lightReshuffle();
					updatePosPLightUBO();
					break;
					
				case OrderedAsCube:
					setLightsOrder1(XORDER1, YORDER1, ZORDER1, STARTPOS1);
					break;
					
				case OrderedOnPlane:
					setLightsOrder2(XORDER2, YORDER2, ZORDER2, STARTPOS2);
					break;
				case OrderedAsCubeCrytek:
					setLightsOrder3(XORDER3, YORDER3, ZORDER3, STARTPOS3);
					break;
					
					
				}
			}
			
			if(BallPlacementToggle.isToggled())
			{
				BallPlacementToggle.toggleOff();
				
				switch (BP)
				{
				
				case Random:
					
					spherPosRandom();
					
					break;
					
				case OrderedAsCube:
				
					spherePosCube(10,10,10);
					
					break;
					
				case OrderedOnPlane:
					spherePosPlane(XORDER2, YORDER2, ZORDER2, STARTPOS2, -100.0f);
					break;
					
				}
				
				
			}
			
			
			if (BenchMarkTestGO)
			{
				
				boolean tempZCUL = ZCULLING;
				int tempTM = TiledMode;
				Shader temp = shade;
				startBenchmarkTest();
				BenchMarkTestGO = false;
				shade = temp;
				TiledMode = tempTM;
				ZCULLING = tempZCUL;
			}
			else
			{
				long b = getTimeD();
				render();
				
				float ab = a/100;
				ab = ab/10.0f;
			
				as = "Milliseconds: " + ab;
				displayStringShader.bind();
				drawString("LightCount: " + MAX_PLIGHT,100,130);
				drawString("SphereCount: " + SPHERE_COUNT,100,110);
				drawString(as,100,70);
				drawString(s,100,90);
				drawString(shade.toString(),100,50);
				if(shade == Shader.TiledDefPar || shade == Shader.TiledForwardPar)
				{
					drawString(TiledModeA[TiledMode],300,50);
					drawString("Zculling: " + ZCULLING, 300, 25);
					if(shade == Shader.TiledForwardPar)
					{
						
						drawString("EarlyZ: " + TF_EARLYZ,450,25);
					}
				}
				displayStringShader.unbind();
			
				
				Display.update();
				
				a = getTimeD() -b;
			
				frameCount++;
				
				if(FRAME_SECOND > 1000.0f){
					FRAME_SECOND -= 1000.0f;
				
					s = "Frames Per Second: " + frameCount;
					
					frameCount = 0;
				}
				
			
				Display.sync(syncrate);
				
				if(reSetUp)
				{
					retileTilePrograms();
				}
			
			}
			
		}
		dispose();
		
		
		Display.destroy();
		
		endP();
		
		
	}
	
	
	
	
	public void checkE(boolean a)
	{
		try{
			Util.checkGLError();
		}
		catch(OpenGLException e )
		{
			//if(a)System.out.println(e);
		}
	}
	

	public void startLWJGL(){
		
		
		
		gameThread.start();
		
	}
	
	
	private void stopLWJGL(){
		
		end = true;
		
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void destroy(){
		remove(display_parent);
		super.dispose();
		
		
	}
	
	/*
	 * Erstellt die Swing-Gui, d.h. alle Tasten wie auch deren Funktionsweisen
	 */
	public void createSwingGui(){
		getContentPane().setLayout(null);
		
		display_parent = new Canvas(){
			
			
			
			public final void addNotify(){
				super.addNotify();
				startLWJGL();
				
			}
			public final void removeNotify(){
				stopLWJGL();
				super.removeNotify();
			}
			
		};
		display_parent.setLocation(320, 0);
		display_parent.setMaximumSize(new Dimension(1920, 1080));
		display_parent.setMinimumSize(new Dimension(100, 100));
		
		
		
		
		/*
		 * 
		 * GUI
		 * 
		 */
		
		
		
		
		
		
		
		display_parent.setSize(1600,900);
		
		getContentPane().add(display_parent);
		guiP.setLocation(0, 0);
		guiP.setPreferredSize(new Dimension(320, 900));
		
		guiP.setMinimumSize(new Dimension(220, 900));
		guiP.setMaximumSize(new Dimension(220, 900));
		
		getContentPane().add(guiP);
		button.setBounds(0, 9, 145, 38);
		
		
		button.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	end = true;
	        	reallyEnd = true;
	            
	        }
		});
		
		guiP.setLayout(null);
		guiP.add(button);
		guiP.setSize(320, 900);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 107, 145, 105);
		guiP.add(scrollPane_1);
		
		final JList AlgoList = new JList();
		scrollPane_1.setViewportView(AlgoList);
		AlgoList.setModel(new AbstractListModel() {
			String[] values = new String[] {"PerFragmentForward","EarlyZ_PFF", "Deferred", "DeferredSphere","DeferredWithStencil", "TiledDeferred", "TiledForward"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		
		final DefaultListModel benchLM;
		benchLM = new DefaultListModel();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 289, 145, 150);
		guiP.add(scrollPane);
		
		final JList BenchList = new JList(benchLM);
		scrollPane.setViewportView(BenchList);
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(Main.TiledModeEnum.values()));
		comboBox.setBounds(10, 551, 135, 23);
		
		comboBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				TiledModeEnum a = (TiledModeEnum)((JComboBox)arg0.getSource()).getSelectedItem();
				int i = BenchList.getSelectedIndex();
				TMEList.set(i, a);
			}
			
		});
		
		final JCheckBox chckbxZculling = new JCheckBox("zCulling");
		chckbxZculling.setBounds(10, 581, 73, 23);
		chckbxZculling.setEnabled(false);
		
		
		final JCheckBox chckbxEarlyz = new JCheckBox("EarlyZ");
		chckbxEarlyz.setBounds(85, 581, 61, 23);
		chckbxEarlyz.setEnabled(false);
		chckbxEarlyz.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int i = BenchList.getSelectedIndex();
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					EAZList.set(i, true);
					
					
					chckbxZculling.setEnabled(true);
					
					
				}
				else if(!a.isSelected())
				{
					
					EAZList.set(i, false);
					zCulList.set(i,false);
					
					chckbxZculling.setEnabled(false);
				
				}
				
				
				
			}
			
			
			
		});
		
		
		
		
	
		
		chckbxZculling.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int i = BenchList.getSelectedIndex();
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					zCulList.set(i, true);
					
				}
				else if(!a.isSelected())
				{
					
					zCulList.set(i, false);
					
				
				}
				
				
				
			}
			
			
			
		});
		
		
		
		BenchList.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				String item = (String) ((JList)arg0.getSource()).getSelectedValue();
				int i = ((JList)arg0.getSource()).getSelectedIndex();
				
				
				if(	item == "TiledDeferred" )
				{
					comboBox.setEnabled(true);
					chckbxZculling.setEnabled(true);
					chckbxEarlyz.setEnabled(false);
					
				}
				else if(item == "TiledForward" )
				{
					comboBox.setEnabled(true);
					
					chckbxEarlyz.setEnabled(true);
					
					chckbxZculling.setEnabled(EAZList.get(i));
					
					
					
				}
				else
				{
					comboBox.setEnabled(false);
					chckbxZculling.setEnabled(false);
					chckbxEarlyz.setEnabled(false);
				}
				if(i >= 0)
				{
					chckbxEarlyz.setSelected(EAZList.get(i));
					chckbxZculling.setSelected(zCulList.get(i));
					comboBox.setSelectedItem(TMEList.get(i));
				}
						
				
			}
			
			
			
		});
		
		JButton AddAlgobtn = new JButton("Add Algo");
		AddAlgobtn.setBounds(22, 221, 99, 23);
		guiP.add(AddAlgobtn);
		
		
		AddAlgobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
	           
	        	benchLM.addElement(AlgoList.getSelectedValue());
	        	TiledModeEnum a = TiledModeEnum.OneSubdivision;
	        	TMEList.add(a);
	        	zCulList.add(false);
	        	EAZList.add(false);
	        
	            
	        }
		});
		
		
		JButton DelAlgobtn = new JButton("Del Algo");
		DelAlgobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) 
	        {
	           if(BenchList.getSelectedIndex() >= 0)
	           {
	        	   int i = BenchList.getSelectedIndex();
	        	   benchLM.remove(i);
	        	   TMEList.remove(i);
	        	   zCulList.remove(i);
	        	   EAZList.remove(i);
	           }
	            
	        }
		});
		DelAlgobtn.setBounds(22, 255, 99, 23);
		guiP.add(DelAlgobtn);
		
		final DefaultListModel numLM;
		numLM = new DefaultListModel();
		
		
		JButton StartBenchbtn = new JButton("Start Bench.");
		StartBenchbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) 
	        {
			   BenchMarkTest.clear();
	           for(int i = 0; i < benchLM.size(); i++)
	           {
	        	   BenchMarkTest.add((String) benchLM.get(i));
	           }
	           
	           NUMBERLIST.clear();
	           for(int i = 0; i < numLM.size(); i++)
	           {
	        	   
	        	   NUMBERLIST.add((int) numLM.get(i));
	        	   
	           }
	           
	           
	           BenchMarkTestGO = true;
	           
	           
	        }
		});
		StartBenchbtn.setBounds(0, 450, 145, 46);
		guiP.add(StartBenchbtn);
		
		guiP.add(comboBox);
		
		
		guiP.add(chckbxZculling);
		
		guiP.add(chckbxEarlyz);
		
		
		final JFileChooser fc = new JFileChooser();
		
		
		
		JButton newBenchFile = new JButton("New Bench File");
		newBenchFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				int returnVal = fc.showDialog(guiP, "Bench to File");
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					BenchFileString = fc.getSelectedFile();
				
				
				}
				
				
			}
		});
		newBenchFile.setBounds(0, 58, 145, 38);
		guiP.add(newBenchFile);
		
		final JFormattedTextField Lengthfield = new JFormattedTextField();
		Lengthfield.setValue(new Float(15.0f));
		Lengthfield.setBounds(60, 507, 85, 20);
		
		Lengthfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				BENCHMARKLENGTH = (float)(Lengthfield.getValue());
				System.out.println(BENCHMARKLENGTH);
			}
			
		});

		
		
		guiP.add(Lengthfield);
		
		JLabel lblNewLabel = new JLabel("Length");
		lblNewLabel.setBounds(10, 510, 52, 17);
		guiP.add(lblNewLabel);
		
		JComboBox LightPlacementCombobox = new JComboBox();
		LightPlacementCombobox.setModel(new DefaultComboBoxModel(LightPlacement.values()));
		LightPlacementCombobox.setBounds(10, 642, 135, 23);
		LightPlacementCombobox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				
				LightPlacement a = (LightPlacement)((JComboBox)arg0.getSource()).getSelectedItem();
					
				LP = a;
				
				LightPlacementToggle.toggleOn();	
				
				
			}
			
		});
		guiP.add(LightPlacementCombobox);
		
		JLabel lblLightplace = new JLabel("Light Placement");
		lblLightplace.setBounds(10, 611, 135, 23);
		guiP.add(lblLightplace);
		
		JLabel lblBallplace = new JLabel("Ball Placement");
		lblBallplace.setBounds(10, 671, 135, 20);
		guiP.add(lblBallplace);
		
		JComboBox BallPlacementCombobox = new JComboBox();
		BallPlacementCombobox.setModel(new DefaultComboBoxModel(BallPlacement.values()));
		BallPlacementCombobox.setBounds(10, 691, 135, 20);
		BallPlacementCombobox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				BallPlacement a = (BallPlacement)((JComboBox)arg0.getSource()).getSelectedItem();
				
				BP = a;
				
				BallPlacementToggle.toggleOn();
				
			}
			
		});
		
		guiP.add(BallPlacementCombobox);
		
		
		final JFormattedTextField Radiusfield = new JFormattedTextField();
		Radiusfield.setEditable(true);
		Radiusfield.setBounds(60, 872, 85, 17);
		guiP.add(Radiusfield);
		Radiusfield.setValue(calcRadius());
		Radiusfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				calcfromdis((float)(a.getValue()));
				LightStatsToggle.toggleOn();
				
				
				Radiusfield.setValue(calcRadius());
				
				System.out.println(calcRadius());
			}
			
		});
		
		
		final JFormattedTextField ConstAttenfield = new JFormattedTextField();
		ConstAttenfield.setBounds(60, 747, 85, 20);
		guiP.add(ConstAttenfield);
		
		ConstAttenfield.setValue(new Float(CONSTATTEN));
		
		ConstAttenfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				CONSTATTEN = (float)(a.getValue());
				LightStatsToggle.toggleOn();
				
				
				Radiusfield.setValue(calcRadius());
				
				System.out.println(CONSTATTEN);
			}
			
		});
		
		JFormattedTextField LinearAttenfield = new JFormattedTextField();
		LinearAttenfield.setBounds(60, 778, 85, 20);
		guiP.add(LinearAttenfield);
		LinearAttenfield.setValue(new Float(LINATTEN));
		LinearAttenfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				LINATTEN = (float)(a.getValue());
				LightStatsToggle.toggleOn();
				
				Radiusfield.setValue(calcRadius());
				
				
				System.out.println(LINATTEN);
			}
			
		});
		
		JFormattedTextField QuadrAttenfield = new JFormattedTextField();
		QuadrAttenfield.setBounds(60, 809, 85, 20);
		guiP.add(QuadrAttenfield);
		QuadrAttenfield.setValue(new Float(QUADATTEN));
		QuadrAttenfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				QUADATTEN = (float)(a.getValue());
				LightStatsToggle.toggleOn();
				
				Radiusfield.setValue(calcRadius());
				
				
				System.out.println(QUADATTEN);
			}
			
		});
		
		
		
		JLabel lblLinear = new JLabel("Const");
		lblLinear.setBounds(4, 750, 46, 14);
		guiP.add(lblLinear);
		
		JLabel lblLinear_1 = new JLabel("Linear");
		lblLinear_1.setBounds(4, 781, 46, 14);
		guiP.add(lblLinear_1);
		
		JLabel lblQuad = new JLabel("Quad");
		lblQuad.setBounds(4, 812, 46, 14);
		guiP.add(lblQuad);
		
		JLabel lblRadius = new JLabel("Radius");
		lblRadius.setBounds(4, 872, 46, 14);
		guiP.add(lblRadius);
		
		JLabel lblLightAttenuation = new JLabel("Light Attenuation");
		lblLightAttenuation.setBounds(10, 722, 135, 14);
		guiP.add(lblLightAttenuation);
		
		JFormattedTextField Prozentfield = new JFormattedTextField();
		Prozentfield.setBounds(60, 840, 85, 20);
		guiP.add(Prozentfield);
		Prozentfield.setValue(new Float(PROZENT));
		Prozentfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				PROZENT = (float)(a.getValue());
				LightStatsToggle.toggleOn();
				
				Radiusfield.setValue(calcRadius());
				
				
				System.out.println(PROZENT);
			}
			
		});
		
		
		JLabel lblNewLabel_1 = new JLabel("Prozent");
		lblNewLabel_1.setBounds(4, 843, 46, 14);
		guiP.add(lblNewLabel_1);
		
		final JFormattedTextField LightCountfield = new JFormattedTextField();
		LightCountfield.setBounds(259, 105, 51, 23);
		guiP.add(LightCountfield);
		LightCountfield.setValue(new Integer(MAX_PLIGHT));
		LightCountfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				MAX_PLIGHT = (int)(a.getValue());
				
				
				if(MAX_PLIGHT > 1024)
				{
					LightCountfield.setValue(1024);
					MAX_PLIGHT = 1024;
				}
				
				if(MAX_PLIGHT < 0)
				{
					LightCountfield.setValue(0);
					MAX_PLIGHT = 0;
				}
				
				UpdateShaderToggle.toggleOn();
				
				System.out.println(MAX_PLIGHT);
			}
			
		});
		
		JLabel lblLightcount = new JLabel("Lights");
		lblLightcount.setBounds(155, 109, 64, 14);
		guiP.add(lblLightcount);
		
		final JFormattedTextField SphereCountfield = new JFormattedTextField();
		SphereCountfield.setBounds(259, 139, 51, 20);
		guiP.add(SphereCountfield);
		SphereCountfield.setValue(new Integer(SPHERE_COUNT));
		SphereCountfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				SPHERE_COUNT = (int)(a.getValue());
				
				if(SPHERE_COUNT > SPHERE_MAX)
				{
					SphereCountfield.setValue(SPHERE_MAX);
					SPHERE_COUNT = SPHERE_MAX;
				}
				
				if(SPHERE_COUNT < 0)
				{
					SphereCountfield.setValue(0);
					SPHERE_COUNT = 0;
				}
				
				UpdateShaderToggle.toggleOn();
				System.out.println(SPHERE_COUNT);
			}
			
		});
		
		JLabel lblSpherecount = new JLabel("Spheres");
		lblSpherecount.setBounds(155, 142, 74, 14);
		guiP.add(lblSpherecount);
		
		JFormattedTextField tileWidthfield = new JFormattedTextField();
		tileWidthfield.setBounds(259, 167, 51, 17);
		guiP.add(tileWidthfield);
		tileWidthfield.setValue(new Integer(tileWidth));
		
		tileWidthfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileWidth = (int)(a.getValue());
				
				
				System.out.println(tileWidth);
			
				
			}
			
		});
		
		
		
		
		JFormattedTextField tileHeightfield = new JFormattedTextField();
		tileHeightfield.setBounds(259, 192, 51, 17);
		guiP.add(tileHeightfield);
		tileHeightfield.setValue(new Integer(tileHeight));
		
		tileHeightfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileHeight = (int)(a.getValue());
				
				
				System.out.println(tileHeight);
			//	restart();
				
			}
			
		});
		
		JLabel lblTilewidth = new JLabel("TileWidth");
		lblTilewidth.setBounds(155, 169, 64, 14);
		guiP.add(lblTilewidth);
		
		
		JLabel lblTileheight = new JLabel("TileHeight");
		lblTileheight.setBounds(155, 195, 73, 14);
		guiP.add(lblTileheight);
		
		JFormattedTextField tilewidthsubfield = new JFormattedTextField();
		tilewidthsubfield.setBounds(259, 218, 51, 17);
		guiP.add(tilewidthsubfield);
		tilewidthsubfield.setValue(new Integer(tileWidthSub));
		
		tilewidthsubfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileWidthSub = (int)(a.getValue());
				
				
				System.out.println(tileWidthSub);
		//		restart();
				
			}
			
		});
		
		
		
		JFormattedTextField tileheightsubfield = new JFormattedTextField();
		tileheightsubfield.setBounds(259, 242, 51, 17);
		guiP.add(tileheightsubfield);
		tileheightsubfield.setValue(new Integer(tileHeightSub));
		
		tileheightsubfield.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileHeightSub = (int)(a.getValue());
				
				
				System.out.println(tileHeightSub);
		//		restart();
				
			}
			
		});
		
		JLabel lblTilewidthsub = new JLabel("TileWidthSub");
		lblTilewidthsub.setBounds(155, 220, 94, 14);
		guiP.add(lblTilewidthsub);
		
		JLabel lblTileheightsub = new JLabel("TileHeightSub");
		lblTileheightsub.setBounds(155, 246, 94, 14);
		guiP.add(lblTileheightsub);
		
		JFormattedTextField tilewidthsub2field = new JFormattedTextField();
		tilewidthsub2field.setBounds(259, 274, 51, 17);
		guiP.add(tilewidthsub2field);
		tilewidthsub2field.setValue(new Integer(tileWidthSub2));
		
		tilewidthsub2field.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileWidthSub2 = (int)(a.getValue());
				
				
				System.out.println(tileWidthSub2);
		//		restart();
				
			}
			
		});
		
		
		
		
		
		JFormattedTextField tileheightsub2field = new JFormattedTextField();
		tileheightsub2field.setBounds(259, 300, 51, 17);
		guiP.add(tileheightsub2field);
		tileheightsub2field.setValue(new Integer(tileHeightSub2));
		
		tileheightsub2field.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				tileHeightSub2 = (int)(a.getValue());
				
				
				System.out.println(tileHeightSub2);
			//	restart();
				
			}
			
		});
		
		JLabel lblTilewidthsub_1 = new JLabel("TileWidthSub2");
		lblTilewidthsub_1.setBounds(155, 276, 94, 14);
		guiP.add(lblTilewidthsub_1);
		
		JLabel lblTileheightsub_1 = new JLabel("TileHeightSub2");
		lblTileheightsub_1.setBounds(155, 302, 94, 14);
		guiP.add(lblTileheightsub_1);
		
		
		JButton btnRetile = new JButton("Reset Tiles");
		btnRetile.setBounds(155, 9, 155, 38);
		guiP.add(btnRetile);
		btnRetile.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	retile();
	            
	        }
		});
		
		JFormattedTextField widthField = new JFormattedTextField();
		widthField.setBounds(259, 54, 51, 17);
		guiP.add(widthField);
		widthField.setValue(new Integer(width));
		widthField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				width = (int)(a.getValue());
				
				
				System.out.println(width);
			//	restart();
				
			}
			
		});
		
		
		
		JFormattedTextField heightField = new JFormattedTextField();
		heightField.setBounds(259, 79, 51, 17);
		guiP.add(heightField);
		heightField.setValue(new Integer(height));
		heightField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				height = (int)(a.getValue());
				
				
				System.out.println(height);
			//	restart();
				
			}
			
		});
		
		JLabel widthLabel = new JLabel("Width");
		widthLabel.setBounds(155, 56, 64, 14);
		guiP.add(widthLabel);
		
		JLabel heightLabel = new JLabel("Height");
		heightLabel.setBounds(155, 82, 73, 14);
		guiP.add(heightLabel);
		
		JComboBox TestScenecombobox = new JComboBox();
		TestScenecombobox.setModel(new DefaultComboBoxModel(Main.TestScenes.values()));
		TestScenecombobox.setBounds(155, 711, 135, 23);
		TestScenecombobox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				TestScenes a = (TestScenes)((JComboBox)arg0.getSource()).getSelectedItem();
				
				TS = a;
				
			}
			
		});
		
		
		guiP.add(TestScenecombobox);
		
		JLabel lblTestscene = new JLabel("Testscene");
		lblTestscene.setBounds(155, 680, 135, 23);
		guiP.add(lblTestscene);
		
		JComboBox LightMovementcombobox = new JComboBox();
		LightMovementcombobox.setModel(new DefaultComboBoxModel(Main.LightMovement.values()));
		LightMovementcombobox.setBounds(155, 762, 135, 20);
		LightMovementcombobox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				
				LightMovement a = (LightMovement)((JComboBox)arg0.getSource()).getSelectedItem();
					
				LM = a;
				
				
				
				
			}
			
		});
		guiP.add(LightMovementcombobox);
		
		JLabel lblLightmovement = new JLabel("LightMovement");
		lblLightmovement.setBounds(155, 740, 135, 23);
		guiP.add(lblLightmovement);
		
		JComboBox CLAcombo = new JComboBox();
		CLAcombo.setModel(new DefaultComboBoxModel(Main.CLA.values()));
		CLAcombo.setBounds(155, 813, 135, 20);
		CLAcombo.setSelectedIndex(2);
		CLAcombo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				
				CLA a = (CLA)((JComboBox)arg0.getSource()).getSelectedItem();
					
				CAMERA_LOOK_AT = a;
				
				
				
				
			}
			
		});
		guiP.add(CLAcombo);
		
		JLabel lblCameraLookDir = new JLabel("Camera Look Direction");
		lblCameraLookDir.setBounds(155, 791, 135, 23);
		guiP.add(lblCameraLookDir);
		
		JComboBox Kamerafahrtcombo = new JComboBox();
		Kamerafahrtcombo.setModel(new DefaultComboBoxModel(KameraFahrt.values()));
		Kamerafahrtcombo.setBounds(155, 869, 135, 20);
		Kamerafahrtcombo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				
				KameraFahrt a = (KameraFahrt)((JComboBox)arg0.getSource()).getSelectedItem();
					
				KF = a;
				
				
				
				
			}
			
		});
		guiP.add(Kamerafahrtcombo);
		
		JLabel lblKamerafahrt = new JLabel("Kamerafahrt");
		lblKamerafahrt.setBounds(155, 847, 135, 23);
		guiP.add(lblKamerafahrt);
		
		JFormattedTextField XField = new JFormattedTextField();
		XField.setBounds(205, 591, 85, 20);
		XField.setValue(new Float(LOOK_AT_POSITION.x));
		
		XField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				LOOK_AT_POSITION.x = (float)(a.getValue());
				LOOK_IN_DIR.x = (float)(a.getValue());
				
				
			}
			
		});
		guiP.add(XField);
		
		JFormattedTextField YField = new JFormattedTextField();
		YField.setBounds(205, 622, 85, 20);
		
		YField.setValue(new Float(LOOK_AT_POSITION.y));
		
		YField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				LOOK_AT_POSITION.y = (float)(a.getValue());
				LOOK_IN_DIR.y = (float)(a.getValue());
				
				
			}
			
		});
		guiP.add(YField);
		
		JFormattedTextField ZField = new JFormattedTextField();
		ZField.setBounds(205, 653, 85, 20);
		ZField.setValue(new Float(LOOK_AT_POSITION.z));
		
		ZField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				LOOK_AT_POSITION.z = (float)(a.getValue());
				LOOK_IN_DIR.z = (float)(a.getValue());
				

				
			}
			
		});
		guiP.add(ZField);
		
		JLabel lblX = new JLabel("X");
		lblX.setBounds(155, 594, 46, 14);
		
		guiP.add(lblX);
		
		JLabel lblY = new JLabel("Y");
		lblY.setBounds(155, 624, 46, 14);
		guiP.add(lblY);
		
		JLabel lblZ = new JLabel("Z");
		lblZ.setBounds(155, 654, 46, 14);
		guiP.add(lblZ);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(155, 426, 143, 148);
		guiP.add(scrollPane_2);
		
		final JList NumList = new JList(numLM);
		scrollPane_2.setViewportView(NumList);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(155, 358, 64, 23);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
	           
	        	numLM.addElement(NUMBER);
	        		            
	        }
		});
		guiP.add(btnAdd);
		
		JButton btnDel = new JButton("Del");
		btnDel.setBounds(155, 392, 64, 23);
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
	           
				if(NumList.getSelectedIndex() >= 0)
		           {
		        	   int i = NumList.getSelectedIndex();
		        	   numLM.remove(i);
		        	  
		           }
	        		            
	        }
		});
		guiP.add(btnDel);
		
		JFormattedTextField NUMBERTextField = new JFormattedTextField();
		NUMBERTextField.setBounds(229, 360, 81, 17);
		NUMBERTextField.setValue(new Integer(NUMBER));
		
		NUMBERTextField.addPropertyChangeListener("value", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JFormattedTextField a = (JFormattedTextField) evt.getSource();
				
				
				
				
				NUMBER = (int)(a.getValue());
				
				
			}
			
		});
		guiP.add(NUMBERTextField);
		
		final JCheckBox chckbxNewCheckBox = new JCheckBox("Object");
		chckbxNewCheckBox.setEnabled(false);
		
		final JCheckBox chckbxLight = new JCheckBox("Light");
		chckbxLight.setEnabled(false);
		chckbxLight.setBounds(151, 328, 68, 23);
		chckbxLight.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					chckbxNewCheckBox.setSelected(false);
					
					LIGHTOBJECTTEST = true;
					
				}
				else if(!a.isSelected())
				{
					
					chckbxNewCheckBox.setSelected(true);
					
					LIGHTOBJECTTEST = false;
					
				
				}
				
				
				
			}
			
			
			
		});
		
		guiP.add(chckbxLight);
		
		
		chckbxNewCheckBox.setBounds(221, 324, 85, 27);
		chckbxNewCheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					chckbxLight.setSelected(false);
					
					LIGHTOBJECTTEST = false;
					
				}
				else if(!a.isSelected())
				{
					
					chckbxLight.setSelected(true);
					
					LIGHTOBJECTTEST = true;
					
				
				}
				
				
				
			}
			
			
			
		});
		
		guiP.add(chckbxNewCheckBox);
		final JCheckBox chckbxSponzatest = new JCheckBox("SponzaTest");
		final JCheckBox chckbxDefault = new JCheckBox("DEFAULT");
		chckbxDefault.setSelected(true);
		chckbxDefault.setBounds(225, 381, 89, 17);
		chckbxDefault.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					
					chckbxLight.setEnabled(false);
					chckbxNewCheckBox.setEnabled(false);
					chckbxSponzatest.setSelected(false);
					DEFAULTBENCHMARK = true;
					
				}
				else if(!a.isSelected())
				{
					
					
					chckbxLight.setEnabled(true);
					chckbxNewCheckBox.setEnabled(true);
					DEFAULTBENCHMARK = false;
					
				
				}
				
				
				
			}
			
			
			
		});
		guiP.add(chckbxDefault);
		
		
		chckbxSponzatest.setActionCommand("SponzaTest");
		chckbxSponzatest.setBounds(225, 402, 85, 17);
		chckbxSponzatest.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				JCheckBox a = (JCheckBox)arg0.getSource();
				if(a.isSelected())
				{
					
					chckbxLight.setEnabled(false);
					chckbxNewCheckBox.setEnabled(false);
					chckbxDefault.setSelected(false);
					DEFAULTBENCHMARK = false;
					SPONZATEST = true;
				}
				else if(!a.isSelected())
				{
					
					
					chckbxLight.setEnabled(true);
					chckbxNewCheckBox.setEnabled(true);
					DEFAULTBENCHMARK = true;
					SPONZATEST = false;
				
				}
				
				
				
			}
			
			
			
		});
		guiP.add(chckbxSponzatest);
		
		display_parent.setFocusable(true);
		display_parent.requestFocus();
		display_parent.setIgnoreRepaint(true);
		
		pack();
		setVisible(true);
	}
	
	boolean reSetUp = false;
	/*
	 * Erstellt neue Shader-Programme mit neuen Tileheights/widths. Da das im OpenGl-Kontext erfolgen muss, wird hier nur ein boolean geflippt.
	 */
	public void retile()
	{
		reSetUp=true;	
	}
	
	/*
	 * Erstellt neue Shader-Programme mit neuen Tileheights/widths. Da das im OpenGl-Kontext erfolgen muss, wird hier nur ein boolean geflippt.
	 */
	public void retileTilePrograms()
	{

		Matrix4f proj;
		proj = new Matrix4f(camera.getProj());
 		//Wird für Umrechnung der Z-Werte aus NDC zu Worldview gebraucht
 		float C = new Float(proj.m22);
 		float D = new Float(proj.m32);	
		reSetUp = false;
		
		TDP = new TiledDeferredPar(C,D,tileWidth, tileHeight);
        
        checkE(true);
        TFP = new TiledForwardPar(C,D, tileWidth, tileHeight);
        
        TDP.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        TFP.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        
        setUpPLightUBOProg(TDP.SPprogram);
        
        setUpPLightUBOProg(TDP.ComputeTile);
        
        
        

        
        setUpPLightUBOProg(TFP.FPprogram);
        
        setUpPLightUBOProg(TFP.ComputeTile);
        
        setupTiledSSBO();
        checkE(true);
        setUpTileInts(TDP);
        setUpTileInts(TFP);
	}
	
	
	/*
	 * Starte die Swing-Gui
	 */
	public void init(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try{
			createSwingGui();
			
		}catch (Exception e) {
			
			System.err.println(e);
			
			throw new RuntimeException("Unable to create display");
			
		}
		
		this.setSize(343, 367);
		
				
	}
	/*
	 * Berechne die globalen Attenuation-Werte anhand einer angegebenen Distanz
	 */
	public void calcfromdis(float dis)
	{
		
			CONSTATTEN = 1;
			LINATTEN = 0;
			
			QUADATTEN = (PROZENT - CONSTATTEN)/(dis * dis);
			
	}
	/*
	 * Berechne den Radius anhand der globalen Attenuation-Werte und gib ihn zur�ck
	 */
	public float calcRadius()
	{
		float distance = 0.0f;
		
		
		if(QUADATTEN > 0 && LINATTEN > 0)
		{
			distance = (float)(-(LINATTEN /(2 * QUADATTEN)) + Math.sqrt(Math.pow(LINATTEN/(2 * QUADATTEN),2) - ((CONSTATTEN - PROZENT)/QUADATTEN)));
		}
		
		else if (LINATTEN > 0)
		{
			distance = (PROZENT - CONSTATTEN) / LINATTEN;
		}
		
		else if (QUADATTEN > 0)
		{
			distance = (float)(Math.sqrt((PROZENT - CONSTATTEN) / QUADATTEN));
			
		}
		
		else
		{
			distance = 0;
			return 1;
		}
		
		distance *= 1f;
		
		return distance;
		
	}
	
	/*
	 * Speichert den Benchmarkstring als File
	 */
	public void saveStringToFile()
	{
		try  
		{

			Writer out = new BufferedWriter(new OutputStreamWriter(
				    new FileOutputStream(BenchFileString), "UTF-8"));
				try {
				    out.write(BenchmarkString);
				} finally {
				    out.close();
				}

		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Der Core des Benchmarktests, hier wird f�r jeden gespeicherten Algorithmus in der 
	 * oberen Liste das Rendern f�r eine Kamerafahrt ausgef�hrt und die Werte zur�ckgegeben
	 */
	public String benchMarkTest()
	{
		queryID[queryBackBuffer] = glGenQueries();
		queryID[queryFrontBuffer] = glGenQueries();	
		
		
		DecimalFormat f = new DecimalFormat("##.##");
		
		TiledModeEnum a;
		String FINALSTRING = "";
		String firstline = "Algorithmus\t|Mil.Sec. - Min\t|Mil.Sec. - Max\t|Mil.Sec. - Mean\t|Mil.Sec. - Median";
		firstline += "\r\n___________________________________________________________________________________________________________________\r\n";
		
		if(!SPONZATEST)firstline ="Spherecount: " + SPHERE_COUNT + "SpherePlacement: " + BP + "\r\nLightcount: " + MAX_PLIGHT + " \tLightPlacement: " + LP + "\r\n" + firstline;
		else firstline = "\r\nLightcount: " + MAX_PLIGHT + " \tLightPlacement: " + LP + "\r\n" + firstline;
		
		if(!SPONZATEST)firstline ="Lightstats: \r\nConstAtten: " + CONSTATTEN + " LinearAtten: " + LINATTEN + " QuadAtten: " + QUADATTEN + " Prozent: " + PROZENT + " Radius: " + calcRadius() + "\r\n"+ firstline ;
		
		
		for(int i = 0; i < BenchMarkTest.size();i++)
		{
			if(isKeyDown(Keyboard.KEY_ESCAPE))
				break;
			
			String extra = "";
			String s = "";
			ZCULLING = zCulList.get(i);
			TF_EARLYZ = EAZList.get(i);
			
			switch(BenchMarkTest.get(i))
			{
			
			case "EarlyZ_PFF":
				shade = Shader.EarlyZ;
				extra = "\t";
				break;
			
			case "Deferred":
				shade = Shader.DefRen;
				extra = "\t";
				break;
				
			case "DeferredSphere":
				shade = Shader.DefSphereCul;
				break;
				
			
			case "DeferredWithStencil":
				shade = Shader.DefSphereStencil;			
				break;
			
				
			case "TiledDeferred":
				shade = Shader.TiledDefPar;
				a = TMEList.get(i);
				System.out.println(a);
				s += "\r\n" + a + "\r\n";
				s += "zCulling: " + ZCULLING + "\r\n";
				
				switch(a)
				{
				
				case OneSubdivision:
					TiledMode = 0;
					break;
					
				case TwoSubdivisons_1:
					TiledMode = 1;
					break;
					
				case TwoSubdivisons_2:
					TiledMode = 2;
					break;
					
				case ThreeSubdivisions:
					TiledMode = 3;
					break;
					
				case FourSubdivisions:
					TiledMode = 4;
					break;
					
				
				
				}
				s+= TiledModeA[TiledMode] + "\r\n";
				
				
				break;
					
			
			
			case "TiledForward":
				shade = Shader.TiledForwardPar;
				a = TMEList.get(i);
				System.out.println(a);
				s +="\r\n" + a + "\r\n";
				s += "zCulling: " + ZCULLING + "\r\n";
				s += "EarlyZ: " + TF_EARLYZ + "\r\n";
				switch(a)
				{
				
				case OneSubdivision:
					TiledMode = 0;
					break;
					
				case TwoSubdivisons_1:
					TiledMode = 1;
					break;
					
				case TwoSubdivisons_2:
					TiledMode = 2;
					break;
					
				case ThreeSubdivisions:
					TiledMode = 3;
					break;
					
				case FourSubdivisions:
					TiledMode = 4;
					break;
					
				
				
				}
				s+= TiledModeA[TiledMode] + "\r\n";
				break;
			
			case "PerFragmentForward":
				shade = Shader.PerPixel;
				break;
			}
			
			
			
			
			camera.startBench(BENCHMARKLENGTH);
			
			frameCount = 0;
			Boolean breakOut = false;
			while(camera.start )
			{
				if(isKeyDown(Keyboard.KEY_ESCAPE))
				{
					breakOut = true;
					camera.endBench();
					break;
				}
				renderTest();
			}
			
			if(breakOut)
				break;
			
			System.out.println(shade);
			
			
			
			
			
			
			
			float averageMSec = 0.0f;
			float minMSec = MilList.get(1);
			float maxMSec = MilList.get(0);
			
			
			for (int j = 0; j < MilList.size(); j++)
			{
				averageMSec += MilList.get(j);
				if (minMSec > MilList.get(j) && MilList.get(j) >= 1) minMSec = MilList.get(j);
				if (maxMSec < MilList.get(j)) maxMSec = MilList.get(j);
				
			}
			averageMSec /= MilList.size();
			Collections.sort(MilList);
			
			
			float median = MilList.get((int) (MilList.size()/2));
			
			
			
			if(shade != Shader.DefSphereStencil)
			{
				extra += "\t";
			}
			s = extra + "|"  + f.format(minMSec) + "\t\t|" + f.format(maxMSec) + "\t\t|" + f.format(averageMSec)  + "\t\t|" + f.format(median) +s;
			s = "\r\n" + shade + s;
			
			
			MilList.clear();
			
			FINALSTRING += s;
			
		}
		
		FINALSTRING = firstline + FINALSTRING;
		System.out.println(FINALSTRING);
		
		return FINALSTRING;
		
	}
	
	/*
	 * Starte den Benchmarktest, je nach boolean ver�ndere die Einstellungen und Anzahl an Testf�llen
	 * 
	 */
	public void startBenchmarkTest()
	{
		
		
		
		switch(KF)
		{	
			
		
		case Box:
			camera.setUpWithPForKnots(KLIST, BENCHMARKLENGTH);
			break;
			
		case PlaneAll:
			
			camera.setUpWithPForKnots(PLANEALL, BENCHMARKLENGTH);
			break;
			
		case PlaneSide:
			
			break;
			
		case PlaneAbove:
			camera.setUpWithPForKnots(PLANEABOVE, BENCHMARKLENGTH);
			break;
		
		case CrytekBuildInside:
			
			camera.setUpWithPForKnots(CRYTEKINSIDE, BENCHMARKLENGTH);
			break;
		
		
		}
		
		if(SPONZATEST)
		{
			int tempSphereCount = SPHERE_COUNT;
			int tempLightCount = MAX_PLIGHT;
			float tempRadius = calcRadius();
			
			SPHERE_COUNT = 0;
			
			
			List<Float> RadList = new ArrayList<Float>();
			for(int j = 1; j < 3; j++)
			{
				RadList.clear();
				MAX_PLIGHT = LIGHT_SPONZA_FIXED * j;
				
				for(int i = 0; i < MAX_PLIGHT; i++)
				{
					
					float curRadius = random( MINRADIUS, MAXRADIUS - MINRADIUS);
					RadList.add(curRadius);
					calcfromdis(curRadius);
					changeLightStatsSingle(i , CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
					
					float SponzaX = random(SPONZA_MIN_X, SPONZA_MAX_X - SPONZA_MIN_X);
					float SponzaY = random(SPONZA_MIN_Y, SPONZA_MAX_Y - SPONZA_MIN_Y);
					float SponzaZ = random(SPONZA_MIN_Z, SPONZA_MAX_Z - SPONZA_MIN_Z);
							
					
					pLightA[i].setPos(new Vector3f(SponzaX,SponzaY,SponzaZ));
					
				}
				
							
				
				updateAllLightUBO();
				updateShader();
				
				float RadSum = 0;
				
				for(int i = 0; i < RadList.size(); i++)
				{
					RadSum += RadList.get(i);
				}
				
				float RadAverage = RadSum / RadList.size();
				System.out.println(RadAverage);
				
				BenchmarkString += "Average Case-Test_________no Spheres_____Lights fixed at " + MAX_PLIGHT + "___Average Radius is " + RadAverage;
				BenchmarkString += "\r\n\r\n";
				BenchmarkString += "____________________________________________________________________________________";
				BenchmarkString += "\r\n\r\n";
				
				
					
				BenchmarkString += benchMarkTest()+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
				
			}
		}
		
		
		else if(DEFAULTBENCHMARK)
		{
			int tempSphereCount = SPHERE_COUNT;
			int tempLightCount = MAX_PLIGHT;
			float tempRadius = calcRadius();
		
			if(KF == KameraFahrt.PlaneSide){
				camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
				}
			
			SPHERE_COUNT = SPHERE_FIXED;
			MAX_PLIGHT = LIGHT_FIXED;
			updateShader();
			
			BenchmarkString += "RADIUS-Test_________Spheres fixed at " + SPHERE_FIXED + "_____Lights fixed at " + LIGHT_FIXED;
			BenchmarkString += "\r\n\r\n";
			BenchmarkString += "____________________________________________________________________________________";
			BenchmarkString += "\r\n\r\n";
			
			for(int p = 0; p < RADIUS_TEST_ARRAY.length; p++)
			{
				calcfromdis(RADIUS_TEST_ARRAY[p]);
				changeLightStats(CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
				updateShader();
								
				
				BenchmarkString += benchMarkTest()+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
				
		
			}
			
			
			calcfromdis(RADIUS_FIXED);
			changeLightStats(CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
			MAX_PLIGHT = LIGHT_FIXED;
			updateShader();
			
			BenchmarkString += "SPHERE-Test_________Radius fixed at " + RADIUS_FIXED + "_____Lights fixed at " + LIGHT_FIXED;
			BenchmarkString += "\r\n\r\n";
			BenchmarkString += "____________________________________________________________________________________";
			BenchmarkString += "\r\n\r\n";
			
			for(int o = 0; o < SPHERE_TEST_ARRAY.length; o++)
			{		
					
				SPHERE_COUNT = SPHERE_TEST_ARRAY[o];
				if(KF == KameraFahrt.PlaneSide){
					
				if(SPHERE_COUNT <100) camera.setUpWithPForKnots(PLANESIDE50,BENCHMARKLENGTH);
				else if(SPHERE_COUNT == 100)camera.setUpWithPForKnots(PLANESIDE100,BENCHMARKLENGTH);
				else if(SPHERE_COUNT == 200)camera.setUpWithPForKnots(PLANESIDE200,BENCHMARKLENGTH);
				else if(SPHERE_COUNT == 500)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
				else if(SPHERE_COUNT == 750)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
				else if(SPHERE_COUNT == 1000)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH); 
					
				}
					
				
			
				
				
				BenchmarkString += benchMarkTest()+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
				
		
		
			}
			
			if(KF == KameraFahrt.PlaneSide){
			camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
			}
			SPHERE_COUNT = SPHERE_FIXED;
			calcfromdis(RADIUS_FIXED);
			changeLightStats(CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
			updateShader();
			
			BenchmarkString += "LIGHT-Test_________Radius fixed at " + RADIUS_FIXED + "_____Spheres fixed at " + SPHERE_FIXED;
			BenchmarkString += "\r\n\r\n";
			BenchmarkString += "____________________________________________________________________________________";
			BenchmarkString += "\r\n\r\n";
			
			for(int k = 0; k < LIGHT_TEST_ARRAY.length; k++)
			{
				
							
							
				MAX_PLIGHT = LIGHT_TEST_ARRAY[k];
				if(KF == KameraFahrt.PlaneSide){
					
					if(MAX_PLIGHT <100) camera.setUpWithPForKnots(PLANESIDE50,BENCHMARKLENGTH);
					else if(MAX_PLIGHT == 100)camera.setUpWithPForKnots(PLANESIDE100,BENCHMARKLENGTH);
					else if(MAX_PLIGHT == 200)camera.setUpWithPForKnots(PLANESIDE200,BENCHMARKLENGTH);
					else if(MAX_PLIGHT == 500)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
					else if(MAX_PLIGHT == 750)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH);
					else if(MAX_PLIGHT == 1000)camera.setUpWithPForKnots(PLANESIDE500,BENCHMARKLENGTH); 
						
				}
				
				updateShader();
				
				
				BenchmarkString += benchMarkTest()+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
			
			}
		
				
			calcfromdis(tempRadius);
			changeLightStats(CONSTATTEN,LINATTEN,QUADATTEN, PROZENT);
			
			SPHERE_COUNT = tempSphereCount;
			MAX_PLIGHT = tempLightCount;
			updateShader();
		
		
		}
		
		else
		{
			if(NUMBERLIST.size() > 0)
			{
				int tempSphereCount = SPHERE_COUNT;
				int tempLightCount = MAX_PLIGHT;
				
				for(int k = 0; k < NUMBERLIST.size(); k++)
				{
					
						if(LIGHTOBJECTTEST == true)
						{
						
							MAX_PLIGHT = NUMBERLIST.get(k) <= 1024? NUMBERLIST.get(k) : 1024 ;
							updateShader();
						}
						else 
						{
							
							SPHERE_COUNT = NUMBERLIST.get(k);
							
						}
					
						
						
						BenchmarkString += benchMarkTest()+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
								+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
								+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
						
				}
			
						
				SPHERE_COUNT = tempSphereCount;
				MAX_PLIGHT = tempLightCount;
			
			
			
			}
			
			else
				
			{
				
				
				
				
				BenchmarkString += benchMarkTest() + "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n"
						+ "\r\n\r\n\r\n___________________________________________________\r\n\r\n\r\n";
				
			}
			
		}
		
		
		
		
		/*
		 * 
		 * 
		 * Save Data to file
		 * 
		 * 
		 * 
		 * 
		 */
		
		
		
		saveStringToFile();
		
	
	}
	
	
	
	/*
	 * Renderfunktion wird innerhalb dieser hier bei den Benchmarktests ausgef�hrt, noch zuvor werden die ben�tigten Queries abgefragt, daher 
	 * diese Zwischenfunktion
	 */
	public void renderTest()
	{
			
		glBeginQuery(GL_TIME_ELAPSED, queryID[queryBackBuffer]);
		
		long b = getTimeD();
		render();
		
		long a = getTimeD() -b;
		
		glEndQuery(GL_TIME_ELAPSED);
		
		
		glEndQuery(GL_TIME_ELAPSED);
		int done = 0;
		
		long timeTaken = glGetQueryObjectui64(queryID[queryFrontBuffer], GL_QUERY_RESULT);
		
		//swapQuery
		
		
				if(queryBackBuffer == 1)
				{
					queryBackBuffer = 0;
					queryFrontBuffer = 1;
				}
				else
				{
					queryBackBuffer = 1;
					queryFrontBuffer = 0;
				}
				
		
		
		MilList.add(timeTaken / 1000000.0f);
		
		displayStringShader.bind();
		drawString("LightCount: " + MAX_PLIGHT,100,130);
		drawString("SphereCount: " + SPHERE_COUNT,100,110);
		drawString(shade.toString(),100,50);
		drawString("" + timeTaken / 1000000.0f,100,70);
		if(shade == Shader.TiledDefPar || shade == Shader.TiledForwardPar)
		{
			drawString(TiledModeA[TiledMode],300,50);
		}
		displayStringShader.unbind();
		Display.update();
		Display.sync(syncrate);
		
	}
	
	/*
	 * Bereitet alle benötigten Daten wie Objekte, Framebuffer, Kamera, etc. vor
	 */
	public void initGL()
	{
		
		int a = 0;
		System.out.println(GL11.glGetInteger( GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));
		
		
		directionTest.normalise();

		Mouse.setClipMouseCoordinatesToWindow(false);
		camera = new Camera(fov, (float)width / (float) height, zNear, zFar);
		camera.setPosition ( new Vector3f(0,250,-1000));
		camera.lookInDirection(new Vector3f(0,0,1));
		
		
		camera.setUpWithPForKnots(KLIST, BENCHMARKLENGTH);

		
		
		
		
		//load all obj
		loadAllObj();
		
		
		
		for(int i = 0; i < SPHERE_COUNT*SPHERE_COUNT_SCALE; i++)
		{
			SPHERE_POS[i] = new Vector3f((float)(SPHERE_POS_MIN + Math.random()*SPHERE_POS_MAX),random(SPHERE_POS_Z_MIN,SPHERE_POS_Z_MAX),(float)(SPHERE_POS_MIN + Math.random()*SPHERE_POS_MAX) - 20);
			SPHERE_COL[i] = new Vector3f(random(0,1),random(0,1),random(0,1));
			SPHERE_SPEC[i] = random(1.0f,150.0f);
			SPHERE_FAR_POS[i] = new Vector3f((float)(SPHERE_FAR_POS_MIN + Math.random()*SPHERE_FAR_POS_MAX),random(SPHERE_FAR_POS_Z_MIN,SPHERE_FAR_POS_Z_MAX),(float)(SPHERE_FAR_POS_MIN + Math.random()*SPHERE_FAR_POS_MAX) - 20);
						
		}
		
		
		
		float ambfactor = 0.1f;
		float specfactor = 0.2f;
				
		//PointLights vorbereiten
		
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			pLightA[i] = new Light();
			pLightA[i].diff = new Vector3f(random(0.3f,1f),random(0.3f,1f),random(0.3f,1f));
			pLightA[i].setPos(new Vector3f(random(SPHERE_POS_MIN - 50,SPHERE_POS_MAX + 100),random(SPHERE_POS_Z_MIN,SPHERE_POS_Z_MAX),random(SPHERE_POS_MIN - 50,SPHERE_POS_MAX + 100)));
			pLightA[i].ambient = new Vector3f(ambfactor * pLightA[i].diff.x,ambfactor*pLightA[i].diff.y,ambfactor*pLightA[i].diff.z);
			pLightA[i].specular = new Vector3f(pLightA[i].diff);
			pLightA[i].attenConst = CONSTATTEN;
			pLightA[i].attenLin = LINATTEN;
			pLightA[i].attenQuad = QUADATTEN;
			pLightA[i].proz = PROZENT;
			pLightA[i].calcDistanceFromAtten();
		
		}
		
		
		
		
		if(MAX_PLIGHT > 0){
			pLightA[0].setPos(new Vector3f(0,0,0));
			pLightA[0].diff = pLight_TEST;
			pLightA[0].ambient = new Vector3f(pLight_TEST.x * 0.1f,pLight_TEST.y * 0.1f,pLight_TEST.z * 0.1f); 
			pLightA[0].specular = pLight_TEST;
		}
		if(MAX_PLIGHT > 1){
			pLightA[1].setPos( new Vector3f(sphereTest));
			pLightA[1].diff = new Vector3f(1f,0,0);
			pLightA[1].specular = new Vector3f(1f,0,0);;
			pLightA[1].ambient = new Vector3f(pLight_TEST.x * 0.1f,pLight_TEST.y * 0.1f,pLight_TEST.z * 0.1f); 
		}
		
		
		
		
		
		
		
		
		
		
		
		
		//Erstelle Framebuffer
		createDeferredRenderingFBO();
		
		createRenderingFBO();
		
		
		//Alle Modelle/Objekte vorbereiten
		setUpModelsAndObjects();
		

		//Shader für String-Darstellung als weiße Pixel vorbereiten
        displayStringShader = new ShaderProgram();
        displayStringShader.attachVertexShader("displayString.vert");
        displayStringShader.attachFragmentShader("displayString.frag");
        displayStringShader.link();
        
      
        Matrix4f proj; //Projektionsmatrix
		
		
		
 		proj = new Matrix4f(camera.getProj());
 		//Wird für Umrechnung der Z-Werte aus NDC zu Worldview gebraucht
 		float C = new Float(proj.m22);
 		float D = new Float(proj.m32);	
     		
        
        /*
         * 
         * Alle Algos vorbereiten
         * 
         * 
         */
 		
 		System.out.println("Max. Shader Storage Buffer Bindings (mindestens 8 gebraucht):" + glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS));
 		
        PFF = new PerFragmentForward();
        checkE(true);
        DF = new Deferred();
        checkE(true);
        DFSP = new DeferredSphere();
        checkE(true);
        DSS = new DeferredSphereStencil();
        checkE(true);
     
        TDP = new TiledDeferredPar(C,D,tileWidth, tileHeight);
        
        checkE(true);
        TFP = new TiledForwardPar(C,D, tileWidth, tileHeight);
        checkE(true);
        CamAlgo = new Cam();
        checkE(true);
        DRT = new DefRenTest();
        checkE(true);
        EAZ = new EarlyZPFF();
        checkE(true);
        
        PFF.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR);
        DF.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        DFSP.init(pLightA, MAX_PLIGHT,AMBIENTCOLOR, width, height);
        DSS.init(pLightA, MAX_PLIGHT, AMBIENTCOLOR, width, height);
        TDP.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        TFP.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        CamAlgo.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        DRT.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        EAZ.init(pLightA, MAX_PLIGHT,  AMBIENTCOLOR);
        
        ShaderProgram.unbind();
        
        
      
        setUpPLightUBOProg(PFF.FPprogram);
        
        setUpPLightUBOProg(DF.SPprogram);
        
        setUpPLightUBOProg(DFSP.FPprogram);
        
        setUpPLightUBOProg(DFSP.SPprogram);
        
        setUpPLightUBOProg(DSS.FPprogram);
        
        setUpPLightUBOProg(DSS.SPprogram);
        
        setUpPLightUBOProg(TDP.SPprogram);
        
        setUpPLightUBOProg(TDP.ComputeTile);
        
        
        

        
        setUpPLightUBOProg(TFP.FPprogram);
        
        setUpPLightUBOProg(TFP.ComputeTile);
        
        setUpPLightUBOProg(DRT.SPprogram);
        
        setUpPLightUBOProg(EAZ.FPprogram);
 
        setupTiledSSBO();
        
        
        
        setUpTileInts(TDP);
        setUpTileInts(TFP);
        
       
		
	}
	
	
	/*
	 * Sphären an randomisierte Orte legen
	 */
	public void spherPosRandom()
	{
		
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			SPHERE_POS[i] = new Vector3f((float)(SPHERE_POS_MIN + Math.random()*SPHERE_POS_MAX),random(SPHERE_POS_Z_MIN,SPHERE_POS_Z_MAX),(float)(SPHERE_POS_MIN + Math.random()*SPHERE_POS_MAX) - 20);
			SPHERE_COL[i] = new Vector3f(random(0,1),random(0,1),random(0,1));
		}
		
		
	}
	
	
	
	/*
	 * Sphären als Würfel anordnen
	 */
	public void spherePosCube(int x, int y,int z)
	{
		
		
		
		Vector3f p = SPHERE_START_POS1;
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			SPHERE_POS[i] = new Vector3f(p.x + (i % x) * SSX, p.y + (i /(x*y))* SSY,p.z +(((i / x)%y) * SSZ) );
			
		}
	}
	/*
	 * Sphären auf eine Fläche anordnen
	 */
	public void spherePosPlane(int x, int y,int z, Vector3f start, float yspace)
	{
		
		
		
		Vector3f p = start;
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			SPHERE_POS[i] = new Vector3f(p.x + (i % x) * SSX, p.y +yspace,p.z +((i /(x)) * SSZ) );
			
		}
	}
	
	/*
	 * Lade eine Szene, bestehend aus mehreren Objekten
	 */
	public Scene loadScene(String path, float scale ) throws IOException
	{
		
		Scene scene = new Scene();
		try {
			 scene = OBJLoader.loadScene(getClass().getResourceAsStream(path), this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String s : scene.Texturenames)
		{
			if(s != null && s != "")
			{
				s = s.replace('\\', '/');
			System.out.println("trying to load: " + s);
			if(s.contains("curtain"))
				{
				
				int a = 2;;
				}
			scene.Textures.add(TextureLoader.getTexture(
						new String(s.substring(s.length()-3).toUpperCase()),
						getClass().getResourceAsStream( s))
						);
				
			System.out.println(s + " loaded");
			}	
			
			
		}
		
		for(Model model : scene.Models)
		{
			float data[][] = loadData(model,scale);
			//System.out.println(model.count);
			float vertA[] = data[0];
			float norA[] = data[1];
			float texA[] = new float[0];
			float tanA[] = new float[0];
			
			float[] colA = new float[vertA.length];
			for(int i = 0; i < vertA.length; i = i + 3){
				
				colA[i] = 0.4f;
				colA[i+1] = 0.27f;
				colA[i + 2] = 0.17f;
				
				
			}
			
			if(data.length > 2)
			{
				texA = data[2];
				tanA = data[3];
			}
			
			FloatBuffer vertB = BufferUtils.createFloatBuffer(vertA.length);
			vertB.put(vertA);
			vertB.flip();
			
			FloatBuffer norB = BufferUtils.createFloatBuffer(norA.length);
			norB.put(norA);
			norB.flip();
			
			FloatBuffer colB = BufferUtils.createFloatBuffer(colA.length);
			colB.put(colA);
			colB.flip();
			
			FloatBuffer texB = BufferUtils.createFloatBuffer(texA.length);
			texB.put(texA);
			texB.flip();
			
			FloatBuffer tanB = BufferUtils.createFloatBuffer(tanA.length);
			tanB.put(tanA);
			tanB.flip();
		
			int vao = glGenVertexArrays();
			glBindVertexArray(vao);
			
			int vboVert = glGenBuffers();
	        glBindBuffer(GL_ARRAY_BUFFER, vboVert);
	        glBufferData(GL_ARRAY_BUFFER, vertB,GL_STATIC_DRAW);
	        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
	        
	        int vboCol = glGenBuffers();
	        glBindBuffer(GL_ARRAY_BUFFER, vboCol);
	        glBufferData(GL_ARRAY_BUFFER, colB,GL_STATIC_DRAW);
	        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
	        
	        int vboNor = glGenBuffers();
	        glBindBuffer(GL_ARRAY_BUFFER, vboNor);
	        glBufferData(GL_ARRAY_BUFFER, norB,GL_STATIC_DRAW);
	        glVertexAttribPointer(2,3,GL_FLOAT,false,0,0);
	        
	        
	       
	        
	        
			if(data.length > 2)
			{
			    int vboTex = glGenBuffers();
			    glBindBuffer(GL_ARRAY_BUFFER, vboTex);
			    glBufferData(GL_ARRAY_BUFFER, texB,GL_STATIC_DRAW);
			    glVertexAttribPointer(3,2,GL_FLOAT,false,0,0);
			    
			    
			    int vboTan = glGenBuffers();
			    glBindBuffer(GL_ARRAY_BUFFER, vboTan);
			    glBufferData(GL_ARRAY_BUFFER, tanB,GL_STATIC_DRAW);
			    glVertexAttribPointer(4,3,GL_FLOAT,false,0,0);
			 
			}
			
			glBindVertexArray(0);
			
			model.vao = vao;
			
			
			
			if(model.sambTexture != null && model.sambTexture != "")
			{
				
				model.ambTexture = scene.Textures.get(scene.Texturenames.indexOf(model.sambTexture)).getTextureID();
				System.out.println(model.sambTexture);
				
				
			}
			if(model.sdifTexture != null && model.sdifTexture != "")
			{
				
				model.difTexture = scene.Textures.get(scene.Texturenames.indexOf(model.sdifTexture)).getTextureID();
				
			}
			if(model.sspecTexture != null && model.sspecTexture != "")
			{
				
				model.specTexture = scene.Textures.get(scene.Texturenames.indexOf(model.sspecTexture)).getTextureID();
				
				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
		
		return scene;
		
	}
	
	
	/*
	 * Lade ein Model
	 */
	public Model loadModelInt(String path, float scale )
	{
		Model model = new Model();
		try {
			 model = OBJLoader.loadModel(getClass().getResourceAsStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		float data[][] = loadData(model,scale);
		System.out.println(model.count);
		float vertA[] = data[0];
		float norA[] = data[1];
		float texA[] = new float[0];
		float tanA[] = new float[0];
		
		float[] colA = new float[vertA.length];
		for(int i = 0; i < vertA.length; i = i + 3){
			
			colA[i] = 0.4f;
			colA[i+1] = 0.27f;
			colA[i + 2] = 0.17f;
			
			
		}
		
		if(data.length > 2)
		{
			texA = data[2];
			tanA = data[3];
		}
		
		FloatBuffer vertB = BufferUtils.createFloatBuffer(vertA.length);
		vertB.put(vertA);
		vertB.flip();
		
		FloatBuffer norB = BufferUtils.createFloatBuffer(norA.length);
		norB.put(norA);
		norB.flip();
		
		FloatBuffer colB = BufferUtils.createFloatBuffer(colA.length);
		colB.put(colA);
		colB.flip();
		
		FloatBuffer texB = BufferUtils.createFloatBuffer(texA.length);
		texB.put(texA);
		texB.flip();
		
		FloatBuffer tanB = BufferUtils.createFloatBuffer(tanA.length);
		tanB.put(tanA);
		tanB.flip();
	
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		int vboVert = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVert);
        glBufferData(GL_ARRAY_BUFFER, vertB,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        int vboCol = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboCol);
        glBufferData(GL_ARRAY_BUFFER, colB,GL_STATIC_DRAW);
        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
        
        int vboNor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboNor);
        glBufferData(GL_ARRAY_BUFFER, norB,GL_STATIC_DRAW);
        glVertexAttribPointer(2,3,GL_FLOAT,false,0,0);
        
        
       
        
        
		if(data.length > 2)
		{
		    int vboTex = glGenBuffers();
		    glBindBuffer(GL_ARRAY_BUFFER, vboTex);
		    glBufferData(GL_ARRAY_BUFFER, texB,GL_STATIC_DRAW);
		    glVertexAttribPointer(3,2,GL_FLOAT,false,0,0);
		    
		    
		    int vboTan = glGenBuffers();
		    glBindBuffer(GL_ARRAY_BUFFER, vboTan);
		    glBufferData(GL_ARRAY_BUFFER, tanB,GL_STATIC_DRAW);
		    glVertexAttribPointer(4,3,GL_FLOAT,false,0,0);
		 
		}
		
		glBindVertexArray(0);
		
		model.vao = vao;
		
		
		return model;
		
	}
	
	
	
	/*
	 * Bereite alle Modelle und Objekte vor
	 */
	public void setUpModelsAndObjects()
	{
		

		if(SPONZATRUE)
		{
		
		try {
			/*
			 * Crytek-Sponza
			 * 
			 * Quelle: http://graphics.cs.williams.edu/data/meshes.xml
			 */
			sponzaScene = loadScene("sponza.obj", 1);
			
			
		
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}
	
		
		
		
		
		
		
		
		
		
		
		float[][]lightS1 = loadData(lightsphere,SPHERESCALE);
		
		float vertLA[] = lightS1[0];
		
 
		
		FloatBuffer lightSB = BufferUtils.createFloatBuffer(vertLA.length);
		lightSB.put(vertLA);
		lightSB.flip();
		
		
        
        //Sphere
        float[][] sphere1 = loadData(sphere,SPHERESCALE) ;
        
        float vertSA[] = sphere1[0];
		float norSA[] = sphere1[1];
		float texSA[] = sphere1[2];
		float tangentSA[] = sphere1[3];
		
	
		float[] colSA = new float[vertSA.length];
		for(int i = 0; i < vertSA.length; i = i + 3){
			
			colSA[i] = 0.4f;
			colSA[i+1] = 0.27f;
			colSA[i + 2] = 0.17f;
			
			
		}
        
        
		
        
        FloatBuffer sphvertB = BufferUtils.createFloatBuffer(vertSA.length);
        sphvertB.put(vertSA);
        sphvertB.flip();
        
        FloatBuffer sphnorB = BufferUtils.createFloatBuffer(norSA.length);
        sphnorB.put(norSA);
        sphnorB.flip();
        
        FloatBuffer sphtexB = BufferUtils.createFloatBuffer(texSA.length);
        sphtexB.put(texSA);
        sphtexB.flip();
        
        FloatBuffer sphtanB = BufferUtils.createFloatBuffer(tangentSA.length);
        sphtanB.put(tangentSA);
        sphtanB.flip();
        
        FloatBuffer sphcolB = BufferUtils.createFloatBuffer(colSA.length);
        sphcolB.put(colSA);
        sphcolB.flip();
        
       
		
		
		
		
		
		
        //Fläche
        
        
        float[][] planeData = loadData(plane,PLANESCALE);
        
        
        float vertPA[] = planeData[0];
        float norPA[] = planeData[1];
        
        float[] colPA = new float[vertPA.length];
		for(int i = 0; i < vertPA.length; i = i + 3){
			
			colPA[i] = 0.5f;
			colPA[i+1] = 0.5f;
			colPA[i + 2] = 0.5f;
			
			
		}
		
		
		
		FloatBuffer planevertB = BufferUtils.createFloatBuffer(vertPA.length);
		planevertB.put(vertPA);
		planevertB.flip();
        
		FloatBuffer planenorB = BufferUtils.createFloatBuffer(norPA.length);
        planenorB.put(norPA);
        planenorB.flip();
        
        FloatBuffer planecolB = BufferUtils.createFloatBuffer(colPA.length);
        planecolB.put(colPA);
        planecolB.flip();
        
        
        
        
        
        
        vaoLSID = glGenVertexArrays();
        glBindVertexArray(vaoLSID);
        
        vboLSID = glGenBuffers();
        
        glBindBuffer(GL_ARRAY_BUFFER,vboLSID);
        glBufferData(GL_ARRAY_BUFFER,lightSB,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        
        
        
        
        
        
        vaoTestID = glGenVertexArrays();
        glBindVertexArray(vaoTestID);
        
        float[] vert = {
        		testOnScreenX,testOnScreenY,0.0f, 
        		testOnScreenWidth,testOnScreenY,0.0f,
        		testOnScreenX,testOnScreenHeight,0.0f,
        		testOnScreenWidth,testOnScreenHeight,0.0f
        		};
        vboTestVertID = glGenBuffers();
        FloatBuffer bbb = BufferUtils.createFloatBuffer(12).put(vert);
        bbb.flip();
        
        float[] tex = {
        		0.0f,0.0f,
        		1.0f,0.0f,
        		0.0f,1.0f,
        		1.0f,1.0f
        };
        FloatBuffer ttt = BufferUtils.createFloatBuffer(8).put(tex);
        ttt.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboTestVertID);
        glBufferData(GL_ARRAY_BUFFER, bbb,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        vboTestTexID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vboTestTexID);
        glBufferData(GL_ARRAY_BUFFER, ttt,GL_STATIC_DRAW);  
        glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);
        
        
        glBindVertexArray(0);
        
        
        //fullscreenQuad
        vaoFSQuadID = glGenVertexArrays();
        glBindVertexArray(vaoFSQuadID);
        
        float[] FSQvert = {
        		0,0,0.0f, 
        		width,0,0.0f,
        		0,height,0.0f,
        		width,height,0.0f
        		};
        vboFSQVertID = glGenBuffers();
        FloatBuffer FSQbbb = BufferUtils.createFloatBuffer(12).put(FSQvert);
        FSQbbb.flip();
        
        float[] FSQtex = {
        		0.0f,0.0f,
        		1.0f,0.0f,
        		0.0f,1.0f,
        		1.0f,1.0f
        };
        FloatBuffer FSQttt = BufferUtils.createFloatBuffer(8).put(FSQtex);
        FSQttt.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboFSQVertID);
        glBufferData(GL_ARRAY_BUFFER, FSQbbb,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        vboFSQTexID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vboFSQTexID);
        glBufferData(GL_ARRAY_BUFFER, FSQttt,GL_STATIC_DRAW);  
        glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //plane
        vaoPlaneID = glGenVertexArrays();
        glBindVertexArray(vaoPlaneID);
        
  
       
        vboPlaneVertID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboPlaneVertID);
        glBufferData(GL_ARRAY_BUFFER, planevertB,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        vboPlaneColID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboPlaneColID);
        glBufferData(GL_ARRAY_BUFFER, planecolB,GL_STATIC_DRAW);
        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
        
        vboPlaneNorID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboPlaneNorID);
        glBufferData(GL_ARRAY_BUFFER, planenorB,GL_STATIC_DRAW);
        glVertexAttribPointer(2,3,GL_FLOAT,false,0,0);
        
		
		
		
		
		
		

        
       
        
        
        
        
        
        
        //vao Sphere
        
       
        
        vaoSphereID = glGenVertexArrays();
        glBindVertexArray(vaoSphereID);
        
  
       
        vboSphereVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboSphereVertexID);
        glBufferData(GL_ARRAY_BUFFER, sphvertB,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        
        
        
        vboSphereColorID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboSphereColorID);
        glBufferData(GL_ARRAY_BUFFER, sphcolB,GL_STATIC_DRAW);
        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
        
        vboSphereNormalID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboSphereNormalID);
        glBufferData(GL_ARRAY_BUFFER, sphnorB,GL_STATIC_DRAW);
        glVertexAttribPointer(2,3,GL_FLOAT,false,0,0);
        
        
        vboSphereTexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboSphereTexID);
        glBufferData(GL_ARRAY_BUFFER, sphtexB,GL_STATIC_DRAW);
        glVertexAttribPointer(3,2,GL_FLOAT,false,0,0);
        
        vboSphereTanID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboSphereTanID);
        glBufferData(GL_ARRAY_BUFFER, sphtanB,GL_STATIC_DRAW);
        glVertexAttribPointer(4,3,GL_FLOAT,false,0,0);
        
       
        
        
        
        glBindBuffer(GL_ARRAY_BUFFER,0);

        glBindVertexArray(0);
        
        
        
        
		
		
	}
	
	/*
	 * Folgende zwei Funktionen sind dazu da, bei Enums über einen Funktionsaufruf den nächsten/vorherigen Wert
	 * zu setzen. Von : http://stackoverflow.com/questions/8458060/how-do-i-write-a-generic-java-enum-rotator
	 */
	static <T extends Enum> T next(T t) throws Exception
    {
        Method values = t.getClass().getMethod("values");
        if (t.ordinal() == ((T[])values.invoke(t)).length - 1)
            return ((T[])values.invoke(t))[0];
        else
            return ((T[])values.invoke(t))[t.ordinal() + 1];
    }
	
	public <T extends Enum> T prev(T t) throws Exception
	{
		
		
		
		Method values = t.getClass().getMethod("values");
		
		int max = ((T[])values.invoke(t)).length - 1;
		
		if(t.ordinal() == 0)
				return ((T[])values.invoke(t))[max];
		else
			return ((T[])values.invoke(t))[t.ordinal() - 1];
	}
	

	
	

	/*
	 * Ändert mit oberen Funktionen den derzeitig benutzten Shader
	 */
	public void nextShader()
	{
		
		
		
		try {
			
			shade = next(shade);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public void prevShader()
	{
	
		
		
		try {
			
			shade = prev(shade);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	/*
	 * Lade die Objektinformationen aus den OBJ-Files
	 */
	public void loadAllObj(){
		try {
			/*
			 * Selbst gemacht
			 */
			sphere = OBJLoader.loadModel(getClass().getResourceAsStream("sphereiso.obj"));
		
			
			
		
			/*
			 * Selbst gemacht
			 */
			plane = OBJLoader.loadModel(getClass().getResourceAsStream("plane.obj"));
		
			/*
			 * Selbst gemacht
			 */
			lightsphere = OBJLoader.loadModel(getClass().getResourceAsStream("sphere.obj"));
		
			
			/*
			 * Lade die Normalmap-Textur
			 * 
		     * Normalmap-Textur:
		     * 
		     * 
		     * Originales Bild dazu:
		     * http://www.texturex.com/albums/Stone-Textures/TextureX%20Stone%20Pebble%20Rocky%20Beach%20Grey%20Texture.jpg
		     * bearbeitet mit CrazyBump:
		     * http://www.crazybump.com/
		     * um eine Normalmap zu bekommen
		     *
		     *
		     */
			 
			sphNor = TextureLoader.getTexture("PNG", getClass().getResourceAsStream("rocky2.png"));
			
			glActiveTexture(GL_TEXTURE0+4);
			glBindTexture(GL_TEXTURE_2D, sphNor.getTextureID());
			glActiveTexture(GL_TEXTURE0);
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/*
	 * Gib die Floatarrayinformationen des Modells zurück
	 */
	public float[][] loadData(Model m,float scale)
	{
		
		
		List<Float> vertL = new ArrayList<Float>();
		List<Float> normalL = new ArrayList<Float>();
		List<Float> texcoordL = new ArrayList<Float>();
		
		List<Float> tangentL = new ArrayList<Float>();
		
		List<Vector3f[]> tangentVA = new ArrayList<Vector3f[]>();
			
		int indi = m.faces.size();
		
		boolean hasTex = false;
		
		for(Face face : m.faces)
		{
			Vector3f v1;
			Vector3f v2;
			Vector3f v3;
			
			Vector3f v;
			
			Vector2f uv1;
			Vector2f uv2;
			Vector2f uv3;
			
			Vector3f n;
			
			switch(face.type)
			{
			
			case 1:
				
			
				
				
				n = m.normals.get((int) face.normal.x - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.y - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.z - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				
				v = m.vertices.get((int) face.vertex.x - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v1 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.y - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v2 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.z - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v3 = new Vector3f(v);
				
				if(face.hasTex){
					
					hasTex = true;
					Vector2f uv = m.texcoord.get((int) face.texcoord.x - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv1 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.y - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv2 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.z - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
				
					
					uv3 = new Vector2f(uv);
					
					//http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
					
					
					Vector3f kante1 = new Vector3f();
					
					Vector3f.sub(v2, v1, kante1);
					
					Vector3f kante2 = new Vector3f();
					
					Vector3f.sub(v3, v1, kante2);
					
					float DeltaU1 = uv2.x - uv1.x;
					float DeltaV1 = uv2.y - uv1.x;
					float DeltaU2 = uv3.x - uv1.x;
					float DeltaV2 = uv3.y - uv1.y;
					
					float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);
					
					Vector3f Tangent = new Vector3f();
					
					Tangent.x = f * (DeltaV2 * kante1.x - DeltaV1 * kante2.x);
					Tangent.y = f * (DeltaV2 * kante1.y - DeltaV1 * kante2.y);
					Tangent.z = f * (DeltaV2 * kante1.z - DeltaV1 * kante2.z);
					
					/*
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
					*/
					Vector3f vA[] = {
							v1, Tangent
					};
					tangentVA.add(vA);
					
					Vector3f vA1[] = {
							v2, Tangent
					};
					tangentVA.add(vA1);
					
					Vector3f vA2[] = {
							v3, Tangent
					};
					tangentVA.add(vA2);
					
					
					
					
					
				}
				
				
				break;
				
			
			case 2:
				
				
				n = m.normals.get((int) face.normal.x - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.y - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.z - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				
				v = m.vertices.get((int) face.vertex.x - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v1 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.y - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v2 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.z - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v3 = new Vector3f(v);
				
				
				
				
				
				
				if(face.hasTex){
					
					hasTex = true;
					Vector2f uv = m.texcoord.get((int) face.texcoord.x - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv1 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.y - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv2 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.z - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
										
					uv3 = new Vector2f(uv);
					
					//http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
					
					
					Vector3f kante1 = new Vector3f();
					
					Vector3f.sub(v2, v1, kante1);
					
					Vector3f kante2 = new Vector3f();
					
					Vector3f.sub(v3, v1, kante2);
					
					float DeltaU1 = uv2.x - uv1.x;
					float DeltaV1 = uv2.y - uv1.x;
					float DeltaU2 = uv3.x - uv1.x;
					float DeltaV2 = uv3.y - uv1.y;
					
					float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);
					
					Vector3f Tangent = new Vector3f();
					
					Tangent.x = f * (DeltaV2 * kante1.x - DeltaV1 * kante2.x);
					Tangent.y = f * (DeltaV2 * kante1.y - DeltaV1 * kante2.y);
					Tangent.z = f * (DeltaV2 * kante1.z - DeltaV1 * kante2.z);
					
					/*
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
					*/
					Vector3f vA[] = {
							v1, Tangent
					};
					tangentVA.add(vA);
					
					Vector3f vA1[] = {
							v2, Tangent
					};
					tangentVA.add(vA1);
					
					Vector3f vA2[] = {
							v3, Tangent
					};
					tangentVA.add(vA2);
					
					
					
					
					
				}
				
				
				
				n = m.normals.get((int) face.normal.x - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.z - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				n = m.normals.get((int) face.normal.w - 1);
				normalL.add(n.x);
				normalL.add(n.y);
				normalL.add(n.z);
				
				v = m.vertices.get((int) face.vertex.x - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v1 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.z - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v2 = new Vector3f(v);
				
				v = m.vertices.get((int) face.vertex.w - 1);
				vertL.add(v.x);
				vertL.add(v.y);
				vertL.add(v.z);
				
				v3 = new Vector3f(v);
				
				
				
				
				
				
				if(face.hasTex){
					
					hasTex = true;
					Vector2f uv = m.texcoord.get((int) face.texcoord.x - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv1 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.z - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
					uv2 = new Vector2f(uv);
					
					uv = m.texcoord.get((int) face.texcoord.w - 1);
					texcoordL.add(uv.x);
					texcoordL.add(uv.y);
					
										
					uv3 = new Vector2f(uv);
					
					//http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
					
					
					Vector3f kante1 = new Vector3f();
					
					Vector3f.sub(v2, v1, kante1);
					
					Vector3f kante2 = new Vector3f();
					
					Vector3f.sub(v3, v1, kante2);
					
					float DeltaU1 = uv2.x - uv1.x;
					float DeltaV1 = uv2.y - uv1.x;
					float DeltaU2 = uv3.x - uv1.x;
					float DeltaV2 = uv3.y - uv1.y;
					
					float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);
					
					Vector3f Tangent = new Vector3f();
					
					Tangent.x = f * (DeltaV2 * kante1.x - DeltaV1 * kante2.x);
					Tangent.y = f * (DeltaV2 * kante1.y - DeltaV1 * kante2.y);
					Tangent.z = f * (DeltaV2 * kante1.z - DeltaV1 * kante2.z);
					
					/*
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
				
					tangentL.add(Tangent.x);
					tangentL.add(Tangent.y);
					tangentL.add(Tangent.z);
					*/
					Vector3f vA[] = {
							v1, Tangent
					};
					tangentVA.add(vA);
					
					Vector3f vA1[] = {
							v2, Tangent
					};
					tangentVA.add(vA1);
					
					Vector3f vA2[] = {
							v3, Tangent
					};
					tangentVA.add(vA2);
					
					
					
					
					
				}
				
				
				
				
				break;
				
				
				
				
				
			}
		}
		if(tangentVA.size() < 10000)
		{
		for(int i = 0; i < tangentVA.size(); i++)
		{
			Vector3f res = new Vector3f();
			
			for(int j = 0; j < tangentVA.size(); j++)
			{
				if(tangentVA.get(i)[0].x == tangentVA.get(j)[0].x &&
						tangentVA.get(i)[0].x == tangentVA.get(j)[0].x &&
								tangentVA.get(i)[0].x == tangentVA.get(j)[0].x)
				{
					Vector3f.add(tangentVA.get(j)[1], res, res);
					
				}
			}
			
			
			
			tangentL.add(res.x);
			tangentL.add(res.y);
			tangentL.add(res.z);
			
			
			
		}
		
		}
		
		
		
		float[] vertA = new float[vertL.size()];
		for(int i = 0; i < vertL.size(); i++) vertA[i] = vertL.get(i) * scale;
		
		float[] norA = new float[normalL.size()];
		for(int i = 0; i < normalL.size(); i++){
			
			norA[i] = normalL.get(i);
			
		}
		
		m.count = vertA.length/3;
		
		System.out.println(m.name + " loaded");
		
		
		if(hasTex)
		{
			float[] texA = new float[texcoordL.size()];
			for(int i = 0; i < texcoordL.size(); i++)
			{
				texA[i] = texcoordL.get(i);
				
			}
			
			float[] tangentA = new float[tangentL.size()];
			for(int i = 0; i < tangentL.size(); i++)
			{
				tangentA[i] = tangentL.get(i);
			}
			
			float ret[][] = {vertA, norA, texA, tangentA};
			
			return ret;
		}
		
		else 
		{
			float ret[][] = {vertA, norA};
			return ret;
		}
		
		
		
	}

	/*
	 * Random Funktion zwischen gegebenen Startwert und Startwert + Range
	 */
	public float random(float start,float range)
	{
		return (float)(start + Math.random() * range);
	}
	
	/*
	 * FBO für das Deferred Rendering
	 */
	public void createDeferredRenderingFBO()
	{
		glEnable(GL_TEXTURE_2D);
		
		
		diffuseTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, diffuseTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
	
		normalTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, normalTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		
		
		

		lightTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, lightTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		
		
		
		
		
		defDepthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, defDepthTexture);
		glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH32F_STENCIL8, width,height, 0, GL_DEPTH_COMPONENT, GL_FLOAT,(java.nio.ByteBuffer)null);
		
		
		seperateDepthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, seperateDepthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, width, height, 0, GL_RED,
				GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		
		
		
		
		
		deferredRenFBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER,deferredRenFBO);
		/*
		
		depthRenderBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER,depthRenderBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32,width,height);
		
		glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER,depthRenderBuffer);
		*/
		
	
		
		glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,GL_TEXTURE_2D, defDepthTexture,0);

		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,diffuseTexture,0);
		

		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT1,normalTexture,0);
		
	
	
		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT2,lightTexture,0);
		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT3,seperateDepthTexture,0);
		
		
		

		drawdefBuffs = BufferUtils.createIntBuffer(4);
		drawdefBuffs.put(0, GL_COLOR_ATTACHMENT0);
		drawdefBuffs.put(1, GL_COLOR_ATTACHMENT1);
		drawdefBuffs.put(2, GL_COLOR_ATTACHMENT2);
		drawdefBuffs.put(3, GL_COLOR_ATTACHMENT3);
	
		
		//drawdefBuffs.put(4, GL_DEPTH_ATTACHMENT);
		
	
		glDrawBuffers(drawdefBuffs);
		
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER)!= GL_FRAMEBUFFER_COMPLETE)
		{
		System.out.print("Error with GL_FRAMEBUFFER.");	
		}
		
		glBindFramebuffer(GL_FRAMEBUFFER,0);
		
		
		
		
		
	}
	
	
	
	/*
	 * FBO zum Arbeiten nach dem ersten Pass,
	 * FBO1 benutzt die Tiefentextur des Deferred Rendering als Tiefenpuffer
	 * FBO2 benutzt eine eigene Tiefentextur, unabhängig vom Deferred Rendering
	 */
	public void createRenderingFBO()
	{
		
        textureSampler = glGenSamplers();
		glSamplerParameteri(textureSampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glSamplerParameteri(textureSampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glSamplerParameteri(textureSampler, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glSamplerParameteri(textureSampler, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
		depthSampler = glGenSamplers();
		glSamplerParameteri(depthSampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glSamplerParameteri(depthSampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glSamplerParameteri(depthSampler, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glSamplerParameteri(depthSampler, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        
		
		
		
		
		
		
		
		
		

		glEnable(GL_TEXTURE_2D);
		
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER,frameBuffer);
		
		//create Texture

		renderedTexture = glGenTextures();
	
		glBindTexture(GL_TEXTURE_2D, renderedTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height,0,GL_RGB,GL_UNSIGNED_BYTE,(java.nio.ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glBindSampler(0,textureSampler);
		glBindTexture(GL_TEXTURE_2D,0);
		
		
		
		
		
		
		
		
		
		depthTexture = glGenTextures();
	
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height,0,GL_DEPTH_COMPONENT,GL_FLOAT,(java.nio.ByteBuffer) null);
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE );
		glTexParameteri( GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_LUMINANCE );
		glBindSampler(1,depthSampler);
		glBindTexture(GL_TEXTURE_2D,0);
		
		
		
		//configure framebuffer
		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,renderedTexture,0);
		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,defDepthTexture,0);

		
		
		drawBuffs = BufferUtils.createIntBuffer(2);
		drawBuffs.put(0, GL_COLOR_ATTACHMENT0);
		drawBuffs.put(1, GL_DEPTH_ATTACHMENT);
		
	
		glDrawBuffers(drawBuffs);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER)!= GL_FRAMEBUFFER_COMPLETE)
		{
		System.out.print("GL_FRAMEBUFFER error.");	
		}
		
		glBindFramebuffer(GL_FRAMEBUFFER,0);
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
		fbo2 = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER,fbo2);
		
		
		//configure framebuffer
		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,renderedTexture,0);
		
		glFramebufferTexture(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,depthTexture,0);

		
		
		drawBuffs = BufferUtils.createIntBuffer(2);
		drawBuffs.put(0, GL_COLOR_ATTACHMENT0);
		drawBuffs.put(1, GL_DEPTH_ATTACHMENT);
		
	
		glDrawBuffers(drawBuffs);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER)!= GL_FRAMEBUFFER_COMPLETE)
		{
		System.out.print("GL_FRAMEBUFFER error.");	
		}
		
		glBindFramebuffer(GL_FRAMEBUFFER,0);
		
	
		
	}
	
	

	
	/*
	 * Erstelle einen Intbuffer mit der Größe für das Tile Culling
	 */
	public IntBuffer createIntBufferForTiled(int size)
	{
		
		
		IntBuffer iB = BufferUtils.createIntBuffer(size);
		int b[] = new int[size];
		
		for(int i = 0; i < b.length; i++ ) b[i] = 0;
		
		iB.put(b);
		iB.flip();
		return iB;
		
	}
	

	


	/*
	 * Erstelle einen Floatbuffer mit der Größe für das Z-Culling
	 */
	public FloatBuffer createFloatZBufferForTiled(int size)
	{
		
		FloatBuffer iB = BufferUtils.createFloatBuffer(size);
		float b[] = new float[size];
		
		for(int i = 0; i < b.length; i +=2  ){
			b[i] = 1.0f; 
			b[i+1] = 0.0f;
		}
		
		iB.put(b);
		iB.flip();
		return iB;
		
	}
	
	
	
	/*
	 * Erstelle einen Bytebuffer mit der Größe und der angegebenen Zahl für das Tile Culling
	 */
	public ByteBuffer createByteBufferForTiled(int size, int j)
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
	
	
	/*
	 * 
	 * Übergebe alle benötigten Ints für die Tile-Culling-Algorithmen
	 * 
	 */
	public void setUpTileInts(Algo AL)
	{
		AL.ComputeTile.bind();
		
		AL.setInts(bindingPointInfB, infoSSBO, bindingPointIndB, indexSSBO, bindingPointInfSubB, infoSubSSBO, bindingPointIndSubB, indexSubSSBO, bindingPointInfSubB2, infoSub2SSBO, bindingPointIndSubB2, indexSub2SSBO, width, height, tileWidth, tileHeight, tileWidthSub, tileHeightSub, tileWidthSub2, tileHeightSub2, MAX_PLIGHT, maxLightsPerTile);
		AL.setTestInt(tileTestW0, tileTestH0, infoSSBOTest, indexSSBOTest, tileTestW1, tileTestH1, tileTestW2, tileTestH2, tileTestW3, tileTestH3, infoSSBOTest2, indexSSBOTest2);
				
		AL.ComputeTile.unbind();
	}
	
	/*
	 * Bereite alle SSBO die für das Tiled Culling nötig sind
	 */
	public void setupTiledSSBO()
	{
		
		
		
		infB = createIntBufferForTiled((width/tileWidth) * (height/tileHeight) * 2);
		System.out.println("width/tileWidth" + width/tileWidth);
		System.out.println("width" + width);
		System.out.println("tileWidth" + tileWidth);
		//infoSSBO
		infoSSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,infoSSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, infB,
					GL_STREAM_DRAW
					);
		
		
		indB =createIntBufferForTiled((width/tileWidth) * (height/tileHeight) * maxLightsPerTile);
		//indexSSBO
		indexSSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,indexSSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, indB,
					GL_STREAM_DRAW
					);
		
		infSubB = createIntBufferForTiled((width/tileWidthSub) * (height/tileHeightSub) * 2);
		//infoSubSSBO
		infoSubSSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,infoSubSSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, infSubB,
					GL_STREAM_DRAW
					);
		
		indSubB = createIntBufferForTiled((width/tileWidthSub) * (height/tileHeightSub) * maxLightsPerTile);
		//indexSubSSBO
		indexSubSSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,indexSubSSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, indSubB,
					GL_STREAM_DRAW
					);
		
		
		
		infSubB2 = createIntBufferForTiled((width/tileWidthSub2) * (height/tileHeightSub2) * 2);
		//infoSubSSBO
		infoSub2SSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,infoSub2SSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, infSubB2,
					GL_STREAM_DRAW
					);
		
		indSubB2 = createIntBufferForTiled((width/tileWidthSub2) * (height/tileHeightSub2) * maxLightsPerTile);
		//indexSubSSBO
		indexSub2SSBO = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,indexSub2SSBO);
		glBufferData(	
					GL43.GL_SHADER_STORAGE_BUFFER, indSubB2,
					GL_STREAM_DRAW
					);
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		infBTest = createIntBufferForTiled((width/tileTestW3) * (height/tileTestH3) * 2);
		infoSSBOTest = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,infoSSBOTest);
		glBufferData(
				GL43.GL_SHADER_STORAGE_BUFFER,infBTest,
				GL_STREAM_DRAW
				);
		
		
		indBTest = createIntBufferForTiled((width/tileTestW3) * (height/tileTestH3) * maxLightsPerTile);
		indexSSBOTest = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,indexSSBOTest);
		glBufferData(
				GL43.GL_SHADER_STORAGE_BUFFER,indBTest,
				GL_STREAM_DRAW
				);
		
		infBTest2 = createIntBufferForTiled((width/tileTestW3) * (height/tileTestH3) * 2);
		infoSSBOTest2 = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,infoSSBOTest2);
		glBufferData(
				GL43.GL_SHADER_STORAGE_BUFFER,infBTest2,
				GL_STREAM_DRAW
				);
		
		indBTest2 = createIntBufferForTiled((width/tileTestW3) * (height/tileTestH3) * maxLightsPerTile);
		indexSSBOTest2 = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,indexSSBOTest2);
		glBufferData(
				GL43.GL_SHADER_STORAGE_BUFFER,indBTest2,
				GL_STREAM_DRAW
				);
		
		
		
				
		zMinMaxB = createFloatZBufferForTiled((width/tileWidth) * (height/tileHeight) * 2);
		zMinMaxBuf = glGenBuffers();
		glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER,zMinMaxBuf);
		glBufferData(
				GL43.GL_SHADER_STORAGE_BUFFER,zMinMaxB,
				GL_STREAM_DRAW
				);
		

				
		
		
		byteZero = createByteBufferForTiled(1, 0);
	
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfB, infoSSBO);
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndB, indexSSBO);
		

		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB, infoSubSSBO);
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB, indexSubSSBO);

		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointInfSubB2, infoSub2SSBO);
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointIndSubB2, indexSub2SSBO);
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPointzMinMax, zMinMaxBuf);
		

		
		
	}

	/*
	 * Leere den angegebenen SSBO und setze ihn an den bindingpoint
	 */
	public void clearBuffer(int bindingPoint, int SSBO)
	{
		glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, 4, SSBO);
		
		ARBClearBufferObject.glClearBufferData(GL42.GL_ATOMIC_COUNTER_BUFFER, GL_R32I,GL_RED,GL_INT, byteZero);
		
		
		glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPoint, SSBO);
		
	}
	

	/*
	 * Leere den angegebenen SSBO
	 */
	public void clearBuffer(int SSBO)
	{
		glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, 4, SSBO);
		
		ARBClearBufferObject.glClearBufferData(GL42.GL_ATOMIC_COUNTER_BUFFER, GL_R32I,GL_RED,GL_INT, byteZero);
		
		
	}


	
	/*
	 * Zeichne den zweiten Pass des Tile Deferred
	 */
	public void drawTDPSP(){
		
		TDP.computeTiles(ZCULLING,TiledMode, defDepthTexture);
		TDP.prepareDrawSP(RasterMode, defDepthTexture, normalTexture, diffuseTexture, lightTexture);
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		TDP.drawSP(4);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		
		
		
	}
	
	
	
	/*
	 * Erstelle das Licht-Uniform Buffer Object
	 */
	public FloatBuffer createPLightUBO()
	{
		
		int count = 
				(
				3 + 1+ 	//position //die 1-er sind für alignment
				3 + 1+	//ambient //w is quad
				3 + 1+	//diff		//w is lin
				3 + 1	//spec		//w is const
				);
		
		int size = count * MAX_PLIGHT;
		
		FloatBuffer pLightData = BufferUtils.createFloatBuffer(size);
		
		float f[] = new float[size];
		
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			
			f[i * count + 0] = pLightA[i].getPos().x;
			f[i * count + 1] = pLightA[i].getPos().y;
			f[i * count + 2] = pLightA[i].getPos().z;
			
			f[i * count + 3] = pLightA[i].distance;
			
			f[i * count + 4] = pLightA[i].ambient.x;
			f[i * count + 5] = pLightA[i].ambient.y;
			f[i * count + 6] = pLightA[i].ambient.z;
		
			f[i * count + 7] = pLightA[i].attenQuad;
			
			f[i * count + 8] = pLightA[i].diff.x;
			f[i * count + 9] = pLightA[i].diff.y;
			f[i * count + 10] = pLightA[i].diff.z;
			
			f[i * count + 11] = pLightA[i].attenLin;
			
			f[i * count + 12] = pLightA[i].specular.x;
			f[i * count + 13] = pLightA[i].specular.y;
			f[i * count + 14] = pLightA[i].specular.z;
			
			f[i * count + 15] = pLightA[i].attenConst;
		
			
		}
		
		pLightData.put(f);
		pLightData.flip();
		
		
		return pLightData;
				
	}
	
	/*
	 * Update die Position der Lichter im Licht-Uniform Buffer Object vor
	 */
	public void updatePosPLightUBO()
	{
		glBindBuffer(GL_UNIFORM_BUFFER, pLightBufferID);
		
		long offset = 0;
		
		for(int i = 0; i < MAX_PLIGHT; i++)	
		{
			
			offset = i * 16 * 4;
			float array[] = {pLightA[i].getPos().x,pLightA[i].getPos().y,pLightA[i].getPos().z};
			
			g_FloatB.put(array);
			g_FloatB.flip();
			
			glBufferSubData(GL_UNIFORM_BUFFER,offset, g_FloatB );
			
			g_FloatB.clear();
			
		}
		
	}
	
	
	/*
	 * Folgenden drei Funktionen verändern Position der drei Lichter je nach Einstellung
	 * 
	 */
	public void setLightsOrder3(int x, int y, int z, Vector3f startPos)
	{

		for(int i = 0; i < MAX_PLIGHT;i++)
		{
			
			
			
			
			float fx = startPos.x + XSTEP3 * (int)(i % x);
			float fz = startPos.z + ZSTEP3 * (((int)(i/x))%(z));
			float fy = startPos.y + YSTEP3 * (int)(i/(x*z));
			pLightA[i].setPos(new Vector3f(fx,fy,fz));
			System.out.println(i + " " + (((int)(i/x))%(z)) );
			
			
		}
		updatePosPLightUBO();
		
		
		
	}
	
	public void setLightsOrder1(int x, int y, int z, Vector3f startPos)
	{
	
		for(int i = 0; i < MAX_PLIGHT;i++)
		{
			
			
			
			
			float fx = startPos.x + XSTEP * (int)(i % x);
			float fz = startPos.z + ZSTEP * (((int)(i/x))%(z));
			float fy = startPos.y + YSTEP * (int)(i/(x*z));
			pLightA[i].setPos(new Vector3f(fx,fy,fz));
			System.out.println(i + " " + (((int)(i/x))%(z)) );
			
			
		}
		updatePosPLightUBO();
		
	}
	
	public void setLightsOrder2(int x, int y, int z, Vector3f startPos)
	{
	
		for(int i = 0; i < MAX_PLIGHT;i++)
		{
			
			
			
			
			float fx = startPos.x + XSTEP2 * (int)(i % x);
			float fz = startPos.z + ZSTEP2 * (((int)(i/x))%(z));
			float fy = -150.0f;
			pLightA[i].setPos(new Vector3f(fx,fy,fz));
		//	System.out.println(i + " " + (((int)(i/x))%(z)) );
			
			
		}
		updatePosPLightUBO();
		
	}
	
	/*
	 * Ändere die Licht-Information aller Lichter
	 */
	public void changeLightStats(float constA, float linA, float quadA, float prozent)
	{
		
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			
			pLightA[i].attenConst = constA;
			pLightA[i].attenLin = linA;
			pLightA[i].attenQuad = quadA;
			pLightA[i].proz = prozent;
			pLightA[i].distance = pLightA[i].calcDistanceFromAtten();
			
			
		}
		
		updateAllLightUBO();
		
	}
	
	/*
	 * Ändere Licht Informationen eines Lichts
	 */
	public void changeLightStatsSingle(int i,float constA, float linA, float quadA, float prozent)
	{
		
		
		pLightA[i].attenConst = new Float(constA);
		pLightA[i].attenLin = new Float(linA);
		pLightA[i].attenQuad = new Float(quadA);
		pLightA[i].proz = new Float(prozent);
		pLightA[i].distance = pLightA[i].calcDistanceFromAtten();
			
			
		
		
		
		
	}
	
	/*
	 * Update die Licht-Uniform Buffer Object vor
	 */
	public void updateAllLightUBO()
	{
		
		glBindBuffer(GL_UNIFORM_BUFFER, pLightBufferID);
		
		long offset = 0;
		
		for(int i = 0; i < MAX_PLIGHT; i++)	
		{
			
			offset = i * 16 * 4;
			float array[] = {
					pLightA[i].getPos().x,pLightA[i].getPos().y,pLightA[i].getPos().z,
					pLightA[i].distance,
					pLightA[i].ambient.x, pLightA[i].ambient.y,pLightA[i].ambient.z,
					pLightA[i].attenQuad,
					pLightA[i].diff.x,pLightA[i].diff.y,pLightA[i].diff.z,
					pLightA[i].attenLin,
					pLightA[i].specular.x,pLightA[i].specular.y,pLightA[i].specular.z,
					pLightA[i].attenConst};
			
			g_FloatLightAllB.put(array);
			g_FloatLightAllB.flip();
			
			glBufferSubData(GL_UNIFORM_BUFFER,offset, g_FloatLightAllB );
			
			g_FloatLightAllB.clear();
			
		}
		
	}

	/*
	 * Bereite die Licht-Uniform Buffer Object vor
	 */
	public void setUpPLightUBOProg(ShaderProgram prog)
	{
		
		
		pLightBufferID = glGenBuffers();
		
		glBindBuffer(GL_UNIFORM_BUFFER, pLightBufferID);
		
		int bindingPoint = 1;
		int blockIndex = glGetUniformBlockIndex(prog.getID(), "pl");
		glUniformBlockBinding(prog.getID(), blockIndex, bindingPoint);
		
			
		pLData = createPLightUBO();
		
		glBufferData(GL_UNIFORM_BUFFER,pLData, GL_DYNAMIC_DRAW);
		
		glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, pLightBufferID);
		
		
		
		glBufferSubData(GL_UNIFORM_BUFFER,0, pLData);
		
		
		
		
		
		
	}

	
	
	/*
	 * Randomisiere alle Lichterinformationen
	 */
	public void lightReshuffle()
	{
		
		float ambfactor = 0.1f;
		float specfactor = 0.2f;
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			
			pLightA[i].diff = new Vector3f(random(0f,1.5f),random(0f,1.5f),random(0f,1.5f));
			pLightA[i].setPos(new Vector3f(random(SPHERE_POS_MIN - 50,SPHERE_POS_MAX + 100),random(SPHERE_POS_Z_MIN,SPHERE_POS_Z_MAX),random(SPHERE_POS_MIN - 50,SPHERE_POS_MAX + 100)));
			pLightA[i].ambient = new Vector3f(ambfactor * pLightA[i].diff.x,ambfactor*pLightA[i].diff.y,ambfactor*pLightA[i].diff.z);
			pLightA[i].specular = new Vector3f(specfactor * pLightA[i].diff.x,specfactor*pLightA[i].diff.y,specfactor*pLightA[i].diff.z);
		}
		
	}
	

	/*
	 * Gib Zeit zurück, Millisekunden
	 */
	static public long getTime()
	{
		
		return System.nanoTime() / 1000000;
		
		
	}
	/*
	 * Gib Zeit zurück, Nanosekunden
	 */
	static public long getTimeD()
	{
		
		return System.nanoTime() / 1000 ;
		
		
	}
	
	
	/*
	 * Gib Delta-Zeit zurück
	 */
	public float getDelta()
	{
		
		
		long time = getTime();
		float delta = (float) (time - lastFrame);
		lastFrame = time;
		
		
		
		return delta;
		
		
		
	}
	
	
	
	/*
	 * Ändere Tile-Mode per Funktionsaufruf
	 * 
	 */
	public void nextTME(){
			try {
				next(TME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	public void prevTME(){
			
			try {
				prev(TME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
 	

	
	public void resized()
	{
		
		glViewport(0,0,Display.getWidth(), Display.getHeight());
		camera.changeAspect(Display.getWidth()/ Display.getHeight());	
	}
	
	
	
	
	/*
	 * 
	 * Die Input-Funktion, reagiert auf Tastenbefehle
	 */
	public void updateInput(float delta)
	{
		
		if(isKeyDown(Keyboard.KEY_ESCAPE))
		{
			
			
		}
		
		if(isKeyDown(Keyboard.KEY_COMMA))
		{
			if(lock250.isUnlocked())
			{
				
				show_Lights = !show_Lights;
				
			}
		}
		

		if(isKeyDown(Keyboard.KEY_RCONTROL))
		{
			if(lock250.isUnlocked())
			{
				
				System.out.println("Current Position: " + LOOK_IN_DIR);
				
			}
		}
		
		if(isKeyDown(Keyboard.KEY_5))
		{
			if(lock250.isUnlocked())
			{
				
				NORMALNUM += 1;
				NORMALNUM %= 2;
				
			}
		}
		
		if(isKeyDown(Keyboard.KEY_6))
		{
			if(lock250.isUnlocked())
			{
				
				TF_EARLYZ = !TF_EARLYZ;
				
			}
		}
		
		if(isKeyDown(Keyboard.KEY_7))
		{
			if(lock250.isUnlocked())
			{
				
				changeLightStats(1.0f,0.05f,0.001f, 200.0f);
				
				
			}
		}
		if(isKeyDown(Keyboard.KEY_8))
		{
			if(lock250.isUnlocked())
			{
				
				
				
			}
		}
		
		
		
		if(isKeyDown(Keyboard.KEY_0))
		{
			if(lock250.isUnlocked())
			{
				
				switch(KF)
				{	
					
				
				case Box:
					camera.setUpWithPForKnots(KLIST, BENCHMARKLENGTH);
					break;
					
				case PlaneAll:
					
					camera.setUpWithPForKnots(PLANEALL, BENCHMARKLENGTH);
					break;
				
				
				}
				camera.startBench();
				
			}
		}
		
		if(isKeyDown(Keyboard.KEY_1)){
			
			if(lock250.isUnlocked()){
				
				setLightsOrder1(XORDER1, YORDER1, ZORDER1, STARTPOS1);
				
			}
			
			
		}
		
		
		//System.out.println(glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS));
		if(isKeyDown(Keyboard.KEY_T))
 		{
			if(lock250.isUnlocked())
 			{
				showStrings = !showStrings;
 			}
 		}
		 
	 	if(isKeyDown(Keyboard.KEY_Z))
	 	{
 			if(lock250.isUnlocked())
 			{
 				
 				STARMODE = 1;
 				
 			}
 			
	 	}
	 		
 		if(isKeyDown(Keyboard.KEY_U))
 		{
 			if(lock250.isUnlocked())
 			{
 				STARMODE = 0;
 			}
 		}
	 		
	 		
 	
	 	
	 	
 		if(isKeyDown(Keyboard.KEY_J))
 		{
 			if(lock250.isUnlocked())
 			{
 				
 				TiledMode -= 1;
 				if(TiledMode < 0) TiledMode = MAX_TILED_MODE;
 				System.out.println(TiledModeA[TiledMode]);
 				
 			}
 			
 		}
 		
 		if(isKeyDown(Keyboard.KEY_K))
 		{
 			if(lock250.isUnlocked())
 			{
 				
 				TiledMode += 1;
 				if(TiledMode > MAX_TILED_MODE) TiledMode = 0;
 				System.out.println(TiledModeA[TiledMode]);
 			}
 			
 		}
 		
 		if(isKeyDown(Keyboard.KEY_R))
 		{
 			if(lock250.isUnlocked())
 			{
 				RasterMode += 1;
 				if(RasterMode > 1) RasterMode = 0;
 			}
 			
 		}
 		
 		
 		
 		
 		if(isKeyDown(Keyboard.KEY_Y))
 		{
 			if(lock250.isUnlocked())
 			{
 				ZCULLING = !ZCULLING;
 			}
 			
 		}
 		
	 		
	
	 	
	 	if(isKeyDown(Keyboard.KEY_SPACE))
	 	{
	 		
	 		
	 		camera.setPosition ( new Vector3f(0,250,-1000));
			camera.lookInDirection(new Vector3f(0,0,1));
	 		
	 		
	 	}
	 	
	 	if(isKeyDown(Keyboard.KEY_RSHIFT))
	 	{
	 		
	 		Matrix4f d = camera.getView();
	 		
	 		Matrix4f.mul(camera.getProj(), d, d);
	 		
	 		Vector4f a = new Vector4f(sphereTest.x,sphereTest.y,sphereTest.z,1.0f);
	 		
	 		Vector4f b = MatrixUtil.mul(a, d);
	 		
	 	//	System.out.println(d.toString());
	 		System.out.println(b.toString());
	 		
	 	}
	 
	 
	 	if(isKeyDown(Keyboard.KEY_LSHIFT))
	 		speed = MAX_SPEED * (delta + 1 /1000.0f);
	 	else if (isKeyDown(Keyboard.KEY_LCONTROL))
	 			speed = MIN_SPEED *(delta + 1 /1000.0f);
	 	else speed = (delta + 1 /1000.0f);
	 	
       

        // Look up
        if (isKeyDown(KEY_UP))
            camera.addRotation(-1f, 0, 0);

        // Look down
        if (isKeyDown(KEY_DOWN))
            camera.addRotation(1f, 0, 0);

        // Turn left
        if (isKeyDown(KEY_LEFT))
            camera.addRotation(0, -1f, 0);

        // Turn right
        if (isKeyDown(KEY_RIGHT))
            camera.addRotation(0, 1f, 0);

        // Move front
        if (isKeyDown(KEY_W))
            camera.move(speed * -0.1f, 1);
        
    
  //      System.out.println("x: " + target.x + " y: " + target.y + " z: " + target.z + "\n");
     
        // Move back
        if (isKeyDown(KEY_S))
        	camera.move(speed * 0.1f,1);

        // Strafe left
        if (isKeyDown(KEY_A))
        	camera.move(speed * -0.1f,0);

        // Strafe right
        if (isKeyDown(KEY_D))
        	camera.move(speed * 0.1f,0);
        
        
        
        if (isKeyDown(KEY_L))
        {
        	if(lock250.isUnlocked())
 			{
        		prevShader();
	        	System.out.println(shade);
 			}
        }
        
        
        if (isKeyDown(KEY_P))
        {
        	if(lock250.isUnlocked())
 			{
        		nextShader();
	        	System.out.println(shade);
 			}
        	
        }
        
        if(isKeyDown(KEY_B))
        	allLightBack(amount * speed);
        if(isKeyDown(KEY_G))
        	allLightFor(amount * speed);
        if(isKeyDown(KEY_N))
        	allLightRight(amount * speed);
        if(isKeyDown(KEY_V))
        	allLightLeft(amount * speed);
        if(isKeyDown(KEY_F))
        	allLightDown(amount * speed);
        if(isKeyDown(KEY_H))
        	allLightUp(amount * speed);
        
        if(isKeyDown(KEY_X))
        	if(lock250.isUnlocked())
        		lightReshuffle();
        
        
        if(isKeyDown(KEY_Q))
        	camera.up(speed * 0.1f, false);
        if(isKeyDown(KEY_E))
        	camera.up(speed * 0.1f, true);
        
        switch(LM)
        {
        
        case Still:
        	
        	break;
        	
        case InCircle:
        	allLightMoveCircleSinCos(delta, new Vector3f(20,0,0),new Vector3f(0,0,20));
        	break;
        	
        case InCircleUpDown:
        	allLightMoveCircleSinCos(delta, new Vector3f(20,15,0),new Vector3f(0,0,20));
        	break;
        
        
        }
   //     allLightMoveCircleSin(delta, new Vector3f(50,50,0));
   //     allLightMoveCircleCos(delta, new Vector3f(0,0,50));
   //     allLightMoveCircleSinCos(delta, new Vector3f(20,30,0),new Vector3f(0,0,20));
    }
	
	
	
	/*
	 * 
	 * Die nächsten paar Funktionen sind nur so da um Lichter bewegen zu können
	 * 
	 * 
	 * 
	 */
	
	public float sin(float t)
	{
		
		return (float) Math.sin((double) t);
		
	}
	
	public float cos(float t)
	{
		
		return (float) Math.cos((double) t);
		
	}
	
	public void allLightMoveCircleSin(float amount, Vector3f vec)
	{
		passed += amount;
		float a = passed / CircleConst;
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].delta.x = sin(a) * vec.x;
			pLightA[i].delta.y = sin(a) * vec.y;
			pLightA[i].delta.z = sin(a) * vec.z;
		}
	}
	
	public void allLightMoveCircleCos(float amount, Vector3f vec)
	{
		passed += amount;
		float a = passed / CircleConst;
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].delta.x = cos(a) * vec.x;
			pLightA[i].delta.y = cos(a) * vec.y;
			pLightA[i].delta.z = cos(a) * vec.z;
		}
	}
	public void allLightMoveCircleSinCos(float amount , Vector3f v1, Vector3f v2)
	{
		passed += amount;
		float a = passed / CircleConst;
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].delta.x = sin(a) * v1.x + cos(a) * v2.x;
			pLightA[i].delta.y = sin(a/5) * v1.y + cos(a) * v2.y;
			pLightA[i].delta.z = sin(a) * v1.z + cos(a) * v2.z;
		}
		
		
	}

	public void allLightFor(float amount){
		
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setZ(pLightA[i].getPos().z- amount);
		}
		
		
				
	}
	
	
	
	public void allLightBack(float amount){
		
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setZ(pLightA[i].getPos().z+ amount);
		}
		
		
		
				
	}
	
	
	
	
	public void allLightUp(float amount){

		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setY(pLightA[i].getPos().y+ amount);
			if (TS != TestScenes.CrytekBuilding && pLightA[i].getPos().y > ROOF) pLightA[i].setY( GROUND);
		}
		
		

		
	}
	public void allLightDown(float amount){
		
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setY(pLightA[i].getPos().y- amount);
			if (TS != TestScenes.CrytekBuilding && pLightA[i].getPos().y < GROUND) pLightA[i].setY(ROOF);
		}
		
		
	}
	
	
	public void allLightLeft(float amount){
		
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setX(pLightA[i].getPos().x- amount);
		}
				
		
		
	
		
		
	}
	 
	public void allLightRight(float amount){
	
		for(int i = 0; i < pLightA.length; i++)
		{
			pLightA[i].setX(pLightA[i].getPos().x+ amount);
		}
		
		
				
	}
	 
	/*
	 * Wechsel zwischen den Algorithmen, und ändere dabei die Einstellungen
	 */
	public void switchBetweenRenders()
	{
		Matrix4f normM;
		normM = createInverseTranspose(camera.view());
		

		Matrix4f normMLight = new Matrix4f(normM);
		
		
		Vector3f camPos;
		
		camPos = camera.getPosition();
		
		
		switch (shade)
		{
		
		case EarlyZ:
			EAZ.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA, normM,camPos,WORLD_AMBIENT);
			EAZ.setStar(STARMODE);
			break;
		

		case PerPixel:
			
			PFF.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM,camPos,WORLD_AMBIENT);
			PFF.FPprogram.bind();
			PFF.FPprogram.setInt("Rock", 4);
			PFF.FPprogram.setInt("norTexUse", NORMALNUM);
			PFF.setStar(STARMODE);
			PFF.FPprogram.unbind();
			break;
			
		case TiledForwardPar:
			
			TFP.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,normM,camPos,WORLD_AMBIENT);
			TFP.setRasterMode(RasterMode);
			TFP.setStar(STARMODE);
			
			break;
		case DefRen:
		case DefRenTest:
			
			
			DF.prepareSP(camera.getView(), null, normMLight, pLightA,null, camPos, WORLD_AMBIENT);
			DRT.prepareSP(camera.getView(), null, normMLight, pLightA,  null, camPos, WORLD_AMBIENT);
			glBindFramebuffer(GL_FRAMEBUFFER, deferredRenFBO);
			
			DF.setStar(STARMODE);
			DRT.setStar(STARMODE);
			
			break;
			
			
		
		case DefSphereCul:
			
			DFSP.prepareSP(camera.getView(), null, normMLight, pLightA, null, camPos, WORLD_AMBIENT);
			glBindFramebuffer(GL_FRAMEBUFFER, deferredRenFBO);
			
			DFSP.setStar(STARMODE);
			
			
			
			break;
		
		case DefSphereStencil:
			

			DSS.prepareSP(camera.getView(), null, normMLight, pLightA,  null, camPos, WORLD_AMBIENT);
			glBindFramebuffer(GL_FRAMEBUFFER, deferredRenFBO);
			
			DSS.setStar(STARMODE);
			
			
			
			break;
		
		
		case TiledDefPar:
			
			TDP.prepareSP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM, camPos, WORLD_AMBIENT);
			checkE(true);
			glBindFramebuffer(GL_FRAMEBUFFER, deferredRenFBO);
			TDP.setStar(STARMODE);
			
			//rastermode wird schon woanders gesetzt
			
			
			break;
		

		
		}
		
		TDP.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM, camPos, WORLD_AMBIENT);
		DF.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM,camPos,WORLD_AMBIENT);
		DFSP.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM,camPos,WORLD_AMBIENT);
		DSS.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM,camPos,WORLD_AMBIENT);
		DRT.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA, normM,camPos,WORLD_AMBIENT);
		CamAlgo.prepareFP(camera.getView(), camera.getProj(), normMLight, pLightA,  normM,camPos,WORLD_AMBIENT);
		
		ShaderProgram.unbind();
		
	
	}
	/*
	 * Zeichne "Test", d.h., Tiefen-, Normal-, Diffuse-,Endbild
	 */
	public void drawDRT()
	{
		
		DRT.prepareDrawSP(height, width, defDepthTexture, normalTexture, diffuseTexture, lightTexture, fbo2);
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		
		DRT.drawSP(4);
		

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
		
		
		
		
		
		
		glActiveTexture(GL_TEXTURE0);
		
		
		
		
		
		DRT.RendTextP.setInt("Texture", 0); 
		checkE(false);
		
		Matrix4f view = camera.view();	
		
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		DRT.prepareDrawRend(width, height, ortho);
		
		
		
		

		glBindVertexArray(vaoTestID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glBindTexture(GL_TEXTURE_2D,defDepthTexture);
		Vector3f transl = new Vector3f(0,0,0);
		
		DRT.setTranslRend(transl);
		DRT.setModeRend(4);
		DRT.drawRend();
		
	
		
		
		glBindTexture(GL_TEXTURE_2D,renderedTexture);
		transl = new Vector3f(width/2,0,0);
		
		DRT.setTranslRend(transl);
		DRT.setModeRend(mode);
		DRT.drawRend();
		
		
		
		
		glBindTexture(GL_TEXTURE_2D,normalTexture);
		transl = new Vector3f(0,height/2,0);
		
		DRT.setTranslRend(transl);
		DRT.drawRend();
		
		
		glBindTexture(GL_TEXTURE_2D,diffuseTexture);
		transl = new Vector3f(width/2,height/2,0);
		DRT.setTranslRend(transl);
		DRT.drawRend();
		
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
		transl = new Vector3f(0,0,0);
		
		DRT.setTranslRend(transl);
		ShaderProgram.unbind();
		
		
	}
	
	
	
	
	
	/*
	 * Zeichne Tiefenpass für Tiled Forward Crytek-Szene
	 */
	public void drawEarlyZTFCrytek(TiledForwardPar AL)
	{
		
		
		

		
		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		AL.getDP().bind();
		
		
		long c = getTimeD();
		
		
		
		for(Model m : sponzaScene.Models)
		{
		if(m.count == 0) continue;
		
		
		
		glBindVertexArray(m.vao);
		glEnableVertexAttribArray(0);

		
		
		AL.drawDepthOnly(new Vector3f(0f,0f,0f), 1, m.count);
		
		
		glDisableVertexAttribArray(0);

		}
		
		
	}

	/*
	 * Zeichne Tiefenpass für Tiled Forward, alle Szenen
	 */
	public void prepIfEarlyZ(TiledForwardPar AL)
	{
		

		if(TF_EARLYZ)
		{
			glDepthMask(true);
			glColorMask(false,false,false,false);
			
			glDepthFunc(GL_LESS);
			if(ZCULLING)
			{
			
				glBindFramebuffer(GL_FRAMEBUFFER,fbo2);
			
			}
			else
			{
				
				glBindFramebuffer(GL_FRAMEBUFFER,0);
			}
			
		
			
			
			
			switch(TS)
			{
			
			case SpheresInBox:
				drawEarlyZTFSpheresInBox(AL);
				break;
			case SpheresOnPlane:
				drawEarlyZTFSpheresOnPlane(AL);
				break;
				
			case CrytekBuilding:
				drawEarlyZTFCrytek(AL);
				break;
				
			}
			
			
			
			glDepthMask(false);
			glColorMask(true,true,true,true);
			glDepthFunc(GL_EQUAL);
			
		}
		
	}

	/*
	 * Ändere Einstellungen nach Tiefenpass für Tiled Forward
	 */
	public void postIfEarlyZ(TiledForwardPar AL)
	{
		if(TF_EARLYZ)
		{
			glDepthMask(true);
			glDepthFunc(GL_LESS);
		
			if(ZCULLING)
			{
				
				
				Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
				
				AL.prepareDrawRend(width, height, ortho, renderedTexture);
				glBindVertexArray(vaoFSQuadID);
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				AL.drawToScreen();
				glDisableVertexAttribArray(0);
				glDisableVertexAttribArray(1);
				glBindVertexArray(0);
				
			}
			
			
		}
		
		
	}
	
	

	/*
	 * Zeichne Tiefenpass für Forward Crytek-Szene
	 */
	public void drawEarlyZCrytek()
	{
		

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		EAZ.getDP().bind();
		
		
	
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		long c = getTimeD();
		//draw Spheres
		
		
		for(Model m : sponzaScene.Models)
		{
		if(m.count == 0) continue;
		
	
		glBindVertexArray(m.vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
	
		
		
		EAZ.drawDepthOnly(new Vector3f(0f,0f,0f), 1, m.count);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		}
		
		
		
	}
	
	/*
	 * Renderfunktion
	 */
	public void render()
	{
		
	
		long b = getTimeD();
		updatePosPLightUBO();
		
		if(Mouse.isButtonDown(1)){
			if(lock250.isUnlocked())
			{
				if(!MOUSEGRAB){
					mousePrevx = Mouse.getX();
					mousePrevy = Mouse.getY();
				}
				MOUSEGRAB = !MOUSEGRAB;
				
			}
		}
		
		if(MOUSEGRAB){
			Mouse.setGrabbed(true);
			
			
		
		}
		else Mouse.setGrabbed(false);
		
		


		float delta = getDelta();

		for(int i = 0; i < lockA.length; i++)
		{
			lockA[i].update(delta);
		}
		
		updateInput(delta);
		
		camera.updatePosBench(delta);
		if(camera.start)
		{
			switch(CAMERA_LOOK_AT)
			{
			
			case Position:
				camera.lookAt(LOOK_AT_POSITION);
				
				break;
				
				
			case Direction:
				Vector3f desttt = new Vector3f();
				LOOK_IN_DIR.normalise(desttt);
				camera.lookInDirection(desttt);
				
				break;
				
				
			case MovingDir:
				
				camera.lookInDirection(camera.getDirFromBench());
				
				break;
				
			
			
			}
			
			
		}
		
		FRAME_SECOND += delta;
		
		fElapsedTime += delta;
		
		if(Mouse.isGrabbed()){
			
			mouseX = Mouse.getX();					
			mouseDx = mouseX - mousePrevx;
			mousePrevx = mouseX;
			
			
			mouseY = Mouse.getY();
			mouseDy = mouseY - mousePrevy;
			mousePrevy = mouseY;
			
			camera.rotateByMouse(mouseDx, mouseDy);
		
		
			
		
		}
		camera.apply();
	
		
		
			
		
		
		glBindFramebuffer(GL_FRAMEBUFFER,0);
		
		switchBetweenRenders();
		
		checkE(true);
		switch(shade)
		{
	
			
		
		
		
			
		case TiledForwardPar:
			
			
			
			
			prepIfEarlyZ(TFP);	
			
			
			TFP.computeTiles(ZCULLING, TiledMode, depthTexture);
			
			
			
			
			drawAlgo(TFP);
			
			
			
			
			
			postIfEarlyZ(TFP);
			
			
			break;
			
		
		case EarlyZ:
			
			glDepthMask(true);
			glColorMask(false,false,false,false);
			
			glDepthFunc(GL_LESS);
			
		
			
			
			switch(TS)
			{
			case SpheresInBox:
				
				drawEarlyZPSpheresInBox();
				break;
				
			case SpheresOnPlane:
				drawEarlyZSphereOnFarPlane();
				break;
				
			case CrytekBuilding:
				drawEarlyZCrytek();
				break;
			
			
			}
			
			
			glDepthMask(false);
			glColorMask(true,true,true,true);
			glDepthFunc(GL_EQUAL);
			
		
			
			
			
			
			drawAlgo(EAZ);
			
			
			
			
			
			
			glDepthMask(true);
			glDepthFunc(GL_LESS);
			
			
			break;
			
			
		case PerPixel:
			
			drawAlgo(PFF);
			
			
			break;
				
		case DefRenTest:

		
			
			drawAlgo(DRT);
		
			
			
			
			
			
			drawDRT();
			
		
			break;
			
			
		case DefRen:
					
			drawAlgo(DF);
		
			
			
			
			
			drawDFSP();
			
			break;
		
		case DefSphereCul:
			
			drawAlgo(DFSP);
		
			
			
			
			
			drawSPSP();
			
			break;
			
			
		case DefSphereStencil:
			
			
			drawAlgo(DSS);
		
			
			drawDSS();
		
			
			
			break;
			
		
			
			
		case TiledDefPar:
			drawAlgo(TDP);
		
			
			
			checkE(true);
			
			drawTDPSP();

			checkE(true);
			break;
			
		
			
			
		}
		
		
	
		
		
	}
	
	
	/*
	 * Setze Programmparameter für übergebenes Programm für Deferred Rendering
	 */
	public void setProgramParamDefS(ShaderProgram prog)
	{

		
		
		glEnable(GL_TEXTURE_2D);
		
		
		Matrix4f proj = camera.getProj();
		
		
		
		
		float A = proj.m00;
		float B = proj.m11;
		float C = proj.m22;
		float D = proj.m32;
		
		prog.setFloat("A", A);
		prog.setFloat("B", B);
		prog.setFloat("C", C);
		prog.setFloat("D", D);
		
		
		glActiveTexture(GL_TEXTURE0+1);
		glBindTexture(GL_TEXTURE_2D, defDepthTexture);
		prog.setInt("depthTex", 1);
		
		glActiveTexture(GL_TEXTURE0+2);
		glBindTexture(GL_TEXTURE_2D, normalTexture);
		prog.setInt("normalTex", 2);
		
		glActiveTexture(GL_TEXTURE0+3);
		glBindTexture(GL_TEXTURE_2D, diffuseTexture);
		prog.setInt("diffTex", 3);

		
		
		
		
		
		
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,0);
		
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		prog.setUniformMatrix4f("ortho", ortho);
		
		
		
	}
	
	
	/*
	 * Zeichne Deferred Rendering Zweiten Pass (FullscreenQuad)
	 */
	public void drawDFSP()
	{
		
		DF.prepareDrawSP(height, width, defDepthTexture, normalTexture, diffuseTexture,lightTexture);
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		DF.drawSP(4);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
	}
	
	
	/*
	 * Zeichne Deferred Rendering - Stencil
	 */
	public void drawDSS()
	{
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		DSS.prepareDrawSP(height,width,camera.getView(), camera.getProj(), seperateDepthTexture, normalTexture, diffuseTexture, lightTexture, frameBuffer);
	

		
		DSS.prepareStencilPass();
		glColorMask(false,false,false,false);
		
	
		glDisableVertexAttribArray(0);
		glBindVertexArray(vaoLSID);
		glEnableVertexAttribArray(0);
		
		glEnable(GL_STENCIL_TEST);
		glCullFace(GL_FRONT);
		glDepthFunc(GL_GEQUAL);
		glDepthMask(false);
		
		
		
		glEnable(GL_DEPTH_TEST);
		
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			
			
			glColorMask(false,false,false,false);
			
			if( i%255 == 0) glClear(GL_STENCIL_BUFFER_BIT);
			
			DSS.prepareStencilPass();
		
			glCullFace(GL_BACK);
			glStencilFunc(GL_NOTEQUAL,((i)%255)+1,0b11111111);
		
			glStencilOp(GL_KEEP,GL_KEEP,GL_REPLACE);
			
			DSS.drawStencilPass(pLightA[i].getPos(), pLightA[i].calcDistanceFromAtten()*FIZZLE, sphere);
		
			glCullFace(GL_FRONT);
			glStencilOp(GL_KEEP,GL_KEEP,GL_KEEP);
			glStencilFunc(GL_NOTEQUAL,((i)%255)+1,0b11111111);
			
			
			
			DSS.SPprogram.bind();
			glColorMask(true,true,true,true);
		
			DSS.drawSP(pLightA[i].getPos(), pLightA[i].calcDistanceFromAtten()*FIZZLE, pLightA[i].diff, i, sphere);
		
			
			
		}
		
		glColorMask(true,true,true,true);
		glDepthFunc(GL_LEQUAL);
		glDepthMask(true);
		glDisable(GL_STENCIL_TEST);
		glCullFace(GL_BACK);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		
		
		DSS.prepdrawAmbLight(width, height);
		DSS.drawAmbLight(AMBIENTCOLOR);
		DSS.SPprogram.bind();
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		if(show_Lights)
		{
			DSS.prepareDrawSPLights();
		
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				
				DSS.drawSPLights(pLightA[i].getPos(), LIGHT_SIZE, pLightA[i].diff, i, sphere);
				
			}
		}
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		DSS.endDrawSP(renderedTexture, width, height);
		
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		glEnable(GL_TEXTURE_2D);
		
		
	
	
		glBindTexture(GL_TEXTURE_2D,renderedTexture);
		DSS.drawTexture();

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
	}
	
	/*
	 * Zeichne Deferred Rendering - Lightvolume ohne Stencil
	 * Zweiter Pass
	 */
	public void drawSPSP()
	{
		
		DFSP.prepareDrawSP(height,width,camera.getView(), camera.getProj(), defDepthTexture, normalTexture, diffuseTexture, lightTexture, frameBuffer);
		/*
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		*/
		
		
		glBindVertexArray(vaoLSID);
		glEnableVertexAttribArray(0);
		
		
		for(int i = 0; i < MAX_PLIGHT; i++)
		{
			
		//	float a = pLightA[i].calcDistanceFromAtten();
		//	System.out.println(a);
			DFSP.drawSP(pLightA[i].getPos(), pLightA[i].calcDistanceFromAtten()*FIZZLE, pLightA[i].diff, i, sphere);
	
		}
		
		
		glDepthFunc(GL_LEQUAL);
		glDepthMask(true);
		glDisable(GL_STENCIL_TEST);
		glCullFace(GL_BACK);
		
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		
		
		DFSP.prepdrawAmbLight(width, height);
		DFSP.drawAmbLight(AMBIENTCOLOR);
		DFSP.SPprogram.bind();
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		
		if(show_Lights)
		{
			DFSP.prepareDrawSPLights();
		
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				
				DFSP.drawSPLights(pLightA[i].getPos(), LIGHT_SIZE, pLightA[i].diff, i, sphere);
				
			}
		}
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		glBindVertexArray(vaoFSQuadID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		
		
	
		
		DFSP.endDrawSP(renderedTexture, width, height);
		
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClearColor(0,0,1,1);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		
		glEnable(GL_TEXTURE_2D);
		
		
	
	
		glBindTexture(GL_TEXTURE_2D,renderedTexture);
		
		
		DFSP.drawTexture();
		/*
		RendTextP.bind();
		int g = glGetUniformLocation(RendTextP.getID(), "Texture");
		glUniform1i(g,0); // die Zahl wählt die Textur in GLACTIVETEXTURE aus
	
		
		Matrix4f view = camera.view();	
		
		Matrix4f ortho = MatrixUtil.createOrthogonalMatrix(0, 0 , height, width, 1,-1);
		
		RendTextP.setUniformMatrix4f("view", view);
		RendTextP.setUniformMatrix4f("projection", camera.getProj());
		RendTextP.setUniformMatrix4f("ortho", ortho);
		RendTextP.setInt("width", width);
		RendTextP.setInt("height", height);
		
		RendTextP.setInt("mode", 0);
		
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
		
		*/
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
		
		
	}
	
	/*
	 * Zeichne PerFragment Algo
	 */
	public void drawPFF()
	{
		
		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		
		
		
		
		
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		
		//draw Spheres
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			PFF.drawFP(SPHERE_POS[i], SPHERE_SIZE, sphere, SPHERE_COL[i]);
							
		}
		
		
		if(show_Lights)
		{
		//draw lightPos
		
		
		
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				Vector3f col = new Vector3f(pLightA[i].diff);
				col.scale(COL_SCALE);
				
				PFF.drawCam(pLightA[i].getPos(), LIGHT_SIZE, sphere, col);
				
			}
			
		
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glBindVertexArray(0);
			
		
		}
		
		
		
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		//unten
		PFF.drawFP(planePos, 1, plane, (Vector3f)null);
		//rechts
		PFF.drawFP(new Vector3f(500, 0,0),plane,(Vector3f)null, 0,0,90);
		//links
		PFF.drawFP(new Vector3f(-500, 0,0),plane,(Vector3f)null, 0,0,-90);
		//vorne
		PFF.drawFP(new Vector3f(0, 0, 500),plane,(Vector3f)null, -90, 0, 0);
		//hinten
		PFF.drawFP(new Vector3f(0, 0, -500),plane,(Vector3f)null, 90, 0, 0);
		//oben
		PFF.drawFP(new Vector3f(0,500,0),plane,(Vector3f)null, 180,0,0);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
	
	}
	


	/*
	 * Zeichne Earyl-Z(Tiled) für Box-Test
	 */
	public void drawEarlyZTFSpheresInBox(TiledForwardPar AL)
	{

		
		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		
		
		
		
		AL.getDP().bind();
		
		//TF.bindDText(depthTexture);		
		
		
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		long c = getTimeD();
		//draw Spheres
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			AL.drawDepthOnly(SPHERE_POS[i], SPHERE_SIZE, sphere);
							
		}
		//System.out.print(getTimeD() - c);
	//	System.out.print("Algo\n");
		
		
		//long c = getTimeD();
		if(show_Lights)
		{
		//draw lightPos
		
		
			
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				Vector3f col = new Vector3f(pLightA[i].diff);
				col.scale(COL_SCALE);
				
				AL.drawDepthOnly(pLightA[i].getPos(), LIGHT_SIZE, sphere);
				
			}
			
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glBindVertexArray(0);
			
		
		}
		//System.out.println(getTimeD() - c);
		
		
		AL.getDP().bind();
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		//unten
		AL.drawDepthOnly(planePos, 1, plane);
		//rechts
		AL.drawDepthOnly(new Vector3f(500, 0,0),plane, 0,0,90);
		//links
		AL.drawDepthOnly(new Vector3f(-500, 0,0),plane, 0,0,-90);
		//vorne
		AL.drawDepthOnly(new Vector3f(0, 0, 500),plane, -90, 0, 0);
		//hinten
		AL.drawDepthOnly(new Vector3f(0, 0, -500),plane, 90, 0, 0);
		//oben
		AL.drawDepthOnly(new Vector3f(0,500,0),plane, 180,0,0);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
		
	}

	/*
	 * Zeichne Earyl-Z(Tiled) für Flächen-Test
	 */
	public void drawEarlyZTFSpheresOnPlane(TiledForwardPar AL)
	{

		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		AL.getDP().bind();
		
		
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		long c = getTimeD();
		//draw Spheres
		
		
		
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			AL.drawDepthOnly(SPHERE_POS[i], SPHERE_SIZE, sphere);
							
		}
		//System.out.print(getTimeD() - c);
	//	System.out.print("Algo\n");
		
		
		//long c = getTimeD();
		if(show_Lights)
		{
		//draw lightPos
		
		
			
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				
				AL.drawDepthOnly(pLightA[i].getPos(), LIGHT_SIZE, sphere);
				
			}
			
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glBindVertexArray(0);
			
		
		}
		//System.out.println(getTimeD() - c);
		
		

		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		
		//unten
	
		AL.drawDepthOnly(planePos, 10, plane);
		
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
		
	}

	/*
	 * Zeichne Earyl-Z(Tiled) für Box-Test
	 */
	
	
	
	/*
	 * Zeichne Early-Z-Pass für Flächen-Test
	 */
	public void drawEarlyZSphereOnFarPlane(){
		
		
		
		
		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
	
	
	
		
		
		
		
		
		
		EAZ.getDP().bind();
		
		
	
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		long c = getTimeD();
		//draw Spheres
		
		
		
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			EAZ.drawDepthOnly(SPHERE_POS[i], SPHERE_SIZE, sphere);
							
		}
		//System.out.print(getTimeD() - c);
	//	System.out.print("Algo\n");
		
		//long c = getTimeD();
		if(show_Lights)
		{
		//draw lightPos
		
		
			EAZ.drawCamStart();
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				
				EAZ.drawDepthOnly(pLightA[i].getPos(), LIGHT_SIZE, sphere);
				
			}
			
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glBindVertexArray(0);
			
		
		}
		//System.out.println(getTimeD() - c);
		
		
		
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		
		
		//unten
		
		EAZ.drawDepthOnly(planePos, 10, plane);
		
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
		
	}
	
	
	
	/*
	 * Zeichne Early-Z-Pass für Box-Szene
	 */
	public void drawEarlyZPSpheresInBox()
	{

		
		
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	    glEnable(GL_DEPTH_TEST);
		
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		
		
		
		
		
		
		EAZ.getDP().bind();
		
		
		
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		long c = getTimeD();
		//draw Spheres
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			
			EAZ.drawDepthOnly(SPHERE_POS[i], SPHERE_SIZE, sphere);
							
		}
		//System.out.print(getTimeD() - c);
	//	System.out.print("Algo\n");
		
		
		//long c = getTimeD();
		if(show_Lights)
		{
		//draw lightPos
		
		
			
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				Vector3f col = new Vector3f(pLightA[i].diff);
				col.scale(COL_SCALE);
				
				EAZ.drawDepthOnly(pLightA[i].getPos(), LIGHT_SIZE, sphere);
				
			}
			
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glBindVertexArray(0);
			
		
		}
		//System.out.println(getTimeD() - c);
		
		
		EAZ.getDP().bind();
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		//unten
		EAZ.drawDepthOnly(planePos, 1, plane);
		//rechts
		EAZ.drawDepthOnly(new Vector3f(500, 0,0),plane, 0,0,90);
		//links
		EAZ.drawDepthOnly(new Vector3f(-500, 0,0),plane, 0,0,-90);
		//vorne
		EAZ.drawDepthOnly(new Vector3f(0, 0, 500),plane, -90, 0, 0);
		//hinten
		EAZ.drawDepthOnly(new Vector3f(0, 0, -500),plane, 90, 0, 0);
		//oben
		EAZ.drawDepthOnly(new Vector3f(0,500,0),plane, 180,0,0);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
		
	}
	
	/*
	 * 
	 * 3D-Szenen werden gezeichnet mit übergebenem Algorithmus
	 * 
	 * 
	 */
	public void drawCrytekBuild(Algo AL)
	{
		
		AL.clearBuffers();
		
		
		
		
		
		
		
		
		AL.getFP().bind();
		
		AL.useShini();
	
		
		
		
		for(Model m : sponzaScene.Models)
		{
		if(m.count == 0) continue;
		
		
		glBindVertexArray(m.vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		AL.getFP().setInt("norTexUse", 0);
		
		AL.setModelInf(m);
		AL.drawFP(new Vector3f(0f,0f,0f), 1, sphere, (Vector3f)null, m.count);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		
		AL.getFP().setInt("mtlUse", 0);
		AL.getFP().setInt("difTexUse",0);
		AL.getFP().setInt("specTexUse", 0);
		AL.getFP().setInt("ambTexUse", 0);
		}
		
		
	}
	
	public void drawAlgo(Algo Al)
	{
		
		switch(TS)
		{
		
		case SpheresInBox:
			drawAlgoSpheresInBox(Al);
		
			break;
			
		case SpheresOnPlane:
			drawAlgoSphereOnFarPlane(Al);
			break;
			
		case CrytekBuilding:
			drawCrytekBuild(Al);
			break;
		
		}
		
	}
	
	public void drawAlgoSphereOnFarPlane(Algo AL){
		
		
		
		
		
		AL.clearBuffers();
		
		
		
		
		
		
		
		
		AL.getFP().bind();
		
		
		AL.getFP().setInt("Rock", 4);
		AL.getFP().setInt("norTexUse", NORMALNUM);
		
		AL.useShini();
		//checkE(true);
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		long c = getTimeD();
		//draw Spheres
		
		
		
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			AL.getFP().setFloat("shininess", SPHERE_SPEC[i]);
			AL.drawFP(SPHERE_POS[i], SPHERE_SIZE, sphere, SPHERE_COL[i]);
							
		}
		//System.out.print(getTimeD() - c);
	//	System.out.print("Algo\n");
		AL.getFP().setInt("norTexUse", 0);
		
		//long c = getTimeD();
		if(show_Lights)
		{
		//draw lightPos
		
		
			AL.drawCamStart();
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				Vector3f col = new Vector3f(pLightA[i].diff);
				col.scale(COL_SCALE);
				
				AL.drawCam(pLightA[i].getPos(), LIGHT_SIZE, sphere, col);
				
			}
			
			
			
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glBindVertexArray(0);
			
		
		}
		//System.out.println(getTimeD() - c);
		
		AL.getFP().bind();
		
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		float PLANE_SPEC[] = {
				10,
				20,
				50,
				80,
				100,
				120
		};
		
		//unten
		AL.getFP().setFloat("shininess", PLANE_SPEC[5]);
		AL.drawFP(planePos, 10, plane, (Vector3f)null);
		
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
		
	}
	
	public void drawAlgoSpheresInBox(Algo AL){

		
		AL.clearBuffers();
		
		
		
		
		
		
		
		
		AL.getFP().bind();
		
		
		AL.getFP().setInt("Rock", 4);
		AL.getFP().setInt("norTexUse", NORMALNUM);
		
		AL.useShini();
		//checkE(true);
		
		
		glBindVertexArray(vaoSphereID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		long c = getTimeD();
		//draw Spheres
		for(int i = 0; i < SPHERE_COUNT; i++)
		{
			AL.getFP().setFloat("shininess", SPHERE_SPEC[i]);
			AL.drawFP(SPHERE_POS[i], SPHERE_SIZE, sphere, SPHERE_COL[i]);
							
		}
		
		AL.getFP().setInt("norTexUse", 0);
		
		if(show_Lights)
		{
		//draw lightPos
		
		
			AL.drawCamStart();
			for(int i = 0; i < MAX_PLIGHT; i++)
			{
				Vector3f col = new Vector3f(pLightA[i].diff);
				col.scale(COL_SCALE);
				
				AL.drawCam(pLightA[i].getPos(), LIGHT_SIZE, sphere, col);
				
			}
					
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glBindVertexArray(0);
			
		
		}
		
		AL.getFP().bind();
		
		
		
		glBindVertexArray(vaoPlaneID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		float PLANE_SPEC[] = {
				10,
				20,
				50,
				80,
				100,
				120
		};
		
		//unten
		AL.getFP().setFloat("shininess", PLANE_SPEC[0]);
		AL.drawFP(planePos, 1, plane, (Vector3f)null);
		
		//rechts
		AL.getFP().setFloat("shininess", PLANE_SPEC[1]);
		AL.drawFP(new Vector3f(500, 0,0),plane,(Vector3f)null, 0,0,90);
		
		//links
		AL.getFP().setFloat("shininess", PLANE_SPEC[2]);
		AL.drawFP(new Vector3f(-500, 0,0),plane,(Vector3f)null, 0,0,-90);
		//vorne
		AL.getFP().setFloat("shininess", PLANE_SPEC[3]);
		AL.drawFP(new Vector3f(0, 0, 500),plane,(Vector3f)null, -90, 0, 0);
		//hinten
		AL.getFP().setFloat("shininess", PLANE_SPEC[4]);
		AL.drawFP(new Vector3f(0, 0, -500),plane,(Vector3f)null, 90, 0, 0);
		
		//oben
		AL.getFP().setFloat("shininess", PLANE_SPEC[5]);
		
		AL.drawFP(new Vector3f(0,500,0),plane,(Vector3f)null, 180,0,0);
		
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		
		
	}
	
	
	public void dispose()
	{
		
		glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboColorID);
		glDeleteBuffers(vboNormalID);
		
		glDeleteBuffers(vboSphereVertexID);
		glDeleteBuffers(vboSphereNormalID);
		glDeleteBuffers(vboSphereColorID);
		
		glDeleteBuffers(vboPlaneVertID);
		glDeleteBuffers(vboPlaneNorID);
		glDeleteBuffers(vboPlaneColID);

		
	}
	
	/*
	 * 
	 * 
	 * Zum Rendern von Onscreen-Text
	 * 
	 * Quelle: http://www.java-gaming.org/index.php?topic=28055.0
	 * 
	 * 
	 */
	public static void drawString(String s, int x, int y) {
		 glEnable(GL_BLEND);
	      glDisable(GL_DEPTH_TEST);
		  glBlendEquation(GL_FUNC_ADD);
		  glBlendFunc(GL_ONE,GL_ZERO);
	      
	      glDisable(GL_LIGHTING);
	    
		 if(showStrings)
		 {
		  
	      int startX = x;
	      
	      
	      GL11.glBegin(GL11.GL_POINTS);
	      GL11.glColor4f(1,1,1,1);
	      for (char c : s.toLowerCase().toCharArray()) {
	         if (c == 'a') {
	            for (int i = 0; i < 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == 'b') {
	            for (int i = 0; i < 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	               GL11.glVertex2f(x + i, y + 4);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 7, y + 5);
	            GL11.glVertex2f(x + 7, y + 7);
	            GL11.glVertex2f(x + 7, y + 6);
	            GL11.glVertex2f(x + 7, y + 1);
	            GL11.glVertex2f(x + 7, y + 2);
	            GL11.glVertex2f(x + 7, y + 3);
	            x += 8;
	         } else if (c == 'c') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 6, y + 2);

	            GL11.glVertex2f(x + 6, y + 6);
	            GL11.glVertex2f(x + 6, y + 7);

	            x += 8;
	         } else if (c == 'd') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 6, y + 2);
	            GL11.glVertex2f(x + 6, y + 3);
	            GL11.glVertex2f(x + 6, y + 4);
	            GL11.glVertex2f(x + 6, y + 5);
	            GL11.glVertex2f(x + 6, y + 6);
	            GL11.glVertex2f(x + 6, y + 7);

	            x += 8;
	         } else if (c == 'e') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 0);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == 'f') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == 'g') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 6, y + 2);
	            GL11.glVertex2f(x + 6, y + 3);
	            GL11.glVertex2f(x + 5, y + 3);
	            GL11.glVertex2f(x + 7, y + 3);

	            GL11.glVertex2f(x + 6, y + 6);
	            GL11.glVertex2f(x + 6, y + 7);

	            x += 8;
	         } else if (c == 'h') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == 'i') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 3, y + i);
	            }
	            for (int i = 1; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 0);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            x += 7;
	         } else if (c == 'j') {
	            for (int i = 1; i <= 8; i++) {
	               GL11.glVertex2f(x + 6, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 0);
	            }
	            GL11.glVertex2f(x + 1, y + 3);
	            GL11.glVertex2f(x + 1, y + 2);
	            GL11.glVertex2f(x + 1, y + 1);
	            x += 8;
	         } else if (c == 'k') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            GL11.glVertex2f(x + 6, y + 8);
	            GL11.glVertex2f(x + 5, y + 7);
	            GL11.glVertex2f(x + 4, y + 6);
	            GL11.glVertex2f(x + 3, y + 5);
	            GL11.glVertex2f(x + 2, y + 4);
	            GL11.glVertex2f(x + 2, y + 3);
	            GL11.glVertex2f(x + 3, y + 4);
	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 5, y + 2);
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 7, y);
	            x += 8;
	         } else if (c == 'l') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	            }
	            x += 7;
	         } else if (c == 'm') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            GL11.glVertex2f(x + 3, y + 6);
	            GL11.glVertex2f(x + 2, y + 7);
	            GL11.glVertex2f(x + 4, y + 5);

	            GL11.glVertex2f(x + 5, y + 6);
	            GL11.glVertex2f(x + 6, y + 7);
	            GL11.glVertex2f(x + 4, y + 5);
	            x += 8;
	         } else if (c == 'n') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            GL11.glVertex2f(x + 2, y + 7);
	            GL11.glVertex2f(x + 2, y + 6);
	            GL11.glVertex2f(x + 3, y + 5);
	            GL11.glVertex2f(x + 4, y + 4);
	            GL11.glVertex2f(x + 5, y + 3);
	            GL11.glVertex2f(x + 6, y + 2);
	            GL11.glVertex2f(x + 6, y + 1);
	            x += 8;
	         } else if (c == 'o' || c == '0') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 0);
	            }
	            x += 8;
	         } else if (c == 'p') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            GL11.glVertex2f(x + 6, y + 7);
	            GL11.glVertex2f(x + 6, y + 5);
	            GL11.glVertex2f(x + 6, y + 6);
	            x += 8;
	         } else if (c == 'q') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               if (i != 1)
	                  GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               if (i != 6)
	                  GL11.glVertex2f(x + i, y + 0);
	            }
	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 5, y + 2);
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 7, y);
	            x += 8;
	         } else if (c == 'r') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            GL11.glVertex2f(x + 6, y + 7);
	            GL11.glVertex2f(x + 6, y + 5);
	            GL11.glVertex2f(x + 6, y + 6);

	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 5, y + 2);
	            GL11.glVertex2f(x + 6, y + 1);
	            GL11.glVertex2f(x + 7, y);
	            x += 8;
	         } else if (c == 's') {
	            for (int i = 2; i <= 7; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 1, y + 7);
	            GL11.glVertex2f(x + 1, y + 6);
	            GL11.glVertex2f(x + 1, y + 5);
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	               GL11.glVertex2f(x + i, y);
	            }
	            GL11.glVertex2f(x + 7, y + 3);
	            GL11.glVertex2f(x + 7, y + 2);
	            GL11.glVertex2f(x + 7, y + 1);
	            GL11.glVertex2f(x + 1, y + 1);
	            GL11.glVertex2f(x + 1, y + 2);
	            x += 8;
	         } else if (c == 't') {
	            for (int i = 0; i <= 8; i++) {
	               GL11.glVertex2f(x + 4, y + i);
	            }
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            x += 7;
	         } else if (c == 'u') {
	            for (int i = 1; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 0);
	            }
	            x += 8;
	         } else if (c == 'v') {
	            for (int i = 2; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 6, y + i);
	            }
	            GL11.glVertex2f(x + 2, y + 1);
	            GL11.glVertex2f(x + 5, y + 1);
	            GL11.glVertex2f(x + 3, y);
	            GL11.glVertex2f(x + 4, y);
	            x += 7;
	         } else if (c == 'w') {
	            for (int i = 1; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            GL11.glVertex2f(x + 2, y);
	            GL11.glVertex2f(x + 3, y);
	            GL11.glVertex2f(x + 5, y);
	            GL11.glVertex2f(x + 6, y);
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + 4, y + i);
	            }
	            x += 8;
	         } else if (c == 'x') {
	            for (int i = 1; i <= 7; i++)
	               GL11.glVertex2f(x + i, y + i);
	            for (int i = 7; i >= 1; i--)
	               GL11.glVertex2f(x + i, y + 8 - i);
	            x += 8;
	         } else if (c == 'y') {
	            GL11.glVertex2f(x + 4, y);
	            GL11.glVertex2f(x + 4, y + 1);
	            GL11.glVertex2f(x + 4, y + 2);
	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 4, y + 4);

	            GL11.glVertex2f(x + 3, y + 5);
	            GL11.glVertex2f(x + 2, y + 6);
	            GL11.glVertex2f(x + 1, y + 7);
	            GL11.glVertex2f(x + 1, y + 8);

	            GL11.glVertex2f(x + 5, y + 5);
	            GL11.glVertex2f(x + 6, y + 6);
	            GL11.glVertex2f(x + 7, y + 7);
	            GL11.glVertex2f(x + 7, y + 8);
	            x += 8;
	         } else if (c == 'z') {
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + i);
	            }
	            GL11.glVertex2f(x + 6, y + 7);
	            x += 8;
	         } else if (c == '1') {
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	            }
	            for (int i = 1; i <= 8; i++) {
	               GL11.glVertex2f(x + 4, y + i);
	            }
	            GL11.glVertex2f(x + 3, y + 7);
	            x += 8;
	         } else if (c == '2') {
	            for (int i = 1; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 1, y + 7);
	            GL11.glVertex2f(x + 1, y + 6);

	            GL11.glVertex2f(x + 6, y + 7);
	            GL11.glVertex2f(x + 6, y + 6);
	            GL11.glVertex2f(x + 6, y + 5);
	            GL11.glVertex2f(x + 5, y + 4);
	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 3, y + 2);
	            GL11.glVertex2f(x + 2, y + 1);
	            x += 8;
	         } else if (c == '3') {
	            for (int i = 1; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y);
	            }
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 6, y + i);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == '4') {
	            for (int i = 2; i <= 8; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 7; i++) {
	               GL11.glVertex2f(x + i, y + 1);
	            }
	            for (int i = 0; i <= 4; i++) {
	               GL11.glVertex2f(x + 4, y + i);
	            }
	            x += 8;
	         } else if (c == '5') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            for (int i = 4; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            GL11.glVertex2f(x + 1, y + 1);
	            GL11.glVertex2f(x + 2, y);
	            GL11.glVertex2f(x + 3, y);
	            GL11.glVertex2f(x + 4, y);
	            GL11.glVertex2f(x + 5, y);
	            GL11.glVertex2f(x + 6, y);

	            GL11.glVertex2f(x + 7, y + 1);
	            GL11.glVertex2f(x + 7, y + 2);
	            GL11.glVertex2f(x + 7, y + 3);

	            GL11.glVertex2f(x + 6, y + 4);
	            GL11.glVertex2f(x + 5, y + 4);
	            GL11.glVertex2f(x + 4, y + 4);
	            GL11.glVertex2f(x + 3, y + 4);
	            GL11.glVertex2f(x + 2, y + 4);
	            x += 8;
	         } else if (c == '6') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y);
	            }
	            for (int i = 2; i <= 5; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	               GL11.glVertex2f(x + i, y + 8);
	            }
	            GL11.glVertex2f(x + 7, y + 1);
	            GL11.glVertex2f(x + 7, y + 2);
	            GL11.glVertex2f(x + 7, y + 3);
	            GL11.glVertex2f(x + 6, y + 4);
	            x += 8;
	         } else if (c == '7') {
	            for (int i = 0; i <= 7; i++)
	               GL11.glVertex2f(x + i, y + 8);
	            GL11.glVertex2f(x + 7, y + 7);
	            GL11.glVertex2f(x + 7, y + 6);

	            GL11.glVertex2f(x + 6, y + 5);
	            GL11.glVertex2f(x + 5, y + 4);
	            GL11.glVertex2f(x + 4, y + 3);
	            GL11.glVertex2f(x + 3, y + 2);
	            GL11.glVertex2f(x + 2, y + 1);
	            GL11.glVertex2f(x + 1, y);
	            x += 8;
	         } else if (c == '8') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 0);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            x += 8;
	         } else if (c == '9') {
	            for (int i = 1; i <= 7; i++) {
	               GL11.glVertex2f(x + 7, y + i);
	            }
	            for (int i = 5; i <= 7; i++) {
	               GL11.glVertex2f(x + 1, y + i);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 8);
	               GL11.glVertex2f(x + i, y + 0);
	            }
	            for (int i = 2; i <= 6; i++) {
	               GL11.glVertex2f(x + i, y + 4);
	            }
	            GL11.glVertex2f(x + 1, y + 0);
	            x += 8;
	         } else if (c == '.') {
	            GL11.glVertex2f(x + 1, y);
	            GL11.glVertex2f(x + 1, y+1);
	            GL11.glVertex2f(x + 2, y);
	            GL11.glVertex2f(x + 2, y+1);
	            x += 3;
	         } else if (c == ',') {
	            GL11.glVertex2f(x + 1, y);
	            GL11.glVertex2f(x + 1, y + 1);
	            x += 2;
	         } else if (c == '\n') {
	            y -= 10;
	            x = startX;
	         } else if (c == ' ') {
	            x += 8;
	         }
	      }
	      GL11.glEnd();
	      
		 }
	   }
	

	/*
	 * Inverstransponierte Matrix Erstellen
	 */
	public Matrix4f createInverseTranspose(Matrix4f view)
	{
		Matrix4f ret = new Matrix4f(view);
		
		Matrix4f.invert(ret, ret);
		ret.transpose();
		
		return ret;
	}
	
	public Matrix4f createInverseTranspose(Matrix4f view,Matrix4f ret)
	{
		
		
		Matrix4f.invert(view, ret);
		ret.transpose();
		
		return ret;
	}
	
	
}

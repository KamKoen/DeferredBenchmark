# DeferredBenchmark
For licenses and sources in regards to the used libraries and models, one should take a look at Licenses.txt.

This benchmark program was created for my bachelor's thesis "Vergleich zwischen Deferred and Forward Rendering" 
(transl.: "Comparison between Deferred and Forward Rendering", [link to the pdf in German](http://www.gdv.informatik.uni-frankfurt.de/abschlussarbeiten/download/2014-4/ausarbeitung.pdf)). After starting the program and letting it load to 
completion, the user is greeted with a Swing-UI holding a panel which uses LWJGL to render a scene of 3D-models. 
The only requirement should be a graphics card that can handle multiple FBOs and GL4.3, since LWJGL should work
regardless of operating system.

# Setup:

The .jar file can be executed as long as the natives can be found in a folder next to the .jar file with the following structure:

* Random Folder
  * DeferredBenchmark.jar
  * lib
    * lwjgl-2.9.1
      * all_native
        * natives for all platforms should be here
              
This repository provides a folder with the required natives and folder structure, so downloading the release version should be enough.


# Available Scene Options:

There are three scenes to chose from a drop-down menu beneath the word Testscene:

SpheresInBox
	which is a scene with six planes creating a box, inside which a number of spheres show up
SpheresOnPlane
	which is a scene with one longer plane and spheres floating above it
Crytekbuilding
	which is the Crytek Sponza taken from the following page: http://www.crytek.com/cryengine/cryengine3/downloads
	For more information, see the Licenses.txt file or visit the original website.

All three scenes can hold a varying number of lights (up to 1024) with random colors. The light attenuation can be modified in the
UI (constant, linear and quadratic attenuation). A percentage field is open to choose a cutoff point for the effective range of the 
lights (i.e. 200 means that the effective light radius range ends at the point where the light's effective color is below the
fractional threshold of (1/200) of the original light's color). The modifications apply to all lights.

The UI allows the user to specify the number of lights (and spheres for the first two scenes) which dis/appear instantly when changing
the respective numbers. 



# Interactive Scene and Keyboard Controls:

The scenes camera can be moved by common keyboard movements (WASD for X/Z-movement, QE for Y-movement, L-Shift for speedup in movement speed).
The camera viewing angle can only be moved when entering camera mode, which can be toggled by right-clicking on the 3D-scene (another click disables camera
mode). The mouse should disappear during camera mode to indicate the entered state. The lights' position can be shown by pressing ',' and
be moved with GVBN and FH akin to the camera movement (the direction is not camera-bound, though).

Pressing P and L changes the used shader algorithm, indicated by the on-screen text. Pressing '6' toggles Early-Z-Culling for the 
Tiled_Forward_Rendering algorithm, while 'Y' toggles general Z-Culling for both Tiled_Forward/Tiled_Deferred_Rendering. Note that 
Early-Z-Culling can be used together with general Z-Culling together during Tiled_Forward_Rendering, as the Z-Culling refers
to the lights being culled for each tile. 

Apart from the different shader algorithms, there is also one view which shows the same scene in its components (albedo, normal and depth in 
seperate windows).

While using the Tiled_X_Rendering shaders, pressing 'R' presents a visualization of the number of lights in grey-scale found in each tile, 
where a completely white tile means that all current scene lights hit that tile, while a completely black tile means that none do.

The UI allows to set the resolution (height and width). Based on that, one can also set the height and width of a single tile (preferably one
that the resolution is divisible through). Since on can multiple subdivisions, there is the option for that as well (there are six 
textfields for the widths and heights of at most three subdivisions). By pressing the 'K' or 'J' key, one can change the used sub/divisions
for the Tiled_X_Rendering shaders. The effect can also be seen by pressing 'R', since larger tiles should get larger during the grey-scale
view as well. This only works, though, after clicking the 'Reset Tiles'-button, since the shader-programs have to be redone.

Finally, pressing 'X' randomizes the light positions and colors.



# Shader algorithms:

The following options are available:

* PerPixel-Rendering
  * Untouched forward rendering. Each model is sent through the pipeline. Each fragment arriving at 
  the fragment shader runs through all possible light sources and calculates the resulting color.
		
* Early-Z
	* Forward rendering with two passes. The first pass only
		sends objects in order to cull hidden fragments in the 
		second pass through the - at that point - filled z-buffer.
		
* Deferred-Rendering
	* The first pass sends objects and saves information for the lighting (normal, albedo, depth),
		the second pass constructs the scene based on the saved information and the lights in the 
		scene. In this case, each fragment/pixel on the screen takes every light into account and 
		calculates, whether the distance is inside the light's effective region, and what effect the
		light has on the color.
	
* Deferred-Sphere
	* The first pass sends objects and saves information. The second pass sends for each light a representative
		sphere model with the radius being as large as the light's effective region. Since we are sending per sphere
		model fragments to the fragment shader, we can calculate per fragment the effect the light has on that particular
		fragment, and thus reduce the number of lights we have to take into consideration per each fragment on the screen.
		
* Deferred-Stencil (see [Yuriy O'Donnell's post](http://kayru.org/articles/deferred-stencil/) on this 
					from which this algorithm is taken/adapted
	* The first pass sends objects and saves information. The second pass sends for each light
		a representative sphere which saves in the stencil-buffer the information of whether or
		not the light affects the sent fragment (since it could either be covered by a scene
		object or not hit any object at all (dangling light in empty space)). Any fragment that
		passes the stencil test in a third pass calculates the respective light's effect on the fragment.
		
* Tiled-Deferred (see Johan Andersson (DICE), GDC 2011)
	* By using a compute shader, we can divide the screen into tiles and can calculate which lights 
		hit which tiles, thus cull the number of lights to a large degree. We can cull more lights on the 
    Z-axis with additional Z-Culling, improving performance if the scene has lots of overlapping spheres or narrow corridors.
		
* Tiled-Forward 
	* The idea to divide the screen in tiles to cull the lights can be used in combination with 
		a forward renderer (see AMD's "Leo" graphic demo in 2012). Here we can use additionally the
		Early-Z-pass as well as the tile-based algorithm's Z-Culling.
		
		
# Benchmarking:

The benchmarking itself is not really recommended for general usage, since camera-movements and positioning
during the benchmarking are hard-coded into the code. In case one wants to use it anyway:
	
The upper left corner of the UI holds a list of all algorithms. Each can be chosen to be put into a benchmarking
list, through which the benchmark is going to go one by one. The length of each benchmark scene capture is 
modifiable (the number given is in seconds). The camera either follows the camera movement direction, is set to 
focus on a point, or is set to look in a direction. The latter two option can be modified with the X,Y and Z
fields, where one can enter either a coordinate or a viewing-vector. Which option is chosen depends on the set
drop down menu option.
	
The SpheresInBox-scene should best be benchmarked with setting the camera to lock to a point in the center
of the box, since the camera will move around the box multiple times in a circle. 
	
The SpheresOnAPlane-scene will have the camera move along the plane, meaning that setting the camera
to look in a specified direction along the spheres would be preferable.
	
The Sponza has its own route that the camera is going to take, all throughout the sponza, so one 
should set the camera viewing direction to move along with the camera movement.
	
There are three things to benchmark:
	
* Varying number of lights with set number of spheres and set light distance.
		
* Varying number of sphere with set number of lights and set light distance.
		
* Varying light distance with set number of lights and set number of spheres.
		
The set numbers are:
* Number of lights: 500
* Number of spheres: 500
* Light distance: 200 units
			
The varying numbers are:
* Spheres: 0,50,100,200,500,750,1000
* Lights: 0,10,20,50,100,200,500,750,1024
* Light distance: 50,100,200,350,500
			
	One can use a self-made list of varying numbers for lights and spheres, if one unsets the 'default' checkmark
	and sets either the 'lights' or 'spheres' option. After which one can enter a number and ADD it to a list.
	Deleting selected numbers from the list is also possible with DEL.
	
	The benchmark data is going to be saved in a "defaultFile.txt" file if not set otherwise through the 
	'New Benchmark' button.

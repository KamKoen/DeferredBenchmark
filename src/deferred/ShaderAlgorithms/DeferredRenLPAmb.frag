#version 330


uniform sampler2D normalTex;
uniform sampler2D diffTex;
uniform sampler2D posTex;
uniform sampler2D depthTex;
uniform sampler2D specTex;




layout (location = 0) out vec4 theColor;





uniform int width;
uniform int height;


uniform int kAmode = 0;


uniform mat4 view;
uniform mat4 normalMLight;


uniform float matEmission = 0.3f;

uniform float A;
uniform float B;
uniform float C;
uniform float D;

in vec2 TexCoord0;


uniform vec3 ambientLight;



void main()
{
	vec4 res = vec4(0,0,0,0);
	vec3 col = vec3(0,0,0);
	
	
   
   
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
		
	
		
		
		col = vColor.rgb * ambientLight;
		res = vec4(col,vColor.w);
	    	
	  
	    
    
   
    theColor = res;
   

}














































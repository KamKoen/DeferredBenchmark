#version 330


uniform sampler2D normalTex;
uniform sampler2D diffTex;
uniform sampler2D posTex;
uniform sampler2D depthTex;
uniform sampler2D specTex;

uniform int useShini = 0;

uniform vec3 camPos;

struct PointLight{

	vec4 position;	//.w is rad
	vec4 ambient;	//.w ist quad
	vec4 diff;		//.w ist lin
	vec4 specular;	//.w ist const
};

struct SpotLight{

	vec3 position;
	vec3 ambient;
	vec3 diff;
	float cutoff;
	vec3 specular;
	vec3 direction;

};



layout (location = 0) out vec4 theColor;



uniform int mode = 0;

uniform int starMode = 0;

uniform int width;
uniform int height;

layout (std140) uniform pl{
	
	PointLight pLight[1000];

};

uniform SpotLight sLight[1];
uniform int LIGHT_MAX;
uniform int SPOT_MAX;

uniform int kAmode = 0;


uniform mat4 view;
uniform mat4 normalMLight;





uniform vec3 worldAmbient = vec3(0,0,0);
uniform float matEmission = 0.3f;


uniform float matShin;

uniform int CUR_LIGHT_ID;

uniform float A;
uniform float B;
uniform float C;
uniform float D;

vec2 TexCoord0;




vec4 calcViewSpace(float x, float y, float depthZ)
{
	
	
	x = x * 2.0 - 1.0;
	
	y = y * 2.0 - 1.0;
	vec3 view = vec3(0,0,0);
	
	float nDcz = depthZ * 2.0 - 1.0;
	
	view.z = D / (-nDcz - C);
	
	view.x = (x * -view.z) / A; //-view.z, da die proj in OpenGl in mat.23 = -1, nicht 1
	
	view.y = (y * -view.z) / B;
	
	//view = vec3((vec2(x,y)*nDcz)/vec2(A,B), nDcz);
	
	return vec4(view,1);


}

vec3 CalcPointlights()
{
	vec3 col = vec3(0,0,0);
	
	vec4 vPosition = calcViewSpace(TexCoord0.x, TexCoord0.y,texture2D(depthTex, TexCoord0.st).x);
	
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
	
	float quadrAtt = 0;
	float constAtt = 0;
	float linAtt = 0;
	
	vec4 shininess = texture2D(specTex, TexCoord0.st);
	
	
		
	vec4 vNormal = texture2D(normalTex, TexCoord0.st);

	
	int i = CUR_LIGHT_ID;
		
	quadrAtt = pLight[i].ambient.w;
	linAtt = pLight[i].diff.w;
	constAtt = pLight[i].specular.w;
	
	
   //diffuse and ambient
   
    vec3 specularComp = vec3(0,0,0);
	vec3 diffuseComp = vec3(0,0,0);
	vec3 ambientComp = vec3(0,0,0);
   
   
   	vec3 lpos = (view * vec4(pLight[i].position.xyz,1.0)).xyz;
    
    vec3 vertPos = vPosition.xyz;
  
    vec3 lightDirection = normalize(lpos - vertPos);
   
   
  	//attenuationFactor 
    float distance = length(lpos-vertPos);
    
    float distanceFromL = pLight[i].position.w;
    
    
    if(distance > distanceFromL)
    {
    	discard;
    }
    float attenuation = 1.0;
    
  	attenuation = 1 / (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
    if (starMode == 1)
	    {
	     	
	 	    float abc = distance / distanceFromL ;
	 	    
	 	    // |   von TiledLighting11_2010 @ Jason Stewart, Gareth Thomas
		    // |   Quelle: http://developer.amd.com/tools-and-sdks/graphics-development/graphics-		    		    // |           development-sdks/amd-radeon-sdk/
	 	    // V 
	 	    attenuation = -0.05 + 1.05/(1 + 20 * abc * abc); 	 	    
	    }
    
    
    
    vec3 surfNor = normalize(vNormal.xyz);
    
    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
    
    diffuseComp += (diffuseLightInt * pLight[i].diff.xyz) * attenuation;
    
    ambientComp += pLight[i].ambient.xyz * attenuation;
    
   
    
    
    vec3 cPos = (view * vec4(camPos,1.0)).xyz;
    
    vec3 cC = (cPos - vertPos);
    
    //specular
    vec3 R = normalize(reflect(-lightDirection, surfNor));
    
    
    
 	vec3 C = normalize(cC);
 
   
   float specular = max(0.0, dot(C, R));
    
    if(diffuseLightInt != 0){
    
    	float fspecular = pow(specular, shininess.w);
    	
    	specularComp += fspecular * pLight[i].specular.xyz * attenuation;
    	
    	}
    	
    	   
	
	    
	    
	    
	    
	
	
	vec3 retcol = diffuseComp * vColor.xyz * shininess.y  + shininess.x * specularComp + ambientComp * vColor.xyz;
	
	return retcol;

}






void main()
{
	vec4 res = vec4(0,0,0,0);
	vec3 col = vec3(0,0,0);
	
	TexCoord0.s = gl_FragCoord.x / width;
	TexCoord0.t = gl_FragCoord.y / height; 
   
   
	if(kAmode == 0)
	{
		vec4 vColor = texture2D(diffTex, TexCoord0.st);
		
		if(vColor.w == -1 )
		{
		
			res = vColor;
		
		}
		else
		{
			col += CalcPointlights();
			
			res = vec4(col,1);
	  	}
	    
    }
    
    else
    {
    	vec4 vColor = texture2D(diffTex, TexCoord0.st);
    	
		if(vColor.w == -1)
		{
		
			res = vec4(vColor.xyz,1);
			//res = vec4(0,0,0,1);
		
		}
    	
    }
 	
 	
    theColor = res;
   

}














































#version 430

uniform sampler2D Texture;



uniform vec3 ambientLight;

uniform sampler2D difModTex;
uniform sampler2D ambModTex;
uniform sampler2D specModTex;

uniform int difTexUse=0;

uniform int ambTexUse=0;
uniform int specTexUse=0;

uniform vec3 Kd;
uniform vec3 Ka;
uniform float Ns;


uniform int mtlUse = 0;





layout(std430, binding = 4)  buffer infB
{
	int info[];
};

layout(std430, binding = 5)  buffer indB
{
	int indexB[];
};


uniform int tileWidth;
uniform int tileHeight;
uniform int width;
uniform int height;

uniform int starMode = 0;

uniform int RasterMode = 0;

struct PointLight{

	vec4 position;	//w hat dis
	vec4 ambient;	//w hat quad
	vec4 diff;		//w hat lin
	vec4 specular;	//w hat const
};


uniform int useShini = 0;

uniform float shininess;

in vec4 vColor;
in vec3 vNormal;
in vec3 vPosition;

in vec4 ShadowCoord;

uniform vec3 camPos;

uniform mat4 view;
uniform mat4 MV;
uniform mat4 normalM;
uniform mat4 normalMLight;

layout (std140) uniform pl{
	
	PointLight pLight[1024];

};

uniform int LIGHT_MAX;

uniform float quadrAtt = 0.02f;
uniform float constAtt = 0.5f;
uniform float linAtt = 0.0f;



uniform float matShin;



uniform sampler2D Rock;

in vec2 vUv;
in vec3 vTan;

uniform int norTexUse = 0;


out vec4 theColor;


vec3 calcBumbNormal()
{

	vec3 Normal = normalize((normalM * vec4(vNormal, 1.0)).xyz);
    vec3 Tangent = normalize(vTan);
    Tangent = normalize(Tangent - dot(Tangent, Normal) * Normal);
    vec3 Bitangent = cross(Tangent, Normal);
    vec3 BumpMapNormal = texture(Rock, vec2(vUv.x, 1.0-vUv.y)).xyz;
    BumpMapNormal = 2.0 * BumpMapNormal - vec3(1.0, 1.0, 1.0);
    vec3 NewNormal;
    mat3 TBN = mat3(Tangent, Bitangent, Normal);
    NewNormal = TBN * BumpMapNormal;
    NewNormal = normalize(NewNormal);
    return NewNormal;


}




vec3 CalcPointlights()
{
	vec3 col = vec3(0,0,0);
	
	float quadrAtt = 0;
	float constAtt = 0;
	float linAtt = 0;
	
	vec3 specularComp = vec3(0,0,0);
	vec3 diffuseComp = vec3(0,0,0);
	vec3 ambientComp = vec3(0,0,0);
	
	
	
	float shin = matShin;
	if(useShini ==1) shin = shininess;
	if(mtlUse ==1) shin = Ns;
	
	//what tile?
	
	int curX = int(gl_FragCoord.x/tileWidth);
	int curY = int(gl_FragCoord.y/tileHeight);
	
	int offset = info[(curX + curY * (width/tileWidth)) * 2];
	int count = info[(curX + curY * (width/tileWidth)) * 2 + 1];
	
	vec3 ambTex = vColor.rgb;
	vec3 difTex = vec3(1,1,1);
	vec3 specTex = vec3(1,1,1);
	//ambientComp += ambientLight;
	
	if(offset < 0) //no lights
	{
	
	
	
	
	 return col;
	
	
	
	}
	
	
	
	int i = 0;
	
	vec3 surfNor;
	
  	if(norTexUse != 1)
    {
    	surfNor = normalize((normalM * vec4(vNormal, 1.0)).xyz);	    
    }
    else 
    {
    	surfNor = calcBumbNormal();
    }
	
	
	for(int j = 0; j < count; j++)
	{
	
		i = indexB[offset + j];
		if(i < 0) break;
	
		quadrAtt = pLight[i].ambient.w;
		linAtt = pLight[i].diff.w;
		constAtt = pLight[i].specular.w;
		
	   //diffuse and ambient
	   
	   	vec3 lpos = (view * vec4(pLight[i].position.xyz,1.0)).xyz;
	    
	    vec3 vertPos = (MV * vec4(vPosition,1.0)).xyz;
	  
	    vec3 lightDirection = normalize(lpos - vertPos);
	   
	   
	  	//attenuationFactor 
	    float distance = length(lpos-vertPos);
	    if(distance > pLight[i].position.w) continue;
	    float attenuation = (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
	    
	    if(attenuation != 0) attenuation = 1/attenuation;
	    
	    float distanceFromL = pLight[i].position.w;
	    
	    if (starMode == 1)
	    {
	     	//attenuation = (1- linstep(distanceFromL /8.0, distanceFromL, distance));
	     
	 	   // attenuation = attenuation * attenuation * attenuation;
	 	    
	 	    float abc = distance / distanceFromL ;
	 	    
	 	    // |   von TiledLighting11_2010 @ Jason Stewart, Gareth Thomas
		    // |   Quelle: http://developer.amd.com/tools-and-sdks/graphics-development/graphics-		    		    // |           development-sdks/amd-radeon-sdk/
	 	    // V 
	 	    attenuation = -0.05 + 1.05/(1 + 20 * abc * abc); 	 	    
	    }
	    
	  
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
	    
	    	float fspecular = pow(specular, shin);
	    	
	    	specularComp += fspecular * pLight[i].specular.xyz *attenuation;
	    	
	    	}
	    	
	    	   
	}
	    
	    
	    
	if(RasterMode == 1) return vec3(float(count) / float(LIGHT_MAX),float(count) / float(LIGHT_MAX),float(count) / float(LIGHT_MAX)); 
	
	
	
	
	
	vec3 retcol = diffuseComp * vec3(vColor.rgb)  + specularComp + ambTex * ambientComp;
	
	if(mtlUse == 1)
	{
	
	if(specTexUse == 1) specTex = texture2D(specModTex, vec2(1-vUv.x, 1-vUv.y)).rgb; 
	if(difTexUse == 1) 
	{
	difTex = texture2D(difModTex, vec2(1-vUv.x, 1-vUv.y)).rgb;
	ambTex = difTex;
	}
	
	retcol = difTex  * diffuseComp * Kd  + ambTex * ambientComp  +  specularComp * specTex;


	}
	
	return retcol;


}




void main()
{
   
    vec3 col = vec3(0,0,0);
   
	
	col += CalcPointlights();
    
    vec3 amb = vColor.rgb;
    
   	if(difTexUse == 1) amb = texture2D(difModTex, vec2(1-vUv.x, 1-vUv.y)).rgb;
   	if(RasterMode != 1) col += amb * ambientLight;
    
    theColor = vec4(col, vColor.w);
    

}
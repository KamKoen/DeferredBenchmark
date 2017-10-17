#version 330

uniform sampler2D Texture;

uniform sampler2D Rock;

struct PointLight{

	vec4 position;	//w hat dis
	vec4 ambient;	//w hat quad
	vec4 diff;		//w hat lin
	vec4 specular;	//w hat const
	

};

struct SpotLight{

	vec3 position;
	vec3 ambient;
	vec3 diff;
	float cutoff;
	vec3 specular;
	vec3 direction;

};


in vec4 vColor;
in vec3 vNormal;
in vec3 vPosition;
in vec2 vUv;
in vec3 vTan;
in vec4 ShadowCoord;

uniform vec3 camPos;

uniform mat4 view;
uniform mat4 MV;
uniform mat4 normalM;
uniform mat4 normalMLight;

layout (std140) uniform pl{
	
	PointLight pLight[1024];

};

uniform SpotLight sLight[10];
uniform int LIGHT_MAX;
uniform int SPOT_MAX;

uniform float quadrAtt = 0.02f;
uniform float constAtt = 0.5f;
uniform float linAtt = 0.0f;


uniform vec3 worldAmbient = vec3(0,0,0);
uniform float matEmission = 0.3f;


uniform float matShin;

uniform int useShini = 0;

uniform float shininess;


uniform bool Shadows = false;

uniform int norTexUse = 0;

uniform int starMode = 0;


out vec4 theColor;

vec3 calcBumbNormal()
{

	vec3 Normal = normalize((normalM * vec4(vNormal, 1.0f)).xyz);
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
	vec3 surfNor;
	
  	if(norTexUse != 1)
    {
    	surfNor = normalize((normalM * vec4(vNormal, 1.0f)).xyz);	    
    }
    else 
    {
     
    	surfNor = calcBumbNormal();
    	
  		  
    }
	
	float shin = matShin;
	if(useShini ==1) shin = shininess;
	
	for(int i = 0; i < LIGHT_MAX; i++)
	{
	
		quadrAtt = pLight[i].ambient.w;
		linAtt = pLight[i].diff.w;
		constAtt = pLight[i].specular.w;
		
	   //diffuse and ambient
	   
	   	vec3 lpos = (view * vec4(pLight[i].position.xyz,1.0f)).xyz;
	    
	    vec3 vertPos = (MV * vec4(vPosition,1.0f)).xyz;
	  
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
	 	    
	 	    // |   from TiledLighting11_2010 @ Jason Stewart, Gareth Thomas
	 	    // V 
	 	    attenuation = -0.05 + 1.05/(1 + 20 * abc * abc); 	 	    
	    }
	  
	    
	    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
	    
	    col += (diffuseLightInt * pLight[i].diff.xyz ) * attenuation;
	    
	    col += pLight[i].ambient.xyz * attenuation;
	    
	    
	    
	    
	    vec3 cPos = (view * vec4(camPos,1.0f)).xyz;
	    
	   
	    vec3 cC = (cPos - vertPos);
	    //specular
	    vec3 R = normalize(reflect(-lightDirection, surfNor));
	    
	    
	 	 
	 	vec3 C = normalize(cC);
	 
	 
	    
	   float specular = max(0.0, dot(C, R));
	    
	   if(diffuseLightInt != 0){
	    
	    	float fspecular = pow(specular, shin);
	    	
	    	col += fspecular * pLight[i].specular.xyz *attenuation;
	    	
	    	}
	    	
	    	   
	}
	    
	    
	    
	
	//return surfNor ;
	return col * vec3(vColor.rgb);

	

}


vec3 CalcSpotlightsColor(sampler2D tex){

	vec3 retcol = vec3(0,0,0);
	
	vec3 col = vec3(0,0,0);
	
	
	float constAtt = 0.5f;
	float linAtt = 0;
	float quadrAtt = 0.02f;
	
	
	vec3 lpos;
	vec3 vertPos;
	vec3 lightToPixel;
	vec3 lightDirection;
	float distance;
	
	float dotProd = 0;
	
	
	float factor = 0;
	float bias = 0.0005f;
	for(int i = 0; i < SPOT_MAX; i++)
	{
	
	
	
		
		if(!(i == 0 && Shadows && ((texture(tex, ShadowCoord.xy / ShadowCoord.w)).z > (ShadowCoord.z - bias) / ShadowCoord.w)))
		{	
			
		
		
		
		
		
		
				lpos = (view * vec4(sLight[i].position,1.0f)).xyz;
			    
				vertPos = (MV * vec4(vPosition,1.0f)).xyz;
				
				lightToPixel = vertPos - lpos;
				
				distance = length(lightToPixel);
				
				lightToPixel = normalize(lightToPixel);
				
				lightDirection = normalize((normalMLight * vec4(sLight[i].direction,1.0f)).xyz);
				
				
				
				dotProd = dot(lightDirection, lightToPixel);
				
				//light Direction jetzt ändern, da die Formel für die Berechnung
				//die Richtung zum Licht braucht, und nicht andersherum
				
				lightDirection = -lightToPixel;
				
				
				if(dotProd >= sLight[i].cutoff)
				{
					
					col = vec3(0,0,0);
					
					
					
				   
				   
				   
				   
				   
				   
				   //diffuse and ambient
				   
				   
				   
				   
				  	//attenuationFactor 
				    
				    float attenuation = (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
				    
				    
				    
				    vec3 surfNor = normalize((normalM * vec4(vNormal, 1.0f)).xyz);
				    
				    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
				    
				    col += (diffuseLightInt * sLight[i].diff * vColor.rgb) / attenuation;
				    
				    col += sLight[i].ambient / attenuation;
				    
				 //   col += worldAmbient * matEmission;
				    
				    
				    vec3 cPos = (view * vec4(camPos,1.0f)).xyz;
				    
				    vec3 cC = (cPos - lpos);
				    
				    //specular
				    vec3 R = normalize(reflect(-lightDirection, surfNor));
				    
				    
				    
				 	vec3 C = normalize(cC);
				 
				   // float specular = max(0.0, dot(C,R));
				    
				   float specular = max(0.0, dot(C, R));
				    
				    if(diffuseLightInt != 0){
				    
				    	float fspecular = pow(specular, matShin);
				    	
				    	col += fspecular * sLight[i].specular /attenuation;
				    	
				    }
				    	
				    	
				    
				    
				    float n = 1.0;
					float f = 1000.0f;
					float z = texture2D(tex, ShadowCoord.xy / ShadowCoord.w).z;
					float d = (2.0 * n) / (f + n - z * (f - n));
					
						    
				    
				    factor = (dotProd - sLight[i].cutoff) / (1.0f - sLight[i].cutoff);
				    
				    retcol += col * factor;
				   
				    
				    	
				    	
				}	  
			
			
			
			
			
		}
	
			
			 
	}
	    
	return retcol;



}


vec3 CalcSpotlights(sampler2D tex)
{
	vec3 col = vec3(0,0,0);
	
	col = CalcSpotlightsColor(tex);
	    
	col += worldAmbient * matEmission; 
	
	return col;


}




void main()
{
   
    vec3 col = vec3(0,0,0);
   
	
	col += CalcPointlights();
    
 //   col += CalcSpotlights(Texture);
    

    theColor = vec4(col, vColor.w);
    

}
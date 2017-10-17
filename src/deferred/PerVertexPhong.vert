#version 330


struct PointLight{

	vec3 position;
	vec3 ambient;
	vec3 diff;
	vec3 specular;

};


in vec3 position;
in vec3 color;
in vec3 normal;

uniform vec3 camPos;

uniform mat4 MVP;
uniform mat4 view;
uniform mat4 normalM;
uniform mat4 MV;

uniform PointLight pLight[50];

uniform int LIGHT_MAX;


uniform float quadrAtt = 0.0001f;
uniform float constAtt = 1.0f;
uniform float linAtt = 0f;

uniform vec3 worldAmbient = vec3(0,0,0);
uniform float matEmission = 0.01f;

uniform float matShin;

out vec4 vColor;

void main()
{

	vec3 col = vec3(0,0,0);
	
	for(int i = 0; i < LIGHT_MAX; i++)
	{
   
   
	   //diffuse and ambient
	   
	   	vec3 lpos = (view * vec4(pLight[i].position,1.0f)).xyz;
	    
	    vec3 vertPos = (MV * vec4(position,1.0f)).xyz;
	  
	  	//attenuationFactor 
	    float distance = length(lpos-vertPos);
	    float attenuation = (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
	    
	  
	  
	    vec3 lightDirection = normalize(lpos - vertPos);
	    
	    vec3 surfNor = normalize((normalM * vec4(normal, 1.0f)).xyz);
	    
	    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
	    
	    col += diffuseLightInt * pLight[i].diff  * color /attenuation;
	    
	   // col += pLight[i].ambient /attenuation;
	    
	    col += worldAmbient * matEmission;
	    
	    vec3 cPos = (view * vec4(camPos,1.0f)).xyz;
	    
	    vec3 cC = -cPos;
	    
	    //specular
	    vec3 R = normalize(reflect(-lightDirection, surfNor));
	    
	    
	    
	 	vec3 C = normalize(cC);
	 
	   // float specular = max(0.0, dot(C,R));
	    
	   float specular = max(0.0, dot(surfNor, R));
	    
	    if(diffuseLightInt != 0){
	    
	    	float fspecular = pow(specular, matShin);
	    	
	    	col += fspecular * pLight[i].specular /attenuation;
	    	   
	    }
    
    }
    
    
    
    
    
    vColor = vec4(col, 1.0f);
    
    gl_Position = MVP * vec4(position, 1.0f);
}
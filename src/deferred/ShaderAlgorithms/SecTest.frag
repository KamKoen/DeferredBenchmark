#version 330


uniform sampler2D normalTex;
uniform sampler2D diffTex;
uniform sampler2D posTex;
uniform sampler2D depthTex;

uniform vec3 camPos;

struct PointLight{

	vec3 position;
	vec3 ambient;
	vec3 diff;
	vec3 specular;

};

struct SpotLight{

	vec3 position;
	vec3 ambient;
	vec3 diff;
	float cutoff;
	vec3 specular;
	vec3 direction;

};



out vec4 theColor;

in vec2 TexCoord0;

uniform int mode = 0;

uniform PointLight pLight[120];
uniform SpotLight sLight[1];
uniform int LIGHT_MAX;
uniform int SPOT_MAX;

uniform float quadrAtt = 0.02f;
uniform float constAtt = 0.5f;
uniform float linAtt = 0f;

uniform mat4 view;
uniform mat4 normalMLight;

uniform float A;
uniform float B;
uniform float C;
uniform float D;



uniform vec3 worldAmbient = vec3(0,0,0);
uniform float matEmission = 0.3f;


uniform float matShin;






vec4 calcViewSpace( float depthZ)
{
	
	
	vec3 view = (0,0,0);
	
	float nDcz = depthZ * 2.0f - 1.0f;
	
	view.z = -D / (nDcz + C);
	
	view.x = (nDcz * -view.z) / A;
	
	view.y = (nDcz * -view.z) / B;
	
	return vec4(view,1);


}





vec3 CalcPointlights()
{
	vec3 col = vec3(0,0,0);
	
	//vec4 vPosition = texture2D(posTex, TexCoord0.st);
	vec4 vPosition = calcViewSpace(TexCoord0.x, TexCoord0.y,texture2D(depthTex, TexCoord0.st).x);
	
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
	
	
	vec4 vNormal = texture2D(normalTex, TexCoord0.st) - 0.5f;
	vNormal = vNormal / 0.5f;
	
	for(int i = 0; i < LIGHT_MAX; i++)
	{
	
		
		
	   //diffuse and ambient
	   
	   	vec3 lpos = (view * vec4(pLight[i].position,1.0f)).xyz;
	    
	    vec3 vertPos = vPosition.xyz;
	  
	    vec3 lightDirection = normalize(lpos - vertPos);
	   
	   
	  	//attenuationFactor 
	    float distance = length(lpos-vertPos);
	    float attenuation = (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
	    
	    
	    
	    vec3 surfNor = normalize(vNormal.xyz);
	    
	    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
	    
	    col += (diffuseLightInt * pLight[i].diff * vColor.xyz) / attenuation;
	    
	    col += pLight[i].ambient / attenuation;
	    
	    col += worldAmbient * matEmission;
	    
	    
	    vec3 cPos = (view * vec4(camPos,1.0f)).xyz;
	    
	    vec3 cC = (cPos - lpos);
	    
	    //specular
	    vec3 R = normalize(reflect(-lightDirection, surfNor));
	    
	    
	    
	 	vec3 C = normalize(cC);
	 
	   // float specular = max(0.0, dot(C,R));
	    
	   float specular = max(0.0, dot(C, R));
	    
	    if(diffuseLightInt != 0){
	    
	    	float fspecular = pow(specular, matShin);
	    	
	    	col += fspecular * pLight[i].specular /attenuation;
	    	
	    	}
	    	
	    	   
	}
	    
	    
	    
	    
	
	return col;


}



vec3 CalcSpotlightsColor(){

	vec3 retcol = vec3(0,0,0);
	
	vec3 col = vec3(0,0,0);
	
	
	vec4 vPosition = calcViewSpace(TexCoord0.x, TexCoord0.y,texture2D(depthTex, TexCoord0.st).x);
	
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
	
	vec4 vNormal = texture2D(normalTex, TexCoord0.st) - 0.5f;
	vNormal = vNormal / 0.5f;
	
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
	
	
		lpos = (view * vec4(sLight[i].position,1.0f)).xyz;
	    
		vertPos = vPosition.xyz;
		
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
		    
		    
		    
		    vec3 surfNor = vNormal.xyz;
		    
		    float diffuseLightInt = max(0, dot(surfNor, lightDirection));
		    
		    col += (diffuseLightInt * sLight[i].diff * vColor.xyz) / attenuation;
		    
		    col += sLight[i].ambient / attenuation;
		    
		    col += worldAmbient * matEmission;
		    
		    
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
		    	
		    	
		    
		    
				    
		    
		    factor = (dotProd - sLight[i].cutoff) / (1.0f - sLight[i].cutoff);
		    
		    retcol += col * factor;
		   
		    
		    	
		    	
		}	  
			
			
			
			
			
			 
	}
	    
	return retcol;



}


vec3 CalcSpotlights()
{
	vec3 col = vec3(0,0,0);
	
	col = CalcSpotlightsColor();
	    
	
	return col;


}




void main()
{

	vec3 col = vec3(0,0,0);
   
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
	if(vColor.w == 0) col = vec3(vColor.xyz);
	else
	{
		col += CalcPointlights();
    
    	col += CalcSpotlights();
  	}
    
    theColor = vec4(col, 1.0f);
	 //  theColor = texture2D(diffTex, TexCoord0.st);
	// theColor = texture2D(normalTex, TexCoord0.st);
}















}
#version 430


uniform sampler2D normalTex;
uniform sampler2D diffTex;
uniform sampler2D depthTex;
uniform sampler2D specTex;


uniform int useShini = 0;
uniform vec3 camPos;


uniform int starMode = 0;


uniform int RasterMode = 0;


uniform float A;
uniform float B;
uniform float C;
uniform float D;



layout(std430, binding = 3)  buffer zBuf
{
	float zB[]; //min max
};

layout(std430, binding = 4)  buffer infB
{
	int info[];
};

layout(std430, binding = 5)  buffer indB
{
	int indexB[];
};


uniform int MAX_LIGHT_PER_TILE;
uniform int width;
uniform int height;

uniform int tileWidth;
uniform int tileHeight;


uniform vec3 ambientLight;













struct PointLight{

	vec4 position;	//.w ist distance
	vec4 ambient;	//.w ist quad
	vec4 diff;		//.w ist lin
	vec4 specular;	//.w ist const
};





out vec4 theColor;

in vec2 TexCoord0;

uniform int mode = 0;

layout (std140) uniform pl{
	
	PointLight pLight[1024];

};

uniform int LIGHT_MAX;




uniform mat4 view;
uniform mat4 normalMLight;






vec4 calcViewSpace(float x, float y, float depthZ)
{
	
	
	x = x * 2.0 - 1.0;
	
	y = y * 2.0 - 1.0;
	vec3 view = vec3(0,0,0);
	
	float nDcz = depthZ * 2.0 - 1.0;
	
	view.z = -D / (nDcz + C);
	
	view.x = (x * -view.z) / A;
	
	view.y = (y * -view.z) / B;
	
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
	
	
		
	vec4 vNormal = texture2D(normalTex, TexCoord0.st);
	
	
	
	
	
	int curX = int(gl_FragCoord.x/tileWidth);
	int curY = int(gl_FragCoord.y/tileHeight);
	
	
	//what tile
	
	int offset = info[(curX + curY * (width/tileWidth)) * 2];
	int count = info[(curX + curY * (width/tileWidth)) * 2 + 1];
	
	if(offset < 0) return col;
	
	
	vec4 shininess = texture2D(specTex, TexCoord0.st);
	
	
	
	
	int i = 0;
	
	vec3 specularComp = vec3(0,0,0);
	vec3 diffuseComp = vec3(0,0,0);
	vec3 ambientComp = vec3(0,0,0);
	

	
	
	for(int j = 0; j < count; j++)
	{
		i = indexB[offset + j];
		if(i < 0) break;
		
		quadrAtt = pLight[i].ambient.w;
		linAtt = pLight[i].diff.w;
		constAtt = pLight[i].specular.w;
		
		
	   //diffuse and ambient
	   
	   	vec3 lpos = (view * vec4(pLight[i].position.xyz,1.0)).xyz;
	    
	    vec3 vertPos = vPosition.xyz;
	  
	    vec3 lightDirection = normalize(lpos - vertPos);
	   
	   
	  	//attenuationFactor 
	    float distance = length(lpos-vertPos);
	    if(distance > pLight[i].position.w) continue;
	    float attenuation = (constAtt + linAtt * distance + quadrAtt* pow(distance,2));
	    
	    if(attenuation != 0) attenuation = 1/attenuation;
	    
	    float distanceFromL = pLight[i].position.w;
	    
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
	    
	    diffuseComp += (diffuseLightInt * pLight[i].diff.xyz)* attenuation;
	    
	    ambientComp += pLight[i].ambient.xyz * attenuation;
	    
	    
	    
	    
	    vec3 cPos = (view * vec4(camPos,1.0)).xyz;
	    
	    vec3 cC = (cPos - vertPos);
	    
	    //specular
	    vec3 R = normalize(reflect(-lightDirection, surfNor));
	    
	    
	    
	 	vec3 C = normalize(cC);
	 
	    
	   float specular = max(0.0, dot(C, R));
	    
	    if(diffuseLightInt != 0){
	    
	    	float fspecular = pow(specular, shininess.w);
	    	
	    	specularComp += fspecular * pLight[i].specular.xyz *attenuation;
	    	
	    	}
	    	
	    	   
	}
	    
	int offsetZB = int(curY*(width/tileWidth) * 2 + curX * 2);
	float t = zB[offsetZB];
	if(RasterMode == 1) return vec3(float(count) / float(LIGHT_MAX),float(count) / float(LIGHT_MAX),float(count) / float(LIGHT_MAX));    
	   
	
	
	
	vec3 retcol = diffuseComp * vColor.xyz *shininess.y  + shininess.x * specularComp + vColor.xyz * ambientComp;
	
	return retcol;


}









void main()
{

	vec3 col = vec3(0,0,0);
   
	vec4 vColor = texture2D(diffTex, TexCoord0.st);
	if(vColor.w == -1) col = vec3(vColor.xyz);
	else
	{
		col += CalcPointlights();
    	
    	if(RasterMode != 1)col += vColor.rgb * ambientLight;
    
   
  	}
    
    theColor = vec4(col, 1.0);
   
}
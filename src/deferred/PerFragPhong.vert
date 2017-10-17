#version 330


in vec3 position;
in vec3 color;
in vec3 normal;
layout(location = 3) in vec2 uv;
layout(location = 4) in vec3 tangent;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 MVP;
uniform mat4 sLightVP[10];


uniform vec4 setCol = vec4(-1,-1,-1, -1);


out vec4 vColor;
out vec3 vNormal;
out vec3 vPosition;
out vec4 ShadowCoord;
out vec2 vUv;
out vec3 vTan;

void main() {



	if(setCol.x == -1)vColor = vec4(color,1);
	else vColor = setCol;
	vNormal = normal;
	
	vUv = uv;
	vTan = (MVP * vec4(tangent,0)).xyz;
	vPosition = position;
	
    	gl_Position = MVP * vec4(position, 1.0f);
    
    
    

}
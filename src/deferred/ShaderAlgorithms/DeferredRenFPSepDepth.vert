#version 330


in vec3 position;
in vec3 color;
in vec3 normal;
in vec2 uv;
in vec3 tangent;



uniform int norTexUse;

uniform mat4 normalM;
uniform mat4 MVP;
uniform mat4 MV;

uniform vec4 setCol = vec4(-1,-1,-1,-1);

out vec3 out_normal;
out vec4 out_color;
out vec4 out_pos;
out vec2 vUv;
out vec3 vTan;



void main()
{


	vUv = uv;
	vTan = (MVP * vec4(tangent,0)).xyz;


	if(setCol.x == -1)out_color = vec4(color,1);
	else out_color = setCol;
	if(norTexUse == 1)
	{
		out_normal = normalize(normal);
	}
	else
	{
		out_normal = normalize((normalM * vec4(normal,0)).xyz);
	}
	
	out_pos = MVP * vec4(position,1);
	
	gl_Position = out_pos;
	
	
	
	






}
#version 120

in vec3 position;
in vec3 color;

uniform mat4 projection;
uniform mat4 MV;
uniform mat4 MVP;

uniform vec3 setCol = vec3(-1,-1,-1);



void main()
{
	gl_FrontColor = gl_Color;
    gl_BackColor = gl_Color;
    
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
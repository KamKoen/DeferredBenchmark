#version 330

in vec3 position;



uniform mat4 MVP;

void main()
{

	
    gl_Position = MVP * vec4(position, 1);
 
}
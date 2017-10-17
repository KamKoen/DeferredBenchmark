#version 330

in vec3 position;
in vec3 color;

uniform mat4 projection;
uniform mat4 MV;
uniform mat4 MVP;

uniform vec3 setCol = vec3(-1,-1,-1);

out vec4 vColor;

void main()
{
	if(setCol.x == -1)
	{
    	vColor = vec4(color, 1.0f);
    }
    else 
    {
    	vColor = vec4(setCol,1.0f);
    
    }
    
    
    gl_Position = MVP * vec4(position, 1.0f);
}
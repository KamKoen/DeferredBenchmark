#version 330

in vec3 position;
in vec2 tex;


uniform mat4 ortho;
uniform vec3 translate = vec3(0,0,0);

uniform int width;
uniform int height;

out vec2 TexCoord0;

void main()
{

	
    TexCoord0 = tex;
    gl_Position = ortho* vec4(position.x + translate.x, position.y + translate.y, position.z, 1.0);
}
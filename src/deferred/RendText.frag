#version 330


uniform sampler2D Texture;

out vec4 theColor;

in vec2 TexCoord0;
uniform int mode = 0;

void main()
{
    if (mode == 0)		//nothing special
    {
    	theColor = texture2D(Texture, TexCoord0.st);
	}
	else if(mode == 4) //visualize depth texture
	{
	
		float n = 1.0f;
		float f = 4000.0f;
		float z = texture2D(Texture, TexCoord0.st).x;
		float d = (2.0 * n) / (f + n - z * (f - n));
		if(d == 1.0f) theColor = vec4(1,0,0,0);
		theColor = vec4(d,d,d,1.0);
	
	}


}
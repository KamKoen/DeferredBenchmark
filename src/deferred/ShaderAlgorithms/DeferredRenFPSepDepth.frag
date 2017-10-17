#version 330

uniform sampler2D Rock;










uniform sampler2D difModTex;
uniform sampler2D ambModTex;
uniform sampler2D specModTex;

uniform int difTexUse=0;
uniform int ambTexUse=0;
uniform int specTexUse=0;

uniform vec3 Kd;
uniform vec3 Ka;
uniform float Ns;


uniform int mtlUse = 0;
















uniform int width;
uniform int height;

uniform int tileWidth;
uniform int tileHeight;

in vec3 out_normal;

in vec4 out_color;

in vec2 vUv;
in vec3 vTan;

uniform int norTexUse = 0;

uniform int useShini = 0;

uniform float shininess;

layout(location=0) out vec4 diffuse;
layout(location=1) out vec4 nor;
layout(location=2) out vec4 shin;
layout(location=3) out float ddd;




uniform mat4 normalM;

uniform int isLight = 0;

vec3 calcBumbNormal()
{

	//from http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
		
	vec3 Normal = normalize((normalM * vec4(out_normal, 1.0)).xyz);
    vec3 Tangent = normalize(vTan);
    Tangent = normalize(Tangent - dot(Tangent, Normal) * Normal);
    vec3 Bitangent = cross(Tangent, Normal);
    vec3 BumpMapNormal = texture(Rock, vec2(vUv.x, 1- vUv.y)).xyz;
    BumpMapNormal = 2.0 * BumpMapNormal - vec3(1.0, 1.0, 1.0);
    vec3 NewNormal;
    mat3 TBN = mat3(Tangent, Bitangent, Normal);
    NewNormal = TBN * BumpMapNormal;
    NewNormal = normalize(NewNormal);
    return NewNormal;


}


void main()
{

	
	vec3 specTex = vec3(1,1,1);
	vec4 difTex = out_color;

	if(specTexUse == 1) specTex = texture2D(specModTex, vec2(1-vUv.x, 1-vUv.y)).rgb; 
	if(difTexUse == 1) difTex = texture2D(difModTex, vec2(1-vUv.x, 1-vUv.y));
	
		
	diffuse = difTex;
	//diffuse = vec4(0.5f,0.5f,0.5f,1);
	if(isLight == 1)diffuse = vec4(out_color.rgb,-1);
	//else diffuse = vec4(out_color,1.0);
	
	if(norTexUse == 0)
	{
	nor = vec4(out_normal , 0.0);
	}
	else
	{
	nor = vec4(calcBumbNormal(),0.0);
	}

	float shinichi = 10;
	float difScal = 1;
	float ambScal = 1;
	
	if(useShini == 1) shinichi = shininess;

	if(mtlUse == 1) 
	{
	shinichi = Ns;
	difScal = Kd.x;
	}
		
	
	
	shin = vec4(specTex.x, difScal, ambScal ,shinichi);
	
	ddd = gl_FragCoord.z;


}
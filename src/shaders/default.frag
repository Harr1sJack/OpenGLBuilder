#version 430 core

out vec4 FragColor;

in vec2 TexCoord;
in vec3 Normal;
in vec3 crntPos;

uniform int light;
uniform vec3 viewPos;
uniform sampler2D diffuseTexture;

vec4 lightColor = vec4(1.0f,1.0f,1.0f,1.0f);

vec4 DirectionalLight()
{
    float ambient = 0.2f;
    float specularLight = 0.2f;

    vec3 normals = normalize(Normal);
    vec3 lightdir = normalize(vec3(10.0f,1.0f,0.0f));
    float diffuse = max(dot(normals,lightdir),0.0f);

    vec3 viewdir = normalize(viewPos - crntPos);
    vec3 reflectiondir = reflect(-lightdir,normals);
    float specAmount = pow(max(dot(viewdir,reflectiondir),0.0f),6);
    float specular = specAmount * specularLight;

    vec4 Color = vec4(1,1,1,1);

    if(light == 1)
    {
        Color = texture(diffuseTexture,TexCoord)* lightColor * (diffuse + ambient) + specular;
    }
    else
    {
        Color = texture(diffuseTexture,TexCoord);
    }

    return Color;
}

void main() {
    FragColor = DirectionalLight();
}

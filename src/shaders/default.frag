#version 460 core

out vec4 FragColor;

in vec2 TexCoord;
in vec3 Normal;
in vec3 crntPos;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform sampler2D diffuseTexture;

vec4 lightColor = vec4(1.0f,1.0f,1.0f,1.0f);

vec4 DirectionalLight()
{
    float ambient = 0.2f;
    float specularLight = 0.5f;

    vec3 normals = normalize(Normal);
    vec3 lightdir = normalize(vec3(0.0f,1.0f,0.0f));
    float diffuse = max(dot(normals,lightdir),0.0f);

    vec3 viewdir = normalize(viewPos - crntPos);
    vec3 reflectiondir = reflect(-lightdir,normals);
    float specAmount = pow(max(dot(viewdir,reflectiondir),0.0f),16);
    float specular = specAmount * specularLight;
    return texture(diffuseTexture,TexCoord) * lightColor * (diffuse + ambient) + specular;
}

void main() {
    FragColor = DirectionalLight();
}

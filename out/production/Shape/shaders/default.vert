#version 460 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aTexCoord;
layout(location = 2) in vec3 aNormal;

out vec2 TexCoord;
out vec3 Normal;
out vec3 crntPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {

    crntPos = vec3(model * vec4(aPos, 1.0));
    TexCoord = aTexCoord;
    //reconfiguring the normals,if the model is being scaled or rotated
    Normal = mat3(transpose(inverse(model))) * aNormal;
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}

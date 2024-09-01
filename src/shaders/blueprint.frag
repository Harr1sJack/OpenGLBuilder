#version 430 core

in vec3 FragPos;
in vec3 Normal;

out vec4 FragColor;

uniform bool isSelected;

void main()
{
    vec3 color = vec3(1,1,1);
    if (isSelected) {
        color = vec3(1,0,0);
    }

    FragColor = vec4(color, 1);
}

#version 120

varying vec3 normalDirection;
varying vec3 vertexPosition;

void main() {

    // Transform normal into eye coordinates
    normalDirection =
        normalize(gl_NormalMatrix * gl_Normal);

    // Transform vertex into eye coordinates
    vec4 position =
        gl_ModelViewMatrix * gl_Vertex;

    vertexPosition = position.xyz;

    // Calculate final vertex position
    gl_Position = ftransform();
}


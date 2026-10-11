#version 120

varying vec4 vertexColor;

void main() {

    vec3 normal = normalize(gl_NormalMatrix * gl_Normal);

    vec4 position = gl_ModelViewMatrix * gl_Vertex;

    vec3 lightDirection = normalize(
        vec3(gl_LightSource[0].position - position)
    );

    vec3 viewDirection = normalize(-position.xyz);

    vec3 reflectionDirection = reflect(
        -lightDirection, normal
    );

    // Ambient lighting
    vec4 ambient =
        gl_FrontLightProduct[0].ambient;

    // Diffuse lighting
    float diffuseFactor = max(
        dot(normal, lightDirection), 0.0
    );

    vec4 diffuse =
        gl_FrontLightProduct[0].diffuse
*	diffuseFactor;

    // Specular lighting
    float specularFactor = 0.0;

    if (diffuseFactor > 0.0) {
        specularFactor = pow(
            max(dot(reflectionDirection,
                    viewDirection), 0.0),
            gl_FrontMaterial.shininess
        );
    }

    vec4 specular =
        gl_FrontLightProduct[0].specular
*	specularFactor;

    vertexColor = ambient + diffuse + specular;
    vertexColor.a = 1.0;

    gl_Position = ftransform();
}


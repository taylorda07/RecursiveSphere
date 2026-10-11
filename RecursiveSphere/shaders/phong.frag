#version 120

varying vec3 normalDirection;
varying vec3 vertexPosition;

void main() {

    vec3 normal = normalize(normalDirection);

    vec3 lightDirection = normalize(
        gl_LightSource[0].position.xyz
-	vertexPosition
    );

    vec3 viewDirection =
        normalize(-vertexPosition);

    vec3 reflectionDirection =
        reflect(-lightDirection, normal);

    // Ambient component
    vec4 ambient =
        gl_FrontLightProduct[0].ambient;

    // Diffuse component
    float diffuseFactor = max(
        dot(normal, lightDirection), 0.0
    );

    vec4 diffuse =
        gl_FrontLightProduct[0].diffuse
*	diffuseFactor;

    // Specular component
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

    vec4 finalColor =
        ambient + diffuse + specular;

    finalColor.a = 1.0;

    gl_FragColor = finalColor;
}


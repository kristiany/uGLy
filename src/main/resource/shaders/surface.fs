uniform sampler2D sampler0;
varying vec4 diffuse,ambient;
varying vec3 normal,lightDir,halfVector;

void main(void) {
	vec3 n,halfV,lightDir;
    float NdotL,NdotHV;

    lightDir = vec3(gl_LightSource[0].position);

    /* The ambient term will always be present */
    vec4 color = ambient;
    /* a fragment shader can't write a varying variable, hence we need
    a new variable to store the normalized interpolated normal */
    n = normalize(normal);
    /* compute the dot product between normal and ldir */

    NdotL = max(dot(n,lightDir),0.0);
	if (NdotL > 0.0) {
		color += diffuse * NdotL;
		halfV = normalize(halfVector);
		NdotHV = max(dot(n,halfV),0.0);
		color += gl_FrontMaterial.specular *
				gl_LightSource[0].specular *
				pow(NdotHV, gl_FrontMaterial.shininess);
	}

	float af = gl_FrontMaterial.diffuse.a;
    vec4 texel = texture2D(sampler0, gl_TexCoord[0].st);
    vec3 ct = texel.rgb;
    vec3 cf = color.rgb * 0.1;
    float at = texel.a;
    gl_FragColor = texel;//vec4(ct * cf, at * af);
}
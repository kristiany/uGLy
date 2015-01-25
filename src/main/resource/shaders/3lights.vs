/**
 **   __ __|_  ___________________________________________________________________________  ___|__ __
 **  //    /\                                           _                                  /\    \\
 ** //____/  \__     __ _____ _____ _____ _____ _____  | |     __ _____ _____ __        __/  \____\\
 **  \    \  / /  __|  |     |   __|  _  |     |  _  | | |  __|  |     |   __|  |      /\ \  /    /
 **   \____\/_/  |  |  |  |  |  |  |     | | | |   __| | | |  |  |  |  |  |  |  |__   "  \_\/____/
 **  /\    \     |_____|_____|_____|__|__|_|_|_|__|    | | |_____|_____|_____|_____|  _  /    /\
 ** /  \____\                       http://jogamp.org  |_|                              /____/  \
 ** \  /   "' _________________________________________________________________________ `"   \  /
 **  \/____.                                                                             .____\/
 **
 ** Simple generic vertex shader using mvp transformation and diffuse and specular lighting
 ** based on the primary color and three colored lights with hardcoded specular exponent.
 **
 **/

uniform float time;
varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;

void main(void) {
    vec4 v = vec4(gl_Vertex);
    //v.z = sin(5.0*v.x + time)*0.25;
    gl_Position = gl_ModelViewProjectionMatrix * v;

    gl_TexCoord[0] = gl_MultiTexCoord0;

    /* first transform the normal into eye space and
    normalize the result */
    normal = normalize(gl_NormalMatrix * gl_Normal);

    /* pass the halfVector to the fragment shader */
    halfVector = gl_LightSource[0].halfVector.xyz;

    /* Compute the diffuse, ambient and globalAmbient terms */
    diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
    ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
    ambient += gl_LightModel.ambient * gl_FrontMaterial.ambient;
}
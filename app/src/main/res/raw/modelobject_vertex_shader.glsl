uniform mat4 u_MVPMatrix;
uniform vec3 u_Ka;
uniform vec3 u_Kd;
uniform vec3 u_Ks;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

void main()                    
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
}
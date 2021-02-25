precision mediump float;

uniform sampler2D u_TexturesSampler;
varying vec2 v_TextureCoordinates;

void main()
{
    vec4 textureColor = texture2D(u_TexturesSampler, v_TextureCoordinates);
    gl_FragColor = textureColor;
}
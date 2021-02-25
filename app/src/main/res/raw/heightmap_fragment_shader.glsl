precision mediump float; 

uniform sampler2D u_TexturesSampler;

varying vec3 v_Color;
varying vec2 v_TextureCoordinates;
varying float v_Ratio;
	    	   								
void main()                    		
{
    vec4 textureColor = texture2D(u_TexturesSampler, v_TextureCoordinates);

    gl_FragColor = textureColor * (1.0 - v_Ratio);
    gl_FragColor *= vec4(v_Color, 1.0);
}

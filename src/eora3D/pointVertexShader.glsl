#version 300 es
layout(location = 0) in vec4 vertex;
uniform mat4 modelView;
uniform float scale;
uniform vec4 color;
out vec4 v_color;
void main(void) {
 	gl_Position = vertex;
 	gl_Position.x *= scale;
 	gl_Position.y *= scale;
 	gl_Position.z *= scale;
 	gl_Position = modelView * gl_Position;
 	gl_PointSize = 1.0;
 	v_color = color;
}

#version 300 es
layout(location = 0) in vec4 vertex;
layout(location = 1) in vec4 color;
uniform mat4 modelView;
out vec4 v_color;
void main(void) {
  gl_Position = modelView * vertex;
  v_color = color;
}
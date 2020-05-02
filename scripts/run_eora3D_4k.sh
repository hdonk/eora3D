#!/bin/sh
JAVA_BIN=/home/nickh/java/jdk-13.0.1/bin/java
RUN_ROOT=.
cd eora3D

${JAVA_BIN} -Djava.library.path=${RUN_ROOT}/libs -Dfile.encoding=UTF-8 -Dsun.java2d.uiScale=2.5 -classpath \
${RUN_ROOT}/../jPLY/bin:${RUN_ROOT}/libs/joml-1.9.14.jar:${RUN_ROOT}/libs/bridj-0.7.0.jar:${RUN_ROOT}/libs/slf4j-simple-2.0.0-alpha1.jar:${RUN_ROOT}/libs/slf4j-api-2.0.0-alpha1.jar:${RUN_ROOT}/libs/webcam-capture-0.3.12.jar:${RUN_ROOT}/libs/tinyb.jar:${RUN_ROOT}/libs/linux/lwjgl-glfw-natives-linux.jar:${RUN_ROOT}/libs/linux/lwjgl-natives-linux.jar:${RUN_ROOT}/libs/linux/lwjgl-opengles-natives-linux.jar:${RUN_ROOT}/libs:${RUN_ROOT}/libs/lwjgl.jar:${RUN_ROOT}/libs/lwjgl-egl.jar:${RUN_ROOT}/libs/lwjgl-glfw.jar:${RUN_ROOT}/libs/lwjgl-opengles.jar:${RUN_ROOT}/bin \
eora3D.eora3D \
-Xms1024m -Xmx2048m


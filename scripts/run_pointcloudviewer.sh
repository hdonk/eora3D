#!/bin/sh
JAVA_BIN=/home/nickh/java/jdk-13.0.1/bin/java
RUN_ROOT=.
cd pointCloudViewer
${JAVA_BIN} \
-Djava.library.path=${RUN_ROOT}/libs/linux:${RUN_ROOT}/libs/linux:${RUN_ROOT}/libs/linux:${RUN_ROOT}/libs/linux:${RUN_ROOT}/../eora3D/libs \
-Dfile.encoding=UTF-8 -classpath \
${RUN_ROOT}/bin:${RUN_ROOT}/libs/joml-1.9.14.jar:${RUN_ROOT}/libs/lwjgl.jar:${RUN_ROOT}/libs/lwjgl-egl.jar:${RUN_ROOT}/libs/lwjgl-glfw.jar:${RUN_ROOT}/libs/lwjgl-opengles.jar:${RUN_ROOT}/../eora3D/libs/linux/lwjgl-glfw-natives-linux.jar:${RUN_ROOT}/../eora3D/libs/linux/lwjgl-natives-linux.jar:${RUN_ROOT}/../eora3D/libs/linux/lwjgl-opengles-natives-linux.jar:${RUN_ROOT}/../eora3D/bin:${RUN_ROOT}/jPLY/bin \
pointCloudViewer.pointCloudViewer


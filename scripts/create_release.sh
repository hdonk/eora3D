#!/bin/bash
set -e
cp eora3D/scripts/*sh .
mkdir -p pointCloudViewer/src/eora3D/
cp eora3D/src/eora3D/*glsl pointCloudViewer/src/eora3D/
FILES=`find eora3D jPLY/ pointCloudViewer/ -iname \*glsl -or -iname \*txt -or -iname LICENSE -or -iname \*class -or -iname \*\.so\* -or -iname \*\.dll -or -iname \*jar -or -iname \*glsl`
FILES=`echo "$FILES" | grep -v -e javadoc -e source`
tar cvJf e3d_release.tar.xz ${FILES} run_*.sh eora3D/Documents/*html eora3D/Documents/*png

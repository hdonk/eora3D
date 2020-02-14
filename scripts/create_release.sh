#!/bin/sh
tar cvJf e3d_release.tar.xz `find eora3D jPLY/ pointCloudViewer/ -iname \*glsl -or -iname \*txt -or -iname LICENSE -or -iname \*class -or -iname \*\.so\* -or -iname \*\.dll`

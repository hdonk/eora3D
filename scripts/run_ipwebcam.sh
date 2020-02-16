#!/bin/sh
set -e
sudo modprobe -r v4l2loopback_dc
sudo modprobe v4l2loopback
ffmpeg -re -i http://10.10.10.104:8080/video -vcodec rawvideo -pix_fmt yuv420p -threads 0 -f v4l2 /dev/video1

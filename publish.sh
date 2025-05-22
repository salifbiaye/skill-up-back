#!/bin/bash

# Check if Docker username is provided
if [ -z "$1" ]; then
    echo "Please provide your Docker Hub username as an argument"
    echo "Usage: ./publish.sh <docker-username>"
    exit 1
fi

DOCKER_USERNAME=$1

# Build the image
echo "Building Docker image..."
docker build -t ${DOCKER_USERNAME}/skill-up:latest .

# Push the image
echo "Pushing to Docker Hub..."
docker push ${DOCKER_USERNAME}/skill-up:latest

echo "Done! Your friend can now pull the image using:"
echo "docker pull ${DOCKER_USERNAME}/skill-up:latest" 
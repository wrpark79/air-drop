#!/bin/sh

docker rm -f airdrop

if [ -f "./airdrop.img" ]; then
    docker load < ./airdrop.img
fi

docker run -d --restart always -p 8080:8080 --name airdrop \
    -v `pwd`/config/application.properties:/app/config/application.properties \
    airdrop:latest

docker logs -f airdrop

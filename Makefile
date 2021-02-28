.PHONY: build run dist docker docker-dist clean

VERSION := $(shell cat gradle.properties | cut -d'=' -f2)
JAR_DIST := airdrop-jar-$(VERSION).tar
DOCKER_DIST := airdrop-docker-$(VERSION).tar

all: build

build:
	@./gradlew clean build

run:
	@./gradlew clean bootRun

dist: build
	@echo $(VERSION) > VERSION
	@tar cvzf $(JAR_DIST) -C ./build/libs airdrop-$(VERSION).jar
	@tar rvf $(JAR_DIST) -C ./bin run_jar.sh
	@tar rvf $(JAR_DIST) ./config VERSION

docker:
	@docker build -t airdrop:$(VERSION) -t airdrop:latest .

docker-dist: docker
	@echo $(VERSION) > VERSION
	@docker save airdrop:latest > airdrop.img
	@tar cvf $(DOCKER_DIST) ./airdrop.img ./config VERSION
	@tar rvf $(DOCKER_DIST) -C ./bin run_docker.sh

clean:
	@rm -rf build/ *.jar *.img *.tar VERSION

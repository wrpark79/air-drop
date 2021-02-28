.PHONY: clean build run dist

VERSION := `cat gradle.properties | cut -d'=' -f2`

all: build

build:
	@./gradlew clean build

run:
	@./gradlew clean bootRun

dist:
	@docker build -t airdrop:$(VERSION) -t airdrop:latest .

clean:
	@rm -rf build/

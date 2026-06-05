ENV_FILE := $(if $(wildcard .env.local),--env-file .env.local,--env-file .env)

build:
	docker compose $(ENV_FILE) build

up:
	docker compose $(ENV_FILE) up -d

down:
	docker compose $(ENV_FILE) down

ifneq (,$(wildcard ./.env))
    include .env
    export
endif

ifneq (,$(wildcard ./.env.local))
    include .env.local
    export
endif

up:
	docker compose up -d

down:
	docker compose down

ps:
	docker compose ps -a
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
	docker compose down -v

ps:
	docker compose ps -a

mongosh:
	docker compose exec mongo mongosh -u $(MONGO_USER) -p $(MONGO_PASSWORD) --authenticationDatabase $(MONGO_DATABASE)
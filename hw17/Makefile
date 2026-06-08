ENV_FILE := $(if $(wildcard .env.local),.env.local,.env)

build:
	docker compose --env-file $(ENV_FILE) build

up:
	docker compose --env-file $(ENV_FILE) up -d

down:
	docker compose --env-file $(ENV_FILE) down

k8s-build:
	eval $$(minikube docker-env) && BACKEND_URL="" docker compose --env-file .env build

k8s-up:
	set -a && . ./.env && $(if $(wildcard .env.local),. ./.env.local &&,) set +a && \
	find k8s/ -name "*.yaml" | xargs -I{} sh -c 'envsubst < {} | kubectl apply -f -'

k8s-down:
	set -a && . ./.env && $(if $(wildcard .env.local),. ./.env.local &&,) set +a && \
	find k8s/ -name "*.yaml" | xargs -I{} sh -c 'envsubst < {} | kubectl delete -f -'

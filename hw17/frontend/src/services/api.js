const backendUrl = import.meta.env.VITE_BACKEND_URL ?? '';

export function apiFetch(path, options) {
    return fetch(`${backendUrl}${path}`, options);
}

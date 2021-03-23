export function setToken(token) {
    localStorage.setItem("token", "Bearer " + token);
}

export function getToken() {
    return localStorage.getItem("token");
}

export function isAuthorized() {
    return getToken() != null;
}

export function deleteToken() {
    localStorage.removeItem("token");
}
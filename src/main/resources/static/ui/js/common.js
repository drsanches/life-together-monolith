import {getToken, deleteToken} from "/ui/js/token.js"

var BASE_URL = "http://localhost:8080";

export function followLink(path) {
    window.location.href = BASE_URL + path;
}

export function sendData(path, method, body, needResponseData, onSuccess) {
    fetch(BASE_URL + path, {
        method: method,
        headers: {
            "Content-Type": "application/json",
            "Authorization": getToken()
        },
        body: JSON.stringify(body),
    })
    .then(response => {
        if (response.ok) {
            if (needResponseData) {
                response.json().then(data => onSuccess(data));
            } else {
                onSuccess();
            }
        } else {
            response.json().then(data => processError(response.status, data));
        }
    })
    .catch(error => console.error(error));
}

export function getData(path) {
    return fetch(BASE_URL + path, {
        method: 'GET',
        headers: {
            "Authorization": getToken()
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            response.json().then(data => processError(response.status, data));
        }
    })
    .catch(error => console.error(error));
}

function processError(status, data) {
    if (status == 401) {
        deleteToken();
        followLink("/ui/index.html");
    }
    console.error(JSON.stringify(data));
    alert("ERROR:\n" + JSON.stringify(data));
}
import {getData} from "/ui/js/utils/common.js"

export var userInfo = {
    data() {
        return {
            id: "",
            username: "",
            email: "",
            firstName: "",
            lastName: ""
        }
    },
    mounted() {
        getData("/auth/info").then(data => {
            this.id = data.id;
            this.username = data.username;
            this.email = data.email;
        });
        getData("/profile").then(data => {
            this.firstName = data.firstName;
            this.lastName = data.lastName;
        });
    },
    template: `
        <div class="user-info">
            <div class="title">User info:</div>
            <br>
            <div class="key">Id:</div>
            <div class="value">{{id}}</div>
            <br>
            <div class="key">Username:</div>
            <div class="value">{{username}}</div>
            <br>
            <div class="key">Email:</div>
            <div class="value">{{email}}</div>
            <br>
            <div class="key">First name:</div>
            <div class="value">{{firstName}}</div>
            <br>
            <div class="key">Last name:</div>
            <div class="value">{{lastName}}</div>
        </div>
    `
}
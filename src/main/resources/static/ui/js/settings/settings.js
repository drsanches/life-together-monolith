import {getData} from "/ui/js/common.js"

export var settings = {
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
        <div class="settings">
            <div class="user-info">
                <div class="title">User info:</div>
                <br>
                <div class="key">Id:&#160;</div>
                <div class="value">{{id}}</div>
                <br>
                <div class="key">Username:&#160;</div>
                <div class="value">{{username}}</div>
                <br>
                <div class="key">Email:&#160;</div>
                <div class="value">{{email}}</div>
                <br>
                <div class="key">First name:&#160;</div>
                <div class="value">{{firstName}}</div>
                <br>
                <div class="key">Last name:&#160;</div>
                <div class="value">{{lastName}}</div>
            </div>
            <hr>
            <br>
            <change-username></change-username>
            <br>
            <change-email></change-email>
            <br>
            <change-password></change-password>
            <hr>
            <br>
            <delete-account></delete-account>
            <hr>
        </div>
    `
}
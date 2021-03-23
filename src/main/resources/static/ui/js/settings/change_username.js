import {followLink, getData, sendData} from "/ui/js/utils/common.js"
import {deleteToken} from "/ui/js/utils/token.js"

export var changeUsername = {
    data() {
        return {
            username: "",
            password: ""
        }
    },
    methods: {
        change: function() {
            if (this.username == "" || this.password == "") {
                alert("Enter all data");
                return;
            }
            //TODO: hash password
            var body = {
                newUsername: this.username,
                password: this.password
            }
            sendData("/auth/changeUsername", "PUT", body, false, function() {
                deleteToken();
                alert("Success");
                followLink("/ui/login.html");
            });
        }
    },
    mounted() {
        getData("/auth/info").then(data => {
            this.username = data.username;
        });
    },
    template: `
        <div class="change-username">
            <div class="title">Change username:</div>
            <div class="input-title">Username:</div>
            <input class="variable-value" v-model="username">
            <div class="input-title">Password:</div>
            <input class="password-value" type="password" v-model="password">
            <button v-on:click="change">Change</button>
        </div>
    `
}
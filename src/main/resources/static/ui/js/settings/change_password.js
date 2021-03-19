import {followLink, sendData} from "/ui/js/common.js"
import {deleteToken} from "/ui/js/token.js"

export var changePassword = {
    data() {
        return {
            oldPassword: "",
            newPassword1: "",
            newPassword2: ""
        }
    },
    methods: {
        change: function() {
            if (this.oldPassword == "" || this.newPassword1 == "" || this.newPassword2 == "") {
                alert("Enter all data");
                return;
            }
            if (this.newPassword1 != this.newPassword2) {
                alert("Passwords do not match");
                return;
            }
            //TODO: hash password
            var body = {
                oldPassword: this.oldPassword,
                newPassword: this.newPassword1
            }
            sendData("/auth/changePassword", "PUT", body, false, function() {
                deleteToken();
                alert("Success");
                followLink("/ui/login.html");
            });
        }
    },
    template: `
        <div class="change-password">
            <div class="title">Change password:</div>
            <div class="input-title">Old password:</div>
            <input class="password-value" type="password" v-model="oldPassword">
            <br>
            <div class="input-title">New password:</div>
            <input class="password-value" type="password" v-model="newPassword1">
            <div class="input-title">Repeat:</div>
            <input class="password-value" type="password" v-model="newPassword2">
            <button v-on:click="change">Change</button>
        </div>
    `
}
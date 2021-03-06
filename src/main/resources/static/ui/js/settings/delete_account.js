import {followLink, sendData, hash} from "/ui/js/utils/common.js"
import {deleteToken} from "/ui/js/utils/token.js"

export var deleteAccount = {
    data() {
        return {
            password: ""
        }
    },
    methods: {
        deleteAccount: function() {
            if (this.password == "") {
                alert("Enter password");
                return;
            }
            if (confirm("Are you sure you want to delete your account?\n"
                    + "It will be impossible to restore it after this operation.")) {
                var body = {
                    password: hash(this.password)
                }
                sendData("/auth/deleteUser", "POST", body, false, function() {
                    deleteToken();
                    alert("Account has been deleted:(");
                    followLink("/ui/index.html");
                });
            }
        }
    },
    template: `
        <div class="delete-account">
            <div class="title">Delete account:</div>
            <div class="input-title">Password:</div>
            <input class="password-value" type="password" v-model="password">
            <button v-on:click="deleteAccount">Delete</button>
        </div>
    `
}
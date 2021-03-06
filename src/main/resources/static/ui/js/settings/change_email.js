import {getData, sendData, followLink, hash} from "/ui/js/utils/common.js"

export var changeEmail = {
    data() {
        return {
            email: "",
            password: ""
        }
    },
    methods: {
        change: function() {
            if (this.email == "" || this.password == "") {
                alert("Enter all data");
                return;
            }
            var body = {
                newEmail: this.email,
                password: hash(this.password)
            }
            sendData("/auth/changeEmail", "PUT", body, false, function() {
                followLink("/ui/settings.html");
            });
        }
    },
    mounted() {
        getData("/auth/info").then(data => {
            this.email = data.email;
        });
    },
    template: `
        <div class="change-email">
            <div class="title">Change email:</div>
            <div class="input-title">Email:</div>
            <input class="variable-value" v-model="email">
            <div class="input-title">Password:</div>
            <input class="password-value" type="password" v-model="password">
            <button v-on:click="change">Change</button>
        </div>
    `
}
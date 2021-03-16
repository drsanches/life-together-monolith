import {sendData, followLink} from "/ui/js/common.js";

export var registration = {
    data() {
        return {
            username: "",
            email: "",
            password1: "",
            password2: ""
        }
    },
    methods: {
        register: function() {
            if (this.username == "" || this.email == "" || this.password1 == "" || this.password2 == "") {
                alert("Enter all data");
                return;
            }
            if (this.password1 != this.password2) {
                alert("Passwords do not equal");
                return;
            }
            //TODO: hash password
            var body = {
                username: this.username,
                email: this.email,
                password: this.password1,
            }
            sendData("/auth/registration", "POST", body, true, function(data) {
                alert("Success");
                followLink("/ui/index.html");
            });
        }
    },
    template: `
        <div class="registration">
            <span>Username:</span><br>
            <input v-model="username"><br>
            <span>Email:</span><br>
            <input v-model="email"><br>
            <span>Password:</span><br>
            <input type="password" v-model="password1"><br>
            <span>Repeat password:</span><br>
            <input type="password" v-model="password2"><br>
            <button v-on:click="register">Register</button>
        </div>
    `
}
import {getData, sendData, followLink} from "/ui/js/common.js"

export var changeProfile = {
    data() {
        return {
            firsName: "",
            lastName: ""
        }
    },
    methods: {
        change: function() {
            var body = {
                firstName: this.firstName,
                lastName: this.lastName
            }
            sendData("/profile", "PUT", body, false, function() {
                followLink("/ui/settings.html");
            });
        }
    },
    mounted() {
        getData("/profile").then(data => {
            this.firstName = data.firstName;
            this.lastName = data.lastName;
        });
    },
    template: `
        <div class="change-profile">
            <div class="title">Change profile:</div>
            <div class="input-title">First name:</div>
            <input class="variable-value" v-model="firstName">
            <br>
            <div class="input-title">Last name:</div>
            <input class="variable-value" v-model="lastName">
            <br>
            <button v-on:click="change">Change</button>
        </div>
    `
}
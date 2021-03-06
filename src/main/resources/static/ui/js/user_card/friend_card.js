import {sendData, followLink} from "/ui/js/utils/common.js"

export var friendCard = {
    props: {
        id: {
            type: String,
            require: true
        },
        username: {
            type: String,
            require: true
        },
        firstName: {
            type: String,
            require: true
        },
        lastName: {
            type: String,
            require: true
        }
    },
    methods: {
        removeRequest: function() {
            var body = {
                userId: this.id
            }
            sendData("/friends/manage/delete", "POST", body, false, function() {
                followLink("/ui/friends.html");
            });
        }
    },
    template: `
        <div class="friend-card">
            <user-card
                :id="id"
                :username="username"
                :firstName="firstName"
                :lastName="lastName"
            ></user-card>
            <button v-on:click="removeRequest">Delete</button>
            <hr>
        </div>
    `
}
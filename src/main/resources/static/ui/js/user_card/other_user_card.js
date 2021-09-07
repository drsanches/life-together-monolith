import {sendData, followLink} from "/ui/js/utils/common.js"

export var otherUserCard = {
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
        sendRequest: function() {
            var body = {
                userId: this.id
            }
            sendData("/friends/manage/add", "POST", body, false, function() {
                followLink("/ui/friends.html");
            });
        }
    },
    template: `
        <div class="other-user-card">
            <user-card
                :id="id"
                :username="username"
                :firstName="firstName"
                :lastName="lastName"
            ></user-card>
            <button v-on:click="sendRequest">Send friend request</button>
            <hr>
        </div>
    `
}
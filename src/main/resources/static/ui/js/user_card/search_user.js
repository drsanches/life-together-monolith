import {getData} from "/ui/js/utils/common.js"

export var searchUser = {
    data() {
        return {
            userInfo: null,
            username: ""
        }
    },
    methods: {
        search: function() {
            if (this.username === "") {
                alert("Enter username");
                return;
            }
            getData("/profile/search/" + this.username).then(data => {
                this.userInfo = data;
            });
        }
    },
    template: `
        <div class="search-user">
            <input v-model="username">
            <button class="search-button" v-on:click="search">Search</button>
            <br><br>
            <other-user-card v-if="userInfo != null"
                :id="userInfo.id"
                :username="userInfo.username"
                :firstName="userInfo.firstName"
                :lastName="userInfo.lastName"
            ></other-user-card>
        </div>
    `
}
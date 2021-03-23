import {getData} from "/ui/js/utils/common.js"

export var friendList = {
    data() {
        return {
            friends: []
        }
    },
    mounted() {
        getData("/friends").then(data => {
            this.friends = data;
        });
    },
    computed: {
        isEmpty() {
            return this.friends.length == 0;
        }
    },
    template: `
        <div class="friend-list" v-if="!isEmpty">
            <div class="title">Friends:</div>
            <br>
            <friend-card v-for="friend in friends"
                :id="friend.id"
                :username="friend.username"
                :firstName="friend.firstName"
                :lastName="friend.lastName"
            ></friend-card>
        </div>
    `
}
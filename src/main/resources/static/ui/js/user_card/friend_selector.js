import {getData} from "/ui/js/utils/common.js"

export var friendSelector = {
    data() {
        return {
            friends: [],
            selectedFriend: ''
        }
    },
    emits: ["friend-select"],
    mounted() {
        getData("/friends").then(data => {
            this.friends = data;
        });
    },
    watch: {
        selectedFriend(val) {
            this.$emit('friend-select', this.selectedFriend);
        }
    },
    template: `
        <div class="friend-selector">
            <select v-model="selectedFriend">
                <option selected value=''>Select a friend</option>
                <option v-for="friend in friends" v-bind:value="friend">{{friend.username}}</option>
            </select>
        </div>
    `
}
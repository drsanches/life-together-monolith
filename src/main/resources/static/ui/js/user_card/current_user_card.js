import {getData} from "/ui/js/common.js"

export var currentUserCard = {
    data() {
        return {
            userInfo: null
        }
    },
    mounted() {
        getData("/profile").then(data => {
            this.userInfo = data;
        });
    },
    template: `
        <user-card class="current-user-card" v-bind="userInfo"></user-card>
    `
}
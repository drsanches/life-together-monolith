import {getData} from "/ui/js/common.js"

export var currentUserCard = {
    data() {
        return {
            userInfo: {
                id: "",
                username: "",
                firstName: "",
                lastName: ""
            }
        }
    },
    mounted() {
        getData("/profile").then(data => {
            this.userInfo = data;
        });
    },
    template: `
        <user-card class="current-user-card"
            :id="userInfo.id"
            :username="userInfo.username"
            :firstName="userInfo.firstName"
            :lastName="userInfo.lastName"
        ></user-card>
    `
}
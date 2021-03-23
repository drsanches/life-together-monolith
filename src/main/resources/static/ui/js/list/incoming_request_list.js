import {getData} from "/ui/js/utils/common.js"

export var incomingRequestList = {
    data() {
        return {
            requests: []
        }
    },
    mounted() {
        getData("/friends/requests/incoming").then(data => {
            this.requests = data;
        });
    },
    computed: {
        isEmpty() {
            return this.requests.length == 0;
        }
    },
    template: `
        <div class="incoming-request-list" v-if="!isEmpty">
            <div class="title">Incoming requests:</div>
            <br>
            <friend-card v-for="request in requests"
                :id="request.id"
                :username="request.username"
                :firstName="request.firstName"
                :lastName="request.lastName"
            ></friend-card>
        </div>
    `
}
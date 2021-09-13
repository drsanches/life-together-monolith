import {sendData, followLink} from "/ui/js/utils/common.js";

export var moneySendPanel = {
    data() {
        return {
            selectedFriend: '',
            money: null,
            message: ""
        }
    },
    methods: {
        onFriendSelect: function(friend) {
            this.selectedFriend = friend;
        },
        sendMoney: function() {
            var value = Number(this.money);
            if (value <= 0) {
                alert("Amount must be positive!");
                return;
            }
            var body = {
                transactions: [{
                    toUserId: this.selectedFriend.id,
                    message: this.message,
                    money: value
                }]
            }
            sendData("/debts/send", "POST", body, false, function() {
                followLink("/ui/debts.html");
            });
        }
    },
    template: `
        <div class="money-send-panel">
            <span class="title">Send money</span>
            <friend-selector @friend-select="onFriendSelect"></friend-selector>
            <div v-if="selectedFriend != ''">
                <user-card
                    :id="selectedFriend.id"
                    :username="selectedFriend.username"
                    :firstName="selectedFriend.firstName"
                    :lastName="selectedFriend.lastName"
                ></user-card>
                <input type="number" placeholder="amount" v-model="money">
                <input placeholder="message" v-model="message">
                <button v-on:click="sendMoney">Send money</button>
            </div>
        </div>
    `
}
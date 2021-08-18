import {getData} from "/ui/js/utils/common.js"

export var totalDebts = {
    data() {
        return {
            debts: [],
            sentUsers: {},
            receivedUsers: {}
        }
    },
    mounted() {
        getData("/debts").then(data => {
            this.debts = data;
            var i;
            for (i = 0; i < this.debts.sent.length; i++) {
                getData("/profile/" + this.debts.sent[i].userId).then(data => {
                    this.sentUsers[data.id] = data;
                });
            }
            for (i = 0; i < this.debts.received.length; i++) {
                getData("/profile/" + this.debts.received[i].userId).then(data => {
                    this.receivedUsers[data.id] = data;
                });
            }
        });
    },
    template: `
        <div class="total-debts">
            <span class="main-title">Total debts</span>
            <div class="sent">
                <span class="title">You sent:</span>
                <debt-list :debts="debts.sent" :users="sentUsers"></debt-list>
            </div>
            <div class="received">
                <span class="title">You received:</span>
                <debt-list :debts="debts.received" :users="receivedUsers"></debt-list>
            </div>
        </div>
    `
}
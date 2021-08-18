export var debtList = {
    props: {
        debts: {
            type: Array,
            require: true
        },
        users: {
            type: Object,
            require: true
        }
    },
    computed: {
        isEmpty() {
            return this.debts == null || this.debts.length == 0
                || this.users == null || Object.keys(this.users).length == 0;
        },
        aggregate() {
            var result = [];
            var i;
            for (i = 0; i < this.debts.length; i++) {
                var key = this.debts[i].userId;
                if (this.users.hasOwnProperty(key)) {
                    result.push({
                        userId: this.debts[i].userId,
                        username: this.users[key].username,
                        firstName: this.users[key].firstName,
                        lastName: this.users[key].lastName,
                        amount: this.debts[i].amount
                    });
                } else {
                    result.push({
                        userId: this.debts[i].userId,
                        username: "UNKNOWN",
                        amount: this.debts[i].amount
                    });
                }
            }
            return result;
        }
    },
    template: `
        <div class="debt-list" v-if="!isEmpty">
            <debt-card v-for="debtInfo in aggregate"
                :id="debtInfo.userId"
                :username="debtInfo.username"
                :firstName="debtInfo.firstName"
                :lastName="debtInfo.lastName"
                :amount="debtInfo.amount"
            ></debt-card>
        </div>
    `
}
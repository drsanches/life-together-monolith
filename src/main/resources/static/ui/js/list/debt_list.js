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
                var user = this.users[key];
                if (user) {
                    if (user.username == null) {
                        user.username = "DELETED";
                    }
                    result.push({
                        userId: this.debts[i].userId,
                        username: user.username,
                        firstName: user.firstName,
                        lastName: user.lastName,
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
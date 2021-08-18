export var debtCard = {
    props: {
        id: {
            type: String,
            require: true
        },
        username: {
            type: String,
            require: true
        },
        firstName: {
            type: String,
            require: true
        },
        lastName: {
            type: String,
            require: true
        },
        amount: {
            type: Number,
            require: true
        }
    },
    template: `
        <div class="debt-card">
            <user-card
                :id="id"
                :username="username"
                :firstName="firstName"
                :lastName="lastName"
            ></user-card>
            <span>{{amount}}</span>
            <hr>
        </div>
    `
}
export var userCard = {
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
        }
    },
    computed: {
        fullName() {
            if (this.firstName != null) {
                return this.firstName + " " + this.lastName;
            } else if (this.lastName != null) {
                return this.lastName;
            } else {
                return "";
            }
        },
    },
    template: `
        <div class="user-card">
            <span class="username">{{username}}</span>
            <br>
            <span class="fullname">{{fullName}}</span>
        </div>
    `
}
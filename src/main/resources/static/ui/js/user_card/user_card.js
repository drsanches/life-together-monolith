export var userCard = {
    props: {
        id: {
            type: String
        },
        username: {
            type: String
        },
        firstName: {
            type: String
        },
        lastName: {
            type: String
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
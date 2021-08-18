import {followLink, getData} from "/ui/js/utils/common.js";
import {isAuthorized, deleteToken} from "/ui/js/utils/token.js";

export var header = {
    data() {
        return {
            authorized: false
        }
    },
    methods: {
            home: () => followLink("/ui/index.html"),
            friends: () => followLink("/ui/friends.html"),
            debts: () => followLink("/ui/debts.html"),
            registration: () => followLink("/ui/registration.html"),
            login: () => followLink("/ui/login.html"),
            settings: () => followLink("/ui/settings.html"),
            logout: () => {
                getData("/auth/logout").then(data => {
                    deleteToken();
                    followLink("/ui/index.html");
                });
            }
        },
    mounted() {
        this.authorized = isAuthorized();
    },
    template: `
        <div>
            <div class="header">
                <!-- Menu -->
                <div class="menu">
                    <span class="logo" v-on:click="home">Life Together</span>
                    <button v-on:click="friends" v-if="authorized">Friends</button>
                    <button v-on:click="debts" v-if="authorized">Debts</button>
                </div>

                <!-- Without token -->
                <div class="auth" v-if="!authorized">
                    <div class="buttons">
                        <button class="registration-button" v-on:click="registration">Registration</button><br>
                        <button class="login-button" v-on:click="login">Login</button>
                    </div>
                </div>

                <!-- With token -->
                <div class="auth" v-if="authorized">
                    <current-user-card></current-user-card>
                    <div class="buttons">
                        <button class="logout-button" v-on:click="logout">Logout</button><br>
                        <button class="settings-button" v-on:click="settings">Settings</button>
                    </div>
                </div>
            </div>
            <hr>
        </div>
    `
}
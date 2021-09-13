<template>
 <div id="app">
  <b-navbar class="blueNavBar" toggleable="lg" type="dark" fixed="true">
    <b-navbar-brand href="#"  @click="home()">
      <img class="logoImg" alt="Simbolo de compartilhar" src="/src/assets/logo.ico"> 
SHARE: Economia Compartilhada</b-navbar-brand>
    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav class="ml-auto">
        <b-navbar-nav class="login-commands" v-if="!$root.credentials">
              <b-nav-item><router-link class="link" :to="{ name: 'login' }">Login</router-link></b-nav-item>
              <b-nav-item ><router-link class="link" :to="{ name: 'create-account' }">Criar conta</router-link></b-nav-item>
        </b-navbar-nav>
        <b-navbar-nav class="login-commands" v-if="$root.credentials">
              <b-nav-item ><router-link class="link" :to="{ name: 'item-list' }">Itens</router-link>
          </b-nav-item>
              <b-nav-item ><router-link class="link" :to="{ name: 'item-received-list' }">Compartilhados comigo</router-link>
          </b-nav-item>
          <b-dropdown right="true" text="Usuário" variant="primary">
          <b-dropdown-item >
            <router-link class="dropLink" :to="{ name: 'change-password' }" replace>Troca senha</router-link>
          </b-dropdown-item>
          <b-dropdown-divider></b-dropdown-divider>
          <b-dropdown-item >
            <a class="dropLink" @click="logout">Logout</a>
          </b-dropdown-item>
        </b-dropdown>
        </b-navbar-nav>
        
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
  <b-container class="container">
      <router-view></router-view>
  </b-container>
      <div class="row footer-row">
      <div class="text-center">
        <div class="footer">
          <p>©2021 UNIRIO, Universidade Federal do Estado do Rio de Janeiro</p>
        </div>
      </div>
    </div>
</div>

</template>

<script>
export default {
  data() {
    return {};
  },

  methods: {
    logout: function () {
      this.$root.credentials = null;
      this.$router.replace("/");
    },
    home: function () {
      this.$router.replace("/");
    },
  },
};
</script>

<style>
#app {
  font-family: Helvetica, Arial, sans-serif;
}
.logoImg{
  height: 50px;
}
.container {
  width: 100% !important;
}
.blueNavBar{
  background-color: #2B8BE1;
}
span.greetings {
  color: white;
  margin: 0 4px;
}
li {
  margin: 0 4px;
}
li.login-commands {
  color: white;
  padding-top: 15px;
  margin-right: 32px;
}
li.login-commands a {
  display: inline;
  padding: 15px 8px;
}
li.user-commands {
  color: white;
  padding-top: 15px;
  margin-right: 32px;
}
li.user-commands span.greetings {
  margin-right: 12px;
}
li.user-commands a {
  display: inline;
  padding: 15px 8px;
}
.link{
  color: white;
}
.dropLink{
  color:black
}
.footer {
  margin-top: 64px;
  margin-bottom: 32px;
  padding-top: 8px;
  color: #777;
}
.text-center{
  margin-top: 20px;
}
</style>

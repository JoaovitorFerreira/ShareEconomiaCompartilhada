<template lang="html">
<div class="centralContent">
  <b-container v-if="!$root.credentials">
        <div class="login">
            <img class="shareEco" alt="simbolo" src="/src/assets/sharedEcon.jfif" >
            <b-container  class="text-center">
              <h2 class="display-3">SHARE</h2>
                <p class="lead">SHARE: Economia Compartilhada é uma aplicação Web para o compartilhamento de itens. O proprietário pode registrar
                seus itens no sistema e indicar que, durante um determinado período, eles serão utilizados por outros usuários.</p>
            </b-container>
        </div>
    <b-container>
      <hr class="my-2">
      <div class="row featurette">
        <div class="col-md-7">
          <h2 class="featurette-heading">Como funciona? <span class="text-muted">é simples!</span></h2>
          <p class="lead">Um usuário fornece, através do sistema, algum objeto, produto ou algo que queira compartilhar com a rede</p>
          <p class="lead">O segundo usuário que deseja aquele produto pede para o sistema, o direito de acessar o objeto compartilhado</p>
          <p class="lead">E aí o sistema notifica o primeiro usuário, perguntando se o mesmo aceita ou não compartilhar o objeto dele</p>
          <p class="lead">Caso aceite, o sistema disponibiliza o item para o solicitante, caso não, o sistema notifica que o primeiro usuário não quis compartilhar</p>
        </div>
        <div class="col-md-5 order-md-1">
          <img class="bd-placeholder-img bd-placeholder-img-lg featurette-image img-fluid mx-auto" width="500" height="500" preserveAspectRatio="xMidYMid slice" src="/src/assets/98608010.jpg" >
        </div>
      </div>
      <hr class="featurette-divider">
      <div class="row featurette">
        <div class="col-md-7 order-md-2">
          <h2 class="featurette-heading">Se interessou?</h2>
          <p class="lead">Sinta-se livre para testar nosso site e ingressar em nossa comunidade!</p>
          <router-link class="lead" :to="{ name: 'create-account' }">Junte-se a nós!</router-link>
        </div>
        <div class="col-md-5 order-md-1">
          <img class="bd-placeholder-img bd-placeholder-img-lg featurette-image img-fluid mx-auto" width="500" height="500" preserveAspectRatio="xMidYMid slice" src="/src/assets/JoinUsImg.png" >
        </div>
      </div>
      <hr class="featurette-divider">
      <b-container>
        <b-row align-v="start">
          <h4>Desenvolvedores</h4>
          <b-col  class="col-lg-4">
            <h2>João Vítor Ferreira</h2>
          </b-col>
          <b-col class="col-lg-4">
            <h2>Yuri Farias</h2>
          </b-col>
          <b-col class="col-lg-4">
            <h2>Augusto Heil</h2>
          </b-col>
        </b-row>
      </b-container>
  </b-container>  
  <b-container v-if="$root.credentials"> 
    <b-container class="login">
      <img class="shareEco" alt="simbolo" src="/src/assets/sharedEcon.jfif" >
      <b-container  class="text-center">
        <h2 class="display-3">Economia Compartilhada</h2>
      </b-container>
      <b-container  class="text-center">
        <hr class="featurette-divider">
        <h3>Olá {{$root.credentials.nome}}!</h3>
        <p> Para acessar a tela de itens compartilhados clique aqui: <router-link class="lead" :to="{ name: 'item-received-list' }">Compartilhados comigo</router-link></p>
      </b-container>
      <hr class="featurette-divider">
      </b-container>
        <b-container class="modal show" tabindex="-1" role="dialog" v-if="showModal && compartilhamentos.length">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h3 class="modal-title">Olá, você tem {{ compartilhamentos.length }} {{ compartilhamentos.length > 1 ? 'itens' : 'item'}} com status aberto</h3>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true" v-on:click="fecharModal()">&times;</span>
                </button>
              </div>
              <div class="modal-body">
                <div v-for="item in compartilhamentos">
                  <h3 class="text-info">{{ item.nomeItem }} </h3>
                  <p>Data de início do compartilhamento: <b>{{ item.dataInicio }}</b></p>
                  <p>Data de termino do compartilhamento: <b>{{ item.dataTermino }}</b></p>
                  <hr>
                </div>
                  <h5>Vá para itens compartilhados</h5>
                  <p> Para acessar a tela de itens compartilhados clique aqui: <router-link class="link" :to="{ name: 'item-received-list' }">Compartilhados comigo</router-link></p>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"  v-on:click="fecharModal()">Fechar</button>
              </div>
            </div>
          </div>
      </b-container>
    </b-container>
  </b-container>
</div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      compartilhamentos: [],
      showModal: false,
      httpOptions: {
        baseURL: this.$root.config.url,
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          Authorization:
            "Bearer " + this.$root.credentials != null
              ? this.$root.credentials.token
              : "",
        },
      },
    };
  },
  created: function () {
    if (this.$root.credentials != null) {
      this.loadCompartilhamentos();
    }
  },
  methods: {
    loadCompartilhamentos: function () {
      axios
        .get("/api/compartilhamento/recebidos/abertos", this.httpOptions)
        .then((response) => {
          this.compartilhamentos = response.data.data;
          this.showModal = true;
        })
        .catch((error) => {});
    },
    fecharModal: function () {
      this.showModal = false;
    },
  },
};
</script>

<style lang="css" scoped>
div.jumbotron-row {
  margin-top: 32px;
}
.login{
  margin-top: 5%;
  margin-bottom: 10%;
}
.shareEco{
  width: 100%;
  height: 100%;
}
.centralContent {
  width: "100%";
  height: "100%";
}
.text-center{
  margin-top: 20px;
}
.marketing h4 {
  font-size: 20px;
}

</style>

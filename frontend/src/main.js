import Vue from 'vue'
import Router from 'vue-router'
import { BootstrapVue,BootstrapVueIcons } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import App from './App.vue'
import Homepage from './components/Homepage.vue'
import Login from './components/login/login/Login.vue'


/* Criacao de conta */
import CriacaoConta from './components/login/criacao-conta/CriacaoConta.vue'
import CriacaoContaSucesso from './components/login/criacao-conta/CriacaoContaSucesso.vue'

/* Esquecimento de senha */
import EsquecimentoSenha from './components/login/esquecimento-senha/EsquecimentoSenha.vue'
import EsquecimentoSenhaSucesso from './components/login/esquecimento-senha/EsquecimentoSenhaSucesso.vue'
import RecuperacaoSenha from './components/login/esquecimento-senha/RecuperacaoSenha.vue'
import RecuperacaoSenhaSucesso from './components/login/esquecimento-senha/RecuperacaoSenhaSucesso.vue'

/* Troca de senha */
import TrocaSenha from './components/login/troca-senha/TrocaSenha.vue'
import TrocaSenhaSucesso from './components/login/troca-senha/TrocaSenhaSucesso.vue'

/* Itens compartilhados */
import ListaItemCompartilhado from './components/item/ListaItemCompartilhado.vue'
import NovoItemCompartilhado from './components/item/NovoItemCompartilhado.vue'
import AtualizaItemCompartilhado from './components/item/AtualizaItemCompartilhado.vue'
import RemoveItemCompartilhado from './components/item/RemoveItemCompartilhado.vue'
import DetalhesItemCompartilhado from './components/item/DetalhesItemCompartilhado.vue'
import CompartilhaItemCompartilhado from './components/item/CompartilhaItemCompartilhado.vue'
import ListaItemRecebido from './components/item/ListaItemRecebido.vue'


Vue.use(Router)
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

const router = new Router({
  mode: 'history',
  routes: [
      {
          path: '/',
          name: 'home',
          component: Homepage
      },
      {
          path: '/login',
          name: 'login',
          component: Login,
      },
      {
          path: '/login/new',
          name: 'create-account',
          component: CriacaoConta,
      },
      {
          path: '/login/account-created',
          name: 'account-created',
          component: CriacaoContaSucesso,
      },
      {
          path: '/login/forgot',
          name: 'forgot-password',
          component: EsquecimentoSenha,
      },
      {
          path: '/login/token-sent',
          name: 'token-sent',
          component: EsquecimentoSenhaSucesso,
      },
      {
          path: '/login/reset',
          name: 'reset-password',
          component: RecuperacaoSenha,
      },
      {
          path: '/login/reseted',
          name: 'password-reseted',
          component: RecuperacaoSenhaSucesso,
      },
      {
          path: '/login/change',
          name: 'change-password',
          component: TrocaSenha,
      },
      {
          path: '/login/changed',
          name: 'password-changed',
          component: TrocaSenhaSucesso,
      },
      {
          path: '/item/list',
          name: 'item-list',
          component: ListaItemCompartilhado,
      },
      {
          path: '/item/update',
          name: 'item-update',
          component: AtualizaItemCompartilhado,
          props: true
      },
      {
          path: '/item/new',
          name: 'item-new',
          component: NovoItemCompartilhado,
      },
      {
          path: '/item/delete',
          name: 'item-delete',
          component: RemoveItemCompartilhado,
          props: true
      },
      {
          path: '/item/details',
          name: 'item-details',
          component: DetalhesItemCompartilhado,
          props: true
      },
      {
          path: '/item/share',
          name: 'item-share',
          component: CompartilhaItemCompartilhado,
          props: true
      },
      {
          path: '/item/received/list',
          name: 'item-received-list',
          component: ListaItemRecebido,
          props: true
      }
  ]
})

new Vue({
  data: {
    credentials: null,
    config: {
      url: "http://localhost:7781"
    }
  },

 el: '#app',
 render: h => h(App),
 router
})

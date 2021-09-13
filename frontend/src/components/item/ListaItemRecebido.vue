<template>
    <div class="row" v-if="this.$root.credentials">
        <div class="col-md-10 col-md-offset-1 text-left">
             <br/>
            <div>
                <div class="header">
                    <h2 class="form-title">Itens Recebidos</h2>
                    <h6 class="form-subtitle">Abaixo estão os itens que foram compartilhados com você.</h6>
                </div>
                <div class="clear"></div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label>Buscar</label>
                    <input class="form-control" v-model="busca"
                        placeholder="Digite o nome ou descrição do item" >
                </div>
             </div>
              <br/>
            <table class="table table-striped" id="tbItens">
                <thead>
                <tr>
                    <th>Nome do Item</th>
                    <th>De</th>
                    <th>Até</th>
                    <th>Status</th>
                    <th class="commands"></th>
                </tr>
                </thead>

                <tbody>
                <tr v-for="comp in compartilhamentos" v-if="comp.nomeItem.includes(busca)">
                    <td>{{comp.nomeItem}}</td>
                    <td>{{comp.dataInicio}}</td>
                    <td>{{comp.dataTermino}}</td>
                    <td>{{comp.status}}</td>
                    <td v-if="comp.status === 'ACEITO'" class="item-options">
                        <span class="option glyphicon glyphicon-remove" aria-hidden="true" @click="rejeitaItem(comp)"></span>
                    </td>
                    <td v-if="comp.status === 'ABERTO'">
                        <button @click="aceitaItem(comp)" class="btn btn-info">Aceitar</button>
                        <button @click="rejeitaItem(comp)" class="btn  btn-danger">Rejeitar</button>
                    </td>
                </tr>
                </tbody>
            </table>

            <!--<div>-->
                <!--<div class="page-item first" :class="{ disable: this.page == 1 }" @click="moveTo(page-1)">&lt;&lt;</div>-->
                <!--<div class="page-item" v-show="page > 3" @click="moveTo(page-3)">{{page-3}}</div>-->
                <!--<div class="page-item" v-show="page > 2" @click="moveTo(page-2)">{{page-2}}</div>-->
                <!--<div class="page-item" v-show="page > 1" @click="moveTo(page-1)">{{page-1}}</div>-->
                <!--<div class="page-item current disable">{{page}}</div>-->
                <!--<div class="page-item" v-show="totalPages > page"   @click="moveTo(page+1)">{{page+1}}</div>-->
                <!--<div class="page-item" v-show="totalPages > page+1" @click="moveTo(page+2)">{{page+2}}</div>-->
                <!--<div class="page-item" v-show="totalPages > page+2" @click="moveTo(page+3)">{{page+3}}</div>-->
                <!--<div class="page-item last" :class="{ disable: this.page == this.totalPages }" @click="moveTo(page+1)">&gt;&gt;</div>-->
                <!--<div class="clear"></div>-->
            <!--</div>-->
        </div>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "ListaItemRecebido",
        data() {
            return {
                busca: '',
                compartilhamentos: [],
                httpOptions: {
                    baseURL: this.$root.config.url,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + this.$root.credentials.token
                    }
                },
            }
        },
        created() {
            this.getList();
        },
        methods: {
            getList: function() {
                axios.get("api/compartilhamento/recebidos/lista", this.httpOptions)
                    .then(result => {
                        this.compartilhamentos = result.data.data;
                    })
                    .catch(error => {
                    })
            },
            aceitaItem: function(compartilhamento) {
                axios.delete("api/compartilhamento/aceita/" + compartilhamento.id, this.httpOptions)
                    .then(result => {
                        console.log("DA CERTO");
                        this.getList();
                    })
                    .catch(error => {
                        console.log("ERROOOO")
                    })
            },
            rejeitaItem: function(compartilhamento) {
                axios.delete("api/compartilhamento/rejeita/" + compartilhamento.id, this.httpOptions)
                    .then(result => {
                       compartilhamento.status = "REJEITADO";
                    })
                    .catch(error => {
                        console.log("ERROOOO")
                    })
            }
        }
    }
</script>

<style scoped>

</style>

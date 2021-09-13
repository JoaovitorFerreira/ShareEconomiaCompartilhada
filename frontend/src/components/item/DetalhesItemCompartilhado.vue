<template>
    <div class="lista-items-compartilhados row" v-if="this.$root.credentials">
        <div class="col-md-10 col-md-offset-1 text-left">
            <div>
                <div class="header">
                    <h2 class="form-title">Detalhes do item: {{item.nome}}</h2>
                </div>
                <div class="clear"></div>
            </div>
            <br/>
            <table class="table table-striped" id="tbItem">
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Descrição</th>
                    <th>Tipo</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>{{item.nome}}</td>
                    <td>{{item.descricao}}</td>
                    <td>{{item.tipo}}</td>
                </tr>
                </tbody>
            </table>
             <br/>
            <div class="compartilhamentos">
               <h3>Compartilhamentos:</h3>
            </div>
             <br/>
            <table class="table table-striped" id="tbCompartilhamentos">
                <thead>
                <tr>
                    <th>Destinatário</th>
                    <th>De</th>
                    <th>Até</th>
                    <th>Status</th>
                    <th class="commands"></th>
                </tr>
                </thead>

                <tbody>
                <tr v-for="comp in compartilhamentos">
                    <td>{{comp.destinatario}}</td>
                    <td>{{comp.dataInicio}}</td>
                    <td>{{comp.dataTermino}}</td>
                    <td>{{comp.status}}</td>
                    <td class="cancela-compartilhamento">
                        <span class="option glyphicon glyphicon-remove" aria-hidden="true" @click="remove(comp.id)"></span>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="new-button">
                <button type="button" class="btn btn-primary" @click="compartilha(item)">
                    Compartilhar Item
                </button>
            </div>

        </div>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "DetalhesItemCompartilhado",
        props: ['item'],
        data() {
            return {
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
        created: function() {
            this.processForm();
        },
        methods: {
            processForm: function() {
                axios.get(`/api/compartilhamento/lista?page=1&per_page=10&nomeItem=${this.item.nome}`, this.httpOptions)
                    .then(result => {
                        this.compartilhamentos = result.data.data;
                    })
                    .catch(error => {
                    })
            },
            compartilha: function(item) {
                this.$router.push({
                    name: 'item-share',
                    params: {item: item}
                })
            },
            remove: function(compartilhamentoId) {
                axios.delete(`/api/compartilhamento/dono/cancela/${compartilhamentoId}`, this.httpOptions)
                    .then(result => {
                        this.processForm();
                    })
                    .catch(error => {
                    })
            }
        }
    }
</script>

<style scoped>
    .cancela-compartilhamento {
        cursor: pointer;
    }

.new-button {
    margin-top: 1.5em;
}
</style>

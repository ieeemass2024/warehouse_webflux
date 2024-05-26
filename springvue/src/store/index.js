import { createStore } from 'vuex';
import axios from 'axios';

export default createStore({
    state: {
        user: null
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
            localStorage.setItem('jwtToken', user.jwtToken);
        },
        clearUser(state) {
            state.user = null;
            localStorage.removeItem('jwtToken');
        }
    },
    actions: {
        login({ commit }, credentials) {
            return axios.post('/users/login', credentials).then(({ data }) => {
                commit('setUser', data);
            });
        },
        logout({ commit }) {
            commit('clearUser');
        }
    }
});

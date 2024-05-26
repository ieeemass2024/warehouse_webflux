import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';  // 引入store

const app = createApp(App);
app.use(router);
app.use(store);
app.mount('#app');

import axios from 'axios';

// 设置 Axios 拦截器来自动附加 JWT
axios.interceptors.request.use(function (config) {
    const token = localStorage.getItem('jwtToken');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});


export default axios;
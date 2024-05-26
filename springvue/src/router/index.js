import { createRouter, createWebHistory } from 'vue-router';

import ItemList from '@/components/itemLists.vue';
import Update from '@/components/updatePage';
import Detail from '@/components/detailPage';
import Insert from '@/components/insertPage';
import Login from '@/components/userLogin.vue';
import UserItem from '@/components/userItem.vue';
import UserDetail from '@/components/userDetail.vue';

// 创建 router 实例
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'Login', component: Login },
        { path: '/itemlists', name: 'ItemList', component: ItemList },
        { path: '/updatePage/:id', name: 'Update', component: Update },
        { path: '/insertPage', name: 'Insert', component: Insert },
        { path: '/detailPage/:id', name: 'Detail', component: Detail },
        { path: '/useritem', name: 'UserItem', component: UserItem },
        { path: '/userdetail/:id', name: 'UserDetail', component: UserDetail },
    ],
});

router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('jwtToken'); // 检查是否存在 token 表示用户已经登录

    if (!isAuthenticated && to.name !== 'Login') {
        next({ name: 'Login', query: { redirect: to.fullPath } }); // 未登录用户，重定向到登录页面
    } else {
        next(); // 其他情况，正常访问
    }
});

export default router;

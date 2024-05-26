<template>
  <div>
    <div class="table-container">
      <button class="btn btn-detail" @click="logout()">退出登录</button>
      <table class="table table-bordered table-hover">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Stock</th>
          <th>Price</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in items" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.stock }}</td>
          <td>{{ item.price }}</td>
          <td>
            <button class="btn btn-detail" @click="goToDetailPage(item)">查看详情</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'UserItem',
  data() {
    return {
      items: []
    }
  },
  mounted() {
    // 添加请求拦截器
    axios.interceptors.response.use(undefined, (error) => {
      if (error.response && error.response.status === 401 && !error.config.url.includes('/users/login')) {
        // 如果是401错误且不是登录接口，执行退出登录操作
        this.logout();
        // 弹出提示，告知用户会话过期
        alert('Your session has expired. Please log in again.');
        // 阻止进一步的错误处理
        return new Promise(() => {});
      }
      // 对于非401错误，继续抛出错误，由其他拦截器或调用处处理
      return Promise.reject(error);
    });

    this.fetchItems();
  },
  methods: {
    fetchItems() {
      axios.get('/items')
          .then(response => {
            this.items = response.data;
          })
          .catch(error => {
            console.error('Error fetching items:', error);
          });
    },

    logout(){
      // 移除 token
      localStorage.removeItem('jwtToken');
      // 可选：清除 Vuex store 中的用户数据
      // this.$store.commit('clearUserData');
      // 重定向到登录页面
      this.$router.push('/');
    },

    goToDetailPage(item) {
      this.$router.push({
        name: 'UserDetail',
        params: {
          id: item.id
        }
      });
    }
  }
}
</script>


<style>
body {
  margin: 0;
  padding: 0;
  font-family: Arial, sans-serif;
}

.table-container {
  padding: 20px;
  background-color: #f9f9f9;
}
.table {
  width: 100%;
  margin-bottom: 0;
  text-align: center; /* Center table content */
}
.table thead th {
  background-color: #023d7e;
  color: white;
  border-color: #023d7e;
}
.table td, .table th {
  padding: .75rem;
  vertical-align: top;
  border-top: 1px solid #dee2e6;
}
.table-bordered {
  border: 1px solid #dee2e6;
}
.btn-detail{
  background-color: #025024;
  border-color: #025024;
}

</style>

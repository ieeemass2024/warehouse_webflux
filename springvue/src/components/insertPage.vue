<template>
  <div class="add-user-form">
    <form @submit.prevent="saveItem">
      <label for="name">商品名：</label>
      <input id="name" class="form-control" type="text" v-model="item.name" name="name" required><br>
      <label for="category">类别：</label>
      <input id="category" class="form-control" type="text" v-model="item.category" name="category" required><br>
      <label for="description">描述：</label>
      <input id="description" class="form-control" type="text" v-model="item.description" name="description" required><br>
      <label for="stock">货存：</label>
      <input id="stock" class="form-control" type="text" v-model="item.stock" name="stock" required><br>
      <label for="price">价格：</label>
      <input id="price" class="form-control" type="text" v-model="item.price" name="price" required><br>
      <button class="btn btn-primary btn-lg btn-block" type="submit">保存</button>
      <button class="btn btn-primary" @click="back()">返回</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment"; // 用于日期格式化

export default {
  name: 'AddItem',
  data() {
    return {
      item: {
        id: null, // 初始化为空或从路由参数获取
        name: '',
        category: '',
        description: '',
        stock: '',
        price: '',
        createTime: '',
        updateTime: ''
      }
    };
  },

  mounted() {
    // 添加请求拦截器
    axios.interceptors.response.use(undefined, (error) => {
      if (error.response && error.response.status === 401) {
        // 如果是401错误，执行退出登录操作
        this.logout();
        // 弹出提示，告知用户会话过期
        alert('Your session has expired. Please log in again.');
        // 阻止进一步的错误处理
        return new Promise(() => {});
      }
      // 对于非401错误，继续抛出错误，由其他拦截器或调用处处理
      return Promise.reject(error);
    });
  },

  methods: {
    logout() {
      // 移除 token
      localStorage.removeItem('jwtToken');
      // 可选：清除 Vuex store 中的用户数据
      // this.$store.commit('clearUserData');
      // 重定向到登录页面
      this.$router.push('/');
    },

    saveItem() {
      // 设置创建时间和更新时间为当前时间
      this.item.createTime = moment().format('YYYY-MM-DD HH:mm:ss');
      this.item.updateTime = moment().format('YYYY-MM-DD HH:mm:ss');

      axios.post('/items', this.item) // 更新API端点
          .then(response => {
            console.log('Item added successfully', response);
            this.$router.push('/itemlists'); // 更新路由路径
          })
          .catch(error => {
            console.error('Error adding item', error);
            // 处理错误，例如显示错误消息
          });
    },

    back() {
      this.$router.push({
        name: 'ItemList',
      });
    }
  }
}
</script>

<style scoped>
/* 样式与 UpdatePage.vue 保持一致 */
.add-user-form {
  max-width: 500px; /* 宽度与 UpdatePage.vue 一致 */
  margin: auto; /* 水平居中 */
  padding: 20px; /* 内边距调整 */
  background: #fff; /* 背景颜色 */
  border-radius: 8px; /* 边角圆润 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  margin-top: 20px; /* 顶部间距 */
}

.form-control {
  display: block;
  width: calc(100% - 20px); /* 减去 padding 的宽度 */
  margin-bottom: 15px; /* 间距调整 */
  border: 1px solid #ccc; /* 边框颜色调整 */
  padding: 10px; /* 内边距调整 */
}

.btn {
  width: 100%; /* 按钮宽度 */
  padding: 10px 20px; /* 内边距调整 */
  background-color: #0056b3; /* 背景颜色调整 */
  border-color: #0056b3; /* 边框颜色调整 */
  margin-top: 10px; /* 顶部间距调整 */
}

.btn:hover {
  background-color: #004494; /* 鼠标悬停时的背景颜色调整 */
  border-color: #004494; /* 边框颜色调整 */
}

.form-control[readonly] {
  background-color: #f8f9fa; /* 只读输入框的背景颜色 */
  color: #6c757d; /* 文本颜色 */
  cursor: not-allowed; /* 鼠标样式 */
}
</style>

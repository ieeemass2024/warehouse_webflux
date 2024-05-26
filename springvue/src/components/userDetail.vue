<template>
  <div class="detail-form">
    <form @submit.prevent="back">
      <label for="id">ID：</label>
      <input id="id" class="form-control" type="text" v-model="item.id" readonly="readonly"><br>
      <label for="name">商品名：</label>
      <input id="name" class="form-control" type="text" v-model="item.name" readonly="readonly"><br>
      <label for="category">类别：</label>
      <input id="category" class="form-control" type="text" v-model="item.category" readonly="readonly"><br>
      <label for="description">描述：</label>
      <input for="description" class="form-control" type="text" v-model="item.description" readonly="readonly"><br>
      <label for="stock">货存：</label>
      <input id="stock" class="form-control" type="text" v-model="item.stock" readonly="readonly"><br>
      <label for="price">价格：</label>
      <input id="price" class="form-control" type="text" v-model="item.price" readonly="readonly"><br>
      <label for="createTime">创建时间：</label>
      <input id="createTime" class="form-control" type="text" v-model="item.createTime" readonly="readonly"><br>
      <label for="updateTime">更新时间：</label>
      <input id="updateTime" class="form-control" type="text" v-model="item.updateTime" readonly="readonly"><br>
      <button class="btn btn-primary btn-lg btn-block" type="submit">返回</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'UserDetail',
  data() {
    return {
      item: {
        id: null, // 初始化为空或从路由参数获取
        name: '',
        category:'',
        description:'',
        stock: '',
        price: '',
        createTime:'',
        updateTime:''
      }
    }
  },

  created() {
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

    // 从路由参数获取用户数据
    this.item.id = this.$route.params.id;

    this.fetchItem();
  },
  methods: {
    fetchItem() {
      axios.get(`/items/${this.item.id}`)
          .then(response => {
            this.item = response.data;
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

    back() {
      this.$router.push({
        name: 'UserItem',
      });
    }
  }
}
</script>


<style scoped>
/* 适用于 DetailPage.vue 的样式 */
.detail-form {
  max-width: 500px; /* 限制最大宽度，使其不会过宽 */
  margin: auto; /* 水平居中 */
  padding: 20px; /* 增加内边距 */
  background: #fff; /* 白色背景 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.1); /* 轻微的阴影 */
  margin-top: 20px; /* 与页面顶部的距离 */
}

.detail-form .form-control {
  display: block; /* 确保它们是块级元素 */
  width: calc(100% - 20px); /* 减去 padding 的宽度 */
  margin-bottom: 15px; /* 间距 */
  border: 1px solid #ccc; /* 边框颜色更轻 */
  padding: 10px 10px; /* padding */
}

.detail-form .btn {
  width: 100%; /* 自适应内容宽度 */
  padding: 10px 10px; /* 增加左右内边距 */
  background-color: #0056b3; /* 更亮的蓝色 */
  border-color: #0056b3; /* 边框颜色 */
  margin-top: 10px; /* 按钮与输入框间距 */
}

.detail-form .btn:hover {
  background-color: #004494; /* 悬停时更深的蓝色 */
  border-color: #004494; /* 边框颜色 */
}

/* 调整只读输入框的样式 */
.detail-form .form-control[readonly] {
  background-color: #f8f9fa; /* 更浅的背景颜色 */
  color: #6c757d; /* 文本颜色 */
  cursor: not-allowed; /* 鼠标样式 */
}
</style>
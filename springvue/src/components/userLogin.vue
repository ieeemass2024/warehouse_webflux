<template>
  <div class="container">
    <div class="login-wrapper">
      <div class="header">用户登录</div>
      <div class="form-wrapper">
        <input type="text" v-model="username" placeholder="请输入用户名" class="input-item">
        <div v-if="!username && submitAttempted" class="error-msg">用户名不能为空</div>
        <input type="password" v-model="password" placeholder="请输入密码" class="input-item">
        <div v-if="!password && submitAttempted" class="error-msg">密码不能为空</div>
        <input type="submit" id="login" class="btn" value="登录" @click="userLogin">
        <div v-if="errorMsg" class="msg">{{ errorMsg }}</div> <!-- 错误消息展示 -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { mapActions } from 'vuex';
export default {
  name: 'UserLogin',
  data() {
    return {
      username: "",
      password: "",
      errorMsg: "",
      submitAttempted: false,
    };
  },
  methods: {
    ...mapActions(['login']),  // 引用 Vuex action
    userLogin() {

      this.errorMsg = "";
      this.submitAttempted = true;
      localStorage.removeItem('jwtToken');
      axios.post('/users/login', {
        username: this.username,
        password: this.password
      })
      .then(response => {

        console.log("登录成功", response);
        // console.log("data"+response.data);
        // console.log("token"+response.data.jwtToken);
        // 假设后端在登录响应中返回了token
        if (response.data && response.data.jwtToken) {
          console.log(response.data.user.role)
          localStorage.setItem('jwtToken', response.data.jwtToken); // 存储token

          //根据role的不同进行跳转

          if(response.data.user.role == "ADMIN"){
            this.$router.push("/itemlists"); // 根据角色重定向到不同的路由
          }else if(response.data.user.role == "USER"){
          this.$router.push("/useritem"); // 根据角色重定向到不同的路由
          }
        } else {
          throw new Error('Token not provided');
        }
      })
      .catch(error => {
        this.errorMsg = "登录失败: " + (error.response.data.message || "服务器错误");
        console.error(this.errorMsg);
      });
    }
  }
}
</script>


<style scoped lang="scss">
/* 容器样式 */
.container {
  height: 100vh; /* 全屏高度 */
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  background-image: linear-gradient(to right, #fbc2eb, #a6c1ee); /* 渐变背景 */
}

/* 登录框样式 */
.login-wrapper {
  background-color: #fff;
  width: 300px; /* 微调宽度 */
  padding: 40px; /* 微调内边距 */
  border-radius: 15px; /* 边角圆润 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影 */
}

/* 标题样式 */
.header {
  font-size: 24px; /* 字体大小调整 */
  font-weight: bold;
  color: #333; /* 更深的字体颜色以增加对比度 */
  margin-bottom: 30px; /* 标题到输入框的距离 */
  text-align: center;
}

/* 输入框样式 */
.input-item {
  width: calc(100% - 20px); /* 减去内边距的总宽度 */
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 5px; /* 输入框边角圆润 */
  border: 1px solid #ddd; /* 边框颜色 */
  font-size: 16px; /* 字体大小 */
  outline: none; /* 去除轮廓 */
  transition: border-color 0.3s; /* 边框颜色变化过渡效果 */
}

.input-item::placeholder {
  color: #aaa; /* 占位文字颜色 */
}

.input-item:focus {
  border-color: #a6c1ee; /* 输入框聚焦时边框颜色 */
}

/* 按钮样式 */
.btn {
  width: 100%;
  padding: 12px 15px; /* 增加填充，更易点击 */
  background-color: #007bff; /* 按钮背景颜色 */
  color: #fff;
  border-radius: 5px; /* 按钮边角圆润 */
  font-size: 18px; /* 按钮字体大小 */
  transition: background-color 0.3s; /* 按钮背景颜色变化过渡效果 */
}

.btn:hover {
  background-color: #0056b3; /* 鼠标悬停时按钮背景颜色变化 */
}

/* 底部提示信息样式 */
.msg {
  text-align: center;
  margin-top: 20px; /* 信息与按钮的距离 */
}

.msg a {
  color: #007bff; /* 链接颜色 */
  text-decoration: none; /* 去除下划线 */
}

/* 响应式设计：在小屏幕上减少内边距 */
@media (max-width: 600px) {
  .login-wrapper {
    padding: 20px;
  }
}


</style>

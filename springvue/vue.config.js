const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/items': { // 使用统一的基本路径以匹配RESTful风格
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/items': '/items' } // 不需要重写路径
      },
      'users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/users': '/users' }
      }

    }
  }
});

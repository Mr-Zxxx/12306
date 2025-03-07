module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:9000',
        changeOrigin: true,
        ws: true
      }
    },
    // 忽略ResizeObserver报错
    client: {
      overlay: {
        warnings: false,
        runtimeErrors: (error) => {
          const ignoreErrors = [
            "ResizeObserver loop limit exceeded",
            "ResizeObserver loop completed with undelivered notifications.",
          ]
          if (ignoreErrors.includes(error.message)) {
            return false
          }
        },
      },
    },
  },
  css: {
    loaderOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          'border-radius-base': '4px',
          'card-radius': '4px'
        }
      }
    }
  }
}

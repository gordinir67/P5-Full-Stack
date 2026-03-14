const path = require('path');

module.exports = {
  module: {
    rules: [
      {
        test: /\.[jt]s$/,
        include: [path.resolve(__dirname, 'src/app')],
        exclude: [
          /node_modules/,
          /\.spec\.[jt]s$/,
          /\.cy\.[jt]s$/,
          /cypress/
        ],
        enforce: 'post',
        use: {
          loader: 'babel-loader',
          options: {
            babelrc: false,
            configFile: false,
            presets: [
              ['@babel/preset-env', { targets: { esmodules: true } }]
            ],
            plugins: ['babel-plugin-istanbul']
          }
        }
      }
    ]
  }
};
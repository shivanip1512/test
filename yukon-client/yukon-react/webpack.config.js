const UglifyJsPlugin = require("uglifyjs-webpack-plugin")

module.exports = {
    entry: { NavigationContainer: './src/components/Navigation/NavigationContainer.js' },
    output: {
        filename: 'yukon.react.navigationContainer.js',
        publicPath: 'dist/'
    },
    optimization: {
      minimizer: [new UglifyJsPlugin()]
    },
    plugins: [
      new UglifyJsPlugin()
  ],
    module: {
        rules: [
        {
            test: /\.js$/,
            exclude: /node_modules/,
            use: {
                loader: 'babel-loader',
                options: {
                    presets: [
                        ['@babel/preset-env'], 
                        ['@babel/preset-react']
                    ]
                }
            }
          },
        ],
    },
}
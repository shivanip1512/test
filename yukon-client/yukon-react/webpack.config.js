const UglifyJsPlugin = require("uglifyjs-webpack-plugin")

module.exports = {
    entry: { NavigationContainer: './src/components/Navigation/NavigationContainer.js' },
    output: {
        filename: 'yukon.react.navigationContainer.js',
        publicPath: 'dist/'
    },
    resolve: {
        extensions: ['.js', '.jsx', '.ts', '.tsx']
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
            test: /\.(js|jsx|ts|tsx)$/,
            exclude: /node_modules/,
            use: {
                loader: 'babel-loader',
                options: {
                    presets: [
                        ['@babel/preset-env'], 
                        ['@babel/preset-react'],
                        ['@babel/preset-typescript']
                    ]
                }
            }
          },
        ],
    },
}
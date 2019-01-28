const {injectBabelPlugin} = require('react-app-rewired')
const rewireLess = require('react-app-rewire-less-modules')

module.exports = function override(config, env) {
  config = injectBabelPlugin(['import', {libraryName: 'antd', libraryDirectory: 'es', style: true}], config);
  config = rewireLess.withLoaderOptions({
    modifyVars: {
    },
  })(config, env);
  return config;
};
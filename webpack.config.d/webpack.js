config.resolve.modules.push("../../processedResources/js/main");

if (config.devServer) {
    config.devServer.stats = {
        warnings: false
    };
    config.devServer.clientLogLevel = 'error';
   // config.devtool = 'eval-cheap-source-map';
    config.devServer.host = '0.0.0.0';
}

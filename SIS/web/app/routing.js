// Routing settings
app.config( function($routeProvider, $httpProvider) {

    //$httpProvider.defaults.withCredentials = true;
    //$httpProvider.interceptors.push('APIInterceptor');

    $routeProvider.when('/',
                        {templateUrl: './views/main.html',
                         controller: 'home_ctrl'});
    $routeProvider.when('/login',
                        {templateUrl: './views/login.html',
                         controller: 'login_ctrl'});
    $routeProvider.when('/oauth1/authorize',
                        {templateUrl: './views/authorize.html',
                         controller: 'authorize_ctrl'});
     $routeProvider.when('/developer',
                        {templateUrl: './views/developer.html',
                         controller: 'developer_ctrl'});
    $routeProvider.when('/signup',
                        {templateUrl: './views/signup.html',
                         controller: 'signup_ctrl'});
    $routeProvider.when('/users',
                        {templateUrl: './views/users.html',
                         controller: 'users_ctrl'});
    $routeProvider.when('/upload',
                        {templateUrl: './views/upload.html',
                         controller: 'upload_ctrl'});
    $routeProvider.when('/user/:id/images',
                        {templateUrl: './views/images.html',
                         controller: 'user_images_ctrl'});
    $routeProvider.when('/images',
                        {templateUrl: './views/images.html',
                         controller: 'images_ctrl'});
    $routeProvider.when('/dropbox',
                        {templateUrl: './views/dropbox.html',
                         controller: 'dropbox_ctrl'});
     $routeProvider.when('/editor',
                        {templateUrl: './views/editor.html',
                         controller: 'editor_ctrl'});
    $routeProvider.otherwise({redirectTo:'/'});

});
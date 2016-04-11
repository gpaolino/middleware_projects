app.controller('main_ctrl', function ($scope, $http, $window, $interval, $location, $rootScope) {

    $scope.$on('$routeChangeStart', function(next, current) { 
        $("body").hide();
    });

    $rootScope.ready = function() {
        $("body").fadeIn('slow').delay(1000);
    }

    $rootScope.check = function(setReady) {


        access_token = getCookie("access-token");
        user_id = getCookie("user-id");

        if( access_token == "" || user_id == "" ) {
            if( $location.path() != "/login" ) 
                $window.open("./#/login", "_self");
            return;
        }

        $rootScope.loggedUser = {};
        $rootScope.loggedUser.id = user_id;
        $rootScope.loggedUser.token = access_token;

        $http({
            method: 'GET',
            url: API +"user"+ "/" + user_id,

            headers: {'access-token': access_token},
        }).then(
            function (response) {
                $rootScope.loggedUser = response.data;
                $rootScope.loggedUser.token = access_token;

                if(setReady) {
                    $rootScope.ready();
                }
            }
        );

    }

    $rootScope.logout = function() {

        $http({
            method: 'DELETE',
            headers: {'access-token': access_token},
            url: API+"session"
        }).then(
            function() {
                deleteCookie("access-token");
                deleteCookie("user-id");
                $rootScope.loggedUser = null;
                $window.open("./#/login", "_self");
            }
        );

    }


});


app.controller('home_ctrl', function($scope, $http, $window, $rootScope) {

    $rootScope.check(true);

    $scope.deleteUser = function() {

        $http({
            method: 'DELETE',
            url: API + "user/" + $rootScope.loggedUser.id,
            headers: {'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {
                $window.open("./#/login", "_self");
            }
        );

    }

});

app.controller('developer_ctrl', function($scope, $http, $window, $rootScope) {

    $rootScope.check(false);

    $scope.getApps = function() {
        $http({
            method: 'GET',
            url: API +"user"+ "/" + $rootScope.loggedUser.id + "/apps",
            headers: {'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {
                $scope.apps = response.data.consumers;
                $rootScope.ready();
            }
        );
    }

    $scope.addNewApp = function(appName) {

        params = {} ;
        params.name = appName;

        $http({
            method: 'POST',
            url: API +"user"+ "/" + $rootScope.loggedUser.id + "/apps",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded',
                      'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {
                $scope.getApps();
            }
        );

    }

    $scope.getApps();

});

app.controller('authorize_ctrl', function($scope, $http, $window, $rootScope, $routeParams) {

    $rootScope.check(true);


    $http({
        method: 'GET',
        url: API +"consumer"+ "/" + $routeParams.oauth_token +"/info",
        headers: {'access-token': $rootScope.loggedUser.token},
    }).then(
        function (response) {
            $scope.appName = response.data.appName;
        }
    );


    $scope.authorize = function() {
        $http({
            method: 'GET',
            url: API +"consumer"+ "/" + $routeParams.oauth_token +"/authorize",
            headers: {'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {
                $scope.showVerifier = true;
                $scope.verifier = response.data.verifier;
            }
        );
    }



});

app.controller('login_ctrl', function($scope, $http, $window,$rootScope) {

    $scope.login = function(email, password) {
        params = {};
        params.email = email;
        params.password = password;
        $http({
            method: 'POST',
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            url: API+"session"
        }).then(
            function (response) {
                document.cookie="access-token="+response.data.token;
                document.cookie="user-id="+response.data.user;
                $window.open("./#/main", "_self");
            }
            ,
            function (response) {

                if ( response.status == "401" )
                    $scope.error_message = "Wrong Credentials.";
                else if ( response.status == "404" )
                    $scope.error_message = "Bad Request.";
                else 
                    $scope.error_message = "Unknown error.";

            }
        );
    }

    $rootScope.ready();
});

app.controller('signup_ctrl', function($scope, $http, $window, $rootScope) {

    $scope.newuser = {};

    $scope.signup = function(user) {
        $http({
            method: 'POST',
            data: $.param(user),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            url: API+"user"
        }).then(
            function (response) {
                $window.open("./#/login", "_self");
            }
            ,
            function (response) {  
                    $scope.error_message =  response.statusText;
            }
        );
    }

    $rootScope.ready();
});

app.controller('users_ctrl', function($scope, $http, $window, $rootScope) {

    $rootScope.check();

    $http({
        method: 'GET',
        url: API +"user"+ "/",
        headers: {'access-token': $rootScope.loggedUser.token},
    }).then(
        function (response) {
            $scope.users = response.data;
            $rootScope.ready();
        }
    );

});

app.controller('user_images_ctrl', function($scope, $http, $window, $rootScope, $routeParams) {

    $rootScope.check(false);


    $http({
        method: 'GET',
        url: API +"user"+ "/" + $routeParams.id,
        headers: {'access-token': $rootScope.loggedUser.token},
    }).then(
        function (response) {
            $scope.owner = response.data;

            if($scope.images != null) {
                $rootScope.ready();
            }
        }
    );

    $http({
        method: 'GET',
        url: API +"user"+ "/" + $routeParams.id +"/images",
        headers: {'access-token': $rootScope.loggedUser.token},
    }).then(
        function (response) {
            $scope.images = response.data.images;

            if($scope.owner != null) {
                $rootScope.ready();
            }
        }
    );

});

app.controller('upload_ctrl', function($scope, $http, $window, $rootScope) {

    $rootScope.check(true);

    $("#uploader").hide();

    $scope.titleChanged = function() {
        if($scope.photoTitle.length > 0) {
            $('#uploader').fadeIn('slow');
        } else {
            $("#uploader").fadeOut('slow');
        }
    }


    $("#dropzone").dropzone({
        url: API+"image/",
        dictResponseError: 'Error uploading file!',
        headers: {
            'access-token': $rootScope.loggedUser.token
        },
        init: function () {
            this.on("complete", function (file) {
                if (this.getUploadingFiles().length === 0 && this.getQueuedFiles().length === 0) {
                    $scope.uploadCompleted = true;
                    $scope.$apply(); 
                }
            })}
    });


});

app.controller('editor_ctrl', function($scope, $http, $window, $rootScope, $interval) {

    $scope.options = {};
    $scope.options.crop = true;
    $scope.options.greyscale = true;

    $scope.refreshStatus = function() {
        $http({
            method: 'GET',
            url: API +"editor/"+$scope.usid
        }).then(

            function (response) {
                $scope.uploadsession = response.data;

                if( $scope.uploadsession.uploaded == 4) {
                    $scope.uploadDone = true;
                }
                if( $scope.uploadsession.status == 1 ) {
                    $scope.started = true;
                    $scope.progress = $scope.options.crop ? "Cropping" : "Resizing";

                }
                if( $scope.uploadsession.status == 2 ) {
                    if ( $scope.options.greyscale ) {
                        $scope.progress = "GrayScaling";
                    } else {
                        $scope.progress = "Combining";
                    }
                }
                if( $scope.uploadsession.status == 3 ) {
                    $scope.progress = "Combining";
                }
                if( $scope.uploadsession.status == 4 ) {
                    $scope.progress = "Completed";
                    $interval.cancel($scope.interval);

                    $scope.completed = true;
                    $scope.result = $scope.uploadsession.result;
                }


            }
        );


    }

    $scope.start = function() {
        $http({
            method: 'POST',
            data: $.param($scope.options),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            url: API +"editor/"+$scope.usid+"/options"
        }).then( function(response) {

        });
    }

    // Initiate an upload session
    $http({
        method: 'POST',
        url: API +"editor"
    }).then(
        function (response) {
            $scope.usid = response.data.id;


            // Setup Dropzone
            $("#dropzone").dropzone({
                url: API+"editor/"+$scope.usid+"/upload",
                uploadMultiple: false,
                init: function () {
                    this.on("complete", function (file) {
                        if (this.getUploadingFiles().length === 0 && this.getQueuedFiles().length === 0) {
                            //dosomething
                        }
                    })},
                dictResponseError: 'Error uploading file!'
            });

            $scope.interval = $interval($scope.refreshStatus, 2000);

            $rootScope.ready();
        }
    );



});



app.controller('dropbox_ctrl', function($scope, $http, $window, $rootScope) {

    $rootScope.check(true);

    $scope.authDropbox = function() {

        $http({
            method: 'GET',
            url: API +"user"+ "/" + $rootScope.loggedUser.id + "/dropbox",
            headers: {'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {
                redirect_url = response.data.auth_url;

                $window.open(redirect_url, '_blank');

                $scope.displayCodeForm = true;

            }
        );

    }

    $scope.completeDropboxAuth = function(code) {

        params = {};
        params.auth_code = code;

        $http({
            method: 'POST',
            url: API +"user"+ "/" + $rootScope.loggedUser.id + "/dropbox",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded',
                      'access-token': $rootScope.loggedUser.token},
        }).then(
            function (response) {


                $rootScope.loggedUser.pairedWithDropbox = true;

            }
        );

    }

});
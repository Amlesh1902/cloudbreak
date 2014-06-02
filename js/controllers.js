'use strict';

/* Controllers */

var cloudbreakControllers = angular.module('cloudbreakControllers', []);

cloudbreakControllers.controller('cloudbreakController', ['$scope', '$http', 'Templates', '$location', '$rootScope',
    function ($scope, $http, Templates, $location, $rootScope) {
        $scope.form = undefined;
        $http.defaults.useXDomain = true;
        delete $http.defaults.headers.common['X-Requested-With'];
        $http.defaults.headers.common['Content-Type']= 'application/json';

        if($rootScope.apiUrl === null || $rootScope.apiUrl === undefined ) {
            $rootScope.apiUrl = "http://localhost:8080";
        }
        if($rootScope.basic_auth === null || $rootScope.basic_auth === undefined ) {
            $rootScope.basic_auth = "dXNlckBzZXEuY29tOnRlc3QxMjM=";
        }

        $scope.reloadCtrl = function(){
            console.log('reloading...');
            $route.reload();
        }

        $scope.signIn = function() {
            if(emailFieldLogin.value === "user@seq.com" && passwFieldLogin.value === "test123") {
                localStorage.signedIn = true;
                $rootScope.signedIn = true;
                $location.path("/console");
            }
        }

        if (typeof (Storage) !== "undefined") {
            if (localStorage.signedIn === 'true') {
                $rootScope.signedIn = true;
                $location.path("/console");
            }
        } else {
            console.log("No localstorage support!");
        }

    }
]);

cloudbreakControllers.controller('consoleController', ['$scope', '$http', 'Templates', '$location', '$rootScope',
    function ($scope, $http, Templates, $location, $rootScope) {
        $scope.form = undefined;
        $http.defaults.useXDomain = true;
        delete $http.defaults.headers.common['X-Requested-With'];
        $http.defaults.headers.common['Content-Type']= 'application/json';

        $scope.signOut = function() {
            $rootScope.signedIn = false;
            localStorage.signedIn = false;
        }

        $scope.reloadCtrl = function(){
            console.log('reloading...');
            $route.reload();
        }

        if (typeof (Storage) !== "undefined") {
            if (localStorage.signedIn === 'false' || localStorage.signedIn === undefined) {
                $location.path("/");
            }
        } else {
            $location.path("/");
        }

    }
]);

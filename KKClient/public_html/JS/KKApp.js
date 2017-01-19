var module = angular.module("KKApp", ["ui.router"]);

module.config(function ($urlRouterProvider, $stateProvider) {
    $urlRouterProvider.otherwise("/");

    $stateProvider.state("home", {
        url: "/",
        templateUrl: "templates/home.html",
        controller: "homeCtrl"
    });
});

module.controller("headerCtrl", function ($scope, $rootScope, kkService) {
    $scope.submitForm = function () {
        console.log($scope.username);
        console.log($scope.password);
        kkService.checkLogin().then(function () {
            $rootScope.username = $scope.username;
            $rootScope.password = $scope.password;
        }).error(function () {
            console.log("Error i submitForm");
        });
    };
});

module.controller("homeCtrl", function ($scope, $rootScope, kkService) {
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        $scope.recipes = data.data;
        console.log(data.data);
    });
});

module.service("kkService", function ($q, $http, $rootScope) {
    this.getRecipe = function () {
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/recept";
        var auth = "Basic Q2FzcGVyOmphZ2VlYWRtaW4=";
        $http({
            url: url,
            method: "GET",
            headers: {'Authorization': auth}
        }).then(function (data) {
            deffer.resolve(data);
        });
        return deffer.promise;
    };
    
    this.checkLogin = function(username, password){
        var url="http://localhost:8080/KKApp/webresources/user";
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
           url: url,
           method: "GET",
           headers: {'Authorization': auth}
        }).then(function (){
            $rootScope.username = username;
            $rootScope.password = password;
        });
    };
});

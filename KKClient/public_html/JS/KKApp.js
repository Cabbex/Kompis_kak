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
    $scope.submitForm = function (login) {
        if (login === true) {
            kkService.checkLogin($scope.username, $scope.password);
        } else {
            kkService.createUser($scope.username, $scope.password); //Inte klar!
        }
    };
    $scope.logout = function () {
        console.log("Logout funktion");
        $rootScope.logged = false;
        $rootScope.username = "";
    };
});

module.controller("homeCtrl", function ($scope, kkService) {
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

    this.checkLogin = function (username, password) {
        var url = "http://localhost:8080/KKApp/webresources/user";
        var auth = "Basic " + window.btoa(username + ":" + password);
        $http({
            url: url,
            method: "GET",
            headers: {'Authorization': auth}
        }).then(function (status) {
            if (status.status === 200) {
                $rootScope.username = username;
                $rootScope.logged = true;
            } else {
                $rootScope.logged = false;
                $rootScope.username = "";
            }
        });
    };

});

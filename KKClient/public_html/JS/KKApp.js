var module = angular.module("KKApp", ["ui.router"]);

module.config(function ($urlRouterProvider, $stateProvider) {
    $urlRouterProvider.otherwise("/");

    $stateProvider.state("home", {
        url: "/",
        templateUrl: "templates/home.html",
        controller: "homeCtrl"
    }).state("addRecipe", {
        url: "/addRecipe",
        templateUrl: "templates/addRecipe.html",
        controller: "addCtrl"
    });
});


module.controller("headerCtrl", function ($scope, $rootScope, kkService) {
    $scope.submitForm = function (login) {
        if (login === true) {
            kkService.checkLogin($scope.username, $scope.password);
        } else {
            kkService.createUser($scope.username, $scope.password);
        }
    };
    $scope.logout = function () {
        $rootScope.logged = false;
        $rootScope.username = "";
    };
});


module.controller("addCtrl", function ($scope, $rootScope, kkService) {
    // Indata
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        $scope.Recipe_id = data.data[data.data.length - 1].id + 1;
    });

    promise = kkService.getAllTags();
    promise.then(function (data) {
        $scope.tags = data.data;
    });

    promise = kkService.getAllIngrediense();
    promise.then(function (data) {
        $scope.ingredienses = data.data;
    });

    //Utdata
    $scope.PostObject = [];
    $scope.PreviewIngr = [];
    var ingredient_name;
    $scope.insertIngr = function () {
        var JSONObject = {"recipe_id": $scope.Recipe_id, "amount": $scope.amount, "ingrediens": $scope.ingrediens_id};
        $scope.PostObject.push(JSONObject);
        //funkar inte.
//        for (var i = 0; i < $scope.ingredienses.length; i++) {
//            console.log($scope.ingredienses[i].id + " " + $scope.ingrediens_id);
//            if ($scope.ingredienses[i].id === $scope.ingrediens_id) {
//                ingredient_name = $scope.ingredienses[i].ingredienseName;
//            }
//        }
//        var PreviewObject = {"amount": $scope.amount, "name": ingredient_name};
//        console.log(PreviewObject);
//        $scope.PreviewIngr.push(PreviewObject);
    };
});


module.controller("homeCtrl", function ($scope, kkService) {
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        $scope.recipes = data.data;
    });
});



module.service("kkService", function ($q, $http, $rootScope) {
    this.getRecipe = function () {
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/recept";
        $http({
            url: url,
            method: "GET"
        }).then(function (data) {
            deffer.resolve(data);
        });
        return deffer.promise;
    };

    this.getAllIngrediense = function () {
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/ingrediense";
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);

        $http({
            url: url,
            method: "GET",
            headers: {'Authorization': auth}
        }).then(function (data) {
            deffer.resolve(data);
        });
        return deffer.promise;
    };
    this.getAllTags = function () {
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/tag";
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);

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
        }).then(function (data) {
            if (data.status === 200) {
                $rootScope.id = data.data[0].id;
                $rootScope.username = username;
                $rootScope.password = password;
                $rootScope.logged = true;
            } else {
                $rootScope.id = 0;
                $rootScope.logged = false;
                $rootScope.username = "";
                $rootScope.password = "";
            }
        });
    };

    this.createUser = function (username, password) { //Måste fånga fel här!
        var url = "http://localhost:8080/KKApp/webresources/user";
        if (username === null || password === null) {
            return false;
        }
        var data = [{
                name: username,
                password: password
            }];
        $http({
            url: url,
            method: "POST",
            data: data
        }).then(function (data) {
            console.log(data.status);
        });
    };
});

var module = angular.module("KKApp", ["ui.router"]);

module.config(function ($urlRouterProvider, $stateProvider){
   $urlRouterProvider.otherwise("/");
   
   $stateProvider.state("home",{
       url:"/",
       templateUrl:"templates/home.html",
       controller:"homeCtrl"
   });
});

module.controller("homeCtrl", function($scope, $rootScope, kkService){
    var promise = kkService.getRecipe();
    promise.then(function(data){
        $scope.recipes = data;     
        console.log($scope.recipes);
    });
});

module.service("kkService", function ($q,$http,$rootScope){
    this.getRecipe = function(){
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/recept";
        var auth = "Basic Q2FzcGVyOmphZ2VlYWRtaW4=";
        $http({
            url: url,
            method: "GET",
            headers: {'Authorization': auth}
        }).then(function (data){
            deffer.resolve(data);
        });
        return deffer.promise;
    };
});

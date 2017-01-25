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
    }).state("recipe", {
        url: "/recipe/:id",
        templateUrl: "templates/recipe.html",
        controller: "recipeCtrl"
    }).state("profile", {
        url: "/profil",
        templateUrl: "templates/profile.html",
        controller: "profileCtrl"
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
    $scope.PostIngredient = [];
    $scope.newRecipedata = [];
    $scope.insertIngr = function () {
        var JSONObject = {"recipe_id": $scope.Recipe_id, "amount": $scope.amount, "ingrediens": Number($scope.ingrediens_id)};
        console.log(typeof (JSONObject.ingrediens));
        $scope.PostIngredient.push(JSONObject);
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

    $scope.savePreset = function () {
        var JSONObject = {"rname": $scope.recipeName,
            "description": $scope.descInput,
            "author": $rootScope.username,
            "tag": $scope.selectedTag};
        $scope.newRecipedata[0] = JSONObject;
    };

    $scope.postRecipeCtrl = function () {
        if ($scope.newRecipedata === null || $scope.PostIngredient === null) {
            console.log("Var god och skriv in data innan du skickar iväg det."); //Gör så att knappen syns när man har data!
        } else {
            kkService.postRecipe($scope.newRecipedata);
            kkService.postIngrediens($scope.PostIngredient);
        }
    };
});

module.controller("homeCtrl", function ($scope, kkService) {
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        $scope.recipes = data.data;
    });

    $scope.gotoRecipe = function (id) {
        window.location.href = "http://localhost:8383/KKClient/index.html#!/recipe/" + id;
    };
});

module.controller("recipeCtrl", function ($scope, kkService, $rootScope, $stateParams) {
    //Indata

    var id = $stateParams;
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        for (var i = 0; i <= data.data.length - 1; i++) {
            if (data.data[i].id === Number(id.id)) {
                $scope.recipe = data.data[i];
                if($scope.recipe.author === $rootScope.username){
                    $scope.canEdit = true;
                }
            }

        }
    });
    promise = kkService.getIngrediense(Number(id.id));
    promise.then(function (data) {
        $scope.ingrs = data.data;
    });

    promise = kkService.getAllIngrediense();
    promise.then(function (data) {
        $scope.ingredienses = data.data;
    });

    promise = kkService.getAllTags();
    promise.then(function (data) {
        $scope.tags = data.data;
    });


    //Utdata

    $scope.removeIngrediens = function (id) { //Ta bort den raden som blir klickad på.
        kkService.removeIngr(id);
    };

    $scope.insertIngr = function () {
        var JSONObject = {"recipe_id": Number(id.id), "amount": $scope.amount, "ingrediens": Number($scope.ingrediens_id)};
        console.log(JSONObject);
        $scope.PostIngredient = [];
        $scope.PostIngredient.push(JSONObject);
        if ($scope.PostIngredient === null) {
            console.log("Väl något innan du skickar");
        } else {
            kkService.postIngrediens($scope.PostIngredient);
        }
        //Uppdaterar tabellen
        promise = kkService.getIngrediense(Number(id.id));
        promise.then(function (data) {
            $scope.ingrs = data.data;
        });

    };

    $scope.removeRecipe = function () {
        kkService.removeRecipe(Number(id.id));
    };

    function refresh() {
        $scope.editing = false;
        window.location.href = "http://localhost:8383/KKClient/index.html#!/";
    }

    $scope.saveChanges = function () {
        var putArray = [{"rname": $scope.newName, "description": $scope.newDesc, "author": $rootScope.username, "tag": $scope.newTag}];
        kkService.putRecipe(putArray, Number(id.id));
        setTimeout(refresh(), 5000);
    };

    //Funktioner
    
    $scope.edit = function () {
        $scope.editing = true;
        $scope.newName = $scope.recipe.name;
        $scope.newDesc = $scope.recipe.desc;
    };
});

module.controller("profileCtrl", function ($scope, kkService, $rootScope) {
    var recipeArray = [];
    var promise = kkService.getRecipe();
    promise.then(function (data) {
        for (var i = 0; i <= data.data.length - 1; i++) {
            if (data.data[i].author === $rootScope.username) {
                recipeArray.push(data.data[i]);
            }
        }

        $scope.userRecipe = recipeArray;
        $scope.amountRecipe = recipeArray.length;
    });

    $scope.gotoRecipe = function (id) {
        window.location.href = "http://localhost:8383/KKClient/index.html#!/recipe/" + id;
    };
});

module.service("kkService", function ($q, $http, $rootScope) {

    //Get services

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

    this.getIngrediense = function (id) {
        var deffer = $q.defer();
        var url = "http://localhost:8080/KKApp/webresources/ingrediense/" + id;
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

    //User services

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

    //Post services

    this.postRecipe = function (RecipeData) {
        var url = "http://localhost:8080/KKApp/webresources/recept";
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
            url: url,
            method: "POST",
            headers: {'Authorization': auth},
            data: RecipeData
        }).then(function (data) {
            console.log(data.status);
        });
    };
    this.postIngrediens = function (IngredienseData) {
        var url = "http://localhost:8080/KKApp/webresources/ingrediense";
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
            url: url,
            method: "POST",
            headers: {'Authorization': auth},
            data: IngredienseData
        }).then(function (data) {
            console.log(data.status);
        });
    };

    //Delete services
    this.removeIngr = function (id) {
        var url = "http://localhost:8080/KKApp/webresources/ingrediense/" + id;
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
            url: url,
            method: "DELETE",
            headers: {'Authorization': auth}
        }).then(function (data) {
            console.log(data.status);
        });
    };
    
    this.removeRecipe = function (id) {
        var url = "http://localhost:8080/KKApp/webresources/recept/" + id;
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
            url: url,
            method: "DELETE",
            headers: {'Authorization': auth}
        }).then(function (data) {
            console.log(data.status);
        });
    };

    //Put services

    this.putRecipe = function (data, id) {
        var url = "http://localhost:8080/KKApp/webresources/recept/" + id;
        var auth = "Basic " + window.btoa($rootScope.username + ":" + $rootScope.password);
        $http({
            url: url,
            method: "PUT",
            data: data,
            headers: {'Authorization': auth}
        }).then(function (data) {
            console.log(data.status);
        });
    };
});

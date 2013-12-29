var pms = angular.module('pms', ['ngRoute']);

pms.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/new', {
        templateUrl: 'partials/new-patient.html',
        controller: 'NewPatientCtrl'
    }).when('/default', {
            templateUrl: '/partials/default.html',
            controller: 'NewPatientCtrl'
        }).when('/savePatient', {

            templateUrl: '/partials/'
        })
        .otherwise({
            redirectTo: '/default'
        });

}]);

pms.controller('NewPatientCtrl', function ($scope, $http) {

    $scope.patient = "Superman";

    $scope.linkClicked = function () {

        $http.post("wtf", {name: $scope.name, age: $scope.age, problem: $scope.problem});
    }
});
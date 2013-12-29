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

pms.controller('NewPatientCtrl', function ($scope) {

    $scope.patient = "Superman";

    $scope.linkClicked = function () {
        alert($scope.age);
    }
});
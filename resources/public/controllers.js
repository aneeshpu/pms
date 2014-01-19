var pms = angular.module('pms', ['ngRoute']);

pms.factory('patientService', function () {

    return {
        saveCurrentPatient: function (name, age, id) {
            this.name = name;
            this.age = age;
            this.id = id;
        },

        getCurrentPatient: function getCurrentPatient() {
            return {
                name: this.name,
                age: this.age,
                id: this.id
            }
        }
    }
});

pms.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/new', {
        templateUrl: 'partials/new-patient.html',
        controller: 'NewPatientCtrl'

    }).when('/default', {
            templateUrl: '/partials/default.html',
            controller: 'NewPatientCtrl'

        }).when('/savePatient', {
            templateUrl: '/partials/'

        }).when('/addCase', {
            templateUrl: 'partials/add-case.html',
            controller: 'NewPatientCtrl'

        }).when('/search', {
            templateUrl: 'partials/search-patient.html',
            controller: 'NewPatientCtrl'
        }).otherwise({
            redirectTo: '/default'
        });

}]);

pms.controller('NewPatientCtrl', function ($scope, $http, patientService) {

    $scope.linkClicked = function () {

        $http.post("wtf", {name: $scope.name, age: $scope.age, problem: $scope.problem});
    }

    $scope.searchPatient = function () {
        $http.get("/patient/" + $scope.patientName).success(function (data) {
            $scope.name = data.name;
            $scope.age = data.age;
            $scope.id = data.id;

            patientService.saveCurrentPatient(data.name, data.age, data.id);
        });
    }

    $scope.currentPatient = patientService.getCurrentPatient();

    $scope.createCase = function () {

        $http.post("/patients/" + $scope.currentPatient.id + "/cases", {id: $scope.currentPatient.id, complaint: $scope.complaint});
    }
});
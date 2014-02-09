var pms = angular.module('pms', ['ngRoute']);

pms.factory('patientService', function () {

    return {
        saveCurrentPatient: function (patient) {
            this.patient = patient;
        },

        getCurrentPatient: function getCurrentPatient() {
            return this.patient;
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

        }).when('/addSession', {
            templateUrl: 'partials/view-session.html',
            controller: 'NewPatientCtrl'

        }).when('/search', {
            templateUrl: 'partials/search-patient.html',
            controller: 'NewPatientCtrl'

        }).when('/viewPatient',{
            templateUrl: 'partials/view-patient.html',
            controller: 'NewPatientCtrl'

        }).otherwise({
            redirectTo: '/default'
        });

}]);

pms.controller('NewPatientCtrl', function ($scope, $http, patientService, $location) {

    $scope.linkClicked = function () {

        $http.post("wtf", {name: $scope.name, age: $scope.age, problem: $scope.problem});
    }

    $scope.searchPatient = function () {
        $http.get("/patient/" + $scope.patientName).success(function (data) {
            $scope.patients = data
            patientService.saveCurrentPatient(data);
        });
    }

    //Used by add-case. Do not remove.
    $scope.currentPatient = patientService.getCurrentPatient();

    $scope.createCase = function () {
        //templatize the URL
        //TODO:Change /cases to /complaints
        $http.post("/patients/" + patientService.getCurrentPatient().id + "/cases", {id: patientService.getCurrentPatient().id, complaint: $scope.complaint});
    }

    $scope.addSession = function () {
        $http.post("/patients/" + patientService.getCurrentPatient().id + "/cases/" + $scope.currentComplaint.id, {diagnosis: $scope.diagnosis, medicine: $scope.medicine});
    }

    $scope.viewSession = function (complaint) {
        $scope.currentComplaint = complaint;
        $http.get("")
        $location.path("addSession");
    }

    $scope.viewPatient = function(patient){
        patientService.saveCurrentPatient(patient);
        $location.path("viewPatient");
    }

    $scope.getCurrentPatient = function(){
        $scope.patient = patientService.getCurrentPatient();
    }
});
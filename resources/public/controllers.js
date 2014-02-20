var pms = angular.module('pms', ['ngRoute']);

pms.factory('patientService', function () {

    return {
        saveCurrentPatient: function (patient) {
            this.patient = patient;
        },

        getCurrentPatient: function getCurrentPatient() {
            return this.patient;
        },

        saveCurrentComplaint: function (complaint){
            this.complaint = complaint;
        },

        getCurrentComplaint: function(){
            return this.complaint;
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

        }).when('/sessionSaved', {
            templateUrl: 'partials/session-saved.html',
            controller: 'NewPatientCtrl'

        }).otherwise({
            redirectTo: '/default'
        });

}]);

pms.controller('NewPatientCtrl', function ($scope, $http, patientService, $location) {

    $scope.createPatient = function () {

        $http.post("patients", {name: $scope.name
            , age: $scope.age
            , problem: $scope.problem
            , address: $scope.address}).success(function(data){
            $scope.viewPatient(data);
        });
    }

    $scope.searchPatient = function () {
        $http.get("/patient/" + $scope.patientName).success(function (data) {
            $scope.patients = data
            patientService.saveCurrentPatient(data);
        });

        $location.path("search");
    }

    //Used by add-case. Do not remove.
    $scope.currentPatient = patientService.getCurrentPatient();

    $scope.createCase = function () {
        //templatize the URL
        //TODO:Change /cases to /complaints
        $http.post("/patients/" + patientService.getCurrentPatient().id + "/cases", {id: patientService.getCurrentPatient().id, complaint: $scope.complaint}).success(function (data){
            $scope.viewSession(patientService.getCurrentPatient(), data);
        });
    }

    $scope.addSession = function () {
        $http.post("/patients/" + patientService.getCurrentPatient().id + "/cases/" + $scope.currentComplaint.id, {diagnosis: $scope.diagnosis, medicine: $scope.medicine}).success(function (data){
            patientService.saveCurrentPatient(data);
            $location.path("viewPatient");
        });
    }

    $scope.viewSession = function (patient, complaint) {
        patientService.saveCurrentComplaint(complaint);
        patientService.saveCurrentPatient(patient);

        $location.path("addSession");
    }

    $scope.viewPatient = function(patient){
        patientService.saveCurrentPatient(patient);
        $location.path("viewPatient");
    }

    $scope.getCurrentPatient = function(){
        $scope.patient = patientService.getCurrentPatient();
    }

    $scope.initComplaint = function(){
        $scope.currentComplaint = patientService.getCurrentComplaint();
        $scope.currentPatient = patientService.getCurrentPatient();
    }
});
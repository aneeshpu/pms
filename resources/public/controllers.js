var pms = angular.module('pms', ['ngRoute']);

pms.factory('patientService', function () {

    return {
        saveCurrentPatient: function (patient) {
            this.patient = patient;
        },

        getCurrentPatient: function getCurrentPatient() {
            return this.patient;
        },

        saveCurrentComplaint: function (complaint) {
            this.complaint = complaint;
        },

        getCurrentComplaint: function () {
            return this.complaint;
        },
        
        saveCurrentSession: function(session, currentComplaint){
            this.currentSession = session;
            this.currentComplaint = currentComplaint;
        },
        getCurrentSession: function() {
            return {session: this.currentSession, complaint: this.currentComplaint};
        }

    }
});

pms.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/new', {
        templateUrl: 'partials/new-patient.html',
        controller: 'NewPatientCtrl'

    }).when('/default', {
            templateUrl: 'partials/default.html',
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

        }).when('/viewPatient', {
            templateUrl: 'partials/view-patient.html',
            controller: 'NewPatientCtrl'

        }).when('/sessionSaved', {
            templateUrl: 'partials/session-saved.html',
            controller: 'NewPatientCtrl'

        }).when('/viewSessionDetails',{
            templateUrl: 'partials/viewSession-details.html',
            controller: 'NewPatientCtrl'
        }).when('/allpatients',{
            templateUrl: 'partials/all-patients.html',
            controller: 'NewPatientCtrl'

        }).otherwise({
            redirectTo: '/default'
        })

}]);

pms.controller('NewPatientCtrl', function ($scope, $http, patientService, $location) {

    $scope.formatDate = function(isoDate){
        var formattedDate = new Date(isoDate);
        return formattedDate.toLocaleDateString() + " " + formattedDate.toLocaleTimeString();
    }

    $scope.createPatient = function () {

        $http.post("patients", {name: $scope.name, age: $scope.age, problem: $scope.problem, address: $scope.address})
            .success(function (data) {
                $scope.viewPatient(data);
            }).error(function (data) {
                $scope.errMsg = data.message;
            });
    }

    $scope.searchPatient = function () {
        $http.get("patient/" + $scope.patientName).success(function (data) {
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
        $http.post("patients/" + patientService.getCurrentPatient().id + "/cases", {id: patientService.getCurrentPatient().id, complaint: $scope.complaint})
            .success(function (data) {
                $scope.viewSession(patientService.getCurrentPatient(), data);
            }).error(function (data) {
                $scope.errMsg = data.message;
            });
    }

    var collectMedicines = function(medicines){
        var medicineNames = [];
        for(var i=0;i<medicines.length;i++){
            medicineNames.push(medicines[i].value);
        }

        return medicineNames;
    }

    $scope.addSession = function () {
        var medicineNames = collectMedicines($scope.medicine);
        console.log("medicine names:" + medicineNames);

        $http.post("patients/" + patientService.getCurrentPatient().id + "/cases/" + $scope.currentComplaint.id, {diagnosis: $scope.diagnosis, medicine: medicineNames.join()})
            .success(function (data) {
                patientService.saveCurrentPatient(data);
                $location.path("viewPatient");
            }).error(function (data) {
                $scope.errMsg = data.message;
            });
    }

    $scope.addMedicine = function(){
        $scope.medicine.push({"medicine":"Name", "value":""});
    }

    $scope.viewSession = function (patient, complaint) {
        patientService.saveCurrentComplaint(complaint);
        patientService.saveCurrentPatient(patient);

        $location.path("addSession");
    }

    $scope.viewPatient = function (patient) {
        patientService.saveCurrentPatient(patient);
        $location.path("viewPatient");
    }

    $scope.getCurrentPatient = function () {
        $scope.patient = patientService.getCurrentPatient();
    }

    $scope.initComplaint = function () {
        $scope.currentComplaint = patientService.getCurrentComplaint();
        $scope.currentPatient = patientService.getCurrentPatient();
        $scope.medicine = [{"medicine":"Name", "value":""}];
    }

    $scope.getPatients  = function(index){
        $http.get("patients/" + index).success(function (data){
            $scope.patients = data;
        }).error(function (data){
            $scope.errMsg = data.message;
        });

        $location.path("allpatients");

    }

    $scope.allPatients = function(){
        $scope.searchIndex = 1;
        $scope.getPatients($scope.searchIndex);
    }

    $scope.nextPatients = function(){
        if($scope.searchIndex > 1 && $scope.patients.length == 0){
            return;
        }

        $scope.searchIndex++;
        $scope.getPatients($scope.searchIndex);
    }

    $scope.prevPatients = function(){
        if($scope.searchIndex <= 1){
            return;
        }
        $scope.searchIndex--;
        $scope.getPatients($scope.searchIndex);
    }

    $scope.viewSessionDetails = function(patient, currentSession, currentComplaint){
        patientService.saveCurrentPatient(patient);
        patientService.saveCurrentSession(currentSession, currentComplaint);
        $location.path("viewSessionDetails");
    }

    $scope.getCurrentSession = function() {
        return patientService.getCurrentSession();
    }
});

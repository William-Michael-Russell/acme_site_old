(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlpaNumericInputFieldDetailController', AlpaNumericInputFieldDetailController);

    AlpaNumericInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'AlpaNumericInputField', 'User'];

    function AlpaNumericInputFieldDetailController($scope, $rootScope, $stateParams, entity, AlpaNumericInputField, User) {
        var vm = this;
        vm.alpaNumericInputField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:alpaNumericInputFieldUpdate', function(event, result) {
            vm.alpaNumericInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

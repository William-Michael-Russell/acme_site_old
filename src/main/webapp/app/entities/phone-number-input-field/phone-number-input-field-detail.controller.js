(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PhoneNumberInputFieldDetailController', PhoneNumberInputFieldDetailController);

    PhoneNumberInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'PhoneNumberInputField', 'User'];

    function PhoneNumberInputFieldDetailController($scope, $rootScope, $stateParams, entity, PhoneNumberInputField, User) {
        var vm = this;
        vm.phoneNumberInputField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:phoneNumberInputFieldUpdate', function(event, result) {
            vm.phoneNumberInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

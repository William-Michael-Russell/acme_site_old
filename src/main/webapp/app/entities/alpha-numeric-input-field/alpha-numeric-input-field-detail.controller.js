(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlphaNumericInputFieldDetailController', AlphaNumericInputFieldDetailController);

    AlphaNumericInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'AlphaNumericInputField', 'User'];

    function AlphaNumericInputFieldDetailController($scope, $rootScope, $stateParams, entity, AlphaNumericInputField, User) {
        var vm = this;
        vm.alphaNumericInputField = entity;

        var unsubscribe = $rootScope.$on('acmeSiteApp:alphaNumericInputFieldUpdate', function(event, result) {
            vm.alphaNumericInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('NumericInputFieldDetailController', NumericInputFieldDetailController);

    NumericInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'NumericInputField', 'User'];

    function NumericInputFieldDetailController($scope, $rootScope, $stateParams, entity, NumericInputField, User) {
        var vm = this;
        vm.numericInputField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:numericInputFieldUpdate', function(event, result) {
            vm.numericInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

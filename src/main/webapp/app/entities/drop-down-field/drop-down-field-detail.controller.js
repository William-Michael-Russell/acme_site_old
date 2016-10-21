(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('DropDownFieldDetailController', DropDownFieldDetailController);

    DropDownFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'DropDownField', 'User'];

    function DropDownFieldDetailController($scope, $rootScope, $stateParams, entity, DropDownField, User) {
        var vm = this;
        vm.dropDownField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:dropDownFieldUpdate', function(event, result) {
            vm.dropDownField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

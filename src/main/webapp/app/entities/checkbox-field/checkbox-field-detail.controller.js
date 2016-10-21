(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('CheckboxFieldDetailController', CheckboxFieldDetailController);

    CheckboxFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'CheckboxField', 'User'];

    function CheckboxFieldDetailController($scope, $rootScope, $stateParams, entity, CheckboxField, User) {
        var vm = this;
        vm.checkboxField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:checkboxFieldUpdate', function(event, result) {
            vm.checkboxField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

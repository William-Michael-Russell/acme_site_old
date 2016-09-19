(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('RadioCheckboxFieldDetailController', RadioCheckboxFieldDetailController);

    RadioCheckboxFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'RadioCheckboxField', 'User'];

    function RadioCheckboxFieldDetailController($scope, $rootScope, $stateParams, entity, RadioCheckboxField, User) {
        var vm = this;
        vm.radioCheckboxField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:radioCheckboxFieldUpdate', function(event, result) {
            vm.radioCheckboxField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('EmailInputFieldDetailController', EmailInputFieldDetailController);

    EmailInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'EmailInputField', 'User'];

    function EmailInputFieldDetailController($scope, $rootScope, $stateParams, entity, EmailInputField, User) {
        var vm = this;
        vm.emailInputField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:emailInputFieldUpdate', function(event, result) {
            vm.emailInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

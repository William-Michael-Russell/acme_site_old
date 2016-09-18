(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PasswordInputFieldDetailController', PasswordInputFieldDetailController);

    PasswordInputFieldDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'PasswordInputField', 'User'];

    function PasswordInputFieldDetailController($scope, $rootScope, $stateParams, entity, PasswordInputField, User) {
        var vm = this;
        vm.passwordInputField = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:passwordInputFieldUpdate', function(event, result) {
            vm.passwordInputField = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlertPromptDetailController', AlertPromptDetailController);

    AlertPromptDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'AlertPrompt', 'User'];

    function AlertPromptDetailController($scope, $rootScope, $stateParams, entity, AlertPrompt, User) {
        var vm = this;
        vm.alertPrompt = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:alertPromptUpdate', function(event, result) {
            vm.alertPrompt = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlertPromptDeleteController',AlertPromptDeleteController);

    AlertPromptDeleteController.$inject = ['$uibModalInstance', 'entity', 'AlertPrompt'];

    function AlertPromptDeleteController($uibModalInstance, entity, AlertPrompt) {
        var vm = this;
        vm.alertPrompt = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            AlertPrompt.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

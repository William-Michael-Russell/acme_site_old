(function () {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlertPromptDialogController', AlertPromptDialogController);

    AlertPromptDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AlertPrompt', 'User'];

    function AlertPromptDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, AlertPrompt, User) {
        var vm = this;
        vm.alertPrompt = entity;
        vm.users = User.query();

        vm.alertMe = function (myAlert) {
            alert("Hello! I am an alert box!");
        };

        // vm.alertMe(function myAlert() {
        //
        // });

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:alertPromptUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.alertPrompt.id !== null) {
                AlertPrompt.update(vm.alertPrompt, onSaveSuccess, onSaveError);
            } else {
                AlertPrompt.save(vm.alertPrompt, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

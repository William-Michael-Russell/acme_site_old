(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PasswordInputFieldDialogController', PasswordInputFieldDialogController);

    PasswordInputFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PasswordInputField', 'User'];

    function PasswordInputFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PasswordInputField, User) {
        var vm = this;
        vm.passwordInputField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:passwordInputFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.passwordInputField.id !== null) {
                PasswordInputField.update(vm.passwordInputField, onSaveSuccess, onSaveError);
            } else {
                PasswordInputField.save(vm.passwordInputField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

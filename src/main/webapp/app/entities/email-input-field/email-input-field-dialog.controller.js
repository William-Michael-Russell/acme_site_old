(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('EmailInputFieldDialogController', EmailInputFieldDialogController);

    EmailInputFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'EmailInputField', 'User'];

    function EmailInputFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, EmailInputField, User) {
        var vm = this;
        vm.emailInputField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:emailInputFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.emailInputField.id !== null) {
                EmailInputField.update(vm.emailInputField, onSaveSuccess, onSaveError);
            } else {
                EmailInputField.save(vm.emailInputField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

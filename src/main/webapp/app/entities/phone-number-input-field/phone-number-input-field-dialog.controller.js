(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PhoneNumberInputFieldDialogController', PhoneNumberInputFieldDialogController);

    PhoneNumberInputFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PhoneNumberInputField', 'User'];

    function PhoneNumberInputFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PhoneNumberInputField, User) {
        var vm = this;
        vm.phoneNumberInputField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:phoneNumberInputFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.phoneNumberInputField.id !== null) {
                PhoneNumberInputField.update(vm.phoneNumberInputField, onSaveSuccess, onSaveError);
            } else {
                PhoneNumberInputField.save(vm.phoneNumberInputField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

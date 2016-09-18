(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('NumericInputFieldDialogController', NumericInputFieldDialogController);

    NumericInputFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'NumericInputField', 'User'];

    function NumericInputFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, NumericInputField, User) {
        var vm = this;
        vm.numericInputField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:numericInputFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.numericInputField.id !== null) {
                NumericInputField.update(vm.numericInputField, onSaveSuccess, onSaveError);
            } else {
                NumericInputField.save(vm.numericInputField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

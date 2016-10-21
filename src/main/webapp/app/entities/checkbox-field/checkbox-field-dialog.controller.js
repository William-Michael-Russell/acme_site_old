(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('CheckboxFieldDialogController', CheckboxFieldDialogController);

    CheckboxFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'CheckboxField', 'User'];

    function CheckboxFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, CheckboxField, User) {
        var vm = this;
        vm.checkboxField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:checkboxFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.checkboxField.id !== null) {
                CheckboxField.update(vm.checkboxField, onSaveSuccess, onSaveError);
            } else {
                CheckboxField.save(vm.checkboxField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

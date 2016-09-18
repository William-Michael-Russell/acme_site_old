(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('FileUploadDialogController', FileUploadDialogController);

    FileUploadDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'FileUpload', 'User'];

    function FileUploadDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, FileUpload, User) {
        var vm = this;
        vm.fileUpload = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:fileUploadUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.fileUpload.id !== null) {
                FileUpload.update(vm.fileUpload, onSaveSuccess, onSaveError);
            } else {
                FileUpload.save(vm.fileUpload, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setFieldField = function ($file, fileUpload) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        fileUpload.fieldField = base64Data;
                        fileUpload.fieldFieldContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();

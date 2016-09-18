(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('ImageUploadDialogController', ImageUploadDialogController);

    ImageUploadDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ImageUpload', 'User'];

    function ImageUploadDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ImageUpload, User) {
        var vm = this;
        vm.imageUpload = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:imageUploadUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.imageUpload.id !== null) {
                ImageUpload.update(vm.imageUpload, onSaveSuccess, onSaveError);
            } else {
                ImageUpload.save(vm.imageUpload, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setImageField = function ($file, imageUpload) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        imageUpload.imageField = base64Data;
                        imageUpload.imageFieldContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();

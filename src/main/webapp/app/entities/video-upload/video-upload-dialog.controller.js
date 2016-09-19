(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('VideoUploadDialogController', VideoUploadDialogController);

    VideoUploadDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'VideoUpload', 'User'];

    function VideoUploadDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, VideoUpload, User) {
        var vm = this;
        vm.videoUpload = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:videoUploadUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.videoUpload.id !== null) {
                VideoUpload.update(vm.videoUpload, onSaveSuccess, onSaveError);
            } else {
                VideoUpload.save(vm.videoUpload, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setVideoField = function ($file, videoUpload) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        videoUpload.videoField = base64Data;
                        videoUpload.videoFieldContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();

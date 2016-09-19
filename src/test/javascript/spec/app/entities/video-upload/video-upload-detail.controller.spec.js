'use strict';

describe('Controller Tests', function() {

    describe('VideoUpload Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockVideoUpload, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockVideoUpload = jasmine.createSpy('MockVideoUpload');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'VideoUpload': MockVideoUpload,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("VideoUploadDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:videoUploadUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

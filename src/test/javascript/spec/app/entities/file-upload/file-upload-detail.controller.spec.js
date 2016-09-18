'use strict';

describe('Controller Tests', function() {

    describe('FileUpload Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFileUpload, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFileUpload = jasmine.createSpy('MockFileUpload');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'FileUpload': MockFileUpload,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("FileUploadDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:fileUploadUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

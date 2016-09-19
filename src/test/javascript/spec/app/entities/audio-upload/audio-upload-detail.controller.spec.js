'use strict';

describe('Controller Tests', function() {

    describe('AudioUpload Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAudioUpload, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAudioUpload = jasmine.createSpy('MockAudioUpload');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AudioUpload': MockAudioUpload,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("AudioUploadDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:audioUploadUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

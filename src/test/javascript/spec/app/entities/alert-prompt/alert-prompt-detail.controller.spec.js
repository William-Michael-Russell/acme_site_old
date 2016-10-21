'use strict';

describe('Controller Tests', function() {

    describe('AlertPrompt Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAlertPrompt, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAlertPrompt = jasmine.createSpy('MockAlertPrompt');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AlertPrompt': MockAlertPrompt,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("AlertPromptDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:alertPromptUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

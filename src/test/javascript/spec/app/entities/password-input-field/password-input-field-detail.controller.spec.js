'use strict';

describe('Controller Tests', function() {

    describe('PasswordInputField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPasswordInputField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPasswordInputField = jasmine.createSpy('MockPasswordInputField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'PasswordInputField': MockPasswordInputField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("PasswordInputFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:passwordInputFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

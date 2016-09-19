'use strict';

describe('Controller Tests', function() {

    describe('RadioCheckboxField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockRadioCheckboxField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockRadioCheckboxField = jasmine.createSpy('MockRadioCheckboxField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'RadioCheckboxField': MockRadioCheckboxField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("RadioCheckboxFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:radioCheckboxFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

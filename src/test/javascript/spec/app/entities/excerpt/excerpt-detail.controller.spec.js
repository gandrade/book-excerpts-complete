'use strict';

describe('Controller Tests', function() {

    describe('Excerpt Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockExcerpt, MockUser, MockBook;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockExcerpt = jasmine.createSpy('MockExcerpt');
            MockUser = jasmine.createSpy('MockUser');
            MockBook = jasmine.createSpy('MockBook');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Excerpt': MockExcerpt,
                'User': MockUser,
                'Book': MockBook
            };
            createController = function() {
                $injector.get('$controller')("ExcerptDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'bookexcerptsApp:excerptUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

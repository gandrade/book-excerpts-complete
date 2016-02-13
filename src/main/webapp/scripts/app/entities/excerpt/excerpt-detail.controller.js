'use strict';

angular.module('bookexcerptsApp')
    .controller('ExcerptDetailController', function ($scope, $rootScope, $stateParams, entity, Excerpt, User, Book) {
        $scope.excerpt = entity;
        $scope.load = function (id) {
            Excerpt.get({id: id}, function(result) {
                $scope.excerpt = result;
            });
        };
        var unsubscribe = $rootScope.$on('bookexcerptsApp:excerptUpdate', function(event, result) {
            $scope.excerpt = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });

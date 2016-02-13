'use strict';

angular.module('bookexcerptsApp')
	.controller('ExcerptDeleteController', function($scope, $uibModalInstance, entity, Excerpt) {

        $scope.excerpt = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Excerpt.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });

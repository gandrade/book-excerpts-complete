'use strict';

angular.module('bookexcerptsApp')
	.controller('BookDeleteController', function($scope, $uibModalInstance, entity, Book) {

        $scope.book = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Book.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });

'use strict';

angular.module('bookexcerptsApp').controller('ExcerptDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Excerpt', 'User', 'Book',
        function($scope, $stateParams, $uibModalInstance, entity, Excerpt, User, Book) {

        $scope.excerpt = entity;
        $scope.users = User.query();
        $scope.books = Book.query();
        $scope.load = function(id) {
            Excerpt.get({id : id}, function(result) {
                $scope.excerpt = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('bookexcerptsApp:excerptUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.excerpt.id != null) {
                Excerpt.update($scope.excerpt, onSaveSuccess, onSaveError);
            } else {
                Excerpt.save($scope.excerpt, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreatedDate = {};

        $scope.datePickerForCreatedDate.status = {
            opened: false
        };

        $scope.datePickerForCreatedDateOpen = function($event) {
            $scope.datePickerForCreatedDate.status.opened = true;
        };
}]);

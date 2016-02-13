'use strict';

angular.module('bookexcerptsApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });



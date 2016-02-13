'use strict';

angular.module('bookexcerptsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('author', {
                parent: 'entity',
                url: '/authors',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bookexcerptsApp.author.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/author/authors.html',
                        controller: 'AuthorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('author');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('author.detail', {
                parent: 'entity',
                url: '/author/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bookexcerptsApp.author.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/author/author-detail.html',
                        controller: 'AuthorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('author');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Author', function($stateParams, Author) {
                        return Author.get({id : $stateParams.id});
                    }]
                }
            })
            .state('author.new', {
                parent: 'author',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/author/author-dialog.html',
                        controller: 'AuthorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    picture: null,
                                    pictureContentType: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('author', null, { reload: true });
                    }, function() {
                        $state.go('author');
                    })
                }]
            })
            .state('author.edit', {
                parent: 'author',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/author/author-dialog.html',
                        controller: 'AuthorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Author', function(Author) {
                                return Author.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('author', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('author.delete', {
                parent: 'author',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/author/author-delete-dialog.html',
                        controller: 'AuthorDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Author', function(Author) {
                                return Author.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('author', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });

'use strict';

angular.module('bookexcerptsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('excerpt', {
                parent: 'entity',
                url: '/excerpts',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bookexcerptsApp.excerpt.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/excerpt/excerpts.html',
                        controller: 'ExcerptController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('excerpt');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('excerpt.detail', {
                parent: 'entity',
                url: '/excerpt/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bookexcerptsApp.excerpt.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/excerpt/excerpt-detail.html',
                        controller: 'ExcerptDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('excerpt');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Excerpt', function($stateParams, Excerpt) {
                        return Excerpt.get({id : $stateParams.id});
                    }]
                }
            })
            .state('excerpt.new', {
                parent: 'excerpt',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/excerpt/excerpt-dialog.html',
                        controller: 'ExcerptDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    excerpt: null,
                                    createdDate: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('excerpt', null, { reload: true });
                    }, function() {
                        $state.go('excerpt');
                    })
                }]
            })
            .state('excerpt.edit', {
                parent: 'excerpt',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/excerpt/excerpt-dialog.html',
                        controller: 'ExcerptDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Excerpt', function(Excerpt) {
                                return Excerpt.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('excerpt', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('excerpt.delete', {
                parent: 'excerpt',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/excerpt/excerpt-delete-dialog.html',
                        controller: 'ExcerptDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Excerpt', function(Excerpt) {
                                return Excerpt.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('excerpt', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });

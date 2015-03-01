dependencies = [
  'ngRoute',
  'ngSanitize',
  'ui.bootstrap',
  'mtgSearch.filters',
  'mtgSearch.services',
  'mtgSearch.controllers',
  'mtgSearch.directives',
  'mtgSearch.common',
  'mtgSearch.routeConfig',
  'infinite-scroll'
]

app = angular.module('mtgSearch', dependencies)

angular.module('mtgSearch.routeConfig', ['ngRoute'])
.config ($routeProvider) ->
  $routeProvider
  .when('/', {
      templateUrl: '/assets/partials/search.html'
    })
  .otherwise({redirectTo: '/'})
.config ($locationProvider) ->
  $locationProvider.html5Mode({
    enabled: true,
    requireBase: false
  })

@commonModule = angular.module('mtgSearch.common', [])
@controllersModule = angular.module('mtgSearch.controllers', [])
@servicesModule = angular.module('mtgSearch.services', [])
@modelsModule = angular.module('mtgSearch.models', [])
@directivesModule = angular.module('mtgSearch.directives', [])
@filtersModule = angular.module('mtgSearch.filters', [])
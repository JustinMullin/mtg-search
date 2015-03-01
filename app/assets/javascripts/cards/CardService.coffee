class CardService

  @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
  @defaultConfig = { headers: @headers }

  constructor: (@$log, @$http, @$q) ->
    @$log.debug "constructing CardService"

  findCards: (data) ->
    deferred = @$q.defer()

    @$http.post("/cards", data)
    .success((data, status, headers) =>
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      deferred.reject(data)
    )

    deferred.promise

servicesModule.service('CardService', CardService)
class CardController

  @$inject: ['$log', 'CardService', '$scope']
  constructor: (@$log, @CardService, @scope) ->
    @scope.query = {
      page: 0,
      pageSize: 100,
      name: {value: "", mode: ""}
      text: {value: "", mode: ""}
    }
    @loading = false
    @scope.resultsLayout = 'Table'
    @cards = []
    @maxPage = 0
    @searchOptions = {
      debounce: 250
    }

  findCards: (retainExisting) ->
    @loading = true

    if(!retainExisting)
      @cards = []
      @page = @scope.query.page = 0

    @CardService.findCards(@scope.query)
    .then(
      (data) =>
        newCards = data.cards
        @results = data.results
        @maxPage = data.maxPage
        newCards.map (card) =>
          if(card.gameInfo?)
            if(card.gameInfo.manaCost?)
              card.gameInfo.manaCost = @format(card.gameInfo.manaCost)
            if(card.gameInfo.text?)
              card.gameInfo.text = @format(card.gameInfo.text)
        @cards = @cards.concat newCards...
        @loading = false
    ,
    (error) =>
      @$log.error "Unable to get cards: #{error}"
    )

  nextPage: () ->
    if(!@loading && @scope.query.page < @maxPage)
      @scope.query.page += 1
      @findCards(true)

  format: (text) ->
    blocks = text.split(/\n/)
    blocks = blocks.map (block) =>
      "<p>" + @addManaIcons(@addTapIcons(block)) + "</p>"
    return blocks.join("")

  addTapIcons: (text) ->
    return text.replace(/\{([TQC])\}/g, (match...) -> "<img class='icon' src='//mtgimage.com/symbol/other/"+match[1].replace(/\W/g, "").toLowerCase()+"/64.gif'/>")

  addManaIcons: (text) ->
    return text.replace(/\{([^}]+)\}/g, (match...) -> "<img class='icon' src='//mtgimage.com/symbol/mana/"+match[1].replace(/\W/g, "").toLowerCase()+"/64.gif'/>")

controllersModule.controller('CardController', CardController)
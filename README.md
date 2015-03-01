A simple Magic the Gathering card search.  Light on features at the moment, mostly a proof of concept.

Scala/Play/MongoDB backend with a Coffeescript/AngularJS/Bootstrap frontend.

To run locally, 'activator run', then hit localhost:9000 in your browser when Play has finished setting up.
Card data is initialized by hitting /loadCards in your browser (subsequent GETs at that URL will clean the DB and reload)
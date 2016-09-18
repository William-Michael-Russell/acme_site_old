(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('Text_inputsSearch', Text_inputsSearch);

    Text_inputsSearch.$inject = ['$resource'];

    function Text_inputsSearch($resource) {
        var resourceUrl =  'api/_search/text-inputs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

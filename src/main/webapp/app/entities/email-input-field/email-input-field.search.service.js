(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('EmailInputFieldSearch', EmailInputFieldSearch);

    EmailInputFieldSearch.$inject = ['$resource'];

    function EmailInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/email-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

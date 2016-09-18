(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('PasswordInputFieldSearch', PasswordInputFieldSearch);

    PasswordInputFieldSearch.$inject = ['$resource'];

    function PasswordInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/password-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

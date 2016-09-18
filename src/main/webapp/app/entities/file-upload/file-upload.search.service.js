(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('FileUploadSearch', FileUploadSearch);

    FileUploadSearch.$inject = ['$resource'];

    function FileUploadSearch($resource) {
        var resourceUrl =  'api/_search/file-uploads/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

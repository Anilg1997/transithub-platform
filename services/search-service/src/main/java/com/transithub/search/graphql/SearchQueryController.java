package com.transithub.search.graphql;
import com.transithub.search.service.SearchService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class SearchQueryController {
    private final SearchService service;
    public SearchQueryController(SearchService service) { this.service = service; }

    @QueryMapping
    public List<SearchService.AutocompleteResult> autocomplete(@Argument String query, @Argument String type) {
        return service.autocomplete(query, type);
    }

    @QueryMapping
    public List<SearchService.PriceCalendarDay> priceCalendar(@Argument String from, @Argument String to,
            @Argument String month, @Argument String mode) {
        return service.priceCalendar(from, to, month, mode);
    }

    @QueryMapping
    public List<SearchService.PopularRoute> popularRoutes(@Argument String mode, @Argument Integer limit) {
        return service.popularRoutes(mode, limit);
    }
}

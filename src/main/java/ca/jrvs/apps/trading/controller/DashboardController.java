package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService){
        this.dashboardService = dashboardService;
    }

    @PutMapping(path = "/portfolio/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void showPortfolioView(@PathVariable Integer traderId){
        try {
            dashboardService.getProfileViewByTraderId(traderId);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }

    }

    @PutMapping(path = "/profile/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void showTraderAccountView(@PathVariable Integer traderId){
        try {
            dashboardService.getTraderAccount(traderId);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

}

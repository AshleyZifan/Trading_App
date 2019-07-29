package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import ca.jrvs.apps.trading.service.FundTransferService;
import ca.jrvs.apps.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;

@Controller
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PutMapping(path = "/marketOrder")
    @ResponseStatus(HttpStatus.OK)
    public void marketOrder(@RequestBody MarketOrderDto orderDto) {
        try {
            orderService.executeMarketOrder(orderDto);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }
}

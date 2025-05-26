package org.example.bookstore.controller;

import org.example.bookstore.entity.Order;
import org.example.bookstore.entity.OrderedBook;
import org.example.bookstore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrdersController {


        private final OrderService orderService;

    public AdminOrdersController(OrderService orderService ) {
        this.orderService = orderService;
    }

    @GetMapping
    public String ordersPage(Model model) {
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "admin-orders";
    }

    @GetMapping("/{id}/details")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderedBook> orderedBooks = orderService.getOrderedBooks(id);

        model.addAttribute("order", order);
        model.addAttribute("orderedBooks", orderedBooks);
        return "fragments/order-details :: details";
    }

        @PostMapping("/{id}/update")
        public String updateOrder(
                @PathVariable Long id,
                @RequestParam String status,
                @RequestParam String trackingNumber,   RedirectAttributes redirectAttributes
        ) {
            orderService.updateOrder(id, status, trackingNumber);
            redirectAttributes.addFlashAttribute("updatedOrderId", id);
            return "redirect:/admin/orders";
        }

    @GetMapping("/search")
    public String searchOrderById(@RequestParam Long orderId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrderById(orderId);
            redirectAttributes.addFlashAttribute("updatedOrderId", orderId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("notFound", true);
            return "redirect:/admin/orders";
        }

        return "redirect:/admin/orders";
    }

}

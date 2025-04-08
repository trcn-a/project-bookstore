package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.OrderService;
import org.example.bookstore.Services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }

        double totalSum = cartService.getTotalSumForCart(user);
        model.addAttribute("totalSum", totalSum);

        List<CartBook> cartBooks = cartService.getCartContents(user);

        if (cartBooks == null || cartBooks.isEmpty()) {
           return "redirect:/cart";
        }
        model.addAttribute("orderedBooks", cartBooks);

        return "order";
    }

    @PostMapping("/create")
    public String createOrder(@SessionAttribute(value = "user") User user,
                              @RequestParam String phoneNumber,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String city,
                              @RequestParam String postOfficeNumber,
                              RedirectAttributes redirectAttributes) {

        List<CartBook> cartBooks = cartService.getCartContents(user);


        try {
            Order order = orderService.createOrder(user.getId(), phoneNumber, firstName, lastName,
                    city, postOfficeNumber);
            return "redirect:/order/success/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/checkout";
        }
    }


    @GetMapping("/success/{orderId}")
    public String showOrderSuccess(@PathVariable Long orderId, @SessionAttribute(value = "user", required = false) User user, Model model) {

        try {

            Order order = orderService.getOrderById(orderId);

            if (user==null || !order.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "У вас немає доступу до цього замовлення.");
                return "error";
            }

            model.addAttribute("order", order);
            return "order-success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
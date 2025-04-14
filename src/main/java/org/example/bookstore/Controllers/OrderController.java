package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.OrderService;
import org.example.bookstore.Services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контролер для обробки процесу оформлення замовлень.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final CartService cartService;

    /**
     * Конструктор контролера, який приймає сервіси для замовлень та кошика через інʼєкцію залежностей.
     *
     * @param orderService сервіс для роботи з замовленнями
     * @param cartService  сервіс для роботи з кошиком
     */
    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    /**
     * Відображає сторінку оформлення замовлення.
     *
     * @param user  авторизований користувач, отриманий з сесії
     * @param model модель для передачі атрибутів в представлення
     * @return сторінка оформлення замовлення або перенаправлення на сторінку входу
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user == null) {
            logger.warn("User is not authenticated. Redirecting to login.");
            return "redirect:/user/login";
        }

        double totalSum = cartService.getTotalSumForCart(user);
        model.addAttribute("totalSum", totalSum);

        List<CartBook> cartBooks = cartService.getCartContents(user);

        if (cartBooks == null || cartBooks.isEmpty()) {
            logger.warn("Cart is empty for user ID: {}", user.getId());
            return "redirect:/cart";
        }

        model.addAttribute("orderedBooks", cartBooks);

        logger.debug("Displaying checkout page for user ID: {}", user.getId());
        return "order";
    }

    /**
     * Створює нове замовлення та перенаправляє на сторінку успішного оформлення.
     *
     * @param user               авторизований користувач, отриманий з сесії
     * @param phoneNumber        номер телефону користувача
     * @param firstName          імʼя користувача
     * @param lastName           прізвище користувача
     * @param city               місто користувача
     * @param postOfficeNumber   номер відділення пошти
     * @param redirectAttributes атрибути для перенаправлення, зокрема повідомлення про помилку
     * @return перенаправлення на сторінку успішного оформлення замовлення
     * або на сторінку оформлення з повідомленням про помилку
     */
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
            logger.info("Order created successfully. Order ID: {}, User ID: {}", order.getId(), user.getId());
            return "redirect:/order/success/" + order.getId();
        } catch (Exception e) {
            logger.error("Error creating order for user ID: {}. Reason: {}", user.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    /**
     * Відображає сторінку успішного оформлення замовлення.
     *
     * @param orderId ідентифікатор замовлення
     * @param user    авторизований користувач, отриманий з сесії
     * @param model   модель для передачі атрибутів в представлення
     * @return сторінка успіху оформлення замовлення або сторінка з помилкою,
     * якщо користувач не має доступу до замовлення
     */
    @GetMapping("/success/{orderId}")
    public String showOrderSuccess(@PathVariable Long orderId,
                                   @SessionAttribute(value = "user", required = false) User user,
                                   Model model) {

        try {
            Order order = orderService.getOrderById(orderId);

            if (user == null || !order.getUser().getId().equals(user.getId())) {
                logger.error("Unauthorized access attempt to order ID: {}", orderId);
                model.addAttribute("error", "У вас немає доступу до цього замовлення.");
                return "error";
            }

            model.addAttribute("order", order);
            logger.info("User ID: {} accessed order ID: {}", user.getId(), orderId);
            return "order-success";
        } catch (RuntimeException e) {
            logger.error("Failed to retrieve order ID: {}. Reason: {}", orderId, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}

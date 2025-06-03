package org.example.bookstore.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.config.CustomUserDetails;
import org.example.bookstore.entity.CartBook;
import org.example.bookstore.entity.Order;
import org.example.bookstore.entity.OrderedBook;
import org.example.bookstore.entity.User;
import org.example.bookstore.service.OrderService;
import org.example.bookstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Контролер для обробки процесу оформлення замовлень.
 */
@Controller
@RequestMapping("/")
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
     * @param model модель для передачі атрибутів в представлення
     * @return сторінка оформлення замовлення або перенаправлення на сторінку входу
     */
    @GetMapping("/order/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
User user = customUserDetails.getUser();
        double totalSum = cartService.getTotalSumForCart(user);
        model.addAttribute("totalSum", totalSum);

        List<CartBook> cartBooks = cartService.getCartContents(user);

        if (cartBooks == null || cartBooks.isEmpty()) {
            logger.warn("Cart is empty for user ID: {}", user.getId());
            return "redirect:/cart";
        }

        model.addAttribute("orderedBooks", cartBooks);
        model.addAttribute("user", user);

        logger.debug("Displaying checkout page for user ID: {}", user.getId());
        return "order";
    }

    /**
     * Створює нове замовлення та перенаправляє на сторінку успішного оформлення.
     *
     * @param phoneNumber        номер телефону користувача
     * @param firstName          імʼя користувача
     * @param lastName           прізвище користувача
     * @param city               місто користувача
     * @param postOfficeNumber   номер відділення пошти
     * @param redirectAttributes атрибути для перенаправлення, зокрема повідомлення про помилку
     * @return перенаправлення на сторінку успішного оформлення замовлення
     * або на сторінку оформлення з повідомленням про помилку
     */
    @PostMapping("/order/create")
    public String createOrder(    @AuthenticationPrincipal CustomUserDetails customUserDetails,
                              @RequestParam String phoneNumber,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String city,
                              @RequestParam Integer postOfficeNumber,
                              RedirectAttributes redirectAttributes) {
        User user = customUserDetails.getUser();

        List<CartBook> cartBooks = cartService.getCartContents(user);

        try {
            Order order = orderService.createOrder(user.getId(), phoneNumber, firstName, lastName,
                    city, postOfficeNumber);
            logger.info("Order created successfully. Order ID: {}, User ID: {}", order.getId(), user.getId());
            return "redirect:/order/success/" + order.getId();
        } catch (Exception e) {
            UUID errorId = UUID.randomUUID();
            logger.error("Order creation error [{}] for user ID {}: {}", errorId, user.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Сталася помилка при створенні замовлення. Код: " + errorId);
            return "redirect:/order/checkout";
        }
    }

    /**
     * Відображає сторінку успішного оформлення замовлення.
     *
     * @param orderId ідентифікатор замовлення
     * @param model   модель для передачі атрибутів в представлення
     * @return сторінка успіху оформлення замовлення або сторінка з помилкою,
     * якщо користувач не має доступу до замовлення
     */
    @GetMapping("/order/success/{orderId}")
    public String showOrderSuccess(@PathVariable Long orderId,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   Model model) {
        User user = customUserDetails.getUser();
        try {

            Order order = orderService.getOrderById(orderId);

            if (user == null || !order.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to order ID: " + orderId);
            }

            model.addAttribute("order", order);
            logger.info("User ID: {} accessed order ID: {}", user.getId(), orderId);
            return "order-success";

        } catch (Exception ex) {
            throw new RuntimeException("Error loading order ID: " + orderId, ex);
        }
    }

    /**
     * Показує історію замовлень користувача.
     *
     * @param model модель для передачі даних у шаблон
     * @return сторінка з історією замовлень користувача або редирект на сторінку входу
     */
    @GetMapping("/orders")
    public String showOrderHistory(    @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        User user = customUserDetails.getUser();
        List<Order> orders = orderService.getUserOrders(user.getId());
        model.addAttribute("activePage", "orders");
        model.addAttribute("orders", orders);

        logger.info("Displaying order history for user: {}", user.getEmail());

        return "order-history";
    }

    @GetMapping("/orders/{id}/details")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderedBook> orderedBooks = orderService.getOrderedBooks(id);

        model.addAttribute("order", order);
        model.addAttribute("orderedBooks", orderedBooks);
        return "fragments/order-details :: details";
    }

    /**
     * Скасовує замовлення користувача.
     *
     * @param orderId ідентифікатор замовлення
     * @param session HTTP-сесія користувача
     * @param redirectAttributes атрибути для редиректу з повідомленням
     * @return редирект на сторінку історії замовлень
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,     @AuthenticationPrincipal CustomUserDetails customUserDetails,  HttpSession session, RedirectAttributes redirectAttributes) {
        User user = customUserDetails.getUser();
        try {
            orderService.cancelOrder(orderId, user.getId());
            logger.info("Order {} canceled for user: {}", orderId, user.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Замовлення успішно скасовано");
            redirectAttributes.addFlashAttribute("orderId", orderId);
        } catch (RuntimeException e) {
            logger.error("Failed to cancel order {} for user: {}: {}", orderId, user.getEmail(), e.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders";
    }
}

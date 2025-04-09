package doanjava.webbannuochoa.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import doanjava.webbannuochoa.Service.CartService;
import doanjava.webbannuochoa.Service.OrderService;
import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.models.CartItem;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.type.CreatePaymentLinkRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final PayOS payOS;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final CartService cartService;
    @Autowired
    private OrderService orderService;

    public OrderController(PayOS payOS, ProductService productService, CartService cartService) {
        this.payOS = payOS;
        this.productService = productService;
        this.cartService = cartService;
    }
}





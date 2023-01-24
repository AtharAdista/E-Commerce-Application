package com.app.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entites.Cart;
import com.app.entites.CartItem;
import com.app.entites.Order;
import com.app.entites.OrderItem;
import com.app.entites.Payment;
import com.app.entites.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.OrderDTO;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.OrderItemRepo;
import com.app.repositories.OrderRepo;
import com.app.repositories.PaymentRepo;
import com.app.repositories.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	public UserRepo userRepo;

	@Autowired
	public CartRepo cartRepo;

	@Autowired
	public OrderRepo orderRepo;
	
	@Autowired
	private PaymentRepo paymentRepo;
	
	@Autowired
	public OrderItemRepo orderItemRepo;

	@Autowired
	public CartItemRepo cartItemRepo;

	@Autowired
	public UserService userService;

	@Autowired
	public CartService cartService;

	@Autowired
	public ModelMapper modelMapper;

	@Override
	public OrderDTO placeOrder(String emailId, Long cartId, OrderDTO orderDTO) {

		Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);
		
		User user = userRepo.findByEmail(emailId);
		
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}

		Order order = new Order();

//		order.setUser(user);
		order.setEmail(emailId);
		order.setOrderDate(LocalDate.now());

		Order savedOrder = orderRepo.save(order);
		
		List<CartItem> cartItems = cart.getCartItems();
		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();

			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);
			
			orderItems.add(orderItem);
		}
		
		orderItemRepo.saveAll(orderItems);

		cart.getCartItems().removeAll(cartItems);
		
		savedOrder.setTotalAmount(cart.getTotalPrice());
		savedOrder.setOrderStatus("Order Accepted !");

		Payment payment = new Payment();
		payment.setOrder(savedOrder);
		payment.setPaymentMethod(orderDTO.getPaymentMethod());
		
		payment = paymentRepo.save(payment);
		
//		savedOrder.setPayment(payment);
		
//		cart.setTotalPrice(0.0);

//		cartRepo.save(cart);

		return modelMapper.map(savedOrder, OrderDTO.class);
	}

	@Override
	public OrderDTO getOrder(String emailId, Long orderId) {
		Order order = orderRepo.findOrderByEmailAndOrderId(emailId, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

//		UserDTO user = userService.getUserById(order.getUser().getUserId());

		OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
		
//		List<CartItem> cartItems = order.getOrderedProducts();
//		List<CartItemDTO> orderedItems = new ArrayList<>();
//		
//		for (CartItem cartItem : cartItems) {
//			CartItem orderItem = new CartItem();
//
//			orderItem.setProduct(cartItem.getProduct());
//			orderItem.setQuantity(cartItem.getQuantity());
//			orderItem.setDiscount(cartItem.getDiscount());
//			orderItem.setProductPrice(cartItem.getProductPrice());
//			orderItem.setOrder(order);
//
//			orderedItems.add(modelMapper.map(orderItem, CartItemDTO.class));
//
//		}
		
//		orderDTO.setUser(user);
		
		return orderDTO;
	}

	@Override
	public List<OrderDTO> getAllOrders(String emailId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteOrder(String email, Long orderId) {
		// TODO Auto-generated method stub
		return null;
	}

}

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import static org.mockito.Mockito.*;

/**
 * Тестирование сервиса {@link ShoppingService}
 */
public class ShoppingServiceTest {

	private final ProductDao mockProductDao = mock(ProductDao.class);

	private final ShoppingService shoppingService = new ShoppingServiceImpl(mockProductDao);

	private final static String FIRST_NAME_PRODUCT = "Product";
	private final static String SECOND_NAME_PRODUCT = "Second Product";

	/**
	 * Тест покупки отрицательного количества товара
	 */
	@Test
	public void testMinusCountProduct() throws BuyException {
		Cart cart = new Cart(null);
		Product product = new Product(FIRST_NAME_PRODUCT, 1);
		cart.add(product, -1);

		shoppingService.buy(cart);
		Assertions.assertEquals(1, product.getCount());
		Mockito.verify(mockProductDao, never()).save(product);
	}

	/**
	 * Тест на получение корзины текущего самого покупателя
	 */
	@Test
	public void testSameCartCustomer() {
		Customer customer = new Customer(1, "+78005553535");
		Cart cart = shoppingService.getCart(customer);
		Product product = new Product(FIRST_NAME_PRODUCT, 10);
		cart.add(product, 1);
		Assertions.assertEquals(cart.getProducts(), shoppingService.getCart(customer).getProducts());
	}

	/**
	 * Тест покупки, если корзина пустая
	 */
	@Test
	public void testBuyEmptyCart() throws BuyException {
		Cart cart = new Cart(null);
		Assertions.assertFalse(shoppingService.buy(cart));
		Mockito.verify(mockProductDao, never()).save(any(Product.class));
	}

	/**
	 * Тест на успешную покупку единственного продукта
	 *
	 * Тест не должен падать, однако это делает при условии, если будет куплен 1 продукт
	 */
	@Test
	public void testBuyOneProduct() throws BuyException {
		Cart cart = new Cart(null);
		Product product = new Product(FIRST_NAME_PRODUCT, 1);
		cart.add(product, 1);
		Assertions.assertTrue(shoppingService.buy(cart));
		Mockito.verify(mockProductDao, times(1)).save(product);
		Mockito.verify(product, times(1)).subtractCount(product.getCount());
	}

	/**
	 * Тест покупки из корзины нескольких одинаковых продуктов
	 */
	@Test
	public void testBuyMultipleCountProduct() throws BuyException {
		Cart cart = new Cart(null);
		Product product = new Product(FIRST_NAME_PRODUCT, 4);
		cart.add(product, 2);
		Assertions.assertTrue(shoppingService.buy(cart));
		Assertions.assertEquals(2, cart.getProducts().get(product));
	}

	/**
	 * Тест на успешную покупку разных товаров
	 * Если count у firstProduct и secondProduct будет столько же, сколько я добавляю, то выдаст ошибку
	 */
	@Test
	public void testBuyVariousProduct() throws BuyException {
		Cart cart = new Cart(null);

		Product firstProduct = new Product(FIRST_NAME_PRODUCT, 10);
		Product secondProduct = new Product(SECOND_NAME_PRODUCT, 10);

		cart.add(firstProduct, 5);
		cart.add(secondProduct, 4);

		Assertions.assertTrue(shoppingService.buy(cart));
		Mockito.verify(mockProductDao, times(1)).save(firstProduct);
		Mockito.verify(mockProductDao, times(1)).save(secondProduct);
	}

	/**
	 * Тест на покупку большего количества продуктов,чем есть возможность
	 */
	@Test
	public void testExceptionBuyMultiplyVariousProducts() throws BuyException {
		Cart cart = new Cart(null);
		Cart secondCart = new Cart(null);
		Product product = new Product(FIRST_NAME_PRODUCT, 10);

		cart.add(product, 9);
		secondCart.add(product, 9);
		shoppingService.buy(secondCart);

		BuyException buyException = Assertions.assertThrows(BuyException.class,
				() -> shoppingService.buy(cart));
		Assertions.assertEquals(
				"В наличии нет необходимого количества товара " + FIRST_NAME_PRODUCT,
				buyException.getMessage());

	}


	/**
	 * Тестирование получения списка товаров
	 * Поскольку метод просто-напросто вызывает сам метод из дао, то в таком случае
	 * нам нет никакого смысла его тестировать
	 */
	@Test
	public void getAllProductsTest() {

	}

	/**
	 * Тест на получение товара по названию
	 * Поскольку метод просто-напросто вызывает сам метод из дао, то в таком случае
	 * нам нет никакого смысла его тестировать
	 */
	@Test
	public void getProductByNameTest() {

	}
}
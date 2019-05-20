package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand command;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SuggestionService suggestionService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SystemContext systemContext;
    @Mock
    private Reservation reservation;
    @Mock
    private Product product;

    @Before
    public void initialize() {
        command = new AddProductCommand(Id.generate(), new Id("10"), 1);
        Client client = new Client();
        when(product.getProductType()).thenReturn(ProductType.DRUG);
        when(product.getPrice()).thenReturn(new Money(100));
        when(product.getName()).thenReturn("DummyName");
        when(product.isAvailable()).thenReturn(true);

        ClientData clientData = new ClientData(Id.generate(), "ClientData1");
        when(reservation.getClientData()).thenReturn(clientData);
        when(reservation.getStatus()).thenReturn(Reservation.ReservationStatus.OPENED);
        when(reservation.getCreateDate()).thenReturn(new Date());
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);
        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                clientRepository, systemContext);
    }

    @Test
    public void reservationRepositoryLoadShouldBeCalledOnce() {
        addProductCommandHandler.handle(command);
        verify(reservationRepository, times(1)).load(any());
    }

    @Test
    public void productRepositoryLoadShouldBeCalledOnce() {
        addProductCommandHandler.handle(command);
        verify(productRepository, times(1)).load(any(Id.class));
    }

    @Test
    public void reservationRepositorySaveShouldBeCalledOnce() {
        addProductCommandHandler.handle(command);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void productRepositoryLoadShouldReturnProperProduct() {
        addProductCommandHandler.handle(command);
        Assert.assertThat(productRepository.load(command.getProductId()).getName(), is(equalTo(product.getName())));
        Assert.assertThat(productRepository.load(command.getProductId()).getPrice(), is(equalTo(product.getPrice())));
        Assert.assertThat(productRepository.load(command.getProductId()).getProductType(), is(equalTo(product.getProductType())));
    }

    @Test
    public void reservationRepositoryLoadShouldReturnProperReservation() {
        addProductCommandHandler.handle(command);
        Assert.assertThat(reservationRepository.load(command.getOrderId()).getStatus(), is(equalTo(reservation.getStatus())));
        Assert.assertThat(reservationRepository.load(command.getOrderId()).getClientData(), is(equalTo(reservation.getClientData())));
        Assert.assertThat(reservationRepository.load(command.getOrderId()).getCreateDate(), is(equalTo(reservation.getCreateDate())));
    }

}

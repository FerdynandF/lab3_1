package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Mock
    private TaxPolicy taxPolicy;
    @Mock
    private ProductData product;

    private ClientData clientData;
    private InvoiceRequest invoiceRequest;
    private InvoiceFactory invoiceFactory;
    private BookKeeper keeper;

    @Before
    public void initialize() {
        clientData = new ClientData(Id.generate(), insignificantName());
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceFactory = new InvoiceFactory();
        keeper = new BookKeeper(invoiceFactory);
    }

    @Test
    public void invoiceRequestWithEmptyItemShouldReturnEmptyInvoice() {
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().isEmpty(), is(equalTo(invoiceRequest.getItems().isEmpty())));
    }

    @Test
    public void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {

        when(product.getType()).thenReturn(ProductType.DRUG);
        RequestItem requestItem = new RequestItem(product, 1, new Money(100));
        invoiceRequest.add(requestItem);

        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));
        Invoice returnInvoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(returnInvoice.getItems().size(), is(1));
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldCallCalculateTaxTwice() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));

        when(product.getType()).thenReturn(ProductType.DRUG);
        RequestItem requestItem = new RequestItem(product, 1, new Money(100));
        invoiceRequest.add(requestItem);

        requestItem = new RequestItem(product, 1, new Money(200));
        invoiceRequest.add(requestItem);

        keeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(any(), any());
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldCallProductGetTypeTwice() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));

        when(product.getType()).thenReturn(ProductType.DRUG);
        RequestItem requestItem = new RequestItem(product, 1, new Money(100));
        invoiceRequest.add(requestItem);

        requestItem = new RequestItem(product, 1, new Money(200));
        invoiceRequest.add(requestItem);

        keeper.issuance(invoiceRequest, taxPolicy);
        verify(product, times(2)).getType();
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldCallGetTotalCostTwice() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(new Money(10), "10%"));

        when(product.getType()).thenReturn(ProductType.DRUG);
        RequestItem requestItem = mock(RequestItem.class);
        when(requestItem.getTotalCost()).thenReturn(new Money(100));
        when(requestItem.getProductData()).thenReturn(product);
        when(requestItem.getQuantity()).thenReturn(1);

        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        keeper.issuance(invoiceRequest, taxPolicy);
        verify(requestItem, times(2)).getTotalCost();
    }

    private String insignificantName() {
        return "Nowak";
    }
}

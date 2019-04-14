package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Test
    public void invoiceRequestWithEmptyItemShouldReturnEmptyInvoice() {
        ClientData clientData = new ClientData(Id.generate(), insignificantName());
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        BookKeeper keeper = new BookKeeper(invoiceFactory);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().isEmpty(), is(equalTo(invoiceRequest.getItems().isEmpty())));
    }

    private String insignificantName() {
        return "Nowak";
    }
}

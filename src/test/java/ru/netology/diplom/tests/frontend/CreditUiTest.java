package ru.netology.diplom.tests.frontend;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.netology.diplom.data.DataHelper;
import ru.netology.diplom.data.SQLHelper;
import ru.netology.diplom.page.CardPage;
import ru.netology.diplom.page.FormPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreditUiTest {
    private static DataHelper.Data data;

    private static CardPage card;
    private static FormPage form;
    private static List<SQLHelper.PaymentEntity> payments;
    private static List<SQLHelper.CreditRequestEntity> credits;
    private static List<SQLHelper.OrderEntity> orders;

    @BeforeEach
    public void setupMethod() {
        open("http://localhost:8080/");
        card = new CardPage();
    }

    @AfterEach
    public void setDownMethod() {
        SQLHelper.setDown();
    }

    @Test
    public void shouldHappyPath() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form = card.clickCreditButton();
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
    }
}

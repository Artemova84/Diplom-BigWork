package ru.netology.diplom.tests.frontend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.diplom.data.DataHelper;
import ru.netology.diplom.data.SQLHelper;
import ru.netology.diplom.page.CardPage;
import ru.netology.diplom.page.FormPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class PaymentUiTest {
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

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertEquals(card.getAmount() * 100, payments.get(0).getAmount());
        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertEquals(card.getAmount() * 100, payments.get(0).getAmount());
        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        card.clickPayButton();
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
    }

    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        data = DataHelper.getValidApprovedCard();
        form = card.clickPayButton();
        form.insertingValueInForm("", data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue("", data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldSuccessfulWithStartEndSpacebarInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = " " + data.getNumber() + " ";
        var matchesNumber = data.getNumber();

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();
    }

    @Test
    public void shouldVisibleNotificationWith16DigitsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith16RandomNumerals();
        var matchesNumber = number;

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsInvalidValue();
    }

    @Test
    public void shouldUnsuccessfulWith10DigitsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith10Numerals();
        var matchesNumber = number;

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        data = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), year, data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), matchesYear, data.getHolder(), data.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        data = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), year, data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), matchesYear, data.getHolder(), data.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), matchesHolder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), matchesHolder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), matchesHolder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), matchesCvc);
        form.assertCvcFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Numerals();
        var matchesCvc = cvc;

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), matchesCvc);
        form.assertCvcFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), matchesCvc);
        form.assertCvcFieldIsEmptyValue();
    }
}

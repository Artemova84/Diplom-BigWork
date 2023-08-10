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
    private static DataHelper.Data cardData;
    private static CardPage card;
    private static FormPage form;
    private static List<SQLHelper.PaymentEntity> payments;
    private static List<SQLHelper.CreditRequestEntity> credits;
    private static List<SQLHelper.OrderEntity> orders;

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    @AfterEach
    public void cleanDataBase() {
        SQLHelper.cleanDatabase();
    }

    @Test
    public void shouldHappyPath() {
        cardData = DataHelper.getValidApprovedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
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
        cardData = DataHelper.getValidDeclinedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
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
        cardData = DataHelper.getValidApprovedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        card.clickPayButton();
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
    }

    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        cardData = DataHelper.getValidApprovedCard();
        form = card.clickPayButton();
        form.insertingValueInForm("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldSuccessfulWithStartEndSpacebarInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = " " + cardData.getNumber() + " ";
        var matchesNumber = cardData.getNumber();

        form = card.clickPayButton();
        form.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertBuyOperationIsSuccessful();
    }

    @Test
    public void shouldVisibleNotificationWith16DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith16RandomNumerals();
        var matchesNumber = number;

        form = card.clickPayButton();
        form.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertNumberFieldIsInvalidValue();
    }

    @Test
    public void shouldUnsuccessfulWith10DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith10Numerals();
        var matchesNumber = number;

        form = card.clickPayButton();
        form.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertBuyOperationWithErrorNotification();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        form = card.clickPayButton();
        form.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldAutoDeletingStartEndSpacebarInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "-" + cardData.getHolder() + "-";
        var matchesHolder = cardData.getHolder();

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        form.assertBuyOperationIsSuccessful();
    }

    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        form.assertCvcFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Numerals();
        var matchesCvc = cvc;

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        form.assertCvcFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        form = card.clickPayButton();
        form.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        form.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        form.assertCvcFieldIsEmptyValue();
    }
}

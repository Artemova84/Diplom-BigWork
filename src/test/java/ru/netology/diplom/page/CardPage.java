package ru.netology.diplom.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class CardPage {
    private static final SelenideElement Head = $x("//div[@id='root']/div/h2");;
    private static final SelenideElement Card = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private static final SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");;
    private static final SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");;
    private static final SelenideElement formHead = $x("//form//preceding-sibling::h3");
    private static final SelenideElement form = $x("//form");
    private static final SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private static final SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");

    public CardPage() {
        Head.should(Condition.visible, Condition.text("Путешествие дня"));
        Card.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        formHead.should(Condition.hidden);
        form.should(Condition.hidden);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public FormPage clickPayButton() {
        payButton.click();
        formHead.should(Condition.visible, Condition.text("Оплата по карте"));
        return new FormPage();
    }

    public FormPage clickCreditButton() {
        creditButton.click();
        formHead.should(Condition.visible, Condition.text("Кредит по данным карты"));
        return new FormPage();
    }

    public int getAmount() {
        var str = Card.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.valueOf(str[1] + str[2]);
    }
}

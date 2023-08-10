package ru.netology.diplom.tests.backend;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.diplom.data.DataHelper;
import ru.netology.diplom.data.SQLHelper;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;


public class CreditApiTest {
    private static DataHelper.Data cardData;
    private static final Gson gson = new Gson();
    private static final RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static final String creditUrl = "/credit";
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
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(1, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        Assertions.assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        Assertions.assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        cardData = DataHelper.getValidDeclinedCard();
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(1, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        Assertions.assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        Assertions.assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldStatus400WithEmptyBody() {
        cardData = DataHelper.getValidApprovedCard();
        given().spec(spec)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyNumber() {
        cardData = new DataHelper.Data(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyMonth() {
        cardData = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyYear() {
        cardData = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyHolder() {
        cardData = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyCvc() {
        cardData = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateValidHolder(), null);
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }
}

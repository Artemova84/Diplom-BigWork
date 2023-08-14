package ru.netology.diplom.tests.backend;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.diplom.data.DataHelper;
import ru.netology.diplom.data.SQLHelper;

import java.util.List;
import static io.restassured.RestAssured.given;

public class PaymentApiTest {
    private static DataHelper.Data data;
    private static Gson gson = new Gson();
    private static RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static String paymentUrl = "/payment";
    private static List<SQLHelper.PaymentEntity> payments;
    private static List<SQLHelper.CreditRequestEntity> credits;
    private static List<SQLHelper.OrderEntity> orders;

    @AfterEach
    public void setDownMethod() {
        SQLHelper.setDown();
    }

    @Test
    public void shouldHappyPath() {
        data = DataHelper.getValidApprovedCard();
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldStatus400WithEmptyBody() {
        data = DataHelper.getValidApprovedCard();
        given().spec(spec)
                .when().post(paymentUrl)
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
        data = new DataHelper.Data(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
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
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
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
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
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
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
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
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateValidHolder(), null);
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }
}



package ru.netology.diplom.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Faker fakerWithCyrillicLocale = new Faker(new Locale("ru", "RU"));

    @Value
    public static class Data {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;
    }

    public static Data getValidApprovedCard() {
        return new Data(getNumberByStatus("approved"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static Data getValidDeclinedCard() {
        return new Data(getNumberByStatus("declined"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static String getNumberByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return "4444 4444 4444 4441";
        } else if (status.equalsIgnoreCase("DECLINED")) {
            return "4444 4444 4444 4442";
        }
        return null;
    }

    public static String generateInvalidCardNumberWith16RandomNumerals() {
        return faker.numerify("4444 #### #### ####");
    }

    public static String generateValidCardNumberWith10Numerals() {
        return faker.numerify("4444 #### ##");
    }

    public static String generateInvalidCardNumberWithRandomSymbols() {
        return faker.letterify("???? ???? ???? ????");
    }

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateMonthWithRandomSymbols() {
        return faker.letterify("??");
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidHolder() {
        return faker.name().fullName().toUpperCase();
    }

    public static String generateInvalidHolderWithCyrillicSymbols() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + " "
                + fakerWithCyrillicLocale.name().lastName().toUpperCase();
    }

    public static String generateHolderWithInvalidSymbols() {
        return faker.numerify("#### #### #### ####");
    }

    public static String generateValidCVC() {
        return faker.numerify("###");
    }

    public static String generateInvalidCVCWithRandomSymbols() {
        return faker.letterify("???");
    }

    public static String generateInvalidCVCWith2Numerals() {
        return faker.numerify("##");
    }
}

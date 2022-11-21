package org.meisl.vereinsmelder.data.entity;

import java.util.List;
import java.util.Optional;

public class CompetitionCategory {

    public static CompetitionCategory HERREN = new CompetitionCategory("Herren");
    public static CompetitionCategory DAMEN = new CompetitionCategory("Damen");
    public static CompetitionCategory SENIOREN_HERREN_50 = new CompetitionCategory("Senioren Ü50");
    public static CompetitionCategory SENIOREN_HERREN_60 = new CompetitionCategory("Senioren Ü60");
    public static CompetitionCategory SENIORINNEN_50 = new CompetitionCategory("Seniorinnen Ü50");
    public static CompetitionCategory SENIORINNEN_60 = new CompetitionCategory("Seniorinnen Ü60");
    public static CompetitionCategory MIXED = new CompetitionCategory("Mixed");
    public static CompetitionCategory U14 = new CompetitionCategory("U14");
    public static CompetitionCategory U16 = new CompetitionCategory("U16");
    public static CompetitionCategory U19 = new CompetitionCategory("U19");
    public static CompetitionCategory U23 = new CompetitionCategory("U23");

    public static List<CompetitionCategory> getAllCategories() {
        return List.of(HERREN, DAMEN, SENIOREN_HERREN_50, SENIOREN_HERREN_60, SENIORINNEN_50, SENIORINNEN_60,
                MIXED, U14, U16, U19, U23);
    }

    public static CompetitionCategory get(String name) {
        return getAllCategories().stream().filter(x -> x.name == name).findFirst()
                .orElse(new CompetitionCategory(""));
    }

    private String name;

    private CompetitionCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

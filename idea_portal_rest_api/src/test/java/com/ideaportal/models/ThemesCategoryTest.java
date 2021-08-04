package com.ideaportal.models;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ThemesCategoryTest {

    ThemesCategory themesCategory = new ThemesCategory();

    @BeforeEach
    public void init(){
        themesCategory.setThemeCategoryID(1);
        themesCategory.setThemeCategoryName("demo");
    }

    @Test
    void getThemeCategoryID() {
        assertEquals(1, themesCategory.getThemeCategoryID());
    }

    @Test
    void setThemeCategoryID() {
        themesCategory.setThemeCategoryID(2);
        assertEquals(2, themesCategory.getThemeCategoryID());
    }

    @Test
    void getThemeCategoryName() {
        assertEquals("demo", themesCategory.getThemeCategoryName());
    }

    @Test
    void setThemeCategoryName() {
        themesCategory.setThemeCategoryName("updated");
        assertEquals("updated", themesCategory.getThemeCategoryName());
    }

    @Test
    void testEquals() {
        assertTrue(themesCategory.equals(themesCategory));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(themesCategory.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(themesCategory.getThemeCategoryID(), themesCategory.getThemeCategoryName()),
                themesCategory.hashCode());
    }

    @Test
    void testToString() {
        String result = "ThemesCategory{" +
                "themeCategoryID=" + themesCategory.getThemeCategoryID() +
                ", themeCategoryName='" + themesCategory.getThemeCategoryName() + '\'' +
                '}';
        assertEquals(result, themesCategory.toString());
    }
}
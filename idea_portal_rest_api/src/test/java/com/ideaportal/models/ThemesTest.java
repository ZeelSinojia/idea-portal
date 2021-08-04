package com.ideaportal.models;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ThemesTest {

    Themes themes = new Themes();

    @BeforeEach
    public void init() {
        User user = new User();
        user.setUserID(1);

        List<Artifacts> list = new ArrayList<Artifacts>();

        ThemesCategory themesCategory = new ThemesCategory();
        themesCategory.setThemeCategoryID(1);

        themes.setThemeID(1);
        themes.setThemeName("demo");
        themes.setThemeDescription("demo");
        themes.setThemeDate(null);
        themes.setUser(user);
        themes.setArtifacts(list);
        themes.setThemeModificationDate(null);
        themes.setModifiedBy(user);
        themes.setIsDeleted(IsDeleted.FALSE);
        themes.setThemesCategory(themesCategory);
    }

    @Test
    void getIsDeleted() {
        assertEquals(IsDeleted.FALSE, themes.getIsDeleted());
    }

    @Test
    void setIsDeleted() {
        themes.setIsDeleted(IsDeleted.TRUE);
        assertEquals(IsDeleted.TRUE, themes.getIsDeleted());
    }

    @Test
    void getThemeDate() {
        assertNull(themes.getThemeDate());
    }

    @Test
    void setThemeDate() {
        Date date = new Date();
        themes.setThemeDate(date);
        assertEquals(date, themes.getThemeDate());
    }

    @Test
    void getThemeID() {
        assertEquals(1, themes.getThemeID());
    }

    @Test
    void setThemeID() {
        themes.setThemeID(2);
        assertEquals(2, themes.getThemeID());
    }

    @Test
    void getThemeName() {
        assertEquals("demo", themes.getThemeName());
    }

    @Test
    void setThemeName() {
        themes.setThemeName("updated");
        assertEquals("updated", themes.getThemeName());

    }

    @Test
    void getThemeDescription() {
        assertEquals("demo", themes.getThemeDescription());

    }

    @Test
    void setThemeDescription() {
        themes.setThemeDescription("updated");
        assertEquals("updated", themes.getThemeDescription());

    }

    @Test
    void getUser() {
        assertEquals(1, themes.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        themes.setUser(user);
        assertEquals(2, themes.getUser().getUserID());
    }

    @Test
    void getThemeModificationDate() {
        assertNull(themes.getThemeModificationDate());
    }

    @Test
    void setThemeModificationDate() {
        Date date = new Date();
        themes.setThemeModificationDate(date);
        assertEquals(date, themes.getThemeModificationDate());
    }

    @Test
    void getModifiedBy() {
        assertEquals(1, themes.getModifiedBy().getUserID());
    }

    @Test
    void setModifiedBy() {
        User user = new User();
        user.setUserID(2);
        themes.setModifiedBy(user);
        assertEquals(2, themes.getModifiedBy().getUserID());
    }

    @Test
    void getArtifacts() {
        assertEquals(0, themes.getArtifacts().size());
    }

    @Test
    void setArtifacts() {
        Artifacts artifacts = new Artifacts();
        List<Artifacts> list = new ArrayList<>();
        list.add(artifacts);
        themes.setArtifacts(list);
        assertEquals(1, themes.getArtifacts().size());
    }

    @Test
    void getThemesCategory() {
        assertEquals(1, themes.getThemesCategory().getThemeCategoryID());
    }

    @Test
    void setThemesCategory() {
        ThemesCategory themesCategory = new ThemesCategory();
        themesCategory.setThemeCategoryID(2);
        themes.setThemesCategory(themesCategory);
        assertEquals(2, themes.getThemesCategory().getThemeCategoryID());
    }

    @Test
    void testEquals() {
        assertTrue(themes.equals(themes));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(themes.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(themes.getThemeID(), themes.getThemeName(), themes.getThemeDescription(), themes.getThemeDate(),
                themes.getUser(), themes.getArtifacts(), themes.getThemeModificationDate(), themes.getModifiedBy(), themes.getIsDeleted(),
                themes.getThemesCategory()),
                themes.hashCode());
    }

    @Test
    void testToString() {
    }
}
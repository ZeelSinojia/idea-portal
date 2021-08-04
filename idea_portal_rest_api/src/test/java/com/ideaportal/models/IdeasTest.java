package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Ideas.class)
class IdeasTest {

    Ideas ideas = new Ideas();

    @BeforeEach
    public void init(){

        Themes themes = new Themes();
        themes.setThemeID(1);

        User user = new User();
        user.setUserID(1);

        List<Artifacts> list = new ArrayList<Artifacts>();

        ideas.setIdeaID(1);
        ideas.setIdeaName("demo");
        ideas.setIdeaDescription("demo");
        ideas.setIdeaDate(null);
        ideas.setUser(user);
        ideas.setTheme(themes);
        ideas.setArtifacts(list);
        ideas.setIdeaModificationDate(null);
        ideas.setModifiedBy(user);
        ideas.setIsDeleted(IsDeleted.FALSE);

    }

    @Test
    void getIsDeleted() {
        assertEquals(IsDeleted.FALSE, ideas.getIsDeleted());
    }

    @Test
    void setIsDeleted() {
        ideas.setIsDeleted(IsDeleted.TRUE);
        assertEquals(IsDeleted.TRUE, ideas.getIsDeleted());
    }

    @Test
    void getIdeaDate() {
        assertNull(ideas.getIdeaDate());
    }

    @Test
    void setIdeaDate() {
        Date date = new Date();
        ideas.setIdeaDate(date);
        assertEquals(date, ideas.getIdeaDate());
    }

    @Test
    void getIdeaID() {
        assertEquals(1, ideas.getIdeaID());
    }

    @Test
    void setIdeaID() {
        ideas.setIdeaID(2);
        assertEquals(2, ideas.getIdeaID());
    }

    @Test
    void getIdeaName() {
        assertEquals("demo", ideas.getIdeaName());
    }

    @Test
    void setIdeaName() {
        ideas.setIdeaName("updated");
        assertEquals("updated", ideas.getIdeaName());
    }

    @Test
    void getIdeaDescription() {
        assertEquals("demo", ideas.getIdeaDescription());
    }

    @Test
    void setIdeaDescription() {
        ideas.setIdeaDescription("updated");
        assertEquals("updated", ideas.getIdeaDescription());
    }

    @Test
    void getTheme() {
        assertEquals(1, ideas.getTheme().getThemeID());
    }

    @Test
    void setTheme() {
        Themes themes = new Themes();
        themes.setThemeID(2);
        ideas.setTheme(themes);
        assertEquals(2, ideas.getTheme().getThemeID());
    }

    @Test
    void getUser() {
        assertEquals(1, ideas.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        ideas.setUser(user);
        assertEquals(2, ideas.getUser().getUserID());
    }

    @Test
    void getIdeaModificationDate() {
        assertNull(ideas.getIdeaModificationDate());
    }

    @Test
    void setIdeaModificationDate() {
        Date date = new Date();
        ideas.setIdeaModificationDate(date);
        assertEquals(date, ideas.getIdeaModificationDate());
    }

    @Test
    void getModifiedBy() {
        assertEquals(1, ideas.getUser().getUserID());
    }

    @Test
    void setModifiedBy() {
        User user = new User();
        user.setUserID(2);
        ideas.setModifiedBy(user);
        assertEquals(2, ideas.getModifiedBy().getUserID());
    }

    @Test
    void getArtifacts() {
        assertEquals(0, ideas.getArtifacts().size());
    }

    @Test
    void setArtifacts() {
        Artifacts artifacts = new Artifacts();
        List<Artifacts> list = new ArrayList<>();
        list.add(artifacts);
        ideas.setArtifacts(list);
        assertEquals(1, ideas.getArtifacts().size());
    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (ideas.getIdeaID() ^ (ideas.getIdeaID() >>> 32));
        assertEquals(result, ideas.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(ideas.equals(ideas));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(ideas.equals(null));
    }

    @Test
    void testToString() {
        String result =  "Ideas [ideaID=" + ideas.getIdeaID() + ", ideaName=" + ideas.getIdeaName() + ", ideaDescription=" + ideas.getIdeaDescription()
                + ", ideaDate=" + ideas.getIdeaDate() + ", theme=" + ideas.getTheme() + ", user=" + ideas.getUser() + ", artifacts=" + ideas.getArtifacts()
                + ", ideaModificationDate=" + ideas.getIdeaModificationDate() + ", modifiedBy=" + ideas.getModifiedBy() + "]";
        assertEquals(result, ideas.toString());
    }
}
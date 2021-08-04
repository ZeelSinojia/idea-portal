package com.ideaportal.models;

import com.ideaportal.controllers.ClientPartnerController;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Artifacts.class)
class ArtifactsTest {

    Artifacts artifacts = new Artifacts();

    @BeforeEach
    public void init(){

        Themes themes = new Themes();
        themes.setThemeID(1);

        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        artifacts.setArtifactID(1);
        artifacts.setIdea(ideas);
        artifacts.setTheme(themes);
        artifacts.setUser(user);
        artifacts.setFileType("txt");
        artifacts.setOriginalFileName("");
        artifacts.setArtifactCreationDate(null);
        artifacts.setIsModified(IsModified.FALSE);
        artifacts.setArtifactURL("");
    }


    @Test
    void getIsModified() {
        assertEquals(IsModified.FALSE, artifacts.getIsModified());
    }

    @Test
    void setIsModified() {
        artifacts.setIsModified(IsModified.TRUE);
        assertEquals(IsModified.TRUE, artifacts.getIsModified());
    }

    @Test
    void getArtifactID() {
        assertEquals(1, artifacts.getArtifactID());
    }

    @Test
    void setArtifactID() {
        artifacts.setArtifactID(2);
        assertEquals(2, artifacts.getArtifactID());
    }

    @Test
    void getTheme() {
        assertEquals(1, artifacts.getTheme().getThemeID());
    }

    @Test
    void setTheme() {
        Themes themes = new Themes();
        themes.setThemeID(2);
        artifacts.setTheme(themes);
        assertEquals(2, artifacts.getTheme().getThemeID());
    }

    @Test
    void getIdea() {
        assertEquals(1, artifacts.getIdea().getIdeaID());
    }

    @Test
    void setIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(2);
        artifacts.setIdea(ideas);
        assertEquals(2, artifacts.getIdea().getIdeaID());
    }

    @Test
    void getUser() {
        assertEquals(1, artifacts.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        artifacts.setUser(user);
        assertEquals(2, artifacts.getUser().getUserID());
    }

    @Test
    void getArtifactURL() {
        assertEquals("", artifacts.getArtifactURL());

    }

    @Test
    void setArtifactURL() {
        artifacts.setArtifactURL("updated");
        assertEquals("updated", artifacts.getArtifactURL());

    }

    @Test
    void getFileType() {
        assertEquals("txt", artifacts.getFileType());

    }

    @Test
    void setFileType() {
        artifacts.setFileType("ppt");
        assertEquals("ppt", artifacts.getFileType());

    }

    @Test
    void getArtifactCreationDate() {
        assertNull(artifacts.getArtifactCreationDate());

    }

    @Test
    void setArtifactCreationDate() {
        Date date = new Date();
        artifacts.setArtifactCreationDate(date);
        assertEquals(date, artifacts.getArtifactCreationDate());
    }

    @Test
    void getOriginalFileName() {
        assertEquals("", artifacts.getOriginalFileName());

    }

    @Test
    void setOriginalFileName() {
        artifacts.setOriginalFileName("updated-filename");
        assertEquals("updated-filename", artifacts.getOriginalFileName());

    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (artifacts.getArtifactID() ^ (artifacts.getArtifactID() >>> 32));
        assertEquals(result, artifacts.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(artifacts.equals(artifacts));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(artifacts.equals(null));
    }

    @Test
    void testToString() {
        String result = "Artifacts [artifactID=" + artifacts.getArtifactID() + ", theme=" + artifacts.getTheme() + ", idea=" + artifacts.getIdea() + ", user=" + artifacts.getUser()
                + ", artifactURL=" + artifacts.getArtifactURL() + ", fileType=" + artifacts.getFileType() + ", artifactCreationDate="
                + artifacts.getArtifactCreationDate() + ", artifactModificationDate=" + ", modifiedBy="
                + "]";
        assertEquals(result, artifacts.toString());
    }
}
package com.ideaportal.models;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Comments.class)
class CommentsTest {

    Comments comments = new Comments();

    @BeforeEach
    public void init(){
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        comments.setCommentID(1);
        comments.setCommentValue("demo");
        comments.setIdea(ideas);
        comments.setUser(user);
        comments.setCommentDate(null);
    }

    @Test
    void getCommentID() {
        assertEquals(1, comments.getCommentID());
    }

    @Test
    void setCommentID() {
        comments.setCommentID(2);
        assertEquals(2, comments.getCommentID());
    }

    @Test
    void getCommentValue() {
        assertEquals("demo", comments.getCommentValue());

    }

    @Test
    void setCommentValue() {
        comments.setCommentValue("updated");
        assertEquals("updated", comments.getCommentValue());
    }

    @Test
    void getIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        assertEquals(1, comments.getIdea().getIdeaID());
    }

    @Test
    void setIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(2);
        comments.setIdea(ideas);
        assertEquals(2, comments.getIdea().getIdeaID());
    }

    @Test
    void getUser() {
        User user = new User();
        user.setUserID(1);
        assertEquals(1, comments.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        comments.setUser(user);
        assertEquals(2, comments.getUser().getUserID());
    }

    @Test
    void getCommentDate() {
        assertNull(comments.getCommentDate());
    }

    @Test
    void setCommentDate() {
        Date date = new Date();
        comments.setCommentDate(date);
        assertEquals(date, comments.getCommentDate());
    }

    @Test
    void testToString() {
        String result = "Comments [commentID=" + comments.getCommentID() + ", commentValue=" + comments.getCommentValue() + ", idea=" + comments.getIdea() + ", user="
                + comments.getUser() + "]";
    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (comments.getCommentID() ^ (comments.getCommentID() >>> 32));
        assertEquals(result, comments.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(comments.equals(comments));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(comments.equals(null));
    }
}
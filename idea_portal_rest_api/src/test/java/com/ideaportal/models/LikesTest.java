package com.ideaportal.models;

import org.bouncycastle.jcajce.provider.symmetric.IDEA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class LikesTest {

    Likes likes = new Likes();

    @BeforeEach
    public void init(){

        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.DISLIKE);
        likes.setLikeDate(null);
        likes.setIdea(ideas);
        likes.setUser(user);
    }

    @Test
    void getLikeID() {
        assertEquals(1, likes.getLikeID());
    }

    @Test
    void setLikeID() {
        likes.setLikeID(2);
        assertEquals(2, likes.getLikeID());
    }

    @Test
    void getLikeValue() {
        assertEquals(LikeValue.DISLIKE, likes.getLikeValue());
    }

    @Test
    void setLikeValue() {
        likes.setLikeValue(LikeValue.LIKE);
        assertEquals(LikeValue.LIKE, likes.getLikeValue());
    }

    @Test
    void getIdea() {
        assertEquals(1, likes.getIdea().getIdeaID());
    }

    @Test
    void setIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(2);
        likes.setIdea(ideas);
        assertEquals(2, likes.getIdea().getIdeaID());
    }

    @Test
    void getUser() {
        assertEquals(1, likes.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        likes.setUser(user);
        assertEquals(2, likes.getUser().getUserID());
    }

    @Test
    void getLikeDate() {
        assertNull(likes.getLikeDate());
    }

    @Test
    void setLikeDate() {
        Date date = new Date();
        likes.setLikeDate(date);
        assertEquals(date, likes.getLikeDate());

    }

    @Test
    void testToString() {
        String result = "Likes [likeID=" + likes.getLikeID() + ", likeValue=" + likes.getLikeValue() + ", idea=" + likes.getIdea() + ", user=" + likes.getUser() + "]";
        assertEquals(result, likes.toString());
    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (likes.getLikeID() ^ (likes.getLikeID() >>> 32));
        assertEquals(result, likes.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(likes.equals(likes));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(likes.equals(null));
    }
}
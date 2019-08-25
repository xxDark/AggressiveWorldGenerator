package ru.cristalix.aggressiveworldgenerator;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class Vector2iTest {
    @Test
    public void vectorTest() {
        Vector2i v = new Vector2i(0, 0);
        Vector2i v2 = new Vector2i(0, 0);
        Assert.assertTrue(v.equals(v2));
        Assert.assertTrue(v.x == v2.x);
    }
}
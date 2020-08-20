package com.roncoder.bookstore;

import android.util.Log;

import com.roncoder.bookstore.utils.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals("4,001 FCFA", Utils.formatPrise(4001.0));
    }
}
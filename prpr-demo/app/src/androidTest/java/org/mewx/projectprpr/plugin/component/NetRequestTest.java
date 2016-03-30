package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;

import junit.framework.TestCase;

/**
 * This test case is for testing the NetRequest component.
 */
public class NetRequestTest extends TestCase {

    /**
     * Test url requests' conjunction method.
     * Containing differenct cases.
     */
    public void testArgumentConjunction() {
        ContentValues cv = new ContentValues();
        cv.put("test1", "arg1");
        cv.put("test2", 2);
        NetRequest netRequest = new NetRequest(NetRequest.REQUEST_TYPE.GET, "https://test.com", cv);
        assertEquals(netRequest.getArgs(), "test1=arg1&test2=2");

        cv.clear();
        netRequest = new NetRequest(NetRequest.REQUEST_TYPE.GET, "https://test.com", cv);
        assertEquals(netRequest.getArgs(), "");

        cv.clear();
        netRequest = new NetRequest(NetRequest.REQUEST_TYPE.POST, "https://test.com", null);
        assertEquals(netRequest.getType(), NetRequest.REQUEST_TYPE.POST);
        assertEquals(netRequest.getUrl(), "https://test.com");
        assertEquals(netRequest.getArgs(), "");
    }
}

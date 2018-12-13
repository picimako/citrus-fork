/*
 *    Copyright 2018 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.consol.citrus.http.message;

import com.consol.citrus.endpoint.resolver.DynamicEndpointUriResolver;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class HttpMessageTest {

    private HttpMessage httpMessage;

    @BeforeMethod
    public void setUp(){
        httpMessage = new HttpMessage();
    }

    @Test
    public void testSetCookies() {

        //GIVEN
        final Cookie cookie = mock(Cookie.class);
        final Cookie[] cookies = new Cookie[]{cookie};

        //WHEN
        httpMessage.setCookies(cookies);

        //THEN
        assertTrue(httpMessage.getCookies().contains(cookie));
    }

    @Test
    public void testSetCookiesOverwritesOldCookies() {

        //GIVEN
        httpMessage.setCookies(new Cookie[]{
                mock(Cookie.class),
                mock(Cookie.class)});

        final Cookie expectedCookie = mock(Cookie.class);
        final Cookie[] cookies = new Cookie[]{expectedCookie};

        //WHEN
        httpMessage.setCookies(cookies);

        //THEN
        assertTrue(httpMessage.getCookies().contains(expectedCookie));
        assertEquals(httpMessage.getCookies().size(), 1);
    }

    @Test
    public void testCopyConstructorPreservesCookies() {

        //GIVEN
        final Cookie expectedCookie = mock(Cookie.class);
        final HttpMessage originalMessage = new HttpMessage();
        originalMessage.cookie(expectedCookie);

        //WHEN
        final HttpMessage messageCopy = new HttpMessage(originalMessage);

        //THEN
        assertEquals(messageCopy.getCookies(), originalMessage.getCookies());
    }

    @Test
    public void testParseQueryParamsAreParsedCorrectly() {

        //GIVEN
        final String queryParamString = "foo=foobar,bar=barbar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParams(queryParamString);

        //THEN
        final Map<String, String> queryParams = resultMessage.getQueryParams();
        assertEquals(queryParams.get("foo"), "foobar");
        assertEquals(queryParams.get("bar"), "barbar");
    }

    @Test
    public void testParseQueryParamsSetsQueryParamsHeader() {

        //GIVEN
        final String queryParamString = "foo=foobar,bar=barbar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParams(queryParamString);

        //THEN
        assertEquals(resultMessage.getHeader(HttpMessageHeaders.HTTP_QUERY_PARAMS), queryParamString);
    }

    @Test
    public void testParseQueryParamsSetsQueryParamHeaderName() {

        //GIVEN
        final String queryParamString = "foo=foobar,bar=barbar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParams(queryParamString);

        //THEN
        assertEquals(resultMessage.getHeader(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME), queryParamString);
    }

    @Test
    public void testQueryParamWithoutValueContainsNull() {

        //GIVEN
        final String queryParam = "foo";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParam(queryParam);

        //THEN
        assertNull(resultMessage.getQueryParams().get("foo"));
    }

    @Test
    public void testQueryParamWithValueIsSetCorrectly() {

        //GIVEN
        final String key = "foo";
        final String value = "foo";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParam(key, value);

        //THEN
        assertEquals(resultMessage.getQueryParams().get(key), value);
    }

    @Test
    public void testNewQueryParamIsAddedToExistingParams() {

        //GIVEN
        final String existingKey = "foo";
        final String existingValue = "foobar";
        httpMessage.queryParam(existingKey, existingValue);

        final String newKey = "bar";
        final String newValue = "barbar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParam(newKey, newValue);

        //THEN
        assertEquals(resultMessage.getQueryParams().get(existingKey), existingValue);
        assertEquals(resultMessage.getQueryParams().get(newKey), newValue);
    }

    @Test
    public void testNewQueryParamIsAddedQueryParamsHeader() {

        //GIVEN
        httpMessage.queryParam("foo", "foobar");

        final String expectedHeaderValue = "bar=barbar,foo=foobar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParam("bar", "barbar");

        //THEN
        assertEquals(resultMessage.getHeader(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME), expectedHeaderValue);
    }

    @Test
    public void testNewQueryParamIsAddedQueryParamHeaderName() {

        //GIVEN
        httpMessage.queryParam("foo", "foobar");

        final String expectedHeaderValue = "bar=barbar,foo=foobar";

        //WHEN
        final HttpMessage resultMessage = httpMessage.queryParam("bar", "barbar");

        //THEN
        assertEquals(resultMessage.getHeader(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME), expectedHeaderValue);
    }

    @Test
    public void testDefaultStatusCodeIsNull() {

        //GIVEN


        //WHEN
        final HttpStatus statusCode = httpMessage.getStatusCode();

        //THEN
        assertNull(statusCode);
    }

    @Test
    public void testStringStatusCodeIsParsed() {

        //GIVEN
        httpMessage.header(HttpMessageHeaders.HTTP_STATUS_CODE, "404");

        //WHEN
        final HttpStatus statusCode = httpMessage.getStatusCode();

        //THEN
        assertEquals(statusCode, HttpStatus.NOT_FOUND);
    }

    @Test
    public void testIntegerStatusCodeIsParsed() {

        //GIVEN
        httpMessage.header(HttpMessageHeaders.HTTP_STATUS_CODE, 403);

        //WHEN
        final HttpStatus statusCode = httpMessage.getStatusCode();

        //THEN
        assertEquals(statusCode, HttpStatus.FORBIDDEN);
    }

    @Test
    public void testStatusCodeObjectIsPreserved() {

        //GIVEN
        httpMessage.header(HttpMessageHeaders.HTTP_STATUS_CODE, HttpStatus.I_AM_A_TEAPOT);

        //WHEN
        final HttpStatus statusCode = httpMessage.getStatusCode();

        //THEN
        assertEquals(statusCode, HttpStatus.I_AM_A_TEAPOT);
    }
}
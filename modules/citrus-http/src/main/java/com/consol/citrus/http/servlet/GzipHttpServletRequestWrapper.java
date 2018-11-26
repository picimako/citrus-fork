/*
 * Copyright 2006-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.http.servlet;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * Request wrapper wraps gzip input stream with unzipped stream. Read operations on that stream are
 * automatically decompressed with gzip encoding.
 *
 * @author Christoph Deppisch
 * @since 2.8.0
 */
public class GzipHttpServletRequestWrapper extends HttpServletRequestWrapper {
    /**
     * Constructs a request adaptor wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public GzipHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new GzipServletInputStream(getRequest());
    }

    /**
     * Gzip enabled servlet input stream.
     */
    private class GzipServletInputStream extends ServletInputStream {
        private final GZIPInputStream gzipStream;

        /**
         * Default constructor using wrapped input stream.
         * @param request
         * @throws IOException
         */
        public GzipServletInputStream(ServletRequest request) throws IOException {
            super();
            gzipStream = new GZIPInputStream(request.getInputStream());
        }

        @Override
        public boolean isFinished() {
            try {
                return gzipStream.available() == 0;
            } catch (IOException e) {
                throw new CitrusRuntimeException("Failed to check gzip intput stream availability", e);
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(final ReadListener readListener) {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        @Override
        public int read() {
            try {
                return gzipStream.read();
            } catch (IOException e) {
                throw new CitrusRuntimeException("Failed to read gzip input stream", e);
            }
        }
    }
}

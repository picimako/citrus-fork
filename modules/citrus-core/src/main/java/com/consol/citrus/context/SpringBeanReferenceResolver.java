/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring bean reference resolver operates on given application context to resolve bean references.
 *
 * @author Christoph Deppisch
 * @since 2.5
 */
public class SpringBeanReferenceResolver implements ReferenceResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Default constructor.
     */
    public SpringBeanReferenceResolver() {
        super();
    }

    /**
     * Constructor initializes with given application context.
     * @param applicationContext
     */
    public SpringBeanReferenceResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> List<T> resolve(Class<T> requiredType, String... names) {
        if (names.length > 0) {
            return resolve(names, requiredType);
        }

        return new ArrayList<>(resolveAll(requiredType).values());
    }

    @Override
    public <T> T resolve(Class<T> requiredType) {
        try {
            return applicationContext.getBean(requiredType);
        } catch (NoSuchBeanDefinitionException e) {
            throw new CitrusRuntimeException(String.format("Unable to find bean reference for type '%s'", requiredType), e);
        }
    }

    @Override
    public <T> T resolve(String name, Class<T> type) {
        try {
            return applicationContext.getBean(name, type);
        } catch (NoSuchBeanDefinitionException e) {
            throw new CitrusRuntimeException(String.format("Unable to find bean reference for name '%s'", name), e);
        }
    }

    @Override
    public <T> List<T> resolve(String[] names, Class<T> type) {
        List<T> resolved = new ArrayList<>();

        for (String name : names) {
            resolved.add(resolve(name, type));
        }

        return resolved;
    }

    @Override
    public Object resolve(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            throw new CitrusRuntimeException(String.format("Unable to find bean reference for name '%s'", name), e);
        }
    }

    @Override
    public <T> Map<String, T> resolveAll(Class<T> requiredType) {
        try {
            return applicationContext.getBeansOfType(requiredType);
        } catch (NoSuchBeanDefinitionException e) {
            throw new CitrusRuntimeException(String.format("Unable to find bean references for type '%s'", requiredType), e);
        }
    }

    @Override
    public boolean isResolvable(String name) {
        return applicationContext.containsBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Obtains the applicationContext.
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

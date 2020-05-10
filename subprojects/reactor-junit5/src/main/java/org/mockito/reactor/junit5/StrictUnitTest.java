/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ExtendWith(ReactorSubscribeCountExtension.class)
@ExtendWith(MockitoExtension.class)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface StrictUnitTest {

}

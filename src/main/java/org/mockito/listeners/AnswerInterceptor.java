package org.mockito.listeners;

import java.util.function.Function;

import org.mockito.stubbing.Answer;

public interface AnswerInterceptor<T> extends Function<Answer<T>, Answer<T>> {

}

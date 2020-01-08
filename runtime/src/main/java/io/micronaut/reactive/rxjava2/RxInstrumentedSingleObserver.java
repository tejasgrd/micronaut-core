/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.reactive.rxjava2;

import io.micronaut.core.annotation.Internal;
import io.micronaut.scheduling.instrument.InvocationInstrumenter;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Inspired by code in Brave. Provides general instrumentation abstraction for RxJava2.
 * See https://github.com/openzipkin/brave/tree/master/context/rxjava2/src/main/java/brave/context/rxjava2/internal.
 *
 * @param <T> The type
 * @author graemerocher
 * @since 1.1
 */
@Internal
final class RxInstrumentedSingleObserver<T> implements SingleObserver<T>, RxInstrumentedComponent {
    private final SingleObserver<T> source;
    private final RxInstrumenterFactory instrumenterFactory;

    /**
     * Default constructor.
     *
     * @param source              The source observer
     * @param instrumenterFactory The instrumenterFactory
     */
    RxInstrumentedSingleObserver(SingleObserver<T> source, RxInstrumenterFactory instrumenterFactory) {
        this.source = source;
        this.instrumenterFactory = instrumenterFactory;
    }

    @Override
    public void onSubscribe(Disposable d) {
        InvocationInstrumenter instrumenter = instrumenterFactory.create();
        if (instrumenter == null) {
            source.onSubscribe(d);
        } else {
            try {
                instrumenter.beforeInvocation();
                source.onSubscribe(d);
            } finally {
                instrumenter.afterInvocation();
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        InvocationInstrumenter instrumenter = instrumenterFactory.create();
        if (instrumenter == null) {
            source.onError(t);
        } else {
            try {
                instrumenter.beforeInvocation();
                source.onError(t);
            } finally {
                instrumenter.afterInvocation();
            }
        }
    }

    @Override
    public void onSuccess(T value) {
        InvocationInstrumenter instrumenter = instrumenterFactory.create();
        if (instrumenter == null) {
            source.onSuccess(value);
        } else {
            try {
                instrumenter.beforeInvocation();
                source.onSuccess(value);
            } finally {
                instrumenter.afterInvocation();
            }
        }
    }

}
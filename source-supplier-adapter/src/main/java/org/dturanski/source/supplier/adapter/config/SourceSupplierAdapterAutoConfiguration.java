/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dturanski.source.supplier.adapter.config;

import java.util.Collections;
import java.util.function.Supplier;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * @author David Turanski
 **/
@Configuration
public class SourceSupplierAdapterAutoConfiguration implements EnvironmentPostProcessor {
	private EmitterProcessor<Message<?>> emitterProcessor = EmitterProcessor.create();

	@Bean
	Supplier<Flux<Message<?>>> source() {
		return () -> emitterProcessor;
	}

	@ServiceActivator(inputChannel = Source.OUTPUT)
	public void service(Message<?> message) {
		emitterProcessor.onNext(message);
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		environment.getPropertySources().addLast(new MapPropertySource("binder-settings",
			Collections.singletonMap("spring.cloud.stream.bindings.output.producer.use-native-encoding",true)));
	}
}

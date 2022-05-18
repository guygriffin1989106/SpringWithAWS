/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.aws;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;

/**
 *
 * @author Artem Bilan
 *
 * @since 3.0
 */
@Testcontainers(disabledWithoutDocker = false)
public interface LocalstackContainerTest {

	@Container
	LocalStackContainer localStack =
			new LocalStackContainer(
					DockerImageName.parse("localstack/localstack:0.14.2")
							.asCompatibleSubstituteFor("localstack/localstack"))
					.withServices(
							LocalStackContainer.Service.DYNAMODB,
							LocalStackContainer.Service.KINESIS,
							LocalStackContainer.Service.CLOUDWATCH)
					.withReuse(true);


	static AmazonDynamoDBAsync dynamoDbClient() {
		return applyAwsClientOptions(AmazonDynamoDBAsyncClientBuilder.standard(), LocalStackContainer.Service.DYNAMODB);
	}

	static AmazonKinesisAsync kinesisClient() {
		return applyAwsClientOptions(AmazonKinesisAsyncClientBuilder.standard(), LocalStackContainer.Service.KINESIS);
	}

	static AmazonCloudWatch cloudWatchClient() {
		return applyAwsClientOptions(AmazonCloudWatchClientBuilder.standard(), LocalStackContainer.Service.CLOUDWATCH);
	}

	private static <B extends AwsClientBuilder<B, T>, T> T applyAwsClientOptions(B clientBuilder,
			LocalStackContainer.Service serviceToBuild) {

		return clientBuilder.withEndpointConfiguration(localStack.getEndpointConfiguration(serviceToBuild))
				.withCredentials(localStack.getDefaultCredentialsProvider())
				.build();
	}

}

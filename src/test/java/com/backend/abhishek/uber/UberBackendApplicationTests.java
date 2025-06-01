package com.backend.abhishek.uber;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
class UberBackendApplicationTests {

	@Bean
//	@ServiceConnection
	PostgreSQLContainer<?> postgreSQLContainer(){
		var image = DockerImageName.parse("postgis/postgis:12-3.0")
				.asCompatibleSubstituteFor("postgres");
		return new  PostgreSQLContainer<>(image);
	}
}

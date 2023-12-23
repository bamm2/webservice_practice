package com.yubeom.study.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

//@EnableJpaAuditing // JPA Auditing 활성화 ,
                    // Entity 가 하나 이상있어야 인식하지만 helloController 테스트는 엔티티가 없어 인식 못함 에러 발생
                    // -> Config 클래스 생성 이동
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }
}
